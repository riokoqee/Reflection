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

    private static final int FRAME_SIZE = 32;
    private static final int ROW_DOWN = 0;
    private static final int ROW_SIDE = 1;
    private static final int ROW_UP = 2;
    private static final double DRAW_SCALE = 2.1;
    private static final int NO_TARGET = 999;

    private final KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    private BufferedImage[][] idleFrames;
    private BufferedImage[][] walkFrames;
    private BufferedImage[][] hurtFrames;
    private BufferedImage[] idleLeftFrames;
    private BufferedImage[] walkLeftFrames;
    private BufferedImage[] hurtLeftFrames;
    private BufferedImage[] deathFrames;
    private int animationCounter = 0;
    private int animationFrame = 0;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);

        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 10;
        solidArea.y = 10;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 28;
        solidArea.height = 10;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        speed = 4;
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

    public void restoreStatus() {
        invincible = false;
    }

    public void getPlayerImage() {
        idleFrames = loadDirectionalSheet("/player/idle", 2);
        walkFrames = loadDirectionalSheet("/player/walk", 4);
        hurtFrames = loadDirectionalSheet("/player/hurt", 2);
        deathFrames = loadFlatSheet("/player/death", 3, 3);
        idleLeftFrames = flipFrames(idleFrames[ROW_SIDE]);
        walkLeftFrames = flipFrames(walkFrames[ROW_SIDE]);
        hurtLeftFrames = flipFrames(hurtFrames[ROW_SIDE]);

        down1 = idleFrames[ROW_DOWN][0];
        down2 = idleFrames[ROW_DOWN][1];
        left1 = idleLeftFrames[0];
        left2 = idleLeftFrames[1];
        right1 = idleFrames[ROW_SIDE][0];
        right2 = idleFrames[ROW_SIDE][1];
        up1 = idleFrames[ROW_UP][0];
        up2 = idleFrames[ROW_UP][1];
    }

    @Override
    public void update() {
        boolean moving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;

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
        else {
            gp.story.showCurrentHint();
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
        int frameDelay = moving ? 9 : 18;
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
        boolean moving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;
        BufferedImage[] frames = getAnimationFrames(moving);
        int frameIndex = life <= 0 ? Math.min(animationFrame, frames.length - 1) : animationFrame % frames.length;
        return frames[frameIndex];
    }

    private BufferedImage[] getAnimationFrames(boolean moving) {
        if (life <= 0) {
            return deathFrames;
        }
        if (invincible) {
            return getDirectionalFrames(hurtFrames, hurtLeftFrames);
        }
        if (moving) {
            return getDirectionalFrames(walkFrames, walkLeftFrames);
        }
        return getDirectionalFrames(idleFrames, idleLeftFrames);
    }

    private BufferedImage[] getDirectionalFrames(BufferedImage[][] frames, BufferedImage[] leftFrames) {
        switch (direction) {
            case "up": return frames[ROW_UP];
            case "left": return leftFrames;
            case "right": return frames[ROW_SIDE];
            default: return frames[ROW_DOWN];
        }
    }

    private BufferedImage[][] loadDirectionalSheet(String path, int columns) {
        BufferedImage[][] frames = new BufferedImage[3][columns];
        BufferedImage sheet = loadImage(path);
        UtilityTool uTool = new UtilityTool();
        int drawSize = (int) (gp.tileSize * DRAW_SCALE);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < columns; col++) {
                BufferedImage frame = sheet.getSubimage(col * FRAME_SIZE, row * FRAME_SIZE, FRAME_SIZE, FRAME_SIZE);
                frames[row][col] = uTool.scaleImage(frame, drawSize, drawSize);
            }
        }
        return frames;
    }

    private BufferedImage[] loadFlatSheet(String path, int columns, int rows) {
        BufferedImage[] frames = new BufferedImage[columns * rows];
        BufferedImage sheet = loadImage(path);
        UtilityTool uTool = new UtilityTool();
        int drawSize = (int) (gp.tileSize * DRAW_SCALE);
        int index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                BufferedImage frame = sheet.getSubimage(col * FRAME_SIZE, row * FRAME_SIZE, FRAME_SIZE, FRAME_SIZE);
                frames[index] = uTool.scaleImage(frame, drawSize, drawSize);
                index++;
            }
        }
        return frames;
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path + ".png"));
        }
        catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot load player sprite: " + path, e);
        }
    }

    private BufferedImage[] flipFrames(BufferedImage[] frames) {
        BufferedImage[] flipped = new BufferedImage[frames.length];
        for (int i = 0; i < frames.length; i++) {
            flipped[i] = flipHorizontal(frames[i]);
        }
        return flipped;
    }

    private BufferedImage flipHorizontal(BufferedImage image) {
        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = flipped.createGraphics();
        g2.drawImage(image, image.getWidth(), 0, -image.getWidth(), image.getHeight(), null);
        g2.dispose();
        return flipped;
    }
}
