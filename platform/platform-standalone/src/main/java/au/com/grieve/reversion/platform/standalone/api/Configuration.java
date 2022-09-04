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

package au.com.grieve.reversion.platform.standalone.api;

import au.com.grieve.reversion.platform.standalone.Standalone;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Getter
@ToString
@SuppressWarnings("FieldMayBeFinal")
public class Configuration {
    private List<JsonNode> servers;
    private JsonNode client;

        public static Configuration load(Path path) throws IOException {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                return Standalone.YAML_MAPPER.readValue(reader, Configuration.class);
            }
        }

//    @Getter
//    @ToString
//    public static class Listen {
//        private String host = "0.0.0.0";
//        private int port;
//        private String edition;
//
//        public InetSocketAddress getAddress() {
//            return new InetSocketAddress(host, port);
//        }
//    }

//    @Getter
//    @ToString
//    public static class Connect {
//        private String host = "0.0.0.0";
//        private int port;
//
//        public InetSocketAddress getAddress() {
//            return new InetSocketAddress(host, port);
//        }
//    }


}
