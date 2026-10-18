package dev.xclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private final List<ClientModule> modules = new ArrayList<>();
    public ModuleManager() {
        add("FPS","HUD","Current frames per second.",true);
        add("Coordinates","HUD","Player XYZ coordinates.",true);
        add("Armor Status","HUD","Equipped armor and durability.",false);
        add("Potion Effects","HUD","Active effects and timers.",false);
        add("Durability","HUD","Held item durability.",false);
        add("Keystrokes","HUD","Movement keys and mouse buttons.",false);
        add("SMP Info","SMP","Server and session information.",false);
    }
    private void add(String n,String c,String d,boolean e){modules.add(new ClientModule(n,c,d,e));}
    public List<ClientModule> all(){return Collections.unmodifiableList(modules);}
    public boolean isEnabled(String name){return modules.stream().anyMatch(m->m.name().equals(name)&&m.enabled());}
    public void load(XClientConfig config){
        for(ClientModule m:modules)m.setEnabled(Boolean.parseBoolean(config.values.getProperty("module."+m.name(),Boolean.toString(m.enabled()))));
    }
    public void save(XClientConfig config){
        for(ClientModule m:modules)config.values.setProperty("module."+m.name(),Boolean.toString(m.enabled()));
        config.save();
    }
}
