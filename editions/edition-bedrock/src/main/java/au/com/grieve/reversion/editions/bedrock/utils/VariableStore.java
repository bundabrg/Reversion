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

package au.com.grieve.reversion.editions.bedrock.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple class to compare values keeping in mind replacement parameters
 */
@Getter
public class VariableStore {
    private final Map<String, Object> variables = new HashMap<>();

    public boolean compare(Object left, JsonNode right) {
        // If right is null then we assume we're not checking it and always match
        if (right == null) {
            return true;
        }

        // For now we check if right is a replacement totally and if so we assume it matches
        // TODO use regex to allow partial matches
        if (right.getNodeType() == JsonNodeType.STRING) {
            String rightString = right.asText();
            if (rightString.startsWith("%")) {
                variables.put(rightString.substring(1, rightString.length() - 1), left);
                return true;
            }
        }

        // Else we try compare the string values
        String leftCompare = String.valueOf(left);
        String rightCompare = null;

        switch (right.getNodeType()) {
            case POJO: // {"type": value}
                switch (right.fieldNames().next()) {
                    case "string":
                        rightCompare = right.get("string").asText();
                        break;
                    case "byte":
                    case "int":
                    case "short":
                        rightCompare = String.valueOf(right.get("short").asInt());
                        break;
                    case "boolean":
                        rightCompare = String.valueOf(right.get("boolean").asBoolean());
                        break;
                }
                break;
            case STRING:
                rightCompare = right.asText();
                break;
            case NUMBER:
                rightCompare = String.valueOf(right.asInt());
                break;
            case BOOLEAN:
                rightCompare = String.valueOf(right.asBoolean());
                break;
        }


        return leftCompare.equals(rightCompare);
    }

    public Object get(JsonNode value) throws IndexOutOfBoundsException {
        // Check for simple substitution
        if (value.getNodeType() == JsonNodeType.STRING) {
            String valueString = value.asText();

            if (valueString.startsWith("%")) {
                String key = valueString.substring(1, valueString.length() - 1);
                if (!variables.containsKey(key)) {
                    throw new IndexOutOfBoundsException("No such variable: " + key);
                }
                return variables.get(key);
            }
        }

        // TODO Allow substitution here
        switch (value.getNodeType()) {
            case OBJECT: // {"type": value}
                switch (value.fieldNames().next()) {
                    case "string":
                        return value.get("string").asText();
                    case "byte":
                        return (byte) value.get("byte").asInt();
                    case "integer":
                    case "int":
                        return value.get("int").asInt();
                    case "short":
                        return (short) value.get("short").asInt();
                    case "boolean":
                        return value.get("boolean").asBoolean();
                }
                break;
            case STRING:
                return value.asText();
            case NUMBER:
                return value.asInt();
            case BOOLEAN:
                return value.asBoolean();
        }

        return null;
    }

}
