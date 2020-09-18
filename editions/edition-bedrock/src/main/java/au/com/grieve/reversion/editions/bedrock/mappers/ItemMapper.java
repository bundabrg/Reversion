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
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import lombok.Builder;
import lombok.Getter;

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
    private final Map<Integer, List<ItemConfig>> itemToUpstreamMap = new HashMap<>();
    private final Map<Integer, List<ItemConfig>> itemToDownstreamMap = new HashMap<>();

    // Enchantment Map
    private final Map<Short, List<EnchantmentConfig>> enchantmentMap = new HashMap<>();

    // Item Palette Map
    private final Map<Short, Short> itemIdToUpstreamMap = new HashMap<>();
    private final Supplier<InputStream> itemMapper;
    private final Supplier<InputStream> enchantmentMapper;
    private final Supplier<InputStream> itemIdMapper;
    // Is this initialized
    private boolean initialized;

    @Builder
    public ItemMapper(Supplier<InputStream> itemMapper, Supplier<InputStream> enchantmentMapper, Supplier<InputStream> itemRuntimeMapper) {
        this.itemMapper = itemMapper;
        this.enchantmentMapper = enchantmentMapper;
        this.itemIdMapper = itemRuntimeMapper;

        init();
    }

    protected void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        if (itemMapper != null) {
            initItemMapper(itemMapper.get());
        }

        if (enchantmentMapper != null) {
            initEnchantmentMapper(enchantmentMapper.get());
        }

        if (itemIdMapper != null) {
            initItemIdMapper(itemIdMapper.get());
        }
    }


    public void initItemMapper(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        ItemConfig[] itemConfigs;
        try {
            itemConfigs = mapper.readValue(stream, ItemConfig[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load ItemMapper file", e);
        }

        for (ItemConfig itemConfig : itemConfigs) {
            if (!itemToUpstreamMap.containsKey(itemConfig.getDownstream().getId())) {
                itemToUpstreamMap.put(itemConfig.getDownstream().getId(), new ArrayList<>());
            }

            this.itemToUpstreamMap.get(itemConfig.getDownstream().getId()).add(itemConfig);

            if (!itemToDownstreamMap.containsKey(itemConfig.getUpstream().getId())) {
                itemToDownstreamMap.put(itemConfig.getUpstream().getId(), new ArrayList<>());
            }

            this.itemToDownstreamMap.get(itemConfig.getUpstream().getId()).add(itemConfig);
        }
    }

    public void initEnchantmentMapper(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        EnchantmentConfig[] mapConfigs;
        try {
            mapConfigs = mapper.readValue(stream, EnchantmentConfig[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load EnchantmentMapper file", e);
        }

        for (EnchantmentConfig mapConfig : mapConfigs) {
            if (!enchantmentMap.containsKey(mapConfig.getDownstream().getId())) {
                enchantmentMap.put(mapConfig.getDownstream().getId(), new ArrayList<>());
            }

            enchantmentMap.get(mapConfig.getDownstream().getId()).add(mapConfig);
        }
    }

    public void initItemIdMapper(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        ItemPaletteConfig[] mapConfigs;
        try {
            mapConfigs = mapper.readValue(stream, ItemPaletteConfig[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load ItemIdMapper file", e);
        }

        for (ItemPaletteConfig mapConfig : mapConfigs) {
            itemIdToUpstreamMap.put(mapConfig.getDownstream().getId(), mapConfig.getUpstream().getId());
        }
    }

    public ItemData mapItemDataToDownstream(ItemData item) {
        if (item == null) {
            return null;
        }

        for (ItemConfig itemConfig : itemToDownstreamMap.getOrDefault(item.getId(), new ArrayList<>())) {
            VariableStore variableStore = new VariableStore();

            if (!variableStore.compare(item.getDamage(), itemConfig.getUpstream().getData())) {
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
            return ItemData.fromNet(item.getNetId(), itemConfig.getDownstream().getId(), (short) variableStore.get(itemConfig.getUpstream().getData()), item.getCount(), tag, item.getCanPlace(), item.getCanBreak(), item.getBlockingTicks());
        }
        return item;
    }

    public ItemData mapItemDataToUpstream(ItemData item) {
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

        for (ItemConfig itemConfig : itemToUpstreamMap.getOrDefault(translated.getId(), new ArrayList<>())) {
            VariableStore variableStore = new VariableStore();

            if (!variableStore.compare(translated.getDamage(), itemConfig.getDownstream().getData())) {
                continue;
            }

            NbtMap tag = translated.getTag();

            if (itemConfig.getUpstream().getName() != null) {
                NbtMapBuilder tagBuilder = tag != null ? tag.toBuilder() : NbtMap.builder();

                tagBuilder.putCompound("display", NbtMap.builder().putString("Name", itemConfig.getUpstream().getName()).build());
                tag = tagBuilder.build();
            }

            return ItemData.fromNet(translated.getNetId(), mapItemIdToUpstream((short) itemConfig.getUpstream().getId()),
                    (short) variableStore.get(itemConfig.getUpstream().getData()), translated.getCount(),
                    tag, translated.getCanPlace(), translated.getCanBreak(), translated.getBlockingTicks());
        }

        return ItemData.fromNet(translated.getNetId(), mapItemIdToUpstream((short) translated.getId()),
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

        for (EnchantmentConfig mapConfig : enchantmentMap.getOrDefault(original.getShort("id"), new ArrayList<>())) {
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

    public short mapItemIdToUpstream(short original) {
        return itemIdToUpstreamMap.getOrDefault(original, original);
    }


    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemConfig {
        Upstream upstream;
        Downstream downstream;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Upstream {
            int id;
            JsonNode data;
            String name;
            boolean creative = true;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            int id;
            JsonNode data;
        }
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EnchantmentConfig {
        Upstream upstream;
        Downstream downstream;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Upstream {
            short id;
            JsonNode lvl;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            short id;
            JsonNode lvl;
        }
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemPaletteConfig {
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
