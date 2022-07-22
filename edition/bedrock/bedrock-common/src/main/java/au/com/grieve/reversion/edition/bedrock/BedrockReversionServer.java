/*
 * MIT License
 *
 * Copyright (c) 2022 Reversion Developers
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

package au.com.grieve.reversion.edition.bedrock;

import au.com.grieve.reversion.api.ReversionServer;
import au.com.grieve.reversion.api.ReversionTranslator;
import au.com.grieve.reversion.edition.bedrock.api.BedrockVersion;
import com.nukkitx.network.raknet.RakNetServerListener;
import com.nukkitx.network.raknet.RakNetServerSession;
import com.nukkitx.network.raknet.RakNetSession;
import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.network.util.EventLoops;
import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockRakNetSessionListener;
import com.nukkitx.protocol.bedrock.BedrockServer;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializer;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializers;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import lombok.Getter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/*
    BedrockReversionServer

    A Bedrock server that will accept multiple client bedrock versions and should be a
    drop in replacement for the excellent CloudBurst Protocol BedrockServer which this
    extends.
 */

@Getter
public class BedrockReversionServer extends BedrockServer implements ReversionServer {
    private final String edition = "bedrock";

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final Set<BedrockVersion> sourceVersions = new HashSet<>();
    private final Set<ReversionTranslator> reversionTranslators = new HashSet<>();
    private BedrockVersion targetVersion;

    public BedrockReversionServer(InetSocketAddress bindAddress) {
        this(bindAddress, 1);
    }

    public BedrockReversionServer(InetSocketAddress bindAddress, int bindThreads) {
        this(bindAddress, bindThreads, EventLoops.commonGroup());
    }

    public BedrockReversionServer(InetSocketAddress bindAddress, int bindThreads, EventLoopGroup eventLoopGroup) {
        this(bindAddress, bindThreads, eventLoopGroup, false);
    }

    public BedrockReversionServer(InetSocketAddress bindAddress, int bindThreads, EventLoopGroup group, boolean allowProxyProtocol) {
        this(bindAddress, bindThreads, group, group, allowProxyProtocol);
    }

    public BedrockReversionServer(InetSocketAddress bindAddress, int bindThreads, EventLoopGroup bossGroup, EventLoopGroup workerGroup, boolean allowProxyProtocol) {
        super(bindAddress, bindThreads, bossGroup, workerGroup, allowProxyProtocol);
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    /**
     * Set the target version to translate inbound clients to
     *
     * @param version Target Version
     */
    public void setTargetVersion(BedrockVersion version) {
        this.targetVersion = version;
    }

    /**
     * Add the source versions we will accept
     *
     * @param version Source Version
     */
    public void addSourceVersion(BedrockVersion version) {
        this.sourceVersions.add(version);
    }

    /**
     * Add translators we can use to translate from source to target
     *
     * @param translator Translator
     */
    public void addTranslator(ReversionTranslator translator) {
        this.reversionTranslators.add(translator);
    }

    // Provide our own RakNetServerListener as parent class has it private and we need to hook it
    @ParametersAreNonnullByDefault
    private class BedrockServerListener implements RakNetServerListener {

        @Override
        public boolean onConnectionRequest(InetSocketAddress address, InetSocketAddress realAddress) {
            return BedrockReversionServer.this.getHandler() == null || BedrockReversionServer.this.getHandler().onConnectionRequest(address, realAddress);
        }

        @Nullable
        @Override
        public byte[] onQuery(InetSocketAddress address) {
            if (BedrockReversionServer.this.getHandler() != null) {
                BedrockPong pong = BedrockReversionServer.this.getHandler().onQuery(address);
                if (pong != null) {
                    try {
                        Method setServerId = pong.getClass().getDeclaredMethod("setServerId", long.class);
                        setServerId.setAccessible(true);
                        setServerId.invoke(pong, getRakNet().getGuid());

                        Method toRakNet = pong.getClass().getDeclaredMethod("toRakNet");
                        toRakNet.setAccessible(true);
                        return (byte[]) toRakNet.invoke(pong);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        // TODO: Do something better?
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return null;
        }

        @Override
        public void onSessionCreation(RakNetServerSession connection) {
            BedrockWrapperSerializer serializer = BedrockWrapperSerializers.getSerializer(connection.getProtocolVersion());
            BedrockReversionServerSession session = new BedrockReversionServerSession(connection, BedrockReversionServer.this.workerGroup.next(), serializer);

            BedrockRakNetSessionListener.Server server;
            try {
                Constructor<BedrockRakNetSessionListener.Server> constructor = BedrockRakNetSessionListener.Server.class
                        .getDeclaredConstructor(com.nukkitx.protocol.bedrock.BedrockServerSession.class, RakNetSession.class, BedrockServer.class);

                constructor.setAccessible(true);
                server = constructor.newInstance(session, connection, BedrockReversionServer.this);
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                e.printStackTrace();
                connection.disconnect(DisconnectReason.CLOSED_BY_REMOTE_PEER);
                return;
            }

            connection.setListener(server);
        }

        @Override
        public void onUnhandledDatagram(ChannelHandlerContext ctx, DatagramPacket packet) {
            if (BedrockReversionServer.this.getHandler() != null) {
                BedrockReversionServer.this.getHandler().onUnhandledDatagram(ctx, packet);
            }
        }
    }

}
