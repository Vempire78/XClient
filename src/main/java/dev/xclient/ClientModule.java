package dev.xclient;

public final class ClientModule {
    private final String name, category, description;
    private boolean enabled;
    public ClientModule(String name, String category, String description, boolean enabled) {
        this.name=name; this.category=category; this.description=description; this.enabled=enabled;
    }
    public String name(){return name;}
    public String category(){return category;}
    public String description(){return description;}
    public boolean enabled(){return enabled;}
    public void setEnabled(boolean value){enabled=value;}
    public void toggle(){enabled=!enabled;}
}
