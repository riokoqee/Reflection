package object;

import entity.Entity;
import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StaticObject extends Entity {

    private final int drawWidth;
    private final int drawHeight;

    public StaticObject(GamePanel gp, String name, String imagePath, double widthTiles, double heightTiles, boolean collision) {
        super(gp);

        this.name = name;
        this.collision = collision;
        drawWidth = (int) Math.round(gp.tileSize * widthTiles);
        drawHeight = (int) Math.round(gp.tileSize * heightTiles);
        down1 = loadImage(imagePath, drawWidth, drawHeight);

        setSolidArea(0, collision ? Math.max(0, drawHeight - gp.tileSize) : 0,
                drawWidth, collision ? gp.tileSize : 0);
    }

    public StaticObject setSolidArea(int x, int y, int width, int height) {
        solidArea.x = x;
        solidArea.y = y;
        solidArea.width = width;
        solidArea.height = height;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        return this;
    }

    private BufferedImage loadImage(String imagePath, int width, int height) {
        UtilityTool uTool = new UtilityTool();
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            return uTool.scaleImage(image, width, height);
        }
        catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot load static object: " + imagePath, e);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = gp.worldToScreenX(worldX);
        int screenY = gp.worldToScreenY(worldY);

        if (gp.isInCamera(worldX, worldY, drawWidth, drawHeight)) {
            g2.drawImage(down1, screenX, screenY, null);
        }
    }
}
