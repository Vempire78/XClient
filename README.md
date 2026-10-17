# XClient

Lightweight client-side Fabric mod for Minecraft 1.21.5.

## Included
- Right Shift opens the module screen.
- All / PvP / HUD / SMP categories and module search.
- Toggle module entries and save their states.
- X Settings hub with HUD color cycling.
- HUD editor: drag FPS and coordinates labels.
- HUD positions, color, and module states persist in `config/xclient.properties`.

Important: this is a starter implementation, not a complete Lunar Client clone. Keystrokes, CPS, armor status, potion effects, durability, SMP info and Toggle Sprint are currently UI placeholders, not fully implemented features.

## Build
Java 21 required. Run `gradle build --no-daemon` or use the included GitHub Actions workflow.
