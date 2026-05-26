package main;

import data.SaveLoad;
import entity.Entity;
import entity.Player;
import entity.SwingChildNPC;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.locks.LockSupport;

public class GamePanel extends JPanel implements Runnable {

    private static final long serialVersionUID = 1L;

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
    public int currentMap = MapId.APARTMENT;

    private static final int APARTMENT_LEFT_COL = 5;
    private static final int APARTMENT_RIGHT_EXCLUSIVE_COL = 38;
    private static final int APARTMENT_TOP_ROW = 6;
    private static final int APARTMENT_BOTTOM_EXCLUSIVE_ROW = 25;
    private static final int ROOM_BEDROOM = 0;
    private static final int ROOM_HALL = 1;
    private static final int ROOM_KITCHEN = 2;
    private static final int ROOM_BATHROOM = 3;
    private static final int ROOM_CORRIDOR = 4;
    private static final ApartmentRoom[] APARTMENT_ROOMS = {
            new ApartmentRoom(ROOM_BEDROOM, "Спальня", 5, 6, 23, 16, true),
            new ApartmentRoom(ROOM_HALL, "Зал", 26, 6, 38, 16, true),
            new ApartmentRoom(ROOM_KITCHEN, "Кухня", 5, 15, 23, 25, true),
            new ApartmentRoom(ROOM_BATHROOM, "Санузел", 26, 15, 38, 25, true),
            new ApartmentRoom(ROOM_CORRIDOR, "Коридор", 22, 6, 27, 25, false)
    };
    private static final Color APARTMENT_ROOM_SHADOW = new Color(5, 7, 10, 178);
    private static final Color APARTMENT_INACTIVE_ROOM_SHADOW = new Color(0, 0, 0, 205);
    private static final int APARTMENT_ROOM_TRANSITION_FRAMES = 38;
    private static final int APARTMENT_ROOM_TRANSITION_MAX_ALPHA = 190;
    private static final int FOREST_LEFT_COL = 4;
    private static final int FOREST_RIGHT_EXCLUSIVE_COL = 46;
    private static final int FOREST_TOP_ROW = 4;
    private static final int FOREST_BOTTOM_EXCLUSIVE_ROW = 46;

    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    private BufferedImage renderScreen;
    private BufferStrategy screenBufferStrategy;
    private BufferedImage forestDarknessBuffer;
    private BufferedImage strongLightMask;
    private BufferedImage weakLightMask;
    private BufferedImage lanternLightMask;
    private BufferedImage playerGlowImage;
    private BufferedImage lanternGlowImage;
    private BufferedImage forestVignetteLantern;
    private BufferedImage forestVignetteDark;
    public boolean fullScreenOn = false;
    public boolean hudVisible = true;

    int FPS = 60;

    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    Sound cursorSE = new Sound();
    private final Sound[] oneShotSE = {new Sound(), new Sound(), new Sound(), new Sound()};
    Sound swingSound = new Sound();
    Sound footstepSound = new Sound();
    Sound apartmentAmbienceSound = new Sound();
    Sound whisperSound = new Sound();
    private boolean cursorSoundLoaded = false;
    private boolean swingSoundLoaded = false;
    private boolean swingSoundUnavailable = false;
    private int activeFootstepSoundIndex = -1;
    private int unavailableFootstepSoundIndex = -1;
    private boolean apartmentAmbienceLoaded = false;
    private boolean apartmentAmbienceUnavailable = false;
    private boolean whisperSoundLoaded = false;
    private boolean whisperSoundUnavailable = false;
    private int oneShotSECursor = 0;
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public StoryManager story = new StoryManager(this);
    Config config = new Config(this);
    public SaveLoad saveLoad = new SaveLoad(this);
    Thread gameThread;

