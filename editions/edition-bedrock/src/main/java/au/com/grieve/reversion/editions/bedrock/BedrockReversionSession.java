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

package au.com.grieve.reversion.editions.bedrock;

import au.com.grieve.reversion.api.LoginData;
import au.com.grieve.reversion.api.ReversionSession;
import au.com.grieve.reversion.api.Translator;
import au.com.grieve.reversion.exceptions.LoginException;
import au.com.grieve.reversion.exceptions.TranslatorException;
import com.nukkitx.network.raknet.RakNetSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoop;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.Getter;

import java.util.Collection;
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

    public BedrockReversionSession(BedrockReversionServer server, RakNetSession connection, EventLoop eventLoop, BedrockWrapperSerializer serializer) {
        super(connection, eventLoop, serializer);

        this.server = server;
        setBatchHandler(new ReversionBatchHandler());
        getFromClientHandlers().add(new LoginHandler());
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

    @Override
    public void setTranslator(Translator translator) {
        this.translator = translator;
        setPacketCodec(translator.getCodec());
    }

    /**
     * Send packets through translation chain
     *
     * @param packet packet to send
     */
    @Override
    public void sendPacket(BedrockPacket packet) {
        // Take care of fromServer Handlers
        for (BedrockPacketHandler handler : getFromServerHandlers()) {
            if (packet.handle(handler)) {
                return;
            }
        }

        if (translator != null) {
            translator.getServerTranslator().fromServer(packet);
            return;
        }

        // Else send them directly
        toClient(packet);
    }

    // TODO all packets are immediate for now
    @Override
    public void sendPacketImmediately(BedrockPacket packet) {
        sendPacket(packet);
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
                // No need for translation when packet codec matches our to codec
                if (packet.getProtocolVersion() == server.getToCodec().getProtocolVersion() && server.getFromEdition().equals(server.getToEdition())) {
                    setPacketCodec(server.getToCodec());
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
