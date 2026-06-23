package main;

import data.SaveLoad;
import entity.Entity;
import entity.Player;
import entity.SwingChildNPC;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
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
    private static final int LIBRARY_LEFT_COL = 15;
    private static final int LIBRARY_RIGHT_EXCLUSIVE_COL = 35;
    private static final int LIBRARY_TOP_ROW = 12;
    private static final int LIBRARY_BOTTOM_EXCLUSIVE_ROW = 24;
    private static final int ROOM_BEDROOM = 0;
    private static final int ROOM_HALL = 1;
    private static final int ROOM_KITCHEN = 2;
    private static final int ROOM_BATHROOM = 3;
    private static final int ROOM_CORRIDOR = 4;
    private static final ApartmentRoom[] APARTMENT_ROOMS = {
            new ApartmentRoom(ROOM_BEDROOM, "Спальня", 13, 6, 23, 16, true),
            new ApartmentRoom(ROOM_HALL, "Зал", 26, 6, 38, 16, true),
            new ApartmentRoom(ROOM_KITCHEN, "Кухня", 13, 15, 23, 25, true),
            new ApartmentRoom(ROOM_BATHROOM, "Ванная", 26, 16, 34, 24, true),
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
    static final int FPS_LIMIT_60 = 0;
    public static final int GRAPHICS_QUALITY = 0;
    public static final int GRAPHICS_BALANCED = 1;
    public static final int GRAPHICS_LAPTOP = 2;
    public static final int LANGUAGE_RU = 0;
    public static final int LANGUAGE_EN = 1;
    private static final int INTRO_MENU_TRANSITION_FRAMES = 92;
    private static final int INTRO_DISCLAIMER_TYPE_FRAMES = 135;
    private static final int INTRO_DISCLAIMER_HOLD_FRAMES = 300;
    private static final int INTRO_GAME_FADE_FRAMES = 80;
    private static final int INTRO_TOTAL_FRAMES = INTRO_MENU_TRANSITION_FRAMES +
            INTRO_DISCLAIMER_TYPE_FRAMES + INTRO_DISCLAIMER_HOLD_FRAMES + INTRO_GAME_FADE_FRAMES;
    private static final int FINAL_SCENE_TOTAL_FRAMES = 1140;
    private static final int DIALOGUE_TYPE_INTERVAL_FRAMES = 10;
    private static final boolean SYNC_TOOLKIT_AFTER_PRESENT =
            !System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");

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
    private BufferedImage bedroomLampGlowImage;
    private BufferedImage forestVignetteLantern;
    private BufferedImage forestVignetteDark;
    private boolean forestDarknessCacheValid = false;
    private boolean forestDarknessCacheHasLantern = false;
    private int forestDarknessCacheBrightness = Integer.MIN_VALUE;
    private int forestDarknessCacheLightX = Integer.MIN_VALUE;
    private int forestDarknessCacheLightY = Integer.MIN_VALUE;
    private int forestDarknessCacheLanternX = Integer.MIN_VALUE;
    private int forestDarknessCacheLanternY = Integer.MIN_VALUE;
    public boolean fullScreenOn = false;
    public boolean hudVisible = false;
    public int brightnessScale = 3;
    public boolean crispPixels = true;
    public boolean screenShakeEnabled = true;
    public int fpsLimitMode = FPS_LIMIT_60;
    public int graphicsMode = GRAPHICS_BALANCED;
    public boolean showFpsCounter = false;
    public int ambienceVolumeScale = 3;
    public int footstepVolumeScale = 3;
    public int uiVolumeScale = 3;
    public int whisperVolumeScale = 3;
    public int dialogueTextSizeMode = 1;
    public int dialogueTextSpeedMode = 1;
    public boolean highContrastDialogue = false;
    public int languageMode = LANGUAGE_RU;

    int FPS = 60;

    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    public MouseHandler mouseH = new MouseHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    private final Sound[] cursorSE = {new Sound(), new Sound(), new Sound(), new Sound()};
    private final Sound[] oneShotSE = {new Sound(), new Sound(), new Sound(), new Sound()};
    Sound swingSound = new Sound();
    private final FootstepAudio footstepAudio = new FootstepAudio(this);
    Sound apartmentAmbienceSound = new Sound();
    Sound whisperSound = new Sound();
    private boolean cursorSoundLoaded = false;
    private boolean cursorSoundUnavailable = false;
    private int cursorSECursor = 0;
    private boolean swingSoundLoaded = false;
    private boolean swingSoundUnavailable = false;
    private boolean apartmentAmbienceLoaded = false;
    private boolean apartmentAmbienceUnavailable = false;
    private boolean whisperSoundLoaded = false;
    private boolean whisperSoundUnavailable = false;
    private int oneShotSECursor = 0;
    private int introFrame = 0;
    private int finalSceneFrame = 0;
    private int dialogueTypeCooldown = 0;
    private int framesThisSecond = 0;
    private long fpsSampleStartNanos = System.nanoTime();
    private volatile int currentFps = 0;
    private long updateNanosThisSecond = 0L;
    private long renderNanosThisSecond = 0L;
    private long presentNanosThisSecond = 0L;
    private int updateSamplesThisSecond = 0;
    private int renderSamplesThisSecond = 0;
    private int presentSamplesThisSecond = 0;
    private volatile double currentUpdateMs = 0.0;
    private volatile double currentRenderMs = 0.0;
    private volatile double currentPresentMs = 0.0;
    private volatile long currentMemoryMb = 0L;
    private String playerName = "";
    private String lastResultReportPath = "";
    private String resultReportNotice = "";
    private int resultReportNoticeCounter = 0;
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public StoryManager story = new StoryManager(this);
    Config config = new Config(this);
    public SaveLoad saveLoad = new SaveLoad(this);
    Thread gameThread;

    public Player player = new Player(this, keyH);
    public boolean hasLantern = false;
    public boolean bedroomLampOn = false;
    public boolean tvOn = false;
    public Entity obj[][] = new Entity[maxMap][120];
    public Entity npc[][] = new Entity[maxMap][10];
    private final ArrayList<Entity> entityList = new ArrayList<>();
    private final Object frameLock = new Object();
    private int lastApartmentRoomId = -1;
    private int apartmentRoomTransitionCounter = 0;
    private String apartmentRoomTransitionTitle = "";
    private final Cursor visibleCursor = Cursor.getDefaultCursor();
    private final Cursor hiddenCursor = createHiddenCursor();

    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int optionsState = 5;
    public final int resultState = 9;
    public final int introState = 10;
    public final int nameInputState = 11;
    public final int finalSceneState = 12;
    public int optionsReturnState = titleState;
    private int optionsReturnCommand = 0;
    public static final int SE_CURSOR = Sound.MENU_CURSOR;
    private static final int FALLBACK_CURSOR_SOUND_INDEX = 10;
    private static final int SWING_SOUND_INDEX = 15;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(false);
        this.setOpaque(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
    }

    public void setupGame() {
        aSetter.setObject();
        aSetter.setNPC();
        gameState = titleState;
        syncMouseCursor();
        if (Main.window != null) {
            Main.window.setIgnoreRepaint(true);
        }

        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        renderScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        forestDarknessBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        syncSoundEffectVolumes();
        preloadCursorSound();
        footstepAudio.preload();

        if (fullScreenOn) {
            syncFullScreenSize();
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
        ui.setOptionsTab(UI.OPTIONS_TAB_GRAPHICS);
        ui.commandNum = 0;
        playConfirmSE();
    }

    public void closeOptionsMenu() {
        config.saveConfig();
        gameState = optionsReturnState;
        ui.commandNum = optionsReturnCommand;
        playBackSE();
    }

    public void startNewGameInSlot(int slot) {
        lastResultReportPath = "";
        resultReportNotice = "";
        resultReportNoticeCounter = 0;
        playerName = "";
        saveLoad.setCurrentSlot(slot);
        story.startNewGame();
        startNameInput();
    }

    public boolean loadGameFromSlot(int slot) {
        if (!saveLoad.load(slot)) {
            return false;
        }
        gameState = playState;
        ui.commandNum = 0;
        return true;
    }

    private void startIntroSequence() {
        introFrame = 0;
        gameState = introState;
        ui.resetIntroAnimation();
        syncMouseCursor();
    }

    public void startFinalScene() {
        finalSceneFrame = 0;
        gameState = finalSceneState;
        ui.resetFinalSceneAnimation();
        syncMouseCursor();
        stopAmbientSounds();
        footstepAudio.stop();
        stopSwingSound();
    }

    private void startNameInput() {
        gameState = nameInputState;
        ui.resetNameInput();
        syncMouseCursor();
    }

    public void confirmPlayerName() {
        String name = ui.getNameInputText().trim();
        if (name.isEmpty()) {
            ui.setNameInputNotice(tr("Введите имя, чтобы продолжить", "Enter a name to continue"));
            playBackSE();
            return;
        }

        setPlayerName(name);
        saveLoad.save();
        playConfirmSE();
        startIntroSequence();
    }

    public void cancelNameInput() {
        gameState = titleState;
        ui.returnToTitleMain();
        syncMouseCursor();
        playBackSE();
    }

    public void setPlayerName(String name) {
        playerName = sanitizePlayerName(name);
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerNameForReport() {
        return playerName.isEmpty() ? tr("Игрок", "Player") : playerName;
    }

    private String sanitizePlayerName(String name) {
        if (name == null) {
            return "";
        }
        String cleaned = name.trim().replaceAll("\\s+", " ");
        if (cleaned.length() > 28) {
            cleaned = cleaned.substring(0, 28).trim();
        }
        return cleaned;
    }

    public void finishIntroSequence() {
        introFrame = INTRO_TOTAL_FRAMES;
        gameState = playState;
        ui.showControlHints();
        syncMouseCursor();
    }

    public boolean saveResultReportPdf() {
        try {
            File file = ResultPdfExporter.export(this);
            lastResultReportPath = file.getAbsolutePath();
            resultReportNotice = tr("PDF сохранён: ", "PDF saved: ") + lastResultReportPath;
            resultReportNoticeCounter = 180;
            return true;
        }
        catch (IOException e) {
            resultReportNotice = tr("Не удалось сохранить PDF: ", "Could not save PDF: ") + e.getMessage();
            resultReportNoticeCounter = 180;
            return false;
        }
    }

    public boolean openResultReportFolder() {
        try {
            if (lastResultReportPath.isEmpty() && !saveResultReportPdf()) {
                return false;
            }

            File folder = ResultPdfExporter.getResultsDirectory();
            if (!openFolder(folder)) {
                throw new IOException("desktop open is not supported");
            }
            resultReportNotice = tr("Папка результатов открыта", "Results folder opened");
            resultReportNoticeCounter = 180;
            return true;
        }
        catch (IOException | UnsupportedOperationException e) {
            resultReportNotice = tr("Не удалось открыть папку результатов: ", "Could not open results folder: ")
                    + e.getMessage();
            resultReportNoticeCounter = 180;
            return false;
        }
    }

    private boolean openFolder(File folder) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(folder);
            return true;
        }
        if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            new ProcessBuilder("explorer", folder.getAbsolutePath()).start();
            return true;
        }
        return false;
    }

    public String getLastResultReportPath() {
        return lastResultReportPath;
    }

    public String getResultReportNotice() {
        return resultReportNotice;
    }

    public int getResultReportNoticeCounter() {
        return resultReportNoticeCounter;
    }

    public void tickResultReportNotice() {
        if (resultReportNoticeCounter > 0) {
            resultReportNoticeCounter--;
        }
    }

    public int getIntroFrame() {
        return introFrame;
    }

    public int getFinalSceneFrame() {
        return finalSceneFrame;
    }

    public int getFinalSceneTotalFrames() {
        return FINAL_SCENE_TOTAL_FRAMES;
    }

    public void finishFinalScene() {
        finalSceneFrame = FINAL_SCENE_TOTAL_FRAMES;
        story.finishFinalSceneResult();
    }

    public int getIntroFlowFrames() {
        return INTRO_MENU_TRANSITION_FRAMES;
    }

    public int getIntroTotalFrames() {
        return INTRO_TOTAL_FRAMES;
    }

    public int getIntroTypeFrames() {
        return INTRO_DISCLAIMER_TYPE_FRAMES;
    }

    public int getIntroHoldFrames() {
        return INTRO_DISCLAIMER_HOLD_FRAMES;
    }

    public int getIntroFadeFrames() {
        return INTRO_GAME_FADE_FRAMES;
    }

    private int getIntroFadeStartFrame() {
        return INTRO_MENU_TRANSITION_FRAMES + INTRO_DISCLAIMER_TYPE_FRAMES + INTRO_DISCLAIMER_HOLD_FRAMES;
    }

    public void changeCurrentOption(int amount) {
        if (ui.isOptionsBackCommand()) {
            return;
        }

        switch (ui.getOptionsTab()) {
            case UI.OPTIONS_TAB_GRAPHICS:
                changeGraphicsOption(ui.commandNum, amount);
                break;
            case UI.OPTIONS_TAB_SOUND:
                changeSoundOption(ui.commandNum, amount);
                break;
            case UI.OPTIONS_TAB_CHAT:
                changeChatOption(ui.commandNum, amount);
                break;
            default:
                break;
        }
        config.saveConfig();
    }

    public void activateCurrentOption() {
        if (ui.isOptionsBackCommand()) {
            closeOptionsMenu();
            return;
        }
        if (ui.getOptionsTab() == UI.OPTIONS_TAB_SOUND) {
            ui.beginSoundOptionEdit();
            return;
        }
        if (ui.getOptionsTab() == UI.OPTIONS_TAB_GRAPHICS && ui.commandNum == 0) {
            toggleFullScreen();
            config.saveConfig();
            return;
        }
        if (ui.getOptionsTab() == UI.OPTIONS_TAB_GRAPHICS && ui.commandNum == 2) {
            ui.beginBrightnessOptionEdit();
            return;
        }
        changeCurrentOption(1);
    }

    private void changeGraphicsOption(int command, int amount) {
        if (command == 0) {
            return;
        }
        else if (command == 1) {
            graphicsMode = cycleSetting(graphicsMode, amount, 3);
            forestDarknessCacheValid = false;
        }
        else if (command == 2) {
            brightnessScale = clampSetting(brightnessScale + amount, 0, 5);
        }
        else if (command == 3) {
            toggleProfilerOverlay();
        }
    }

    public void toggleProfilerOverlay() {
        showFpsCounter = !showFpsCounter;
        config.saveConfig();
    }

    private void changeSoundOption(int command, int amount) {
        if (command == 0) {
            changeSoundEffectVolume(amount);
        }
        else if (command == 1) {
            ambienceVolumeScale = clampVolume(ambienceVolumeScale + amount);
            syncSoundEffectVolumes();
        }
        else if (command == 2) {
            footstepVolumeScale = clampVolume(footstepVolumeScale + amount);
            syncSoundEffectVolumes();
        }
        else if (command == 3) {
            uiVolumeScale = clampVolume(uiVolumeScale + amount);
            syncSoundEffectVolumes();
        }
        else if (command == 4) {
            whisperVolumeScale = clampVolume(whisperVolumeScale + amount);
            syncSoundEffectVolumes();
        }
    }

    private void changeChatOption(int command, int amount) {
        if (command == 0) {
            languageMode = cycleSetting(languageMode, amount, 2);
            ui.revealDialogueTextNow();
            aSetter.setNPC();
        }
        else if (command == 1) {
            dialogueTextSizeMode = cycleSetting(dialogueTextSizeMode, amount, 3);
            ui.revealDialogueTextNow();
        }
        else if (command == 2) {
            dialogueTextSpeedMode = cycleSetting(dialogueTextSpeedMode, amount, 4);
            ui.revealDialogueTextNow();
        }
        else if (command == 3) {
            highContrastDialogue = !highContrastDialogue;
        }
    }

    public void showInitialWindow() {
        if (Main.window == null) {
            return;
        }

        Main.window.setIgnoreRepaint(true);
        if (fullScreenOn) {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            Main.window.setUndecorated(true);
            Main.window.setBounds(gd.getDefaultConfiguration().getBounds());
            Main.window.setVisible(true);
            enterFullScreenMode(gd);
        }
        else {
            Main.window.pack();
            Main.window.setLocationRelativeTo(null);
            Main.window.setVisible(true);
            syncWindowedSize();
        }
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

    public String getGraphicsModeLabel() {
        switch (graphicsMode) {
            case GRAPHICS_QUALITY:
                return tr("Качество", "Quality");
            case GRAPHICS_LAPTOP:
                return tr("Ноутбук", "Laptop");
            default:
                return tr("Баланс", "Balanced");
        }
    }

    public String getLanguageLabel() {
        return languageMode == LANGUAGE_EN ? "English" : "Русский";
    }

    public String getDialogueTextSizeLabel() {
        switch (dialogueTextSizeMode) {
            case 0:
                return tr("Малый", "Small");
            case 2:
                return tr("Крупный", "Large");
            default:
                return tr("Обычный", "Normal");
        }
    }

    public String getDialogueTextSpeedLabel() {
        switch (dialogueTextSpeedMode) {
            case 0:
                return tr("Медленно", "Slow");
            case 2:
                return tr("Быстро", "Fast");
            case 3:
                return tr("Мгновенно", "Instant");
            default:
                return tr("Обычно", "Normal");
        }
    }

    public boolean isEnglish() {
        return languageMode == LANGUAGE_EN;
    }

    public String tr(String ru, String en) {
        return isEnglish() ? en : ru;
    }

    public String tr(String text) {
        return Localization.translate(text, languageMode);
    }

    public int getDialogueTextSizeDelta() {
        switch (dialogueTextSizeMode) {
            case 0:
                return -2;
            case 2:
                return 3;
            default:
                return 0;
        }
    }

    public int getDialogueRevealCharsPerFrame() {
        switch (dialogueTextSpeedMode) {
            case 0:
                return 1;
            case 2:
                return 3;
            case 3:
                return Integer.MAX_VALUE;
            default:
                return 2;
        }
    }

    private int clampVolume(int value) {
        return Math.max(0, Math.min(5, value));
    }

    private int clampSetting(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private int cycleSetting(int value, int amount, int count) {
        int next = (value + amount) % count;
        if (next < 0) {
            next += count;
        }
        return next;
    }

    private void syncSoundEffectVolumes() {
        for (Sound sound : cursorSE) {
            sound.volumeScale = uiVolumeScale;
            sound.checkVolume();
        }
        for (Sound sound : oneShotSE) {
            sound.volumeScale = se.volumeScale;
            sound.checkVolume();
        }
        swingSound.volumeScale = ambienceVolumeScale;
        footstepAudio.syncVolume();
        apartmentAmbienceSound.volumeScale = ambienceVolumeScale;
        whisperSound.volumeScale = whisperVolumeScale;
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
        Main.window.setIgnoreRepaint(true);

        if (fullScreenOn) {
            Main.window.setVisible(true);
            enterFullScreenMode(gd);
        }
        else {
            Main.window.pack();
            Main.window.setLocationRelativeTo(null);
            Main.window.setVisible(true);
            syncWindowedSize();
        }

        requestFocusInWindow();
    }

    private void enterFullScreenMode(GraphicsDevice gd) {
        if (Main.window == null) {
            return;
        }

        Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        screenBufferStrategy = null;
        Main.window.setBounds(bounds);
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(Main.window);
        }

        if (!isWindowScreenSized(bounds)) {
            gd.setFullScreenWindow(null);
            Main.window.setBounds(bounds);
        }

        Main.window.validate();
        syncFullScreenSize();
        Main.window.toFront();
        requestFocusInWindow();
    }

    private boolean isWindowScreenSized(Rectangle bounds) {
        return Main.window != null &&
                Main.window.getWidth() >= bounds.width - 2 &&
                Main.window.getHeight() >= bounds.height - 2;
    }

    private void syncFullScreenSize() {
        if (Main.window == null) {
            syncWindowedSize();
            return;
        }
        screenWidth2 = Math.max(screenWidth, Main.window.getWidth());
        screenHeight2 = Math.max(screenHeight, Main.window.getHeight());
    }

    private void syncWindowedSize() {
        screenWidth2 = screenWidth;
        screenHeight2 = screenHeight;
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
            case MapId.MOUNTAIN:
                return clampCamera(cameraX, 0, maxWorldCol * tileSize - screenWidth);
            case MapId.LIBRARY:
                return clampCamera(cameraX, LIBRARY_LEFT_COL * tileSize,
                        LIBRARY_RIGHT_EXCLUSIVE_COL * tileSize - screenWidth);
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
            case MapId.MOUNTAIN:
                return clampCamera(cameraY, 0, maxWorldRow * tileSize - screenHeight);
            case MapId.LIBRARY:
                return clampCamera(cameraY, LIBRARY_TOP_ROW * tileSize,
                        LIBRARY_BOTTOM_EXCLUSIVE_ROW * tileSize - screenHeight);
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
            return clampCamera(room.leftCol * tileSize, apartmentMin, apartmentMax);
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
        long updateInterval = 1_000_000_000L / FPS;
        long nextUpdateTime = System.nanoTime();
        long nextRenderTime = nextUpdateTime;

        while (gameThread != null) {
            long now = System.nanoTime();
            int updateCount = 0;
            while (now >= nextUpdateTime && updateCount < 5) {
                long updateStart = System.nanoTime();
                update();
                recordUpdateProfile(System.nanoTime() - updateStart);
                nextUpdateTime += updateInterval;
                updateCount++;
            }
            if (updateCount == 5 && nextUpdateTime < now) {
                nextUpdateTime = now + updateInterval;
            }

            long renderInterval = getRenderIntervalNanos();
            if (renderInterval == 0L || now >= nextRenderTime) {
                long renderStart = System.nanoTime();
                if (shouldDrawWorldBuffer()) {
                    drawToTempScreen();
                }
                recordRenderProfile(System.nanoTime() - renderStart);
                long presentStart = System.nanoTime();
                presentFrame();
                recordPresentProfile(System.nanoTime() - presentStart);
                now = System.nanoTime();
                if (renderInterval > 0L) {
                    nextRenderTime += renderInterval;
                    if (nextRenderTime < now - renderInterval) {
                        nextRenderTime = now + renderInterval;
                    }
                }
                else {
                    nextRenderTime = now;
                }
            }

            if (renderInterval == 0L) {
                Thread.yield();
            }
            else {
                waitForNextFrame(Math.min(nextUpdateTime, nextRenderTime));
            }
        }
    }

    private long getRenderIntervalNanos() {
        int renderFps = getRenderFpsLimit();
        return renderFps <= 0 ? 0L : 1_000_000_000L / renderFps;
    }

    private int getRenderFpsLimit() {
        return FPS;
    }

    private void waitForNextFrame(long nextDrawTime) {
        while (gameThread != null) {
            long remainingTime = nextDrawTime - System.nanoTime();
            if (remainingTime <= 0) {
                return;
            }

            if (remainingTime > 150_000L) {
                LockSupport.parkNanos(Math.max(1L, remainingTime - 80_000L));
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
        player.updatePoseState();
        syncMouseCursor();
        if (dialogueTypeCooldown > 0) {
            dialogueTypeCooldown--;
        }

        if (gameState == introState) {
            updateIntroSequence();
        }
        if (gameState == finalSceneState) {
            updateFinalScene();
        }

        if (gameState == playState) {
            player.update();

            for (int i = 0; i < npc[currentMap].length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }

            updateApartmentRoomTransition();
            footstepAudio.update();
            updateSwingSound();
        }
        else {
            footstepAudio.stop();
            stopSwingSound();
        }

        if (gameState == playState || gameState == dialogueState) {
            updateAmbientSounds();
        }
        else {
            stopAmbientSounds();
        }
    }

    private void updateIntroSequence() {
        if (introFrame < INTRO_TOTAL_FRAMES) {
            introFrame++;
        }
        if (introFrame >= INTRO_TOTAL_FRAMES) {
            finishIntroSequence();
        }
    }

    private void updateFinalScene() {
        if (finalSceneFrame < FINAL_SCENE_TOTAL_FRAMES) {
            finalSceneFrame++;
        }
        if (finalSceneFrame >= FINAL_SCENE_TOTAL_FRAMES) {
            story.finishFinalSceneResult();
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
            apartmentAmbienceSound.volumeScale = ambienceVolumeScale;
            if (!apartmentAmbienceSound.setFile(Sound.APARTMENT_AMBIENCE)) {
                apartmentAmbienceUnavailable = true;
                return;
            }
            apartmentAmbienceLoaded = true;
        }

        apartmentAmbienceSound.setVolumeDb(adjustedVolume(ambienceVolumeScale, -18f));
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
            whisperSound.volumeScale = whisperVolumeScale;
            if (!whisperSound.setFile(Sound.WHISPERS)) {
                whisperSoundUnavailable = true;
                return;
            }
            whisperSoundLoaded = true;
        }

        whisperSound.setVolumeDb(adjustedVolume(whisperVolumeScale, -23f));
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

    private float adjustedVolume(int volumeScale, float offsetDb) {
        return Math.max(-80f, Math.min(6f, Sound.volumeScaleToDb(volumeScale) + offsetDb));
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
        float baseVolume = Sound.volumeScaleToDb(ambienceVolumeScale);
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
        super.paintComponent(graphics);
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, getWidth(), getHeight());
    }

    private void renderWorldLayer(Graphics2D graphics) {
        clearFrame(graphics);

        if (gameState == titleState || gameState == nameInputState) {
            return;
        }
        if (gameState == optionsState && optionsReturnState != pauseState) {
            return;
        }
        if (gameState == introState && introFrame < getIntroFadeStartFrame()) {
            return;
        }

        drawGameWorld(graphics);
        drawApartmentRoomVisibility(graphics);
        drawApartmentObjectLights(graphics);
        drawForestMood(graphics);
        drawBrightnessOverlay(graphics);
    }

    private void prepareWorldGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    private void prepareScreenImageGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    private void prepareUiGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, graphicsMode == GRAPHICS_QUALITY
                ? RenderingHints.VALUE_ANTIALIAS_ON
                : RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
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

        for (int i = 0; i < obj[currentMap].length; i++) {
            if (obj[currentMap][i] != null && obj[currentMap][i].isFloorLayer() &&
                    obj[currentMap][i].isVisibleInCamera()) {
                obj[currentMap][i].draw(g2);
            }
        }

        entityList.add(player);

        for (int i = 0; i < npc[currentMap].length; i++) {
            if (npc[currentMap][i] != null && npc[currentMap][i].isVisibleInCamera()) {
                entityList.add(npc[currentMap][i]);
            }
        }

        for (int i = 0; i < obj[currentMap].length; i++) {
            if (obj[currentMap][i] != null && !obj[currentMap][i].isFloorLayer() &&
                    obj[currentMap][i].isVisibleInCamera()) {
                entityList.add(obj[currentMap][i]);
            }
        }

        entityList.sort(Comparator.comparingInt(Entity::getRenderSortY));

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

    private void drawApartmentObjectLights(Graphics2D g2) {
        if (currentMap != MapId.APARTMENT) {
            return;
        }
        if (graphicsMode == GRAPHICS_LAPTOP) {
            return;
        }

        ApartmentRoom room = getCurrentApartmentRoom();
        if (room == null) {
            return;
        }

        if (bedroomLampOn && room.id == ROOM_BEDROOM) {
            drawBedroomLampLight(g2);
        }
    }

    private void drawBedroomLampLight(Graphics2D g2) {
        if (bedroomLampGlowImage == null) {
            bedroomLampGlowImage = createRadialImage(tileSize * 3.2f,
                    alphaColor(255, 225, 142, 135, 1f),
                    alphaColor(226, 151, 64, 58, 1f),
                    new Color(226, 151, 64, 0));
        }

        Point lampLight = getObjectLightPoint("Bedroom Lamp");
        if (lampLight != null) {
            drawCenteredImage(g2, bedroomLampGlowImage, lampLight.x, lampLight.y);
        }
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
        int roomX = worldToScreenX(leftCol * tileSize);
        int roomY = worldToScreenY(topRow * tileSize);
        int roomWidth = (rightExclusiveCol - leftCol) * tileSize;
        int roomHeight = (bottomExclusiveRow - topRow) * tileSize;

        int roomLeft = Math.max(0, roomX);
        int roomTop = Math.max(0, roomY);
        int roomRight = Math.min(screenWidth, roomX + roomWidth);
        int roomBottom = Math.min(screenHeight, roomY + roomHeight);

        if (roomTop > 0) {
            g2.fillRect(0, 0, screenWidth, roomTop);
        }
        if (roomBottom < screenHeight) {
            g2.fillRect(0, roomBottom, screenWidth, screenHeight - roomBottom);
        }
        if (roomLeft > 0 && roomBottom > roomTop) {
            g2.fillRect(0, roomTop, roomLeft, roomBottom - roomTop);
        }
        if (roomRight < screenWidth && roomBottom > roomTop) {
            g2.fillRect(roomRight, roomTop, screenWidth - roomRight, roomBottom - roomTop);
        }
    }

    private void drawForestMood(Graphics2D g2) {
        if (currentMap != MapId.FOREST_DOUBTS) {
            return;
        }

        if (graphicsMode == GRAPHICS_LAPTOP) {
            drawLaptopForestMood(g2);
            return;
        }

        ensureForestEffectBuffers();
        drawForestDarkness(g2);
        drawForestVignette(g2);
    }

    private void drawLaptopForestMood(Graphics2D g2) {
        int darknessAlpha = hasLantern ? 132 : 226;
        darknessAlpha = clampSetting(darknessAlpha + (3 - brightnessScale) * 14, 92, 242);
        Composite oldComposite = g2.getComposite();
        g2.setColor(new Color(2, 7, 9, darknessAlpha));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setComposite(oldComposite);
    }

    private void drawForestDarkness(Graphics2D g2) {
        if (forestDarknessBuffer == null) {
            forestDarknessBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
            forestDarknessCacheValid = false;
        }

        int darknessAlpha = hasLantern ? 166 : 238;
        darknessAlpha = clampSetting(darknessAlpha + (3 - brightnessScale) * 14, 110, 248);
        int lightX = worldToScreenX(player.worldX) + tileSize / 2;
        int lightY = worldToScreenY(player.worldY) + tileSize / 2;
        Point lanternLight = getForestLanternLightPoint();
        int lanternX = lanternLight != null ? lanternLight.x : Integer.MIN_VALUE;
        int lanternY = lanternLight != null ? lanternLight.y : Integer.MIN_VALUE;

        if (!isForestDarknessCacheFresh(darknessAlpha, lightX, lightY, lanternX, lanternY)) {
            Graphics2D shadow = forestDarknessBuffer.createGraphics();
            shadow.setComposite(AlphaComposite.Src);
            shadow.setColor(new Color(2, 7, 9, darknessAlpha));
            shadow.fillRect(0, 0, screenWidth, screenHeight);

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
            rememberForestDarknessCache(darknessAlpha, lightX, lightY, lanternX, lanternY);
        }

        g2.drawImage(forestDarknessBuffer, 0, 0, null);

        if (hasLantern) {
            drawCenteredImage(g2, playerGlowImage, lightX, lightY);
        }
        else if (lanternLight != null) {
            drawCenteredImage(g2, lanternGlowImage, lanternLight.x, lanternLight.y);
        }
    }

    private boolean isForestDarknessCacheFresh(int darknessAlpha, int lightX, int lightY, int lanternX, int lanternY) {
        return forestDarknessCacheValid &&
                forestDarknessCacheHasLantern == hasLantern &&
                forestDarknessCacheBrightness == darknessAlpha &&
                forestDarknessCacheLightX == lightX &&
                forestDarknessCacheLightY == lightY &&
                forestDarknessCacheLanternX == lanternX &&
                forestDarknessCacheLanternY == lanternY;
    }

    private void rememberForestDarknessCache(int darknessAlpha, int lightX, int lightY, int lanternX, int lanternY) {
        forestDarknessCacheValid = true;
        forestDarknessCacheHasLantern = hasLantern;
        forestDarknessCacheBrightness = darknessAlpha;
        forestDarknessCacheLightX = lightX;
        forestDarknessCacheLightY = lightY;
        forestDarknessCacheLanternX = lanternX;
        forestDarknessCacheLanternY = lanternY;
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

    private void drawBrightnessOverlay(Graphics2D g2) {
        if (brightnessScale == 3) {
            return;
        }

        Composite oldComposite = g2.getComposite();
        if (brightnessScale < 3) {
            int alpha = (3 - brightnessScale) * 22;
            g2.setColor(new Color(0, 0, 0, alpha));
        }
        else {
            int alpha = (brightnessScale - 3) * 12;
            g2.setColor(new Color(255, 245, 220, alpha));
        }
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setComposite(oldComposite);
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

    private Point getObjectLightPoint(String objectName) {
        for (int i = 0; i < obj[currentMap].length; i++) {
            Entity object = obj[currentMap][i];
            if (object != null && objectName.equals(object.name)) {
                int centerX = object.worldX + object.solidArea.x + object.solidArea.width / 2;
                int centerY = object.worldY + object.solidArea.y + object.solidArea.height / 2;
                return new Point(worldToScreenX(centerX), worldToScreenY(centerY));
            }
        }
        return null;
    }

    private void presentFrame() {
        if (shouldUseActiveRender()) {
            if (drawWithBufferStrategy()) {
                return;
            }
            repaint();
        }
        else {
            repaint();
        }
    }

    private boolean shouldUseActiveRender() {
        return Main.window != null && Main.window.isVisible();
    }

    private boolean drawWithBufferStrategy() {
        if (!ensureScreenBufferStrategy()) {
            return false;
        }

        try {
            do {
                do {
                    Graphics2D screenGraphics = (Graphics2D) screenBufferStrategy.getDrawGraphics();
                    try {
                        drawFrameToScreen(screenGraphics, getFrameContentArea());
                    }
                    finally {
                        screenGraphics.dispose();
                    }
                } while (screenBufferStrategy.contentsRestored());

                screenBufferStrategy.show();
                if (SYNC_TOOLKIT_AFTER_PRESENT) {
                    Toolkit.getDefaultToolkit().sync();
                }
            } while (screenBufferStrategy.contentsLost());
            return true;
        }
        catch (RuntimeException e) {
            screenBufferStrategy = null;
            return false;
        }
    }

    private boolean ensureScreenBufferStrategy() {
        if (screenBufferStrategy != null) {
            return true;
        }
        if (Main.window == null || !Main.window.isDisplayable() || !Main.window.isShowing() ||
                Main.window.getWidth() <= 0 || Main.window.getHeight() <= 0) {
            return false;
        }

        try {
            Main.window.createBufferStrategy(2);
            screenBufferStrategy = Main.window.getBufferStrategy();
            return screenBufferStrategy != null;
        }
        catch (RuntimeException e) {
            screenBufferStrategy = null;
            return false;
        }
    }

    private Rectangle getFrameContentArea() {
        if (Main.window == null) {
            return new Rectangle(0, 0, screenWidth2, screenHeight2);
        }

        Insets insets = Main.window.getInsets();
        int width = Main.window.getWidth() - insets.left - insets.right;
        int height = Main.window.getHeight() - insets.top - insets.bottom;
        if (width <= 0 || height <= 0) {
            width = screenWidth2;
            height = screenHeight2;
        }
        return new Rectangle(Math.max(0, insets.left), Math.max(0, insets.top), width, height);
    }

    private void drawFrameToScreen(Graphics2D screenGraphics, Rectangle renderArea) {
        int frameWidth = Main.window != null ? Main.window.getWidth() : screenWidth2;
        int frameHeight = Main.window != null ? Main.window.getHeight() : screenHeight2;
        screenGraphics.setColor(Color.black);
        screenGraphics.fillRect(0, 0, frameWidth, frameHeight);

        int targetWidth = renderArea.width;
        int targetHeight = renderArea.height;
        if (targetWidth <= 0 || targetHeight <= 0) {
            targetWidth = screenWidth2;
            targetHeight = screenHeight2;
        }

        double scale = getScreenDrawScale(targetWidth, targetHeight);
        int drawWidth = Math.max(1, (int) Math.ceil(screenWidth * scale));
        int drawHeight = Math.max(1, (int) Math.ceil(screenHeight * scale));
        int drawX = renderArea.x + (targetWidth - drawWidth) / 2;
        int drawY = renderArea.y + (targetHeight - drawHeight) / 2;

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
        drawFpsCounter(frameGraphics);
        frameGraphics.dispose();
        recordPresentedFrame();
    }

    public Point toGameScreenPoint(int componentX, int componentY) {
        int targetWidth = getWidth() > 0 ? getWidth() : screenWidth2;
        int targetHeight = getHeight() > 0 ? getHeight() : screenHeight2;
        if (targetWidth <= 0 || targetHeight <= 0) {
            targetWidth = screenWidth;
            targetHeight = screenHeight;
        }

        double scale = getScreenDrawScale(targetWidth, targetHeight);
        int drawWidth = Math.max(1, (int) Math.ceil(screenWidth * scale));
        int drawHeight = Math.max(1, (int) Math.ceil(screenHeight * scale));
        int drawX = (targetWidth - drawWidth) / 2;
        int drawY = (targetHeight - drawHeight) / 2;

        int gameX = (int) Math.floor((componentX - drawX) / scale);
        int gameY = (int) Math.floor((componentY - drawY) / scale);
        return new Point(gameX, gameY);
    }

    private double getScreenDrawScale(int targetWidth, int targetHeight) {
        double coverScale = Math.max((double) targetWidth / screenWidth, (double) targetHeight / screenHeight);
        if (graphicsMode == GRAPHICS_LAPTOP) {
            double containScale = Math.min((double) targetWidth / screenWidth, (double) targetHeight / screenHeight);
            return Math.max(1.0, containScale);
        }
        if (graphicsMode == GRAPHICS_QUALITY) {
            return Math.max(1.0, Math.ceil(coverScale));
        }
        return Math.max(1.0, coverScale);
    }

    public boolean shouldShowMouseCursor() {
        return gameState == titleState ||
                gameState == nameInputState ||
                gameState == pauseState ||
                gameState == optionsState ||
                gameState == resultState;
    }

    public void syncMouseCursor() {
        Cursor targetCursor = shouldShowMouseCursor() ? visibleCursor : hiddenCursor;
        if (getCursor() != targetCursor) {
            setCursor(targetCursor);
        }
    }

    private Cursor createHiddenCursor() {
        try {
            BufferedImage cursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "hidden");
        }
        catch (RuntimeException e) {
            return Cursor.getDefaultCursor();
        }
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
        g2.setColor(new Color(0, 0, 0, alpha));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setComposite(oldComposite);
    }

    private boolean shouldDrawWorldBuffer() {
        if (gameState == titleState || gameState == nameInputState ||
                (gameState == optionsState && optionsReturnState != pauseState)) {
            return false;
        }
        return gameState != introState || introFrame >= getIntroFadeStartFrame();
    }

    private void drawFpsCounter(Graphics2D g2) {
        if (!showFpsCounter) {
            return;
        }

        String text = String.format(Locale.ROOT, "FPS %d", currentFps);
        Font font = GameFonts.bold(15);
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();
        int width = metrics.stringWidth(text) + 22;
        int height = 28;
        int x = screenWidth - width - 14;
        int y = 14;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(x, y, width, height, 10, 10);
        g2.setColor(new Color(174, 215, 196, 200));
        g2.drawRoundRect(x + 1, y + 1, width - 2, height - 2, 9, 9);
        g2.setColor(Color.white);
        g2.drawString(text, x + 11, y + 19);
    }

    private void recordUpdateProfile(long nanos) {
        updateNanosThisSecond += nanos;
        updateSamplesThisSecond++;
    }

    private void recordRenderProfile(long nanos) {
        renderNanosThisSecond += nanos;
        renderSamplesThisSecond++;
    }

    private void recordPresentProfile(long nanos) {
        presentNanosThisSecond += nanos;
        presentSamplesThisSecond++;
    }

    private void recordPresentedFrame() {
        framesThisSecond++;
        long now = System.nanoTime();
        if (now - fpsSampleStartNanos >= 1_000_000_000L) {
            currentFps = framesThisSecond;
            currentUpdateMs = nanosToAverageMs(updateNanosThisSecond, updateSamplesThisSecond);
            currentRenderMs = nanosToAverageMs(renderNanosThisSecond, renderSamplesThisSecond);
            currentPresentMs = nanosToAverageMs(presentNanosThisSecond, presentSamplesThisSecond);
            Runtime runtime = Runtime.getRuntime();
            currentMemoryMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L);
            framesThisSecond = 0;
            updateNanosThisSecond = 0L;
            renderNanosThisSecond = 0L;
            presentNanosThisSecond = 0L;
            updateSamplesThisSecond = 0;
            renderSamplesThisSecond = 0;
            presentSamplesThisSecond = 0;
            fpsSampleStartNanos = now;
        }
    }

    private double nanosToAverageMs(long nanos, int samples) {
        if (samples <= 0) {
            return 0.0;
        }
        return nanos / (double) samples / 1_000_000.0;
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
        return playOneShot(i, getSoundEffectVolume());
    }

    private int playOneShot(int i, int volumeScale) {
        Sound sound = oneShotSE[oneShotSECursor];
        oneShotSECursor = (oneShotSECursor + 1) % oneShotSE.length;
        sound.volumeScale = volumeScale;
        if (sound.setFile(i)) {
            int durationFrames = sound.getDurationFrames(FPS);
            sound.play();
            return durationFrames;
        }
        return 0;
    }

    public void playConfirmSE() {
        playOneShot(Sound.MENU_CONFIRM, uiVolumeScale);
    }

    public void playBackSE() {
        playOneShot(Sound.MENU_BACK, uiVolumeScale);
    }

    public void playCursorSE() {
        if (!cursorSoundLoaded && !cursorSoundUnavailable) {
            preloadCursorSound();
        }

        if (cursorSoundLoaded) {
            Sound sound = cursorSE[cursorSECursor];
            cursorSECursor = (cursorSECursor + 1) % cursorSE.length;
            sound.volumeScale = uiVolumeScale;
            sound.checkVolume();
            sound.playFromStart();
        }
        else {
            playOneShot(FALLBACK_CURSOR_SOUND_INDEX, uiVolumeScale);
        }
    }

    public void playDialogueTypeSE() {
        if (dialogueTypeCooldown > 0 || dialogueTextSpeedMode == 3) {
            return;
        }
        playOneShot(Sound.DIALOGUE_TYPE, uiVolumeScale);
        dialogueTypeCooldown = DIALOGUE_TYPE_INTERVAL_FRAMES;
    }

    private void preloadCursorSound() {
        cursorSoundLoaded = true;
        for (Sound sound : cursorSE) {
            sound.volumeScale = uiVolumeScale;
            if (!sound.setFile(SE_CURSOR)) {
                cursorSoundLoaded = false;
                break;
            }
        }

        if (!cursorSoundLoaded) {
            for (Sound sound : cursorSE) {
                sound.close();
            }
            cursorSoundUnavailable = true;
        }
    }

}
