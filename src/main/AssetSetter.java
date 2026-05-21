package main;

import entity.Entity;
import entity.StoryNPC;
import entity.SwingChildNPC;
import object.StaticObject;

public class AssetSetter {

    private static final TreePlacement[] FOREST_TREES = {
            tree("tree_05", 2.9, 3.2, 5, 4),
            tree("tree_11", 2.4, 3.0, 10, 5),
            tree("tree_05", 2.9, 3.2, 15, 4),
            tree("tree_11", 2.4, 3.0, 20, 6),
            tree("tree_05", 2.9, 3.2, 24, 4),
            tree("tree_11", 2.4, 3.0, 36, 5),
            tree("tree_05", 2.9, 3.2, 37, 4),
            tree("tree_11", 2.4, 3.0, 42, 5),
            tree("tree_11", 2.4, 3.0, 4, 9),
            tree("tree_05", 2.9, 3.2, 7, 12),
            tree("tree_11", 2.4, 3.0, 4, 16),
            tree("tree_05", 2.9, 3.2, 6, 20),
            tree("tree_11", 2.4, 3.0, 4, 25),
            tree("tree_05", 2.9, 3.2, 8, 30),
            tree("tree_11", 2.4, 3.0, 5, 35),
            tree("tree_05", 2.9, 3.2, 7, 41),
            tree("tree_05", 2.9, 3.2, 43, 10),
            tree("tree_11", 2.4, 3.0, 39, 13),
            tree("tree_05", 2.9, 3.2, 43, 17),
            tree("tree_11", 2.4, 3.0, 40, 22),
            tree("tree_05", 2.9, 3.2, 43, 26),
            tree("tree_11", 2.4, 3.0, 39, 31),
            tree("tree_05", 2.9, 3.2, 43, 35),
            tree("tree_11", 2.4, 3.0, 40, 41),
            tree("tree_11", 2.4, 3.0, 12, 42),
            tree("tree_05", 2.9, 3.2, 17, 40),
            tree("tree_11", 2.4, 3.0, 26, 42),
            tree("tree_05", 2.9, 3.2, 31, 40),
            tree("tree_11", 2.4, 3.0, 36, 42),
            tree("tree_05", 2.9, 3.2, 43, 42),
            tree("tree_05", 2.9, 3.2, 10, 8),
            tree("tree_11", 2.4, 3.0, 14, 10),
            tree("tree_05", 2.9, 3.2, 18, 12),
            tree("tree_11", 2.4, 3.0, 8, 15),
            tree("tree_05", 2.9, 3.2, 13, 18),
            tree("tree_11", 2.4, 3.0, 25, 9),
            tree("tree_05", 2.9, 3.2, 36, 9),
            tree("tree_11", 2.4, 3.0, 35, 12),
            tree("tree_05", 2.9, 3.2, 37, 17),
            tree("tree_11", 2.4, 3.0, 9, 23),
            tree("tree_05", 2.9, 3.2, 8, 26),
            tree("tree_11", 2.4, 3.0, 18, 24),
            tree("tree_05", 2.9, 3.2, 10, 32),
            tree("tree_11", 2.4, 3.0, 35, 25),
            tree("tree_05", 2.9, 3.2, 38, 29),
            tree("tree_11", 2.4, 3.0, 32, 30),
            tree("tree_05", 2.9, 3.2, 8, 38),
            tree("tree_11", 2.4, 3.0, 10, 36),
            tree("tree_05", 2.9, 3.2, 28, 36),
            tree("tree_11", 2.4, 3.0, 34, 38)
    };

