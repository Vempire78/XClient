package dev.xclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private final List<ClientModule> modules = new ArrayList<>();

    public ModuleManager() {
        add("FPS", "HUD", "Displays current frame rate.", true);
        add("Coordinates", "HUD", "Displays current coordinates.", true);
        add("Keystrokes", "HUD", "Keystrokes display (placeholder).", false);
        add("CPS", "PvP", "Clicks-per-second display (placeholder).", false);
        add("Armor Status", "PvP", "Armor durability display (placeholder).", false);
        add("Potion Effects", "PvP", "Potion effects display (placeholder).", false);
        add("Durability", "PvP", "Item durability display (placeholder).", false);
        add("SMP Info", "SMP", "Server information (placeholder).", false);
        add("Toggle Sprint", "PvP", "Sprint helper (placeholder).", false);
    }
    private void add(String n, String c, String d, boolean e) { modules.add(new ClientModule(n, c, d, e)); }
    public List<ClientModule> all() { return Collections.unmodifiableList(modules); }
    public boolean isEnabled(String name) {
        return modules.stream().filter(m -> m.name().equals(name)).findFirst().map(ClientModule::enabled).orElse(false);
    }
    public void load(XClientConfig config) {
        for (ClientModule m : modules)
            m.setEnabled(Boolean.parseBoolean(config.values.getProperty("module." + m.name(), Boolean.toString(m.enabled()))));
    }
    public void save(XClientConfig config) {
        for (ClientModule m : modules) config.values.setProperty("module." + m.name(), Boolean.toString(m.enabled()));
        config.save();
    }
}
