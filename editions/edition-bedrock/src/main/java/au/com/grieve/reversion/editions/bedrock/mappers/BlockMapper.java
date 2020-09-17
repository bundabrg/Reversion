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

package au.com.grieve.reversion.editions.bedrock.mappers;

import au.com.grieve.reversion.editions.bedrock.utils.VariableStore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.nbt.NbtUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Map blocks between upstream and downstream
 */

@Getter
public class BlockMapper {
    public static final BlockMapper DEFAULT = BlockMapper.builder().build();

    // Downstream RuntimeID to Upstream RuntimeID mapping
    private final Map<Integer, Integer> runtimeIdToUpstreamMap = new HashMap<>();
    private final Map<Integer, Integer> runtimeIdToDownstreamMap = new HashMap<>();

    // Hash of Blockname to Legacy ID mapping
    private final Map<Integer, Short> legacyIdMap = new HashMap<>();

    // Block Map
    private final Map<String, List<BlockMapperConfig>> blockMap = new HashMap<>();
    private final Supplier<InputStream> palette;
    private final Supplier<InputStream> blockMapper;
    private final Supplier<InputStream> runtimeMapper;
    // Debug - When set we will output a mapping to bake in as the RuntimeId Map
    private final String debugName;
    private final Supplier<InputStream> downstreamPalette;
    // Our Block Version
    int version;
    // Are we initialized?
    boolean initialized;
    // Our Palette
    private List<NbtMap> upstreamPalette = new ArrayList<>();

    @Builder
    public BlockMapper(Supplier<InputStream> palette, Supplier<InputStream> downstreamPalette, Supplier<InputStream> blockMapper,
                       Supplier<InputStream> runtimeMapper, String debugName) {
        this.palette = palette;
        this.downstreamPalette = downstreamPalette;
        this.blockMapper = blockMapper;
        this.runtimeMapper = runtimeMapper;
        this.debugName = debugName;

        init();
    }

