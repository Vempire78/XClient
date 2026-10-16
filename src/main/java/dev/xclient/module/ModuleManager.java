package dev.xclient.module;

import dev.xclient.config.ClientConfig;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModuleManager {
    private static final Map<String, ClientModule> MODULES = new LinkedHashMap<>();

    private ModuleManager() {}

    public static void registerDefaults() {
        register(new ClientModule("fps", "FPS Counter", "HUD", true));
        register(new ClientModule("coordinates", "Coordinates", "SMP", false));
        register(new ClientModule("keystrokes", "Keystrokes", "PvP", false));
        register(new ClientModule("cps", "CPS Counter", "PvP", false));
        register(new ClientModule("armor", "Armor Status", "PvP", false));
        register(new ClientModule("potion_effects", "Potion Effects", "SMP", true));
        register(new ClientModule("durability", "Durability Alerts", "SMP", true));
        ClientConfig.applyToModules(MODULES);
    }

    public static void register(ClientModule module) {
        MODULES.put(module.id(), module);
    }

    public static Collection<ClientModule> all() {
        return MODULES.values();
    }

    public static boolean isEnabled(String id) {
        ClientModule module = MODULES.get(id);
        return module != null && module.enabled();
    }

    public static void save() {
        ClientConfig.save(MODULES);
    }
}
