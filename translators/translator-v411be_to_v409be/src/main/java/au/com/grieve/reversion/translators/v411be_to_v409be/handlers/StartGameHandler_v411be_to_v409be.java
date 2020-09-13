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
                .map(i -> new StartGamePacket.ItemEntry(i.getIdentifier(), getTranslator().getRegisteredTranslator().getItemMapper().mapRuntimeIdToUpstream(i.getId())))
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
