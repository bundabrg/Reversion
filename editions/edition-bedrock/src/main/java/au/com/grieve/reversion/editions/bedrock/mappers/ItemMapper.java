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
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemMapper {
    public static final ItemMapper DEFAULT = new ItemMapper();

    private final Map<Integer, List<MapConfig>> toUpstreamMap = new HashMap<>();
    private final Map<Integer, List<MapConfig>> toDownstreamMap = new HashMap<>();

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
            if (!toUpstreamMap.containsKey(mapConfig.getDownstream().getId())) {
                toUpstreamMap.put(mapConfig.getDownstream().getId(), new ArrayList<>());
            }

            this.toUpstreamMap.get(mapConfig.getDownstream().getId()).add(mapConfig);

            if (!toDownstreamMap.containsKey(mapConfig.getUpstream().getId())) {
                toDownstreamMap.put(mapConfig.getUpstream().getId(), new ArrayList<>());
            }

            this.toDownstreamMap.get(mapConfig.getUpstream().getId()).add(mapConfig);
        }
    }

    public ItemData mapItemDataToDownstream(BedrockTranslator translator, ItemData item) {
        if (item == null) {
            return null;
        }

        for (MapConfig mapConfig : toDownstreamMap.getOrDefault(item.getId(), new ArrayList<>())) {
            VariableStore variableStore = new VariableStore();

            if (!variableStore.compare(item.getDamage(), mapConfig.getUpstream().getData())) {
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
            return ItemData.fromNet(item.getNetId(), mapConfig.getDownstream().getId(), (short) variableStore.get(mapConfig.getUpstream().getData()), item.getCount(), tag, item.getCanPlace(), item.getCanBreak(), item.getBlockingTicks());
        }
        return item;
    }

    public ItemData mapItemDataToUpstream(BedrockTranslator translator, ItemData item) {
        if (item == null) {
            return null;
        }

        ItemData translated = item;

        // Translate Enchantments
        if (item.getTag() != null && item.getTag().containsKey("ench")) {
//            NbtMapBuilder tagBuilder = item.getTag().toBuilder();
//            tagBuilder.remove("ench");
//            NbtMap tag = tagBuilder.build();

            NbtMap tag = item.getTag().toBuilder()
                    .putList("ench", NbtType.COMPOUND, translator.getEnchantmentMapper().mapEnchantmentNbtToUpstream(item.getTag().getList("ench", NbtType.COMPOUND)))
                    .build();
            translated = ItemData.fromNet(item.getNetId(), item.getId(), item.getDamage(), item.getCount(), tag, item.getCanPlace(), item.getCanBreak(), item.getBlockingTicks());
        }

        for (MapConfig mapConfig : toUpstreamMap.getOrDefault(translated.getId(), new ArrayList<>())) {
            VariableStore variableStore = new VariableStore();

            if (!variableStore.compare(translated.getDamage(), mapConfig.getDownstream().getData())) {
                continue;
            }

            NbtMap tag = translated.getTag();

            if (mapConfig.getUpstream().getName() != null) {
                NbtMapBuilder tagBuilder = tag != null ? tag.toBuilder() : NbtMap.builder();

                tagBuilder.putCompound("display", NbtMap.builder().putString("Name", mapConfig.getUpstream().getName()).build());
                tag = tagBuilder.build();
            }
            return ItemData.fromNet(translated.getNetId(), mapConfig.getUpstream().getId(),
                    (short) variableStore.get(mapConfig.getUpstream().getData()), translated.getCount(),
                    tag, translated.getCanPlace(), translated.getCanBreak(), translated.getBlockingTicks());
        }
        return translated;
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


}
