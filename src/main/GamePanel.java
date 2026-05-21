package main;

import data.SaveLoad;
import entity.Entity;
import entity.Player;
import entity.SwingChildNPC;
import tile.TileManager;
import tile_interactive.InteractiveTile;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int maxMap = 10;
    public int currentMap = 0;

    private static final int APARTMENT_MAP = 0;
    private static final int APARTMENT_LEFT_COL = 5;
    private static final int APARTMENT_RIGHT_EXCLUSIVE_COL = 26;
    private static final int APARTMENT_TOP_ROW = 6;
    private static final int APARTMENT_BOTTOM_EXCLUSIVE_ROW = 21;
    private static final int FOREST_DOUBTS_MAP = 1;
    private static final int FOREST_LEFT_COL = 4;
    private static final int FOREST_RIGHT_EXCLUSIVE_COL = 46;
    private static final int FOREST_TOP_ROW = 4;
    private static final int FOREST_BOTTOM_EXCLUSIVE_ROW = 46;
    private static final int VILLAGE_MAP = 2;

    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    private BufferedImage pauseBackground;
    private BufferedImage forestDarknessBuffer;
    private BufferedImage strongLightMask;
    private BufferedImage weakLightMask;
    private BufferedImage lanternLightMask;
    private BufferedImage playerGlowImage;
    private BufferedImage lanternGlowImage;
    private BufferedImage forestVignetteLantern;
    private BufferedImage forestVignetteDark;
    Graphics2D g2;
    public boolean fullScreenOn = false;

    int FPS = 60;

    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    Sound cursorSE = new Sound();
    Sound swingSound = new Sound();
    private boolean cursorSoundLoaded = false;
    private boolean swingSoundLoaded = false;
    private boolean swingSoundUnavailable = false;
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public StoryManager story = new StoryManager(this);
    public EventHandler eHandler = new EventHandler(this);
    Config config = new Config(this);
    public SaveLoad saveLoad = new SaveLoad(this);
    Thread gameThread;

    public Player player = new Player(this, keyH);
    public boolean hasLantern = false;
    public Entity obj[][] = new Entity[maxMap][120];
    public Entity npc[][] = new Entity[maxMap][10];
    public InteractiveTile iTile[][] = new InteractiveTile[maxMap][50];
    public ArrayList<Entity> particleList = new ArrayList<>();
    ArrayList<Entity> entityList = new ArrayList<>();

    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int optionsState = 5;
    public final int transitionState = 7;
    public final int resultState = 9;
    public static final int SE_CURSOR = 10;
    private static final int SWING_SOUND_INDEX = 15;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame() {
        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setInteractiveTile();
        gameState = titleState;

        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        pauseBackground = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        forestDarknessBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) tempScreen.getGraphics();
        preloadCursorSound();

        if (fullScreenOn) {
            setFullScreen();
        }
    }

    public void resetGame(boolean restart) {
        if (restart) {
            story.startNewGame();
        }
        else {
            player.setDefaultPositions();
            player.restoreStatus();
        }
    }

    public void openPauseMenu() {
        if (gameState == pauseState) {
            return;
        }

        capturePauseBackground();
        gameState = pauseState;
        ui.commandNum = 0;
        playCursorSE();
    }

    public void closePauseMenu() {
        gameState = playState;
        playCursorSE();
    }

    public void clearPauseBackground() {
        if (pauseBackground == null) {
            return;
        }
        Graphics2D pauseGraphics = pauseBackground.createGraphics();
        pauseGraphics.setComposite(AlphaComposite.Clear);
        pauseGraphics.fillRect(0, 0, screenWidth, screenHeight);
        pauseGraphics.dispose();
    }

    private void capturePauseBackground() {
        if (tempScreen == null) {
            return;
        }
        if (pauseBackground == null) {
            pauseBackground = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D pauseGraphics = pauseBackground.createGraphics();
        pauseGraphics.setComposite(AlphaComposite.Src);
        pauseGraphics.drawImage(tempScreen, 0, 0, null);
        pauseGraphics.dispose();
    }

    public void restart() {
        story.startNewGame();
    }

    public void setFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Main.window);

        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window.getHeight();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public int getCameraX() {
        if (player == null) {
            return 0;
        }

        int cameraX = player.worldX - player.screenX;
        if (currentMap == APARTMENT_MAP) {
            int minX = APARTMENT_LEFT_COL * tileSize;
            int maxX = APARTMENT_RIGHT_EXCLUSIVE_COL * tileSize - screenWidth;
            return clampCamera(cameraX, minX, maxX);
        }
        if (currentMap == FOREST_DOUBTS_MAP) {
            int minX = FOREST_LEFT_COL * tileSize;
            int maxX = FOREST_RIGHT_EXCLUSIVE_COL * tileSize - screenWidth;
            return clampCamera(cameraX, minX, maxX);
        }
        if (currentMap == VILLAGE_MAP) {
            return clampCamera(cameraX, 0, maxWorldCol * tileSize - screenWidth);
        }
        return cameraX;
    }

    public int getCameraY() {
        if (player == null) {
            return 0;
        }

        int cameraY = player.worldY - player.screenY;
        if (currentMap == APARTMENT_MAP) {
            int minY = APARTMENT_TOP_ROW * tileSize;
            int maxY = APARTMENT_BOTTOM_EXCLUSIVE_ROW * tileSize - screenHeight;
            return clampCamera(cameraY, minY, maxY);
        }
        if (currentMap == FOREST_DOUBTS_MAP) {
            int minY = FOREST_TOP_ROW * tileSize;
            int maxY = FOREST_BOTTOM_EXCLUSIVE_ROW * tileSize - screenHeight;
            return clampCamera(cameraY, minY, maxY);
        }
        if (currentMap == VILLAGE_MAP) {
            return clampCamera(cameraY, 0, maxWorldRow * tileSize - screenHeight);
        }
        return cameraY;
    }

    public int worldToScreenX(int worldX) {
        return worldX - getCameraX();
    }

    public int worldToScreenY(int worldY) {
        return worldY - getCameraY();
    }

    public boolean isInCamera(int worldX, int worldY, int width, int height) {
        int cameraX = getCameraX();
        int cameraY = getCameraY();
        return worldX + width > cameraX &&
                worldX < cameraX + screenWidth &&
                worldY + height > cameraY &&
                worldY < cameraY + screenHeight;
    }

    private int clampCamera(int value, int min, int max) {
        if (max < min) {
            return min;
        }
        return Math.max(min, Math.min(value, max));
    }

    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                drawToTempScreen();
                drawToScreen();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            player.update();

            for (int i = 0; i < npc[currentMap].length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }

            for (int i = 0; i < particleList.size(); i++) {
                if (particleList.get(i) != null) {
                    if (particleList.get(i).alive) {
                        particleList.get(i).update();
                    }
                    if (!particleList.get(i).alive) {
                        particleList.remove(i);
                    }
                }
            }

            updateSwingSound();
        }
        else {
            stopSwingSound();
        }
    }

    private void updateSwingSound() {
        if (currentMap != FOREST_DOUBTS_MAP) {
            stopSwingSound();
            return;
        }

        Point source = getSwingSoundSource();
        if (source == null) {
            stopSwingSound();
            return;
        }

        float volume = calculateSpatialVolume(source.x, source.y);
        if (volume <= -79f) {
            stopSwingSound();
            return;
        }

        if (!swingSoundLoaded) {
            if (swingSoundUnavailable || !swingSound.setFile(SWING_SOUND_INDEX)) {
                swingSoundUnavailable = true;
                return;
            }
            swingSoundLoaded = true;
        }

        swingSound.setVolumeDb(volume);
        swingSound.setPan(calculateSpatialPan(source.x));
        if (!swingSound.isRunning()) {
            swingSound.loop();
        }
    }

    private void stopSwingSound() {
        if (swingSoundLoaded && swingSound.isRunning()) {
            swingSound.stop();
        }
    }

    private Point getSwingSoundSource() {
        for (int i = 0; i < npc[currentMap].length; i++) {
            Entity entity = npc[currentMap][i];
            if (entity instanceof SwingChildNPC) {
                return new Point(entity.worldX + tileSize / 2, entity.worldY + tileSize / 2);
            }
        }
        return null;
    }

    private float calculateSpatialVolume(int sourceX, int sourceY) {
        double playerCenterX = player.worldX + tileSize / 2.0;
        double playerCenterY = player.worldY + tileSize / 2.0;
        double dx = playerCenterX - sourceX;
        double dy = playerCenterY - sourceY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double minDistance = tileSize * 2.0;
        double maxDistance = tileSize * 38.0;

        if (distance >= maxDistance) {
            return -80f;
        }

        double fade = Math.max(0.0, (distance - minDistance) / (maxDistance - minDistance));
        float baseVolume = Sound.volumeScaleToDb(se.volumeScale);
        float attenuation = (float) (-34.0 * fade * fade);
        return Math.max(-80f, Math.min(6f, baseVolume + attenuation));
    }

    private float calculateSpatialPan(int sourceX) {
        double playerCenterX = player.worldX + tileSize / 2.0;
        double maxPanDistance = tileSize * 10.0;
        double pan = (sourceX - playerCenterX) / maxPanDistance;
        return (float) Math.max(-1.0, Math.min(1.0, pan));
    }

    public void drawToTempScreen() {
        if (g2 == null) {
            return;
        }

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameState == titleState) {
            ui.draw(g2);
        }
        else if (gameState == pauseState && pauseBackground != null) {
            g2.drawImage(pauseBackground, 0, 0, null);
            ui.draw(g2);
        }
        else {
            tileM.draw(g2);

            for (int i = 0; i < iTile[currentMap].length; i++) {
                if (iTile[currentMap][i] != null) {
                    iTile[currentMap][i].draw(g2);
                }
            }

            entityList.add(player);

            for (int i = 0; i < npc[currentMap].length; i++) {
                if (npc[currentMap][i] != null) {
                    entityList.add(npc[currentMap][i]);
                }
            }

            for (int i = 0; i < obj[currentMap].length; i++) {
                if (obj[currentMap][i] != null) {
                    entityList.add(obj[currentMap][i]);
                }
            }

            for (int i = 0; i < particleList.size(); i++) {
                if (particleList.get(i) != null) {
                    entityList.add(particleList.get(i));
                }
            }

            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    return Integer.compare(e1.worldY, e2.worldY);
                }
            });

            for (int i = 0; i < entityList.size(); i++) {
                entityList.get(i).draw(g2);
            }

            entityList.clear();
            drawForestMood(g2);
            ui.draw(g2);
        }
    }

    private void drawForestMood(Graphics2D g2) {
        if (currentMap != FOREST_DOUBTS_MAP) {
            return;
        }

        ensureForestEffectBuffers();
        drawForestDarkness(g2);
        drawForestVignette(g2);
    }

    private void drawForestDarkness(Graphics2D g2) {
        if (forestDarknessBuffer == null) {
            forestDarknessBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D shadow = forestDarknessBuffer.createGraphics();
        shadow.setComposite(AlphaComposite.Src);

        int darknessAlpha = hasLantern ? 166 : 238;
        shadow.setColor(new Color(2, 7, 9, darknessAlpha));
        shadow.fillRect(0, 0, screenWidth, screenHeight);

        int lightX = worldToScreenX(player.worldX) + tileSize / 2;
        int lightY = worldToScreenY(player.worldY) + tileSize / 2;
        Point lanternLight = getForestLanternLightPoint();

        shadow.setComposite(AlphaComposite.DstOut);
        if (hasLantern) {
            drawCenteredImage(shadow, strongLightMask, lightX, lightY);
        }
        else {
            drawCenteredImage(shadow, weakLightMask, lightX, lightY);
            if (lanternLight != null) {
                drawCenteredImage(shadow, lanternLightMask, lanternLight.x, lanternLight.y);
            }
        }
        shadow.dispose();

        g2.drawImage(forestDarknessBuffer, 0, 0, null);

        if (hasLantern) {
            drawCenteredImage(g2, playerGlowImage, lightX, lightY);
        }
        else if (lanternLight != null) {
            drawCenteredImage(g2, lanternGlowImage, lanternLight.x, lanternLight.y);
        }
    }

    private void ensureForestEffectBuffers() {
        if (strongLightMask != null) {
            return;
        }

        strongLightMask = createRadialImage(tileSize * 5.0f, new Color(255, 255, 255, 245),
                new Color(255, 255, 255, 115), new Color(255, 255, 255, 0));
        weakLightMask = createRadialImage(tileSize * 1.35f, new Color(255, 255, 255, 78),
                new Color(255, 255, 255, 24), new Color(255, 255, 255, 0));
        lanternLightMask = createRadialImage(tileSize * 4.1f, new Color(255, 255, 255, 255),
                new Color(255, 255, 255, 126), new Color(255, 255, 255, 0));
        playerGlowImage = createRadialImage(tileSize * 4.5f, alphaColor(255, 211, 92, 150, 0.18f),
                alphaColor(214, 133, 43, 55, 0.18f), new Color(214, 133, 43, 0));
        lanternGlowImage = createRadialImage(tileSize * 3.7f, alphaColor(255, 211, 92, 150, 0.25f),
                alphaColor(214, 133, 43, 55, 0.25f), new Color(214, 133, 43, 0));
        forestVignetteLantern = createForestVignetteImage(true);
        forestVignetteDark = createForestVignetteImage(false);
    }

    private BufferedImage createRadialImage(float radius, Color center, Color middle, Color edge) {
        int size = (int) Math.ceil(radius * 2);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        float[] dist = {0f, 0.48f, 1f};
        Color[] colors = {center, middle, edge};

        graphics.setComposite(AlphaComposite.Src);
        graphics.setPaint(new RadialGradientPaint(new Point2D.Float(size / 2f, size / 2f), radius, dist, colors));
        graphics.fillRect(0, 0, size, size);
        graphics.dispose();
        return image;
    }

    private BufferedImage createForestVignetteImage(boolean hasLanternLight) {
        BufferedImage image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        float radius = Math.max(screenWidth, screenHeight) * 0.75f;
        float[] dist = {0f, 0.6f, 1f};
        Color[] colors = {
                new Color(0, 0, 0, 0),
                new Color(0, 10, 8, hasLanternLight ? 45 : 70),
                new Color(0, 0, 0, hasLanternLight ? 165 : 205)
        };

        graphics.setComposite(AlphaComposite.Src);
        graphics.setPaint(new RadialGradientPaint(
                new Point2D.Float(screenWidth / 2f, screenHeight / 2f),
                radius,
                dist,
                colors
        ));
        graphics.fillRect(0, 0, screenWidth, screenHeight);
        graphics.dispose();
        return image;
    }

    private Color alphaColor(int red, int green, int blue, int alpha, float multiplier) {
        return new Color(red, green, blue, Math.max(0, Math.min(255, Math.round(alpha * multiplier))));
    }

    private void drawCenteredImage(Graphics2D graphics, BufferedImage image, int centerX, int centerY) {
        graphics.drawImage(image, centerX - image.getWidth() / 2, centerY - image.getHeight() / 2, null);
    }

    private void drawForestVignette(Graphics2D g2) {
        g2.drawImage(hasLantern ? forestVignetteLantern : forestVignetteDark, 0, 0, null);
    }

    private Point getForestLanternLightPoint() {
        if (hasLantern) {
            return null;
        }

        for (int i = 0; i < obj[currentMap].length; i++) {
            Entity object = obj[currentMap][i];
            if (object != null && "Lantern".equals(object.name)) {
                int centerX = object.worldX + object.solidArea.x + object.solidArea.width / 2;
                int centerY = object.worldY + object.solidArea.y + object.solidArea.height / 2;
                return new Point(worldToScreenX(centerX), worldToScreenY(centerY));
            }
        }
        return null;
    }

    public void drawToScreen() {
        Graphics g = getGraphics();
        g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
        g.dispose();
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

    public void playCursorSE() {
        if (!cursorSoundLoaded) {
            preloadCursorSound();
        }

        if (cursorSoundLoaded) {
            cursorSE.volumeScale = se.volumeScale;
            cursorSE.checkVolume();
            cursorSE.playFromStart();
        }
        else {
            playSE(SE_CURSOR);
        }
    }

    private void preloadCursorSound() {
        cursorSE.volumeScale = se.volumeScale;
        cursorSoundLoaded = cursorSE.setFile(SE_CURSOR);
    }
}
