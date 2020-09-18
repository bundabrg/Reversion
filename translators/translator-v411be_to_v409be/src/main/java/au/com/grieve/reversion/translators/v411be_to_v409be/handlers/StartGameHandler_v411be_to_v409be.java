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

package au.com.grieve.reversion.translators.v411be_to_v409be.handlers;

import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.editions.bedrock.handlers.StartGameHandler_Bedrock;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;

import java.util.stream.Collectors;

/*
    The StartGame packet now does not send the block palette for vanilla blocks. This means when translating to a lower
    version we need to read the server sent blocks and remove all the vanilla ones, then provide just the changes
 */

public class StartGameHandler_v411be_to_v409be extends StartGameHandler_Bedrock {

    public StartGameHandler_v411be_to_v409be(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(StartGamePacket packet) {
        // Remap ItemPalette
        packet.setItemEntries(packet.getItemEntries().stream()
                .map(i -> new StartGamePacket.ItemEntry(i.getIdentifier(), getTranslator().getRegisteredTranslator().getItemMapper().mapItemIdToUpstream(i.getId())))
                .collect(Collectors.toList())
        );

//        // Translate Block Palette
//        List<NbtMap> translatedPalette = new ArrayList<>();
//        for (NbtMap block : packet.getBlockPalette()) {
//            try {
//                translatedPalette.add(getTranslator().getBlockMapper().mapBlockNbtToUpstream(getTranslator(), block));
//            } catch (MapperException e) {
//                e.printStackTrace();
//            }
//        }
//        packet.setBlockPalette(new NbtList<>(NbtType.COMPOUND, translatedPalette.toArray(new NbtMap[0])));
//
//        packet.setItemEntries(packet.getItemEntries().stream()
//                .map(i -> getTranslator().getItemPaletteMapper().mapItemEntryToUpstream(getTranslator(), i))
//                .collect(Collectors.toList())
//        );
        return false;
    }

}
