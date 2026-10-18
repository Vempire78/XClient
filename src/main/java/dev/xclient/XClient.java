
package dev.xclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class XClient implements ClientModInitializer {
    private static final ModuleManager MODULES = new ModuleManager();
    private static final XClientConfig CONFIG = new XClientConfig();

    private static KeyBinding openMenuKey;
    private static int leftCps, rightCps, tick;
    private static final long[] leftClicks = new long[32];
    private static final long[] rightClicks = new long[32];
    private static int leftIndex, rightIndex;

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
                    client.setScreen(new XClientScreen(MODULES, CONFIG));
                }
            }

            tick++;
            if (tick % 10 == 0) {
                updateCps();
            }
        });

        HudRenderCallback.EVENT.register((ctx, counter) -> renderHud(ctx));
    }

    private static void updateCps() {
        long now = System.currentTimeMillis();
        leftCps = count(leftClicks, now);
        rightCps = count(rightClicks, now);
    }

    private static int count(long[] clicks, long now) {
        int total = 0;

        for (long time : clicks) {
            if (time != 0 && now - time < 1000) {
                total++;
            }
        }

        return total;
    }

    public static void registerClick(boolean left) {
        long now = System.currentTimeMillis();

        if (left) {
            leftClicks[leftIndex++ % leftClicks.length] = now;
        } else {
            rightClicks[rightIndex++ % rightClicks.length] = now;
        }
    }

    private void renderHud(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null || mc.options.hudHidden) {
            return;
        }

        int color = 0xFF000000 | (CONFIG.hudColor & 0xFFFFFF);

        if (MODULES.isEnabled("FPS")) {
            text(context, "FPS: " + mc.getCurrentFps(),
                CONFIG.fpsX, CONFIG.fpsY, color);
        }

        if (MODULES.isEnabled("Coordinates")) {
            text(context,
                String.format("XYZ: %.1f %.1f %.1f",
                    mc.player.getX(),
                    mc.player.getY(),
                    mc.player.getZ()),
                CONFIG.coordsX, CONFIG.coordsY, color);
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

    private void text(DrawContext context, String message,
                      int x, int y, int color) {
        context.drawText(
            MinecraftClient.getInstance().textRenderer,
            Text.literal(message),
            x, y, color, true
        );
    }

    private void renderArmor(DrawContext context,
                             MinecraftClient mc, int color) {
        int y = CONFIG.armorY;
        int x = CONFIG.armorX;

        // Minecraft 1.21.5: read armor from player inventory.
        for (ItemStack stack : mc.player.getInventory().armor) {
            context.drawItem(stack, x, y - 2);

            String name = stack.isEmpty()
                ? "Empty"
                : stack.getName().getString();

            String durability = "";

            if (!stack.isEmpty() && stack.isDamageable()) {
                durability = " "
                    + (stack.getMaxDamage() - stack.getDamage())
                    + "/" + stack.getMaxDamage();
            }

            text(context, name + durability, x + 20, y, color);
            y += 18;
        }
    }

    private void renderPotions(DrawContext context,
                               MinecraftClient mc, int color) {
        int y = CONFIG.potionY;

        for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
            String name = effect.getEffectType()
                .value().getName().getString();

            int seconds = effect.getDuration() / 20;
            String timer = String.format(
                "%d:%02d", seconds / 60, seconds % 60
            );

            String level = effect.getAmplifier() > 0
                ? " " + (effect.getAmplifier() + 1)
                : "";

            String line = name + level + "  " + timer;

            if ("JoZo".equals(CONFIG.potionPreset)) {
                line = "[" + timer + "] " + name + level;
            } else if ("Lunar".equals(CONFIG.potionPreset)) {
                line = name + level + "  " + timer;
            } else if ("Feather".equals(CONFIG.potionPreset)) {
                line = "◆ " + name + level + "  " + timer;
            }

            text(context, line, CONFIG.potionX, y, color);
            y += 10;
        }
    }

    private void renderDurability(DrawContext context,
                                  MinecraftClient mc, int color) {
        ItemStack stack = mc.player.getMainHandStack();

        if (!stack.isEmpty()) {
            String line = stack.getName().getString();

            if (stack.isDamageable()) {
                line += " "
                    + (stack.getMaxDamage() - stack.getDamage())
                    + "/" + stack.getMaxDamage();
            }

            text(context, line,
                CONFIG.durabilityX, CONFIG.durabilityY, color);
        }
    }

    private void renderKeys(DrawContext context,
                            MinecraftClient mc, int color) {
        int x = CONFIG.keysX;
        int y = CONFIG.keysY;

        String w = key(mc.options.forwardKey.isPressed(), "W");
        String a = key(mc.options.leftKey.isPressed(), "A");
        String s = key(mc.options.backKey.isPressed(), "S");
        String d = key(mc.options.rightKey.isPressed(), "D");

        text(context, "   [" + w + "]", x, y, color);
        text(context, "[" + a + "] [" + s + "] [" + d + "]",
            x, y + 12, color);

        text(context,
            "Jump: " + key(mc.options.jumpKey.isPressed(), "ON")
                + "  Sneak: "
                + key(mc.options.sneakKey.isPressed(), "ON"),
            x, y + 24, color);

        text(context,
            "LMB CPS: " + leftCps + "  RMB CPS: " + rightCps,
            x, y + 36, color);
    }

    private String key(boolean pressed, String label) {
        return pressed ? "§a" + label + "§r" : label;
    }

    private void renderInfo(DrawContext context,
                            MinecraftClient mc, int color) {
        String server = mc.getCurrentServerEntry() == null
            ? "Singleplayer"
            : mc.getCurrentServerEntry().address;

        text(context, "Server: " + server,
            CONFIG.infoX, CONFIG.infoY, color);

        if (mc.getNetworkHandler() != null) {
            var entry = mc.getNetworkHandler()
                .getPlayerListEntry(mc.player.getUuid());

            if (entry != null) {
                text(context, "Ping: " + entry.getLatency() + " ms",
                    CONFIG.infoX, CONFIG.infoY + 12, color);
            }
        }
    }

    public static XClientConfig config() {
        return CONFIG;
    }
    }
                 
