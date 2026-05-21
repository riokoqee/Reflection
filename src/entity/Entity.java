package entity;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Entity {

    protected final GamePanel gp;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction = "down";
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collision = false;

    public int worldX, worldY;
    public boolean collisionOn = false;
    public boolean invincible = false;

    public String name;

    public int maxLife;
    public int life;
    public int speed;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public void speak() {}

    public void update() {}

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        int screenX = gp.worldToScreenX(worldX);
        int screenY = gp.worldToScreenY(worldY);

        if (gp.isInCamera(worldX, worldY, gp.tileSize, gp.tileSize)) {

            switch (direction) {
                case "up": image = up1; break;
                case "left": image = left1; break;
                case "right": image = right1; break;
                default: image = down1; break;
            }

            g2.drawImage(image, screenX, screenY, null);
        }
    }

    protected void moveInCurrentDirection() {
        switch (direction) {
            case "up": worldY -= speed; break;
            case "down": worldY += speed; break;
            case "left": worldX -= speed; break;
            case "right": worldX += speed; break;
        }
    }

    public BufferedImage setup(String imagePath, int width, int height) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            image = uTool.scaleImage(image, width, height);
        }
        catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot load entity sprite: " + imagePath, e);
        }

        return image;
    }
}
