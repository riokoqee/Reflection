package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class Sound {

    public static final int MENU_CURSOR = 16;
    public static final int MENU_CONFIRM = 17;
    public static final int MENU_BACK = 18;
    public static final int DOOR_OPEN = 19;
    public static final int DOOR_CLOSE = 20;
    public static final int FOOTSTEPS_WOOD = 21;
    public static final int FOOTSTEPS_DIRT = 22;
    public static final int APARTMENT_AMBIENCE = 23;
    public static final int BED_INTERACT = 24;
    public static final int KETTLE = 25;
    public static final int WATER_SINK = 26;
    public static final int SOFA_SIT = 27;
    public static final int LANTERN_PICKUP = 28;
    public static final int SHADOW_WHOOSH = 29;
    public static final int WHISPERS = 30;

    private Clip clip;
    private final URL[] soundURL = new URL[64];
    private FloatControl fc;
    private FloatControl panControl;
    int volumeScale = 3;

    public Sound() {
        register(0, "BlueBoyAdventure.wav");
        register(1, "coin.wav");
        register(2, "powerup.wav");
        register(3, "unlock.wav");
        register(4, "fanfare.wav");
        register(5, "hitmonster.wav");
        register(6, "receivedamage.wav");
        register(7, "swordswing.wav");
        register(8, "slimedamage.wav");
        register(9, "levelup.wav");
        register(10, "cursor.wav");
        register(11, "gameover.wav");
        register(12, "stairs.wav");
        register(13, "burning.wav");
        register(14, "treecutting.wav");
        register(15, "rocking_chair.wav");
        register(MENU_CURSOR, "new/menu_cursor.wav");
        register(MENU_CONFIRM, "new/menu_confirm.wav");
        register(MENU_BACK, "new/menu_back.wav");
        register(DOOR_OPEN, "new/door_open.wav");
        register(DOOR_CLOSE, "new/door_close.wav");
        register(FOOTSTEPS_WOOD, "new/footsteps_wood.wav");
        register(FOOTSTEPS_DIRT, "new/footsteps_dirt.wav");
        register(APARTMENT_AMBIENCE, "new/apartment_ambience.wav");
        register(BED_INTERACT, "new/bed_interact.wav");
        register(KETTLE, "new/kettle.wav");
        register(WATER_SINK, "new/water_sink.wav");
        register(SOFA_SIT, "new/sofa_sit.wav");
        register(LANTERN_PICKUP, "new/lantern.wav");
        register(SHADOW_WHOOSH, "new/shadow_whoosh.wav");
        register(WHISPERS, "new/whispers.wav");
    }

    public boolean setFile(int i) {
        try {
            close();
            if (i < 0 || i >= soundURL.length || soundURL[i] == null) {
                throw new IllegalArgumentException("Missing sound resource index " + i);
            }
            try (AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i])) {
                clip = AudioSystem.getClip();
                clip.open(ais);
            }
            fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (clip.isControlSupported(FloatControl.Type.PAN)) {
                panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
            }
            else {
                panControl = null;
            }
            checkVolume();
            return true;
        }
        catch (Exception e) {
            System.err.println("Sound failed: " + i + " - " + e.getMessage());
            clip = null;
            fc = null;
            panControl = null;
            return false;
        }
    }

    public void play() {
        if (clip == null) {
            return;
        }
        clip.start();
    }

    public void playFromStart() {
        if (clip == null) {
            return;
        }
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        if (clip == null) {
            return;
        }
        if (!clip.isRunning()) {
            clip.setFramePosition(0);
        }
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip == null) {
            return;
        }
        clip.stop();
    }

    public void close() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
        clip = null;
        fc = null;
        panControl = null;
    }

    public boolean isRunning() {
        return clip != null && clip.isRunning();
    }

    public int getDurationFrames(int fps) {
        if (clip == null || fps <= 0) {
            return 0;
        }

        long microseconds = clip.getMicrosecondLength();
        if (microseconds <= 0) {
            return 0;
        }
        return Math.max(1, (int) Math.ceil(microseconds * fps / 1_000_000.0));
    }

    public void setVolumeDb(float db) {
        if (fc == null) {
            return;
        }
        float min = fc.getMinimum();
        float max = fc.getMaximum();
        fc.setValue(Math.max(min, Math.min(max, db)));
    }

    public void setPan(float pan) {
        if (panControl == null) {
            return;
        }
        float min = panControl.getMinimum();
        float max = panControl.getMaximum();
        panControl.setValue(Math.max(min, Math.min(max, pan)));
    }

    public void checkVolume() {
        setVolumeDb(volumeScaleToDb(volumeScale));
    }

    public static float volumeScaleToDb(int volumeScale) {
        switch (volumeScale) {
            case 0: return -80f;
            case 1: return -20f;
            case 2: return -12f;
            case 3: return -5f;
            case 4: return 1f;
            case 5: return 6f;
            default: return -5f;
        }
    }

    private void register(int index, String fileName) {
        soundURL[index] = getClass().getResource("/sound/" + fileName);
    }
}
