package test;

import entity.Entity;
import entity.SwingChildNPC;
import main.GamePanel;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestLogic {

    public static void main(String[] args) {
        File saveFile = new File("save.dat");
        boolean hadSave = saveFile.exists();
        byte[] saveBackup = readBackup(saveFile);
        File configFile = new File("config.txt");
        boolean hadConfig = configFile.exists();
        byte[] configBackup = readBackup(configFile);

        try {
            runTest("Тест 1: стартовые метрики", TestLogic::testInitialMetrics);
            runTest("Тест 2: профиль роста", TestLogic::testGrowthProfile);
            runTest("Тест 3: стартовая цель", TestLogic::testInitialObjective);
            runTest("Тест 4: первый сюжетный переход", TestLogic::testFirstStoryStep);
            runTest("Test 5: apartment wall collisions", TestLogic::testApartmentWallCollisions);
            runTest("Test 6: apartment bed object", TestLogic::testApartmentBedObject);
            runTest("Test 7: apartment camera bounds", TestLogic::testApartmentCameraBounds);
            runTest("Test 8: forest doubts map", TestLogic::testForestDoubtsMap);
            runTest("Test 9: pause menu controls", TestLogic::testPauseMenuControls);
            runTest("Test 10: forest lantern pickup", TestLogic::testForestLanternPickup);
            runTest("Test 11: village map layout", TestLogic::testVillageMapLayout);
            runTest("Test 12: settings menu controls", TestLogic::testSettingsMenuControls);
        }
        finally {
            restoreFile(saveFile, hadSave, saveBackup);
            restoreFile(configFile, hadConfig, configBackup);
        }
    }

    public static void runTest(String testName, Runnable testMethod) {
        System.out.print(testName + "... ");
        try {
            testMethod.run();
            System.out.println("PASSED");
        }
        catch (AssertionError e) {
            System.out.println("FAILED");
            System.out.println("   Причина: " + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
    }

    private static byte[] readBackup(File file) {
        if (!file.exists()) {
            return null;
        }
        try {
            return Files.readAllBytes(file.toPath());
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot back up " + file.getName() + " before tests", e);
        }
    }

    private static void restoreFile(File file, boolean hadFile, byte[] backup) {
        try {
            if (hadFile && backup != null) {
                Files.write(file.toPath(), backup);
            }
            else if (!hadFile && file.exists()) {
                Files.delete(file.toPath());
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot restore " + file.getName() + " after tests", e);
        }
    }

    public static void testInitialMetrics() {
        GamePanel gp = new GamePanel();

        assertEquals(35, gp.story.growth, "Рост должен начинаться с 35");
        assertEquals(35, gp.story.calm, "Покой должен начинаться с 35");
        assertEquals(35, gp.story.empathy, "Эмпатия должна начинаться с 35");
        assertEquals(35, gp.story.confidence, "Уверенность должна начинаться с 35");
    }

    public static void testGrowthProfile() {
        GamePanel gp = new GamePanel();
        gp.story.growth = 90;
        gp.story.calm = 45;
        gp.story.empathy = 50;
        gp.story.confidence = 55;

        if (!"Путь роста".equals(gp.story.getProfileTitle())) {
            throw new AssertionError("Ожидался профиль роста, получено: " + gp.story.getProfileTitle());
        }
    }

    public static void testInitialObjective() {
        GamePanel gp = new GamePanel();
        String objective = gp.story.getObjective();

        if (!objective.contains("кровать")) {
            throw new AssertionError("Стартовая цель должна начинаться с бытового задания, получено: " + objective);
        }
    }

    public static void testFirstStoryStep() {
        GamePanel gp = new GamePanel();
        gp.setupGame();

        if (gp.npc[0][0] != null) {
            throw new AssertionError("Тень не должна появляться до домашних заданий");
        }

        gp.story.interactObject("Bed");
        if (gp.story.canContinueDialogue()) {
            throw new AssertionError("Bed interaction must stay open until its sound ends");
        }
        finishLockedDialogue(gp);
        if (!gp.story.getObjective().contains("кухне")) {
            throw new AssertionError("После кровати цель должна вести на кухню, получено: " + gp.story.getObjective());
        }

        gp.story.interactObject("Kitchen Table");
        finishLockedDialogue(gp);
        if (!gp.story.getObjective().contains("санузле")) {
            throw new AssertionError("После кухни цель должна вести в санузел, получено: " + gp.story.getObjective());
        }

        gp.story.interactObject("Bathroom Mirror");
        finishLockedDialogue(gp);
        if (!gp.story.getObjective().contains("зале")) {
            throw new AssertionError("После санузла цель должна вести в зал, получено: " + gp.story.getObjective());
        }

        gp.story.interactObject("Sofa");
        finishLockedDialogue(gp);
        if (gp.npc[0][0] == null) {
            throw new AssertionError("Тень должна появиться после домашней рутины");
        }
        if (!gp.story.shouldPlayApartmentWhispers()) {
            throw new AssertionError("Apartment whispers must play before the shadow conversation starts");
        }

        gp.story.interact("shadow_apartment");
        if (gp.story.shouldPlayApartmentWhispers()) {
            throw new AssertionError("Apartment whispers must stop after starting the shadow conversation");
        }
        gp.story.chooseSelected();

        if (!gp.story.hasChoices()) {
            throw new AssertionError("После первого ответа должен открыться второй вопрос Тени");
        }

        gp.story.chooseSelected();
        String objective = gp.story.getObjective();

        if (!objective.contains("Ребёнка")) {
            throw new AssertionError("После ответа Тени цель должна вести к Ребёнку, получено: " + objective);
        }

        gp.story.continueDialogue();
        assertEquals(1, gp.currentMap, "After the apartment shadow, player must enter the forest");
        assertEquals(gp.tileSize * 23, gp.player.worldX, "Forest entrance player X position");
        assertEquals(gp.tileSize * 43, gp.player.worldY, "Forest entrance player Y position");
        if (!gp.ui.isCheckpointNoticeVisible()) {
            throw new AssertionError("Opening a new location must show the checkpoint save notice");
        }
    }

    public static void testApartmentWallCollisions() {
        GamePanel gp = new GamePanel();

        assertBlocked(gp, 14, 6, "Top apartment wall");
        assertBlocked(gp, 14, 24, "Bottom apartment wall");
        assertBlocked(gp, 5, 14, "Left apartment wall");
        assertBlocked(gp, 37, 14, "Right apartment wall");
        assertWalkable(gp, 14, 14, "Apartment floor");
        assertBlocked(gp, 22, 9, "Bedroom right wall");
        assertWalkable(gp, 22, 11, "Bedroom doorway");
        assertBlocked(gp, 10, 15, "Bedroom bottom wall");
        assertBlocked(gp, 22, 18, "Lower-left room partition");
        assertWalkable(gp, 22, 20, "Lower-left room doorway");
        assertBlocked(gp, 26, 9, "Living room partition");
        assertWalkable(gp, 26, 11, "Living room doorway");
        assertBlocked(gp, 30, 15, "Right room split wall");
        assertWalkable(gp, 26, 20, "Right lower room doorway");

        assertPlayerBlocked(gp, gp.tileSize * 14, gp.tileSize * 7 - gp.player.solidArea.y, "up", "Top boundary");
        assertPlayerBlocked(gp, gp.tileSize * 14,
                gp.tileSize * 24 - gp.player.solidArea.y - gp.player.solidArea.height - gp.player.speed + 1,
                "down", "Bottom boundary");
        assertPlayerBlocked(gp, gp.tileSize * 6 - gp.player.solidArea.x, gp.tileSize * 14, "left", "Left boundary");
        assertPlayerBlocked(gp, gp.tileSize * 37 - gp.player.solidArea.x - gp.player.solidArea.width - gp.player.speed + 1,
                gp.tileSize * 14, "right", "Right boundary");
    }

    public static void testApartmentBedObject() {
        GamePanel gp = new GamePanel();
        gp.setupGame();

        if (gp.obj[0][0] == null) {
            throw new AssertionError("Apartment must contain a bed object");
        }
        if (!gp.obj[0][0].collision) {
            throw new AssertionError("Bed must block movement");
        }

        assertEquals(gp.tileSize * 19, gp.obj[0][0].worldX, "Bed X position");
        assertEquals(gp.tileSize * 8, gp.obj[0][0].worldY, "Bed Y position");
        assertEquals(gp.tileSize * 3 / 4, gp.obj[0][0].solidArea.x, "Bed collision X offset");
        assertEquals(gp.tileSize / 4, gp.obj[0][0].solidArea.y, "Bed collision Y offset");
        assertEquals(gp.tileSize + gp.tileSize / 12, gp.obj[0][0].solidArea.width, "Bed collision width");
        assertEquals(gp.tileSize + gp.tileSize * 5 / 8, gp.obj[0][0].solidArea.height, "Bed collision height");
        assertObjectCollisionAtBedEdge(gp);

        if (gp.obj[0][1] == null) {
            throw new AssertionError("Apartment must contain a mirror object");
        }
        if (!gp.obj[0][1].collision) {
            throw new AssertionError("Mirror must block its visible body");
        }
        assertEquals(gp.tileSize * 7, gp.obj[0][1].worldX, "Mirror X position");
        assertEquals(gp.tileSize * 8, gp.obj[0][1].worldY, "Mirror Y position");
        assertEquals(gp.tileSize / 3, gp.obj[0][1].solidArea.x, "Mirror collision X offset");
        assertEquals(gp.tileSize / 3, gp.obj[0][1].solidArea.y, "Mirror collision Y offset");
        assertEquals(gp.tileSize, gp.obj[0][1].solidArea.width, "Mirror collision width");
        assertEquals(gp.tileSize * 13 / 8, gp.obj[0][1].solidArea.height, "Mirror collision height");
        assertObjectBlocksFromTop(gp, gp.obj[0][1], "Mirror top");
        assertObjectBlocksFromLeft(gp, gp.obj[0][1], "Mirror left side");
    }

    public static void testApartmentCameraBounds() {
        GamePanel gp = new GamePanel();

        gp.player.worldX = gp.tileSize * 6;
        assertEquals(gp.tileSize * 5, gp.getCameraX(), "Apartment camera must stop at the left wall");

        gp.player.worldX = gp.tileSize * 9;
        gp.player.worldY = gp.tileSize * 12;
        assertEquals(gp.tileSize * 5, gp.getCameraX(), "Bedroom camera must fill the screen horizontally");
        assertEquals(gp.tileSize * 6, gp.getCameraY(), "Bedroom camera must fill the screen vertically");

        gp.player.worldX = gp.tileSize * 36;
        gp.player.worldY = gp.tileSize * 20;
        assertEquals(gp.tileSize * 38 - gp.screenWidth, gp.getCameraX(), "Apartment camera must stop at the right wall");

        gp.player.worldY = gp.tileSize * 7;
        assertEquals(gp.tileSize * 6, gp.getCameraY(), "Apartment camera must stop at the top wall");

        gp.player.worldY = gp.tileSize * 24;
        assertEquals(gp.tileSize * 25 - gp.screenHeight, gp.getCameraY(), "Apartment camera must stop at the bottom wall");
    }

    public static void testForestDoubtsMap() {
        GamePanel gp = new GamePanel();
        gp.setupGame();

        assertEquals(51, gp.tileM.mapTileNum[1][23][43], "Forest entrance path tile");
        assertEquals(51, gp.tileM.mapTileNum[1][30][8], "Child endpoint path tile");
        assertEquals(51, gp.tileM.mapTileNum[1][16][36], "Forest lower bend path tile");
        assertEquals(51, gp.tileM.mapTileNum[1][27][29], "Forest middle bend path tile");
        assertEquals(51, gp.tileM.mapTileNum[1][31][21], "Shadow clearing path tile");
        assertBlocked(gp, 1, 1, 1, "Forest outer edge");
        assertWalkable(gp, 1, 7, 7, "Forest interior must not contain bush scrap tiles");

        if (gp.obj[1][0] == null || gp.obj[1][19] == null) {
            throw new AssertionError("Forest doubts must contain edge tree objects");
        }
        assertEquals(gp.tileSize * 5, gp.obj[1][0].worldX, "First forest edge tree X position");
        assertEquals(gp.tileSize * 4, gp.obj[1][0].worldY, "First forest edge tree Y position");
        assertEquals(gp.tileSize * 30, gp.npc[1][0].worldX, "Child endpoint X position");
        assertEquals(gp.tileSize * 8, gp.npc[1][0].worldY, "Child endpoint Y position");
        if (!(gp.npc[1][0] instanceof SwingChildNPC)) {
            throw new AssertionError("Forest child must use the animated swing NPC");
        }
        if (countObjectsByPrefix(gp, 1, "tree_") < 45) {
            throw new AssertionError("Forest doubts must contain a dense natural tree layout");
        }
        if (countObjectsByPrefix(gp, 1, "decoration_") < 20) {
            throw new AssertionError("Forest doubts must contain decoration objects");
        }
        assertForestUsesOnlyTree05And11(gp);
        assertForestDecorationsAreNonBlocking(gp);
        assertForestObjectsDoNotBlockPath(gp);
        assertTreeLeavesBlockMovement(gp);

        gp.currentMap = 1;
        gp.player.worldX = gp.tileSize * 4;
        assertEquals(gp.tileSize * 4, gp.getCameraX(), "Forest camera must stop at the left edge");

        gp.player.worldX = gp.tileSize * 45;
        assertEquals(gp.tileSize * 46 - gp.screenWidth, gp.getCameraX(), "Forest camera must stop at the right edge");

        gp.player.worldY = gp.tileSize * 4;
        assertEquals(gp.tileSize * 4, gp.getCameraY(), "Forest camera must stop at the top edge");

        gp.player.worldY = gp.tileSize * 45;
        assertEquals(gp.tileSize * 46 - gp.screenHeight, gp.getCameraY(), "Forest camera must stop at the bottom edge");
    }

    public static void testPauseMenuControls() {
        GamePanel gp = new GamePanel();
        gp.setupGame();
        gp.gameState = gp.playState;
        gp.ui.commandNum = 3;

        gp.keyH.playState(KeyEvent.VK_ESCAPE);
        assertEquals(gp.pauseState, gp.gameState, "Escape must open pause menu");
        assertEquals(0, gp.ui.commandNum, "Pause menu must open on Continue");

        gp.keyH.pauseState(KeyEvent.VK_UP);
        assertEquals(5, gp.ui.commandNum, "Pause menu selection must wrap upward");

        gp.keyH.pauseState(KeyEvent.VK_DOWN);
        assertEquals(0, gp.ui.commandNum, "Pause menu selection must wrap downward");

        gp.keyH.pauseState(KeyEvent.VK_ESCAPE);
        assertEquals(gp.playState, gp.gameState, "Escape must close pause menu");

        gp.keyH.playState(KeyEvent.VK_ESCAPE);
        gp.keyH.pauseState(KeyEvent.VK_DOWN);
        gp.keyH.pauseState(KeyEvent.VK_DOWN);
        gp.keyH.pauseState(KeyEvent.VK_DOWN);
        gp.keyH.pauseState(KeyEvent.VK_DOWN);
        gp.keyH.pauseState(KeyEvent.VK_DOWN);
        gp.keyH.pauseState(KeyEvent.VK_ENTER);
        assertEquals(gp.titleState, gp.gameState, "Main menu pause command must return to title");
        assertEquals(0, gp.ui.commandNum, "Title menu command must reset after leaving pause");
    }

    public static void testForestLanternPickup() {
        GamePanel gp = new GamePanel();
        gp.setupGame();
        gp.currentMap = 1;
        gp.gameState = gp.playState;

        int lanternIndex = findObject(gp, 1, "Lantern");
        if (lanternIndex == 999) {
            throw new AssertionError("Forest doubts must place the lantern near the entrance");
        }
        assertEquals(gp.tileSize * 23, gp.obj[1][lanternIndex].worldX, "Lantern X position");
        assertEquals(gp.tileSize * 41, gp.obj[1][lanternIndex].worldY, "Lantern Y position");
        if (gp.obj[1][lanternIndex].collision) {
            throw new AssertionError("Lantern must be walkable so the player can pick it up");
        }

        int lanternTop = gp.obj[1][lanternIndex].worldY + gp.obj[1][lanternIndex].solidArea.y;
        gp.player.worldX = gp.obj[1][lanternIndex].worldX + gp.obj[1][lanternIndex].solidArea.x - gp.player.solidArea.x;
        gp.player.worldY = lanternTop - gp.player.solidArea.y - gp.player.solidArea.height - gp.player.speed + 1;
        gp.player.direction = "down";
        gp.keyH.downPressed = true;
        gp.player.update();
        gp.keyH.downPressed = false;

        if (!gp.hasLantern) {
            throw new AssertionError("Touching the lantern must add it to the player");
        }
        if (gp.obj[1][lanternIndex] != null) {
            throw new AssertionError("Picked up lantern must disappear from the forest");
        }

        gp.aSetter.setObject();
        if (findObject(gp, 1, "Lantern") != 999) {
            throw new AssertionError("Lantern must not respawn after pickup");
        }
    }

    public static void testVillageMapLayout() {
        GamePanel gp = new GamePanel();
        gp.setupGame();
        gp.currentMap = 2;

        assertBlocked(gp, 2, 0, 0, "Village outer edge");
        assertBlocked(gp, 2, 49, 25, "Village right edge");
        assertWalkable(gp, 2, 23, 15, "Village entrance road");
        assertWalkable(gp, 2, 15, 16, "Friend house doorway");
        assertWalkable(gp, 2, 36, 14, "Library entrance");

        if (countObjectsByPrefix(gp, 2, "village_house") < 6) {
            throw new AssertionError("Village must contain several house objects");
        }
        if (findObject(gp, 2, "village_library") == 999) {
            throw new AssertionError("Village must contain the library building");
        }

        assertEquals(gp.tileSize * 13, gp.npc[2][0].worldX, "Friend must stand inside the friend house");
        assertEquals(gp.tileSize * 14, gp.npc[2][0].worldY, "Friend must stand inside the friend house doorway");
        assertEquals(gp.tileSize * 36, gp.npc[2][1].worldX, "Elder must stand near the library");
        assertEquals(gp.tileSize * 14, gp.npc[2][1].worldY, "Elder must stand near the library entrance");

        gp.player.worldX = 0;
        assertEquals(0, gp.getCameraX(), "Village camera must stop at the left edge");

        gp.player.worldX = gp.tileSize * 49;
        assertEquals(gp.maxWorldCol * gp.tileSize - gp.screenWidth, gp.getCameraX(), "Village camera must stop at the right edge");

        gp.player.worldY = 0;
        assertEquals(0, gp.getCameraY(), "Village camera must stop at the top edge");

        gp.player.worldY = gp.tileSize * 49;
        assertEquals(gp.maxWorldRow * gp.tileSize - gp.screenHeight, gp.getCameraY(), "Village camera must stop at the bottom edge");
    }

    public static void testSettingsMenuControls() {
        GamePanel gp = new GamePanel();
        gp.setupGame();

        gp.gameState = gp.titleState;
        gp.ui.commandNum = 2;
        gp.keyH.titleState(KeyEvent.VK_ENTER);
        assertEquals(gp.optionsState, gp.gameState, "Title settings command must open the settings menu");
        assertEquals(0, gp.ui.commandNum, "Settings menu must open on music volume");

        int startMusicVolume = gp.getMusicVolume();
        gp.keyH.optionsState(KeyEvent.VK_RIGHT);
        assertEquals(Math.min(5, startMusicVolume + 1), gp.getMusicVolume(), "Right must increase music volume");

        gp.keyH.optionsState(KeyEvent.VK_DOWN);
        assertEquals(1, gp.ui.commandNum, "Down must move to sound effects volume");
        int startSoundVolume = gp.getSoundEffectVolume();
        gp.keyH.optionsState(KeyEvent.VK_LEFT);
        assertEquals(Math.max(0, startSoundVolume - 1), gp.getSoundEffectVolume(), "Left must decrease sound effects volume");

        gp.keyH.optionsState(KeyEvent.VK_DOWN);
        boolean fullScreenBefore = gp.fullScreenOn;
        gp.keyH.optionsState(KeyEvent.VK_ENTER);
        assertEquals(fullScreenBefore ? 0 : 1, gp.fullScreenOn ? 1 : 0, "Enter must toggle fullscreen setting");

        gp.keyH.optionsState(KeyEvent.VK_DOWN);
        boolean hudBefore = gp.hudVisible;
        gp.keyH.optionsState(KeyEvent.VK_ENTER);
        assertEquals(hudBefore ? 0 : 1, gp.hudVisible ? 1 : 0, "Enter must toggle HUD setting");

        gp.keyH.optionsState(KeyEvent.VK_DOWN);
        gp.keyH.optionsState(KeyEvent.VK_ENTER);
        assertEquals(gp.titleState, gp.gameState, "Back must return to title menu");
        assertEquals(2, gp.ui.commandNum, "Returning from settings must restore the previous menu command");
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ". Ожидалось " + expected + ", получено " + actual);
        }
    }

    private static void assertBlocked(GamePanel gp, int col, int row, String label) {
        assertBlocked(gp, 0, col, row, label);
    }

    private static void assertBlocked(GamePanel gp, int map, int col, int row, String label) {
        int tileNum = gp.tileM.mapTileNum[map][col][row];
        if (!gp.tileM.tile[tileNum].collision) {
            throw new AssertionError(label + " must block movement");
        }
    }

    private static void assertWalkable(GamePanel gp, int col, int row, String label) {
        int tileNum = gp.tileM.mapTileNum[0][col][row];
        if (gp.tileM.tile[tileNum].collision) {
            throw new AssertionError(label + " must be walkable");
        }
    }

    private static void assertWalkable(GamePanel gp, int map, int col, int row, String label) {
        int tileNum = gp.tileM.mapTileNum[map][col][row];
        if (gp.tileM.tile[tileNum].collision) {
            throw new AssertionError(label + " must be walkable");
        }
    }

    private static int countObjects(GamePanel gp, int map) {
        int count = 0;
        for (int i = 0; i < gp.obj[map].length; i++) {
            if (gp.obj[map][i] != null) {
                count++;
            }
        }
        return count;
    }

    private static void assertForestObjectsDoNotBlockPath(GamePanel gp) {
        for (int i = 0; i < gp.obj[1].length; i++) {
            if (gp.obj[1][i] == null) {
                continue;
            }
            if (!gp.obj[1][i].collision) {
                continue;
            }

            int leftCol = (gp.obj[1][i].worldX + gp.obj[1][i].solidArea.x) / gp.tileSize;
            int rightCol = (gp.obj[1][i].worldX + gp.obj[1][i].solidArea.x +
                    gp.obj[1][i].solidArea.width - 1) / gp.tileSize;
            int topRow = (gp.obj[1][i].worldY + gp.obj[1][i].solidArea.y) / gp.tileSize;
            int bottomRow = (gp.obj[1][i].worldY + gp.obj[1][i].solidArea.y +
                    gp.obj[1][i].solidArea.height - 1) / gp.tileSize;

            for (int col = leftCol; col <= rightCol; col++) {
                for (int row = topRow; row <= bottomRow; row++) {
                    if (gp.tileM.mapTileNum[1][col][row] == 51) {
                        throw new AssertionError("Forest tree must not block path tile at " + col + "," + row);
                    }
                }
            }
        }
    }

    private static void assertForestUsesOnlyTree05And11(GamePanel gp) {
        for (int i = 0; i < gp.obj[1].length; i++) {
            if (gp.obj[1][i] == null) {
                continue;
            }
            if (!gp.obj[1][i].name.startsWith("tree_")) {
                continue;
            }

            if (!"tree_05".equals(gp.obj[1][i].name) && !"tree_11".equals(gp.obj[1][i].name)) {
                throw new AssertionError("Forest must use only tree_05 and tree_11");
            }
        }
    }

    private static void assertForestDecorationsAreNonBlocking(GamePanel gp) {
        for (int i = 0; i < gp.obj[1].length; i++) {
            if (gp.obj[1][i] == null || !gp.obj[1][i].name.startsWith("decoration_")) {
                continue;
            }
            if (gp.obj[1][i].collision) {
                throw new AssertionError("Forest decoration must not block movement");
            }
        }
    }

    private static int countObjectsByPrefix(GamePanel gp, int map, String prefix) {
        int count = 0;
        for (int i = 0; i < gp.obj[map].length; i++) {
            if (gp.obj[map][i] != null && gp.obj[map][i].name.startsWith(prefix)) {
                count++;
            }
        }
        return count;
    }

    private static int findObject(GamePanel gp, int map, String name) {
        for (int i = 0; i < gp.obj[map].length; i++) {
            if (gp.obj[map][i] != null && name.equals(gp.obj[map][i].name)) {
                return i;
            }
        }
        return 999;
    }

    private static void finishLockedDialogue(GamePanel gp) {
        int guard = 2000;
        while (!gp.story.canContinueDialogue() && guard > 0) {
            gp.story.update();
            guard--;
        }
        if (!gp.story.canContinueDialogue()) {
            throw new AssertionError("Timed interaction dialogue did not unlock");
        }
        gp.story.continueDialogue();
    }

    private static void assertTreeLeavesBlockMovement(GamePanel gp) {
        gp.currentMap = 1;
        if (gp.obj[1][0].solidArea.y >= gp.tileSize) {
            throw new AssertionError("Forest tree collision must include leaves, not only the trunk");
        }
        if (gp.obj[1][0].solidArea.height <= gp.tileSize * 2) {
            throw new AssertionError("Forest tree collision must cover the leaf crown");
        }

        int leafTop = gp.obj[1][0].worldY + gp.obj[1][0].solidArea.y;
        gp.player.worldX = gp.obj[1][0].worldX + gp.obj[1][0].solidArea.x;
        gp.player.worldY = leafTop - gp.player.solidArea.y - gp.player.solidArea.height - gp.player.speed + 1;
        gp.player.direction = "down";
        gp.player.collisionOn = false;
        gp.cChecker.checkObject(gp.player, true);

        if (!gp.player.collisionOn) {
            throw new AssertionError("Player must not be able to walk through tree leaves");
        }
    }

    private static void assertPlayerBlocked(GamePanel gp, int worldX, int worldY, String direction, String label) {
        gp.player.worldX = worldX;
        gp.player.worldY = worldY;
        gp.player.direction = direction;
        gp.player.collisionOn = false;
        gp.cChecker.checkTile(gp.player);

        if (!gp.player.collisionOn) {
            throw new AssertionError(label + " must stop the player");
        }
    }

    private static void assertObjectCollisionAtBedEdge(GamePanel gp) {
        int bedBottom = gp.obj[0][0].worldY + gp.obj[0][0].solidArea.y + gp.obj[0][0].solidArea.height;
        gp.player.worldX = gp.obj[0][0].worldX + gp.obj[0][0].solidArea.x;
        gp.player.worldY = bedBottom - gp.player.solidArea.y + gp.player.speed;
        gp.player.direction = "up";
        gp.player.collisionOn = false;
        gp.cChecker.checkObject(gp.player, true);

        if (gp.player.collisionOn) {
            throw new AssertionError("Player must be able to stand next to the bed before touching it");
        }

        gp.player.worldY -= 1;
        gp.player.collisionOn = false;
        gp.cChecker.checkObject(gp.player, true);

        if (!gp.player.collisionOn) {
            throw new AssertionError("Bed must block movement after the player reaches it");
        }
    }

    private static void assertObjectBlocksFromTop(GamePanel gp, Entity object, String label) {
        int objectTop = object.worldY + object.solidArea.y;
        gp.player.worldX = object.worldX + object.solidArea.x;
        gp.player.worldY = objectTop - gp.player.solidArea.y - gp.player.solidArea.height - gp.player.speed + 1;
        gp.player.direction = "down";
        gp.player.collisionOn = false;
        gp.cChecker.checkObject(gp.player, true);

        if (!gp.player.collisionOn) {
            throw new AssertionError(label + " must block movement");
        }
    }

    private static void assertObjectBlocksFromLeft(GamePanel gp, Entity object, String label) {
        int objectLeft = object.worldX + object.solidArea.x;
        gp.player.worldX = objectLeft - gp.player.solidArea.x - gp.player.solidArea.width - gp.player.speed + 1;
        gp.player.worldY = object.worldY + object.solidArea.y;
        gp.player.direction = "right";
        gp.player.collisionOn = false;
        gp.cChecker.checkObject(gp.player, true);

        if (!gp.player.collisionOn) {
            throw new AssertionError(label + " must block movement");
        }
    }
}
