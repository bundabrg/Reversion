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

package au.com.grieve.reversion;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import lombok.Getter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Provide Login Data
 * <p>
 * Much of this has been converted from code in Geyser
 */
@Getter
public class LoginData {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final ReversionServerSession session;

    private boolean validChain = false;
    private JsonNode payload;
    private JsonNode clientData;
    private ECPublicKey clientPublicKey;
    private KeyPair serverKeyPair;
    private byte[] token;
    private SecretKey encryptionKey;

    public LoginData(ReversionServerSession session, LoginPacket packet) throws LoginException {
        this.session = session;
        processPacket(packet);
    }

    protected static JWSObject createHandshakeJwt(KeyPair serverKeyPair, byte[] token, Map<String, String> claims) throws JOSEException {
        URI x5u = URI.create(Base64.getEncoder().encodeToString(serverKeyPair.getPublic().getEncoded()));

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claimsBuilder.claim("salt", Base64.getEncoder().encodeToString(token));

        for (Map.Entry<String, String> claim : claims.entrySet()) {
            claimsBuilder.claim(claim.getKey(), claim.getValue());
        }

        SignedJWT jwt = new SignedJWT((new com.nimbusds.jose.JWSHeader.Builder(JWSAlgorithm.ES384)).x509CertURL(x5u).build(),
                claimsBuilder.build());
        EncryptionUtils.signJwt(jwt, (ECPrivateKey) serverKeyPair.getPrivate());
        return jwt;
    }

    protected void processPacket(LoginPacket packet) throws LoginException {
        JsonNode certData;
        try {
            certData = JSON_MAPPER.readTree(packet.getChainData().toByteArray());
        } catch (IOException ex) {
            throw new LoginException("Certificate JSON can not be read.");
        }

        JsonNode certChainData = certData.get("chain");
        if (certChainData.getNodeType() != JsonNodeType.ARRAY) {
            throw new LoginException("Certificate data is not valid");
        }

        // Validate Chain
        validChain = validateChainData(certChainData);

        // Get Payload
        try {
            JWSObject jwt = JWSObject.parse(certChainData.get(certChainData.size() - 1).asText());
            payload = JSON_MAPPER.readTree(jwt.getPayload().toBytes());
        } catch (ParseException | IOException e) {
            throw new LoginException(e);
        }

        // Make sure we have an identity public key
        if (payload.get("identityPublicKey").getNodeType() != JsonNodeType.STRING) {
            throw new LoginException("Identity Public Key was not found!");
        }

        // Get Client Data
        try {
            clientPublicKey = EncryptionUtils.generateKey(payload.get("identityPublicKey").textValue());
            JWSObject clientJwt = JWSObject.parse(packet.getSkinData().toString());
            EncryptionUtils.verifyJwt(clientJwt, clientPublicKey);
            clientData = JSON_MAPPER.readTree(clientJwt.getPayload().toBytes());
        } catch (IOException | InvalidKeySpecException | ParseException | NoSuchAlgorithmException | JOSEException e) {
            throw new LoginException(e);
        }

        // Generate Encryption Key
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(new ECGenParameterSpec("secp384r1"));
            serverKeyPair = generator.generateKeyPair();

            token = EncryptionUtils.generateRandomToken();
            encryptionKey = EncryptionUtils.getSecretKey(serverKeyPair.getPrivate(), clientPublicKey, token);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new LoginException(e);
        }
    }

    public JWSObject getHandshakeJwt() throws LoginException {
        try {
            return createHandshakeJwt(serverKeyPair, token, new HashMap<>());
        } catch (JOSEException e) {
            throw new LoginException(e);
        }
    }

    /**
     * Validate that the provided certificate chain is correct
     *
     * @param data Chain Data
     * @return true if valid
     * @throws LoginException error
     */
    protected boolean validateChainData(JsonNode data) throws LoginException {
        ECPublicKey lastKey = null;
        boolean validChain = false;
        for (JsonNode node : data) {
            JWSObject jwt;
            try {
                jwt = JWSObject.parse(node.asText());

                if (!validChain) {
                    validChain = EncryptionUtils.verifyJwt(jwt, EncryptionUtils.getMojangPublicKey());
                }

                if (lastKey != null) {
                    EncryptionUtils.verifyJwt(jwt, lastKey);
                }

                JsonNode payloadNode = JSON_MAPPER.readTree(jwt.getPayload().toString());
                JsonNode ipkNode = payloadNode.get("identityPublicKey");
                Preconditions.checkState(ipkNode != null && ipkNode.getNodeType() == JsonNodeType.STRING, "identityPublicKey node is missing in chain");
                lastKey = EncryptionUtils.generateKey(ipkNode.asText());
            } catch (ParseException | JOSEException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new LoginException(e);
            }
        }
        return validChain;
    }

    @SuppressWarnings("unused")
    public static class LoginException extends Exception {
        public LoginException(String message) {
            super(message);
        }

        public LoginException(String message, Throwable e) {
            super(message, e);
        }

        public LoginException(Throwable e) {
            super(e);
        }
    }

}
