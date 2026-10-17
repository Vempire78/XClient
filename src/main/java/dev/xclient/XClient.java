package dev.xclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class XClient implements ClientModInitializer {
    private static final ModuleManager MODULES = new ModuleManager();
    private static final XClientConfig CONFIG = new XClientConfig();
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        CONFIG.load();
        MODULES.load(CONFIG);
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.xclient.open_menu", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, KeyBinding.MISC_CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new XClientScreen(MODULES, CONFIG));
            }
        });
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> renderHud(drawContext));
    }

    private void renderHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;
        if (MODULES.isEnabled("FPS")) {
            context.drawText(client.textRenderer, "FPS: " + client.getCurrentFps(),
                    CONFIG.fpsX, CONFIG.fpsY, CONFIG.hudColor, true);
        }
        if (MODULES.isEnabled("Coordinates")) {
            String coords = String.format("XYZ: %d %d %d",
                    (int) client.player.getX(), (int) client.player.getY(), (int) client.player.getZ());
            context.drawText(client.textRenderer, coords, CONFIG.coordsX, CONFIG.coordsY, CONFIG.hudColor, true);
        }
    }
}
