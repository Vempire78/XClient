# X Client (starter) — Minecraft 1.21.5 / Fabric

A lightweight client-side starter for Java Edition on PojavLauncher. The current milestone includes:
- Right Shift opens the X Client menu.
- Minimal dark menu with purple accent.
- Toggleable module registry.
- FPS counter and optional coordinates HUD.
- Module settings persisted to `config/xclient.properties`.
- Planned module entries for keystrokes, CPS, armor status, potion effects and durability alerts.

## Build
Requires JDK 21 and internet access for Gradle to download dependencies.

1. Open this folder in a Gradle-capable IDE, or install Gradle 8.12.
2. Run `gradle build`.
3. The remapped mod JAR should be in `build/libs/` (choose the JAR without `-dev`).
4. Copy the JAR to the Minecraft instance's `mods` folder with Fabric Loader and Fabric API for Minecraft 1.21.5 installed.

## Important status
This is the first source milestone, not a verified release. It has not been compiled or tested inside PojavLauncher in this environment. Build it and test in a separate instance before using it in your main setup. The currently listed keystrokes, CPS, armor, potion-effects and durability modules are registry placeholders until their render/logic implementations are added. PvP modules are informational/visual utilities, not automated combat.

## Easy build from Android (GitHub Actions)
If you do not want to install Java/Gradle on your phone:
1. Create a GitHub repository and upload this project's files (including the hidden `.github` folder).
2. Open the repository's **Actions** tab.
3. Select **Build X Client** and tap **Run workflow** (or wait for the first push build).
4. When the run finishes successfully, open the run and download the `x-client-build` artifact.
5. In the downloaded artifact, choose the normal `x-client-0.1.0.jar`, not a `-sources.jar` or `-dev.jar` file, if those are present.
6. Install it only in a separate test instance with Fabric Loader and Fabric API for Minecraft 1.21.5.

This automates compilation on GitHub; it does not mean the mod has been tested in PojavLauncher.
