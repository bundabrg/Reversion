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
import au.com.grieve.reversion.editions.bedrock.mappers.BlockMapper;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StartGameHandler_Bedrock extends PacketHandler<BedrockTranslator, StartGamePacket> {
    protected static boolean initialized;

    public StartGameHandler_Bedrock(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(StartGamePacket packet) {
        // Reorder the BlockPalette
        NbtList<NbtMap> originalPalette = packet.getBlockPalette();
        List<NbtMap> translatedPalette = new ArrayList<>();
        for (int id = 0; id < originalPalette.size(); id++) {
            NbtMap translated = getTranslator().getRegisteredTranslator().getBlockMapper().getBlockFromUpstreamPalette(originalPalette, id);
            if (translated == null) {
                break;
            }
            translatedPalette.add(translated);
        }
        packet.setBlockPalette(new NbtList<>(NbtType.COMPOUND, translatedPalette));

        // Send ItemPalette
        packet.setItemEntries(getTranslator().getRegisteredTranslator().getItemMapper().getUpstreamPalette().stream()
                .map(i -> new StartGamePacket.ItemEntry(i.getString("name"), (short) i.getInt("id")))
                .collect(Collectors.toList())
        );
        return false;
    }

    @Override
    public boolean fromServer(StartGamePacket packet) {
        if (!initialized) {
            initialized = true;
            // We can't assume the palette is in any specific order so any packet received directly from the server will
            // be compared to the downstream block palette and an additional remapping will be provided
            BlockMapper blockMapper = getTranslator().getRegisteredTranslator().getBlockMapper();
            BlockMapper preMapper = BlockMapper.builder()
                    .upstreamPalette(blockMapper.getDownstreamPaletteSupplier())
                    .build();
            blockMapper.getPreMapper().add(preMapper);
            preMapper.initRuntimeIdMap(packet.getBlockPalette());
        }
        return super.fromServer(packet);
    }
}
