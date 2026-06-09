package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    private static final int PAUSE_LAST_COMMAND = 5;

    private final GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, shiftPressed, enterPressed;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (gp.gameState == gp.titleState) {
            titleState(code);
        }
        else if (gp.gameState == gp.playState) {
            playState(code);
        }
        else if (gp.gameState == gp.pauseState) {
            pauseState(code);
        }
        else if (gp.gameState == gp.optionsState) {
            optionsState(code);
        }
        else if (gp.gameState == gp.dialogueState) {
            dialogueState(code);
        }
        else if (gp.gameState == gp.resultState) {
            resultState(code);
        }
        else if (gp.gameState == gp.introState) {
            introState(code);
        }
    }

    public void titleState(int code) {
        if (isUp(code)) {
            moveCommand(-1, gp.ui.getTitleCommandCount() - 1);
        }
        if (isDown(code)) {
            moveCommand(1, gp.ui.getTitleCommandCount() - 1);
        }
        if (code == KeyEvent.VK_ESCAPE && gp.ui.isTitleSlotMenu()) {
            gp.playBackSE();
            gp.ui.returnToTitleMain();
            return;
        }
        if (isConfirm(code)) {
            if (gp.ui.isTitleSlotMenu()) {
                confirmTitleSlotMenu();
                return;
            }

            boolean continueFirst = gp.saveLoad.hasAnySave();
            if (gp.ui.commandNum == 0) {
                gp.playConfirmSE();
                if (continueFirst) {
                    gp.ui.enterTitleLoadSlots();
                }
                else {
                    gp.ui.enterTitleNewSlots();
                }
            }
            else if (gp.ui.commandNum == 1) {
                gp.playConfirmSE();
                if (continueFirst) {
                    gp.ui.enterTitleNewSlots();
                }
                else {
                    gp.ui.enterTitleLoadSlots();
                }
            }
            else if (gp.ui.commandNum == 2) {
                gp.openOptionsMenu(gp.titleState);
            }
            else if (gp.ui.commandNum == 3) {
                gp.playConfirmSE();
                System.exit(0);
            }
        }
    }

    private void confirmTitleSlotMenu() {
        if (gp.ui.commandNum == UI.TITLE_SLOT_BACK_COMMAND) {
            gp.playBackSE();
            gp.ui.returnToTitleMain();
            return;
        }

        int slot = gp.ui.commandNum + 1;
        if (gp.ui.isTitleNewSlotMenu()) {
            gp.playConfirmSE();
            gp.startNewGameInSlot(slot);
            return;
        }

        if (gp.ui.isTitleLoadSlotMenu()) {
            if (gp.saveLoad.hasSave(slot) && gp.loadGameFromSlot(slot)) {
                gp.playConfirmSE();
                gp.ui.returnToTitleMain();
            }
            else {
                gp.playBackSE();
                gp.ui.setTitleNotice(gp.tr("Этот слот пуст", "This slot is empty"));
            }
        }
    }

    public void playState(int code) {
        if (code == KeyEvent.VK_I) {
            gp.ui.togglePlanNote();
            gp.playCursorSE();
            return;
        }
        if (isUp(code)) {
            upPressed = true;
        }
        if (isDown(code)) {
            downPressed = true;
        }
        if (isLeft(code)) {
            leftPressed = true;
        }
        if (isRight(code)) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = true;
        }
        if (isAction(code)) {
            enterPressed = true;
        }
        if (code == KeyEvent.VK_P || code == KeyEvent.VK_ESCAPE) {
            gp.openPauseMenu();
        }
    }

    public void pauseState(int code) {
        if (isUp(code)) {
            moveCommand(-1, PAUSE_LAST_COMMAND);
        }
        if (isDown(code)) {
            moveCommand(1, PAUSE_LAST_COMMAND);
        }
        if (code == KeyEvent.VK_P || code == KeyEvent.VK_ESCAPE) {
            gp.closePauseMenu();
        }
        if (isConfirm(code)) {
            if (gp.ui.commandNum == 0) {
                gp.closePauseMenu();
            }
            else if (gp.ui.commandNum == 1) {
                gp.playConfirmSE();
                gp.saveLoad.save();
                gp.ui.setPauseNotice(gp.tr("Игра сохранена", "Game saved"));
            }
            else if (gp.ui.commandNum == 2) {
                gp.playConfirmSE();
                if (gp.saveLoad.load()) {
                    gp.gameState = gp.playState;
                }
                else {
                    gp.ui.setPauseNotice(gp.tr("Сохранение не найдено", "Save not found"));
                }
            }
            else if (gp.ui.commandNum == 3) {
                gp.openOptionsMenu(gp.pauseState);
            }
            else if (gp.ui.commandNum == 4) {
                gp.playConfirmSE();
                gp.story.startNewGame();
                gp.saveLoad.save();
                gp.gameState = gp.playState;
            }
            else if (gp.ui.commandNum == 5) {
                gp.playBackSE();
                gp.saveLoad.save();
                gp.gameState = gp.titleState;
                gp.ui.commandNum = 0;
            }
        }
    }

    public void optionsState(int code) {
        if (isUp(code)) {
            moveCommand(-1, gp.ui.getOptionsCommandCount() - 1);
        }
        if (isDown(code)) {
            moveCommand(1, gp.ui.getOptionsCommandCount() - 1);
        }
        if (code == KeyEvent.VK_Q) {
            gp.ui.moveOptionsTab(-1);
            gp.playCursorSE();
        }
        if (code == KeyEvent.VK_TAB) {
            gp.ui.moveOptionsTab(1);
            gp.playCursorSE();
        }
        if (code == KeyEvent.VK_ESCAPE) {
            gp.closeOptionsMenu();
        }
        if (isLeft(code)) {
            changeOption(-1);
        }
        if (isRight(code)) {
            changeOption(1);
        }
        if (isConfirm(code) || code == KeyEvent.VK_E) {
            boolean closing = gp.ui.isOptionsBackCommand();
            gp.activateCurrentOption();
            if (!closing) {
                gp.playCursorSE();
            }
        }
    }

    public void dialogueState(int code) {
        if ((isAction(code) || code == KeyEvent.VK_SPACE) && !gp.ui.isDialogueTextFullyVisible()) {
            gp.ui.revealDialogueTextNow();
            gp.playCursorSE();
            return;
        }

        if (gp.story.hasChoices()) {
            if (isUp(code)) {
                gp.story.moveChoice(-1);
            }
            if (isDown(code)) {
                gp.story.moveChoice(1);
            }
            if (isAction(code)) {
                gp.playConfirmSE();
                gp.story.chooseSelected();
            }
        }
        else if (isAction(code) || code == KeyEvent.VK_SPACE) {
            if (gp.story.canContinueDialogue()) {
                gp.playConfirmSE();
                gp.story.continueDialogue();
            }
        }
    }

    public void resultState(int code) {
        if (isUp(code) || isDown(code)) {
            gp.ui.commandNum = gp.ui.commandNum == 0 ? 1 : 0;
            gp.playCursorSE();
        }
        if (isConfirm(code)) {
            gp.playConfirmSE();
            if (gp.ui.commandNum == 0) {
                gp.startNewGameInSlot(gp.saveLoad.getCurrentSlot());
            }
            else {
                gp.gameState = gp.titleState;
                gp.ui.commandNum = 0;
            }
        }
    }

    public void introState(int code) {
        if (isAction(code) || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ESCAPE) {
            if (gp.getIntroFrame() >= gp.getIntroTotalFrames()) {
                gp.playConfirmSE();
                gp.finishIntroSequence();
            }
        }
    }

    private boolean isUp(int code) {
        return code == KeyEvent.VK_W || code == KeyEvent.VK_UP;
    }

    private boolean isDown(int code) {
        return code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN;
    }

    private boolean isLeft(int code) {
        return code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT;
    }

    private boolean isRight(int code) {
        return code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT;
    }

    private boolean isConfirm(int code) {
        return code == KeyEvent.VK_ENTER;
    }

    private boolean isAction(int code) {
        return code == KeyEvent.VK_E || isConfirm(code);
    }

    private void moveCommand(int amount, int maxCommand) {
        gp.ui.commandNum += amount;
        if (gp.ui.commandNum < 0) {
            gp.ui.commandNum = maxCommand;
        }
        if (gp.ui.commandNum > maxCommand) {
            gp.ui.commandNum = 0;
        }
        gp.playCursorSE();
    }

    private void changeOption(int amount) {
        gp.changeCurrentOption(amount);
        gp.playCursorSE();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (isUp(code)) {
            upPressed = false;
        }
        if (isDown(code)) {
            downPressed = false;
        }
        if (isLeft(code)) {
            leftPressed = false;
        }
        if (isRight(code)) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = false;
        }
    }
}
