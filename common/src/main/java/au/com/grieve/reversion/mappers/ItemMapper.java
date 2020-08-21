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

package au.com.grieve.reversion.mappers;

import au.com.grieve.reversion.exceptions.MapperException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Item Translator
 */
@Getter
public class ItemMapper {
    private final Map<Integer, List<MapConfig>> mapList = new HashMap<>();

    public ItemMapper(InputStream stream) throws MapperException {
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
            if (!mapList.containsKey(mapConfig.getDownstream().getId())) {
                mapList.put(mapConfig.getDownstream().getId(), new ArrayList<>());
            }

            this.mapList.get(mapConfig.getDownstream().getId()).add(mapConfig);
        }
    }

    public ItemData toUpstream(ItemData item) {
        if (item == null) {
            return null;
        }

        for (MapConfig mapConfig : mapList.getOrDefault(item.getId(), new ArrayList<>())) {
            if (mapConfig.getDownstream().getData() == item.getDamage()) {
                NbtMap tag = item.getTag();
                if (mapConfig.getUpstream().getName() != null) {
                    NbtMapBuilder tagBuilder = tag != null ? tag.toBuilder() : NbtMap.builder();

                    tagBuilder.putCompound("display", NbtMap.builder().putString("Name", mapConfig.getUpstream().getName()).build());
                    tag = tagBuilder.build();
                }
                return ItemData.of(mapConfig.getUpstream().getId(), mapConfig.getUpstream().getData(), item.getCount(), tag, item.getCanPlace(), item.getCanBreak(), item.getBlockingTicks());
            }
        }
        return item;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapConfig {
        Upstream upstream;
        Downstream downstream;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Upstream {
            int id;
            short data = 0;
            String name;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            int id;
            short data = 0;
        }

    }


}
