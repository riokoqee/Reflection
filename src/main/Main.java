package main;

import javax.swing.*;

public class Main {

    public static JFrame window;

    public static void main(String[] args) {
        configureStableJava2D();

        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Reflection");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        gamePanel.config.loadConfig();
        gamePanel.showInitialWindow();

        gamePanel.setupGame();
        gamePanel.startGameThread();
    }

    private static void configureStableJava2D() {
        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("sun.java2d.uiScale.enabled", "false");
        System.setProperty("sun.java2d.dpiaware", "true");
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.opengl", "false");
        System.setProperty("sun.java2d.ddoffscreen", "false");
        System.setProperty("swing.aatext", "false");
        System.setProperty("awt.useSystemAAFontSettings", "off");
    }
}
