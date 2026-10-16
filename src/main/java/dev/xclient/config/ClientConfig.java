package dev.xclient.config;

import dev.xclient.module.ClientModule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

public final class ClientConfig {
    private static final Path FILE = Path.of("config", "xclient.properties");
    private static final Properties VALUES = new Properties();

    private ClientConfig() {}

    public static void load() {
        try {
            Files.createDirectories(FILE.getParent());
            if (Files.exists(FILE)) {
                try (var reader = Files.newBufferedReader(FILE)) {
                    VALUES.load(reader);
                }
            }
        } catch (IOException e) {
            System.err.println("[X Client] Could not load config: " + e.getMessage());
        }
    }

    public static void applyToModules(Map<String, ClientModule> modules) {
        for (ClientModule module : modules.values()) {
            String value = VALUES.getProperty("module." + module.id());
            if (value != null) module.setEnabled(Boolean.parseBoolean(value));
        }
    }

    public static void save(Map<String, ClientModule> modules) {
        for (ClientModule module : modules.values()) {
            VALUES.setProperty("module." + module.id(), Boolean.toString(module.enabled()));
        }
        try {
            Files.createDirectories(FILE.getParent());
            try (var writer = Files.newBufferedWriter(FILE)) {
                VALUES.store(writer, "X Client settings");
            }
        } catch (IOException e) {
            System.err.println("[X Client] Could not save config: " + e.getMessage());
        }
    }
}
