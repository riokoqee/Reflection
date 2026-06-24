# Reflection LibGDX Backend

This module is the first migration step from Swing/Java2D to LibGDX.

## Run

```powershell
mvn -f libgdx/pom.xml exec:java
```

The current prototype renders the real project maps through LibGDX:

- `WASD` or arrow keys: move
- `Shift`: sprint
- `1`: apartment
- `2`: Forest of Doubts
- `3`: village
- `4`: mountain
- `5`: library

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
