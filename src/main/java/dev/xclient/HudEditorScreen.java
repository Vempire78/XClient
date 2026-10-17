package dev.xclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class HudEditorScreen extends Screen {
    private final XClientConfig config;
    private final Screen parent;
    private String dragging = "";
    public HudEditorScreen(XClientConfig config, Screen parent) {
        super(Text.literal("XClient HUD Editor")); this.config = config; this.parent = parent;
    }
    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, height, 0x66000000);
        context.drawCenteredTextWithShadow(textRenderer, "HUD EDITOR • Drag labels to move them", width / 2, 12, 0xFFFFFFFF);
        context.drawText(textRenderer, "FPS: 144", config.fpsX, config.fpsY, config.hudColor, true);
        context.drawText(textRenderer, "XYZ: 120 64 -35", config.coordsX, config.coordsY, config.hudColor, true);
        context.drawCenteredTextWithShadow(textRenderer, "Click and drag a label. ESC to return.", width / 2, height - 25, 0xFFCCCCCC);
        super.render(context, mouseX, mouseY, delta);
    }
    @Override public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            if (near(mx, my, config.fpsX, config.fpsY, 65, 12)) dragging = "fps";
            else if (near(mx, my, config.coordsX, config.coordsY, 125, 12)) dragging = "coords";
        }
        return super.mouseClicked(mx, my, button) || !dragging.isEmpty();
    }
    private boolean near(double mx, double my, int x, int y, int w, int h) {
        return mx >= x - 3 && mx <= x + w && my >= y - 3 && my <= y + h;
    }
    @Override public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (button == 0 && !dragging.isEmpty()) {
            int x = Math.max(0, Math.min(width - 30, (int) mx));
            int y = Math.max(0, Math.min(height - 10, (int) my));
            if (dragging.equals("fps")) { config.fpsX = x; config.fpsY = y; }
            else { config.coordsX = x; config.coordsY = y; }
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }
    @Override public boolean mouseReleased(double mx, double my, int button) {
        dragging = ""; config.save(); return super.mouseReleased(mx, my, button);
    }
    @Override public void close() { config.save(); client.setScreen(parent); }
}