    private static final StaticPlacement[] FOREST_DECORATIONS = {
            decoration("decoration_10_flowers_pink", 0.9, 0.9, 20, 41),
            decoration("decoration_14_flowers_purple", 0.9, 0.9, 26, 38),
            decoration("decoration_08_mushroom_orange", 0.9, 0.9, 18, 34),
            decoration("decoration_12_mushroom_red", 0.8, 0.8, 14, 32),
            decoration("decoration_15_leaves_green", 0.9, 0.9, 12, 28),
            decoration("decoration_09_berries_red", 0.9, 0.9, 21, 27),
            decoration("decoration_05_mushroom_brown", 0.8, 0.8, 29, 27),
            decoration("decoration_11_leaf_curled", 0.9, 0.9, 33, 24),
            decoration("decoration_07_mushroom_blue", 0.8, 0.8, 29, 20),
            decoration("decoration_13_berries_green", 0.9, 0.9, 35, 19),
            decoration("decoration_02_sprout", 0.8, 0.9, 27, 15),
            decoration("decoration_01_mushroom_gold", 0.9, 0.9, 33, 13),
            decoration("decoration_00_crystal_blue", 1.0, 1.0, 27, 8),
            decoration("decoration_04_blue_bulb", 1.0, 1.0, 35, 8),
            decoration("decoration_03_mushroom_purple", 0.9, 0.9, 40, 16),
            decoration("decoration_06_cactus", 0.9, 0.9, 41, 25),
            decoration("decoration_10_flowers_pink", 0.9, 0.9, 36, 32),
            decoration("decoration_14_flowers_purple", 0.9, 0.9, 30, 33),
            decoration("decoration_09_berries_red", 0.9, 0.9, 6, 14),
            decoration("decoration_15_leaves_green", 0.9, 0.9, 7, 22),
            decoration("decoration_08_mushroom_orange", 0.9, 0.9, 6, 33),
            decoration("decoration_11_leaf_curled", 0.9, 0.9, 12, 39),
            decoration("decoration_00_crystal_blue", 1.0, 1.0, 39, 39),
            decoration("decoration_04_blue_bulb", 1.0, 1.0, 44, 33),
            decoration("decoration_13_berries_green", 0.9, 0.9, 16, 11),
            decoration("decoration_12_mushroom_red", 0.8, 0.8, 22, 13)
    };

    private static final VillageHousePlacement[] VILLAGE_HOUSES = {
            house("village_house_friend", "building_020_x956_y563_73x93", 2.0, 2.7, 13, 12),
            house("village_house_blue", "building_014_x13_y464_86x80", 2.7, 2.5, 25, 10),
            house("village_house_red", "building_022_x13_y568_86x80", 2.7, 2.5, 35, 12),
            house("village_house_green", "building_029_x13_y668_86x80", 2.7, 2.5, 10, 25),
            house("village_house_black", "building_038_x13_y758_86x80", 2.7, 2.5, 29, 28),
            house("village_house_white", "building_050_x13_y941_86x80", 2.7, 2.5, 38, 23),
            house("village_library", "building_006_x218_y93_360x183", 6.2, 3.2, 31, 7)
    };

    private static final VillagePropPlacement[] VILLAGE_PROPS = {
            prop("village_bench_square", "prop_027_x4_y122_32x15", 1.4, 0.6, 20, 25, false),
            prop("village_bench_friend", "prop_027_x4_y122_32x15", 1.4, 0.6, 16, 16, false),
            prop("village_sign", "prop_007_x96_y29_20x19", 0.8, 0.8, 23, 18, false),
            prop("village_fence_left", "prop_016_x16_y96_16x16", 0.6, 0.8, 8, 23, true),
            prop("village_fence_right", "prop_017_x59_y96_16x16", 0.6, 0.8, 42, 23, true)
    };

