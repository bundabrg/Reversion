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

import au.com.grieve.reversion.LoginData;
import au.com.grieve.reversion.ReversionServer;
import au.com.grieve.reversion.ReversionServerSession;
import au.com.grieve.reversion.editions.education.utils.TokenManager;
import com.nukkitx.network.raknet.RakNetServerListener;
import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
public class EducationReversionServer extends ReversionServer {
    private final String fromEdition = "education";
    private final TokenManager tokenManager;

    public EducationReversionServer(String edition, int version, TokenManager tokenManager, InetSocketAddress address) {
        super(edition, version, address);

        this.tokenManager = tokenManager;
    }

    @Override
    public LoginData createLoginData(ReversionServerSession session, LoginPacket packet) throws LoginData.LoginException {
        return new EducationLoginData(session, packet);
    }

    @Override
    public RakNetServerListener createRakNetServerListener() {
        return new EducationRakNetServerListener();
    }

    protected class EducationRakNetServerListener extends ReversionRakNetServerListener {

        @Override
        protected BedrockPong processQuery(InetSocketAddress address) {
            BedrockPong pong = super.processQuery(address);

            if (pong != null) {
                pong.setEdition("MCEE");
            }

            return pong;
        }
    }
}
