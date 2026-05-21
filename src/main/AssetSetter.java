package main;

import entity.Entity;
import entity.StoryNPC;
import entity.SwingChildNPC;
import object.StaticObject;

public class AssetSetter {

    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        for (int map = 0; map < gp.obj.length; map++) {
            for (int i = 0; i < gp.obj[map].length; i++) {
                gp.obj[map][i] = null;
            }
        }

        StaticObject bed = new StaticObject(gp, "Bed", "/objects/home/bed", 2.5, 2.5, true);
        bed.solidArea.x = gp.tileSize * 3 / 4;
        bed.solidArea.y = gp.tileSize / 4;
        bed.solidArea.width = gp.tileSize + gp.tileSize / 12;
        bed.solidArea.height = gp.tileSize + gp.tileSize * 5 / 8;
        bed.solidAreaDefaultX = bed.solidArea.x;
        bed.solidAreaDefaultY = bed.solidArea.y;
        placeObject(0, 0, bed, 21, 9);

        int forestObject = 0;
        forestObject = placeForestTree05(forestObject, 5, 4);
        forestObject = placeForestTree11(forestObject, 10, 5);
        forestObject = placeForestTree05(forestObject, 15, 4);
        forestObject = placeForestTree11(forestObject, 20, 6);
        forestObject = placeForestTree05(forestObject, 24, 4);
        forestObject = placeForestTree11(forestObject, 36, 5);
        forestObject = placeForestTree05(forestObject, 37, 4);
        forestObject = placeForestTree11(forestObject, 42, 5);

        forestObject = placeForestTree11(forestObject, 4, 9);
        forestObject = placeForestTree05(forestObject, 7, 12);
        forestObject = placeForestTree11(forestObject, 4, 16);
        forestObject = placeForestTree05(forestObject, 6, 20);
        forestObject = placeForestTree11(forestObject, 4, 25);
        forestObject = placeForestTree05(forestObject, 8, 30);
        forestObject = placeForestTree11(forestObject, 5, 35);
        forestObject = placeForestTree05(forestObject, 7, 41);

        forestObject = placeForestTree05(forestObject, 43, 10);
        forestObject = placeForestTree11(forestObject, 39, 13);
        forestObject = placeForestTree05(forestObject, 43, 17);
        forestObject = placeForestTree11(forestObject, 40, 22);
        forestObject = placeForestTree05(forestObject, 43, 26);
        forestObject = placeForestTree11(forestObject, 39, 31);
        forestObject = placeForestTree05(forestObject, 43, 35);
        forestObject = placeForestTree11(forestObject, 40, 41);

        forestObject = placeForestTree11(forestObject, 12, 42);
        forestObject = placeForestTree05(forestObject, 17, 40);
        forestObject = placeForestTree11(forestObject, 26, 42);
        forestObject = placeForestTree05(forestObject, 31, 40);
        forestObject = placeForestTree11(forestObject, 36, 42);
        forestObject = placeForestTree05(forestObject, 43, 42);

        forestObject = placeForestTree05(forestObject, 10, 8);
        forestObject = placeForestTree11(forestObject, 14, 10);
        forestObject = placeForestTree05(forestObject, 18, 12);
        forestObject = placeForestTree11(forestObject, 8, 15);
        forestObject = placeForestTree05(forestObject, 13, 18);
        forestObject = placeForestTree11(forestObject, 25, 9);
        forestObject = placeForestTree05(forestObject, 36, 9);
        forestObject = placeForestTree11(forestObject, 35, 12);
        forestObject = placeForestTree05(forestObject, 37, 17);

        forestObject = placeForestTree11(forestObject, 9, 23);
        forestObject = placeForestTree05(forestObject, 8, 26);
        forestObject = placeForestTree11(forestObject, 18, 24);
        forestObject = placeForestTree05(forestObject, 10, 32);
        forestObject = placeForestTree11(forestObject, 35, 25);
        forestObject = placeForestTree05(forestObject, 38, 29);
        forestObject = placeForestTree11(forestObject, 32, 30);
        forestObject = placeForestTree05(forestObject, 8, 38);
        forestObject = placeForestTree11(forestObject, 10, 36);
        forestObject = placeForestTree05(forestObject, 28, 36);
        forestObject = placeForestTree11(forestObject, 34, 38);

