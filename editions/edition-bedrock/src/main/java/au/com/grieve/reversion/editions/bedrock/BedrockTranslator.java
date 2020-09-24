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

import au.com.grieve.reversion.api.PacketHandler;
import au.com.grieve.reversion.api.ReversionSession;
import au.com.grieve.reversion.api.Translator;
import au.com.grieve.reversion.editions.bedrock.mappers.EntityMapper;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BedrockTranslator implements Translator {
    private final Map<Class<? extends BedrockPacket>, PacketHandler<?, ?>> handlers = new HashMap<>();

    // Runtime Entity Map
    private final Map<Long, EntityMapper.MapConfig> runtimeEntityMap = new HashMap<>();

    private final BedrockRegisteredTranslator registeredTranslator;
    private final ReversionSession reversionSession;

    @Setter
    private Translator upstreamTranslator;
    @Setter
    private Translator downstreamTranslator;

    public BedrockTranslator(BedrockRegisteredTranslator registeredTranslator, ReversionSession reversionSession) {
        this.registeredTranslator = registeredTranslator;
        this.reversionSession = reversionSession;

        for (Map.Entry<Class<? extends BedrockPacket>, Class<? extends PacketHandler<? extends Translator, ? extends BedrockPacket>>>
                entry : registeredTranslator.getPacketHandlers().entrySet()) {
            try {
                handlers.put(entry.getKey(), entry.getValue().getConstructor(getClass()).newInstance(this));
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // Initialize the BlockMapper
        registeredTranslator.getBlockMapper().init();
    }

    @Override
    public Translator getServerTranslator() {
        return downstreamTranslator == null ? this : downstreamTranslator.getServerTranslator();
    }

    @Override
    public Translator getClientTranslator() {
        return upstreamTranslator == null ? this : upstreamTranslator.getClientTranslator();
    }

    @Override
    public <T extends BedrockPacket> boolean fromUpstream(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) handlers.get(packet.getClass());

        if (handler != null) {
            if (handler.fromUpstream(packet)) {
                return true;
            }
        }

        // Pass to our Downstream
        return toDownstream(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean fromDownstream(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) handlers.get(packet.getClass());

        if (handler != null) {
            if (handler.fromDownstream(packet)) {
                return true;
            }
        }

        // Pass to our Upstream
        return toUpstream(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean fromServer(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) handlers.get(packet.getClass());

        if (handler != null) {
            if (handler.fromServer(packet)) {
                return true;
            }
        }

        return fromDownstream(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean fromClient(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) handlers.get(packet.getClass());

        if (handler != null) {
            if (handler.fromClient(packet)) {
                return true;
            }
        }

        return fromUpstream(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean toUpstream(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) handlers.get(packet.getClass());

        if (handler != null) {
            if (handler.toUpstream(packet)) {
                return true;
            }
        }

        return upstreamTranslator != null ? upstreamTranslator.fromDownstream(packet) : toClient(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean toDownstream(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) handlers.get(packet.getClass());

        if (handler != null) {
            if (handler.toDownstream(packet)) {
                return true;
            }
        }

        return downstreamTranslator != null ? downstreamTranslator.fromUpstream(packet) : toServer(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean toServer(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) handlers.get(packet.getClass());

        if (handler != null) {
            if (handler.toServer(packet)) {
                return true;
            }
        }

        return reversionSession.toServer(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean toClient(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) handlers.get(packet.getClass());

        if (handler != null) {
            if (handler.toClient(packet)) {
                return true;
            }
        }

        return reversionSession.toClient(packet);
    }

    @Override
    public BedrockPacketCodec getCodec() {
        return registeredTranslator.getCodec();
    }

    @Override
    public String toString() {
        return getRegisteredTranslator().getName();
    }
}
