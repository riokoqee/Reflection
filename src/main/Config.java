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
            gp.hudVisible = false;
        }
        catch (Exception e) {
            System.err.println("Config load failed: " + e.getMessage());
        }
    }

    private int parseVolume(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value);
            return Math.max(0, Math.min(5, parsed));
        }
        catch (Exception e) {
            return fallback;
        }
    }

}
