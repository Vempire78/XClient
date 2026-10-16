package dev.xclient.gui;

import dev.xclient.module.ClientModule;
import dev.xclient.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class XClientScreen extends Screen {
    private static final int PANEL_WIDTH = 300;
    private static final int ROW_HEIGHT = 25;
    private int panelLeft;
    private int panelTop;
    private final List<Row> rows = new ArrayList<>();

    public XClientScreen() {
        super(Text.literal("X Client"));
    }

    @Override
    protected void init() {
        panelLeft = (width - PANEL_WIDTH) / 2;
        panelTop = Math.max(24, (height - 260) / 2);
        rows.clear();
        int y = panelTop + 55;
        for (ClientModule module : ModuleManager.all()) {
            rows.add(new Row(module, y));
            y += ROW_HEIGHT;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int panelHeight = 62 + rows.size() * ROW_HEIGHT + 12;
        context.fill(panelLeft, panelTop, panelLeft + PANEL_WIDTH, panelTop + panelHeight, 0xEE111318);
        context.fill(panelLeft, panelTop, panelLeft + PANEL_WIDTH, panelTop + 2, 0xFF8B5CF6);
        context.drawTextWithShadow(textRenderer, "X CLIENT", panelLeft + 14, panelTop + 12, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "PvP  /  SMP  /  SETTINGS", panelLeft + 14, panelTop + 29, 0xFFB8BBC7);

        for (Row row : rows) {
            boolean hovered = mouseX >= panelLeft + 10 && mouseX <= panelLeft + PANEL_WIDTH - 10
                    && mouseY >= row.y && mouseY < row.y + ROW_HEIGHT - 2;
            int left = panelLeft + 10;
            int right = panelLeft + PANEL_WIDTH - 10;
            context.fill(left, row.y, right, row.y + ROW_HEIGHT - 2,
                    hovered ? 0xFF292D38 : 0xFF1C1F27);
            context.drawTextWithShadow(textRenderer, row.module.name(), left + 8, row.y + 7, 0xFFFFFFFF);
            String state = row.module.enabled() ? "ON" : "OFF";
            int color = row.module.enabled() ? 0xFFB9F6CA : 0xFF9CA3AF;
            context.drawTextWithShadow(textRenderer, state, right - 30, row.y + 7, color);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (Row row : rows) {
                if (mouseX >= panelLeft + 10 && mouseX <= panelLeft + PANEL_WIDTH - 10
                        && mouseY >= row.y && mouseY < row.y + ROW_HEIGHT - 2) {
                    row.module.toggle();
                    ModuleManager.save();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private record Row(ClientModule module, int y) {}
}
