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

package au.com.grieve.reversion.protocol.bedrock.v409.serializer;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.packet.MoveEntityDeltaPacket;
import com.nukkitx.protocol.bedrock.v388.serializer.MoveEntityDeltaSerializer_v388;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoveEntityDeltaSerializer_v409 extends MoveEntityDeltaSerializer_v388 {
    public static final MoveEntityDeltaSerializer_v409 INSTANCE = new MoveEntityDeltaSerializer_v409();

    private static final int HAS_X = 0x01;
    private static final int HAS_Y = 0x02;
    private static final int HAS_Z = 0x4;
    private static final int HAS_PITCH = 0x8;
    private static final int HAS_YAW = 0x10;
    private static final int HAS_ROLL = 0x20;
    private static final int HAS_UNKNOWN1 = 0x40;
    private static final int HAS_FLOAT_POSITION = 0xFE00;


    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, MoveEntityDeltaPacket packet) {
        VarInts.writeUnsignedLong(buffer, packet.getRuntimeEntityId());
        short flags = 0;

        Vector3f rotationDelta = packet.getRotationDelta();
        flags |= rotationDelta.getX() != 0 ? HAS_PITCH : 0;
        flags |= rotationDelta.getY() != 0 ? HAS_YAW : 0;
        flags |= rotationDelta.getZ() != 0 ? HAS_ROLL : 0;

        flags |= packet.getMovementDelta().getX() != 0 ? HAS_X : 0;
        flags |= packet.getMovementDelta().getY() != 0 ? HAS_Y : 0;
        flags |= packet.getMovementDelta().getZ() != 0 ? HAS_Z : 0;

        buffer.writeShortLE(flags);

        if ((flags & HAS_X) != 0) {
            VarInts.writeInt(buffer, packet.getMovementDelta().getX());
        }
        if ((flags & HAS_Y) != 0) {
            VarInts.writeInt(buffer, packet.getMovementDelta().getY());
        }
        if ((flags & HAS_Z) != 0) {
            VarInts.writeInt(buffer, packet.getMovementDelta().getZ());
        }

        if ((flags & HAS_PITCH) != 0) {
            helper.writeByteAngle(buffer, rotationDelta.getX());
        }
        if ((flags & HAS_YAW) != 0) {
            helper.writeByteAngle(buffer, rotationDelta.getY());
        }
        if ((flags & HAS_ROLL) != 0) {
            helper.writeByteAngle(buffer, rotationDelta.getZ());
        }
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, MoveEntityDeltaPacket packet) {
        packet.setRuntimeEntityId(VarInts.readUnsignedLong(buffer));
        short flags = buffer.readShortLE();
        int x = 0, y = 0, z = 0;
        float pitch = 0, yaw = 0, roll = 0;

        if ((flags & HAS_FLOAT_POSITION) != 0) {
            if ((flags & HAS_X) != 0) {
                x = (int) buffer.readFloat();
            }
            if ((flags & HAS_Y) != 0) {
                y = (int) buffer.readFloat();
            }
            if ((flags & HAS_Z) != 0) {
                z = (int) buffer.readFloat();
            }
        } else {
            if ((flags & HAS_X) != 0) {
                x = VarInts.readInt(buffer);
            }
            if ((flags & HAS_Y) != 0) {
                y = VarInts.readInt(buffer);
            }
            if ((flags & HAS_Z) != 0) {
                z = VarInts.readInt(buffer);
            }
        }

        packet.setMovementDelta(Vector3i.from(x, y, z));

        if ((flags & HAS_PITCH) != 0) {
            pitch = helper.readByteAngle(buffer);
        }
        if ((flags & HAS_YAW) != 0) {
            yaw = helper.readByteAngle(buffer);
        }
        if ((flags & HAS_ROLL) != 0) {
            roll = helper.readByteAngle(buffer);
        }
        packet.setRotationDelta(Vector3f.from(pitch, yaw, roll));
    }
}
