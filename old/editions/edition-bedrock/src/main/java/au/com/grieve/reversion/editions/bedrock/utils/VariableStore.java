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

package au.com.grieve.reversion.editions.bedrock.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple class to compare values keeping in mind replacement parameters
 */
@Getter
public class VariableStore {
    private final Map<String, Object> variables = new HashMap<>();

    public boolean compare(Object left, Data right) {
        // If right is null then we assume we're not checking it and always match
        if (right == null) {
            return true;
        }

        // For now we check if right is a replacement totally and if so we assume it matches
        // TODO use regex to allow partial matches
        String rightString = right.asString();
        if (rightString.startsWith("%")) {
            variables.put(rightString.substring(1, rightString.length() - 1), left);
            return true;
        }

        // Else we try compare the string values
        String leftString = String.valueOf(left);
        return leftString.equals(rightString);
    }

    public Object getOrDefault(Data value, Object defaultValue) {
        Object ret = get(value);
        return ret != null ? ret : defaultValue;
    }

    public Integer getInt(Data value) {
        return getInt(value, null);
    }

    public Integer getInt(Data value, Integer defaultValue) {
        return (Integer) getOrDefault(value, defaultValue);
    }

    public Object get(Data value) throws IndexOutOfBoundsException {
        if (value == null) {
            return null;
        }

        // Check for simple substitution
        String valueString = value.asString();
        if (valueString.startsWith("%")) {
            String key = valueString.substring(1, valueString.length() - 1);
            if (!variables.containsKey(key)) {
                throw new IndexOutOfBoundsException("No such variable: " + key);
            }
            return variables.get(key);
        }

        return value.getData();
    }

    /**
     * Jackson Data node
     */
    @JsonDeserialize(using = Data.Deserializer.class)
    @JsonSerialize(using = Data.Serializer.class)
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    public static class Data {
        private Object data;

        public String asString() {
            return String.valueOf(data);
        }

        public static class Serializer extends StdSerializer<Data> {

            public Serializer() {
                this(null);
            }

            protected Serializer(Class<Data> t) {
                super(t);
            }

            @Override
            public void serialize(Data data, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeObject(data.getData());
            }
        }

        public static class Deserializer extends StdDeserializer<Data> {

            public Deserializer() {
                this(null);
            }

            protected Deserializer(Class<?> vc) {
                super(vc);
            }

            @Override
            public Data deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException, JsonProcessingException {
                JsonNode node = jsonParser.getCodec().readTree(jsonParser);
                Object data = null;
                switch (node.getNodeType()) {
//                    case OBJECT: // {"type": value}
//                        switch (node.fieldNames().next()) {
//                            case "string":
//                                data = node.get("string").asText();
//                                break;
//                            case "byte":
//                                data = (byte) node.get("byte").asInt();
//                                break;
//                            case "integer":
//                            case "int":
//                                data = node.get("int").asInt();
//                                break;
//                            case "short":
//                                data = (short) node.get("short").asInt();
//                                break;
//                            case "boolean":
//                                data = node.get("boolean").asBoolean();
//                                break;
//                        }
//                        break;
                    case STRING:
                        data = node.asText();
                        break;
                    case NUMBER:
                        data = node.asInt();
                        break;
                    case BOOLEAN:
                        data = node.asBoolean();
                        break;
                }

                return new Data(data);
            }
        }

    }

}
