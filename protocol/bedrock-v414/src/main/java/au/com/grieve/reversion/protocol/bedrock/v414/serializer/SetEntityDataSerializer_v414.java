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
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import com.nukkitx.protocol.bedrock.v291.serializer.SetEntityDataSerializer_v291;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetEntityDataSerializer_v414 extends SetEntityDataSerializer_v291 {
    public static final SetEntityDataSerializer_v414 INSTANCE = new SetEntityDataSerializer_v414();


    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, SetEntityDataPacket packet) {
        super.serialize(buffer, helper, packet);

        // TODO 0x00
        buffer.writeByte(0);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, SetEntityDataPacket packet) {
        super.deserialize(buffer, helper, packet);

        // TODO 0x00
        buffer.readByte();
    }

}