        forestObject = placeForestDecoration(forestObject, "decoration_10_flowers_pink", 0.9, 0.9, 20, 41);
        forestObject = placeForestDecoration(forestObject, "decoration_14_flowers_purple", 0.9, 0.9, 26, 38);
        forestObject = placeForestDecoration(forestObject, "decoration_08_mushroom_orange", 0.9, 0.9, 18, 34);
        forestObject = placeForestDecoration(forestObject, "decoration_12_mushroom_red", 0.8, 0.8, 14, 32);
        forestObject = placeForestDecoration(forestObject, "decoration_15_leaves_green", 0.9, 0.9, 12, 28);
        forestObject = placeForestDecoration(forestObject, "decoration_09_berries_red", 0.9, 0.9, 21, 27);
        forestObject = placeForestDecoration(forestObject, "decoration_05_mushroom_brown", 0.8, 0.8, 29, 27);
        forestObject = placeForestDecoration(forestObject, "decoration_11_leaf_curled", 0.9, 0.9, 33, 24);
        forestObject = placeForestDecoration(forestObject, "decoration_07_mushroom_blue", 0.8, 0.8, 29, 20);
        forestObject = placeForestDecoration(forestObject, "decoration_13_berries_green", 0.9, 0.9, 35, 19);
        forestObject = placeForestDecoration(forestObject, "decoration_02_sprout", 0.8, 0.9, 27, 15);
        forestObject = placeForestDecoration(forestObject, "decoration_01_mushroom_gold", 0.9, 0.9, 33, 13);
        forestObject = placeForestDecoration(forestObject, "decoration_00_crystal_blue", 1.0, 1.0, 27, 8);
        forestObject = placeForestDecoration(forestObject, "decoration_04_blue_bulb", 1.0, 1.0, 35, 8);
        forestObject = placeForestDecoration(forestObject, "decoration_03_mushroom_purple", 0.9, 0.9, 40, 16);
        forestObject = placeForestDecoration(forestObject, "decoration_06_cactus", 0.9, 0.9, 41, 25);
        forestObject = placeForestDecoration(forestObject, "decoration_10_flowers_pink", 0.9, 0.9, 36, 32);
        forestObject = placeForestDecoration(forestObject, "decoration_14_flowers_purple", 0.9, 0.9, 30, 33);
        forestObject = placeForestDecoration(forestObject, "decoration_09_berries_red", 0.9, 0.9, 6, 14);
        forestObject = placeForestDecoration(forestObject, "decoration_15_leaves_green", 0.9, 0.9, 7, 22);
        forestObject = placeForestDecoration(forestObject, "decoration_08_mushroom_orange", 0.9, 0.9, 6, 33);
        forestObject = placeForestDecoration(forestObject, "decoration_11_leaf_curled", 0.9, 0.9, 12, 39);
        forestObject = placeForestDecoration(forestObject, "decoration_00_crystal_blue", 1.0, 1.0, 39, 39);
        forestObject = placeForestDecoration(forestObject, "decoration_04_blue_bulb", 1.0, 1.0, 44, 33);
        forestObject = placeForestDecoration(forestObject, "decoration_13_berries_green", 0.9, 0.9, 16, 11);
        forestObject = placeForestDecoration(forestObject, "decoration_12_mushroom_red", 0.8, 0.8, 22, 13);

        if (!gp.hasLantern) {
            placeForestLantern(forestObject, 23, 41);
        }

