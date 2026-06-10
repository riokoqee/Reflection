package main;

final class FootstepAudio {

    private static final int VILLAGE_STONE_ROAD_FIRST_TILE = 54;
    private static final int VILLAGE_STONE_ROAD_LAST_TILE = 56;
    private static final int[] SOUND_INDICES = {
            Sound.FOOTSTEPS_WOOD,
            Sound.FOOTSTEPS_DIRT,
            Sound.FOOTSTEPS_STONE,
            Sound.FOOTSTEPS_STONE_SPRINT
    };

    private final GamePanel gp;
    private final Sound[] sounds = new Sound[SOUND_INDICES.length];
    private final boolean[] loaded = new boolean[SOUND_INDICES.length];
    private final boolean[] unavailable = new boolean[SOUND_INDICES.length];
    private int activeSoundSlot = -1;

    FootstepAudio(GamePanel gp) {
        this.gp = gp;
        for (int i = 0; i < sounds.length; i++) {
            sounds[i] = new Sound();
        }
    }

    void preload() {
        for (int i = 0; i < SOUND_INDICES.length; i++) {
            if (loaded[i] || unavailable[i]) {
                continue;
            }
            sounds[i].volumeScale = gp.footstepVolumeScale;
            if (sounds[i].setFile(SOUND_INDICES[i])) {
                loaded[i] = true;
            }
            else {
                unavailable[i] = true;
            }
        }
    }

    void syncVolume() {
        for (Sound sound : sounds) {
            if (sound != null) {
                sound.volumeScale = gp.footstepVolumeScale;
            }
        }
    }

    void update() {
        if (!isPlayerTryingToMove()) {
            stop();
            return;
        }

        int soundIndex = getSoundIndex();
        int soundSlot = getSoundSlot(soundIndex);
        if (soundSlot == -1 || unavailable[soundSlot]) {
            stop();
            return;
        }

        Sound sound = sounds[soundSlot];
        if (!loaded[soundSlot]) {
            sound.volumeScale = gp.footstepVolumeScale;
            if (!sound.setFile(soundIndex)) {
                unavailable[soundSlot] = true;
                stop();
                return;
            }
            loaded[soundSlot] = true;
        }

        if (activeSoundSlot != soundSlot) {
            stopActiveSound();
            activeSoundSlot = soundSlot;
        }

        sound.setVolumeDb(adjustedVolume(gp.footstepVolumeScale, gp.player.isSprinting() ? -7f : -9f));
        if (!sound.isRunning()) {
            sound.loop();
        }
    }

    void stop() {
        stopActiveSound();
        activeSoundSlot = -1;
    }

    private void stopActiveSound() {
        if (activeSoundSlot >= 0 && activeSoundSlot < sounds.length) {
            Sound sound = sounds[activeSoundSlot];
            if (sound != null && sound.isRunning()) {
                sound.stop();
            }
        }
    }

    private boolean isPlayerTryingToMove() {
        return gp.keyH.upPressed || gp.keyH.downPressed || gp.keyH.leftPressed || gp.keyH.rightPressed;
    }

    private int getSoundIndex() {
        switch (gp.currentMap) {
            case MapId.APARTMENT:
            case MapId.LIBRARY:
                return Sound.FOOTSTEPS_WOOD;
            case MapId.VILLAGE:
                if (isPlayerOnVillageStoneRoad()) {
                    return gp.player.isSprinting() ? Sound.FOOTSTEPS_STONE_SPRINT : Sound.FOOTSTEPS_STONE;
                }
                return Sound.FOOTSTEPS_DIRT;
            case MapId.FOREST_DOUBTS:
            case MapId.MOUNTAIN:
                return Sound.FOOTSTEPS_DIRT;
            default:
                return -1;
        }
    }

    private int getSoundSlot(int soundIndex) {
        for (int i = 0; i < SOUND_INDICES.length; i++) {
            if (SOUND_INDICES[i] == soundIndex) {
                return i;
            }
        }
        return -1;
    }

    private boolean isPlayerOnVillageStoneRoad() {
        int footX = gp.player.worldX + gp.player.solidArea.x + gp.player.solidArea.width / 2;
        int footY = gp.player.worldY + gp.player.solidArea.y + gp.player.solidArea.height;
        int col = footX / gp.tileSize;
        int row = footY / gp.tileSize;

        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow) {
            return false;
        }

        int tileNum = gp.tileM.mapTileNum[MapId.VILLAGE][col][row];
        return tileNum >= VILLAGE_STONE_ROAD_FIRST_TILE && tileNum <= VILLAGE_STONE_ROAD_LAST_TILE;
    }

    private float adjustedVolume(int volumeScale, float offsetDb) {
        return Math.max(-80f, Math.min(6f, Sound.volumeScaleToDb(volumeScale) + offsetDb));
    }
}
