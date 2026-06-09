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
    private int hoveredGameState = Integer.MIN_VALUE;
    private int hoveredCommand = NO_COMMAND;
    private int hoveredOptionsTab = NO_COMMAND;

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
        if (gp.gameState == gp.optionsState) {
            int tab = getOptionsTabAt(point);
            if (tab != NO_COMMAND) {
                gp.ui.setOptionsTab(tab);
                gp.ui.commandNum = 0;
                gp.playCursorSE();
                gp.syncMouseCursor();
                return;
            }
        }

        int command = getCommandAt(point);
        if (command == NO_COMMAND) {
            return;
        }

        gp.ui.commandNum = command;
        activateCommand(point, command);
        gp.syncMouseCursor();
    }

    private void updateHoveredCommand(MouseEvent e) {
        if (hoveredGameState != gp.gameState) {
            resetHoverState();
            hoveredGameState = gp.gameState;
        }

        Point point = gp.toGameScreenPoint(e.getX(), e.getY());
        if (gp.gameState == gp.optionsState) {
            int tab = getOptionsTabAt(point);
            if (tab != NO_COMMAND) {
                if (hoveredOptionsTab != tab) {
                    hoveredOptionsTab = tab;
                    hoveredCommand = NO_COMMAND;
                    gp.playCursorSE();
                }
                return;
            }
        }

        int command = getCommandAt(point);
        if (command == NO_COMMAND) {
            resetHoverState();
            return;
        }

        if (hoveredCommand != command) {
            hoveredCommand = command;
            hoveredOptionsTab = NO_COMMAND;
            gp.ui.commandNum = command;
            gp.playCursorSE();
        }
        else if (gp.ui.commandNum != command) {
            gp.ui.commandNum = command;
        }
    }

    private void resetHoverState() {
        hoveredCommand = NO_COMMAND;
        hoveredOptionsTab = NO_COMMAND;
    }

    private int getCommandAt(Point point) {
        if (gp.gameState == gp.titleState) {
            return getCommandFromVerticalMenu(point, UI.TITLE_MENU_FIRST_Y, UI.TITLE_MENU_COMMANDS,
                    UI.TITLE_MENU_ROW_HEIGHT, UI.TITLE_MENU_X - 14, UI.TITLE_MENU_WIDTH);
        }
        if (gp.gameState == gp.pauseState) {
            return getCommandFromRows(point, getPauseButtonBounds(), 6);
        }
        if (gp.gameState == gp.optionsState) {
            return getOptionsCommand(point);
        }
        if (gp.gameState == gp.resultState) {
            return getResultCommand(point);
        }
        return NO_COMMAND;
    }

    private int getCommandFromVerticalMenu(Point point, int firstBaselineY, int commandCount, int stepY,
                                           int x, int width) {
        for (int command = 0; command < commandCount; command++) {
            int y = firstBaselineY + command * stepY;
            if (new Rectangle(x, y - 32, width, UI.TITLE_MENU_ITEM_HEIGHT).contains(point)) {
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
        return gp.ui.getOptionsCommandBounds(0);
    }

    private int getOptionsCommand(Point point) {
        for (int command = 0; command < gp.ui.getOptionsCommandCount(); command++) {
            if (gp.ui.getOptionsCommandBounds(command).contains(point)) {
                return command;
            }
        }
        return NO_COMMAND;
    }

    private int getOptionsTabAt(Point point) {
        for (int tab = 0; tab < UI.OPTIONS_TAB_COUNT; tab++) {
            if (gp.ui.getOptionsTabBounds(tab).contains(point)) {
                return tab;
            }
        }
        return NO_COMMAND;
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
        if (gp.ui.isOptionsBackCommand()) {
            return KeyEvent.VK_ENTER;
        }
        if (gp.ui.getOptionsTab() == UI.OPTIONS_TAB_GRAPHICS && command == 0) {
            return KeyEvent.VK_ENTER;
        }

        Rectangle row = gp.ui.getOptionsCommandBounds(command);
        int leftArrowCenterX = row.x + row.width - 300;
        int rightArrowCenterX = row.x + row.width - 34;

        if (Math.abs(point.x - leftArrowCenterX) <= 34) {
            return KeyEvent.VK_LEFT;
        }
        if (Math.abs(point.x - rightArrowCenterX) <= 34) {
            return KeyEvent.VK_RIGHT;
        }
        return KeyEvent.VK_ENTER;
    }
}