    public void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        if (palette != null) {
            upstreamPalette = loadPalette(palette.get());

            initLegacyIdMap(upstreamPalette);

            // Initialize Runtime Mapper
            if (runtimeMapper != null) {
                initRuntimeIdMapFromMapper(runtimeMapper.get());
            } else {
                if (blockMapper != null) {
                    initBlockMap(blockMapper.get());
                }

                // Generate runtime ID from 2 palettes
                if (downstreamPalette != null) {
                    List<NbtMap> downstreamTags = loadPalette(downstreamPalette.get());
                    initRuntimeIdMapFromPalette(upstreamPalette, downstreamTags);
                }

                if (debugName != null) {
                    // TODO Remove this to its own tool. For now it writes files to bake in stuff for speed and memory
                    debugDump(debugName);
                }
            }
        }
    }

    protected void initBlockMap(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        BlockMapperConfig[] mapConfigs;
        try {
            mapConfigs = mapper.readValue(stream, BlockMapperConfig[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load BlockMapper file", e);
        }

        for (BlockMapperConfig mapConfig : mapConfigs) {
            if (!blockMap.containsKey(mapConfig.getDownstream().getName())) {
                blockMap.put(mapConfig.getDownstream().getName(), new ArrayList<>());
            }

            this.blockMap.get(mapConfig.getDownstream().getName()).add(mapConfig);
        }

    }

    protected void initLegacyIdMap(List<NbtMap> upstreamTags) {
        for (NbtMap tag : upstreamTags) {
            // Set version
            if (version == 0) {
                version = tag.getCompound("block").getInt("version");
            }

            // Set Name to legacyID mapping
            legacyIdMap.put(tag.getCompound("block").getString("name").hashCode(), tag.getShort("id"));
        }
    }

    protected void initRuntimeIdMapFromMapper(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        RuntimeMapperConfig[] mapConfigs;
        try {
            mapConfigs = mapper.readValue(stream, RuntimeMapperConfig[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load block_runtime_mapper file", e);
        }

        for (RuntimeMapperConfig mapConfig : mapConfigs) {
            registerRuntimeIdMapping(mapConfig.getDownstream().getId(), mapConfig.getUpstream().getId());
        }
    }

    public NbtMap getBlockFromUpstreamPalette(NbtList<NbtMap> palette, int runtimeId) {
        return runtimeId < upstreamPalette.size() ? upstreamPalette.get(runtimeId) : palette.get(runtimeId);
    }

    /**
     * Register a mapping from a downstream runtime id to an upstream runtime id
     * s
     *
     * @param downstream downstream id
     * @param upstream   upstream id
     */
    public void registerRuntimeIdMapping(int downstream, int upstream) {
        runtimeIdToUpstreamMap.put(downstream, upstream);
        if (!runtimeIdToDownstreamMap.containsKey(upstream)) {
            runtimeIdToDownstreamMap.put(upstream, downstream);
        }
    }

    /**
     * Initialize the runtime map using 2 palettes
     * <p>
     * This is an expensive method so use the baked in map instead!
     *
     * @param upstreamTags   tags from upstream palette
     * @param downstreamTags tags from downstream palette
     */
    protected void initRuntimeIdMapFromPalette(List<NbtMap> upstreamTags, List<NbtMap> downstreamTags) {
        Map<Integer, NbtMap> upstreamMap = IntStream.range(0, upstreamTags.size())
                .boxed()
                .collect(Collectors.toMap(i -> i, upstreamTags::get));

        for (int downstreamRuntimeId = 0; downstreamRuntimeId < downstreamTags.size(); downstreamRuntimeId++) {
            NbtMap originalDownstreamTag = downstreamTags.get(downstreamRuntimeId);
            NbtMap translatedDownstreamTag = mapBlockNbtToUpstream(originalDownstreamTag);
            NbtMap downstreamTag = translatedDownstreamTag.getCompound("block");
            boolean found = false;
            for (int upstreamRuntimeId : upstreamMap.keySet()) {
                NbtMap upstreamTag = upstreamMap.get(upstreamRuntimeId).getCompound("block");
                if (downstreamTag.getString("name").equals(upstreamTag.getString("name"))
                        && downstreamTag.getCompound("states").equals(upstreamTag.getCompound("states"))) {
                    registerRuntimeIdMapping(downstreamRuntimeId, upstreamRuntimeId);
                    found = true;
                    break;
                }
            }
        }
    }

    /**
     * Dump stuff to files starting with our debugName prefix
     * <p>
     * This should really be a separate tool but its easier to stick it here for now.
     * TODO remove sometime
     */
    public void debugDump(String name) {
        if (name == null || name.isEmpty()) {
            return;
        }

        // Dump existing runtime id map to a json file suitable for the runtimeMapper option
        ObjectMapper mapper = new ObjectMapper(new JsonFactory()).enable(SerializationFeature.INDENT_OUTPUT);
        List<RuntimeMapperConfig> runtimeConfigs = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : runtimeIdToUpstreamMap.entrySet()) {
            if (!entry.getKey().equals(entry.getValue())) {
                runtimeConfigs.add(RuntimeMapperConfig.builder()
                        .upstream(RuntimeMapperConfig.Upstream.builder().id(entry.getValue()).build())
                        .downstream(RuntimeMapperConfig.Downstream.builder().id(entry.getKey()).build())
                        .build());
            }
        }

        try (FileOutputStream fos = new FileOutputStream(new File(name + "-runtime_mapper.json"))) {
            mapper.writeValue(fos, runtimeConfigs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected NbtList<NbtMap> loadPalette(InputStream stream) {
        // The stream could be either a list of blocks written with varints (breaks nbt parsing) or an nbt tag containing a blocks child with the list of blocks
        // in NBT format

        try {
            PushbackInputStream pbStream = new PushbackInputStream(stream);
            int paletteType = pbStream.read();
            pbStream.unread(paletteType);

            if (paletteType == 9) {
                try (NBTInputStream nbtInputStream = NbtUtils.createNetworkReader(pbStream)) {
                    // Bare List, like generated by ProxyPass
                    //noinspection unchecked
                    return ((NbtList<NbtMap>) nbtInputStream.readTag());
                }
            }

            try (NBTInputStream nbtInputStream = new NBTInputStream(new DataInputStream(pbStream))) {
                // Compound Tag, like generated by McReverse
                NbtMap ret = (NbtMap) nbtInputStream.readTag();
                return (NbtList<NbtMap>) ret.getList("blocks", NbtType.COMPOUND);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to get blocks from block palette", e);
        }
    }

    public short getLegacyId(String blockName) {
        return legacyIdMap.getOrDefault(blockName.hashCode(), (short) 0);
    }

    public int mapRuntimeIdToUpstream(int original) {
        return runtimeIdToUpstreamMap.getOrDefault(original, original);
    }

    public int mapRuntimeIdToDownstream(int original) {
        return runtimeIdToDownstreamMap.getOrDefault(original, original);
    }

    public NbtMap mapBlockNbtToUpstream(NbtMap original) {
        if (original == null) {
            return null;
        }

        NbtMap originalBlock = original.getCompound("block");
        NbtMap originalStates = originalBlock.getCompound("states");

        NbtMap translated = original;

        for (BlockMapperConfig mapConfig : blockMap.getOrDefault(originalBlock.getString("name"), new ArrayList<>())) {
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
                throw new RuntimeException("Error with " + original + " with config " + mapConfig, e);
            }

            translated = original.toBuilder()
                    .putCompound("block", originalBlock.toBuilder()
                            .putString("name", mapConfig.getUpstream().getName())
                            .putCompound("states", builder.build())
                            .build()
                    ).build();
            break;
        }

        if (!translated.equals(original)) {
            // Set ID
            short id = getLegacyId(translated.getCompound("block").getString("name"));

            translated = translated.toBuilder()
                    .putShort("id", id)
                    .putCompound("block", translated.getCompound("block").toBuilder()
//                            .putInt("version", version)
                                    .build()
                    )
                    .build();
        }

        return translated;
    }

    @ToString
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BlockMapperConfig {
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

    // Yeah.. this is slightly ridiculous TODO maybe change file format to be more compressed? or dont use a builder
    @Builder
    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RuntimeMapperConfig {

        Upstream upstream;
        Downstream downstream;

        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @ToString
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Upstream {
            int id;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @ToString
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            int id;
        }
    }

}
