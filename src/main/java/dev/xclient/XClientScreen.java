package dev.xclient;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class XClientScreen extends Screen {
    private final ModuleManager modules;
    private final XClientConfig config;
    private String category = "All";
    private TextFieldWidget search;
    private int scroll = 0;

    public XClientScreen(ModuleManager modules, XClientConfig config) {
        super(Text.literal("XClient"));
        this.modules = modules; this.config = config;
    }

    @Override protected void init() {
        int left = Math.max(18, width / 2 - 170), top = Math.max(18, height / 2 - 125);
        search = new TextFieldWidget(textRenderer, left + 12, top + 38, 205, 20, Text.literal("Search modules"));
        search.setPlaceholder(Text.literal("Search modules..."));
        addDrawableChild(search);

        String[] cats = {"All", "PvP", "HUD", "SMP"};
        for (int i = 0; i < cats.length; i++) {
            String selected = cats[i];
            addDrawableChild(ButtonWidget.builder(Text.literal(selected), b -> {
                category = selected; scroll = 0; clearAndInit();
            }).dimensions(left + 12 + i * 56, top + 64, 52, 20).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("X Settings"), b ->
                client.setScreen(new ClientHubScreen(modules, config, this)))
                .dimensions(left + 12, top + 91, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Edit HUD"), b ->
                client.setScreen(new HudEditorScreen(config, this)))
                .dimensions(left + 116, top + 91, 101, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(left + 157, top + 222, 60, 20).build());

        List<ClientModule> visible = visibleModules();
        int rowY = top + 118, index = 0;
        for (ClientModule m : visible) {
            if (index++ < scroll) continue;
            if (rowY > top + 210) break;
            ClientModule module = m;
            addDrawableChild(ButtonWidget.builder(Text.literal(module.name() + (module.enabled() ? "  [ON]" : "  [OFF]")), b -> {
                module.toggle(); modules.save(config); clearAndInit();
            }).dimensions(left + 12, rowY, 205, 20).build());
            rowY += 23;
        }
    }

    private List<ClientModule> visibleModules() {
        String q = search == null ? "" : search.getText().toLowerCase(Locale.ROOT).trim();
        return modules.all().stream()
                .filter(m -> category.equals("All") || m.category().equals(category))
                .filter(m -> q.isEmpty() || m.name().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int left = Math.max(18, width / 2 - 170), top = Math.max(18, height / 2 - 125);
        context.fill(left, top, left + 235, top + 252, 0xEE11151D);
        context.fill(left, top, left + 235, top + 27, 0xFF242B3A);
        context.drawText(textRenderer, "XCLIENT", left + 12, top + 9, 0xFF8CC8FF, true);
        context.drawText(textRenderer, "Right Shift • Client Settings", left + 84, top + 9, 0xFFFFFFFF, true);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll = Math.max(0, Math.min(visibleModules().size(), scroll + (verticalAmount < 0 ? 1 : -1)));
        clearAndInit(); return true;
    }
    @Override public void close() { modules.save(config); config.save(); super.close(); }
}
