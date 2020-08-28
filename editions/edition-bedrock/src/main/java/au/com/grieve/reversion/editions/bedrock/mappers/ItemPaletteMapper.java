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

package au.com.grieve.reversion.editions.bedrock.mappers;

import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.exceptions.MapperException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple Item Palette Translator
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemPaletteMapper {
    public static final ItemPaletteMapper DEFAULT = new ItemPaletteMapper();

    private final Map<Short, Short> toUpstreamMap = new HashMap<>();

    public ItemPaletteMapper(InputStream stream) throws MapperException {
        load(stream);
    }


    public void load(InputStream stream) throws MapperException {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        MapConfig[] mapConfigs;
        try {
            mapConfigs = mapper.readValue(stream, MapConfig[].class);
        } catch (IOException e) {
            throw new MapperException("Unable load remap file", e);
        }

        for (MapConfig mapConfig : mapConfigs) {
            toUpstreamMap.put(mapConfig.getDownstream().getId(), mapConfig.getUpstream().getId());
        }
    }

    public StartGamePacket.ItemEntry mapItemEntryToUpstream(BedrockTranslator translator, StartGamePacket.ItemEntry original) {
        if (original == null) {
            return null;
        }

        if (toUpstreamMap.containsKey(original.getId())) {
            return new StartGamePacket.ItemEntry(original.getIdentifier(), toUpstreamMap.get(original.getId()));
        }
        return original;
    }

    /**
     * This just simply replaces the id using the item pallette map
     *
     * @param translator the associated translator
     * @param original   original item
     * @return translated item
     */
    public ItemData mapItemDataToUpstream(BedrockTranslator translator, ItemData original) {
        if (original == null) {
            return null;
        }

        short id = Integer.valueOf(original.getId()).shortValue();

        if (toUpstreamMap.containsKey(id)) {
            return ItemData.of(toUpstreamMap.get(id).intValue(), original.getDamage(), original.getCount(), original.getTag(),
                    original.getCanPlace(), original.getCanBreak(), original.getBlockingTicks());
        }

        return original;
    }


    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapConfig {
        Upstream upstream;
        Downstream downstream;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Upstream {
            short id;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            short id;
        }

    }


}
