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
    private final String toEdition = "bedrock";
    private final TokenManager tokenManager;

    public EducationReversionServer(BedrockPacketCodec toCodec, TokenManager tokenManager, InetSocketAddress address) {
        this(toCodec, tokenManager, address, 1);
    }

    public EducationReversionServer(BedrockPacketCodec toCodec, TokenManager tokenManager, InetSocketAddress address, int maxThreads) {
        this(toCodec, tokenManager, address, maxThreads, EventLoops.commonGroup());
    }

    public EducationReversionServer(BedrockPacketCodec codec, TokenManager tokenManager, InetSocketAddress address, int maxThreads, EventLoopGroup eventLoopGroup) {
        super(codec, address, maxThreads, eventLoopGroup);

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
