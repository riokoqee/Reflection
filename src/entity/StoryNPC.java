package entity;

import main.GamePanel;

import java.awt.*;

public class StoryNPC extends Entity {

    private final String role;
    private final String displayName;

    public StoryNPC(GamePanel gp, String role, String displayName, String spriteSet) {
        super(gp);

        this.role = role;
        this.displayName = displayName;
        type = type_npc;
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
        if (spriteSet.startsWith("character:")) {
            loadStaticSprite("/player/characters/" + spriteSet.substring("character:".length()));
        }
        else if ("merchant".equals(spriteSet)) {
            up1 = setup("/npc/merchant_down_1", gp.tileSize, gp.tileSize);
            up2 = setup("/npc/merchant_down_2", gp.tileSize, gp.tileSize);
            down1 = setup("/npc/merchant_down_1", gp.tileSize, gp.tileSize);
            down2 = setup("/npc/merchant_down_2", gp.tileSize, gp.tileSize);
            left1 = setup("/npc/merchant_down_1", gp.tileSize, gp.tileSize);
            left2 = setup("/npc/merchant_down_2", gp.tileSize, gp.tileSize);
            right1 = setup("/npc/merchant_down_1", gp.tileSize, gp.tileSize);
            right2 = setup("/npc/merchant_down_2", gp.tileSize, gp.tileSize);
        }
        else {
            up1 = setup("/npc/oldman_up_1", gp.tileSize, gp.tileSize);
            up2 = setup("/npc/oldman_up_2", gp.tileSize, gp.tileSize);
            down1 = setup("/npc/oldman_down_1", gp.tileSize, gp.tileSize);
            down2 = setup("/npc/oldman_down_2", gp.tileSize, gp.tileSize);
            left1 = setup("/npc/oldman_left_1", gp.tileSize, gp.tileSize);
            left2 = setup("/npc/oldman_left_2", gp.tileSize, gp.tileSize);
            right1 = setup("/npc/oldman_right_1", gp.tileSize, gp.tileSize);
            right2 = setup("/npc/oldman_right_2", gp.tileSize, gp.tileSize);
        }
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

    public void speak() {
        gp.story.interact(role);
    }

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
