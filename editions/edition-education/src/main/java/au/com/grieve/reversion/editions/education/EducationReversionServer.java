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

import au.com.grieve.reversion.editions.bedrock.BedrockReversionServer;
import au.com.grieve.reversion.editions.bedrock.BedrockReversionSession;
import au.com.grieve.reversion.editions.education.utils.TokenManager;
import com.nukkitx.network.raknet.RakNetServerListener;
import com.nukkitx.network.raknet.RakNetSession;
import com.nukkitx.network.util.EventLoops;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
public class EducationReversionServer extends BedrockReversionServer {
    private final String fromEdition = "education";
    private final TokenManager tokenManager;

    public EducationReversionServer(String toEdition, BedrockPacketCodec toCodec, TokenManager tokenManager, InetSocketAddress address) {
        this(toEdition, toCodec, tokenManager, address, 1);
    }

    public EducationReversionServer(String toEdition, BedrockPacketCodec toCodec, TokenManager tokenManager, InetSocketAddress address, int maxThreads) {
        this(toEdition, toCodec, tokenManager, address, maxThreads, EventLoops.commonGroup());
    }

    public EducationReversionServer(String edition, BedrockPacketCodec codec, TokenManager tokenManager, InetSocketAddress address, int maxThreads, EventLoopGroup eventLoopGroup) {
        super(edition, codec, address, maxThreads, eventLoopGroup);

        this.tokenManager = tokenManager;
    }

    @Override
    public BedrockReversionSession createSession(RakNetSession connection, EventLoop eventLoop, BedrockWrapperSerializer serializer) {
        return new EducationReversionSession(this, connection, eventLoop, serializer);
    }

    @Override
    public RakNetServerListener createRakNetServerListener() {
        return new EducationRakNetServerListener();
    }

    protected class EducationRakNetServerListener extends BedrockRakNetServerListener {

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
