/*
 * MIT License
 *
 * Copyright (c) 2022 Reversion Developers
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nukkitx.nbt.*;
import lombok.*;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * Map blocks between upstream and downstream
 */

@Getter
public class BlockMapper {
    public static final BlockMapper DEFAULT = BlockMapper.builder().build();

    // RuntimeID Maps between Downstream and Upstream
    private final Map<Integer, Integer> runtimeIdToUpstreamMap = new HashMap<>();
    private final Map<Integer, Integer> runtimeIdToDownstreamMap = new HashMap<>();

    // Palette Suppliers
    private final Supplier<InputStream> upstreamPaletteSupplier;
    private final Supplier<InputStream> downstreamPaletteSupplier;

    // Block Mapper Supplier - Only needed if we have no runtimeMapper
    private final Supplier<InputStream> blockMapperSupplier;

    // Runtime Mapper Supplier
    private final Supplier<InputStream> runtimeBlockMapperSupplier;

    // Debug - When set we will output a mapping to bake in as the RuntimeId Map
    private final String debugName;
    private final boolean debug;

    // Linked BlockMappers
    private final List<BlockMapper> preMapper = new ArrayList<>();
    private final List<BlockMapper> postMapper = new ArrayList<>();

    // Are we initialized?
    boolean initialized;

    // Our Palette
    private List<NbtMap> upstreamPalette = new ArrayList<>();

    @Builder
    public BlockMapper(Supplier<InputStream> upstreamPalette, Supplier<InputStream> downstreamPalette, Supplier<InputStream> blockMapper,
                       Supplier<InputStream> runtimeMapper, String debugName, Boolean debug) {
        this.upstreamPaletteSupplier = upstreamPalette;
        this.downstreamPaletteSupplier = downstreamPalette;
        this.blockMapperSupplier = blockMapper;
        this.runtimeBlockMapperSupplier = runtimeMapper;
        this.debugName = debugName;
        this.debug = debug != null;

        init();
    }

