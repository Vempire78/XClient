# XClient Advanced (Minecraft 1.21.5 Fabric)

Client-side HUD starter with configurable toggles, draggable HUD editor, color cycling, and active HUD rendering for FPS, coordinates, armor status, potion effects, durability, keystrokes, and basic SMP/session information.

## Build
Use Java 21 and run `gradle build --no-daemon`. The JAR will be in `build/libs/`.

## Controls
- Right Shift: open XClient menu
- HUD Editor: drag the displayed HUD modules
- Settings: cycle HUD color and choose potion layout preset

## Notes
This project is designed for Fabric 1.21.5. The four potion presets are original approximations inspired by common PvP client layouts, not exact copies of proprietary client assets. SMP Info displays local session information available through the client; ping may be unavailable in singleplayer.
