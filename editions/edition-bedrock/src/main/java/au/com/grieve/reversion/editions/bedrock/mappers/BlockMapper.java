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
import au.com.grieve.reversion.editions.bedrock.utils.VariableStore;
import au.com.grieve.reversion.exceptions.MapperException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtUtils;
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

/**
 * Simple Block Translator
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockMapper {
    public static final BlockMapper DEFAULT = new BlockMapper();

    private final Map<String, List<MapConfig>> mapList = new HashMap<>();
    private final Map<Integer, Short> nameToIdMap = new HashMap<>();
    int version;

    public BlockMapper(InputStream mapStream, InputStream runtimeStatesStream) throws MapperException {
        loadMapper(mapStream);
        loadRuntimeStates(runtimeStatesStream);
    }

    public void loadMapper(InputStream stream) throws MapperException {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        MapConfig[] mapConfigs;
        try {
            mapConfigs = mapper.readValue(stream, MapConfig[].class);
        } catch (IOException e) {
            throw new MapperException("Unable load remap file", e);
        }

        for (MapConfig mapConfig : mapConfigs) {
            if (!mapList.containsKey(mapConfig.getDownstream().getName())) {
                mapList.put(mapConfig.getDownstream().getName(), new ArrayList<>());
            }

            this.mapList.get(mapConfig.getDownstream().getName()).add(mapConfig);
        }
    }

    public void loadRuntimeStates(InputStream stream) throws MapperException {
        NbtList<NbtMap> blocksTag;
        try (NBTInputStream nbtInputStream = NbtUtils.createNetworkReader(stream)) {
            //noinspection unchecked
            blocksTag = (NbtList<NbtMap>) nbtInputStream.readTag();
        } catch (Exception e) {
            throw new MapperException("Unable to get blocks from runtime block states", e);
        }

        for (NbtMap tag : blocksTag) {
            if (version == 0) {
                version = tag.getCompound("block").getInt("version");
            }
            System.err.println("Putting block: " + tag.getCompound("block").getString("name"));
            nameToIdMap.put(tag.getCompound("block").getString("name").hashCode(), tag.getShort("id"));
        }
    }


    public NbtMap mapBlockNbtToUpstream(BedrockTranslator translator, NbtMap original) throws MapperException {
        if (original == null) {
            return null;
        }


        NbtMap originalBlock = original.getCompound("block");
        NbtMap originalStates = originalBlock.getCompound("states");

        NbtMap translated = original;

        for (MapConfig mapConfig : mapList.getOrDefault(originalBlock.getString("name"), new ArrayList<>())) {
            VariableStore variableStore = new VariableStore();

            if (mapConfig.getDownstream().getStates().size() > 0) {
                if (originalStates == null) {
                    continue;
                }

                boolean found = false;

                for (Map.Entry<String, JsonNode> entry : mapConfig.getDownstream().getStates().entrySet()) {
                    if (!originalStates.containsKey(entry.getKey())) {
                        continue;
                    }

                    if (!variableStore.compare(originalStates.get(entry.getKey()), entry.getValue())) {
                        continue;
                    }

                    found = true;
                }

                if (!found) {
                    continue;
                }
            }

            NbtMapBuilder builder = NbtMap.builder();

            try {
                for (Map.Entry<String, JsonNode> entry : mapConfig.getUpstream().getStates().entrySet()) {
                    builder.put(entry.getKey(), variableStore.get(entry.getValue()));
                }
            } catch (IndexOutOfBoundsException e) {
                throw new AssertionError("Error with " + original + " with config " + mapConfig, e);
            }

            translated = original.toBuilder()
                    .putCompound("block", originalBlock.toBuilder()
                            .putString("name", mapConfig.getUpstream().getName())
                            .putCompound("states", builder.build())
                            .build()
                    ).build();
            break;
        }

        if (translated != original) {
            // Set ID
            int hash = translated.getCompound("block").getString("name").hashCode();
            if (!nameToIdMap.containsKey(hash)) {
                throw new MapperException("No such block: " + translated);
            }

            translated = translated.toBuilder()
                    .putShort("id", nameToIdMap.get(hash))
                    .putCompound("block", translated.getCompound("block").toBuilder()
                            .putInt("version", version)
                            .build()
                    )
                    .build();
        }

        return translated;
    }

    @ToString
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapConfig {
        Upstream upstream;
        Downstream downstream;

        @ToString
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Upstream {
            String name;
            Map<String, JsonNode> states;
        }

        @ToString
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            String name;
            Map<String, JsonNode> states;
        }

    }


}
