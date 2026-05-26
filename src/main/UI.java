package main;

import java.awt.*;
import java.util.ArrayList;

public class UI {

    private final GamePanel gp;
    private Graphics2D g2;
    private final Font titleFont;
    public int commandNum = 0;
    private final ArrayList<String> message = new ArrayList<>();
    private final ArrayList<Integer> messageCounter = new ArrayList<>();
    private String pauseNotice = "";
    private int pauseNoticeCounter = 0;
    private String checkpointLocation = "";
    private int checkpointCounter = 0;
    private int checkpointSpinnerFrame = 0;
    private int dialogueSpinnerFrame = 0;

    public UI(GamePanel gp) {
        this.gp = gp;
        titleFont = new Font("SansSerif", Font.BOLD, 72);
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

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setColor(Color.white);

        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
        else if (gp.gameState == gp.playState) {
            if (gp.hudVisible) {
                drawHud();
            }
            drawMessage();
            drawCheckpointNotice();
        }
        else if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }
        else if (gp.gameState == gp.optionsState) {
            drawOptionsScreen();
        }
        else if (gp.gameState == gp.dialogueState) {
            if (gp.hudVisible) {
                drawHud();
            }
            drawDialogueScreen();
        }
        else if (gp.gameState == gp.resultState) {
            drawResultScreen();
        }
    }

    private void drawTitleScreen() {
        GradientPaint paint = new GradientPaint(0, 0, new Color(21, 28, 34), 0, gp.screenHeight, new Color(57, 73, 61));
        g2.setPaint(paint);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(titleFont);
        String text = "Reflection";
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 3;
        drawShadowedString(text, x, y, Color.white, new Color(0, 0, 0, 140));

        g2.drawImage(gp.player.down1, gp.screenWidth / 2 - gp.tileSize, gp.tileSize * 5, gp.tileSize * 2, gp.tileSize * 2, null);

        g2.setFont(new Font("SansSerif", Font.BOLD, 30));
        y = gp.tileSize * 8 + 12;
        drawMenuItem("НОВАЯ ИГРА", 0, y);
        y += 44;
        drawMenuItem("ПРОДОЛЖИТЬ", 1, y);
        y += 44;
        drawMenuItem("НАСТРОЙКИ", 2, y);
        y += 44;
        drawMenuItem("ВЫЙТИ", 3, y);
    }

    private void drawHud() {
        int x = 24;
        int y = 22;
        int width = 270;
        int height = 110;
        drawSubWindow(x, y, width, height, new Color(8, 12, 16, 165));

        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.setColor(new Color(231, 240, 235));
        g2.drawString(gp.story.getLocationTitle(), x + 18, y + 30);

        int barY = y + 62;
        drawMiniMetric("Рост", gp.story.growth, x + 18, barY);
        drawMiniMetric("Покой", gp.story.calm, x + 145, barY);
        drawMiniMetric("Эмпатия", gp.story.empathy, x + 18, barY + 32);
        drawMiniMetric("Увер.", gp.story.confidence, x + 145, barY + 32);
    }

    private void drawMessage() {
        int messageX = gp.tileSize;
        int messageY = gp.tileSize * 4;
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));

        for (int i = 0; i < message.size(); i++) {
            if (message.get(i) != null) {
                drawShadowedString(message.get(i), messageX, messageY, Color.white, Color.black);

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
        g2.setFont(new Font("SansSerif", Font.BOLD, 17));
        g2.setColor(new Color(236, 248, 242, Math.min(255, alpha + 30)));
        g2.drawString("Контрольная точка", textX, y + 31);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g2.setColor(new Color(174, 215, 196, Math.min(240, alpha + 20)));
        g2.drawString("Сохранение...", textX, y + 55);

        if (!checkpointLocation.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(new Color(206, 219, 212, Math.min(220, alpha + 10)));
            g2.drawString(trimToWidth(checkpointLocation, width - 88), textX, y + 76);
        }

        checkpointSpinnerFrame++;
        checkpointCounter--;
        if (checkpointCounter <= 0) {
            checkpointLocation = "";
        }
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

        g2.setFont(new Font("SansSerif", Font.BOLD, 38));
        String text = "ПАУЗА";
        drawShadowedString(text, getXforCenteredText(text), panelY + 66, Color.white, new Color(0, 0, 0, 160));

        g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g2.setColor(new Color(210, 225, 218));
        String place = gp.story.getLocationTitle();
        g2.drawString(place, panelX + 34, panelY + 94);

        int menuY = panelY + 158;
        drawPauseMenuItem("ПРОДОЛЖИТЬ", 0, panelX + 54, menuY, panelWidth - 108);
        drawPauseMenuItem("СОХРАНИТЬ", 1, panelX + 54, menuY + 46, panelWidth - 108);
        drawPauseMenuItem("ЗАГРУЗИТЬ", 2, panelX + 54, menuY + 92, panelWidth - 108);
        drawPauseMenuItem("НАСТРОЙКИ", 3, panelX + 54, menuY + 138, panelWidth - 108);
        drawPauseMenuItem("НОВАЯ ИГРА", 4, panelX + 54, menuY + 184, panelWidth - 108);
        drawPauseMenuItem("В ГЛАВНОЕ МЕНЮ", 5, panelX + 54, menuY + 230, panelWidth - 108);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(new Color(195, 208, 202));
        g2.drawString("Esc - вернуться    Enter - выбрать", panelX + 54, panelY + panelHeight - 28);

        if (!pauseNotice.isEmpty() && pauseNoticeCounter > 0) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            int noticeWidth = g2.getFontMetrics().stringWidth(pauseNotice) + 36;
            int noticeX = gp.screenWidth / 2 - noticeWidth / 2;
            int noticeY = panelY - 46;
            g2.setColor(new Color(6, 10, 12, 220));
            g2.fillRoundRect(noticeX, noticeY, noticeWidth, 34, 16, 16);
            g2.setColor(new Color(174, 215, 196));
            g2.drawString(pauseNotice, noticeX + 18, noticeY + 23);
            pauseNoticeCounter--;
        }
    }

    private void drawOptionsScreen() {
        if (gp.optionsReturnState != gp.pauseState) {
            GradientPaint paint = new GradientPaint(0, 0, new Color(19, 25, 31), 0, gp.screenHeight, new Color(48, 62, 55));
            g2.setPaint(paint);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        g2.setColor(new Color(0, 0, 0, 135));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int panelWidth = 560;
        int panelHeight = 420;
        int panelX = gp.screenWidth / 2 - panelWidth / 2;
        int panelY = gp.screenHeight / 2 - panelHeight / 2;

        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(panelX + 10, panelY + 12, panelWidth, panelHeight, 28, 28);
        drawSubWindow(panelX, panelY, panelWidth, panelHeight, new Color(8, 14, 17, 232));

        g2.setFont(new Font("SansSerif", Font.BOLD, 38));
        String title = "НАСТРОЙКИ";
        drawShadowedString(title, getXforCenteredText(title), panelY + 62, Color.white, new Color(0, 0, 0, 160));

        int rowX = panelX + 52;
        int rowY = panelY + 124;
        int rowWidth = panelWidth - 104;
        int rowStep = 55;

        drawVolumeOption("Музыка", gp.music.volumeScale, 0, rowX, rowY, rowWidth);
        drawVolumeOption("Звуки", gp.se.volumeScale, 1, rowX, rowY + rowStep, rowWidth);
        drawToggleOption("Полный экран", gp.fullScreenOn, 2, rowX, rowY + rowStep * 2, rowWidth);
        drawToggleOption("Интерфейс", gp.hudVisible, 3, rowX, rowY + rowStep * 3, rowWidth);
        drawBackOption("НАЗАД", 4, rowX, rowY + rowStep * 4, rowWidth);
    }

    private void drawDialogueScreen() {
        StoryManager.StoryPrompt prompt = gp.story.getActivePrompt();
        boolean hasChoices = prompt != null;
        boolean lockedInteraction = gp.story.isDialogueLocked();

        int x = gp.tileSize;
        int width = gp.screenWidth - gp.tileSize * 2;
        int textWidth = width - 56;
        int maxHeight = gp.screenHeight - 48;
        int minHeight = hasChoices ? 270 : 190;

        String speaker = hasChoices ? prompt.speaker : gp.story.getMessageSpeaker();
        String text = hasChoices ? prompt.text : gp.story.getMessageText();
        int bodySize = hasChoices ? 24 : 26;
        int choiceSize = 21;
        Font bodyFont;
        Font choiceFont;
        ArrayList<String> textLines;
        int bodyLineHeight;
        int choiceLineHeight;
        int choiceGap = 8;
        int contentHeight;

        while (true) {
            bodyFont = new Font("SansSerif", Font.PLAIN, bodySize);
            choiceFont = new Font("SansSerif", Font.BOLD, choiceSize);
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

        drawSubWindow(x, y, width, height, new Color(5, 8, 12, 220));

        Shape oldClip = g2.getClip();
        g2.clipRect(x + 12, y + 12, width - 24, height - 24);

        int textX = x + 28;
        int textY = y + 42;
        int bottomY = y + height - (lockedInteraction ? 70 : 28);

        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2.setColor(new Color(174, 215, 196));
        g2.drawString(trimToWidth(speaker, textWidth), textX, textY);

        g2.setFont(bodyFont);
        g2.setColor(Color.white);
        textY += 36;
        int nextY = drawTextLines(textLines, textX, textY, textWidth, bodyLineHeight, bottomY);

        if (hasChoices) {
            drawChoices(prompt, textX, Math.max(nextY + 12, y + 134), textWidth,
                    choiceFont, choiceLineHeight, choiceGap, bottomY);
        }
        if (lockedInteraction) {
            drawDialogueProgress(textX, y + height - 42, textWidth);
        }
        g2.setClip(oldClip);
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

    private void drawResultScreen() {
        g2.setColor(new Color(10, 12, 15, 235));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(new Font("SansSerif", Font.BOLD, 46));
        String text = "РЕЗУЛЬТАТ";
        drawShadowedString(text, getXforCenteredText(text), 74, Color.white, Color.black);

        int x = 70;
        int y = 116;
        int width = 360;
        drawMetricBar("Рост", gp.story.growth, x, y, width); y += 48;
        drawMetricBar("Покой", gp.story.calm, x, y, width); y += 48;
        drawMetricBar("Эмпатия", gp.story.empathy, x, y, width); y += 48;
        drawMetricBar("Уверенность", gp.story.confidence, x, y, width);

        int frameX = 500;
        int frameY = 116;
        int frameWidth = 390;
        int frameHeight = 290;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight, new Color(18, 24, 28, 210));

        g2.setFont(new Font("SansSerif", Font.BOLD, 25));
        g2.setColor(new Color(174, 215, 196));
        g2.drawString(gp.story.getProfileTitle(), frameX + 26, frameY + 42);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g2.setColor(Color.white);
        int textY = drawWrappedText(gp.story.getProfileText(), frameX + 26, frameY + 78, frameWidth - 52, 27);

        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2.setColor(new Color(174, 215, 196));
        g2.drawString("Рекомендация", frameX + 26, textY + 18);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 19));
        g2.setColor(Color.white);
        drawWrappedText(gp.story.getRecommendation(), frameX + 26, textY + 48, frameWidth - 52, 26);

        g2.setFont(new Font("SansSerif", Font.BOLD, 27));
        drawResultMenuItem("ПРОЙТИ ЕЩЁ РАЗ", 0, gp.screenHeight - 96);
        drawResultMenuItem("В ГЛАВНОЕ МЕНЮ", 1, gp.screenHeight - 54);
    }

    private void drawChoices(StoryManager.StoryPrompt prompt, int x, int y, int width,
                             Font font, int lineHeight, int gap, int bottomY) {
        g2.setFont(font);
        int textX = x + 28;
        int textWidth = width - 44;
        for (int i = 0; i < prompt.choices.length; i++) {
            StoryManager.Choice choice = prompt.choices[i];
            ArrayList<String> lines = wrapTextLines(choice.text, textWidth, font);
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

        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        Color color = selected ? new Color(235, 250, 242) : new Color(205, 216, 211);
        drawShadowedString(text, x + 18, y, color, new Color(0, 0, 0, 140));

        if (selected) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2.setColor(new Color(174, 215, 196));
            g2.drawString(">", x - 2, y);
        }
    }

    private void drawVolumeOption(String label, int value, int command, int x, int y, int width) {
        drawOptionShell(command, x, y, width);

        g2.setFont(new Font("SansSerif", Font.BOLD, 21));
        g2.setColor(commandNum == command ? new Color(235, 250, 242) : new Color(205, 216, 211));
        g2.drawString(label, x + 18, y);

        int barX = x + width - 240;
        int barY = y - 20;
        int blockWidth = 28;
        int blockHeight = 18;

        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
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

        g2.setFont(new Font("SansSerif", Font.BOLD, 21));
        g2.setColor(commandNum == command ? new Color(235, 250, 242) : new Color(205, 216, 211));
        g2.drawString(label, x + 18, y);

        String value = enabled ? "ВКЛ" : "ВЫКЛ";
        int toggleWidth = 118;
        int toggleX = x + width - toggleWidth - 26;
        int toggleY = y - 28;
        g2.setColor(enabled ? new Color(174, 215, 196, 95) : new Color(255, 255, 255, 45));
        g2.fillRoundRect(toggleX, toggleY, toggleWidth, 34, 17, 17);
        g2.setColor(enabled ? new Color(174, 215, 196) : new Color(166, 178, 172));
        int knobX = enabled ? toggleX + toggleWidth - 31 : toggleX + 7;
        g2.fillOval(knobX, toggleY + 6, 22, 22);

        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.setColor(Color.white);
        g2.drawString(value, toggleX + 42 - g2.getFontMetrics().stringWidth(value) / 2, y - 6);
    }

    private void drawBackOption(String label, int command, int x, int y, int width) {
        drawOptionShell(command, x, y, width);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
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
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2.drawString(">", x - 2, y);
        }
    }

    private void drawMiniMetric(String label, int value, int x, int y) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
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
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
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
            ArrayList<String> lines = wrapTextLines(prompt.choices[i].text, textWidth, font);
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
