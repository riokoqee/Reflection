# Reflection LibGDX Backend

This module is the first migration step from Swing/Java2D to LibGDX.

## Run

```powershell
mvn -f libgdx/pom.xml exec:java
```

The current prototype renders the real project maps and the first gameplay scene layer through LibGDX:

- `WASD` or arrow keys: move
- `Shift`: sprint
- `E` or `Enter`: interact / advance dialogue
- `Space`: advance dialogue
- `1`: apartment
- `2`: Forest of Doubts
- `3`: village
- `4`: mountain
- `5`: library

Implemented in the LibGDX backend so far:

- map loading from the existing `res/maps/*.txt` files
- tile rendering from the existing `res/tiles` assets
- player animation from the existing `res/player/new` sprite sheets
- static objects, trees, decorations, houses, and NPC sprites
- depth sorting between player, objects, and NPCs
- tile, object, tree, house, and NPC collision
- interaction prompts and a basic dialogue overlay
- simple object states for TV, dresser, dirty dishes, and lantern pickup
- prototype location transitions through apartment door, forest shadow, village library door, and library exit

## Build

```powershell
mvn -f libgdx/pom.xml package
```

The shaded desktop jar is created in:

```text
libgdx/target/reflection-libgdx-0.1.0-desktop.jar
```

## Migration Plan

1. Keep the existing Java2D build working while the LibGDX backend grows.
2. Move rendering first: tiles, objects, player, NPCs, lighting, UI.
3. Move input and audio to LibGDX after rendering is stable.
4. Keep story, saves, localization, and PDF logic shared for as long as possible.
5. Replace the old Swing launcher only when the LibGDX backend reaches feature parity.

The target is a GPU-rendered desktop build with vsync and a 60 FPS foreground cap.
