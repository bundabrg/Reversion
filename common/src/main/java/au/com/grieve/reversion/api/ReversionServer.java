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

import com.nukkitx.network.util.EventLoops;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockServer;
import io.netty.channel.EventLoopGroup;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
public abstract class ReversionServer extends BedrockServer {
    private final BedrockPacketCodec toCodec;
    private final EventLoopGroup eventLoopGroup;

    public ReversionServer(BedrockPacketCodec toCodec, InetSocketAddress address) {
        this(toCodec, address, 1);
    }

    public ReversionServer(BedrockPacketCodec toCodec, InetSocketAddress address, int maxThreads) {
        this(toCodec, address, maxThreads, EventLoops.commonGroup());
    }

    public ReversionServer(BedrockPacketCodec toCodec, InetSocketAddress address, int maxThreads, EventLoopGroup eventLoopGroup) {
        super(address, maxThreads, eventLoopGroup);

        this.eventLoopGroup = eventLoopGroup;
        this.toCodec = toCodec;
    }
}
