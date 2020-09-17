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
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StartGameHandler_Bedrock extends PacketHandler<BedrockTranslator, StartGamePacket> {
    protected boolean initialized = false;

    public StartGameHandler_Bedrock(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(StartGamePacket packet) {
        // Reorder the BlockPalette
        NbtList<NbtMap> originalPalette = packet.getBlockPalette();
        List<NbtMap> translatedPalette = new ArrayList<>();
        for (int id = 0; id < originalPalette.size(); id++) {
            translatedPalette.add(getTranslator().getRegisteredTranslator().getBlockMapper().getBlockFromUpstreamPalette(originalPalette, id));
        }
        packet.setBlockPalette(new NbtList<>(NbtType.COMPOUND, translatedPalette));

        // Remap ItemPalette
        packet.setItemEntries(packet.getItemEntries().stream()
                .map(i -> new StartGamePacket.ItemEntry(i.getIdentifier(), getTranslator().getRegisteredTranslator().getItemMapper().mapRuntimeIdToUpstream(i.getId())))
                .collect(Collectors.toList())
        );
        return false;
    }

    @Override
    public boolean fromServer(StartGamePacket packet) {
        // Generate runtime Id from palette as we can't assume the server is sending them in canonical order
        if (!initialized) {
            initialized = true;

            NbtList<NbtMap> geyserTags = packet.getBlockPalette();

            Map<Integer, NbtMap> translatedTags = IntStream.range(0, getTranslator().getRegisteredTranslator().getBlockMapper().getUpstreamPalette().size())
                    .boxed()
                    .collect(Collectors.toMap(i -> i, i -> getTranslator().getRegisteredTranslator().getBlockMapper().getUpstreamPalette().get(i).getCompound("block")));

            for (int geyserId = 0; geyserId < geyserTags.size(); geyserId++) {
                NbtMap geyserTag = geyserTags.get(geyserId).getCompound("block");
                boolean found = false;
                for (Map.Entry<Integer, NbtMap> entry : translatedTags.entrySet()) {
                    if (geyserTag.equals(entry.getValue())) {
                        getTranslator().getRegisteredTranslator().getBlockMapper().registerRuntimeIdMapping(geyserId, entry.getKey());
                        found = true;
                        translatedTags.remove(entry.getKey());
                        break;
                    }
                }
                if (!found) {
                    // TODO Log unfound for debugging?
//                    GeyserReversionExtension.getInstance().getLogger().error("Unable to find upstream palette entry: " + geyserTag);
                    getTranslator().getRegisteredTranslator().getBlockMapper().registerRuntimeIdMapping(geyserId, 0); // Set to 0 for now
                }
                if (translatedTags.size() == 0) {
                    break;
                }
            }

            // TODO - Add extra in
//            if (translatedTags.size() > 0) {
//                GeyserReversionExtension.getInstance().getLogger().error("Extra upstream unmatched palette entries: \n" + translatedTags);
//            }
        }

        return super.fromServer(packet);
    }

}
