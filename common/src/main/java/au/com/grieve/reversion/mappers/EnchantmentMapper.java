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
import au.com.grieve.reversion.utils.VariableStore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentMapper {
    private final Map<Integer, List<MapConfig>> mapList = new HashMap<>();

    public EnchantmentMapper(InputStream stream) throws MapperException {
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

    /**
     * Translate enchantments on ItemData
     *
     * @param original original ItemData
     * @return translated ItemData
     */
    public ItemData toUpstream(ItemData original) {
        if (original == null) {
            return null;
        }

        if (original.getTag() == null || !original.getTag().containsKey("ench")) {
            return original;
        }

        List<NbtMap> translated = new ArrayList<>();

        for (NbtMap enchantment : original.getTag().getList("ench", NbtType.COMPOUND)) {
            for (MapConfig mapConfig : mapList.getOrDefault(enchantment.getInt("id"), new ArrayList<>())) {
                VariableStore variableStore = new VariableStore();

                if (variableStore.compare(enchantment.getInt("lvl"), mapConfig.getDownstream().getLvl())) {
                    // No upstream means remove the enchantment
                    if (mapConfig.getUpstream() == null) {
                        continue;
                    }

                    NbtMapBuilder builder = NbtMap.builder();
                    builder.putInt("id", mapConfig.getUpstream().getId());
                    builder.put("lvl", variableStore.get(mapConfig.getUpstream().getLvl()));
                    enchantment = builder.build();
                    break;
                }
            }
            translated.add(enchantment);
        }

        NbtMap tag = original.getTag().toBuilder()
                .putList("ench", NbtType.COMPOUND, translated)
                .build();

        return ItemData.of(original.getId(), original.getDamage(), original.getCount(), tag, original.getCanPlace(), original.getCanBreak(), original.getBlockingTicks());
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
            JsonNode lvl;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            int id;
            JsonNode lvl;
        }

    }
}
