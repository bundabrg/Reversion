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

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.packet.AddEntityPacket;
import com.nukkitx.protocol.bedrock.v313.serializer.AddEntitySerializer_v313;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddEntitySerializer_v414 extends AddEntitySerializer_v313 {
    public static final AddEntitySerializer_v414 INSTANCE = new AddEntitySerializer_v414();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, AddEntityPacket packet) {
        VarInts.writeLong(buffer, packet.getUniqueEntityId());
        VarInts.writeUnsignedLong(buffer, packet.getRuntimeEntityId());
        helper.writeString(buffer, packet.getIdentifier());
        helper.writeVector3f(buffer, packet.getPosition());
        helper.writeVector3f(buffer, packet.getMotion());
        helper.writeVector3f(buffer, packet.getRotation());
        helper.writeArray(buffer, packet.getAttributes(), this::writeAttribute);
        helper.writeEntityData(buffer, packet.getMetadata());
        helper.writeArray(buffer, packet.getEntityLinks(), helper::writeEntityLink);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, AddEntityPacket packet) {
        packet.setUniqueEntityId(VarInts.readLong(buffer));
        packet.setRuntimeEntityId(VarInts.readUnsignedLong(buffer));
        packet.setIdentifier(helper.readString(buffer));
        packet.setPosition(helper.readVector3f(buffer));
        packet.setMotion(helper.readVector3f(buffer));
        packet.setRotation(helper.readVector3f(buffer));
        helper.readArray(buffer, packet.getAttributes(), this::readAttribute);
        helper.readEntityData(buffer, packet.getMetadata());
        helper.readArray(buffer, packet.getEntityLinks(), helper::readEntityLink);
    }
}
