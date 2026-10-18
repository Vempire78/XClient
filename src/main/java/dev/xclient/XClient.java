
package dev.xclient;

import java.util.Locale;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

public final class XClient implements ClientModInitializer {

    private static final ModuleManager MODULES = new ModuleManager();
    private static final XClientConfig CONFIG = new XClientConfig();

    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        CONFIG.load();
        MODULES.load(CONFIG);

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
                    client.setScreen(
                        new XClientScreen(MODULES, CONFIG)
                    );
                }
            }
        });

        HudRenderCallback.EVENT.register(
            (context, tickCounter) -> renderHud(context)
        );
    }

    private void renderHud(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null
                || mc.world == null
                || mc.options.hudHidden) {
            return;
        }

        int color = 0xFF000000 | (CONFIG.hudColor & 0xFFFFFF);

        if (MODULES.isEnabled("FPS")) {
            drawText(
                context,
                "FPS: " + mc.getCurrentFps(),
                CONFIG.fpsX,
                CONFIG.fpsY,
                color
            );
        }

        if (MODULES.isEnabled("Coordinates")) {
            String coordinates = String.format(
                Locale.ROOT,
                "XYZ: %.1f %.1f %.1f",
                mc.player.getX(),
                mc.player.getY(),
                mc.player.getZ()
            );

            drawText(
                context,
                coordinates,
                CONFIG.coordsX,
                CONFIG.coordsY,
                color
            );
        }

        if (MODULES.isEnabled("Armor Status")) {
            renderArmor(context, mc, color);
        }

        if (MODULES.isEnabled("Potion Effects")) {
            renderPotions(context, mc, color);
        }

        if (MODULES.isEnabled("Durability")) {
            renderDurability(context, mc, color);
        }

        if (MODULES.isEnabled("Keystrokes")) {
            renderKeys(context, mc, color);
        }

        if (MODULES.isEnabled("SMP Info")) {
            renderInfo(context, mc, color);
        }
    }

    private void drawText(
            DrawContext context,
            String message,
            int x,
            int y,
            int color) {

        MinecraftClient mc = MinecraftClient.getInstance();

        context.drawText(
            mc.textRenderer,
            Text.literal(message),
            x,
            y,
            color,
            true
        );
    }

    private void renderArmor(
            DrawContext context,
            MinecraftClient mc,
            int color) {

        int x = CONFIG.armorX;
        int y = CONFIG.armorY;

        EquipmentSlot[] slots = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
        };

        for (EquipmentSlot slot : slots) {
            ItemStack stack = mc.player.getEquippedStack(slot);

            context.drawItem(stack, x, y - 2);

            String name = stack.isEmpty()
                ? "Empty"
                : stack.getName().getString();

            String durability = "";

            if (!stack.isEmpty() && stack.isDamageable()) {
                int remaining =
                    stack.getMaxDamage() - stack.getDamage();

                durability = " " + remaining
                    + "/" + stack.getMaxDamage();
            }

            drawText(
                context,
                name + durability,
                x + 20,
                y,
                color
            );

            y += 18;
        }
    }

    private void renderPotions(
            DrawContext context,
            MinecraftClient mc,
            int color) {

        int y = CONFIG.potionY;

        for (StatusEffectInstance effect
                : mc.player.getStatusEffects()) {

            String name = effect.getEffectType()
                .value()
                .getName()
                .getString();

            int durationTicks = effect.getDuration();

            String timer;

            if (durationTicks < 0) {
                timer = "∞";
            } else {
                int seconds = durationTicks / 20;

                timer = String.format(
                    Locale.ROOT,
                    "%d:%02d",
                    seconds / 60,
                    seconds % 60
                );
            }

            String level = effect.getAmplifier() > 0
                ? " " + (effect.getAmplifier() + 1)
                : "";

            String line = name + level + "  " + timer;

            if ("JoZo".equalsIgnoreCase(CONFIG.potionPreset)) {
                line = "[" + timer + "] " + name + level;
            } else if ("Lunar".equalsIgnoreCase(CONFIG.potionPreset)) {
                line = name + level + "  " + timer;
            } else if ("Feather".equalsIgnoreCase(CONFIG.potionPreset)) {
                line = "◆ " + name + level + "  " + timer;
            }

            drawText(
                context,
                line,
                CONFIG.potionX,
                y,
                color
            );

            y += 10;
        }
    }

    private void renderDurability(
            DrawContext context,
            MinecraftClient mc,
            int color) {

        ItemStack stack = mc.player.getMainHandStack();

        if (stack.isEmpty()) {
            return;
        }

        String line = stack.getName().getString();

        if (stack.isDamageable()) {
            int remaining =
                stack.getMaxDamage() - stack.getDamage();

            line += " " + remaining
                + "/" + stack.getMaxDamage();
        }

        drawText(
            context,
            line,
            CONFIG.durabilityX,
            CONFIG.durabilityY,
            color
        );
    }

    private void renderKeys(
            DrawContext context,
            MinecraftClient mc,
            int color) {

        int x = CONFIG.keysX;
        int y = CONFIG.keysY;

        String w = keyLabel(
            mc.options.forwardKey.isPressed(), "W"
        );

        String a = keyLabel(
            mc.options.leftKey.isPressed(), "A"
        );

        String s = keyLabel(
            mc.options.backKey.isPressed(), "S"
        );

        String d = keyLabel(
            mc.options.rightKey.isPressed(), "D"
        );

        drawText(context, "   [" + w + "]", x, y, color);

        drawText(
            context,
            "[" + a + "] [" + s + "] [" + d + "]",
            x,
            y + 12,
            color
        );

        drawText(
            context,
            "Jump: "
                + keyLabel(mc.options.jumpKey.isPressed(), "ON")
                + "  Sneak: "
                + keyLabel(mc.options.sneakKey.isPressed(), "ON"),
            x,
            y + 24,
            color
        );
    }

    private String keyLabel(boolean pressed, String label) {
        return pressed ? "§a" + label + "§r" : label;
    }

    private void renderInfo(
            DrawContext context,
            MinecraftClient mc,
            int color) {

        String server = "Singleplayer";

        if (mc.getCurrentServerEntry() != null) {
            server = mc.getCurrentServerEntry().address;
        }

        drawText(
            context,
            "Server: " + server,
            CONFIG.infoX,
            CONFIG.infoY,
            color
        );

        if (mc.getNetworkHandler() != null) {
            var entry = mc.getNetworkHandler()
                .getPlayerListEntry(mc.player.getUuid());

            if (entry != null) {
                drawText(
                    context,
                    "Ping: " + entry.getLatency() + " ms",
                    CONFIG.infoX,
                    CONFIG.infoY + 12,
                    color
                );
            }
        }
    }

    public static XClientConfig config() {
        return CONFIG;
    }
                }
                
