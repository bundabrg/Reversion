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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityDataMap;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple Entity Translator
 */
@Getter
public class EntityMapper {
    private final Map<String, MapConfig> mapList = new HashMap<>();
    private final Map<Long, MapConfig> runtime = new HashMap<>();

    public EntityMapper(InputStream mapStream) throws MapperException {
        loadMapper(mapStream);
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
            this.mapList.put(mapConfig.getDownstream().getIdentifier(), mapConfig);
        }
    }

    public Entity addEntity(Entity original) {
        if (original == null) {
            return null;
        }

        MapConfig mapConfig = mapList.get(original.getIdentifier());
        if (mapConfig == null) {
            return original;
        }

        // Save runtime entity
        runtime.put(original.getId(), mapConfig);

        Entity translated = toUpstream(original);

        System.err.println("Translated: " + translated);

        return translated;
    }

    public Entity toUpstream(Entity original) {
        if (original == null) {
            return null;
        }

        MapConfig mapConfig = runtime.get(original.getId());
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

    public void removeEntity(Entity original) {
        runtime.remove(original.getId());
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
