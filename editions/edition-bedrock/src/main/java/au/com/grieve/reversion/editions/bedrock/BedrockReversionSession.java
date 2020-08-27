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
            Translator translator = null;
            try {
                // No need for translation when packet codec matches our to codec
                if (packet.getProtocolVersion() == server.getToCodec().getProtocolVersion() && server.getFromEdition().equals(server.getToEdition())) {
                    setPacketCodec(server.getToCodec());
                } else {
                    translator = server.createTranslatorChain(packet.getProtocolVersion(), BedrockReversionSession.this);
                    setTranslator(translator);
                }

                // Retrieve login data
                loginData = createLoginData(packet);

                if (translator == null) {
                    return false;
                }
            } catch (TranslatorException | LoginException e) {
                log.error(e);
            }
            return false;
        }

    }

}
