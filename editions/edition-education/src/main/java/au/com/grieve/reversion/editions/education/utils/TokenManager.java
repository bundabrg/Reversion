/*
 * Reversion - Minecraft Protocol Support for Bedrock
 * Copyright (C) 2020 Reversion Developers
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package au.com.grieve.reversion.editions.education.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nimbusds.jose.JWSObject;
import lombok.Getter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Takes care of tokens used to authenticate Client and Server
 */
@Getter
public class TokenManager {
    private final Map<String, Token> tokenMap = new HashMap<>();

    private final File tokenFile;

    public TokenManager(File tokenFile) {
        this.tokenFile = tokenFile;

        load();
    }

    /**
     * Load tokens from file
     */
    void load() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonNode root;

        try {
            root = mapper.readTree(tokenFile);
        } catch (IOException e) {
            return;
        }

        for (Iterator<String> it = root.fieldNames(); it.hasNext(); ) {
            String tenantId = it.next();
            String token = root.get(tenantId).asText();
            tokenMap.put(tenantId, new Token(this, token));
        }
    }

    /**
     * Save tokens to file
     */
    void save() {
        if (tokenMap.size() == 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ObjectNode root = mapper.createObjectNode();

        for (Map.Entry<String, Token> entry : tokenMap.entrySet()) {
            root.put(entry.getKey(), entry.getValue().getRefreshToken());
        }

        try {
            mapper.writeValue(tokenFile, root);
        } catch (IOException ignored) {
        }
    }

    /**
     * Start OAuth2 Process
     */
    public URL getNewAuthorizationUrl() {
        try {
            return new URL("https://login.microsoftonline.com/common/oauth2/authorize" +
                    "?response_type=" + "code" +
                    "&client_id=" + "b36b1432-1a1c-4c82-9b76-24de1cab42f2" +
                    "&redirect_uri=" + URLEncoder.encode("https://login.microsoftonline.com/common/oauth2/nativeclient", "UTF-8") +
                    "&state=" + UUID.randomUUID().toString() +
                    "&resource=" + URLEncoder.encode("https://meeservices.minecraft.net", "UTF-8"));
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fetch Token using authorization response
     */
    public void createInitialToken(String authorizationResponse) throws TokenException {
        if (!authorizationResponse.contains("code=")) {
            throw new TokenException("Invalid authorization response");
        }

        String raw = authorizationResponse.substring(authorizationResponse.indexOf("code="));

        try {
            URL url = new URL("https://login.microsoftonline.com/common/oauth2/token");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            byte[] postData = (raw +
                    "&client_id=" + "b36b1432-1a1c-4c82-9b76-24de1cab42f2" +
                    "&redirect_uri=" + URLEncoder.encode("https://login.microsoftonline.com/common/oauth2/nativeclient", "UTF-8") +
                    "&grant_type=authorization_code").getBytes();
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postData);
            }

            if (connection.getResponseCode() != 200) {
                throw new TokenException("Failed to create token. Got response: " + connection.getResponseMessage());
            }

            ObjectMapper mapper = new ObjectMapper();

            JsonNode node = mapper.readTree(connection.getInputStream());

            if (!node.has("refresh_token") || !node.has("access_token")) {
                throw new TokenException("Failed to create token. Missing access/refresh token in response");
            }

            String refreshToken = node.get("refresh_token").asText();
            String accessToken = node.get("access_token").asText();

            JWSObject jwt = JWSObject.parse(accessToken);

            node = mapper.readTree(jwt.getPayload().toBytes());
            String tenantId = node.get("tid").asText();

            tokenMap.put(tenantId, new Token(this, refreshToken));
            save();
        } catch (IOException | ParseException e) {
            throw new TokenException("Failed to create token: " + e.getMessage(), e);
        }
    }


    @Getter
    public static class Token {
        // How long till we need to obtain a new signed token, in seconds
        public static int SIGNED_TOKEN_LIFETIME = 604800;
        private final TokenManager manager;
        ObjectMapper mapper = new ObjectMapper();
        private String accessToken;
        private String refreshToken;
        private String signedToken;
        private LocalDateTime expires;

        public Token(TokenManager manager, String refreshToken) {
            this.manager = manager;
            this.refreshToken = refreshToken;
        }

        /**
         * Lazily get signed token
         */
        public String getSignedToken() {
            // Refresh token if needed
            if (expires == null || LocalDateTime.now().isAfter(expires)) {
                refresh();
            }

            return signedToken;
        }

        /**
         * Retrieve a new signedToken
         */
        public void refresh() {
            refreshMicrosoftToken();
            refreshMinecraftToken();
            manager.save();
        }

        private void refreshMicrosoftToken() {
            // Refresh Microsoft Token
            try {
                URL url = new URL("https://login.microsoftonline.com/common/oauth2/token");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setDoInput(true);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                byte[] postData = String.format(
                        "client_id=%s&refresh_token=%s&grant_type=%s",
                        "b36b1432-1a1c-4c82-9b76-24de1cab42f2",
                        refreshToken,
                        "refresh_token"
                ).getBytes();
                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    wr.write(postData);
                }

                if (connection.getResponseCode() != 200) {
                    return;
                }

                JsonNode node = mapper.readTree(connection.getInputStream());

                accessToken = node.get("access_token").asText();
                refreshToken = node.get("refresh_token").asText();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void refreshMinecraftToken() {
            URL url;
            try {
                url = new URL("https://meeservices.azurewebsites.net/v2/signin");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setDoInput(true);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                ObjectNode postData = mapper.createObjectNode();
                postData.put("accessToken", accessToken);
                postData.put("build", 11260000);
                postData.put("clientVersion", 363);
                postData.put("displayVersion", "1.12.60");
                postData.put("identityToken", accessToken);
                postData.put("locale", "en_US");
                postData.put("osVersion", "iOS 13.4.1"); // Be good citizens
                postData.put("platform", "iPad5,3()");  // Be good citizens
                postData.put("requestId", UUID.randomUUID().toString());

                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    wr.write(postData.toString().getBytes());
                }

                if (connection.getResponseCode() != 200) {
                    return;
                }

                JsonNode node = mapper.readTree(connection.getInputStream());
                JWSObject jwt = JWSObject.parse(node.get("response").asText());

                node = mapper.readTree(jwt.getPayload().toBytes());
                signedToken = node.with("payload").get("signedToken").asText();
                expires = LocalDateTime.now().plus(Duration.ofSeconds(SIGNED_TOKEN_LIFETIME));

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

    }

    public static class TokenException extends Exception {
        TokenException(String message) {
            super(message);
        }

        TokenException(String message, Throwable e) {
            super(message, e);
        }
    }

}
