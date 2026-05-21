package entity;

import main.GamePanel;

import java.awt.*;

public class StoryNPC extends Entity {

    private static final String CHARACTER_PREFIX = "character:";

    private final String role;
    private final String displayName;

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
        up1 = setup(path, gp.tileSize, gp.tileSize);
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
        super.draw(g2);

        int screenX = gp.worldToScreenX(worldX);
        int screenY = gp.worldToScreenY(worldY);

        if (gp.isInCamera(worldX, worldY, gp.tileSize, gp.tileSize)) {

            Font oldFont = g2.getFont();
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int textX = screenX + gp.tileSize / 2 - fm.stringWidth(displayName) / 2;
            int textY = screenY - 8;

            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRoundRect(textX - 6, textY - fm.getAscent(), fm.stringWidth(displayName) + 12, 18, 8, 8);
            g2.setColor(Color.white);
            g2.drawString(displayName, textX, textY);
            g2.setFont(oldFont);
        }
    }
}
