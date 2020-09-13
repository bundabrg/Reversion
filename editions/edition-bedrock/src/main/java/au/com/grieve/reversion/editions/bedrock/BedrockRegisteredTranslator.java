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
import au.com.grieve.reversion.api.Translator;
import au.com.grieve.reversion.editions.bedrock.mappers.BlockMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.EntityMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.ItemMapper;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;

@Builder
@Getter
public class BedrockRegisteredTranslator implements RegisteredTranslator {
    private final String fromEdition;
    private final int fromProtocolVersion;
    private final String toEdition;
    private final int toProtocolVersion;
    private final BedrockPacketCodec codec;
    private final Class<? extends Translator> translator;

    // Mappers
    private final BlockMapper blockMapper;
    private final EntityMapper entityMapper;
    private final ItemMapper itemMapper;

    @Singular("registerPacketHandler")
    private final Map<Class<? extends BedrockPacket>, Class<? extends PacketHandler<? extends Translator, ? extends BedrockPacket>>> packetHandlers;


    /**
     * Get a name to identify this translator
     *
     * @return a string name
     */
    public String getName() {
        return String.format("(%s:%d) -> (%s:%d)", fromEdition, fromProtocolVersion, toEdition, toProtocolVersion);
    }
}
