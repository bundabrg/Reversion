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

package au.com.grieve.reversion.editions.education;

import au.com.grieve.reversion.editions.bedrock.BedrockLoginData;
import au.com.grieve.reversion.editions.bedrock.BedrockReversionSession;
import au.com.grieve.reversion.exceptions.LoginException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;

import java.util.HashMap;
import java.util.Map;

public class EducationLoginData extends BedrockLoginData {

    public EducationLoginData(BedrockReversionSession session, LoginPacket packet) throws LoginException {
        super(session, packet);
    }

    @Override
    public JWSObject getHandshakeJwt() throws LoginException {
        // Education has a TenantID
        if (!getClientData().has("TenantId")) {
            throw new LoginException("No TenantId found from client");
        }

        String tenantId = getClientData().get("TenantId").asText();

        // Lookup a signed token for the tenant
        EducationReversionServer server = (EducationReversionServer) getSession().getServer();
        if (!server.getTokenManager().getTokenMap().containsKey(tenantId)) {
            throw new LoginException("Unknown Tenant tried to connect: " + tenantId);
        }

        Map<String, String> claims = new HashMap<String, String>() {{
            put("signedToken", server.getTokenManager().getTokenMap().get(tenantId).getSignedToken());
        }};

        try {
            return createHandshakeJwt(getServerKeyPair(), getToken(), claims);
        } catch (JOSEException e) {
            throw new LoginException(e);
        }
    }
}
