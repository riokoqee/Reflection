package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class Sound {

    private Clip clip;
    private final URL[] soundURL = new URL[30];
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
