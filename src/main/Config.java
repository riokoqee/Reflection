package main;

import java.io.*;

public class Config {

    private final GamePanel gp;

    public Config(GamePanel gp) {
        this.gp = gp;
    }

    public void saveConfig() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt"))) {
            bw.write(gp.fullScreenOn ? "On" : "Off");
            bw.newLine();

            bw.write(String.valueOf(gp.music.volumeScale));
            bw.newLine();

            bw.write(String.valueOf(gp.se.volumeScale));
            bw.newLine();

            bw.write(String.valueOf(gp.brightnessScale));
            bw.newLine();
            bw.write(gp.crispPixels ? "On" : "Off");
            bw.newLine();
            bw.write(gp.screenShakeEnabled ? "On" : "Off");
            bw.newLine();
            bw.write(String.valueOf(gp.fpsLimitMode));
            bw.newLine();
            bw.write(String.valueOf(gp.ambienceVolumeScale));
            bw.newLine();
            bw.write(String.valueOf(gp.footstepVolumeScale));
            bw.newLine();
            bw.write(String.valueOf(gp.uiVolumeScale));
            bw.newLine();
            bw.write(String.valueOf(gp.whisperVolumeScale));
            bw.newLine();
            bw.write(String.valueOf(gp.dialogueTextSizeMode));
            bw.newLine();
            bw.write(String.valueOf(gp.dialogueTextSpeedMode));
            bw.newLine();
            bw.write(gp.highContrastDialogue ? "On" : "Off");
            bw.newLine();
        }
        catch (IOException e) {
            System.err.println("Config save failed: " + e.getMessage());
        }
    }

    public void loadConfig() {
        try (BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
            gp.fullScreenOn = "On".equals(br.readLine());
            gp.music.volumeScale = parseVolume(br.readLine(), gp.music.volumeScale);
            gp.se.volumeScale = parseVolume(br.readLine(), gp.se.volumeScale);
            gp.brightnessScale = parseRange(br.readLine(), gp.brightnessScale, 0, 5);
            gp.crispPixels = parseToggle(br.readLine(), gp.crispPixels);
            gp.screenShakeEnabled = parseToggle(br.readLine(), gp.screenShakeEnabled);
            gp.fpsLimitMode = parseRange(br.readLine(), gp.fpsLimitMode, 0, 2);
            gp.ambienceVolumeScale = parseVolume(br.readLine(), gp.ambienceVolumeScale);
            gp.footstepVolumeScale = parseVolume(br.readLine(), gp.footstepVolumeScale);
            gp.uiVolumeScale = parseVolume(br.readLine(), gp.uiVolumeScale);
            gp.whisperVolumeScale = parseVolume(br.readLine(), gp.whisperVolumeScale);
            gp.dialogueTextSizeMode = parseRange(br.readLine(), gp.dialogueTextSizeMode, 0, 2);
            gp.dialogueTextSpeedMode = parseRange(br.readLine(), gp.dialogueTextSpeedMode, 0, 3);
            gp.highContrastDialogue = parseToggle(br.readLine(), gp.highContrastDialogue);
            gp.hudVisible = false;
        }
        catch (Exception e) {
            System.err.println("Config load failed: " + e.getMessage());
        }
    }

    private int parseVolume(String value, int fallback) {
        return parseRange(value, fallback, 0, 5);
    }

    private int parseRange(String value, int fallback, int min, int max) {
        try {
            int parsed = Integer.parseInt(value);
            return Math.max(min, Math.min(max, parsed));
        }
        catch (Exception e) {
            return fallback;
        }
    }

    private boolean parseToggle(String value, boolean fallback) {
        if ("On".equals(value)) {
            return true;
        }
        if ("Off".equals(value)) {
            return false;
        }
        return fallback;
    }

}
