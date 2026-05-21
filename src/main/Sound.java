package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class Sound {

    Clip clip;
    URL soundURL[] = new URL[30];
    FloatControl fc;
    FloatControl panControl;
    int volumeScale = 3;
    float volume;

    public Sound() {

        soundURL[0] = getClass().getResource("/sound/BlueBoyAdventure.wav");
        soundURL[1] = getClass().getResource("/sound/coin.wav");
        soundURL[2] = getClass().getResource("/sound/powerup.wav");
        soundURL[3] = getClass().getResource("/sound/unlock.wav");
        soundURL[4] = getClass().getResource("/sound/fanfare.wav");
        soundURL[5] = getClass().getResource("/sound/hitmonster.wav");
        soundURL[6] = getClass().getResource("/sound/receivedamage.wav");
        soundURL[7] = getClass().getResource("/sound/swordswing.wav");
        soundURL[8] = getClass().getResource("/sound/slimedamage.wav");
        soundURL[9] = getClass().getResource("/sound/levelup.wav");
        soundURL[10] = getClass().getResource("/sound/cursor.wav");
        soundURL[11] = getClass().getResource("/sound/gameover.wav");
        soundURL[12] = getClass().getResource("/sound/stairs.wav");
        soundURL[13] = getClass().getResource("/sound/burning.wav");
        soundURL[14] = getClass().getResource("/sound/treecutting.wav");
        soundURL[15] = getClass().getResource("/sound/rocking_chair.wav");
    }

    public boolean setFile(int i) {

        try {
            close();
            if (i < 0 || i >= soundURL.length || soundURL[i] == null) {
                throw new IllegalArgumentException("Missing sound resource index " + i);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            fc = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (clip.isControlSupported(FloatControl.Type.PAN)) {
                panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
            }
            else {
                panControl = null;
            }
            checkVolume();
            return true;
        }catch(Exception e) {
            System.out.println("Sound Exception: " + i + " - " + e.getMessage());
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

        volume = volumeScaleToDb(volumeScale);
        setVolumeDb(volume);
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
}
