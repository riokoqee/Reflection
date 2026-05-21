package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
    boolean showDebugTest = false;

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
        else if (gp.gameState == gp.dialogueState) {
            dialogueState(code);
        }
        else if (gp.gameState == gp.resultState) {
            resultState(code);
        }
    }

    public void titleState(int code) {
        if (isUp(code)) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = 2;
            }
            gp.playCursorSE();
        }
        if (isDown(code)) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > 2) {
                gp.ui.commandNum = 0;
            }
            gp.playCursorSE();
        }
        if (code == KeyEvent.VK_ENTER) {
            if (gp.ui.commandNum == 0) {
                gp.story.startNewGame();
                gp.saveLoad.save();
            }
            else if (gp.ui.commandNum == 1) {
                if (!gp.saveLoad.load()) {
                    gp.story.startNewGame();
                }
                gp.gameState = gp.playState;
            }
            else if (gp.ui.commandNum == 2) {
                System.exit(0);
            }
        }
    }

    public void playState(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_E || code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
        if (code == KeyEvent.VK_P) {
            gp.openPauseMenu();
        }
        if (code == KeyEvent.VK_ESCAPE) {
            gp.openPauseMenu();
        }
        if (code == KeyEvent.VK_F3) {
            showDebugTest = !showDebugTest;
        }
    }

    public void pauseState(int code) {
        if (isUp(code)) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = 4;
            }
            gp.playCursorSE();
        }
        if (isDown(code)) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > 4) {
                gp.ui.commandNum = 0;
            }
            gp.playCursorSE();
        }
        if (code == KeyEvent.VK_P || code == KeyEvent.VK_ESCAPE) {
            gp.closePauseMenu();
        }
        if (code == KeyEvent.VK_ENTER) {
            if (gp.ui.commandNum == 0) {
                gp.closePauseMenu();
            }
            else if (gp.ui.commandNum == 1) {
                gp.saveLoad.save();
                gp.ui.setPauseNotice("Игра сохранена");
            }
            else if (gp.ui.commandNum == 2) {
                if (gp.saveLoad.load()) {
                    gp.clearPauseBackground();
                    gp.gameState = gp.playState;
                }
                else {
                    gp.ui.setPauseNotice("Сохранение не найдено");
                }
            }
            else if (gp.ui.commandNum == 3) {
                gp.story.startNewGame();
                gp.saveLoad.save();
                gp.clearPauseBackground();
                gp.gameState = gp.playState;
            }
            else if (gp.ui.commandNum == 4) {
                gp.saveLoad.save();
                gp.clearPauseBackground();
                gp.gameState = gp.titleState;
                gp.ui.commandNum = 0;
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
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_E) {
                gp.story.chooseSelected();
            }
        }
        else if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_E || code == KeyEvent.VK_SPACE) {
            gp.story.continueDialogue();
        }
    }

    public void resultState(int code) {
        if (isUp(code) || isDown(code)) {
            gp.ui.commandNum = gp.ui.commandNum == 0 ? 1 : 0;
            gp.playCursorSE();
        }
        if (code == KeyEvent.VK_ENTER) {
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

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }
}
