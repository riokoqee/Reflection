package test;

import entity.Entity;
import entity.SwingChildNPC;
import main.GamePanel;
import main.StoryManager;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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
            runTest("Test 13: optional story events", TestLogic::testOptionalStoryEvents);
            runTest("Test 14: player sprint", TestLogic::testPlayerSprint);
            runTest("Test 15: mouse menu controls", TestLogic::testMouseMenuControls);
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
        assertEquals(35, gp.story.responsibility, "Ответственность должна начинаться с 35");
        assertEquals(35, gp.story.avoidance, "Избегание должно начинаться с 35");
        assertEquals(35, gp.story.selfWorth, "Самооценка должна начинаться с 35");
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

        gp.story.interactObject("Kitchen Stove");
        finishLockedDialogue(gp);
        if (!gp.story.getObjective().contains("ванной")) {
            throw new AssertionError("После кухни цель должна вести в ванную, получено: " + gp.story.getObjective());
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
        assertBlocked(gp, 5, 20, "Removed kitchen left side");
        assertBlocked(gp, 37, 14, "Right apartment wall");
        assertWalkable(gp, 14, 14, "Apartment floor");
        assertBlocked(gp, 10, 10, "Removed bedroom left side");
        assertBlocked(gp, 13, 10, "Bedroom left wall");
        assertBlocked(gp, 22, 9, "Bedroom right wall");
        assertWalkable(gp, 22, 11, "Bedroom doorway");
        assertBlocked(gp, 17, 15, "Bedroom bottom wall");
        assertBlocked(gp, 10, 20, "Removed kitchen left half");
        assertBlocked(gp, 13, 20, "Kitchen left wall");
        assertWalkable(gp, 14, 20, "Compact kitchen floor");
        assertBlocked(gp, 22, 18, "Kitchen partition");
        assertWalkable(gp, 22, 20, "Kitchen doorway");
        assertBlocked(gp, 26, 9, "Living room partition");
        assertWalkable(gp, 26, 11, "Living room doorway");
        assertBlocked(gp, 30, 15, "Right room split wall");
        assertWalkable(gp, 26, 20, "Right lower room doorway");

        assertPlayerBlocked(gp, gp.tileSize * 14, gp.tileSize * 7 - gp.player.solidArea.y, "up", "Top boundary");
        assertPlayerBlocked(gp, gp.tileSize * 14,
                gp.tileSize * 24 - gp.player.solidArea.y - gp.player.solidArea.height - gp.player.speed + 1,
                "down", "Bottom boundary");
        assertPlayerBlocked(gp, gp.tileSize * 6 - gp.player.solidArea.x, gp.tileSize * 20, "left", "Left boundary");
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

        assertEquals(gp.tileSize * 22 - (int) Math.round(gp.tileSize * 2.8), gp.obj[0][0].worldX,
                "Bed X position");
        assertEquals(gp.tileSize * 7, gp.obj[0][0].worldY, "Bed Y position");
        assertEquals(gp.tileSize / 2, gp.obj[0][0].solidArea.x, "Bed collision X offset");
        assertEquals(gp.tileSize / 5, gp.obj[0][0].solidArea.y, "Bed collision Y offset");
        assertEquals(gp.tileSize * 7 / 4, gp.obj[0][0].solidArea.width, "Bed collision width");
        assertEquals(gp.tileSize * 2, gp.obj[0][0].solidArea.height, "Bed collision height");
        assertObjectCollisionAtBedEdge(gp);

        if (gp.obj[0][1] == null) {
            throw new AssertionError("Apartment must contain a mirror object");
        }
        if (!gp.obj[0][1].collision) {
            throw new AssertionError("Mirror must block its visible body");
        }
        int expectedDresserX = gp.obj[0][0].worldX - (int) Math.round(gp.tileSize * 1.25) - gp.tileSize / 8;
        int expectedMirrorX = expectedDresserX - (int) Math.round(gp.tileSize * 1.6) - gp.tileSize / 4;
        assertEquals(expectedMirrorX, gp.obj[0][1].worldX, "Mirror X position");
        assertEquals(gp.tileSize * 6 + gp.tileSize / 4, gp.obj[0][1].worldY, "Mirror Y position");
        assertEquals(gp.tileSize / 3, gp.obj[0][1].solidArea.x, "Mirror collision X offset");
        assertEquals(gp.tileSize / 3, gp.obj[0][1].solidArea.y, "Mirror collision Y offset");
        assertEquals(gp.tileSize, gp.obj[0][1].solidArea.width, "Mirror collision width");
        assertEquals(gp.tileSize * 13 / 8, gp.obj[0][1].solidArea.height, "Mirror collision height");
        assertObjectBlocksFromTop(gp, gp.obj[0][1], "Mirror top");
        assertObjectBlocksFromLeft(gp, gp.obj[0][1], "Mirror left side");

        if (findObject(gp, 0, "Bedroom Window") != 999) {
            throw new AssertionError("Bedroom window must be removed");
        }
        if (findObject(gp, 0, "Bedroom Wardrobe") != 999) {
            throw new AssertionError("Bedroom wardrobe must be removed");
        }
        if (findObject(gp, 0, "Bedroom Dresser") != 999) {
            throw new AssertionError("Bedroom table must be removed");
        }
        int plantIndex = findObject(gp, 0, "Bedroom Plant");
        if (plantIndex == 999) {
            throw new AssertionError("Bedroom plant must exist");
        }
        if (!gp.obj[0][plantIndex].collision) {
            throw new AssertionError("Bedroom plant must block movement near the pot");
        }
        int lampIndex = findObject(gp, 0, "Bedroom Lamp");
        if (lampIndex == 999) {
            throw new AssertionError("Bedroom lamp must exist");
        }
        if (gp.obj[0][lampIndex].solidArea.height <= 0) {
            throw new AssertionError("Bedroom lamp must be interactable");
        }
        if (gp.bedroomLampOn) {
            throw new AssertionError("Bedroom lamp must start switched off");
        }
        gp.gameState = gp.playState;
        gp.story.interactObject("Bedroom Lamp");
        if (!gp.bedroomLampOn || gp.gameState != gp.playState) {
            throw new AssertionError("Bedroom lamp interaction must switch light on without opening a task");
        }
        gp.story.interactObject("Bedroom Lamp");
        if (gp.bedroomLampOn || gp.gameState != gp.playState) {
            throw new AssertionError("Bedroom lamp interaction must switch light off without opening a task");
        }
        gp.story.interactObject("Dresser");
        if (!gp.bedroomLampOn || gp.gameState != gp.playState) {
            throw new AssertionError("Bedroom dresser interaction must switch the lamp from any side");
        }
        gp.story.interactObject("Dresser");
        if (gp.bedroomLampOn || gp.gameState != gp.playState) {
            throw new AssertionError("Bedroom dresser interaction must switch the lamp off again");
        }
        gp.player.setPosition(15, 12);
        gp.player.direction = "left";
        gp.keyH.playState(KeyEvent.VK_E);
        gp.player.update();
        if (gp.gameState != gp.playState) {
            throw new AssertionError("Pressing E away from objects must not open the current objective");
        }
        if (findObject(gp, 0, "Kitchen Table") != 999 ||
                findObject(gp, 0, "Kitchen Side Counter") != 999 ||
                findObject(gp, 0, "Kitchen Low Cabinet") != 999 ||
                findObject(gp, 0, "Kitchen Plant") != 999) {
            throw new AssertionError("Kitchen must not contain chairs, wooden cabinet, plant, or book table");
        }
        if (findObject(gp, 0, "Kitchen Stove") == 999 ||
                findObject(gp, 0, "Kitchen Counter Left") == 999 ||
                findObject(gp, 0, "Kitchen Counter Right") == 999 ||
                findObject(gp, 0, "Kitchen Fridge") == 999) {
            throw new AssertionError("Kitchen must keep the functional cooking area");
        }
        int kitchenSinkIndex = findObject(gp, 0, "Kitchen Wall Sink");
        int kitchenCounterLeftIndex = findObject(gp, 0, "Kitchen Counter Left");
        int kitchenCounterRightIndex = findObject(gp, 0, "Kitchen Counter Right");
        int kitchenFridgeIndex = findObject(gp, 0, "Kitchen Fridge");
        int kitchenStoveIndex = findObject(gp, 0, "Kitchen Stove");
        if (kitchenSinkIndex == 999) {
            throw new AssertionError("Kitchen sink must stay in the kitchen");
        }
        assertEquals(gp.tileSize * 18 - gp.tileSize / 2, gp.obj[0][kitchenStoveIndex].worldX,
                "Kitchen stove must shift left to avoid overlapping the counters");
        assertEquals(gp.tileSize * 19 - gp.tileSize / 3, gp.obj[0][kitchenCounterRightIndex].worldX,
                "Kitchen standalone counter must sit cleanly between the stove and sink");
        assertEquals(gp.tileSize * 20 - gp.tileSize / 6, gp.obj[0][kitchenCounterLeftIndex].worldX,
                "Kitchen left L counter must move to the right wall area");
        assertEquals(gp.tileSize * 21, gp.obj[0][kitchenFridgeIndex].worldX,
                "Returned kitchen table must close the right edge");
        assertEquals(gp.obj[0][kitchenCounterLeftIndex].worldX + gp.tileSize / 3,
                gp.obj[0][kitchenSinkIndex].worldX,
                "Kitchen sink must sit farther right on the left counter");
        assertEquals(gp.obj[0][kitchenCounterLeftIndex].worldY - gp.tileSize / 6,
                gp.obj[0][kitchenSinkIndex].worldY,
                "Kitchen sink must sit slightly above the left counter");
        if (findObject(gp, 0, "Hall Bookshelf") != 999 ||
                findObject(gp, 0, "Hall Coffee Table") != 999 ||
                findObject(gp, 0, "Hall Wall Shelf") != 999 ||
                findObject(gp, 0, "Hall Floor Lamp") != 999) {
            throw new AssertionError("Hall must keep only the carpet, TV, and sofa");
        }
        int sofaIndex = findObject(gp, 0, "Sofa");
        if (findObject(gp, 0, "TV") == 999 || sofaIndex == 999 || findObject(gp, 0, "Living Carpet") == 999) {
            throw new AssertionError("Hall must still contain TV, sofa, and carpet");
        }
        Entity sofaObject = gp.obj[0][sofaIndex];
        assertEquals(0, sofaObject.solidArea.y, "Sofa collision must start at the visible back");
        assertEquals((int) Math.round(gp.tileSize * 1.75), sofaObject.solidArea.height,
                "Sofa collision must cover the full visible depth");
        assertObjectBlocksFromTop(gp, sofaObject, "Sofa back");
        if (gp.tvOn) {
            throw new AssertionError("TV must start switched off");
        }
        gp.story.interactObject("TV");
        if (!gp.tvOn || gp.gameState != gp.playState) {
            throw new AssertionError("TV interaction must switch TV on without opening a task");
        }
        gp.story.interactObject("TV");
        if (gp.tvOn || gp.gameState != gp.playState) {
            throw new AssertionError("TV interaction must switch TV off without opening a task");
        }
        int stageBeforeSofaInteraction = gp.story.getStage();
        int sofaFrontY = sofaObject.worldY + sofaObject.solidArea.y
                - gp.player.solidArea.y - gp.player.solidArea.height;
        gp.player.setPixelPosition(gp.tileSize * 33, sofaFrontY);
        gp.player.direction = "down";
        gp.keyH.playState(KeyEvent.VK_E);
        gp.player.update();
        if (gp.story.getStage() != stageBeforeSofaInteraction || gp.gameState != gp.playState) {
            throw new AssertionError("Generic sofa interaction must sit without advancing the story");
        }
        int expectedSofaSitY = gp.tileSize * 12 - gp.tileSize / 3;
        if (gp.player.worldY != expectedSofaSitY || !"up".equals(gp.player.direction)) {
            throw new AssertionError("Pressing E in front of the sofa must put the player into the sitting pose");
        }
        if (gp.obj[0][sofaIndex].getRenderSortY() <= gp.player.worldY ||
                gp.obj[0][sofaIndex].getRenderSortY() >= gp.tileSize * 14) {
            throw new AssertionError("Sofa must render over the seated body without covering the standing player");
        }
        for (int i = 0; i < 120; i++) {
            gp.player.updatePoseState();
        }
        if (gp.player.worldY != expectedSofaSitY || !"up".equals(gp.player.direction)) {
            throw new AssertionError("Generic sofa sitting must stay stable until the player moves");
        }
        gp.keyH.rightPressed = true;
        gp.player.updatePoseState();
        gp.keyH.rightPressed = false;
        if (gp.player.worldY != sofaFrontY || !"up".equals(gp.player.direction)) {
            throw new AssertionError("Moving after sofa sitting must return the player to the TV side of the sofa");
        }
        assertFloorLayer(gp, "Living Carpet");
        assertFloorLayer(gp, "Bedroom Rug");
        assertFloorLayer(gp, "Kitchen Rug");
        assertFloorLayer(gp, "Bathroom Rug");
    }

    public static void testApartmentCameraBounds() {
        GamePanel gp = new GamePanel();

        gp.player.worldX = gp.tileSize * 6;
        assertEquals(gp.tileSize * 5, gp.getCameraX(), "Apartment camera must stop at the left wall");

        gp.player.worldX = gp.tileSize * 16;
        gp.player.worldY = gp.tileSize * 12;
        assertEquals(gp.tileSize * 13, gp.getCameraX(), "Bedroom camera must start at the smaller room wall");
        assertEquals(gp.tileSize * 6, gp.getCameraY(), "Bedroom camera must fill the screen vertically");

        gp.player.worldX = gp.tileSize * 16;
        gp.player.worldY = gp.tileSize * 20;
        assertEquals(gp.tileSize * 13, gp.getCameraX(), "Kitchen camera must start at the smaller room wall");
        assertEquals(gp.tileSize * 25 - gp.screenHeight, gp.getCameraY(), "Kitchen camera must stop at the bottom room wall");

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
        assertStoneRoad(gp, 23, 15, "Village entrance road");
        assertStoneRoad(gp, 13, 10, "Friend house doorway");
        assertStoneRoad(gp, 36, 14, "Library entrance");

        if (countObjectsByPrefix(gp, 2, "village_house") < 13) {
            throw new AssertionError("Village must contain several house objects");
        }
        if (findObject(gp, 2, "village_library") == 999) {
            throw new AssertionError("Village must contain the library building");
        }
        if (countObjects(gp, 2) != 14 ||
                findObject(gp, 2, "Old Letter") != 999 ||
                findObject(gp, 2, "Help Request") != 999) {
            throw new AssertionError("Village must contain only houses, the library, and NPCs");
        }
        assertEquals(gp.tileSize * 12, gp.obj[2][findObject(gp, 2, "village_house_friend")].worldX,
                "Friend house must anchor the north-west village row");
        assertEquals(gp.tileSize * 42, gp.obj[2][findObject(gp, 2, "village_house_east_upper")].worldX,
                "East house must anchor the right side of the village");
        assertEquals(gp.tileSize * 38, gp.obj[2][findObject(gp, 2, "village_house_south_east")].worldX,
                "South houses must frame the lower village road");
        assertEquals(gp.tileSize * 15, gp.obj[2][findObject(gp, 2, "village_house_center_west")].worldX,
                "Center west house must frame the village square");
        assertEquals(gp.tileSize * 32, gp.obj[2][findObject(gp, 2, "village_house_center_east")].worldX,
                "Center east house must frame the village square");
        assertObjectDoesNotOverlapTile(gp, 2, "village_house_south_west", 12,
                "South-west village house must not stand in the pond");
        assertVillageHousesDoNotBlockStoneRoads(gp);
        assertVillageHouseScale(gp, "village_house_north_center");
        assertVillageHouseScale(gp, "village_house_south_center");
        assertVillageHouseScale(gp, "village_house_center_west");

        assertStoneRoad(gp, 24, 24, "Village center road must exist");
        assertStoneRoad(gp, 9, 24, "Village west road must connect");
        assertStoneRoad(gp, 40, 24, "Village east road must connect");
        assertStoneRoad(gp, 24, 42, "Village south road must connect");
        assertNotStoneRoad(gp, 22, 21, "Village vertical road must stay narrow above the square");
        assertNotStoneRoad(gp, 12, 23, "Village west road must stay narrow");

        assertEquals(gp.tileSize * 13, gp.npc[2][0].worldX, "Friend must stand in front of the friend house");
        assertEquals(gp.tileSize * 10, gp.npc[2][0].worldY, "Friend must stand by the friend house doorway");
        assertEquals(gp.tileSize * 36, gp.npc[2][1].worldX, "Elder must stand near the library");
        assertEquals(gp.tileSize * 14, gp.npc[2][1].worldY, "Elder must stand near the library entrance");
        if (gp.npc[2][0].down1.getWidth() <= gp.tileSize || gp.npc[2][1].down1.getHeight() <= gp.tileSize) {
            throw new AssertionError("Village NPC sprites must render slightly larger than one tile");
        }

        gp.player.worldX = 0;
        assertEquals(0, gp.getCameraX(), "Village camera must stop at the left edge");

        gp.player.worldX = gp.tileSize * 49;
        assertEquals(gp.maxWorldCol * gp.tileSize - gp.screenWidth, gp.getCameraX(), "Village camera must stop at the right edge");

        gp.player.worldY = 0;
        assertEquals(0, gp.getCameraY(), "Village camera must stop at the top edge");

        gp.player.worldY = gp.tileSize * 49;
        assertEquals(gp.maxWorldRow * gp.tileSize - gp.screenHeight, gp.getCameraY(), "Village camera must stop at the bottom edge");
    }

    public static void testPlayerSprint() {
        GamePanel gp = new GamePanel();
        gp.setupGame();
        gp.currentMap = 2;
        if (!new File("res/sound/new/stone_footstep_walk_loop.wav").isFile() ||
                !new File("res/sound/new/stone_footstep_sprint_loop.wav").isFile()) {
            throw new AssertionError("Stone footsteps must have faster loop assets");
        }

        int startX = gp.tileSize * 23;
        int startY = gp.tileSize * 15;

        gp.player.worldX = startX;
        gp.player.worldY = startY;
        gp.keyH.rightPressed = true;
        gp.player.update();
        assertEquals(startX + 4, gp.player.worldX, "Walking right must use the base speed");

        gp.player.worldX = startX;
        gp.player.worldY = startY;
        gp.keyH.shiftPressed = true;
        gp.player.update();
        assertEquals(startX + 7, gp.player.worldX, "Holding Shift must sprint faster than walking");
        if (!gp.player.isSprinting()) {
            throw new AssertionError("Player sprint state must be active while Shift and movement are held");
        }

        gp.keyH.rightPressed = false;
        gp.player.update();
        assertEquals(4, gp.player.speed, "Player speed must return to walking when movement stops");
    }

    public static void testSettingsMenuControls() {
        GamePanel gp = new GamePanel();
        gp.setupGame();

        gp.gameState = gp.titleState;
        gp.ui.commandNum = 2;
        gp.keyH.titleState(KeyEvent.VK_ENTER);
        assertEquals(gp.optionsState, gp.gameState, "Title settings command must open the settings menu");
        assertEquals(0, gp.ui.commandNum, "Settings menu must open on music volume");
        if (gp.hudVisible) {
            throw new AssertionError("HUD must stay hidden during the game");
        }

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
        assertEquals(3, gp.ui.commandNum, "Down from fullscreen must move directly to Back");
        gp.keyH.optionsState(KeyEvent.VK_ENTER);
        assertEquals(gp.titleState, gp.gameState, "Back must return to title menu");
        assertEquals(2, gp.ui.commandNum, "Returning from settings must restore the previous menu command");
    }

    public static void testMouseMenuControls() {
        GamePanel gp = new GamePanel();
        gp.setupGame();

        if (!gp.shouldShowMouseCursor()) {
            throw new AssertionError("Mouse cursor must be visible on the title menu");
        }

        int titleSettingsY = gp.tileSize * 6 + 24 + 44 * 2;
        gp.mouseH.mouseMoved(mouseEvent(gp, MouseEvent.MOUSE_MOVED, gp.screenWidth / 2, titleSettingsY));
        assertEquals(2, gp.ui.commandNum, "Hovering title settings must select that command");
        gp.mouseH.mousePressed(mouseEvent(gp, MouseEvent.MOUSE_PRESSED, gp.screenWidth / 2, titleSettingsY));
        assertEquals(gp.optionsState, gp.gameState, "Clicking title settings must open options");

        int optionsPanelY = gp.screenHeight / 2 - 360 / 2;
        int optionsBackY = optionsPanelY + 124 + 55 * 3;
        gp.mouseH.mousePressed(mouseEvent(gp, MouseEvent.MOUSE_PRESSED, gp.screenWidth / 2, optionsBackY));
        assertEquals(gp.titleState, gp.gameState, "Clicking options back must return to title");
        assertEquals(2, gp.ui.commandNum, "Returning from mouse-opened settings must restore title selection");

        gp.gameState = gp.playState;
        gp.syncMouseCursor();
        if (gp.shouldShowMouseCursor()) {
            throw new AssertionError("Mouse cursor must be hidden while playing");
        }

        gp.gameState = gp.pauseState;
        int pausePanelY = gp.screenHeight / 2 - 470 / 2;
        int pauseResumeY = pausePanelY + 158;
        gp.mouseH.mousePressed(mouseEvent(gp, MouseEvent.MOUSE_PRESSED, gp.screenWidth / 2, pauseResumeY));
        assertEquals(gp.playState, gp.gameState, "Clicking pause resume must return to play");
    }

    private static MouseEvent mouseEvent(GamePanel gp, int id, int x, int y) {
        return new MouseEvent(gp, id, System.currentTimeMillis(), 0, x, y, 1, false, MouseEvent.BUTTON1);
    }

    public static void testOptionalStoryEvents() {
        GamePanel gp = new GamePanel();
        gp.setupGame();

        if (findObject(gp, 0, "Phone Message") == 999 || findObject(gp, 0, "Old Photo") == 999) {
            throw new AssertionError("Apartment must contain optional phone and photo events");
        }
        if (findObject(gp, 1, "Lost Lantern") == 999 || findObject(gp, 1, "Wounded Bird") == 999) {
            throw new AssertionError("Forest must contain optional lantern and bird events");
        }
        if (findObject(gp, 2, "Old Letter") != 999 || findObject(gp, 2, "Help Request") != 999) {
            throw new AssertionError("Village optional objects must stay removed while the map is house-only");
        }
        if (findObject(gp, 3, "Mountain Fork") == 999 || findObject(gp, 3, "Traveler Pack") == 999) {
            throw new AssertionError("Mountain must contain optional fork and traveler events");
        }

        gp.story.interactObject("Phone Message");
        if (!gp.story.hasChoices()) {
            throw new AssertionError("Phone event must open an optional prompt");
        }
        gp.story.chooseSelected();
        if (!gp.story.phoneEventDone) {
            throw new AssertionError("Phone event must be marked as completed after choosing");
        }
        if (!gp.story.isPhoneResultOpen()) {
            throw new AssertionError("Phone reply must stay inside the phone screen");
        }
        if (!gp.story.getPhoneResultMomText().contains("рядом")) {
            throw new AssertionError("Mom's reply must be shown as a phone message");
        }
        assertEquals(49, gp.story.empathy, "Answering the phone must raise empathy");
        assertEquals(47, gp.story.responsibility, "Answering the phone must raise responsibility");
        assertEquals(27, gp.story.avoidance, "Answering the phone must lower avoidance");
        gp.story.continueDialogue();

        gp.story.interactObject("Phone Message");
        if (gp.story.hasChoices()) {
            throw new AssertionError("Completed optional phone event must not grant choices again");
        }
        gp.story.continueDialogue();
        assertEquals(49, gp.story.empathy, "Completed phone event must not apply metrics twice");

        gp.story.interactObject("Old Photo");
        gp.story.selectedChoice = 1;
        gp.story.chooseSelected();
        if (!gp.story.photoEventDone) {
            throw new AssertionError("Photo event must be marked as completed");
        }
        assertEquals(45, gp.story.calm, "Putting the photo back must raise calm");
        gp.story.continueDialogue();

        gp.currentMap = 1;
        gp.story.interactObject("Wounded Bird");
        gp.story.chooseSelected();
        if (!gp.story.woundedBirdEventDone) {
            throw new AssertionError("Wounded bird event must be marked as completed");
        }
        if (gp.story.empathy <= 49 || gp.story.responsibility <= 47) {
            throw new AssertionError("Helping the bird must deepen empathy and responsibility");
        }
        gp.story.continueDialogue();

        gp.currentMap = 3;
        gp.story.interactObject("Mountain Fork");
        gp.story.selectedChoice = 1;
        gp.story.chooseSelected();
        if (!gp.story.forkEventDone || gp.story.growth <= 35 || gp.story.calm <= 45) {
            throw new AssertionError("Long mountain path must affect growth and calm");
        }
        gp.story.continueDialogue();

        gp.story.interact(StoryManager.TRAVELER);
        gp.story.selectedChoice = 1;
        gp.story.chooseSelected();
        if (!gp.story.travelerEventDone) {
            throw new AssertionError("Traveler event must be marked as completed");
        }
        gp.story.continueDialogue();

        gp.story.responsibility = 80;
        gp.story.avoidance = 30;
        gp.story.selfWorth = 60;
        gp.story.empathy = 78;
        gp.story.growth = 45;
        gp.story.calm = 50;
        gp.story.confidence = 45;
        if (!"Заботливый тип".equals(gp.story.getProfileTitle())) {
            throw new AssertionError("Hidden metrics must be able to produce the caring profile");
        }

        gp.story.responsibility = 35;
        gp.story.avoidance = 82;
        gp.story.selfWorth = 45;
        gp.story.empathy = 45;
        if (!"Избегающий тип".equals(gp.story.getProfileTitle())) {
            throw new AssertionError("Hidden metrics must be able to produce the avoidant profile");
        }
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

    private static void assertStoneRoad(GamePanel gp, int col, int row, String label) {
        int tileNum = gp.tileM.mapTileNum[2][col][row];
        if (tileNum < 54 || tileNum > 56) {
            throw new AssertionError(label + " must use a stone road tile");
        }
        if (gp.tileM.tile[tileNum].collision) {
            throw new AssertionError(label + " must be walkable");
        }
    }

    private static void assertNotStoneRoad(GamePanel gp, int col, int row, String label) {
        int tileNum = gp.tileM.mapTileNum[2][col][row];
        if (tileNum >= 54 && tileNum <= 56) {
            throw new AssertionError(label);
        }
    }

    private static void assertObjectDoesNotOverlapTile(GamePanel gp, int map, String name, int forbiddenTile,
                                                       String label) {
        int index = findObject(gp, map, name);
        if (index == 999) {
            throw new AssertionError(name + " must exist");
        }

        Entity object = gp.obj[map][index];
        int leftCol = (object.worldX + object.solidArea.x) / gp.tileSize;
        int rightCol = (object.worldX + object.solidArea.x + object.solidArea.width - 1) / gp.tileSize;
        int topRow = (object.worldY + object.solidArea.y) / gp.tileSize;
        int bottomRow = (object.worldY + object.solidArea.y + object.solidArea.height - 1) / gp.tileSize;

        for (int col = leftCol; col <= rightCol; col++) {
            for (int row = topRow; row <= bottomRow; row++) {
                if (gp.tileM.mapTileNum[map][col][row] == forbiddenTile) {
                    throw new AssertionError(label);
                }
            }
        }
    }

    private static void assertVillageHousesDoNotBlockStoneRoads(GamePanel gp) {
        for (int i = 0; i < gp.obj[2].length; i++) {
            Entity object = gp.obj[2][i];
            if (object == null || (!object.name.startsWith("village_house") && !"village_library".equals(object.name))) {
                continue;
            }

            int leftCol = (object.worldX + object.solidArea.x) / gp.tileSize;
            int rightCol = (object.worldX + object.solidArea.x + object.solidArea.width - 1) / gp.tileSize;
            int topRow = (object.worldY + object.solidArea.y) / gp.tileSize;
            int bottomRow = (object.worldY + object.solidArea.y + object.solidArea.height - 1) / gp.tileSize;

            for (int col = leftCol; col <= rightCol; col++) {
                for (int row = topRow; row <= bottomRow; row++) {
                    int tileNum = gp.tileM.mapTileNum[2][col][row];
                    if (tileNum >= 54 && tileNum <= 56) {
                        throw new AssertionError(object.name + " must not block stone road tile at " + col + "," + row);
                    }
                }
            }
        }
    }

    private static void assertVillageHouseScale(GamePanel gp, String name) {
        int index = findObject(gp, 2, name);
        if (index == 999) {
            throw new AssertionError(name + " must exist");
        }
        if (gp.obj[2][index].solidArea.height < gp.tileSize * 5) {
            throw new AssertionError(name + " must be visibly larger after the village scale-up");
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

    private static void assertFloorLayer(GamePanel gp, String name) {
        int index = findObject(gp, 0, name);
        if (index == 999) {
            throw new AssertionError(name + " must exist");
        }
        if (!gp.obj[0][index].isFloorLayer()) {
            throw new AssertionError(name + " must render below the player");
        }
        if (gp.obj[0][index].collision) {
            throw new AssertionError(name + " must be walkable");
        }
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
