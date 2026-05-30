package main;

import javax.swing.SwingUtilities;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    private static final int NO_COMMAND = -1;

    private final GamePanel gp;

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateHoveredCommand(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateHoveredCommand(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        gp.requestFocusInWindow();
        Point point = gp.toGameScreenPoint(e.getX(), e.getY());
        int command = getCommandAt(point);
        if (command == NO_COMMAND) {
            return;
        }

        gp.ui.commandNum = command;
        activateCommand(point, command);
        gp.syncMouseCursor();
    }

    private void updateHoveredCommand(MouseEvent e) {
        Point point = gp.toGameScreenPoint(e.getX(), e.getY());
        int command = getCommandAt(point);
        if (command != NO_COMMAND && gp.ui.commandNum != command) {
            gp.ui.commandNum = command;
            gp.playCursorSE();
        }
    }

    private int getCommandAt(Point point) {
        if (gp.gameState == gp.titleState) {
            return getCommandFromVerticalMenu(point, gp.tileSize * 6 + 24, 4, 44, 520);
        }
        if (gp.gameState == gp.pauseState) {
            return getCommandFromRows(point, getPauseButtonBounds(), 6);
        }
        if (gp.gameState == gp.optionsState) {
            return getCommandFromRows(point, getOptionsButtonBounds(), 4);
        }
        if (gp.gameState == gp.resultState) {
            return getResultCommand(point);
        }
        return NO_COMMAND;
    }

    private int getCommandFromVerticalMenu(Point point, int firstBaselineY, int commandCount, int stepY, int width) {
        int x = gp.screenWidth / 2 - width / 2;
        for (int command = 0; command < commandCount; command++) {
            int y = firstBaselineY + command * stepY;
            if (new Rectangle(x, y - 34, width, 42).contains(point)) {
                return command;
            }
        }
        return NO_COMMAND;
    }

    private int getCommandFromRows(Point point, Rectangle firstButtonBounds, int commandCount) {
        for (int command = 0; command < commandCount; command++) {
            Rectangle bounds = new Rectangle(
                    firstButtonBounds.x,
                    firstButtonBounds.y + command * firstButtonBounds.height,
                    firstButtonBounds.width,
                    firstButtonBounds.height - 8
            );
            if (bounds.contains(point)) {
                return command;
            }
        }
        return NO_COMMAND;
    }

    private Rectangle getPauseButtonBounds() {
        int panelWidth = 430;
        int panelHeight = 470;
        int panelX = gp.screenWidth / 2 - panelWidth / 2;
        int panelY = gp.screenHeight / 2 - panelHeight / 2;
        int rowX = panelX + 54;
        int rowY = panelY + 158;
        int rowWidth = panelWidth - 108;
        return new Rectangle(rowX - 16, rowY - 29, rowWidth, 46);
    }

    private Rectangle getOptionsButtonBounds() {
        int panelWidth = 560;
        int panelHeight = 360;
        int panelX = gp.screenWidth / 2 - panelWidth / 2;
        int panelY = gp.screenHeight / 2 - panelHeight / 2;
        int rowX = panelX + 52;
        int rowY = panelY + 124;
        int rowWidth = panelWidth - 104;
        return new Rectangle(rowX - 16, rowY - 31, rowWidth, 55);
    }

    private int getResultCommand(Point point) {
        int width = 640;
        int x = gp.screenWidth / 2 - width / 2;
        int firstY = gp.screenHeight - 96;
        if (new Rectangle(x, firstY - 34, width, 40).contains(point)) {
            return 0;
        }
        int secondY = gp.screenHeight - 54;
        if (new Rectangle(x, secondY - 34, width, 40).contains(point)) {
            return 1;
        }
        return NO_COMMAND;
    }

    private void activateCommand(Point point, int command) {
        if (gp.gameState == gp.titleState) {
            gp.keyH.titleState(KeyEvent.VK_ENTER);
        }
        else if (gp.gameState == gp.pauseState) {
            gp.keyH.pauseState(KeyEvent.VK_ENTER);
        }
        else if (gp.gameState == gp.optionsState) {
            gp.keyH.optionsState(getOptionsActivationKey(point, command));
        }
        else if (gp.gameState == gp.resultState) {
            gp.keyH.resultState(KeyEvent.VK_ENTER);
        }
    }

    private int getOptionsActivationKey(Point point, int command) {
        if (command != 0 && command != 1) {
            return KeyEvent.VK_ENTER;
        }

        Rectangle firstRow = getOptionsButtonBounds();
        int rowX = firstRow.x + 16;
        int rowWidth = firstRow.width;
        int barX = rowX + rowWidth - 240;
        int blockWidth = 28;
        int blockGap = 8;
        int leftArrowCenterX = barX - 28;
        int rightArrowCenterX = barX + 5 * (blockWidth + blockGap) + 14;

        if (Math.abs(point.x - leftArrowCenterX) <= 34) {
            return KeyEvent.VK_LEFT;
        }
        if (Math.abs(point.x - rightArrowCenterX) <= 34) {
            return KeyEvent.VK_RIGHT;
        }
        return KeyEvent.VK_ENTER;
    }
}
