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

import au.com.grieve.reversion.api.PacketHandler;
import au.com.grieve.reversion.api.RegisteredTranslator;
import au.com.grieve.reversion.api.ReversionSession;
import au.com.grieve.reversion.api.Translator;
import au.com.grieve.reversion.editions.bedrock.mappers.BlockMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.EnchantmentMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.EntityMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.ItemMapper;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BedrockTranslator implements Translator {
    private final Map<String, Object> storage = new HashMap<>();
    private final RegisteredTranslator registeredTranslator;
    private final ReversionSession reversionSession;
    protected ItemMapper itemMapper;
    protected BlockMapper blockMapper;
    protected EntityMapper entityMapper;
    protected EnchantmentMapper enchantmentMapper;
    @Setter
    private Translator upstreamTranslator;

    @Setter
    private Translator downstreamTranslator;

    public BedrockTranslator(RegisteredTranslator registeredTranslator, ReversionSession reversionSession) {
        this.registeredTranslator = registeredTranslator;
        this.reversionSession = reversionSession;

        itemMapper = ItemMapper.DEFAULT;
        blockMapper = BlockMapper.DEFAULT;
        entityMapper = EntityMapper.DEFAULT;
        enchantmentMapper = EnchantmentMapper.DEFAULT;
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
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) registeredTranslator.getPacketHandlers().get(packet.getClass());

        if (handler != null) {
            if (handler.fromUpstream(this, packet)) {
                return true;
            }
        }

        // Pass to our Downstream
        return toDownstream(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean fromDownstream(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) registeredTranslator.getPacketHandlers().get(packet.getClass());

        if (handler != null) {
            if (handler.fromDownstream(this, packet)) {
                return true;
            }
        }

        // Pass to our Upstream
        return toUpstream(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean fromServer(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) registeredTranslator.getPacketHandlers().get(packet.getClass());

        if (handler != null) {
            if (handler.fromServer(this, packet)) {
                return true;
            }
        }

        return fromDownstream(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean fromClient(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) registeredTranslator.getPacketHandlers().get(packet.getClass());

        if (handler != null) {
            if (handler.fromClient(this, packet)) {
                return true;
            }
        }

        return fromUpstream(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean toUpstream(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) registeredTranslator.getPacketHandlers().get(packet.getClass());

        if (handler != null) {
            if (handler.toUpstream(this, packet)) {
                return true;
            }
        }

        return upstreamTranslator != null ? upstreamTranslator.fromDownstream(packet) : toClient(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean toDownstream(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) registeredTranslator.getPacketHandlers().get(packet.getClass());

        if (handler != null) {
            if (handler.toDownstream(this, packet)) {
                return true;
            }
        }

        return downstreamTranslator != null ? downstreamTranslator.fromUpstream(packet) : toServer(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean toServer(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) registeredTranslator.getPacketHandlers().get(packet.getClass());

        if (handler != null) {
            if (handler.toServer(this, packet)) {
                return true;
            }
        }

        return reversionSession.toServer(packet);
    }

    @Override
    public <T extends BedrockPacket> boolean toClient(T packet) {
        @SuppressWarnings("unchecked")
        PacketHandler<BedrockTranslator, T> handler = (PacketHandler<BedrockTranslator, T>) registeredTranslator.getPacketHandlers().get(packet.getClass());

        if (handler != null) {
            if (handler.toClient(this, packet)) {
                return true;
            }
        }

        return reversionSession.toClient(packet);
    }

    @Override
    public BedrockPacketCodec getCodec() {
        return registeredTranslator.getCodec();
    }
}
