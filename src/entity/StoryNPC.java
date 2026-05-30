package entity;

import main.GamePanel;
import main.GameFonts;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StoryNPC extends Entity {

    private static final String CHARACTER_PREFIX = "character:";
    private static final double DRAW_SCALE = 1.15;

    private final String role;
    private final String displayName;
    private int drawSize;

    public StoryNPC(GamePanel gp, String role, String displayName, String spriteSet) {
        super(gp);

        this.role = role;
        this.displayName = displayName;
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
        drawSize = (int) Math.round(gp.tileSize * DRAW_SCALE);
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
    public void draw(Graphics2D g2) {
        int screenX = gp.worldToScreenX(worldX);
        int screenY = gp.worldToScreenY(worldY);
        int drawX = screenX - (drawSize - gp.tileSize) / 2;
        int drawY = screenY - (drawSize - gp.tileSize);

        if (gp.isInCamera(worldX - (drawSize - gp.tileSize) / 2,
                worldY - (drawSize - gp.tileSize), drawSize, drawSize)) {
            BufferedImage image = down1;
            g2.drawImage(image, drawX, drawY, null);

            Font oldFont = g2.getFont();
            g2.setFont(GameFonts.bold(13));
            FontMetrics fm = g2.getFontMetrics();
            int textX = drawX + drawSize / 2 - fm.stringWidth(displayName) / 2;
            int textY = drawY - 8;

            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRoundRect(textX - 6, textY - fm.getAscent(), fm.stringWidth(displayName) + 12, 18, 8, 8);
            g2.setColor(Color.white);
            g2.drawString(displayName, textX, textY);
            g2.setFont(oldFont);
        }
    }
}
