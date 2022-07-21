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

package au.com.grieve.reversion.editions.bedrock;

import au.com.grieve.reversion.api.LoginData;
import au.com.grieve.reversion.api.ReversionSession;
import au.com.grieve.reversion.api.Translator;
import au.com.grieve.reversion.exceptions.LoginException;
import au.com.grieve.reversion.exceptions.TranslatorException;
import com.nukkitx.network.raknet.RakNetSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.annotation.NoEncryption;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoop;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;


@Getter
public class BedrockReversionSession extends ReversionSession {
    private final InternalLogger log = InternalLoggerFactory.getInstance(BedrockReversionSession.class);

    private final ConcurrentLinkedDeque<BedrockPacketHandler> fromServerHandlers = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<BedrockPacketHandler> toServerHandlers = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<BedrockPacketHandler> fromClientHandlers = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<BedrockPacketHandler> toClientHandlers = new ConcurrentLinkedDeque<>();

    private final BedrockReversionServer server;
    private Translator translator;
    private LoginData loginData;

    protected final EventLoop eventLoop;

    protected final Queue<BedrockPacket> queuedPackets;

    public BedrockReversionSession(BedrockReversionServer server, RakNetSession connection, EventLoop eventLoop, BedrockWrapperSerializer serializer) {
        super(connection, eventLoop, serializer);

        this.server = server;
        this.eventLoop = eventLoop;
        setBatchHandler(new ReversionBatchHandler());
        getFromClientHandlers().add(new LoginHandler());

        try {
            Field queuedPacketsField = BedrockSession.class.getDeclaredField("queuedPackets");
            queuedPacketsField.setAccessible(true);
            //noinspection unchecked
            queuedPackets = (Queue<BedrockPacket>) queuedPacketsField.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ReversionBatchHandler getBatchHandler() {
        return (ReversionBatchHandler) super.getBatchHandler();
    }

    @Override
    public boolean toClient(BedrockPacket packet) {
        // Take care of toClient Handlers
        for (BedrockPacketHandler handler : getToClientHandlers()) {
            if (packet.handle(handler)) {
                return true;
            }
        }

        super.sendPacket(packet);
        return true;
    }

    @Override
    public boolean toServer(BedrockPacket packet) {
        // Take care of toServer Handlers
        for (BedrockPacketHandler handler : getToServerHandlers()) {
            if (packet.handle(handler)) {
                return true;
            }
        }

        if (getPacketHandler() == null || !packet.handle(getPacketHandler())) {
            log.debug("Unhandled packet for {}: {}", getAddress(), packet);
            return false;
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean handleOutgoingPacket(BedrockPacket packet) {
        // Take care of fromServer Handlers
        for (BedrockPacketHandler handler : getFromServerHandlers()) {
            if (packet.handle(handler)) {
                return true;
            }
        }

        return translator != null && translator.getServerTranslator().fromServer(packet);
    }

    @Override
    public void setTranslator(Translator translator) {
        this.translator = translator;
        setPacketCodec(translator.getCodec());
    }

    @Override
    public void sendPacket(BedrockPacket packet) {
        if (!handleOutgoingPacket(packet)) {
            super.sendPacket(packet);
        }
    }

    @Override
    public void sendPacketImmediately(BedrockPacket packet) {
        if (!handleOutgoingPacket(packet)) {
            super.sendPacketImmediately(packet);
        }
    }

    @Override
    public void tick() {
        this.eventLoop.execute(this::onTick);
    }

    protected void onTick() {
        if (!this.isClosed()) {
            this.sendQueued();
        }
    }

    protected void sendQueued() {
        BedrockPacket packet;
        List<BedrockPacket> toBatch = new ObjectArrayList<>();

        while ((packet = queuedPackets.poll()) != null) {
            if (packet.getClass().isAnnotationPresent(NoEncryption.class)) {
                // We hit a unencryptable packet. Send the current wrapper and then send the unencryptable packet.
                if (!toBatch.isEmpty()) {
                    this.sendWrapped(toBatch, true);
                    toBatch = new ObjectArrayList<>();
                }

                this.sendWrapped(Collections.singletonList(packet), false);
                continue;
            }

            toBatch.add(packet);
        }

        if (!toBatch.isEmpty()) {
            this.sendWrapped(toBatch, true);
        }
    }

    @Getter
    public class ReversionBatchHandler implements BatchHandler {

        @Override
        public void handle(BedrockSession session, ByteBuf compressed, Collection<BedrockPacket> packets) {
            outer:
            for (BedrockPacket packet : packets) {
                if (session.isLogging() && log.isTraceEnabled()) {
                    log.trace("Inbound {}: {}", session.getAddress(), packet);
                }

                // Take care of fromClient Handlers
                for (BedrockPacketHandler handler : getFromClientHandlers()) {
                    if (packet.handle(handler)) {
                        continue outer;
                    }
                }

                if (translator != null && translator.fromClient(packet)) {
                    continue;
                }

                toServer(packet);
            }
        }
    }

    protected LoginData createLoginData(LoginPacket packet) throws LoginException {
        return new BedrockLoginData(this, packet);
    }

    @Getter
    public class LoginHandler implements BedrockPacketHandler {

        @Override
        public boolean handle(LoginPacket packet) {
            Translator translator;
            try {
                // Find matching Codec in our supported list
                Optional<BedrockPacketCodec> toCodec = server.getToCodecs().stream()
                        .filter(c -> c.getProtocolVersion() == packet.getProtocolVersion())
                        .findFirst();

                // No need for translation when packet codec matches our to codec
                if (toCodec.isPresent() && server.getFromEdition().equals(server.getToEdition())) {
                    setPacketCodec(toCodec.get());
                } else {
                    translator = server.createTranslatorChain(packet.getProtocolVersion(), BedrockReversionSession.this);

                    if (translator == null) {
                        disconnect("You are running an unsupported version: " + packet.getProtocolVersion());
                        return true;
                    }
                    setTranslator(translator);
                }

                // Retrieve login data
                loginData = createLoginData(packet);
            } catch (TranslatorException | LoginException e) {
                log.error(e);
            }
            return false;
        }

    }

}
