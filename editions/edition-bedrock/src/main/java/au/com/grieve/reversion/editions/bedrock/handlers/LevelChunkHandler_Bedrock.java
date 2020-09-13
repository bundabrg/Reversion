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
