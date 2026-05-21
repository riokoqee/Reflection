package tile_interactive;

import entity.Entity;
import main.GamePanel;

import java.awt.*;

public class InteractiveTile extends Entity {

    GamePanel gp;
    public boolean destructible = false;

    public InteractiveTile(GamePanel gp, int col, int row) {
        super(gp);
        this.gp = gp;
    }

    public boolean isCorrectItem(Entity entity) {

        boolean isCorrectItem = false;
        return isCorrectItem;
    }

    public void playSE() {}

    public InteractiveTile getDestroyedForm() {
        InteractiveTile tile = null;
        return tile;
    }

    public void update() {

        if (invincible == true) {
            invincibleCounter++;
            if (invincibleCounter > 20) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    public void draw(Graphics2D g2) {

        int screenX = gp.worldToScreenX(worldX);
        int screenY = gp.worldToScreenY(worldY);

        if (gp.isInCamera(worldX, worldY, gp.tileSize, gp.tileSize)) {

            g2.drawImage(down1, screenX, screenY,null);
        }
    }
}
