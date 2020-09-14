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

package au.com.grieve.reversion.protocol.bedrock.v414.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.packet.ResourcePackStackPacket;
import com.nukkitx.protocol.bedrock.v388.serializer.ResourcePackStackSerializer_v388;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourcePackStackSerializer_v414 extends ResourcePackStackSerializer_v388 {
    public static final ResourcePackStackSerializer_v414 INSTANCE = new ResourcePackStackSerializer_v414();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ResourcePackStackPacket packet) {
        buffer.writeBoolean(packet.isForcedToAccept());
        helper.writeArray(buffer, packet.getBehaviorPacks(), this::writeEntry);
        helper.writeArray(buffer, packet.getResourcePacks(), this::writeEntry);

        // TODO - Removed isExperimental but need to check if that is correct

        helper.writeString(buffer, packet.getGameVersion());

        // TODO - 0x00 0x00 0x00 0x00 0x00
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ResourcePackStackPacket packet) {
        packet.setForcedToAccept(buffer.readBoolean());
        helper.readArray(buffer, packet.getBehaviorPacks(), this::readEntry);
        helper.readArray(buffer, packet.getResourcePacks(), this::readEntry);

        // TODO - Removed isExperimental but need to check if that is correct

        packet.setGameVersion(helper.readString(buffer));

        // TODO - 0x00 0x00 0x00 0x00 0x00
        buffer.readByte();
        buffer.readByte();
        buffer.readByte();
        buffer.readByte();
        buffer.readByte();
    }
}
