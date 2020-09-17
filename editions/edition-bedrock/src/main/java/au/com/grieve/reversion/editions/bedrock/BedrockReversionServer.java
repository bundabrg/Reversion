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

package au.com.grieve.reversion.editions.bedrock;

import au.com.grieve.reversion.api.ReversionServer;
import au.com.grieve.reversion.api.ReversionSession;
import au.com.grieve.reversion.api.Translator;
import au.com.grieve.reversion.exceptions.TranslatorException;
import com.nukkitx.network.raknet.RakNetServerListener;
import com.nukkitx.network.raknet.RakNetServerSession;
import com.nukkitx.network.raknet.RakNetSession;
import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.network.util.EventLoops;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockRakNetSessionListener;
import com.nukkitx.protocol.bedrock.BedrockServer;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializer;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializers;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BedrockReversionServer extends ReversionServer {
    private final String fromEdition = "bedrock";
    private final String toEdition = "bedrock";

    // Supported Translators
    private final List<BedrockRegisteredTranslator> registeredTranslators = new ArrayList<>();

    public BedrockReversionServer(BedrockPacketCodec toCodec, InetSocketAddress address) {
        this(toCodec, address, 1);
    }

    public BedrockReversionServer(BedrockPacketCodec toCodec, InetSocketAddress address, int maxThreads) {
        this(toCodec, address, maxThreads, EventLoops.commonGroup());
    }

    public BedrockReversionServer(BedrockPacketCodec toCodec, InetSocketAddress address, int maxThreads, EventLoopGroup eventLoopGroup) {
        super(toCodec, address, maxThreads, eventLoopGroup);

        getRakNet().setListener(createRakNetServerListener());
    }

    /**
     * Register a Translator for this Server
     *
     * @param registeredTranslator The translator to register
     * @return ourself to allow chaining
     */
    public ReversionServer registerTranslator(BedrockRegisteredTranslator registeredTranslator) {
        registeredTranslators.add(registeredTranslator);
        return this;
    }

    /**
     * Create a translator chain from the client to the server.
     */
    protected Translator createTranslatorChain(int fromVersion, ReversionSession session) throws TranslatorException {
        List<BedrockRegisteredTranslator> bestChain = getBestTranslatorChain(getFromEdition(), fromVersion,
                getToEdition(), getToCodec().getProtocolVersion(), new ArrayList<>(getRegisteredTranslators()));

        if (bestChain == null) {
            return null;
        }

        Translator ret = null;
        Translator current = null;
        for (BedrockRegisteredTranslator registeredTranslator : bestChain) {
            Translator translator;
            try {
                translator = registeredTranslator.getTranslator()
                        .getConstructor(BedrockRegisteredTranslator.class, ReversionSession.class)
                        .newInstance(registeredTranslator, session);
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new TranslatorException(e);
            }

            if (current != null) {
                translator.setUpstreamTranslator(current);
                current.setDownstreamTranslator(translator);
            }

            if (ret == null) {
                ret = translator;
            }

            current = translator;
        }

        return ret;
    }

    /**
     * Return shortest chain from options available in registeredTranslators
     *
     * @param fromEdition           from edition
     * @param fromVersion           from protocol version
     * @param toEdition             to edition
     * @param toVersion             to protocol version
     * @param registeredTranslators registered translators
     * @return list of Registered translators satisfying condition else null
     */
    protected List<BedrockRegisteredTranslator> getBestTranslatorChain(String fromEdition, int fromVersion, String toEdition,
                                                                       int toVersion, List<BedrockRegisteredTranslator> registeredTranslators) {
        List<BedrockRegisteredTranslator> best = null;
        for (BedrockRegisteredTranslator registeredTranslator : registeredTranslators) {
            if (!registeredTranslator.getFromEdition().equals(fromEdition) || registeredTranslator.getFromProtocolVersion() != fromVersion) {
                continue;
            }

            // Found a solution
            if (registeredTranslator.getToEdition().equals(toEdition) && registeredTranslator.getToProtocolVersion() == toVersion) {
                return Collections.singletonList(registeredTranslator);
            }

            // Find shortest
            List<BedrockRegisteredTranslator> newRegisteredTranslators = registeredTranslators.stream().filter(t -> t != registeredTranslator).collect(Collectors.toList());
            List<BedrockRegisteredTranslator> current = getBestTranslatorChain(registeredTranslator.getToEdition(), registeredTranslator.getToProtocolVersion(), toEdition, toVersion, newRegisteredTranslators);

            if (current == null) {
                continue;
            }

            if (best == null || best.size() > current.size() + 1) {
                best = new ArrayList<>();
                best.add(registeredTranslator);
                best.addAll(current);
            }
        }
        return best;
    }

    public BedrockReversionSession createSession(RakNetSession connection, EventLoop eventLoop, BedrockWrapperSerializer serializer) {
        return new BedrockReversionSession(this, connection, eventLoop, serializer);
    }

    public RakNetServerListener createRakNetServerListener() {
        return new BedrockRakNetServerListener();
    }

    public class BedrockRakNetServerListener implements RakNetServerListener {
        public boolean onConnectionRequest(InetSocketAddress address) {
            return getHandler() == null || getHandler().onConnectionRequest(address);
        }

        protected BedrockPong processQuery(InetSocketAddress address) {
            if (getHandler() != null) {
                BedrockPong pong = getHandler().onQuery(address);
                if (pong != null) {
                    try {
                        Method setServerId = pong.getClass().getDeclaredMethod("setServerId", long.class);
                        setServerId.setAccessible(true);
                        setServerId.invoke(pong, getRakNet().getGuid());
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                return pong;
            }
            return null;
        }

        public byte[] onQuery(InetSocketAddress address) {
            BedrockPong pong = processQuery(address);
            if (pong != null) {
                try {
                    Method toRakNet = pong.getClass().getDeclaredMethod("toRakNet");
                    toRakNet.setAccessible(true);
                    return (byte[]) toRakNet.invoke(pong);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            return null;
        }

        public void onSessionCreation(RakNetServerSession connection) {
            BedrockWrapperSerializer serializer = BedrockWrapperSerializers.getSerializer(connection.getProtocolVersion());
            BedrockReversionSession session = createSession(connection, getEventLoopGroup().next(), serializer);

            BedrockRakNetSessionListener.Server server;
            try {
                Constructor<BedrockRakNetSessionListener.Server> constructor = BedrockRakNetSessionListener.Server.class
                        .getDeclaredConstructor(com.nukkitx.protocol.bedrock.BedrockServerSession.class, RakNetSession.class, BedrockServer.class);

                constructor.setAccessible(true);
                server = constructor.newInstance(session, connection, BedrockReversionServer.this);
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                connection.disconnect(DisconnectReason.CLOSED_BY_REMOTE_PEER);
                return;
            }

            connection.setListener(server);
        }

        public void onUnhandledDatagram(ChannelHandlerContext ctx, DatagramPacket packet) {
            if (getHandler() != null) {
                getHandler().onUnhandledDatagram(ctx, packet);
            }

        }
    }
}
