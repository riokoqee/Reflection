package object;

import entity.Entity;
import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.BooleanSupplier;

public class StaticObject extends Entity {

    private final int drawWidth;
    private final int drawHeight;
    private final boolean floorLayer;
    private BufferedImage alternateImage;
    private BooleanSupplier useAlternateImage;
    private Integer renderSortY;

    public StaticObject(GamePanel gp, String name, String imagePath, double widthTiles, double heightTiles, boolean collision) {
        this(gp, name, imagePath, widthTiles, heightTiles, collision, false);
    }

    public StaticObject(GamePanel gp, String name, String imagePath, double widthTiles, double heightTiles,
                        boolean collision, boolean floorLayer) {
        super(gp);

        this.name = name;
        this.collision = collision;
        this.floorLayer = floorLayer;
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

    public StaticObject setRenderSortY(int renderSortY) {
        this.renderSortY = renderSortY;
        return this;
    }

    public StaticObject setAlternateImage(String imagePath, BooleanSupplier useAlternateImage) {
        this.alternateImage = loadImage(imagePath, drawWidth, drawHeight);
        this.useAlternateImage = useAlternateImage;
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
    public boolean isFloorLayer() {
        return floorLayer;
    }

    @Override
    public int getRenderSortY() {
        return renderSortY != null ? renderSortY : super.getRenderSortY();
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = gp.worldToScreenX(worldX);
        int screenY = gp.worldToScreenY(worldY);

        if (gp.isInCamera(worldX, worldY, drawWidth, drawHeight)) {
            BufferedImage image = useAlternateImage != null && useAlternateImage.getAsBoolean()
                    ? alternateImage
                    : down1;
            g2.drawImage(image, screenX, screenY, null);
        }
    }
}
