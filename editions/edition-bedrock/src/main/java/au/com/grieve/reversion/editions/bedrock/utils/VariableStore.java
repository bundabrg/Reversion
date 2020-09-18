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
