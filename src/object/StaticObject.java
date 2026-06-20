package object;

import entity.Entity;
import main.GameFonts;
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
    private boolean visible = true;
    private String worldLabel;
    private int worldLabelCenterX;
    private int worldLabelBaselineY;

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

    public StaticObject setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public StaticObject setWorldLabel(String label, int centerX, int baselineY) {
        this.worldLabel = label;
        this.worldLabelCenterX = centerX;
        this.worldLabelBaselineY = baselineY;
        return this;
    }

    public String getWorldLabel() {
        return worldLabel;
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
    public boolean isVisibleInCamera() {
        return visible && gp.isInCamera(worldX, worldY, drawWidth, drawHeight);
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!visible) {
            return;
        }

        int screenX = gp.worldToScreenX(worldX);
        int screenY = gp.worldToScreenY(worldY);

        if (gp.isInCamera(worldX, worldY, drawWidth, drawHeight)) {
            BufferedImage image = useAlternateImage != null && useAlternateImage.getAsBoolean()
                    ? alternateImage
                    : down1;
            g2.drawImage(image, screenX, screenY, null);
            drawWorldLabel(g2, screenX, screenY);
        }
    }

    private void drawWorldLabel(Graphics2D g2, int screenX, int screenY) {
        if (worldLabel == null || worldLabel.isEmpty()) {
            return;
        }

        Font oldFont = g2.getFont();
        Color oldColor = g2.getColor();

        g2.setFont(GameFonts.bold(18f));
        FontMetrics metrics = g2.getFontMetrics();
        int textX = screenX + worldLabelCenterX - metrics.stringWidth(worldLabel) / 2;
        int textY = screenY + worldLabelBaselineY;
        int plateX = textX - 7;
        int plateY = textY - metrics.getAscent() - 3;
        int plateWidth = metrics.stringWidth(worldLabel) + 14;
        int plateHeight = metrics.getAscent() + metrics.getDescent() + 4;

        g2.setColor(new Color(24, 20, 16, 205));
        g2.fillRoundRect(plateX, plateY, plateWidth, plateHeight, 5, 5);
        g2.setColor(new Color(220, 187, 124));
        g2.drawRoundRect(plateX, plateY, plateWidth, plateHeight, 5, 5);
        g2.setColor(new Color(244, 226, 180));
        g2.drawString(worldLabel, textX, textY);

        g2.setFont(oldFont);
        g2.setColor(oldColor);
    }
}
