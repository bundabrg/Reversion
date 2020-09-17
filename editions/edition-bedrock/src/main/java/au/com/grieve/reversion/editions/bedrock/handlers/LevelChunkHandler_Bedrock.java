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

package au.com.grieve.reversion.editions.bedrock.handlers;

import au.com.grieve.reversion.api.PacketHandler;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class LevelChunkHandler_Bedrock extends PacketHandler<BedrockTranslator, LevelChunkPacket> {

    public LevelChunkHandler_Bedrock(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(LevelChunkPacket packet) {
        ByteBuf buf = Unpooled.wrappedBuffer(packet.getData());
        ByteBuf translated = Unpooled.buffer(32);

        translated.writeByte(buf.readByte());

        byte sectionCount = buf.readByte();
        translated.writeByte(sectionCount);

        for (byte sectionUpto = 0; sectionUpto < sectionCount; sectionUpto++) {
            int bitsPerByte = buf.readByte();
            translated.writeByte(bitsPerByte);

            bitsPerByte = bitsPerByte >> 1;
            int entriesPerWord = 32 / bitsPerByte;

            translated.writeBytes(buf, (int) Math.ceil((double) 4096 / (double) entriesPerWord) * 4);

            int paletteSize = VarInts.readInt(buf);
            VarInts.writeInt(translated, paletteSize);

            for (int i = 0; i < paletteSize; i++) {
                int original_block = VarInts.readInt(buf);
                int translated_block = getTranslator().getRegisteredTranslator().getBlockMapper().mapRuntimeIdToUpstream(original_block);
//                if (original_block < 10 && original_block != translated_block) {
//                    System.err.println("Translated " + original_block + " to " + translated_block);
//                }
                VarInts.writeInt(translated, translated_block);
            }
        }
        // Write rest of buffer
        translated.writeBytes(buf);
        byte[] translatedBytes = new byte[translated.readableBytes()];

        translated.readBytes(translatedBytes);
        packet.setData(translatedBytes);

        return false;
    }
}
