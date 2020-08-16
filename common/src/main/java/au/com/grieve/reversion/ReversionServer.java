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

package au.com.grieve.reversion;

import au.com.grieve.reversion.annotations.ReversionTranslator;
import au.com.grieve.reversion.api.BaseTranslator;
import au.com.grieve.reversion.api.ReversionSession;
import au.com.grieve.reversion.api.TranslatorException;
import com.nukkitx.network.raknet.RakNetServerListener;
import com.nukkitx.network.raknet.RakNetServerSession;
import com.nukkitx.network.raknet.RakNetSession;
import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.network.util.EventLoops;
import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockRakNetSessionListener;
import com.nukkitx.protocol.bedrock.BedrockServer;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
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
public abstract class ReversionServer extends BedrockServer {
    private final String toEdition;
    private final int toVersion;
    private final EventLoopGroup eventLoopGroup;

    // Supported Translators
    private final List<Class<? extends BaseTranslator>> registeredTranslators = new ArrayList<>();

    public ReversionServer(String toEdition, int toVersion, InetSocketAddress address) {
        this(toEdition, toVersion, address, 1);
    }

    public ReversionServer(String toEdition, int toVersion, InetSocketAddress address, int maxThreads) {
        this(toEdition, toVersion, address, maxThreads, EventLoops.commonGroup());
    }

    public ReversionServer(String toEdition, int toVersion, InetSocketAddress address, int maxThreads, EventLoopGroup eventLoopGroup) {
        super(address, maxThreads, eventLoopGroup);

        this.eventLoopGroup = eventLoopGroup;
        this.toEdition = toEdition;
        this.toVersion = toVersion;
        getRakNet().setListener(createRakNetServerListener());
    }

    public void registerTranslator(Class<? extends BaseTranslator> translatorClass) throws TranslatorException {
        if (translatorClass.getAnnotation(ReversionTranslator.class) == null) {
            throw new TranslatorException(String.format("Translator '%s' missing @ReversionTranslator annotation", translatorClass.getName()));
        }
        registeredTranslators.add(translatorClass);
    }

    /**
     * Create a translator chain from the client to the server.
     */
    protected BaseTranslator createTranslatorChain(int fromVersion, ReversionSession session) throws TranslatorException {
        List<Class<? extends BaseTranslator>> bestChain = getBestTranslatorChain(getFromEdition(), fromVersion,
                getToEdition(), getToVersion(), new ArrayList<>(registeredTranslators));

        if (bestChain == null) {
            return null;
        }

        BaseTranslator ret = null;
        BaseTranslator current = null;
        for (Class<? extends BaseTranslator> registeredTranslator : bestChain) {
            BaseTranslator translator;
            try {
                translator = registeredTranslator.getConstructor(ReversionSession.class)
                        .newInstance(session);
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
    protected List<Class<? extends BaseTranslator>> getBestTranslatorChain(String fromEdition, int fromVersion, String toEdition,
                                                                           int toVersion, List<Class<? extends BaseTranslator>> registeredTranslators) {
        List<Class<? extends BaseTranslator>> best = null;
        for (Class<? extends BaseTranslator> translator : registeredTranslators) {
            ReversionTranslator annotation = translator.getAnnotation(ReversionTranslator.class);
            if (!annotation.fromEdition().equals(fromEdition) || annotation.fromVersion() != fromVersion) {
                continue;
            }

            // Found a solution
            if (annotation.toEdition().equals(toEdition) && annotation.toVersion() == toVersion) {
                return Collections.singletonList(translator);
            }

            // Find shortest
            List<Class<? extends BaseTranslator>> newRegisteredTranslators = registeredTranslators.stream().filter(t -> t != translator).collect(Collectors.toList());
            List<Class<? extends BaseTranslator>> current = getBestTranslatorChain(annotation.toEdition(), annotation.toVersion(), toEdition, toVersion, newRegisteredTranslators);

            if (current == null) {
                continue;
            }

            if (best == null || best.size() > current.size() + 1) {
                best = new ArrayList<>();
                best.add(translator);
                best.addAll(current);
            }
        }
        return best;
    }

    public ReversionServerSession createSession(RakNetSession connection, EventLoop eventLoop, BedrockWrapperSerializer serializer) {
        return new ReversionServerSession(this, connection, eventLoop, serializer);
    }

    public LoginData createLoginData(ReversionServerSession session, LoginPacket packet) throws LoginData.LoginException {
        return new LoginData(session, packet);
    }

    public RakNetServerListener createRakNetServerListener() {
        return new ReversionRakNetServerListener();
    }

    public abstract String getFromEdition();

    public class ReversionRakNetServerListener implements RakNetServerListener {
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
            ReversionServerSession session = createSession(connection, getEventLoopGroup().next(), serializer);

            BedrockRakNetSessionListener.Server server;
            try {
                Constructor<BedrockRakNetSessionListener.Server> constructor = BedrockRakNetSessionListener.Server.class
                        .getDeclaredConstructor(BedrockServerSession.class, RakNetSession.class, BedrockServer.class);

                constructor.setAccessible(true);
                server = constructor.newInstance(session, connection, ReversionServer.this);
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
