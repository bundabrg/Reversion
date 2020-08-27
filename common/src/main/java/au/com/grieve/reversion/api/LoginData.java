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

package au.com.grieve.reversion.api;

import au.com.grieve.reversion.exceptions.LoginException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;

import javax.crypto.SecretKey;

public interface LoginData {
    ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    /**
     * Is the login chain valid?
     *
     * @return true if valid
     */
    boolean isValidChain();

    /**
     * Return the payload of the Login
     *
     * @return the payload
     */
    JsonNode getPayload();

    /**
     * Return the clientdata of the Login
     *
     * @return the clientData
     */
    JsonNode getClientData();

    /**
     * Return the server encryption key
     *
     * @return the encryptionkey
     */
    SecretKey getEncryptionKey();

    /**
     * Return the Handshake JWT
     *
     * @return the handshake JWT
     */
    JWSObject getHandshakeJwt() throws LoginException;
}
