package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    private static final int TITLE_LAST_COMMAND = 3;
    private static final int PAUSE_LAST_COMMAND = 5;
    private static final int OPTIONS_LAST_COMMAND = 4;

    private final GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;

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
    }

    public void titleState(int code) {
        if (isUp(code)) {
            moveCommand(-1, TITLE_LAST_COMMAND);
        }
        if (isDown(code)) {
            moveCommand(1, TITLE_LAST_COMMAND);
        }
        if (isConfirm(code)) {
            if (gp.ui.commandNum == 0) {
                gp.playConfirmSE();
                gp.story.startNewGame();
                gp.saveLoad.save();
            }
            else if (gp.ui.commandNum == 1) {
                gp.playConfirmSE();
                if (!gp.saveLoad.load()) {
                    gp.story.startNewGame();
                }
                gp.gameState = gp.playState;
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

    public void playState(int code) {
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
                gp.ui.setPauseNotice("Игра сохранена");
            }
            else if (gp.ui.commandNum == 2) {
                gp.playConfirmSE();
                if (gp.saveLoad.load()) {
                    gp.gameState = gp.playState;
                }
                else {
                    gp.ui.setPauseNotice("Сохранение не найдено");
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
            moveCommand(-1, OPTIONS_LAST_COMMAND);
        }
        if (isDown(code)) {
            moveCommand(1, OPTIONS_LAST_COMMAND);
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
        if (isConfirm(code)) {
            if (gp.ui.commandNum == 4) {
                gp.closeOptionsMenu();
            }
            else {
                changeOption(1);
            }
        }
    }

    public void dialogueState(int code) {
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
                gp.story.startNewGame();
                gp.saveLoad.save();
            }
            else {
                gp.gameState = gp.titleState;
                gp.ui.commandNum = 0;
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
        if (gp.ui.commandNum == 0) {
            gp.changeMusicVolume(amount);
        }
        else if (gp.ui.commandNum == 1) {
            gp.changeSoundEffectVolume(amount);
        }
        else if (gp.ui.commandNum == 2) {
            gp.toggleFullScreen();
        }
        else if (gp.ui.commandNum == 3) {
            gp.toggleHud();
        }
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
    }
}
