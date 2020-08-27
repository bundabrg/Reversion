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
import java.util.ArrayList;
import java.util.List;

@Getter
public class ReversionServer extends BedrockServer {
    private final String toEdition;
    private final BedrockPacketCodec toCodec;
    private final EventLoopGroup eventLoopGroup;

    // Supported Translators
    private final List<RegisteredTranslator> registeredTranslators = new ArrayList<>();

    public ReversionServer(String toEdition, BedrockPacketCodec toCodec, InetSocketAddress address) {
        this(toEdition, toCodec, address, 1);
    }

    public ReversionServer(String toEdition, BedrockPacketCodec toCodec, InetSocketAddress address, int maxThreads) {
        this(toEdition, toCodec, address, maxThreads, EventLoops.commonGroup());
    }

    public ReversionServer(String toEdition, BedrockPacketCodec toCodec, InetSocketAddress address, int maxThreads, EventLoopGroup eventLoopGroup) {
        super(address, maxThreads, eventLoopGroup);

        this.eventLoopGroup = eventLoopGroup;
        this.toEdition = toEdition;
        this.toCodec = toCodec;
    }

    /**
     * Register a Translator for this Server
     *
     * @param registeredTranslator The translator to register
     * @return ourself to allow chaining
     */
    public ReversionServer registerTranslator(RegisteredTranslator registeredTranslator) {
        registeredTranslators.add(registeredTranslator);
        return this;
    }

}
