package entity;

import main.GamePanel;
import main.KeyHandler;
import main.Sound;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player extends Entity {

    private static final String HERO_SPRITE = "/player/new/Amelia";
    private static final int SOURCE_FRAME_WIDTH = 16;
    private static final int SOURCE_FRAME_HEIGHT = 32;
    private static final int DIRECTION_RIGHT = 0;
    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_LEFT = 2;
    private static final int DIRECTION_DOWN = 3;
    private static final int DIRECTION_COUNT = 4;
    private static final int FRAMES_PER_DIRECTION = 6;
    private static final double DRAW_WIDTH_TILES = 0.82;
    private static final double DRAW_HEIGHT_TILES = 1.64;
    private static final int NO_TARGET = 999;
    private static final int SOFA_SIT_X_COL = 33;
    private static final int SOFA_SIT_Y_ROW = 12;
    private static final int WALK_SPEED = 4;
    private static final int SPRINT_SPEED = 7;

    private final KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    private BufferedImage[][] idleFrames;
    private BufferedImage[][] walkFrames;
    private BufferedImage[][] hurtFrames;
    private BufferedImage sofaSitBackFrame;
    private BufferedImage[] deathFrames;
    private int animationCounter = 0;
    private int animationFrame = 0;
    private int sittingCounter = 0;
    private int sittingReturnX = 0;
    private int sittingReturnY = 0;
    private boolean persistentSitting = false;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);

        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 12;
        solidArea.y = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 24;
        solidArea.height = 12;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        speed = WALK_SPEED;
        direction = "down";
        maxLife = 6;
        life = maxLife;
        setDefaultPositions();
    }

    public void setDefaultPositions() {
        worldX = gp.tileSize * 10;
        worldY = gp.tileSize * 12;
        direction = "down";
    }

    public void setPosition(int col, int row) {
        worldX = gp.tileSize * col;
        worldY = gp.tileSize * row;
    }

    public void setPixelPosition(int x, int y) {
        worldX = x;
        worldY = y;
    }

    public void startSittingOnSofa(int frames) {
        sittingCounter = Math.max(1, frames);
        persistentSitting = false;
        sittingReturnX = gp.tileSize * SOFA_SIT_X_COL;
        sittingReturnY = getSofaFrontExitY();
        setPixelPosition(gp.tileSize * SOFA_SIT_X_COL, gp.tileSize * SOFA_SIT_Y_ROW - gp.tileSize / 3);
        direction = "up";
        animationCounter = 0;
        animationFrame = 0;
    }

    private int getSofaFrontExitY() {
        return gp.tileSize * SOFA_SIT_Y_ROW - solidArea.y - solidArea.height;
    }

    public void sitOnSofaUntilMoved() {
        startSittingOnSofa(1);
        persistentSitting = true;
    }

    public void updatePoseState() {
        if (sittingCounter <= 0) {
            return;
        }

        if (persistentSitting) {
            if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
                finishSitting();
            }
            return;
        }

        sittingCounter--;
        if (sittingCounter == 0) {
            finishSitting();
        }
    }

    private void finishSitting() {
        sittingCounter = 0;
        persistentSitting = false;
        setPixelPosition(sittingReturnX, sittingReturnY);
        direction = "up";
    }

    public void restoreStatus() {
        invincible = false;
    }

    public void getPlayerImage() {
        idleFrames = loadDirectionalStrip(HERO_SPRITE + "_idle_anim_16x16", FRAMES_PER_DIRECTION);
        walkFrames = loadDirectionalStrip(HERO_SPRITE + "_run_16x16", FRAMES_PER_DIRECTION);
        sofaSitBackFrame = loadSingleFrame(HERO_SPRITE + "_sofa_sit_back_16x16");
        hurtFrames = idleFrames;
        deathFrames = new BufferedImage[] { idleFrames[DIRECTION_DOWN][0] };

        down1 = idleFrames[DIRECTION_DOWN][0];
        down2 = idleFrames[DIRECTION_DOWN][1];
        left1 = idleFrames[DIRECTION_LEFT][0];
        left2 = idleFrames[DIRECTION_LEFT][1];
        right1 = idleFrames[DIRECTION_RIGHT][0];
        right2 = idleFrames[DIRECTION_RIGHT][1];
        up1 = idleFrames[DIRECTION_UP][0];
        up2 = idleFrames[DIRECTION_UP][1];
    }

    @Override
    public void update() {
        boolean moving = isMovingInputActive();
        speed = isSprinting() ? SPRINT_SPEED : WALK_SPEED;

        if (sittingCounter > 0) {
            keyH.enterPressed = false;
            return;
        }

        if (keyH.upPressed) {
            direction = "up";
        }
        else if (keyH.downPressed) {
            direction = "down";
        }
        else if (keyH.leftPressed) {
            direction = "left";
        }
        else if (keyH.rightPressed) {
            direction = "right";
        }

        if (keyH.enterPressed) {
            interact();
            keyH.enterPressed = false;
        }

        if (moving) {
            collisionOn = false;
            gp.cChecker.checkTile(this);
            int objectIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objectIndex);
            gp.cChecker.checkEntity(this, gp.npc);

            if (!collisionOn) {
                moveInCurrentDirection();
            }

        }

        updateAnimation(moving);
    }

    private void pickUpObject(int objectIndex) {
        if (objectIndex == NO_TARGET || gp.obj[gp.currentMap][objectIndex] == null) {
            return;
        }

        if ("Lantern".equals(gp.obj[gp.currentMap][objectIndex].name)) {
            gp.hasLantern = true;
            gp.obj[gp.currentMap][objectIndex] = null;
            gp.playSE(Sound.LANTERN_PICKUP);
            gp.ui.addMessage("Фонарь найден");
            gp.saveLoad.save();
        }
    }

    private void interact() {
        int npcIndex = findFacingNPC();
        if (npcIndex != NO_TARGET) {
            gp.npc[gp.currentMap][npcIndex].speak();
            return;
        }

        int objectIndex = findFacingObject();
        if (objectIndex != NO_TARGET) {
            gp.story.interactObject(gp.obj[gp.currentMap][objectIndex].name);
        }
    }

    private int findFacingNPC() {
        Rectangle interactionArea = getFacingInteractionArea();

        for (int i = 0; i < gp.npc[gp.currentMap].length; i++) {
            Entity target = gp.npc[gp.currentMap][i];
            if (target != null) {
                Rectangle targetArea = new Rectangle(
                        target.worldX + target.solidArea.x,
                        target.worldY + target.solidArea.y,
                        target.solidArea.width,
                        target.solidArea.height
                );
                if (interactionArea.intersects(targetArea)) {
                    return i;
                }
            }
        }
        return NO_TARGET;
    }

    private int findFacingObject() {
        Rectangle interactionArea = getFacingInteractionArea();

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            Entity target = gp.obj[gp.currentMap][i];
            if (target != null) {
                Rectangle targetArea = new Rectangle(
                        target.worldX + target.solidArea.x,
                        target.worldY + target.solidArea.y,
                        target.solidArea.width,
                        target.solidArea.height
                );
                if (interactionArea.intersects(targetArea)) {
                    return i;
                }
            }
        }
        return NO_TARGET;
    }

    private Rectangle getFacingInteractionArea() {
        Rectangle interactionArea = new Rectangle(
                worldX + solidArea.x,
                worldY + solidArea.y,
                solidArea.width,
                solidArea.height
        );

        switch (direction) {
            case "up": interactionArea.y -= gp.tileSize / 2; break;
            case "down": interactionArea.y += gp.tileSize / 2; break;
            case "left": interactionArea.x -= gp.tileSize / 2; break;
            case "right": interactionArea.x += gp.tileSize / 2; break;
        }

        return interactionArea;
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = getCurrentFrame();
        int drawX = gp.worldToScreenX(worldX) - (image.getWidth() - gp.tileSize) / 2;
        int drawY = gp.worldToScreenY(worldY) - (image.getHeight() - gp.tileSize);
        g2.drawImage(image, drawX, drawY, null);
    }

    private void updateAnimation(boolean moving) {
        BufferedImage[] frames = getAnimationFrames(moving);
        int frameDelay = moving ? (isSprinting() ? 6 : 9) : 18;
        if (life <= 0 || invincible) {
            frameDelay = 10;
        }

        animationCounter++;
        if (animationCounter >= frameDelay) {
            animationFrame++;
            if (animationFrame >= frames.length) {
                animationFrame = life <= 0 ? frames.length - 1 : 0;
            }
            animationCounter = 0;
        }
    }

    private BufferedImage getCurrentFrame() {
        boolean moving = isMovingInputActive();
        BufferedImage[] frames = getAnimationFrames(moving);
        int frameIndex = life <= 0 ? Math.min(animationFrame, frames.length - 1) : animationFrame % frames.length;
        return frames[frameIndex];
    }

    public boolean isSprinting() {
        return keyH.shiftPressed && isMovingInputActive();
    }

    private boolean isMovingInputActive() {
        return keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;
    }

    private BufferedImage[] getAnimationFrames(boolean moving) {
        if (life <= 0) {
            return deathFrames;
        }
        if (sittingCounter > 0) {
            return new BufferedImage[] { sofaSitBackFrame };
        }
        if (invincible) {
            return getDirectionalFrames(hurtFrames);
        }
        if (moving) {
            return getDirectionalFrames(walkFrames);
        }
        return getDirectionalFrames(idleFrames);
    }

    private BufferedImage[] getDirectionalFrames(BufferedImage[][] frames) {
        switch (direction) {
            case "up": return frames[DIRECTION_UP];
            case "left": return frames[DIRECTION_LEFT];
            case "right": return frames[DIRECTION_RIGHT];
            default: return frames[DIRECTION_DOWN];
        }
    }

    private BufferedImage[][] loadDirectionalStrip(String path, int framesPerDirection) {
        BufferedImage[][] frames = new BufferedImage[DIRECTION_COUNT][framesPerDirection];
        BufferedImage sheet = loadImage(path);
        UtilityTool uTool = new UtilityTool();
        int drawWidth = (int) Math.round(gp.tileSize * DRAW_WIDTH_TILES);
        int drawHeight = (int) Math.round(gp.tileSize * DRAW_HEIGHT_TILES);

        for (int directionIndex = 0; directionIndex < DIRECTION_COUNT; directionIndex++) {
            for (int frameIndex = 0; frameIndex < framesPerDirection; frameIndex++) {
                int sourceColumn = directionIndex * framesPerDirection + frameIndex;
                BufferedImage frame = sheet.getSubimage(
                        sourceColumn * SOURCE_FRAME_WIDTH,
                        0,
                        SOURCE_FRAME_WIDTH,
                        SOURCE_FRAME_HEIGHT
                );
                frames[directionIndex][frameIndex] = uTool.scaleImage(frame, drawWidth, drawHeight);
            }
        }
        return frames;
    }

    private BufferedImage loadSingleFrame(String path) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage frame = loadImage(path);
        int drawWidth = (int) Math.round(gp.tileSize * DRAW_WIDTH_TILES);
        int drawHeight = (int) Math.round(gp.tileSize * DRAW_HEIGHT_TILES);
        return uTool.scaleImage(frame, drawWidth, drawHeight);
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path + ".png"));
        }
        catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot load player sprite: " + path, e);
        }
    }

}