    private final GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        clear(gp.obj);
        placeApartmentObjects();
        placeForestObjects();
        placeVillageObjects();
    }

    public void setNPC() {
        clear(gp.npc);

        placeNPC(MapId.APARTMENT, 0, new StoryNPC(gp, StoryManager.SHADOW_APARTMENT, "Тень", "character:shadow"), 18, 12);

        placeNPC(MapId.FOREST_DOUBTS, 0, new SwingChildNPC(gp, "Ребёнок"), 30, 8);
        placeNPC(MapId.FOREST_DOUBTS, 1, new StoryNPC(gp, StoryManager.SHADOW_FOREST, "Тень", "character:shadow"), 31, 21);

        placeNPC(MapId.VILLAGE, 0, new StoryNPC(gp, StoryManager.FRIEND, "Друг", "character:friend"), 13, 14);
        placeNPC(MapId.VILLAGE, 1, new StoryNPC(gp, StoryManager.ELDER, "Старик", "character:elder"), 36, 14);

        placeNPC(MapId.MOUNTAIN, 0, new StoryNPC(gp, StoryManager.WARRIOR, "Воин", "character:warrior"), 35, 29);
    }

    private void placeApartmentObjects() {
        StaticObject bed = new StaticObject(gp, "Bed", "/objects/home/bed", 2.5, 2.5, true);
        bed.solidArea.x = gp.tileSize * 3 / 4;
        bed.solidArea.y = gp.tileSize / 4;
        bed.solidArea.width = gp.tileSize + gp.tileSize / 12;
        bed.solidArea.height = gp.tileSize + gp.tileSize * 5 / 8;
        syncSolidDefaults(bed);
        placeObject(MapId.APARTMENT, 0, bed, 21, 9);

        StaticObject mirror = new StaticObject(gp, "Mirror", "/objects/home/mirror_sink", 1.7, 1.7, true);
        mirror.solidArea.x = gp.tileSize / 3;
        mirror.solidArea.y = gp.tileSize;
        mirror.solidArea.width = gp.tileSize;
        mirror.solidArea.height = gp.tileSize / 2;
        syncSolidDefaults(mirror);
        placeObject(MapId.APARTMENT, 1, mirror, 13, 7);
    }

    private void placeForestObjects() {
        int index = 0;

        for (TreePlacement placement : FOREST_TREES) {
            index = placeObject(MapId.FOREST_DOUBTS, index, createTree(placement), placement.col, placement.row);
        }
        for (StaticPlacement placement : FOREST_DECORATIONS) {
            index = placeObject(MapId.FOREST_DOUBTS, index, createStaticObject(placement), placement.col, placement.row);
        }
        if (!gp.hasLantern) {
            placeForestLantern(index, 23, 41);
        }
    }

    private void placeVillageObjects() {
        int index = 0;

        for (VillageHousePlacement placement : VILLAGE_HOUSES) {
            index = placeVillageHouse(index, placement);
        }
        for (VillagePropPlacement placement : VILLAGE_PROPS) {
            index = placeVillageProp(index, placement);
        }
    }

    private void placeNPC(int map, int index, Entity npc, int col, int row) {
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

    private StaticObject createTree(TreePlacement placement) {
        StaticObject object = new StaticObject(
                gp,
                placement.name,
                "/trees/sliced/" + placement.name,
                placement.widthTiles,
                placement.heightTiles,
                true
        );
        int drawWidth = (int) Math.round(gp.tileSize * placement.widthTiles);
        int drawHeight = (int) Math.round(gp.tileSize * placement.heightTiles);
        object.solidArea.x = gp.tileSize / 6;
        object.solidArea.y = gp.tileSize / 6;
        object.solidArea.width = Math.max(gp.tileSize, drawWidth - gp.tileSize / 3);
        object.solidArea.height = Math.max(gp.tileSize, drawHeight - gp.tileSize / 3);
        syncSolidDefaults(object);
        return object;
    }

    private StaticObject createStaticObject(StaticPlacement placement) {
        return new StaticObject(
                gp,
                placement.name,
                placement.imagePath,
                placement.widthTiles,
                placement.heightTiles,
                placement.collision
        );
    }

    private int placeVillageHouse(int index, VillageHousePlacement placement) {
        StaticObject house = new StaticObject(
                gp,
                placement.name,
                "/tiles/sliced/Buildings/" + placement.imageName,
                placement.widthTiles,
                placement.heightTiles,
                true
        );
        house.solidArea.x = 0;
        house.solidArea.y = 0;
        house.solidArea.width = (int) Math.round(gp.tileSize * placement.widthTiles);
        house.solidArea.height = (int) Math.round(gp.tileSize * placement.heightTiles);
        syncSolidDefaults(house);
        return placeObject(MapId.VILLAGE, index, house, placement.col, placement.row);
    }

    private int placeVillageProp(int index, VillagePropPlacement placement) {
        StaticObject prop = new StaticObject(
                gp,
                placement.name,
                "/tiles/sliced/Props/" + placement.imageName,
                placement.widthTiles,
                placement.heightTiles,
                placement.collision
        );
        if (placement.collision) {
            prop.solidArea.x = 0;
            prop.solidArea.y = 0;
            prop.solidArea.width = Math.max(1, (int) Math.round(gp.tileSize * placement.widthTiles));
            prop.solidArea.height = Math.max(1, (int) Math.round(gp.tileSize * placement.heightTiles));
            syncSolidDefaults(prop);
        }
        return placeObject(MapId.VILLAGE, index, prop, placement.col, placement.row);
    }

    private int placeForestLantern(int index, int col, int row) {
        StaticObject lantern = new StaticObject(gp, "Lantern", "/objects/lantern", 1.7, 1.7, false);
        lantern.solidArea.x = gp.tileSize / 2;
        lantern.solidArea.y = gp.tileSize / 4;
        lantern.solidArea.width = gp.tileSize * 3 / 4;
        lantern.solidArea.height = gp.tileSize + gp.tileSize / 4;
        syncSolidDefaults(lantern);
        return placeObject(MapId.FOREST_DOUBTS, index, lantern, col, row);
    }

    private void syncSolidDefaults(Entity entity) {
        entity.solidAreaDefaultX = entity.solidArea.x;
        entity.solidAreaDefaultY = entity.solidArea.y;
    }

    private static TreePlacement tree(String name, double widthTiles, double heightTiles, int col, int row) {
        return new TreePlacement(name, widthTiles, heightTiles, col, row);
    }

    private static StaticPlacement decoration(String name, double widthTiles, double heightTiles, int col, int row) {
        return new StaticPlacement(name, "/objects/forest_decorations/" + name, widthTiles, heightTiles, col, row, false);
    }

    private static VillageHousePlacement house(String name, String imageName, double widthTiles, double heightTiles,
                                               int col, int row) {
        return new VillageHousePlacement(name, imageName, widthTiles, heightTiles, col, row);
    }

    private static VillagePropPlacement prop(String name, String imageName, double widthTiles, double heightTiles,
                                             int col, int row, boolean collision) {
        return new VillagePropPlacement(name, imageName, widthTiles, heightTiles, col, row, collision);
    }

    private static void clear(Entity[][] entities) {
        for (Entity[] layer : entities) {
            for (int i = 0; i < layer.length; i++) {
                layer[i] = null;
            }
        }
    }

    private static final class TreePlacement {
        final String name;
        final double widthTiles;
        final double heightTiles;
        final int col;
        final int row;

        TreePlacement(String name, double widthTiles, double heightTiles, int col, int row) {
            this.name = name;
            this.widthTiles = widthTiles;
            this.heightTiles = heightTiles;
            this.col = col;
            this.row = row;
        }
    }

    private static class StaticPlacement {
        final String name;
        final String imagePath;
        final double widthTiles;
        final double heightTiles;
        final int col;
        final int row;
        final boolean collision;

        StaticPlacement(String name, String imagePath, double widthTiles, double heightTiles, int col, int row,
                        boolean collision) {
            this.name = name;
            this.imagePath = imagePath;
            this.widthTiles = widthTiles;
            this.heightTiles = heightTiles;
            this.col = col;
            this.row = row;
            this.collision = collision;
        }
    }

    private static class VillageHousePlacement {
        final String name;
        final String imageName;
        final double widthTiles;
        final double heightTiles;
        final int col;
        final int row;

        VillageHousePlacement(String name, String imageName, double widthTiles, double heightTiles, int col, int row) {
            this.name = name;
            this.imageName = imageName;
            this.widthTiles = widthTiles;
            this.heightTiles = heightTiles;
            this.col = col;
            this.row = row;
        }
    }

    private static final class VillagePropPlacement extends VillageHousePlacement {
        final boolean collision;

        VillagePropPlacement(String name, String imageName, double widthTiles, double heightTiles, int col, int row,
                             boolean collision) {
            super(name, imageName, widthTiles, heightTiles, col, row);
            this.collision = collision;
        }
    }
}
