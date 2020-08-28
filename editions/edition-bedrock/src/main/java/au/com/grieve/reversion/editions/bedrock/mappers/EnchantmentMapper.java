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

import au.com.grieve.reversion.editions.bedrock.utils.VariableStore;
import au.com.grieve.reversion.exceptions.MapperException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnchantmentMapper {
    public static final EnchantmentMapper DEFAULT = new EnchantmentMapper();

    private final Map<Short, List<MapConfig>> mapList = new HashMap<>();

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
     * Translate a list of enchants
     */
    public List<NbtMap> mapEnchantmentNbtToUpstream(List<NbtMap> original) {
        if (original == null) {
            return null;
        }

        return original.stream()
                .map(this::mapEnchantmentNbtToUpstream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Translate enchantment Nbt
     *
     * @param original original ItemData
     * @return translated ItemData or null means removed
     */
    public NbtMap mapEnchantmentNbtToUpstream(NbtMap original) {
        System.err.println("Tag: " + original);
        if (original == null) {
            return null;
        }

        for (MapConfig mapConfig : mapList.getOrDefault(original.getShort("id"), new ArrayList<>())) {
            System.err.println("Comparing to: " + mapConfig);
            VariableStore variableStore = new VariableStore();

            if (variableStore.compare(original.getShort("lvl"), mapConfig.getDownstream().getLvl())) {
                // No upstream means remove the enchantment
                if (mapConfig.getUpstream() == null) {
                    return null;
                }

                NbtMapBuilder builder = NbtMap.builder();
                builder.putShort("id", mapConfig.getUpstream().getId());
                builder.put("lvl", variableStore.get(mapConfig.getUpstream().getLvl()));
                return builder.build();
            }
        }
        return original;
    }

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapConfig {
        Upstream upstream;
        Downstream downstream;

        @Getter
        @ToString
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Upstream {
            short id;
            JsonNode lvl;
        }

        @Getter
        @ToString
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            short id;
            JsonNode lvl;
        }

    }
}
