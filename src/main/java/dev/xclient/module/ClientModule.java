package dev.xclient.module;

public final class ClientModule {
    private final String id;
    private final String name;
    private final String category;
    private boolean enabled;

    public ClientModule(String id, String name, String category, boolean enabled) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.enabled = enabled;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String category() { return category; }
    public boolean enabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void toggle() { enabled = !enabled; }
}
