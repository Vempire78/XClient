package dev.xclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class XClientConfig {
    final Properties values = new Properties();
    private final Path file = FabricLoader.getInstance().getConfigDir().resolve("xclient.properties");
    public int fpsX = 8, fpsY = 8, coordsX = 8, coordsY = 22, hudColor = 0xFFFFFF;

    public void load() {
        if (Files.exists(file)) {
            try (InputStream in = Files.newInputStream(file)) { values.load(in); }
            catch (IOException ignored) { }
        }
        fpsX = integer("hud.fps.x", 8); fpsY = integer("hud.fps.y", 8);
        coordsX = integer("hud.coords.x", 8); coordsY = integer("hud.coords.y", 22);
        hudColor = integer("hud.color", 0xFFFFFF);
    }
    private int integer(String key, int fallback) {
        try { return Integer.parseInt(values.getProperty(key, Integer.toString(fallback))); }
        catch (NumberFormatException ignored) { return fallback; }
    }
    public void save() {
        values.setProperty("hud.fps.x", Integer.toString(fpsX));
        values.setProperty("hud.fps.y", Integer.toString(fpsY));
        values.setProperty("hud.coords.x", Integer.toString(coordsX));
        values.setProperty("hud.coords.y", Integer.toString(coordsY));
        values.setProperty("hud.color", Integer.toString(hudColor));
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) { values.store(out, "XClient configuration"); }
        } catch (IOException ignored) { }
    }
}
