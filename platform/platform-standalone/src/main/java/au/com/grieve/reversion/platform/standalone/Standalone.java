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

package au.com.grieve.reversion.platform.standalone;

import au.com.grieve.reversion.platform.standalone.api.Configuration;
import au.com.grieve.reversion.platform.standalone.api.Edition;
import au.com.grieve.reversion.platform.standalone.api.Server;
import au.com.grieve.reversion.platform.standalone.editions.bedrock.BedrockEdition;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Log4j2
public class Standalone {
    public static final YAMLMapper YAML_MAPPER = (YAMLMapper) new YAMLMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Map<String, Edition> registeredEditions = new HashMap<>();
    private final List<Server> servers = new ArrayList<>();
//    private final Set<RegisteredTranslator> registeredTranslators = Collections.emptySet();

    //    private final List<ReversionServer> servers = Collections.emptyList();
//    private final Set<BedrockClient> clients = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Configuration configuration;

    public static void main(String[] args) {
        Standalone standalone = new Standalone();
        try {
            standalone.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {

        // Configuration
        log.info("Loading configuration...");

        Path configPath = Paths.get(".").resolve("config.yml");
        if (Files.notExists(configPath) || !Files.isRegularFile(configPath)) {
            //noinspection ConstantConditions
            Files.copy(Standalone.class.getClassLoader().getResourceAsStream("config.yml"), configPath, StandardCopyOption.REPLACE_EXISTING);
        }

        configuration = Configuration.load(configPath);

        // Built-in Editions
        log.info("Registering editions...");
        registerEdition("bedrock", new BedrockEdition());
//        //        registerEdition("education", new EducationEdition(this));
//
//        // Built-in Translators
//        for (RegisteredTranslator translator : Build.TRANSLATORS) {
//            registerTranslator(translator);
//        }
//
//
//        // Load Listeners
//        for (Configuration.Listen listen : configuration.getListen()) {
//
//        }

        // Create Client
        Edition clientEdition = registeredEditions.get(configuration.getClient().get("edition").asText());
        Client client = clientEdition.createClient(configuration.getClient());

        for (JsonNode listen : configuration.getServers()) {
            Edition edition = registeredEditions.get((listen.get("edition").asText()));
            servers.add(edition.createServer(listen, client));
        }

        // Start all servers
        for (Server server : servers) {
            server.start();
        }

//        BedrockReversionServer server = new BedrockReversionServer(configuration.getListen().get(0).getAddress());
//        server.setTargetVersion(Bedrock_v1_19_20_22.VERSION);
//        server.addSourceVersion(Bedrock_v1_19_20_22.VERSION);
//        server.setHandler(new BedrockEventHandler());
//        server.bind().join();
        loop();
    }

    private void loop() {
        while (running.get()) {
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                // ignore
            }

        }

        // Shutdown
        for (Server server : servers) {
            server.stop();
        }
//        this.clients.forEach(BedrockClient::close);
//        this.bedrockServer.close();
    }

    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            synchronized (this) {
                this.notify();
            }
        }
    }

    /**
     * Register an Edition
     *
     * @param name    Name of edition
     * @param edition Edition to register
     */
    public void registerEdition(String name, Edition edition) {
        registeredEditions.put(name, edition);
    }

    /**
     * Register a Translator
     *
     * @param translator Translator to register
     */
//    public void registerTranslator(RegisteredTranslator translator) {
//        registeredTranslators.add(translator);
//    }

}
