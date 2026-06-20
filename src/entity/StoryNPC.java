package entity;

import main.GamePanel;
import main.GameFonts;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StoryNPC extends Entity {

    private static final String CHARACTER_PREFIX = "character:";
    private static final double DEFAULT_DRAW_SCALE = 1.15;
    private static final int NAME_FONT_SIZE = 16;

    private final String role;
    private final String displayName;
    private final double drawScale;
    private final boolean showDisplayName;
    private int drawSize;

    public StoryNPC(GamePanel gp, String role, String displayName, String spriteSet) {
        this(gp, role, displayName, spriteSet, DEFAULT_DRAW_SCALE, true);
    }

    public StoryNPC(GamePanel gp, String role, String displayName, String spriteSet,
                    double drawScale, boolean showDisplayName) {
        super(gp);

        this.role = role;
        this.displayName = displayName;
        this.drawScale = drawScale;
        this.showDisplayName = showDisplayName;
        direction = "down";
        speed = 0;

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage(spriteSet);
    }

    private void getImage(String spriteSet) {
        String imagePath = spriteSet.startsWith(CHARACTER_PREFIX)
                ? "/player/characters/" + spriteSet.substring(CHARACTER_PREFIX.length())
                : spriteSet;
        loadStaticSprite(imagePath);
    }

    private void loadStaticSprite(String path) {
        drawSize = (int) Math.round(gp.tileSize * drawScale);
        up1 = setup(path, drawSize, drawSize);
        up2 = up1;
        down1 = up1;
        down2 = up1;
        left1 = up1;
        left2 = up1;
        right1 = up1;
        right2 = up1;
    }

    @Override
    public void speak() {
        gp.story.interact(role);
    }

    @Override
    public boolean isVisibleInCamera() {
        int drawX = worldX - (drawSize - gp.tileSize) / 2;
        int drawY = worldY - (drawSize - gp.tileSize);
        return gp.isInCamera(drawX, drawY, drawSize, drawSize);
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = gp.worldToScreenX(worldX);
        int screenY = gp.worldToScreenY(worldY);
        int drawX = screenX - (drawSize - gp.tileSize) / 2;
        int drawY = screenY - (drawSize - gp.tileSize);

        if (gp.isInCamera(worldX - (drawSize - gp.tileSize) / 2,
                worldY - (drawSize - gp.tileSize), drawSize, drawSize)) {
            BufferedImage image = down1;
            g2.drawImage(image, drawX, drawY, null);

            if (!showDisplayName) {
                return;
            }

            Font oldFont = g2.getFont();
            g2.setFont(GameFonts.bold(NAME_FONT_SIZE));
            FontMetrics fm = g2.getFontMetrics();
            int textX = drawX + drawSize / 2 - fm.stringWidth(displayName) / 2;
            int textY = drawY - 10;
            int labelHeight = fm.getHeight() + 6;

            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRoundRect(textX - 8, textY - fm.getAscent() - 3,
                    fm.stringWidth(displayName) + 16, labelHeight, 9, 9);
            g2.setColor(Color.white);
            g2.drawString(displayName, textX, textY);
            g2.setFont(oldFont);
        }
    }
}
