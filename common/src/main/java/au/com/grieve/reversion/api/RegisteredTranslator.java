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

package au.com.grieve.reversion.api;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;

@Builder
@Getter
public class RegisteredTranslator {
    private final String fromEdition;
    private final int fromProtocolVersion;
    private final String toEdition;
    private final int toProtocolVersion;
    private final BedrockPacketCodec codec;
    private final Class<? extends Translator> translator;

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
