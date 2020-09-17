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

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.packet.MoveEntityDeltaPacket;
import com.nukkitx.protocol.bedrock.util.TriConsumer;
import com.nukkitx.protocol.bedrock.v388.serializer.MoveEntityDeltaSerializer_v388;
import io.netty.buffer.ByteBuf;

public class MoveEntityDeltaSerializer_v409 extends MoveEntityDeltaSerializer_v388 {

    protected static final TriConsumer<ByteBuf, BedrockPacketHelper, MoveEntityDeltaPacket> READER_X =
            (buffer, helper, packet) -> packet.setX(buffer.readFloatLE());
    protected static final TriConsumer<ByteBuf, BedrockPacketHelper, MoveEntityDeltaPacket> READER_Y =
            (buffer, helper, packet) -> packet.setY(buffer.readFloatLE());
    protected static final TriConsumer<ByteBuf, BedrockPacketHelper, MoveEntityDeltaPacket> READER_Z =
            (buffer, helper, packet) -> packet.setZ(buffer.readFloatLE());

    protected static final TriConsumer<ByteBuf, BedrockPacketHelper, MoveEntityDeltaPacket> WRITER_X =
            (buffer, helper, packet) -> buffer.writeFloatLE(packet.getX());
    protected static final TriConsumer<ByteBuf, BedrockPacketHelper, MoveEntityDeltaPacket> WRITER_Y =
            (buffer, helper, packet) -> buffer.writeFloatLE(packet.getY());
    protected static final TriConsumer<ByteBuf, BedrockPacketHelper, MoveEntityDeltaPacket> WRITER_Z =
            (buffer, helper, packet) -> buffer.writeFloatLE(packet.getZ());

    public static final MoveEntityDeltaSerializer_v409 INSTANCE = new MoveEntityDeltaSerializer_v409();

    protected MoveEntityDeltaSerializer_v409() {
        super();

        this.readers.put(MoveEntityDeltaPacket.Flag.HAS_X, READER_X);
        this.readers.put(MoveEntityDeltaPacket.Flag.HAS_Y, READER_Y);
        this.readers.put(MoveEntityDeltaPacket.Flag.HAS_Z, READER_Z);

        this.writers.put(MoveEntityDeltaPacket.Flag.HAS_X, WRITER_X);
        this.writers.put(MoveEntityDeltaPacket.Flag.HAS_Y, WRITER_Y);
        this.writers.put(MoveEntityDeltaPacket.Flag.HAS_Z, WRITER_Z);
    }
}
