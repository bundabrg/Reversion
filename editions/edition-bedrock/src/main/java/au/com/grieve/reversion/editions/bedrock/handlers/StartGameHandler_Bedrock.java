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
import au.com.grieve.reversion.exceptions.MapperException;
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

        // Translate Block Palette
        List<NbtMap> translatedPalette = new ArrayList<>();
        for (NbtMap block : packet.getBlockPalette()) {
            try {
                translatedPalette.add(getTranslator().getBlockMapper().mapBlockNbtToUpstream(getTranslator(), block));
            } catch (MapperException e) {
                e.printStackTrace();
            }
        }
        packet.setBlockPalette(new NbtList<>(NbtType.COMPOUND, translatedPalette.toArray(new NbtMap[0])));

        packet.setItemEntries(packet.getItemEntries().stream()
                .map(i -> getTranslator().getItemPaletteMapper().mapItemEntryToUpstream(getTranslator(), i))
                .collect(Collectors.toList())
        );
        return false;
    }
}
