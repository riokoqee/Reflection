package entity;

import main.GamePanel;
import main.StoryManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class SwingChildNPC extends Entity {

    private final String displayName;
    private final BufferedImage childSprite;
    private int animationFrame = 0;

    public SwingChildNPC(GamePanel gp, String displayName) {
        super(gp);
        this.displayName = displayName;
        type = type_npc;
        direction = "down";
        speed = 0;
        childSprite = setup("/player/characters/child", gp.tileSize, gp.tileSize);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void update() {
        animationFrame++;
    }

    @Override
    public void speak() {
        gp.story.interact(StoryManager.CHILD);
    }

    @Override
    public void draw(Graphics2D g2) {
        int centerX = gp.worldToScreenX(worldX) + gp.tileSize / 2;
        int centerY = gp.worldToScreenY(worldY) + gp.tileSize / 2;
        int frameWidth = gp.tileSize * 4;
        int frameHeight = gp.tileSize * 3;
        int topY = centerY - gp.tileSize * 2;
        int groundY = topY + frameHeight + gp.tileSize / 3;
        int leftFootX = centerX - frameWidth / 2;
        int rightFootX = centerX + frameWidth / 2;
        int leftTopX = centerX - frameWidth / 3;
        int rightTopX = centerX + frameWidth / 3;

        if (!gp.isInCamera(worldX - frameWidth / 2, worldY - gp.tileSize * 2, frameWidth, frameHeight + gp.tileSize)) {
            return;
        }

        double swing = Math.sin(animationFrame * 0.055);
        int seatCenterX = centerX + (int) (swing * gp.tileSize * 0.55);
        int seatY = centerY + (int) (Math.abs(swing) * gp.tileSize * 0.16);
        int seatWidth = gp.tileSize + gp.tileSize / 3;
        int seatHeight = gp.tileSize / 6;
        int ropeLeftX = seatCenterX - seatWidth / 3;
        int ropeRightX = seatCenterX + seatWidth / 3;

        Stroke oldStroke = g2.getStroke();
        Composite oldComposite = g2.getComposite();

        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(54, 39, 31));
        g2.drawLine(leftFootX + 4, groundY + 4, leftTopX + 4, topY + 4);
        g2.drawLine(rightFootX + 4, groundY + 4, rightTopX + 4, topY + 4);
        g2.drawLine(leftTopX + 4, topY + 4, rightTopX + 4, topY + 4);

        g2.setColor(new Color(118, 82, 53));
        g2.drawLine(leftFootX, groundY, leftTopX, topY);
        g2.drawLine(rightFootX, groundY, rightTopX, topY);
        g2.drawLine(leftTopX, topY, rightTopX, topY);

        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(193, 176, 129));
        g2.drawLine(centerX - gp.tileSize / 4, topY + 8, ropeLeftX, seatY);
        g2.drawLine(centerX + gp.tileSize / 4, topY + 8, ropeRightX, seatY);

        g2.setColor(new Color(57, 36, 28));
        g2.fillRoundRect(seatCenterX - seatWidth / 2, seatY, seatWidth, seatHeight, 6, 6);
        g2.setColor(new Color(131, 84, 49));
        g2.fillRoundRect(seatCenterX - seatWidth / 2, seatY - 2, seatWidth, seatHeight, 6, 6);

        drawChild(g2, seatCenterX, seatY, swing);
        drawName(g2, centerX, topY - 10);

        g2.setComposite(oldComposite);
        g2.setStroke(oldStroke);
    }

    private void drawChild(Graphics2D g2, int seatCenterX, int seatY, double swing) {
        int childX = seatCenterX - gp.tileSize / 2;
        int childY = seatY - gp.tileSize + 4;
        double rotation = swing * 0.10;
        AffineTransform oldTransform = g2.getTransform();

        g2.rotate(rotation, seatCenterX, childY + gp.tileSize / 2);
        g2.drawImage(childSprite, childX, childY, null);
        g2.setTransform(oldTransform);
    }

    private void drawName(Graphics2D g2, int centerX, int y) {
        Font oldFont = g2.getFont();
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        int textX = centerX - fm.stringWidth(displayName) / 2;

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(textX - 6, y - fm.getAscent(), fm.stringWidth(displayName) + 12, 18, 8, 8);
        g2.setColor(Color.white);
        g2.drawString(displayName, textX, y);
        g2.setFont(oldFont);
    }
}
