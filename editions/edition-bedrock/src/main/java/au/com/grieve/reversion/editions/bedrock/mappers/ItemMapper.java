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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Simple Item Translator
 */
@Getter
public class ItemMapper {
    public static final ItemMapper DEFAULT = ItemMapper.builder().build();

    // Item Maps
    private final Map<Integer, List<RuntimeItemMapperEntry>> itemToUpstreamMap = new HashMap<>();
    private final Map<Integer, List<RuntimeItemMapperEntry>> itemToDownstreamMap = new HashMap<>();

    // Enchantment Map
    private final Map<Short, List<EnchantmentMapperEntry>> enchantmentMap = new HashMap<>();

    // Upstream Palette
    private final Supplier<InputStream> upstreamPaletteSupplier;

    // Downstream Palette - Only needed if we don't have a runtimeSupplier
    private final Supplier<InputStream> downstreamPaletteSupplier;

    // Mappers
    private final Supplier<InputStream> runtimeItemMapperSupplier;
    private final Supplier<InputStream> enchantmentMapper;

    // ItemMapper - Only needed if we don't have a runtimeItemMapper
    private final Supplier<InputStream> itemMapperSupplier;


    // Debug - When set we will output a mapping to bake in as the RuntimeId Map
    private final String debugName;
    private final boolean debug;

    // Our Palette
    private List<NbtMap> upstreamPalette = new ArrayList<>();

    // Is this initialized
    private boolean initialized;

    @Builder
    public ItemMapper(Supplier<InputStream> palette, Supplier<InputStream> downstreamPalette, Supplier<InputStream> itemMapper,
                      Supplier<InputStream> enchantmentMapper, Supplier<InputStream> runtimeMapper, String debugName, Boolean debug) {
        this.upstreamPaletteSupplier = palette;
        this.downstreamPaletteSupplier = downstreamPalette;

        this.runtimeItemMapperSupplier = runtimeMapper;
        this.enchantmentMapper = enchantmentMapper;
        this.itemMapperSupplier = itemMapper;

        this.debugName = debugName;
        this.debug = debug != null;

        init();
    }

    protected void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        if (enchantmentMapper != null) {
            initEnchantmentMapper(enchantmentMapper.get());
        }


