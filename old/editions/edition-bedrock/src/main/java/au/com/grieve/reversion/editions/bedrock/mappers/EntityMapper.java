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

import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityDataMap;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Simple Entity Translator
 */
@Getter
public class EntityMapper {
    public static final EntityMapper DEFAULT = EntityMapper.builder().build();

    private final Map<String, MapConfig> mapList = new HashMap<>();
    private final Supplier<InputStream> entityMapper;
    // Are we initialized
    private boolean initialized;

    @Builder
    public EntityMapper(Supplier<InputStream> entityMapper) {
        this.entityMapper = entityMapper;

        init();
    }

    protected void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        if (entityMapper != null) {
            loadMapper(entityMapper.get());
        }
    }


    public void loadMapper(InputStream stream) {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        MapConfig[] mapConfigs;
        try {
            mapConfigs = mapper.readValue(stream, MapConfig[].class);
        } catch (IOException e) {
            throw new RuntimeException("Unable load EntityMapper file", e);
        }

        for (MapConfig mapConfig : mapConfigs) {
            this.mapList.put(mapConfig.getDownstream().getIdentifier(), mapConfig);
        }
    }

    public Entity addEntityToUpstream(BedrockTranslator translator, Entity original) {
        if (original == null) {
            return null;
        }

        MapConfig mapConfig = mapList.get(original.getIdentifier());
        if (mapConfig == null) {
            return original;
        }

        // Save runtime entity
        translator.getRuntimeEntityMap().put(original.getId(), mapConfig);

        return mapEntitytoUpstream(translator, original);
    }

    public Entity mapEntitytoUpstream(BedrockTranslator translator, Entity original) {
        if (original == null) {
            return null;
        }

        MapConfig mapConfig = translator.getRuntimeEntityMap().get(original.getId());
        if (mapConfig == null) {
            return original;
        }

        Entity translated = new Entity();
        translated.setIdentifier(mapConfig.getUpstream().getIdentifier());
        translated.getEntityData().putAll(original.getEntityData());

        // Update scale
        if (mapConfig.getUpstream().getScale() != null) {
            translated.getEntityData().put(EntityData.SCALE,
                    translated.getEntityData().getFloat(EntityData.SCALE, 1.0f) * mapConfig.getUpstream().getScale());
        }

        // Set Name
        if (mapConfig.getUpstream().getName() != null) {
            translated.getEntityData().putString(EntityData.NAMETAG, mapConfig.getUpstream().getName());
            translated.getEntityData().getFlags().setFlag(EntityFlag.ALWAYS_SHOW_NAME, true);
        }

        // Add Metadata
        if (mapConfig.getUpstream().getMetadata() != null) {
            for (Map.Entry<String, JsonNode> entry : mapConfig.getUpstream().getMetadata().entrySet()) {
                Object value = null;
                switch (EntityData.valueOf(entry.getKey()).getType()) {
                    case INT:
                        value = entry.getValue().asInt();
                        break;
                    case FLOAT:
                        value = (float) entry.getValue().asDouble();
                        break;
                    case STRING:
                        value = entry.getValue().asText();
                        break;
                }

                translated.getEntityData().put(EntityData.valueOf(entry.getKey()), value);

            }
        }

        return translated;
    }

    public void removeEntityToUpstream(BedrockTranslator translator, Entity original) {
        translator.getRuntimeEntityMap().remove(original.getId());
    }

    @ToString
    @Data
    public static class Entity {
        long id;
        String identifier;
        EntityDataMap entityData = new EntityDataMap();
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
            String identifier;
            String name;
            Float scale;
            Map<String, JsonNode> metadata;
        }

        @ToString
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Downstream {
            String identifier;
        }

    }


}
