package dev.xclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class ClientHubScreen extends Screen {
    private final ModuleManager modules;
    private final XClientConfig config;
    private final Screen parent;
    public ClientHubScreen(ModuleManager modules, XClientConfig config, Screen parent) {
        super(Text.literal("XClient Settings"));
        this.modules = modules; this.config = config; this.parent = parent;
    }
    @Override protected void init() {
        int x = width / 2 - 100, y = height / 2 - 55;
        addDrawableChild(ButtonWidget.builder(Text.literal("Edit HUD positions"), b ->
                client.setScreen(new HudEditorScreen(config, this))).dimensions(x, y, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Cycle HUD color"), b -> {
            int[] colors = {0xFFFFFF, 0x8CC8FF, 0x8CFFB0, 0xFFD166, 0xFF8CA1};
            int next = 0;
            for (int i = 0; i < colors.length; i++) if (colors[i] == config.hudColor) next = (i + 1) % colors.length;
            config.hudColor = colors[next]; config.save();
        }).dimensions(x, y + 25, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> client.setScreen(parent))
                .dimensions(x, y + 50, 200, 20).build());
    }
    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, "XCLIENT SETTINGS", width / 2, height / 2 - 85, 0xFF8CC8FF);
        context.drawCenteredTextWithShadow(textRenderer, "Choose HUD options", width / 2, height / 2 - 70, 0xFFFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
