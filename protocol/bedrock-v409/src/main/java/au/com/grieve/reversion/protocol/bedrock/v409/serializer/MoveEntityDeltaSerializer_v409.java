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