    public void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        if (upstreamPaletteSupplier != null) {
            upstreamPalette = loadPalette(upstreamPaletteSupplier.get());

            // Initialize Runtime Mapper
            if (runtimeBlockMapperSupplier != null) {
                initRuntimeIdMapFromMapper(runtimeBlockMapperSupplier.get());
            } else {
                // Generate runtime ID from 2 palettes
                if (downstreamPaletteSupplier != null) {
                    BlockMapperConfig[] blockMapper;
                    if (blockMapperSupplier != null) {
                        blockMapper = loadBlockMapper(blockMapperSupplier.get());
                    } else {
                        blockMapper = new BlockMapperConfig[0];
                    }

                    List<NbtMap> downstreamTags = loadPalette(downstreamPaletteSupplier.get());
                    initRuntimeIdMapFromPalette(upstreamPalette, downstreamTags, blockMapper);
                }

                if (debug && debugName != null) {
                    // TODO Remove this to its own tool. For now it writes files to bake in stuff for speed and memory
                    debugDump(debugName);
                }
            }
        }
    }

    protected BlockMapperConfig[] loadBlockMapper(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        BlockMapperConfig[] mapConfigs;
        try {
            mapConfigs = mapper.readValue(stream, BlockMapperConfig[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load BlockMapper file", e);
        }
        return mapConfigs;
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
        return runtimeId < upstreamPalette.size() ? upstreamPalette.get(runtimeId) : null;//palette.get(runtimeId);
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

    public void initRuntimeIdMap(List<NbtMap> downstreamTags) {
        BlockMapperConfig[] blockMapper;
        if (blockMapperSupplier != null) {
            blockMapper = loadBlockMapper(blockMapperSupplier.get());
        } else {
            blockMapper = new BlockMapperConfig[0];
        }
        initRuntimeIdMapFromPalette(upstreamPalette, downstreamTags, blockMapper);
    }

    /**
     * Initialize the runtime map using 2 palettes
     * <p>
     * This is an expensive method so use the baked in map instead!
     *
     * @param upstreamTags   tags from upstream palette
     * @param downstreamTags tags from downstream palette
     */
    protected void initRuntimeIdMapFromPalette(List<NbtMap> upstreamTags, List<NbtMap> downstreamTags, BlockMapperConfig[] blockMapper) {
        if (debug) {
            System.out.println("Initializing BlockMapper from Palette for " + getDebugName());
        }

        // Preparation
        Map<Integer, NbtMap> upstreamMap = new HashMap<>();
        Map<String, List<Integer>> upstreamNameMap = new HashMap<>();
        Map<String, Short> legacyMap = new HashMap<>();
        Map<String, List<BlockMapperConfig>> blockMapperMap = new HashMap<>();
        int version = 0;

        for (int i = 0; i < upstreamTags.size(); i++) {
            NbtMap tag = upstreamTags.get(i);
            upstreamMap.put(i, tag);

            NbtMap block = tag.getCompound("block");
            legacyMap.put(block.getString("name"), tag.getShort("id"));
            if (version == 0) {
                version = block.getInt("version");
            }

            if (!upstreamNameMap.containsKey(block.getString("name"))) {
                upstreamNameMap.put(block.getString("name"), new ArrayList<>());
            }
            upstreamNameMap.get(block.getString("name")).add(i);
        }

        for (BlockMapperConfig config : blockMapper) {
            if (!blockMapperMap.containsKey(config.getDownstream().getName())) {
                blockMapperMap.put(config.getDownstream().getName(), new ArrayList<>());
            }
            blockMapperMap.get(config.getDownstream().getName()).add(config);
        }

        Map<Integer, NbtMap> unusedMap = new HashMap<>(upstreamMap);
        for (int downstreamRuntimeId = 0; downstreamRuntimeId < downstreamTags.size(); downstreamRuntimeId++) {
            NbtMap downstreamTag = downstreamTags.get(downstreamRuntimeId);
            NbtMap downstreamBlockTag = downstreamTag.getCompound("block");

            // Do we have a blockMap for this?
            if (blockMapperMap.containsKey(downstreamBlockTag.getString("name"))) {
                outer:
                for (BlockMapperConfig config : blockMapperMap.get(downstreamBlockTag.getString("name"))) {
                    VariableStore vs = new VariableStore();
                    if (config.getDownstream().getStates().size() > 0) {
                        NbtMap originalStates = downstreamBlockTag.getCompound("states");
                        if (originalStates == null) {
                            continue;
                        }
                        for (Map.Entry<String, VariableStore.Data> entry : config.getDownstream().getStates().entrySet()) {
                            if (!originalStates.containsKey(entry.getKey())) {
                                continue outer;
                            }

                            if (!vs.compare(originalStates.get(entry.getKey()), entry.getValue())) {
                                continue outer;
                            }
                        }
                    }

                    // Found a match
                    NbtMapBuilder builder = NbtMap.builder();
                    for (Map.Entry<String, VariableStore.Data> entry : config.getUpstream().getStates().entrySet()) {
                        builder.put(entry.getKey(), vs.get(entry.getValue()));
                    }

                    downstreamBlockTag = downstreamBlockTag.toBuilder()
                            .putString("name", config.getUpstream().getName())
                            .putCompound("states", builder.build())
                            .putInt("version", version)
                            .build();

                    downstreamTag = downstreamTag.toBuilder()
                            .putCompound("block", downstreamBlockTag)
                            .putShort("id", legacyMap.getOrDefault(config.getUpstream().getName(), (short) 0))
                            .build();

                    break;
                }
            }
            boolean found = false;
            for (int upstreamRuntimeId : upstreamNameMap.getOrDefault(downstreamBlockTag.getString("name"), Collections.emptyList())) {
                NbtMap upstreamTag = upstreamMap.get(upstreamRuntimeId).getCompound("block");
                if (downstreamBlockTag.getCompound("states").equals(upstreamTag.getCompound("states"))) {
                    registerRuntimeIdMapping(downstreamRuntimeId, upstreamRuntimeId);
                    unusedMap.remove(upstreamRuntimeId);
                    found = true;
                    break;
                }
            }

            if (!found) {
                if (debug) {
                    System.out.println("Unable to find upstream palette entry: " + downstreamTag);
                }
            }
        }

        if (unusedMap.size() > 0) {
            if (debug) {
                System.out.println("Extra upstream unmatched palette entries: \n" + unusedMap);
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

        File outFile = new File(name + "-runtime_block_mapper.json");
        System.out.println("Writing RuntimeIdMap to: " + outFile);
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
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



    public int mapRuntimeIdToUpstream(int original) {
        int translated = original;
        for (BlockMapper mapper : preMapper) {
            translated = mapper.mapRuntimeIdToUpstream(translated);
        }
        translated = runtimeIdToUpstreamMap.getOrDefault(translated, translated);
        for (BlockMapper mapper : postMapper) {
            translated = mapper.mapRuntimeIdToUpstream(translated);
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
            Map<String, VariableStore.Data> states;
        }

        @ToString
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            String name;
            Map<String, VariableStore.Data> states;
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
