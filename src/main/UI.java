package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UI {

    public static final int TITLE_MENU_X = 88;
    public static final int TITLE_MENU_FIRST_Y = 304;
    public static final int TITLE_MENU_WIDTH = 352;
    public static final int TITLE_MENU_ROW_HEIGHT = 54;
    public static final int TITLE_MENU_ITEM_HEIGHT = 44;
    public static final int TITLE_MENU_COMMANDS = 4;
    public static final int TITLE_SLOT_BACK_COMMAND = 3;
    public static final int OPTIONS_TAB_GRAPHICS = 0;
    public static final int OPTIONS_TAB_SOUND = 1;
    public static final int OPTIONS_TAB_CHAT = 2;
    public static final int OPTIONS_TAB_COUNT = 3;
    public static final int OPTIONS_PANEL_WIDTH = 720;
    public static final int OPTIONS_PANEL_HEIGHT = 500;
    public static final int OPTIONS_ROW_STEP = 44;
    public static final int OPTIONS_ROW_HEIGHT = 38;
    private static final int PLAN_NOTE_WIDTH = 330;
    private static final int PLAN_NOTE_ANIMATION_FRAMES = 18;
    private static final int CONTROL_HINT_DURATION_FRAMES = 540;
    private static final int TITLE_MODE_MAIN = 0;
    private static final int TITLE_MODE_NEW_SLOT = 1;
    private static final int TITLE_MODE_LOAD_SLOT = 2;
    private static final String INTRO_DISCLAIMER_SPEAKER = "Reflection";
    private static final String INTRO_DISCLAIMER_TEXT =
            "Во время игры отвечай на вопросы искренне и честно. Reflection реагирует не на правильность, а на твой выбор.";
    private static final int INTRO_TYPE_CHARS_PER_FRAME = 2;

    private final GamePanel gp;
    private Graphics2D g2;
    private final Font titleFont;
    private final BufferedImage titleBackground;
    private final Map<String, BufferedImage> gradientCache = new HashMap<>();
    public int commandNum = 0;
    private final ArrayList<String> message = new ArrayList<>();
    private final ArrayList<Integer> messageCounter = new ArrayList<>();
    private String pauseNotice = "";
    private int pauseNoticeCounter = 0;
    private String checkpointLocation = "";
    private int checkpointCounter = 0;
    private int checkpointSpinnerFrame = 0;
    private int dialogueSpinnerFrame = 0;
    private boolean planNoteOpen = false;
    private int planNoteAnimationFrame = 0;
    private int controlHintCounter = 0;
    private int optionsTab = OPTIONS_TAB_GRAPHICS;
    private int titleMenuMode = TITLE_MODE_MAIN;
    private String titleNotice = "";
    private int titleNoticeCounter = 0;
    private String dialogueRevealKey = "";
    private int dialogueRevealChars = Integer.MAX_VALUE;
    private boolean dialogueRevealComplete = true;
    private int lastIntroFrame = -1;
    private int introSoundRevealChars = 0;

    public UI(GamePanel gp) {
        this.gp = gp;
        titleFont = GameFonts.bold(84);
        titleBackground = loadTitleBackground();
    }

    private String t(String text) {
        return gp.tr(text);
    }

    private String t(String ru, String en) {
        return gp.tr(ru, en);
    }

    public void addMessage(String text) {
        message.add(text);
        messageCounter.add(0);
    }

    public void setPauseNotice(String text) {
        pauseNotice = text;
        pauseNoticeCounter = 150;
    }

    public void showCheckpoint(String locationTitle) {
        checkpointLocation = locationTitle == null ? "" : locationTitle;
        checkpointCounter = 180;
        checkpointSpinnerFrame = 0;
    }

    public boolean isCheckpointNoticeVisible() {
        return checkpointCounter > 0;
    }

    public void togglePlanNote() {
        planNoteOpen = !planNoteOpen;
    }

    public void resetPlanNote() {
        planNoteOpen = false;
        planNoteAnimationFrame = 0;
    }

    public boolean isPlanNoteOpen() {
        return planNoteOpen;
    }

    public void showControlHints() {
        controlHintCounter = CONTROL_HINT_DURATION_FRAMES;
    }

    public boolean isControlHintVisible() {
        return controlHintCounter > 0;
    }

    public int getOptionsTab() {
        return optionsTab;
    }

    public void setOptionsTab(int tab) {
        optionsTab = Math.max(0, Math.min(OPTIONS_TAB_COUNT - 1, tab));
        commandNum = Math.min(commandNum, getOptionsCommandCount() - 1);
    }

    public void moveOptionsTab(int amount) {
        int nextTab = (optionsTab + amount) % OPTIONS_TAB_COUNT;
        if (nextTab < 0) {
            nextTab += OPTIONS_TAB_COUNT;
        }
        setOptionsTab(nextTab);
        commandNum = 0;
    }

    public int getOptionsCommandCount() {
        switch (optionsTab) {
            case OPTIONS_TAB_SOUND:
                return 7;
            case OPTIONS_TAB_CHAT:
                return 5;
            case OPTIONS_TAB_GRAPHICS:
            default:
                return 5;
        }
    }

    public boolean isOptionsBackCommand() {
        return commandNum == getOptionsCommandCount() - 1;
    }

    public boolean isDialogueTextFullyVisible() {
        return dialogueRevealComplete;
    }

    public int getTitleCommandCount() {
        return TITLE_MENU_COMMANDS;
    }

    public boolean isTitleSlotMenu() {
        return titleMenuMode != TITLE_MODE_MAIN;
    }

    public boolean isTitleNewSlotMenu() {
        return titleMenuMode == TITLE_MODE_NEW_SLOT;
    }

    public boolean isTitleLoadSlotMenu() {
        return titleMenuMode == TITLE_MODE_LOAD_SLOT;
    }

    public void enterTitleNewSlots() {
        titleMenuMode = TITLE_MODE_NEW_SLOT;
        commandNum = 0;
        clearTitleNotice();
    }

    public void enterTitleLoadSlots() {
        titleMenuMode = TITLE_MODE_LOAD_SLOT;
        commandNum = 0;
        clearTitleNotice();
    }

    public void returnToTitleMain() {
        titleMenuMode = TITLE_MODE_MAIN;
        commandNum = 0;
        clearTitleNotice();
    }

    public void setTitleNotice(String text) {
        titleNotice = text == null ? "" : text;
        titleNoticeCounter = 150;
    }

    private void clearTitleNotice() {
        titleNotice = "";
        titleNoticeCounter = 0;
    }

    public void revealDialogueTextNow() {
        dialogueRevealChars = Integer.MAX_VALUE;
        dialogueRevealComplete = true;
    }

    public void resetIntroAnimation() {
        lastIntroFrame = -1;
        introSoundRevealChars = 0;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setColor(Color.white);
        updatePlanNoteAnimation();

        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
        else if (gp.gameState == gp.playState) {
            drawMessage();
            drawCheckpointNotice();
            drawControlHints();
            drawPlanNote();
        }
        else if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }
        else if (gp.gameState == gp.optionsState) {
            drawTabbedOptionsScreen();
        }
        else if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }
        else if (gp.gameState == gp.resultState) {
            drawResultScreen();
        }
        else if (gp.gameState == gp.introState) {
            drawIntroDisclaimer();
        }
    }

    private void drawTitleScreen() {
        drawTitleBackground();
        drawTitleBrand();

        if (isTitleSlotMenu()) {
            drawTitleSlotMenu();
        }
        else {
            drawTitleMenuItem(getMainTitleMenuLabel(0), 0);
            drawTitleMenuItem(getMainTitleMenuLabel(1), 1);
            drawTitleMenuItem(t("НАСТРОЙКИ", "SETTINGS"), 2);
            drawTitleMenuItem(t("ВЫЙТИ", "EXIT"), 3);
        }
        drawTitleNotice();
    }

    private String getMainTitleMenuLabel(int command) {
        boolean continueFirst = gp.saveLoad.hasAnySave();
        if (command == 0) {
            return continueFirst ? t("ПРОДОЛЖИТЬ", "CONTINUE") : t("НОВАЯ ИГРА", "NEW GAME");
        }
        if (command == 1) {
            return continueFirst ? t("НОВАЯ ИГРА", "NEW GAME") : t("ПРОДОЛЖИТЬ", "CONTINUE");
        }
        return "";
    }

    private void drawTitleSlotMenu() {
        g2.setFont(GameFonts.bold(20));
        g2.setColor(new Color(174, 215, 196));
        String title = isTitleNewSlotMenu()
                ? t("Выберите слот для новой игры", "Choose a slot for a new game")
                : t("Выберите сохранение", "Choose a save");
        g2.drawString(title, TITLE_MENU_X + 12, TITLE_MENU_FIRST_Y - 56);

        for (int command = 0; command < 3; command++) {
            int slot = command + 1;
            String state = gp.saveLoad.hasSave(slot)
                    ? (isTitleNewSlotMenu() ? t("ПЕРЕЗАПИСАТЬ", "OVERWRITE") : t("ЗАГРУЗИТЬ", "LOAD"))
                    : t("ПУСТОЙ", "EMPTY");
            drawTitleMenuItem(t("СЛОТ", "SLOT") + " " + slot + "  " + state, command);
        }
        drawTitleMenuItem(t("НАЗАД", "BACK"), TITLE_SLOT_BACK_COMMAND);
    }

    private void drawTitleNotice() {
        if (titleNoticeCounter <= 0 || titleNotice.isEmpty()) {
            return;
        }
        titleNoticeCounter--;
        g2.setFont(GameFonts.bold(18));
        g2.setColor(new Color(255, 222, 151));
        drawShadowedString(t(titleNotice), TITLE_MENU_X + 12, TITLE_MENU_FIRST_Y + TITLE_MENU_ROW_HEIGHT * 4 + 4,
                new Color(255, 222, 151), new Color(0, 0, 0, 180));
    }

    private BufferedImage loadTitleBackground() {
        try {
            return ImageIO.read(getClass().getResourceAsStream("/ui/title_reflection_bg.png"));
        }
        catch (Exception e) {
            System.err.println("Title background failed: " + e.getMessage());
            return null;
        }
    }

    private void drawTitleBackground() {
        if (titleBackground != null) {
            g2.drawImage(titleBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }
        else {
            fillVerticalGradient(0, 0, gp.screenWidth, gp.screenHeight,
                    new Color(21, 28, 34), new Color(57, 73, 61));
        }

        fillHorizontalGradient(0, 0, gp.screenWidth, gp.screenHeight,
                new Color(1, 5, 9, 236), new Color(1, 5, 9, 35));

        int bottomShadeY = Math.round(gp.screenHeight * 0.62f);
        fillVerticalGradient(0, bottomShadeY, gp.screenWidth, gp.screenHeight - bottomShadeY,
                new Color(0, 0, 0, 0), new Color(0, 0, 0, 185));

        g2.setColor(new Color(255, 255, 255, 18));
        g2.drawLine(70, 92, 408, 92);
        g2.setColor(new Color(174, 215, 196, 42));
        g2.drawLine(88, 274, 434, 274);
        g2.setColor(new Color(255, 211, 128, 32));
        g2.drawLine(88, 276, 244, 276);
    }

    private void drawTitleBrand() {
        int x = 84;
        int y = 150;

        g2.setFont(titleFont);
        drawShadowedString("Reflection", x, y, new Color(236, 245, 240), new Color(0, 0, 0, 190));

        g2.setFont(GameFonts.regular(19));
        g2.setColor(new Color(204, 216, 210));
        g2.drawString(t("путь через страх, память и выбор"), x + 4, y + 34);
    }

    private void drawTitleMenuItem(String text, int command) {
        int x = TITLE_MENU_X;
        int baselineY = TITLE_MENU_FIRST_Y + command * TITLE_MENU_ROW_HEIGHT;
        int topY = baselineY - 32;
        boolean selected = commandNum == command;

        if (selected) {
            fillHorizontalGradient(x - 14, topY, TITLE_MENU_WIDTH, TITLE_MENU_ITEM_HEIGHT,
                    new Color(174, 215, 196, 86), new Color(174, 215, 196, 12));
            g2.setColor(new Color(255, 222, 151, 210));
            g2.fillRect(x - 14, topY + 8, 3, TITLE_MENU_ITEM_HEIGHT - 16);
        }

        g2.setFont(GameFonts.bold(24));
        Color textColor = selected ? new Color(245, 252, 248) : new Color(188, 202, 197);
        drawShadowedString(text, x + 24, baselineY, textColor, new Color(0, 0, 0, 170));

        g2.setFont(GameFonts.bold(16));
        g2.setColor(selected ? new Color(255, 222, 151) : new Color(174, 215, 196, 120));
        g2.drawString(String.format("%02d", command + 1), x - 6, baselineY - 1);
    }

    private void drawHud() {
        int x = 24;
        int y = 22;
        int width = 270;
        int height = 110;
        drawSubWindow(x, y, width, height, new Color(8, 12, 16, 165));

        g2.setFont(GameFonts.bold(18));
        g2.setColor(new Color(231, 240, 235));
        g2.drawString(t(gp.story.getLocationTitle()), x + 18, y + 30);

        int barY = y + 62;
        drawMiniMetric(t("Рост", "Growth"), gp.story.growth, x + 18, barY);
        drawMiniMetric(t("Покой", "Calm"), gp.story.calm, x + 145, barY);
        drawMiniMetric(t("Эмпатия", "Empathy"), gp.story.empathy, x + 18, barY + 32);
        drawMiniMetric(t("Увер.", "Conf."), gp.story.confidence, x + 145, barY + 32);
    }

    private void drawMessage() {
        int messageX = gp.tileSize;
        int messageY = gp.tileSize * 4;
        g2.setFont(GameFonts.bold(22));

        for (int i = 0; i < message.size(); i++) {
            if (message.get(i) != null) {
                drawShadowedString(t(message.get(i)), messageX, messageY, Color.white, Color.black);

                int counter = messageCounter.get(i) + 1;
                messageCounter.set(i, counter);
                messageY += 34;

                if (messageCounter.get(i) > 180) {
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }
        }
    }

    private void drawCheckpointNotice() {
        if (checkpointCounter <= 0) {
            return;
        }

        int width = Math.min(326, gp.screenWidth - 48);
        int height = 92;
        int x = gp.screenWidth - width - 24;
        int y = 24;

        int alpha = checkpointCounter < 30 ? Math.max(0, checkpointCounter * 7) : 210;
        g2.setColor(new Color(0, 0, 0, Math.min(150, alpha)));
        g2.fillRoundRect(x + 7, y + 8, width, height, 18, 18);

        g2.setColor(new Color(7, 11, 15, Math.min(232, alpha + 22)));
        g2.fillRoundRect(x, y, width, height, 18, 18);
        g2.setColor(new Color(174, 215, 196, Math.min(220, alpha)));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x + 3, y + 3, width - 6, height - 6, 15, 15);

        int spinnerSize = 26;
        int spinnerX = x + 24;
        int spinnerY = y + 32;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(255, 255, 255, Math.min(46, alpha)));
        g2.drawOval(spinnerX, spinnerY, spinnerSize, spinnerSize);
        g2.setColor(new Color(174, 215, 196, Math.min(245, alpha + 20)));
        g2.drawArc(spinnerX, spinnerY, spinnerSize, spinnerSize, (checkpointSpinnerFrame * 16) % 360, 260);
        g2.setStroke(oldStroke);

        int textX = x + 68;
        g2.setFont(GameFonts.bold(17));
        g2.setColor(new Color(236, 248, 242, Math.min(255, alpha + 30)));
        g2.drawString(t("Контрольная точка", "Checkpoint"), textX, y + 31);

        g2.setFont(GameFonts.regular(15));
        g2.setColor(new Color(174, 215, 196, Math.min(240, alpha + 20)));
        g2.drawString(t("Сохранение...", "Saving..."), textX, y + 55);

        if (!checkpointLocation.isEmpty()) {
            g2.setFont(GameFonts.regular(13));
            g2.setColor(new Color(206, 219, 212, Math.min(220, alpha + 10)));
            g2.drawString(trimToWidth(t(checkpointLocation), width - 88), textX, y + 76);
        }

        checkpointSpinnerFrame++;
        checkpointCounter--;
        if (checkpointCounter <= 0) {
            checkpointLocation = "";
        }
    }

    private void updatePlanNoteAnimation() {
        if (planNoteOpen && planNoteAnimationFrame < PLAN_NOTE_ANIMATION_FRAMES) {
            planNoteAnimationFrame++;
        }
        else if (!planNoteOpen && planNoteAnimationFrame > 0) {
            planNoteAnimationFrame--;
        }
    }

    private void drawPlanNote() {
        if (planNoteAnimationFrame <= 0) {
            return;
        }

        float progress = planNoteAnimationFrame / (float) PLAN_NOTE_ANIMATION_FRAMES;
        float easedProgress = 1f - (float) Math.pow(1f - progress, 3);
        int targetX = gp.screenWidth - PLAN_NOTE_WIDTH - 24;
        int hiddenOffset = PLAN_NOTE_WIDTH + 34;
        int x = targetX + Math.round((1f - easedProgress) * hiddenOffset);
        int y = checkpointCounter > 0 ? 126 : 64;
        int height = gp.screenHeight - y - 24;

        Composite oldComposite = g2.getComposite();
        Stroke oldStroke = g2.getStroke();

        g2.setComposite(AlphaComposite.SrcOver.derive(Math.min(1f, 0.35f + easedProgress * 0.65f)));
        g2.setColor(new Color(0, 0, 0, 86));
        g2.fillRoundRect(x + 9, y + 12, PLAN_NOTE_WIDTH, height, 14, 14);

        fillVerticalGradient(x, y, PLAN_NOTE_WIDTH, height,
                new Color(232, 218, 184), new Color(184, 164, 126));

        g2.setColor(new Color(92, 70, 48, 165));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x + 4, y + 4, PLAN_NOTE_WIDTH - 8, height - 8, 9, 9);

        int foldSize = 32;
        Polygon fold = new Polygon(
                new int[]{x + PLAN_NOTE_WIDTH - foldSize - 8, x + PLAN_NOTE_WIDTH - 8, x + PLAN_NOTE_WIDTH - 8},
                new int[]{y + 8, y + 8, y + foldSize + 8},
                3
        );
        g2.setColor(new Color(151, 129, 91, 170));
        g2.fillPolygon(fold);
        g2.setColor(new Color(88, 66, 45, 120));
        g2.drawPolygon(fold);

        g2.setFont(GameFonts.bold(26));
        g2.setColor(new Color(57, 42, 32));
        g2.drawString(t("Записка", "Note"), x + 24, y + 38);
        g2.setFont(GameFonts.regular(14));
        g2.setColor(new Color(91, 68, 47, 205));
        g2.drawString(t(gp.story.getPlanNoteSubtitle()), x + 26, y + 58);

        int rowY = y + 82;
        int rowWidth = PLAN_NOTE_WIDTH - 52;
        Font taskFont = GameFonts.regular(15);
        g2.setFont(taskFont);

        for (StoryManager.PlanTask task : gp.story.getPlanTasks()) {
            ArrayList<String> lines = wrapTextLines(t(task.getDisplayText()), rowWidth - 36, taskFont);
            int rowHeight = Math.max(28, measureLinesHeight(lines, 18) + 8);
            if (rowY + rowHeight > y + height - 22) {
                break;
            }

            drawPlanTask(task, lines, x + 24, rowY, rowWidth, rowHeight);
            rowY += rowHeight + 6;
        }

        g2.setStroke(oldStroke);
        g2.setComposite(oldComposite);
    }

    private void drawPlanTask(StoryManager.PlanTask task, ArrayList<String> lines,
                              int x, int y, int width, int height) {
        int markX = x + 5;
        int markY = y + 10;

        g2.setColor(new Color(255, 255, 255, 70));
        g2.fillRoundRect(x, y, width, height, 9, 9);

        g2.setStroke(new BasicStroke(2));
        g2.setColor(new Color(80, 61, 43, 210));
        g2.drawOval(markX, markY, 13, 13);
        if (task.completed) {
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(markX + 3, markY + 7, markX + 6, markY + 11);
            g2.drawLine(markX + 6, markY + 11, markX + 13, markY + 2);
        }
        else {
            g2.fillOval(markX + 4, markY + 4, 5, 5);
        }

        g2.setFont(GameFonts.regular(15));
        g2.setColor(new Color(48, 37, 28));
        int textY = y + 20;
        for (String line : lines) {
            g2.drawString(line, x + 28, textY);
            textY += 18;
        }
    }

    private void drawControlHints() {
        if (controlHintCounter <= 0) {
            return;
        }

        int width = 430;
        int height = 168;
        int x = 24;
        int y = gp.screenHeight - height - 24;
        int alpha = controlHintCounter < 45 ? Math.max(0, controlHintCounter * 5) : 224;

        Composite oldComposite = g2.getComposite();
        Stroke oldStroke = g2.getStroke();
        g2.setComposite(AlphaComposite.SrcOver.derive(Math.min(1f, alpha / 255f)));

        g2.setColor(new Color(0, 0, 0, 88));
        g2.fillRoundRect(x + 7, y + 9, width, height, 16, 16);
        g2.setColor(new Color(8, 12, 15, 218));
        g2.fillRoundRect(x, y, width, height, 16, 16);
        g2.setColor(new Color(174, 215, 196, 145));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x + 3, y + 3, width - 6, height - 6, 13, 13);

        g2.setFont(GameFonts.bold(18));
        g2.setColor(new Color(236, 246, 241));
        g2.drawString(t("Быстрые подсказки", "Quick Tips"), x + 20, y + 30);

        drawControlHintRow(t("WASD / стрелки", "WASD / arrows"), t("ходьба", "walk"), x + 20, y + 58);
        drawControlHintRow("Shift", t("бег", "run"), x + 20, y + 82);
        drawControlHintRow("E", t("взаимодействовать", "interact"), x + 20, y + 106);
        drawControlHintRow("I", t("список задач", "task list"), x + 20, y + 130);

        g2.setFont(GameFonts.regular(12));
        g2.setColor(new Color(190, 207, 199));
        g2.drawString(t("Подсказка исчезнет сама", "This hint will fade on its own"), x + 20, y + 152);

        g2.setComposite(oldComposite);
        g2.setStroke(oldStroke);
        controlHintCounter--;
    }

    private void drawControlHintRow(String key, String label, int x, int y) {
        g2.setFont(GameFonts.bold(14));
        g2.setColor(new Color(255, 222, 151));
        g2.drawString(key, x, y);

        g2.setFont(GameFonts.regular(14));
        g2.setColor(new Color(220, 232, 226));
        g2.drawString(label, x + 170, y);
    }

    private void drawPauseScreen() {
        g2.setColor(new Color(0, 0, 0, 115));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int panelWidth = 430;
        int panelHeight = 470;
        int panelX = gp.screenWidth / 2 - panelWidth / 2;
        int panelY = gp.screenHeight / 2 - panelHeight / 2;

        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(panelX + 10, panelY + 12, panelWidth, panelHeight, 28, 28);
        drawSubWindow(panelX, panelY, panelWidth, panelHeight, new Color(8, 14, 17, 226));

        g2.setColor(new Color(174, 215, 196, 75));
        g2.fillRoundRect(panelX + 24, panelY + 24, panelWidth - 48, 82, 18, 18);

        g2.setFont(GameFonts.bold(38));
        String text = t("ПАУЗА", "PAUSE");
        drawShadowedString(text, getXforCenteredText(text), panelY + 66, Color.white, new Color(0, 0, 0, 160));

        g2.setFont(GameFonts.regular(15));
        g2.setColor(new Color(210, 225, 218));
        String place = t(gp.story.getLocationTitle());
        g2.drawString(place, panelX + 34, panelY + 94);

        int menuY = panelY + 158;
        drawPauseMenuItem(t("ПРОДОЛЖИТЬ", "CONTINUE"), 0, panelX + 54, menuY, panelWidth - 108);
        drawPauseMenuItem(t("СОХРАНИТЬ", "SAVE"), 1, panelX + 54, menuY + 46, panelWidth - 108);
        drawPauseMenuItem(t("ЗАГРУЗИТЬ", "LOAD"), 2, panelX + 54, menuY + 92, panelWidth - 108);
        drawPauseMenuItem(t("НАСТРОЙКИ", "SETTINGS"), 3, panelX + 54, menuY + 138, panelWidth - 108);
        drawPauseMenuItem(t("НОВАЯ ИГРА", "NEW GAME"), 4, panelX + 54, menuY + 184, panelWidth - 108);
        drawPauseMenuItem(t("В ГЛАВНОЕ МЕНЮ", "MAIN MENU"), 5, panelX + 54, menuY + 230, panelWidth - 108);

        g2.setFont(GameFonts.regular(14));
        g2.setColor(new Color(195, 208, 202));
        g2.drawString(t("Esc - вернуться    Enter - выбрать", "Esc - back    Enter - select"),
                panelX + 54, panelY + panelHeight - 28);

        if (!pauseNotice.isEmpty() && pauseNoticeCounter > 0) {
            g2.setFont(GameFonts.bold(16));
            String noticeText = t(pauseNotice);
            int noticeWidth = g2.getFontMetrics().stringWidth(noticeText) + 36;
            int noticeX = gp.screenWidth / 2 - noticeWidth / 2;
            int noticeY = panelY - 46;
            g2.setColor(new Color(6, 10, 12, 220));
            g2.fillRoundRect(noticeX, noticeY, noticeWidth, 34, 16, 16);
            g2.setColor(new Color(174, 215, 196));
            g2.drawString(noticeText, noticeX + 18, noticeY + 23);
            pauseNoticeCounter--;
        }
    }

    private void drawOptionsScreen() {
        if (gp.optionsReturnState != gp.pauseState) {
            fillVerticalGradient(0, 0, gp.screenWidth, gp.screenHeight,
                    new Color(19, 25, 31), new Color(48, 62, 55));
        }

        g2.setColor(new Color(0, 0, 0, 135));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int panelWidth = OPTIONS_PANEL_WIDTH;
        int panelHeight = OPTIONS_PANEL_HEIGHT;
        int panelX = gp.screenWidth / 2 - panelWidth / 2;
        int panelY = gp.screenHeight / 2 - panelHeight / 2;

        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(panelX + 10, panelY + 12, panelWidth, panelHeight, 18, 18);
        drawSubWindow(panelX, panelY, panelWidth, panelHeight, new Color(8, 14, 17, 238));

        g2.setFont(GameFonts.bold(38));
        String title = t("НАСТРОЙКИ", "SETTINGS");
        drawShadowedString(title, getXforCenteredText(title), panelY + 62, Color.white, new Color(0, 0, 0, 160));

        int tabY = panelY + 84;
        int tabX = panelX + 36;
        int tabWidth = 174;
        drawOptionsTab(t("Графика", "Graphics"), OPTIONS_TAB_GRAPHICS, tabX, tabY, tabWidth);
        drawOptionsTab(t("Звук", "Sound"), OPTIONS_TAB_SOUND, tabX + tabWidth + 12, tabY, tabWidth);
        drawOptionsTab(t("Чат", "Chat"), OPTIONS_TAB_CHAT, tabX + (tabWidth + 12) * 2, tabY, tabWidth);

        int rowX = panelX + 52;
        int rowY = panelY + 162;
        int rowWidth = panelWidth - 104;
        int rowStep = 55;

        drawVolumeOption(t("Музыка", "Music"), gp.music.volumeScale, 0, rowX, rowY, rowWidth);
        drawVolumeOption(t("Звуки", "Sounds"), gp.se.volumeScale, 1, rowX, rowY + rowStep, rowWidth);
        drawToggleOption(t("Полный экран", "Fullscreen"), gp.fullScreenOn, 2, rowX, rowY + rowStep * 2, rowWidth);
        drawBackOption(t("НАЗАД", "BACK"), 3, rowX, rowY + rowStep * 3, rowWidth);
    }

    private void drawTabbedOptionsScreen() {
        if (gp.optionsReturnState != gp.pauseState) {
            fillVerticalGradient(0, 0, gp.screenWidth, gp.screenHeight,
                    new Color(19, 25, 31), new Color(48, 62, 55));
        }

        g2.setColor(new Color(0, 0, 0, 135));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int panelX = getOptionsPanelX();
        int panelY = getOptionsPanelY();

        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(panelX + 10, panelY + 12, OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_HEIGHT, 18, 18);
        drawSubWindow(panelX, panelY, OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_HEIGHT, new Color(8, 14, 17, 238));

        g2.setFont(GameFonts.bold(38));
        drawShadowedString(t("НАСТРОЙКИ", "SETTINGS"), panelX + 36, panelY + 56,
                Color.white, new Color(0, 0, 0, 160));

        int tabY = getOptionsTabY();
        int tabX = getOptionsTabX();
        int tabWidth = getOptionsTabWidth();
        drawOptionsTab(t("Графика", "Graphics"), OPTIONS_TAB_GRAPHICS, tabX, tabY, tabWidth);
        drawOptionsTab(t("Звук", "Sound"), OPTIONS_TAB_SOUND, tabX + tabWidth + 12, tabY, tabWidth);
        drawOptionsTab(t("Чат", "Chat"), OPTIONS_TAB_CHAT, tabX + (tabWidth + 12) * 2, tabY, tabWidth);

        int rowX = getOptionsRowX();
        int rowY = getOptionsRowY();
        int rowWidth = getOptionsRowWidth();

        if (optionsTab == OPTIONS_TAB_GRAPHICS) {
            drawToggleOption(t("Полный экран", "Fullscreen"), gp.fullScreenOn, 0, rowX, rowY, rowWidth);
            drawSliderOption(t("Яркость", "Brightness"), gp.brightnessScale, 5, 1, rowX, rowY + OPTIONS_ROW_STEP, rowWidth);
            drawCycleOption(t("Лимит FPS", "FPS limit"), gp.getFpsLimitLabel(), 2, rowX, rowY + OPTIONS_ROW_STEP * 2, rowWidth);
            drawToggleOption(t("Показ FPS", "Show FPS"), gp.showFpsCounter, 3,
                    rowX, rowY + OPTIONS_ROW_STEP * 3, rowWidth);
            drawBackOption(t("НАЗАД", "BACK"), 4, rowX, rowY + OPTIONS_ROW_STEP * 4, rowWidth);
        }
        else if (optionsTab == OPTIONS_TAB_SOUND) {
            drawSliderOption(t("Музыка", "Music"), gp.music.volumeScale, 5, 0, rowX, rowY, rowWidth);
            drawSliderOption(t("Эффекты", "Effects"), gp.se.volumeScale, 5, 1, rowX, rowY + OPTIONS_ROW_STEP, rowWidth);
            drawSliderOption(t("Окружение", "Ambience"), gp.ambienceVolumeScale, 5, 2, rowX, rowY + OPTIONS_ROW_STEP * 2, rowWidth);
            drawSliderOption(t("Шаги", "Footsteps"), gp.footstepVolumeScale, 5, 3, rowX, rowY + OPTIONS_ROW_STEP * 3, rowWidth);
            drawSliderOption(t("Интерфейс", "Interface"), gp.uiVolumeScale, 5, 4, rowX, rowY + OPTIONS_ROW_STEP * 4, rowWidth);
            drawSliderOption(t("Шепоты", "Whispers"), gp.whisperVolumeScale, 5, 5, rowX, rowY + OPTIONS_ROW_STEP * 5, rowWidth);
            drawBackOption(t("НАЗАД", "BACK"), 6, rowX, rowY + OPTIONS_ROW_STEP * 6, rowWidth);
        }
        else {
            drawCycleOption(t("Язык", "Language"), gp.getLanguageLabel(), 0, rowX, rowY, rowWidth);
            drawCycleOption(t("Размер текста", "Text size"), gp.getDialogueTextSizeLabel(), 1,
                    rowX, rowY + OPTIONS_ROW_STEP, rowWidth);
            drawCycleOption(t("Скорость текста", "Text speed"), gp.getDialogueTextSpeedLabel(), 2,
                    rowX, rowY + OPTIONS_ROW_STEP * 2, rowWidth);
            drawToggleOption(t("Высокий контраст", "High contrast"), gp.highContrastDialogue, 3,
                    rowX, rowY + OPTIONS_ROW_STEP * 3, rowWidth);
            drawBackOption(t("НАЗАД", "BACK"), 4, rowX, rowY + OPTIONS_ROW_STEP * 4, rowWidth);
        }

        g2.setFont(GameFonts.regular(14));
        g2.setColor(new Color(176, 190, 184));
        g2.drawString(t("Q / Tab - вкладки, стрелки - значение, E / Enter - выбрать",
                        "Q / Tab - tabs, arrows - value, E / Enter - select"),
                panelX + 36, panelY + OPTIONS_PANEL_HEIGHT - 24);
    }

    public int getOptionsPanelX() {
        return gp.screenWidth / 2 - OPTIONS_PANEL_WIDTH / 2;
    }

    public int getOptionsPanelY() {
        return gp.screenHeight / 2 - OPTIONS_PANEL_HEIGHT / 2;
    }

    public int getOptionsRowX() {
        return getOptionsPanelX() + 52;
    }

    public int getOptionsRowY() {
        return getOptionsPanelY() + 162;
    }

    public int getOptionsRowWidth() {
        return OPTIONS_PANEL_WIDTH - 104;
    }

    public int getOptionsTabX() {
        return getOptionsPanelX() + 36;
    }

    public int getOptionsTabY() {
        return getOptionsPanelY() + 84;
    }

    public int getOptionsTabWidth() {
        return 174;
    }

    public Rectangle getOptionsTabBounds(int tab) {
        return new Rectangle(getOptionsTabX() + tab * (getOptionsTabWidth() + 12),
                getOptionsTabY(), getOptionsTabWidth(), 36);
    }

    public Rectangle getOptionsCommandBounds(int command) {
        return new Rectangle(getOptionsRowX() - 16,
                getOptionsRowY() + command * OPTIONS_ROW_STEP - 31,
                getOptionsRowWidth(),
                OPTIONS_ROW_HEIGHT);
    }

    private void drawDialogueScreen() {
        StoryManager.StoryPrompt prompt = gp.story.getActivePrompt();
        if (gp.story.isPhoneResultOpen()) {
            drawPhoneResultScreen();
            return;
        }
        if (gp.story.isPhonePrompt(prompt)) {
            drawPhoneDialogueScreen(prompt);
            return;
        }

        boolean hasChoices = prompt != null;
        boolean lockedInteraction = gp.story.isDialogueLocked();

        int x = gp.tileSize;
        int width = gp.screenWidth - gp.tileSize * 2;
        int textWidth = width - 56;
        int maxHeight = gp.screenHeight - 48;
        int minHeight = hasChoices ? 270 : 190;

        String speaker = t(hasChoices ? prompt.speaker : gp.story.getMessageSpeaker());
        String fullText = t(hasChoices ? prompt.text : gp.story.getMessageText());
        String text;
        if (hasChoices && gp.story.shouldTypePrompt(prompt)) {
            text = revealDialogueText("dialogue|" + gp.languageMode + "|" + speaker + "|" + fullText, fullText);
        }
        else {
            dialogueRevealKey = "event|" + gp.languageMode + "|" + speaker + "|" + fullText;
            dialogueRevealChars = fullText == null ? 0 : fullText.length();
            dialogueRevealComplete = true;
            text = fullText;
        }
        int textSizeDelta = gp.getDialogueTextSizeDelta();
        int bodySize = (hasChoices ? 24 : 26) + textSizeDelta;
        int choiceSize = 21 + textSizeDelta;
        Font bodyFont;
        Font choiceFont;
        ArrayList<String> textLines;
        int bodyLineHeight;
        int choiceLineHeight;
        int choiceGap = 8;
        int contentHeight;

        while (true) {
            bodyFont = GameFonts.regular(bodySize);
            choiceFont = GameFonts.bold(choiceSize);
            bodyLineHeight = Math.max(23, bodySize + 6);
            choiceLineHeight = Math.max(20, choiceSize + 4);
            textLines = wrapTextLines(text, textWidth, bodyFont);

            contentHeight = 78 + measureLinesHeight(textLines, bodyLineHeight) + 24;
            if (hasChoices) {
                contentHeight += 14 + measureChoicesHeight(prompt, textWidth, choiceFont, choiceLineHeight, choiceGap);
            }
            if (lockedInteraction) {
                contentHeight += 44;
            }

            if (contentHeight <= maxHeight || bodySize <= 18) {
                break;
            }
            bodySize--;
            if (choiceSize > 17) {
                choiceSize--;
            }
        }

        int height = Math.min(maxHeight, Math.max(minHeight, contentHeight));
        int y = Math.max(24, gp.screenHeight - height - 24);

        drawSubWindow(x, y, width, height, gp.highContrastDialogue
                ? new Color(1, 4, 7, 244)
                : new Color(5, 8, 12, 220));

        Shape oldClip = g2.getClip();
        g2.clipRect(x + 12, y + 12, width - 24, height - 24);

        int textX = x + 28;
        int textY = y + 42;
        int bottomY = y + height - (lockedInteraction ? 70 : 28);

        g2.setFont(GameFonts.bold(22));
        g2.setColor(new Color(174, 215, 196));
        g2.drawString(trimToWidth(speaker, textWidth), textX, textY);

        g2.setFont(bodyFont);
        g2.setColor(Color.white);
        textY += 36;
        int nextY = drawTextLines(textLines, textX, textY, textWidth, bodyLineHeight, bottomY);

        if (hasChoices && dialogueRevealComplete) {
            drawChoices(prompt, textX, Math.max(nextY + 12, y + 134), textWidth,
                    choiceFont, choiceLineHeight, choiceGap, bottomY);
        }
        if (lockedInteraction) {
            drawDialogueProgress(textX, y + height - 42, textWidth);
        }
        g2.setClip(oldClip);
    }

    private void drawPhoneDialogueScreen(StoryManager.StoryPrompt prompt) {
        Rectangle phoneScreen = drawPhoneFrame();
        int screenX = phoneScreen.x;
        int screenY = phoneScreen.y;
        int screenWidth = phoneScreen.width;
        int screenHeight = phoneScreen.height;

        drawPhoneStatusBar(screenX, screenY, screenWidth);
        drawPhoneHeader(t(prompt.speaker), screenX, screenY + 30, screenWidth);

        Shape oldClip = g2.getClip();
        g2.clipRect(screenX + 10, screenY + 74, screenWidth - 20, screenHeight - 86);

        int contentX = screenX + 18;
        int contentY = screenY + 92;
        int messageWidth = screenWidth - 78;
        g2.setFont(GameFonts.regular(15));

        String revealedText;
        String phoneText = t(prompt.text);
        if (gp.story.shouldTypePrompt(prompt)) {
            revealedText = revealDialogueText("phone|" + gp.languageMode + "|" + phoneText, phoneText);
        }
        else {
            dialogueRevealKey = "phone|" + gp.languageMode + "|" + phoneText;
            dialogueRevealChars = phoneText == null ? 0 : phoneText.length();
            dialogueRevealComplete = true;
            revealedText = phoneText;
        }
        String[] messages = revealedText.split("\\n");
        for (String messageLine : messages) {
            contentY = drawPhoneBubble(stripPhoneSpeaker(messageLine), contentX, contentY, messageWidth, true);
        }

        int choicesY = screenY + screenHeight - 166;
        g2.setClip(oldClip);
        g2.clipRect(screenX + 10, choicesY - 8, screenWidth - 20, screenY + screenHeight - choicesY - 8);

        if (dialogueRevealComplete) {
            drawPhoneChoices(prompt, screenX + 18, choicesY, screenWidth - 36);
        }
        g2.setClip(oldClip);
    }

    private void drawPhoneResultScreen() {
        Rectangle phoneScreen = drawPhoneFrame();
        int screenX = phoneScreen.x;
        int screenY = phoneScreen.y;
        int screenWidth = phoneScreen.width;
        int screenHeight = phoneScreen.height;

        drawPhoneStatusBar(screenX, screenY, screenWidth);
        drawPhoneHeader(t("Мама", "Mom"), screenX, screenY + 30, screenWidth);

        Shape oldClip = g2.getClip();
        g2.clipRect(screenX + 10, screenY + 74, screenWidth - 20, screenHeight - 86);

        int contentX = screenX + 18;
        int contentY = screenY + 92;
        int messageWidth = screenWidth - 78;

        for (String messageLine : gp.story.getPhoneIntroMessages()) {
            contentY = drawPhoneBubble(stripPhoneSpeaker(t(messageLine)), contentX, contentY, messageWidth, true);
        }

        String playerText = t(gp.story.getPhoneResultPlayerText());
        if (playerText.isEmpty()) {
            contentY = drawPhoneSystemLine(t("чат закрыт без ответа", "chat closed without an answer"),
                    contentX, contentY + 2, messageWidth);
        }
        else {
            contentY = drawPhoneBubble(playerText, contentX, contentY + 2, messageWidth, false);
        }

        String momText = t(gp.story.getPhoneResultMomText());
        if (!momText.isEmpty()) {
            drawPhoneBubble(momText, contentX, contentY + 4, messageWidth, true);
        }

        g2.setClip(oldClip);
    }

    private Rectangle drawPhoneFrame() {
        g2.setColor(new Color(0, 0, 0, gp.highContrastDialogue ? 190 : 145));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int phoneWidth = Math.min(390, gp.screenWidth - 80);
        int phoneHeight = Math.min(560, gp.screenHeight - 48);
        int phoneX = gp.screenWidth / 2 - phoneWidth / 2;
        int phoneY = gp.screenHeight / 2 - phoneHeight / 2;

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(phoneX + 12, phoneY + 14, phoneWidth, phoneHeight, 34, 34);
        g2.setColor(gp.highContrastDialogue ? new Color(5, 8, 12) : new Color(18, 22, 29));
        g2.fillRoundRect(phoneX, phoneY, phoneWidth, phoneHeight, 34, 34);
        g2.setColor(new Color(58, 65, 77));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(phoneX + 2, phoneY + 2, phoneWidth - 4, phoneHeight - 4, 31, 31);

        int screenX = phoneX + 16;
        int screenY = phoneY + 18;
        int screenWidth = phoneWidth - 32;
        int screenHeight = phoneHeight - 36;

        g2.setColor(gp.highContrastDialogue ? new Color(2, 5, 8) : new Color(12, 18, 23));
        g2.fillRoundRect(screenX, screenY, screenWidth, screenHeight, 22, 22);

        return new Rectangle(screenX, screenY, screenWidth, screenHeight);
    }

    private String stripPhoneSpeaker(String messageLine) {
        int colon = messageLine.indexOf(':');
        if (colon >= 0 && colon + 1 < messageLine.length()) {
            return messageLine.substring(colon + 1).trim();
        }
        return messageLine;
    }

    private String revealDialogueText(String key, String text) {
        if (text == null) {
            dialogueRevealComplete = true;
            return "";
        }
        if (gp.dialogueTextSpeedMode == 3) {
            dialogueRevealKey = key;
            dialogueRevealChars = text.length();
            dialogueRevealComplete = true;
            return text;
        }
        if (!key.equals(dialogueRevealKey)) {
            dialogueRevealKey = key;
            dialogueRevealChars = 0;
        }

        int previousChars = dialogueRevealChars;
        if (dialogueRevealChars < text.length()) {
            dialogueRevealChars = Math.min(text.length(), dialogueRevealChars + gp.getDialogueRevealCharsPerFrame());
        }
        if (dialogueRevealChars > previousChars) {
            gp.playDialogueTypeSE();
        }
        dialogueRevealComplete = dialogueRevealChars >= text.length();
        return text.substring(0, Math.min(dialogueRevealChars, text.length()));
    }

    private void drawPhoneStatusBar(int x, int y, int width) {
        g2.setFont(GameFonts.bold(12));
        g2.setColor(new Color(203, 213, 214));
        g2.drawString("09:17", x + 18, y + 20);

        int batteryX = x + width - 42;
        int batteryY = y + 10;
        g2.drawRoundRect(batteryX, batteryY, 20, 10, 4, 4);
        g2.fillRect(batteryX + 22, batteryY + 3, 2, 4);
        g2.setColor(new Color(174, 215, 196));
        g2.fillRoundRect(batteryX + 3, batteryY + 3, 14, 4, 3, 3);
    }

    private void drawPhoneHeader(String contact, int x, int y, int width) {
        g2.setColor(new Color(20, 29, 36));
        g2.fillRoundRect(x + 8, y, width - 16, 44, 16, 16);
        g2.setColor(new Color(174, 215, 196, 70));
        g2.fillOval(x + 22, y + 8, 28, 28);
        g2.setColor(new Color(174, 215, 196));
        g2.fillOval(x + 31, y + 16, 10, 10);
        g2.fillArc(x + 27, y + 23, 18, 13, 0, 180);

        g2.setFont(GameFonts.bold(18));
        g2.setColor(new Color(236, 244, 240));
        g2.drawString(contact, x + 62, y + 21);
        g2.setFont(GameFonts.regular(12));
        g2.setColor(new Color(153, 170, 166));
        g2.drawString(t("сообщение от мамы", "message from mom"), x + 62, y + 36);
    }

    private int drawPhoneBubble(String text, int x, int y, int maxWidth, boolean incoming) {
        Font font = GameFonts.regular(15 + gp.getDialogueTextSizeDelta());
        FontMetrics metrics = g2.getFontMetrics(font);
        ArrayList<String> lines = wrapTextLines(text, maxWidth - 24, font);
        int lineHeight = 20 + Math.max(0, gp.getDialogueTextSizeDelta());
        int bubbleWidth = 0;
        for (String line : lines) {
            bubbleWidth = Math.max(bubbleWidth, metrics.stringWidth(line));
        }
        bubbleWidth = Math.min(maxWidth, Math.max(86, bubbleWidth + 24));
        int bubbleHeight = Math.max(34, measureLinesHeight(lines, lineHeight) + 14);
        int bubbleX = incoming ? x : x + maxWidth - bubbleWidth;

        g2.setColor(incoming ? new Color(35, 47, 56) : new Color(65, 101, 88));
        g2.fillRoundRect(bubbleX, y, bubbleWidth, bubbleHeight, 14, 14);
        g2.setColor(new Color(255, 255, 255, 24));
        g2.drawRoundRect(bubbleX + 1, y + 1, bubbleWidth - 2, bubbleHeight - 2, 13, 13);

        g2.setFont(font);
        g2.setColor(new Color(239, 244, 241));
        int textY = y + 22;
        for (String line : lines) {
            g2.drawString(line, bubbleX + 12, textY);
            textY += lineHeight;
        }
        return y + bubbleHeight + 10;
    }

    private int drawPhoneSystemLine(String text, int x, int y, int width) {
        Font font = GameFonts.regular(12);
        FontMetrics metrics = g2.getFontMetrics(font);
        int textX = x + Math.max(0, (width - metrics.stringWidth(text)) / 2);

        g2.setFont(font);
        g2.setColor(new Color(153, 170, 166));
        g2.drawString(text, textX, y + 16);

        return y + 26;
    }

    private void drawPhoneChoices(StoryManager.StoryPrompt prompt, int x, int y, int width) {
        Font font = GameFonts.bold(14 + gp.getDialogueTextSizeDelta());
        int lineHeight = 18 + Math.max(0, gp.getDialogueTextSizeDelta());
        int gap = 7;

        for (int i = 0; i < prompt.choices.length; i++) {
            StoryManager.Choice choice = prompt.choices[i];
            ArrayList<String> lines = wrapTextLines(t(choice.text), width - 44, font);
            int rowHeight = Math.max(38, measureLinesHeight(lines, lineHeight) + 14);
            boolean selected = gp.story.selectedChoice == i;

            g2.setColor(selected ? new Color(174, 215, 196, 88) : new Color(255, 255, 255, 28));
            g2.fillRoundRect(x, y, width, rowHeight, 14, 14);
            if (selected) {
                g2.setColor(new Color(174, 215, 196));
                g2.fillRoundRect(x, y, 4, rowHeight, 4, 4);
            }

            g2.setFont(font);
            g2.setColor(selected ? new Color(240, 250, 246) : new Color(212, 222, 218));
            int textY = y + 23;
            for (String line : lines) {
                g2.drawString(line, x + 18, textY);
                textY += lineHeight;
            }
            y += rowHeight + gap;
        }
    }

    private void drawDialogueProgress(int x, int y, int width) {
        int spinnerSize = 20;
        int barX = x + spinnerSize + 18;
        int barY = y - 12;
        int barWidth = width - spinnerSize - 18;
        int barHeight = 10;
        float progress = gp.story.getDialogueLockProgress();
        int fillWidth = Math.max(0, Math.min(barWidth, Math.round(barWidth * progress)));

        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(255, 255, 255, 42));
        g2.drawOval(x, y - spinnerSize, spinnerSize, spinnerSize);
        g2.setColor(new Color(174, 215, 196, 230));
        g2.drawArc(x, y - spinnerSize, spinnerSize, spinnerSize, (dialogueSpinnerFrame * 18) % 360, 240);
        g2.setStroke(oldStroke);

        g2.setColor(new Color(255, 255, 255, 38));
        g2.fillRoundRect(barX, barY, barWidth, barHeight, 8, 8);
        g2.setColor(new Color(174, 215, 196, 220));
        g2.fillRoundRect(barX, barY, fillWidth, barHeight, 8, 8);
        g2.setColor(new Color(255, 255, 255, 52));
        g2.drawRoundRect(barX, barY, barWidth, barHeight, 8, 8);

        dialogueSpinnerFrame++;
    }

    private void drawIntroDisclaimer() {
        int frame = gp.getIntroFrame();
        int menuFrames = gp.getIntroFlowFrames();
        int typeFrames = gp.getIntroTypeFrames();
        int holdFrames = gp.getIntroHoldFrames();

        if (frame < menuFrames) {
            drawIntroMenuFall(frame, menuFrames);
            return;
        }

        int disclaimerFrame = frame - menuFrames;
        int fadeStartFrame = menuFrames + typeFrames + holdFrames;
        if (frame < fadeStartFrame) {
            drawIntroDisclaimerDialogue(disclaimerFrame, typeFrames);
            return;
        }

        int fadeFrame = frame - fadeStartFrame;
        float progress = Math.min(1f, fadeFrame / (float) Math.max(1, gp.getIntroFadeFrames()));
        int alpha = Math.round(255 * (1f - easeInOut(progress)));
        g2.setColor(new Color(0, 0, 0, Math.max(0, Math.min(255, alpha))));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }

    private void drawIntroMenuFall(int frame, int duration) {
        float progress = Math.min(1f, frame / (float) Math.max(1, duration));
        float eased = easeIn(progress);
        float fastEase = easeOut(progress);

        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int screenRise = Math.round(fastEase * gp.screenHeight * 1.08f);
        int objectFall = Math.round(eased * gp.screenHeight * 0.88f);

        AffineTransform oldTransform = g2.getTransform();
        Composite oldComposite = g2.getComposite();

        g2.translate(0, -screenRise);
        drawTitleBackground();
        g2.setTransform(oldTransform);

        g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, 1f - progress * 0.35f)));
        g2.translate(0, objectFall);
        drawTitleBrand();
        if (isTitleSlotMenu()) {
            drawTitleSlotMenu();
        }
        else {
            drawTitleMenuItem(getMainTitleMenuLabel(0), 0);
            drawTitleMenuItem(getMainTitleMenuLabel(1), 1);
            drawTitleMenuItem(t("НАСТРОЙКИ", "SETTINGS"), 2);
            drawTitleMenuItem(t("ВЫЙТИ", "EXIT"), 3);
        }
        g2.setTransform(oldTransform);
        g2.setComposite(oldComposite);

        int blackAlpha = Math.round(255 * Math.max(0f, (progress - 0.34f) / 0.66f));
        g2.setColor(new Color(0, 0, 0, Math.min(255, blackAlpha)));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }

    private void drawIntroDisclaimerDialogue(int frame, int typeFrames) {
        if (frame < lastIntroFrame) {
            resetIntroAnimation();
        }
        lastIntroFrame = frame;

        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int startDelay = 18;
        int typeFrame = Math.max(0, frame - startDelay);
        String disclaimerText = t(INTRO_DISCLAIMER_TEXT);
        int revealChars = Math.min(disclaimerText.length(), typeFrame * INTRO_TYPE_CHARS_PER_FRAME);
        if (revealChars > introSoundRevealChars) {
            gp.playDialogueTypeSE();
            introSoundRevealChars = revealChars;
        }

        float boxProgress = Math.min(1f, Math.max(0f, frame / 28f));
        int alpha = Math.round(235 * easeOut(boxProgress));
        if (alpha <= 0) {
            return;
        }
        int x = gp.tileSize;
        int width = gp.screenWidth - gp.tileSize * 2;
        int height = 205;
        int y = gp.screenHeight - height - 34;
        drawSubWindow(x, y, width, height, new Color(5, 8, 12, alpha));

        g2.setFont(GameFonts.bold(22));
        g2.setColor(new Color(174, 215, 196, alpha));
        g2.drawString(INTRO_DISCLAIMER_SPEAKER, x + 28, y + 42);

        String visibleText = disclaimerText.substring(0, revealChars);
        g2.setFont(GameFonts.regular(26));
        g2.setColor(new Color(245, 248, 246, alpha));
        drawWrappedText(visibleText, x + 28, y + 82, width - 56, 33);

        if (revealChars >= disclaimerText.length() && frame < typeFrames) {
            int dotAlpha = 90 + (int) (Math.sin(frame * 0.18) * 70);
            g2.setColor(new Color(255, 222, 151, Math.max(35, dotAlpha)));
            g2.fillOval(x + width - 52, y + height - 42, 8, 8);
        }
    }

    private void drawResultScreen() {
        g2.setColor(new Color(10, 12, 15, 235));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(GameFonts.bold(46));
        String text = t("РЕЗУЛЬТАТ", "RESULT");
        drawShadowedString(text, getXforCenteredText(text), 74, Color.white, Color.black);

        int x = 70;
        int y = 116;
        int width = 360;
        drawMetricBar(t("Рост", "Growth"), gp.story.growth, x, y, width); y += 48;
        drawMetricBar(t("Покой", "Calm"), gp.story.calm, x, y, width); y += 48;
        drawMetricBar(t("Эмпатия", "Empathy"), gp.story.empathy, x, y, width); y += 48;
        drawMetricBar(t("Уверенность", "Confidence"), gp.story.confidence, x, y, width);

        int frameX = 500;
        int frameY = 116;
        int frameWidth = 390;
        int frameHeight = 290;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight, new Color(18, 24, 28, 210));

        g2.setFont(GameFonts.bold(25));
        g2.setColor(new Color(174, 215, 196));
        g2.drawString(t(gp.story.getProfileTitle()), frameX + 26, frameY + 42);

        g2.setFont(GameFonts.regular(20));
        g2.setColor(Color.white);
        int textY = drawWrappedText(t(gp.story.getProfileText()), frameX + 26, frameY + 78, frameWidth - 52, 27);

        g2.setFont(GameFonts.bold(20));
        g2.setColor(new Color(174, 215, 196));
        g2.drawString(t("Рекомендация", "Recommendation"), frameX + 26, textY + 18);

        g2.setFont(GameFonts.regular(19));
        g2.setColor(Color.white);
        drawWrappedText(t(gp.story.getRecommendation()), frameX + 26, textY + 48, frameWidth - 52, 26);

        g2.setFont(GameFonts.bold(27));
        drawResultMenuItem(t("ПРОЙТИ ЕЩЁ РАЗ", "PLAY AGAIN"), 0, gp.screenHeight - 96);
        drawResultMenuItem(t("В ГЛАВНОЕ МЕНЮ", "MAIN MENU"), 1, gp.screenHeight - 54);
    }

    private void drawChoices(StoryManager.StoryPrompt prompt, int x, int y, int width,
                             Font font, int lineHeight, int gap, int bottomY) {
        g2.setFont(font);
        int textX = x + 28;
        int textWidth = width - 44;
        for (int i = 0; i < prompt.choices.length; i++) {
            StoryManager.Choice choice = prompt.choices[i];
            ArrayList<String> lines = wrapTextLines(t(choice.text), textWidth, font);
            int rowHeight = Math.max(31, measureLinesHeight(lines, lineHeight) + 8);
            if (y + rowHeight - lineHeight > bottomY) {
                break;
            }

            if (gp.story.selectedChoice == i) {
                g2.setColor(new Color(174, 215, 196, 70));
                g2.fillRoundRect(x - 12, y - lineHeight + 4, width, rowHeight, 10, 10);
                g2.setColor(new Color(174, 215, 196));
                g2.drawString(">", x - 2, y);
            }

            g2.setColor(Color.white);
            int lineY = y;
            for (String line : lines) {
                if (!line.isEmpty()) {
                    g2.drawString(line, textX, lineY);
                }
                lineY += line.isEmpty() ? lineHeight / 2 : lineHeight;
            }
            y += rowHeight + gap;
        }
    }

    private void drawMenuItem(String text, int command, int y) {
        int x = getXforCenteredText(text);
        Color color = commandNum == command ? new Color(174, 215, 196) : Color.white;
        drawShadowedString(text, x, y, color, Color.black);
        if (commandNum == command) {
            g2.drawString(">", x - 34, y);
        }
    }

    private void drawResultMenuItem(String text, int command, int y) {
        int x = getXforCenteredText(text);
        Color color = commandNum == command ? new Color(174, 215, 196) : Color.white;
        drawShadowedString(text, x, y, color, Color.black);
        if (commandNum == command) {
            g2.drawString(">", x - 34, y);
        }
    }

    private void drawPauseMenuItem(String text, int command, int x, int y, int width) {
        boolean selected = commandNum == command;

        if (selected) {
            g2.setColor(new Color(174, 215, 196, 62));
            g2.fillRoundRect(x - 16, y - 29, width, 38, 14, 14);
            g2.setColor(new Color(174, 215, 196));
            g2.fillRoundRect(x - 16, y - 29, 5, 38, 5, 5);
        }

        g2.setFont(GameFonts.bold(22));
        Color color = selected ? new Color(235, 250, 242) : new Color(205, 216, 211);
        drawShadowedString(text, x + 18, y, color, new Color(0, 0, 0, 140));

        if (selected) {
            g2.setFont(GameFonts.bold(20));
            g2.setColor(new Color(174, 215, 196));
            g2.drawString(">", x - 2, y);
        }
    }

    private void drawOptionsTab(String label, int tab, int x, int y, int width) {
        boolean selected = optionsTab == tab;
        g2.setColor(selected ? new Color(174, 215, 196, 88) : new Color(255, 255, 255, 28));
        g2.fillRoundRect(x, y, width, 36, 12, 12);
        if (selected) {
            g2.setColor(new Color(255, 222, 151, 210));
            g2.fillRoundRect(x + 10, y + 30, width - 20, 3, 3, 3);
        }
        g2.setFont(GameFonts.bold(18));
        g2.setColor(selected ? new Color(244, 253, 248) : new Color(190, 204, 198));
        int textX = x + (width - g2.getFontMetrics().stringWidth(label)) / 2;
        g2.drawString(label, textX, y + 23);
    }

    private void drawSliderOption(String label, int value, int maxValue, int command, int x, int y, int width) {
        drawOptionShell(command, x, y, width);

        g2.setFont(GameFonts.bold(19));
        g2.setColor(commandNum == command ? new Color(235, 250, 242) : new Color(205, 216, 211));
        g2.drawString(label, x + 18, y);

        int barX = x + width - 276;
        int barY = y - 20;
        int blockWidth = 26;
        int blockGap = 7;

        g2.setFont(GameFonts.bold(19));
        g2.drawString("<", barX - 32, y);
        for (int i = 0; i < maxValue; i++) {
            boolean filled = i < value;
            g2.setColor(filled ? new Color(174, 215, 196) : new Color(255, 255, 255, 45));
            g2.fillRoundRect(barX + i * (blockWidth + blockGap), barY, blockWidth, 18, 8, 8);
        }
        g2.setColor(commandNum == command ? new Color(235, 250, 242) : new Color(205, 216, 211));
        g2.drawString(">", barX + maxValue * (blockWidth + blockGap) + 4, y);
    }

    private void drawCycleOption(String label, String value, int command, int x, int y, int width) {
        drawOptionShell(command, x, y, width);

        g2.setFont(GameFonts.bold(19));
        g2.setColor(commandNum == command ? new Color(235, 250, 242) : new Color(205, 216, 211));
        g2.drawString(label, x + 18, y);

        int valueWidth = 190;
        int valueX = x + width - valueWidth - 26;
        g2.setColor(new Color(255, 255, 255, 38));
        g2.fillRoundRect(valueX, y - 28, valueWidth, 34, 12, 12);
        g2.setColor(new Color(174, 215, 196, 70));
        g2.drawRoundRect(valueX, y - 28, valueWidth, 34, 12, 12);
        g2.setFont(GameFonts.bold(16));
        g2.setColor(Color.white);
        g2.drawString(trimToWidth(value, valueWidth - 52), valueX + 26, y - 7);
        g2.setFont(GameFonts.bold(18));
        g2.setColor(commandNum == command ? new Color(235, 250, 242) : new Color(205, 216, 211));
        g2.drawString("<", valueX - 24, y - 6);
        g2.drawString(">", valueX + valueWidth + 12, y - 6);
    }

    private void drawVolumeOption(String label, int value, int command, int x, int y, int width) {
        drawOptionShell(command, x, y, width);

        g2.setFont(GameFonts.bold(21));
        g2.setColor(commandNum == command ? new Color(235, 250, 242) : new Color(205, 216, 211));
        g2.drawString(label, x + 18, y);

        int barX = x + width - 240;
        int barY = y - 20;
        int blockWidth = 28;
        int blockHeight = 18;

        g2.setFont(GameFonts.bold(20));
        g2.drawString("<", barX - 34, y);
        for (int i = 0; i < 5; i++) {
            boolean filled = i < value;
            g2.setColor(filled ? new Color(174, 215, 196) : new Color(255, 255, 255, 45));
            g2.fillRoundRect(barX + i * (blockWidth + 8), barY, blockWidth, blockHeight, 8, 8);
        }
        g2.setColor(commandNum == command ? new Color(235, 250, 242) : new Color(205, 216, 211));
        g2.drawString(">", barX + 5 * (blockWidth + 8) + 8, y);
    }

    private void drawToggleOption(String label, boolean enabled, int command, int x, int y, int width) {
        drawOptionShell(command, x, y, width);

        g2.setFont(GameFonts.bold(21));
        g2.setColor(commandNum == command ? new Color(235, 250, 242) : new Color(205, 216, 211));
        g2.drawString(label, x + 18, y);

        String value = enabled ? t("ВКЛ", "ON") : t("ВЫКЛ", "OFF");
        int toggleWidth = 118;
        int toggleX = x + width - toggleWidth - 26;
        int toggleY = y - 28;
        g2.setColor(enabled ? new Color(174, 215, 196, 95) : new Color(255, 255, 255, 45));
        g2.fillRoundRect(toggleX, toggleY, toggleWidth, 34, 17, 17);
        g2.setColor(enabled ? new Color(174, 215, 196) : new Color(166, 178, 172));
        int knobX = enabled ? toggleX + toggleWidth - 31 : toggleX + 7;
        g2.fillOval(knobX, toggleY + 6, 22, 22);

        g2.setFont(GameFonts.bold(15));
        g2.setColor(Color.white);
        g2.drawString(value, toggleX + 42 - g2.getFontMetrics().stringWidth(value) / 2, y - 6);
    }

    private void drawBackOption(String label, int command, int x, int y, int width) {
        drawOptionShell(command, x, y, width);
        g2.setFont(GameFonts.bold(22));
        Color color = commandNum == command ? new Color(235, 250, 242) : new Color(205, 216, 211);
        drawShadowedString(label, x + 18, y, color, new Color(0, 0, 0, 140));
    }

    private void drawOptionShell(int command, int x, int y, int width) {
        boolean selected = commandNum == command;
        if (selected) {
            g2.setColor(new Color(174, 215, 196, 62));
            g2.fillRoundRect(x - 16, y - 31, width, 42, 15, 15);
            g2.setColor(new Color(174, 215, 196));
            g2.fillRoundRect(x - 16, y - 31, 5, 42, 5, 5);
            g2.setFont(GameFonts.bold(20));
            g2.drawString(">", x - 2, y);
        }
    }

    private void drawMiniMetric(String label, int value, int x, int y) {
        g2.setFont(GameFonts.regular(12));
        g2.setColor(new Color(210, 220, 216));
        g2.drawString(label, x, y);

        int barX = x + 48;
        int barY = y - 10;
        int barWidth = 58;
        g2.setColor(new Color(255, 255, 255, 55));
        g2.fillRoundRect(barX, barY, barWidth, 8, 6, 6);
        g2.setColor(metricColor(value));
        g2.fillRoundRect(barX, barY, value * barWidth / 100, 8, 6, 6);
    }

    private void drawMetricBar(String label, int value, int x, int y, int width) {
        g2.setFont(GameFonts.bold(22));
        g2.setColor(Color.white);
        g2.drawString(label, x, y);

        String number = value + "%";
        g2.drawString(number, x + width - g2.getFontMetrics().stringWidth(number), y);

        int barY = y + 12;
        g2.setColor(new Color(255, 255, 255, 45));
        g2.fillRoundRect(x, barY, width, 16, 12, 12);
        g2.setColor(metricColor(value));
        g2.fillRoundRect(x, barY, value * width / 100, 16, 12, 12);
    }

    private Color metricColor(int value) {
        if (value >= 75) {
            return new Color(122, 196, 151);
        }
        if (value >= 50) {
            return new Color(215, 192, 115);
        }
        return new Color(203, 111, 105);
    }

    private float easeIn(float progress) {
        float clamped = Math.max(0f, Math.min(1f, progress));
        return clamped * clamped * clamped;
    }

    private float easeOut(float progress) {
        float clamped = Math.max(0f, Math.min(1f, progress));
        float inverse = 1f - clamped;
        return 1f - inverse * inverse * inverse;
    }

    private float easeInOut(float progress) {
        float clamped = Math.max(0f, Math.min(1f, progress));
        if (clamped < 0.5f) {
            return 4f * clamped * clamped * clamped;
        }
        float value = -2f * clamped + 2f;
        return 1f - value * value * value / 2f;
    }

    private void fillVerticalGradient(int x, int y, int width, int height, Color top, Color bottom) {
        if (width <= 0 || height <= 0) {
            return;
        }

        BufferedImage gradient = getVerticalGradient(height, top, bottom);
        g2.drawImage(gradient, x, y, width, height, null);
    }

    private void fillHorizontalGradient(int x, int y, int width, int height, Color left, Color right) {
        if (width <= 0 || height <= 0) {
            return;
        }

        BufferedImage gradient = getHorizontalGradient(width, left, right);
        g2.drawImage(gradient, x, y, width, height, null);
    }

    private BufferedImage getVerticalGradient(int height, Color top, Color bottom) {
        String key = "v:" + height + ":" + top.getRGB() + ":" + bottom.getRGB();
        BufferedImage cached = gradientCache.get(key);
        if (cached != null) {
            return cached;
        }

        BufferedImage image = new BufferedImage(1, height, BufferedImage.TYPE_INT_ARGB);
        for (int row = 0; row < height; row++) {
            float progress = height <= 1 ? 1f : row / (float) (height - 1);
            image.setRGB(0, row, interpolateColor(top, bottom, progress).getRGB());
        }
        gradientCache.put(key, image);
        return image;
    }

    private BufferedImage getHorizontalGradient(int width, Color left, Color right) {
        String key = "h:" + width + ":" + left.getRGB() + ":" + right.getRGB();
        BufferedImage cached = gradientCache.get(key);
        if (cached != null) {
            return cached;
        }

        BufferedImage image = new BufferedImage(width, 1, BufferedImage.TYPE_INT_ARGB);
        for (int col = 0; col < width; col++) {
            float progress = width <= 1 ? 1f : col / (float) (width - 1);
            image.setRGB(col, 0, interpolateColor(left, right, progress).getRGB());
        }
        gradientCache.put(key, image);
        return image;
    }

    private Color interpolateColor(Color from, Color to, float progress) {
        float clampedProgress = Math.max(0f, Math.min(1f, progress));
        int red = Math.round(from.getRed() + (to.getRed() - from.getRed()) * clampedProgress);
        int green = Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * clampedProgress);
        int blue = Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * clampedProgress);
        int alpha = Math.round(from.getAlpha() + (to.getAlpha() - from.getAlpha()) * clampedProgress);
        return new Color(red, green, blue, alpha);
    }

    private void drawSubWindow(int x, int y, int width, int height, Color color) {
        g2.setColor(color);
        g2.fillRoundRect(x, y, width, height, 18, 18);

        g2.setColor(new Color(255, 255, 255, 185));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x + 4, y + 4, width - 8, height - 8, 14, 14);
    }

    private int measureChoicesHeight(StoryManager.StoryPrompt prompt, int width, Font font, int lineHeight, int gap) {
        int height = 0;
        int textWidth = width - 44;
        for (int i = 0; i < prompt.choices.length; i++) {
            ArrayList<String> lines = wrapTextLines(t(prompt.choices[i].text), textWidth, font);
            height += Math.max(31, measureLinesHeight(lines, lineHeight) + 8);
            if (i < prompt.choices.length - 1) {
                height += gap;
            }
        }
        return height;
    }

    private int measureLinesHeight(ArrayList<String> lines, int lineHeight) {
        int height = 0;
        for (String line : lines) {
            height += line.isEmpty() ? lineHeight / 2 : lineHeight;
        }
        return height;
    }

    private int drawWrappedText(String text, int x, int y, int maxWidth, int lineHeight) {
        ArrayList<String> lines = wrapTextLines(text, maxWidth, g2.getFont());
        return drawTextLines(lines, x, y, maxWidth, lineHeight, Integer.MAX_VALUE);
    }

    private int drawTextLines(ArrayList<String> lines, int x, int y, int maxWidth, int lineHeight, int bottomY) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int nextY = y + (line.isEmpty() ? lineHeight / 2 : lineHeight);
            if (y > bottomY) {
                return y;
            }
            if (nextY > bottomY && i < lines.size() - 1) {
                g2.drawString(trimToWidth(line, maxWidth), x, y);
                return nextY;
            }
            if (!line.isEmpty()) {
                g2.drawString(line, x, y);
            }
            y = nextY;
        }
        return y;
    }

    private ArrayList<String> wrapTextLines(String text, int maxWidth, Font font) {
        FontMetrics fm = g2.getFontMetrics(font);
        ArrayList<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }

        String[] paragraphs = text.split("\n", -1);

        for (String paragraph : paragraphs) {
            if (paragraph.trim().isEmpty()) {
                lines.add("");
                continue;
            }

            String[] words = paragraph.trim().split("\\s+");
            String line = "";
            for (String word : words) {
                String testLine = line.isEmpty() ? word : line + " " + word;
                if (fm.stringWidth(testLine) <= maxWidth) {
                    line = testLine;
                }
                else if (!line.isEmpty()) {
                    lines.add(line);
                    line = fitWordToWidth(lines, word, maxWidth, fm);
                }
                else {
                    line = fitWordToWidth(lines, word, maxWidth, fm);
                }
            }

            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    private String fitWordToWidth(ArrayList<String> lines, String word, int maxWidth, FontMetrics fm) {
        if (fm.stringWidth(word) <= maxWidth) {
            return word;
        }

        String line = "";
        for (int i = 0; i < word.length(); i++) {
            String next = line + word.charAt(i);
            if (fm.stringWidth(next) > maxWidth && !line.isEmpty()) {
                lines.add(line);
                line = String.valueOf(word.charAt(i));
            }
            else {
                line = next;
            }
        }
        return line;
    }

    private void drawShadowedString(String text, int x, int y, Color textColor, Color shadowColor) {
        g2.setColor(shadowColor);
        g2.drawString(text, x + 3, y + 3);
        g2.setColor(textColor);
        g2.drawString(text, x, y);
    }

    private String trimToWidth(String text, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }

        String ellipsis = "...";
        while (!text.isEmpty() && fm.stringWidth(text + ellipsis) > maxWidth) {
            text = text.substring(0, text.length() - 1);
        }
        return text + ellipsis;
    }

    private int getXforCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }

}