        if (upstreamPaletteSupplier != null) {

            upstreamPalette = loadPalette(upstreamPaletteSupplier.get());

            // Initialize RuntimeMapper if available
            if (runtimeItemMapperSupplier != null) {
                initItemMapperFromRuntimeFile(runtimeItemMapperSupplier.get());
            } else {
                // Try generate map from 2 palettes and an itemMapper
                if (downstreamPaletteSupplier != null) {
                    List<NbtMap> downstreamTags = loadPalette(downstreamPaletteSupplier.get());
                    ItemMapperEntry[] itemMapper;
                    if (itemMapperSupplier != null) {
                        itemMapper = loadItemMapper(itemMapperSupplier.get());
                    } else {
                        itemMapper = new ItemMapperEntry[0];
                    }

                    initItemMapperFromPalette(upstreamPalette, downstreamTags, itemMapper);
                }

                if (debug && debugName != null) {
                    // TODO Remove this to its own tool. For now it writes files to bake in stuff for speed and memory
                    debugDump(debugName);
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

        // Dump existing runtime id map to a json file suitable for the itemMapper option
        ObjectMapper mapper = new ObjectMapper(new JsonFactory())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        File outFile = new File(name + "-runtime_item_mapper.json");
        System.out.println("Writing ItemMapper to: " + outFile);

        List<RuntimeItemMapperEntry> valid = new ArrayList<>();

        for (Map.Entry<Integer, List<RuntimeItemMapperEntry>> entry : itemToUpstreamMap.entrySet()) {
            valid.addAll(entry.getValue());
        }

        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            mapper.writeValue(fos, valid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected NbtList<NbtMap> loadPalette(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        ItemPaletteEntry[] itemPalette;
        try {
            itemPalette = mapper.readValue(stream, ItemPaletteEntry[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load ItemPalette file", e);
        }

        List<NbtMap> ret = new ArrayList<>();
        for (ItemPaletteEntry item : itemPalette) {
            ret.add(NbtMap.builder()
                    .putString("name", item.getName())
                    .putInt("id", item.getId())
                    .build()
            );
        }

        return new NbtList<>(NbtType.COMPOUND, ret);
    }

    /**
     * Load ItemMapper from the ItemMapper stream
     *
     * @param stream stream providing the ItemMapper
     */
    public ItemMapperEntry[] loadItemMapper(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        ItemMapperEntry[] itemMapperEntries;
        try {
            itemMapperEntries = mapper.readValue(stream, ItemMapperEntry[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load ItemMapper file", e);
        }

        return itemMapperEntries;
    }

    public void initEnchantmentMapper(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        EnchantmentMapperEntry[] mapConfigs;
        try {
            mapConfigs = mapper.readValue(stream, EnchantmentMapperEntry[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load EnchantmentMapper file", e);
        }

        for (EnchantmentMapperEntry mapConfig : mapConfigs) {
            if (!enchantmentMap.containsKey(mapConfig.getDownstream().getId())) {
                enchantmentMap.put(mapConfig.getDownstream().getId(), new ArrayList<>());
            }

            enchantmentMap.get(mapConfig.getDownstream().getId()).add(mapConfig);
        }
    }

    /**
     * Register an item mapping from a downstream runtime id to an upstream runtime id
     */
    public void registerRuntimeItemMapping(RuntimeItemMapperEntry entry) {
        if (!itemToUpstreamMap.containsKey(entry.getDownstream().getId())) {
            itemToUpstreamMap.put(entry.getDownstream().getId(), new ArrayList<>());
        }

        itemToUpstreamMap.get(entry.getDownstream().getId()).add(entry);

        if (!itemToDownstreamMap.containsKey(entry.getUpstream().getId())) {
            itemToDownstreamMap.put(entry.getUpstream().getId(), new ArrayList<>());
        }

        itemToDownstreamMap.get(entry.getUpstream().getId()).add(entry);
    }

    protected void initItemMapperFromRuntimeFile(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        RuntimeItemMapperEntry[] itemMapperEntries;
        try {
            itemMapperEntries = mapper.readValue(stream, RuntimeItemMapperEntry[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load ItemMapper file", e);
        }

        for (RuntimeItemMapperEntry entry : itemMapperEntries) {
            registerRuntimeItemMapping(entry);
        }
    }


    protected void initItemMapperFromPalette(List<NbtMap> upstreamTags, List<NbtMap> downstreamTags, ItemMapperEntry[] itemMapper) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        if (debug) {
            System.out.println("Initializing ItemMapper from Palette for " + getDebugName());
        }

        // Generate a searchable itemMapper
        Map<String, List<ItemMapperEntry>> itemMapperMap = new HashMap<>();
        for (ItemMapperEntry entry : itemMapper) {
            if (!itemMapperMap.containsKey(entry.getDownstream().getName())) {
                itemMapperMap.put(entry.getDownstream().getName(), new ArrayList<>());
            }

            itemMapperMap.get(entry.getDownstream().getName()).add(entry);
        }

        // Generate a searchable upstreamTags
        Map<String, NbtMap> upstreamTagsMap = upstreamTags.stream()
                .collect(Collectors.toMap(u -> u.getString("name"), u -> u));

        // Generate a searchable upstreamTags
        Map<String, NbtMap> downstreamTagsMap = downstreamTags.stream()
                .collect(Collectors.toMap(u -> u.getString("name"), u -> u));

        // List of unmatched upstream tags
        List<NbtMap> unusedDownstreamTags = new ArrayList<>(downstreamTags);
        List<NbtMap> unusedUpstreamTags = new ArrayList<>(upstreamTags);

        // Resolve all item mappers first
        for (ItemMapperEntry entry : itemMapper) {
            if (!downstreamTagsMap.containsKey(entry.getDownstream().getName())) {
                if (debug) {
                    System.out.printf("[%s]: Unable to find downstream item called %s for ItemMapperEntry\n  %s\n",
                            getDebugName(), entry.getDownstream().getName(), entry);
                }
                continue;
            }

            NbtMap downstreamTag = downstreamTagsMap.get(entry.getDownstream().getName());

            if (!upstreamTagsMap.containsKey(entry.getUpstream().getName())) {
                if (debug) {
                    System.out.printf("[%s]: Unable to find upstream item called %s for ItemMapperEntry\n  %s\n",
                            getDebugName(), entry.getUpstream().getName(), entry);
                }
                continue;
            }

            NbtMap upstreamTag = upstreamTagsMap.get(entry.getUpstream().getName());

            registerRuntimeItemMapping(RuntimeItemMapperEntry.builder()
                    .downstream(RuntimeItemMapperEntry.Downstream.builder()
                            .id(downstreamTag.getInt("id"))
                            .data(entry.getDownstream().getData())
                            .build()
                    )
                    .upstream(RuntimeItemMapperEntry.Upstream.builder()
                            .id(upstreamTag.getInt("id"))
                            .data(entry.getUpstream().getData())
                            .creative(entry.getUpstream().isCreative())
                            .customName(entry.getUpstream().getCustomName())
                            .build()
                    )
                    .build()
            );

            unusedDownstreamTags.remove(downstreamTag);
            unusedUpstreamTags.remove(upstreamTag);
        }

        // Now go through the downstream palette and generate generic mappings of any that don't match an existing mapper
        for (NbtMap downstreamTag : downstreamTags) {

            if (itemToUpstreamMap.containsKey(downstreamTag.getInt("id"))) {
                continue;
            }

            // Look for a match in the upstream palette
            for (NbtMap upstreamTag : upstreamTags) {
                if (downstreamTag.getString("name").equals(upstreamTag.getString("name"))) {
                    if (downstreamTag.getInt("id") != upstreamTag.getInt("id")) {
                        // Create a mapping
                        registerRuntimeItemMapping(RuntimeItemMapperEntry.builder()
                                .downstream(RuntimeItemMapperEntry.Downstream.builder()
                                        .id(downstreamTag.getInt("id"))
                                        .build()
                                )
                                .upstream(RuntimeItemMapperEntry.Upstream.builder()
                                        .id(upstreamTag.getInt("id"))
                                        .creative(true)
                                        .build()
                                )
                                .build()
                        );
                    }
                    unusedDownstreamTags.remove(downstreamTag);
                    unusedUpstreamTags.remove(upstreamTag);
                    break;
                }
            }

//            if (!found) {
//                if (debug) {
//                    System.out.println("Unable to find matching upstream palette entry: " + downstreamTag);
//                }
//            }
        }


        if (unusedDownstreamTags.size() > 0) {
            if (debug) {
                System.out.println("[" + getDebugName() + "]: Unused Downstream Item Palette Entries\n" +
                        unusedDownstreamTags.stream()
                                .map(NbtMap::toString)
                                .collect(Collectors.joining("\n"))
                );
            }
        }

        if (unusedUpstreamTags.size() > 0) {
            if (debug) {
                System.out.println("[" + getDebugName() + "]: Unused Upstream Item Palette Entries\n" +
                        unusedUpstreamTags.stream()
                                .map(NbtMap::toString)
                                .collect(Collectors.joining("\n"))
                );
            }
        }
    }

    public ItemData mapItemDataToDownstream(ItemData item) {
        if (item == null) {
            return null;
        }

        for (RuntimeItemMapperEntry itemMapperEntry : itemToDownstreamMap.getOrDefault(item.getId(), new ArrayList<>())) {
            VariableStore variableStore = new VariableStore();

            if (!variableStore.compare(item.getDamage(), itemMapperEntry.getUpstream().getData())) {
                continue;
            }

            NbtMap tag = item.getTag(); //enchantmentMapper.mapEnchantmentNbtToUpstream(item.getTag());
            // TODO
//            if (mapConfig.getDownstream().getName() != null) {
//                NbtMapBuilder tagBuilder = tag != null ? tag.toBuilder() : NbtMap.builder();
//
//                tagBuilder.putCompound("display", NbtMap.builder().putString("Name", mapConfig.getUpstream().getName()).build());
//                tag = tagBuilder.build();
//            }
            return ItemData.fromNet(item.getNetId(), itemMapperEntry.getDownstream().getId(),
                    variableStore.getInt(itemMapperEntry.getDownstream().getData(), 0).shortValue(), item.getCount(),
                    tag, item.getCanPlace(), item.getCanBreak(), item.getBlockingTicks());
        }
        return item;
    }

    public ItemData mapItemDataToUpstream(ItemData item) {
        return mapItemDataToUpstream(item, false);
    }

    public ItemData mapItemDataToUpstream(ItemData item, boolean creativeOnly) {
        if (item == null) {
            return null;
        }

        ItemData translated = item;

        // Translate Enchantments
        if (item.getTag() != null && item.getTag().containsKey("ench")) {
            NbtMap tag = item.getTag().toBuilder()
                    .putList("ench", NbtType.COMPOUND, mapEnchantmentNbtToUpstream(item.getTag().getList("ench", NbtType.COMPOUND)))
                    .build();
            translated = ItemData.fromNet(item.getNetId(), item.getId(), item.getDamage(), item.getCount(), tag, item.getCanPlace(), item.getCanBreak(), item.getBlockingTicks());
        }

        for (RuntimeItemMapperEntry itemMapperEntry : itemToUpstreamMap.getOrDefault(translated.getId(), new ArrayList<>())) {
            VariableStore variableStore = new VariableStore();

            if (!variableStore.compare(translated.getDamage(), itemMapperEntry.getDownstream().getData())) {
                continue;
            }

            if (creativeOnly && !itemMapperEntry.getUpstream().isCreative()) {
                return null;
            }

            NbtMap tag = translated.getTag();

            if (itemMapperEntry.getUpstream().getCustomName() != null) {
                NbtMapBuilder tagBuilder = tag != null ? tag.toBuilder() : NbtMap.builder();

                tagBuilder.putCompound("display", NbtMap.builder().putString("Name", itemMapperEntry.getUpstream().getCustomName()).build());
                tag = tagBuilder.build();
            }

            return ItemData.fromNet(translated.getNetId(), itemMapperEntry.getUpstream().getId(),
                    variableStore.getInt(itemMapperEntry.getUpstream().getData(), 0).shortValue(), translated.getCount(),
                    tag, translated.getCanPlace(), translated.getCanBreak(), translated.getBlockingTicks());
        }

        return ItemData.fromNet(translated.getNetId(), translated.getId(),
                translated.getDamage(), translated.getCount(),
                translated.getTag(), translated.getCanPlace(), translated.getCanBreak(), translated.getBlockingTicks());
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
        if (original == null) {
            return null;
        }

        for (EnchantmentMapperEntry mapConfig : enchantmentMap.getOrDefault(original.getShort("id"), new ArrayList<>())) {
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


    /**
     * Provides an entry for the ItemMapper
     */
    @ToString
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemMapperEntry {
        Upstream upstream;
        Downstream downstream;

        @ToString
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Upstream {
            String name;
            VariableStore.Data data;
            boolean creative = true;
            String customName;
        }

        @ToString
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            String name;
            VariableStore.Data data;
        }
    }

    /**
     * Provides an entry for the RuntimeItemMapper
     */
    @Builder
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RuntimeItemMapperEntry {
        Upstream upstream;
        Downstream downstream;

        @Builder
        @ToString
        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Upstream {
            int id;
            VariableStore.Data data;
            boolean creative = true;
            String customName;
        }

        @Builder
        @ToString
        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            int id;
            VariableStore.Data data;
        }
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EnchantmentMapperEntry {
        Upstream upstream;
        Downstream downstream;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Upstream {
            short id;
            VariableStore.Data lvl;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            short id;
            VariableStore.Data lvl;
        }
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemPaletteEntry {
        String name;
        int id;
    }

}