    public Player player = new Player(this, keyH);
    public boolean hasLantern = false;
    public Entity obj[][] = new Entity[maxMap][120];
    public Entity npc[][] = new Entity[maxMap][10];
    private final ArrayList<Entity> entityList = new ArrayList<>();
    private final Object frameLock = new Object();
    private int lastApartmentRoomId = -1;
    private int apartmentRoomTransitionCounter = 0;
    private String apartmentRoomTransitionTitle = "";

    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int optionsState = 5;
    public final int resultState = 9;
    public int optionsReturnState = titleState;
    private int optionsReturnCommand = 0;
    public static final int SE_CURSOR = Sound.MENU_CURSOR;
    private static final int FALLBACK_CURSOR_SOUND_INDEX = 10;
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
        gameState = titleState;

        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        renderScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        forestDarknessBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        syncSoundEffectVolumes();
        preloadCursorSound();

        if (fullScreenOn) {
            setFullScreen();
        }
    }

    public void openPauseMenu() {
        if (gameState == pauseState) {
            return;
        }

        gameState = pauseState;
        ui.commandNum = 0;
        playConfirmSE();
    }

    public void closePauseMenu() {
        gameState = playState;
        playBackSE();
    }

    public void openOptionsMenu(int returnState) {
        optionsReturnState = returnState;
        optionsReturnCommand = ui.commandNum;
        gameState = optionsState;
        ui.commandNum = 0;
        playConfirmSE();
    }

    public void closeOptionsMenu() {
        config.saveConfig();
        gameState = optionsReturnState;
        ui.commandNum = optionsReturnCommand;
        playBackSE();
    }

    public void setFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        screenBufferStrategy = null;
        Main.window.setIgnoreRepaint(true);
        gd.setFullScreenWindow(Main.window);

        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window.getHeight();
    }

    public void toggleFullScreen() {
        fullScreenOn = !fullScreenOn;
        applyScreenMode();
        config.saveConfig();
    }

    public void changeMusicVolume(int amount) {
        music.volumeScale = clampVolume(music.volumeScale + amount);
        music.checkVolume();
        config.saveConfig();
    }

    public int getMusicVolume() {
        return music.volumeScale;
    }

    public void changeSoundEffectVolume(int amount) {
        se.volumeScale = clampVolume(se.volumeScale + amount);
        syncSoundEffectVolumes();
        config.saveConfig();
    }

    public int getSoundEffectVolume() {
        return se.volumeScale;
    }

    public void toggleHud() {
        hudVisible = !hudVisible;
        config.saveConfig();
    }

    private int clampVolume(int value) {
        return Math.max(0, Math.min(5, value));
    }

    private void syncSoundEffectVolumes() {
        cursorSE.volumeScale = se.volumeScale;
        cursorSE.checkVolume();
        for (Sound sound : oneShotSE) {
            sound.volumeScale = se.volumeScale;
            sound.checkVolume();
        }
        swingSound.volumeScale = se.volumeScale;
        footstepSound.volumeScale = se.volumeScale;
        apartmentAmbienceSound.volumeScale = se.volumeScale;
        whisperSound.volumeScale = se.volumeScale;
    }

    private void applyScreenMode() {
        if (Main.window == null) {
            return;
        }

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(null);
        Main.window.dispose();
        Main.window.setUndecorated(fullScreenOn);
        screenBufferStrategy = null;
        Main.window.setIgnoreRepaint(fullScreenOn);

        if (fullScreenOn) {
            Main.window.setVisible(true);
            gd.setFullScreenWindow(Main.window);
            screenWidth2 = Main.window.getWidth();
            screenHeight2 = Main.window.getHeight();
        }
        else {
            Main.window.pack();
            Main.window.setLocationRelativeTo(null);
            Main.window.setVisible(true);
            screenWidth2 = screenWidth;
            screenHeight2 = screenHeight;
        }

        requestFocusInWindow();
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
        return clampCameraX(cameraX);
    }

    public int getCameraY() {
        if (player == null) {
            return 0;
        }

        int cameraY = player.worldY - player.screenY;
        return clampCameraY(cameraY);
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

    private int clampCameraX(int cameraX) {
        switch (currentMap) {
            case MapId.APARTMENT:
                return clampApartmentCameraX(cameraX);
            case MapId.FOREST_DOUBTS:
                return clampCamera(cameraX, FOREST_LEFT_COL * tileSize,
                        FOREST_RIGHT_EXCLUSIVE_COL * tileSize - screenWidth);
            case MapId.VILLAGE:
                return clampCamera(cameraX, 0, maxWorldCol * tileSize - screenWidth);
            default:
                return cameraX;
        }
    }

    private int clampCameraY(int cameraY) {
        switch (currentMap) {
            case MapId.APARTMENT:
                return clampApartmentCameraY(cameraY);
            case MapId.FOREST_DOUBTS:
                return clampCamera(cameraY, FOREST_TOP_ROW * tileSize,
                        FOREST_BOTTOM_EXCLUSIVE_ROW * tileSize - screenHeight);
            case MapId.VILLAGE:
                return clampCamera(cameraY, 0, maxWorldRow * tileSize - screenHeight);
            default:
                return cameraY;
        }
    }

    private int clampApartmentCameraX(int cameraX) {
        ApartmentRoom room = getCurrentApartmentRoom();
        int apartmentMin = APARTMENT_LEFT_COL * tileSize;
        int apartmentMax = APARTMENT_RIGHT_EXCLUSIVE_COL * tileSize - screenWidth;
        if (room == null || !room.lockCamera) {
            return clampCamera(cameraX, apartmentMin, apartmentMax);
        }

        int roomMin = Math.max(apartmentMin, room.leftCol * tileSize);
        int roomMax = Math.min(apartmentMax, room.rightExclusiveCol * tileSize - screenWidth);
        if (roomMax < roomMin) {
            int anchoredCameraX = Math.max(apartmentMin,
                    Math.min(room.leftCol * tileSize, room.rightExclusiveCol * tileSize - screenWidth));
            return clampCamera(anchoredCameraX, apartmentMin, apartmentMax);
        }
        return clampCamera(cameraX, roomMin, roomMax);
    }

    private int clampApartmentCameraY(int cameraY) {
        ApartmentRoom room = getCurrentApartmentRoom();
        int apartmentMin = APARTMENT_TOP_ROW * tileSize;
        int apartmentMax = APARTMENT_BOTTOM_EXCLUSIVE_ROW * tileSize - screenHeight;
        if (room == null || !room.lockCamera) {
            return clampCamera(cameraY, apartmentMin, apartmentMax);
        }

        int roomMin = Math.max(apartmentMin, room.topRow * tileSize);
        int roomMax = Math.min(apartmentMax, room.bottomExclusiveRow * tileSize - screenHeight);
        if (roomMax < roomMin) {
            int anchoredCameraY = Math.max(apartmentMin,
                    Math.min(room.topRow * tileSize, room.bottomExclusiveRow * tileSize - screenHeight));
            return clampCamera(anchoredCameraY, apartmentMin, apartmentMax);
        }
        return clampCamera(cameraY, roomMin, roomMax);
    }

    public void run() {
        long drawInterval = 1_000_000_000L / FPS;
        long nextDrawTime = System.nanoTime();

        while (gameThread != null) {
            update();
            drawToTempScreen();
            presentFrame();

            nextDrawTime += drawInterval;
            if (nextDrawTime < System.nanoTime()) {
                nextDrawTime = System.nanoTime();
                continue;
            }

            waitForNextFrame(nextDrawTime);
        }
    }

    private void waitForNextFrame(long nextDrawTime) {
        while (gameThread != null) {
            long remainingTime = nextDrawTime - System.nanoTime();
            if (remainingTime <= 0) {
                return;
            }

            if (remainingTime > 2_000_000L) {
                LockSupport.parkNanos(remainingTime - 1_000_000L);
            }
            else {
                Thread.yield();
            }

            if (Thread.currentThread().isInterrupted()) {
                gameThread = null;
                return;
            }
        }
    }

    public void update() {
        story.update();

        if (gameState == playState) {
            player.update();

            for (int i = 0; i < npc[currentMap].length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }

            updateApartmentRoomTransition();
            updateFootstepSound();
            updateSwingSound();
        }
        else {
            stopFootstepSound();
            stopSwingSound();
        }

        if (gameState == playState || gameState == dialogueState) {
            updateAmbientSounds();
        }
        else {
            stopAmbientSounds();
        }
    }

    private void updateApartmentRoomTransition() {
        if (currentMap != MapId.APARTMENT) {
            lastApartmentRoomId = -1;
            apartmentRoomTransitionCounter = 0;
            return;
        }

        ApartmentRoom room = getCurrentApartmentRoom();
        int roomId = room == null ? -1 : room.id;
        int previousRoomId = lastApartmentRoomId;
        if (roomId != lastApartmentRoomId) {
            lastApartmentRoomId = roomId;
            if (previousRoomId != -1) {
                if (room != null && room.id == ROOM_CORRIDOR) {
                    playSE(Sound.DOOR_CLOSE);
                }
                else if (room != null) {
                    playSE(Sound.DOOR_OPEN);
                    startApartmentRoomTransition(room.title);
                }
            }
        }

        if (apartmentRoomTransitionCounter > 0) {
            apartmentRoomTransitionCounter--;
        }
    }

    private void startApartmentRoomTransition(String title) {
        apartmentRoomTransitionTitle = title;
        apartmentRoomTransitionCounter = APARTMENT_ROOM_TRANSITION_FRAMES;
    }

    private void updateSwingSound() {
        if (currentMap != MapId.FOREST_DOUBTS) {
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

    private void updateFootstepSound() {
        if (!isPlayerTryingToMove()) {
            stopFootstepSound();
            return;
        }

        int soundIndex = getFootstepSoundIndex();
        if (soundIndex == -1 || soundIndex == unavailableFootstepSoundIndex) {
            stopFootstepSound();
            return;
        }

        if (activeFootstepSoundIndex != soundIndex) {
            footstepSound.close();
            activeFootstepSoundIndex = -1;
            footstepSound.volumeScale = se.volumeScale;
            if (!footstepSound.setFile(soundIndex)) {
                unavailableFootstepSoundIndex = soundIndex;
                return;
            }
            activeFootstepSoundIndex = soundIndex;
        }

        footstepSound.setVolumeDb(adjustedSoundEffectVolume(-9f));
        if (!footstepSound.isRunning()) {
            footstepSound.loop();
        }
    }

    private void stopFootstepSound() {
        if (footstepSound.isRunning()) {
            footstepSound.stop();
        }
    }

    private boolean isPlayerTryingToMove() {
        return keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;
    }

    private int getFootstepSoundIndex() {
        switch (currentMap) {
            case MapId.APARTMENT:
                return Sound.FOOTSTEPS_WOOD;
            case MapId.FOREST_DOUBTS:
            case MapId.VILLAGE:
            case MapId.MOUNTAIN:
                return Sound.FOOTSTEPS_DIRT;
            default:
                return -1;
        }
    }

    private void updateAmbientSounds() {
        updateApartmentAmbienceSound();
        updateWhisperSound();
    }

    private void updateApartmentAmbienceSound() {
        if (currentMap != MapId.APARTMENT || apartmentAmbienceUnavailable) {
            stopApartmentAmbienceSound();
            return;
        }

        if (!apartmentAmbienceLoaded) {
            apartmentAmbienceSound.volumeScale = se.volumeScale;
            if (!apartmentAmbienceSound.setFile(Sound.APARTMENT_AMBIENCE)) {
                apartmentAmbienceUnavailable = true;
                return;
            }
            apartmentAmbienceLoaded = true;
        }

        apartmentAmbienceSound.setVolumeDb(adjustedSoundEffectVolume(-18f));
        if (!apartmentAmbienceSound.isRunning()) {
            apartmentAmbienceSound.loop();
        }
    }

    private void updateWhisperSound() {
        if (!shouldPlayWhispers() || whisperSoundUnavailable) {
            stopWhisperSound();
            return;
        }

        if (!whisperSoundLoaded) {
            whisperSound.volumeScale = se.volumeScale;
            if (!whisperSound.setFile(Sound.WHISPERS)) {
                whisperSoundUnavailable = true;
                return;
            }
            whisperSoundLoaded = true;
        }

        whisperSound.setVolumeDb(adjustedSoundEffectVolume(-23f));
        if (!whisperSound.isRunning()) {
            whisperSound.loop();
        }
    }

    private boolean shouldPlayWhispers() {
        return currentMap == MapId.FOREST_DOUBTS ||
                (currentMap == MapId.APARTMENT && story.shouldPlayApartmentWhispers());
    }

    private void stopAmbientSounds() {
        stopApartmentAmbienceSound();
        stopWhisperSound();
    }

    private void stopApartmentAmbienceSound() {
        if (apartmentAmbienceSound.isRunning()) {
            apartmentAmbienceSound.stop();
        }
    }

    public void stopWhispers() {
        stopWhisperSound();
    }

    private void stopWhisperSound() {
        if (whisperSound.isRunning()) {
            whisperSound.stop();
        }
    }

    private float adjustedSoundEffectVolume(float offsetDb) {
        return Math.max(-80f, Math.min(6f, Sound.volumeScaleToDb(se.volumeScale) + offsetDb));
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
        if (renderScreen == null) {
            return;
        }

        Graphics2D frameGraphics = renderScreen.createGraphics();
        prepareWorldGraphics(frameGraphics);
        renderWorldLayer(frameGraphics);
        frameGraphics.dispose();

        synchronized (frameLock) {
            BufferedImage readyFrame = renderScreen;
            renderScreen = tempScreen;
            tempScreen = readyFrame;
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        if (fullScreenOn) {
            return;
        }

        super.paintComponent(graphics);
        Graphics2D screenGraphics = (Graphics2D) graphics.create();
        drawFrameToScreen(screenGraphics, getWidth(), getHeight());
        screenGraphics.dispose();
    }

    private void renderWorldLayer(Graphics2D graphics) {
        clearFrame(graphics);

        if (gameState == titleState) {
            return;
        }
        if (gameState == optionsState && optionsReturnState != pauseState) {
            return;
        }

        drawGameWorld(graphics);
        drawApartmentRoomVisibility(graphics);
        drawForestMood(graphics);
    }

    private void prepareWorldGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    private void prepareScreenImageGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    private void prepareUiGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    private void clearFrame(Graphics2D graphics) {
        Composite oldComposite = graphics.getComposite();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, screenWidth, screenHeight);
        graphics.setComposite(oldComposite);
    }

    private void drawGameWorld(Graphics2D g2) {
        tileM.draw(g2);

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

        entityList.sort(Comparator.comparingInt(e -> e.worldY));

        for (int i = 0; i < entityList.size(); i++) {
            entityList.get(i).draw(g2);
        }

        entityList.clear();
    }

    private void drawApartmentRoomVisibility(Graphics2D g2) {
        if (currentMap != MapId.APARTMENT) {
            return;
        }

        ApartmentRoom room = getCurrentApartmentRoom();
        if (room == null) {
            return;
        }

        g2.setColor(room.id == ROOM_CORRIDOR ? APARTMENT_INACTIVE_ROOM_SHADOW : APARTMENT_ROOM_SHADOW);
        fillOutsideWorldTileRect(g2,
                room.leftCol,
                room.topRow,
                room.rightExclusiveCol,
                room.bottomExclusiveRow);
        drawWorldTileRectBorder(g2,
                room.leftCol,
                room.topRow,
                room.rightExclusiveCol,
                room.bottomExclusiveRow);
    }

    private ApartmentRoom getCurrentApartmentRoom() {
        if (currentMap != MapId.APARTMENT || player == null) {
            return null;
        }

        int playerCenterCol = (player.worldX + player.solidArea.x + player.solidArea.width / 2) / tileSize;
        int playerCenterRow = (player.worldY + player.solidArea.y + player.solidArea.height / 2) / tileSize;
        for (ApartmentRoom room : APARTMENT_ROOMS) {
            if (room.contains(playerCenterCol, playerCenterRow)) {
                return room;
            }
        }
        return null;
    }

    private void drawWorldTileRectBorder(Graphics2D g2, int leftCol, int topRow, int rightExclusiveCol,
                                         int bottomExclusiveRow) {
        int screenX = worldToScreenX(leftCol * tileSize);
        int screenY = worldToScreenY(topRow * tileSize);
        int width = (rightExclusiveCol - leftCol) * tileSize;
        int height = (bottomExclusiveRow - topRow) * tileSize;
        Color oldColor = g2.getColor();
        Stroke oldStroke = g2.getStroke();

        g2.setColor(new Color(0, 0, 0, 82));
        g2.setStroke(new BasicStroke(8));
        g2.drawRect(screenX + 2, screenY + 2, width - 4, height - 4);

        g2.setColor(oldColor);
        g2.setStroke(oldStroke);
    }

    private void fillOutsideWorldTileRect(Graphics2D g2, int leftCol, int topRow, int rightExclusiveCol,
                                          int bottomExclusiveRow) {
        Rectangle visibleRoom = new Rectangle(
                worldToScreenX(leftCol * tileSize),
                worldToScreenY(topRow * tileSize),
                (rightExclusiveCol - leftCol) * tileSize,
                (bottomExclusiveRow - topRow) * tileSize
        );
        Area hiddenArea = new Area(new Rectangle(0, 0, screenWidth, screenHeight));
        hiddenArea.subtract(new Area(visibleRoom));

        Shape oldClip = g2.getClip();
        g2.setClip(hiddenArea);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setClip(oldClip);
    }

    private void drawForestMood(Graphics2D g2) {
        if (currentMap != MapId.FOREST_DOUBTS) {
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

    private void presentFrame() {
        if (shouldUseActiveRender()) {
            drawWithBufferStrategy();
        }
        else {
            repaint();
        }
    }

    private boolean shouldUseActiveRender() {
        return fullScreenOn && Main.window != null && Main.window.isVisible();
    }

    private void drawWithBufferStrategy() {
        if (!ensureScreenBufferStrategy()) {
            return;
        }

        do {
            do {
                Graphics2D screenGraphics = (Graphics2D) screenBufferStrategy.getDrawGraphics();
                try {
                    drawFrameToScreen(screenGraphics, Main.window.getWidth(), Main.window.getHeight());
                }
                finally {
                    screenGraphics.dispose();
                }
            } while (screenBufferStrategy.contentsRestored());

            screenBufferStrategy.show();
            Toolkit.getDefaultToolkit().sync();
        } while (screenBufferStrategy.contentsLost());
    }

    private boolean ensureScreenBufferStrategy() {
        if (screenBufferStrategy != null) {
            return true;
        }
        if (Main.window == null || !Main.window.isDisplayable()) {
            return false;
        }

        try {
            Main.window.createBufferStrategy(2);
            screenBufferStrategy = Main.window.getBufferStrategy();
            return screenBufferStrategy != null;
        }
        catch (IllegalStateException e) {
            screenBufferStrategy = null;
            return false;
        }
    }

    private void drawFrameToScreen(Graphics2D screenGraphics, int targetWidth, int targetHeight) {
        if (targetWidth <= 0 || targetHeight <= 0) {
            targetWidth = screenWidth2;
            targetHeight = screenHeight2;
        }

        screenGraphics.setColor(Color.black);
        screenGraphics.fillRect(0, 0, targetWidth, targetHeight);

        double scale = Math.max((double) targetWidth / screenWidth, (double) targetHeight / screenHeight);
        int drawWidth = (int) Math.ceil(screenWidth * scale);
        int drawHeight = (int) Math.ceil(screenHeight * scale);
        int drawX = (targetWidth - drawWidth) / 2;
        int drawY = (targetHeight - drawHeight) / 2;

        if (shouldDrawWorldBuffer()) {
            prepareScreenImageGraphics(screenGraphics);
            synchronized (frameLock) {
                screenGraphics.drawImage(tempScreen, drawX, drawY, drawWidth, drawHeight, null);
            }
        }

        Graphics2D frameGraphics = (Graphics2D) screenGraphics.create(drawX, drawY, drawWidth, drawHeight);
        frameGraphics.scale(scale, scale);
        frameGraphics.setClip(0, 0, screenWidth, screenHeight);
        prepareUiGraphics(frameGraphics);
        ui.draw(frameGraphics);
        drawApartmentRoomTransition(frameGraphics);
        frameGraphics.dispose();
    }

    private void drawApartmentRoomTransition(Graphics2D g2) {
        if (apartmentRoomTransitionCounter <= 0 || apartmentRoomTransitionTitle.isEmpty()) {
            return;
        }

        int elapsed = APARTMENT_ROOM_TRANSITION_FRAMES - apartmentRoomTransitionCounter;
        float progress = elapsed / (float) APARTMENT_ROOM_TRANSITION_FRAMES;
        int alpha;
        if (progress < 0.35f) {
            alpha = (int) (APARTMENT_ROOM_TRANSITION_MAX_ALPHA * (progress / 0.35f));
        }
        else {
            alpha = (int) (APARTMENT_ROOM_TRANSITION_MAX_ALPHA * (1f - ((progress - 0.35f) / 0.65f)));
        }
        alpha = Math.max(0, Math.min(APARTMENT_ROOM_TRANSITION_MAX_ALPHA, alpha));

        Composite oldComposite = g2.getComposite();
        Font oldFont = g2.getFont();
        g2.setColor(new Color(0, 0, 0, alpha));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        int textAlpha = Math.max(0, Math.min(255, alpha + 45));
        if (textAlpha > 45) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 34));
            FontMetrics metrics = g2.getFontMetrics();
            int textX = (screenWidth - metrics.stringWidth(apartmentRoomTransitionTitle)) / 2;
            int textY = screenHeight / 2 + metrics.getAscent() / 2;

            g2.setColor(new Color(0, 0, 0, Math.min(180, textAlpha)));
            g2.drawString(apartmentRoomTransitionTitle, textX + 3, textY + 3);
            g2.setColor(new Color(236, 241, 233, textAlpha));
            g2.drawString(apartmentRoomTransitionTitle, textX, textY);
        }

        g2.setFont(oldFont);
        g2.setComposite(oldComposite);
    }

    private boolean shouldDrawWorldBuffer() {
        return gameState != titleState && !(gameState == optionsState && optionsReturnState != pauseState);
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
        playSEAndGetDurationFrames(i);
    }

    public int playSEAndGetDurationFrames(int i) {
        Sound sound = oneShotSE[oneShotSECursor];
        oneShotSECursor = (oneShotSECursor + 1) % oneShotSE.length;
        sound.volumeScale = getSoundEffectVolume();
        if (sound.setFile(i)) {
            int durationFrames = sound.getDurationFrames(FPS);
            sound.play();
            return durationFrames;
        }
        return 0;
    }

    public void playConfirmSE() {
        playSE(Sound.MENU_CONFIRM);
    }

    public void playBackSE() {
        playSE(Sound.MENU_BACK);
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
            playSE(FALLBACK_CURSOR_SOUND_INDEX);
        }
    }

    private void preloadCursorSound() {
        cursorSE.volumeScale = se.volumeScale;
        cursorSoundLoaded = cursorSE.setFile(SE_CURSOR);
    }

    private static final class ApartmentRoom {
        final int id;
        final String title;
        final int leftCol;
        final int topRow;
        final int rightExclusiveCol;
        final int bottomExclusiveRow;
        final boolean lockCamera;

        ApartmentRoom(int id, String title, int leftCol, int topRow, int rightExclusiveCol,
                      int bottomExclusiveRow, boolean lockCamera) {
            this.id = id;
            this.title = title;
            this.leftCol = leftCol;
            this.topRow = topRow;
            this.rightExclusiveCol = rightExclusiveCol;
            this.bottomExclusiveRow = bottomExclusiveRow;
            this.lockCamera = lockCamera;
        }

        boolean contains(int col, int row) {
            return col >= leftCol &&
                    col < rightExclusiveCol &&
                    row >= topRow &&
                    row < bottomExclusiveRow;
        }
    }
}
