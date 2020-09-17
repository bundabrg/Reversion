/*
 * MIT License
 *
 * Copyright (c) 2020 Reversion Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
