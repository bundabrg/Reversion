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
import java.util.stream.Collectors;

public class StartGameHandler_Bedrock extends PacketHandler<BedrockTranslator, StartGamePacket> {

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
}
