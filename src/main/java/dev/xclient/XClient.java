
package dev.xclient;

import dev.xclient.config.ClientConfig;
import dev.xclient.gui.XClientScreen;
import dev.xclient.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class XClient implements ClientModInitializer {
    public static final String MOD_ID = "xclient";

    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        ClientConfig.load();
        ModuleManager.registerDefaults();

        openMenuKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.xclient.open_menu",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_SHIFT,
                        KeyBinding.MISC_CATEGORY
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new XClientScreen());
                } else if (client.currentScreen instanceof XClientScreen) {
                    client.setScreen(null);
                }
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (ModuleManager.isEnabled("fps")) {
                String fpsText = "FPS " + client.getCurrentFps();

                drawContext.drawTextWithShadow(
                        client.textRenderer,
                        fpsText,
                        6,
                        6,
                        0xFFFFFFFF
                );
            }

            if (ModuleManager.isEnabled("coordinates")
                    && client.player != null
                    && client.world != null) {

                String coords = String.format(
                        "XYZ %.1f / %.1f / %.1f",
                        client.player.getX(),
                        client.player.getY(),
                        client.player.getZ()
                );

                drawContext.drawTextWithShadow(
                        client.textRenderer,
                        coords,
                        6,
                        18,
                        0xFFFFFFFF
                );
            }
        });
    }
}