        int villageObject = 0;
        villageObject = placeVillageHouse(villageObject, "village_house_friend", "building_020_x956_y563_73x93", 2.0, 2.7, 13, 12);
        villageObject = placeVillageHouse(villageObject, "village_house_blue", "building_014_x13_y464_86x80", 2.7, 2.5, 25, 10);
        villageObject = placeVillageHouse(villageObject, "village_house_red", "building_022_x13_y568_86x80", 2.7, 2.5, 35, 12);
        villageObject = placeVillageHouse(villageObject, "village_house_green", "building_029_x13_y668_86x80", 2.7, 2.5, 10, 25);
        villageObject = placeVillageHouse(villageObject, "village_house_black", "building_038_x13_y758_86x80", 2.7, 2.5, 29, 28);
        villageObject = placeVillageHouse(villageObject, "village_house_white", "building_050_x13_y941_86x80", 2.7, 2.5, 38, 23);
        villageObject = placeVillageHouse(villageObject, "village_library", "building_006_x218_y93_360x183", 6.2, 3.2, 31, 7);
        villageObject = placeVillageProp(villageObject, "village_bench_square", "prop_027_x4_y122_32x15", 1.4, 0.6, 20, 25, false);
        villageObject = placeVillageProp(villageObject, "village_bench_friend", "prop_027_x4_y122_32x15", 1.4, 0.6, 16, 16, false);
        villageObject = placeVillageProp(villageObject, "village_sign", "prop_007_x96_y29_20x19", 0.8, 0.8, 23, 18, false);
        villageObject = placeVillageProp(villageObject, "village_fence_left", "prop_016_x16_y96_16x16", 0.6, 0.8, 8, 23, true);
        villageObject = placeVillageProp(villageObject, "village_fence_right", "prop_017_x59_y96_16x16", 0.6, 0.8, 42, 23, true);
    }

    public void setNPC() {
        for (int map = 0; map < gp.npc.length; map++) {
            for (int i = 0; i < gp.npc[map].length; i++) {
                gp.npc[map][i] = null;
            }
        }

        place(0, 0, new StoryNPC(gp, StoryManager.SHADOW_APARTMENT, "Тень", "character:shadow"), 18, 12);

        place(1, 0, new SwingChildNPC(gp, "Ребёнок"), 30, 8);
        place(1, 1, new StoryNPC(gp, StoryManager.SHADOW_FOREST, "Тень", "character:shadow"), 31, 21);

        place(2, 0, new StoryNPC(gp, StoryManager.FRIEND, "Друг", "character:friend"), 13, 14);
        place(2, 1, new StoryNPC(gp, StoryManager.ELDER, "Старик", "character:elder"), 36, 14);

        place(3, 0, new StoryNPC(gp, StoryManager.WARRIOR, "Воин", "character:warrior"), 35, 29);
    }

    public void setMonster() {
    }

    public void setInteractiveTile() {
        for (int map = 0; map < gp.iTile.length; map++) {
            for (int i = 0; i < gp.iTile[map].length; i++) {
                gp.iTile[map][i] = null;
            }
        }
    }

    private void place(int map, int index, Entity npc, int col, int row) {
        gp.npc[map][index] = npc;
        gp.npc[map][index].worldX = gp.tileSize * col;
        gp.npc[map][index].worldY = gp.tileSize * row;
    }

    private int placeObject(int map, int index, StaticObject object, int col, int row) {
        if (index >= gp.obj[map].length) {
            throw new IllegalStateException("Too many objects on map " + map);
        }
        gp.obj[map][index] = object;
        gp.obj[map][index].worldX = gp.tileSize * col;
        gp.obj[map][index].worldY = gp.tileSize * row;
        return index + 1;
    }

    private int placeForestTree(int index, String name, double widthTiles, double heightTiles, int col, int row) {
        return placeObject(1, index, tree(name, widthTiles, heightTiles), col, row);
    }

    private int placeForestTree05(int index, int col, int row) {
        return placeForestTree(index, "tree_05", 2.9, 3.2, col, row);
    }

    private int placeForestTree11(int index, int col, int row) {
        return placeForestTree(index, "tree_11", 2.4, 3.0, col, row);
    }

    private int placeForestDecoration(int index, String name, double widthTiles, double heightTiles, int col, int row) {
        StaticObject object = new StaticObject(
                gp,
                name,
                "/objects/forest_decorations/" + name,
                widthTiles,
                heightTiles,
                false
        );
        return placeObject(1, index, object, col, row);
    }

    private int placeVillageHouse(int index, String name, String imageName, double widthTiles, double heightTiles, int col, int row) {
        StaticObject house = new StaticObject(gp, name, "/tiles/sliced/Buildings/" + imageName, widthTiles, heightTiles, true);
        house.solidArea.x = 0;
        house.solidArea.y = 0;
        house.solidArea.width = (int) Math.round(gp.tileSize * widthTiles);
        house.solidArea.height = (int) Math.round(gp.tileSize * heightTiles);
        house.solidAreaDefaultX = house.solidArea.x;
        house.solidAreaDefaultY = house.solidArea.y;
        return placeObject(2, index, house, col, row);
    }

    private int placeVillageProp(int index, String name, String imageName, double widthTiles, double heightTiles,
                                 int col, int row, boolean collision) {
        StaticObject prop = new StaticObject(gp, name, "/tiles/sliced/Props/" + imageName, widthTiles, heightTiles, collision);
        if (collision) {
            prop.solidArea.x = 0;
            prop.solidArea.y = 0;
            prop.solidArea.width = Math.max(1, (int) Math.round(gp.tileSize * widthTiles));
            prop.solidArea.height = Math.max(1, (int) Math.round(gp.tileSize * heightTiles));
            prop.solidAreaDefaultX = prop.solidArea.x;
            prop.solidAreaDefaultY = prop.solidArea.y;
        }
        return placeObject(2, index, prop, col, row);
    }

    private int placeForestLantern(int index, int col, int row) {
        StaticObject lantern = new StaticObject(gp, "Lantern", "/objects/lantern", 1.7, 1.7, false);
        lantern.solidArea.x = gp.tileSize / 2;
        lantern.solidArea.y = gp.tileSize / 4;
        lantern.solidArea.width = gp.tileSize * 3 / 4;
        lantern.solidArea.height = gp.tileSize + gp.tileSize / 4;
        lantern.solidAreaDefaultX = lantern.solidArea.x;
        lantern.solidAreaDefaultY = lantern.solidArea.y;
        return placeObject(1, index, lantern, col, row);
    }

    private StaticObject tree(String name, double widthTiles, double heightTiles) {
        StaticObject object = new StaticObject(gp, name, "/trees/sliced/" + name, widthTiles, heightTiles, true);
        int drawWidth = (int) Math.round(gp.tileSize * widthTiles);
        int drawHeight = (int) Math.round(gp.tileSize * heightTiles);
        object.solidArea.x = gp.tileSize / 6;
        object.solidArea.y = gp.tileSize / 6;
        object.solidArea.width = Math.max(gp.tileSize, drawWidth - gp.tileSize / 3);
        object.solidArea.height = Math.max(gp.tileSize, drawHeight - gp.tileSize / 3);
        object.solidAreaDefaultX = object.solidArea.x;
        object.solidAreaDefaultY = object.solidArea.y;
        return object;
    }
}
