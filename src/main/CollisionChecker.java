package main;

import entity.Entity;

import java.awt.Rectangle;

public class CollisionChecker {

    public static final int NO_COLLISION = 999;

    private final GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {

        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width - 1;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height - 1;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                if (isBlocked(entityLeftCol, entityTopRow) || isBlocked(entityRightCol, entityTopRow)) {
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                if (isBlocked(entityLeftCol, entityBottomRow) || isBlocked(entityRightCol, entityBottomRow)) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                if (isBlocked(entityLeftCol, entityTopRow) || isBlocked(entityLeftCol, entityBottomRow)) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                if (isBlocked(entityRightCol, entityTopRow) || isBlocked(entityRightCol, entityBottomRow)) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    private boolean isBlocked(int col, int row) {
        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow) {
            return true;
        }

        int tileNum = gp.tileM.mapTileNum[gp.currentMap][col][row];
        if (tileNum < 0 || tileNum >= gp.tileM.tile.length) {
            return true;
        }
        return gp.tileM.tile[tileNum] == null || gp.tileM.tile[tileNum].collision;
    }

    public int checkObject(Entity entity, boolean player) {

        int index = NO_COLLISION;
        Rectangle entityArea = getNextArea(entity);

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            Entity object = gp.obj[gp.currentMap][i];

            if (object != null && entityArea.intersects(getWorldArea(object))) {
                if (object.collision) {
                    entity.collisionOn = true;
                }
                if (player) {
                    index = i;
                }
            }
        }
        return index;
    }

    public int checkEntity(Entity entity, Entity[][] target) {

        int index = NO_COLLISION;
        Rectangle entityArea = getNextArea(entity);

        for (int i = 0; i < target[gp.currentMap].length; i++) {
            Entity targetEntity = target[gp.currentMap][i];

            if (targetEntity != null && targetEntity != entity && entityArea.intersects(getWorldArea(targetEntity))) {
                entity.collisionOn = true;
                index = i;
            }
        }
        return index;
    }

    private Rectangle getNextArea(Entity entity) {
        Rectangle area = getWorldArea(entity);
        switch (entity.direction) {
            case "up": area.y -= entity.speed; break;
            case "down": area.y += entity.speed; break;
            case "left": area.x -= entity.speed; break;
            case "right": area.x += entity.speed; break;
        }
        return area;
    }

    private Rectangle getWorldArea(Entity entity) {
        return new Rectangle(
                entity.worldX + entity.solidArea.x,
                entity.worldY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height
        );
    }
}
