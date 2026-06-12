package main;

import entity.Entity;
import entity.StoryNPC;
import entity.SwingChildNPC;
import object.StaticObject;

public class AssetSetter {

    private static final double VILLAGE_HOUSE_SCALE = 1.5;

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
            decoration("decoration_12_mushroom_red", 0.8, 0.8, 22, 13),
            decoration("decoration_02_sprout", 0.8, 0.9, 12, 7),
            decoration("decoration_11_leaf_curled", 0.9, 0.9, 17, 8),
            decoration("decoration_10_flowers_pink", 0.9, 0.9, 22, 8),
            decoration("decoration_14_flowers_purple", 0.9, 0.9, 39, 8),
            decoration("decoration_05_mushroom_brown", 0.8, 0.8, 42, 12),
            decoration("decoration_13_berries_green", 0.9, 0.9, 5, 13),
            decoration("decoration_08_mushroom_orange", 0.9, 0.9, 23, 16),
            decoration("decoration_04_blue_bulb", 1.0, 1.0, 38, 18),
            decoration("decoration_15_leaves_green", 0.9, 0.9, 15, 20),
            decoration("decoration_03_mushroom_purple", 0.9, 0.9, 6, 28),
            decoration("decoration_09_berries_red", 0.9, 0.9, 20, 31),
            decoration("decoration_07_mushroom_blue", 0.8, 0.8, 35, 30),
            decoration("decoration_01_mushroom_gold", 0.9, 0.9, 40, 36),
            decoration("decoration_12_mushroom_red", 0.8, 0.8, 15, 37),
            decoration("decoration_06_cactus", 0.9, 0.9, 5, 40),
            decoration("decoration_00_crystal_blue", 1.0, 1.0, 31, 42),
            decoration("decoration_11_leaf_curled", 0.9, 0.9, 44, 22),
            decoration("decoration_15_leaves_green", 0.9, 0.9, 24, 34),
            decoration("decoration_02_sprout", 0.8, 0.9, 14, 14),
            decoration("decoration_05_mushroom_brown", 0.8, 0.8, 33, 10),
            decoration("decoration_10_flowers_pink", 0.9, 0.9, 28, 12),
            decoration("decoration_14_flowers_purple", 0.9, 0.9, 36, 15),
            decoration("decoration_09_berries_red", 0.9, 0.9, 11, 27),
            decoration("decoration_13_berries_green", 0.9, 0.9, 40, 29)
    };

    private static final VillageHousePlacement[] VILLAGE_HOUSES = {
            house("village_house_friend", "building_020_x956_y563_73x93", 3.0, 3.8, 12, 3),
            house("village_house_north_west", "building_014_x13_y464_86x80", 4.0, 3.75, 5, 3),
            house("village_house_north_center", "building_022_x13_y568_86x80", 4.0, 3.75, 17, 3),
            house("village_house_north_east", "building_029_x13_y668_86x80", 4.0, 3.75, 25, 3),
            house("village_house_west_upper", "building_038_x13_y758_86x80", 4.0, 3.75, 1, 15),
            house("village_house_west_lower", "building_050_x13_y941_86x80", 4.0, 3.75, 1, 26),
            house("village_house_center_west", "building_018_x791_y563_73x93", 3.0, 3.8, 15, 16),
            house("village_house_center_east", "building_019_x871_y563_73x93", 3.0, 3.8, 32, 16),
            house("village_house_east_upper", "building_029_x13_y668_86x80", 4.0, 3.75, 42, 15),
            house("village_house_east_lower", "building_038_x13_y758_86x80", 4.0, 3.75, 42, 42),
            house("village_house_south_west", "building_050_x13_y941_86x80", 4.0, 3.75, 12, 36),
            house("village_house_south_center", "building_014_x13_y464_86x80", 4.0, 3.75, 25, 36),
            house("village_house_south_east", "building_022_x13_y568_86x80", 4.0, 3.75, 38, 36)
    };

    private static final StaticPlacement[] MOUNTAIN_DECORATIONS = {
            mountainDecoration("mountain_crystal_blue_01", "decoration_00_crystal_blue", 1.0, 1.0, 33, 12),
            mountainDecoration("mountain_crystal_blue_02", "decoration_00_crystal_blue", 1.0, 1.0, 38, 16),
            mountainDecoration("mountain_blue_bulb_01", "decoration_04_blue_bulb", 1.0, 1.0, 30, 18),
            mountainDecoration("mountain_blue_bulb_02", "decoration_04_blue_bulb", 1.0, 1.0, 24, 25),
            mountainDecoration("mountain_mushroom_gold_01", "decoration_01_mushroom_gold", 0.9, 0.9, 18, 36),
            mountainDecoration("mountain_mushroom_blue_01", "decoration_07_mushroom_blue", 0.8, 0.8, 27, 33),
            mountainDecoration("mountain_mushroom_purple_01", "decoration_03_mushroom_purple", 0.9, 0.9, 14, 42),
            mountainDecoration("mountain_mushroom_orange_01", "decoration_08_mushroom_orange", 0.9, 0.9, 38, 33),
            mountainDecoration("mountain_sprout_01", "decoration_02_sprout", 0.8, 0.9, 23, 22),
            mountainDecoration("mountain_sprout_02", "decoration_02_sprout", 0.8, 0.9, 32, 27),
            mountainDecoration("mountain_leaf_01", "decoration_11_leaf_curled", 0.9, 0.9, 21, 28),
            mountainDecoration("mountain_leaf_02", "decoration_15_leaves_green", 0.9, 0.9, 29, 36),
            mountainDecoration("mountain_flowers_pink_01", "decoration_10_flowers_pink", 0.9, 0.9, 16, 39),
            mountainDecoration("mountain_flowers_purple_01", "decoration_14_flowers_purple", 0.9, 0.9, 25, 37),
            mountainDecoration("mountain_berries_red_01", "decoration_09_berries_red", 0.9, 0.9, 37, 30),
            mountainDecoration("mountain_berries_green_01", "decoration_13_berries_green", 0.9, 0.9, 40, 35),
            mountainDecoration("mountain_mushroom_red_01", "decoration_12_mushroom_red", 0.8, 0.8, 12, 43),
            mountainDecoration("mountain_mushroom_brown_01", "decoration_05_mushroom_brown", 0.8, 0.8, 30, 40),
            mountainDecoration("mountain_crystal_blue_03", "decoration_00_crystal_blue", 1.0, 1.0, 36, 14),
            mountainDecoration("mountain_leaf_03", "decoration_15_leaves_green", 0.9, 0.9, 33, 24)
    };

    private static final StaticPlacement[] HOME_DECORATIONS = {
            homeDecoration("Bedroom Rug", "carpet_striped", 1.55, 1.15, 19, 10, false),

            homeDecoration("Kitchen Counter Left", "kitchen_counter_left", 1.15, 1.1, 20, 16, true),
            homeDecoration("Kitchen Counter Right", "kitchen_counter_right", 1.15, 1.1, 19, 16, true),
            homeDecoration("Kitchen Stove", "kitchen_stove", 1.15, 1.1, 18, 16, true),
            homeDecoration("Kitchen Wall Sink", "kitchen_sink_wall", 0.8, 0.75, 20, 16, false),
            homeDecoration("Kitchen Fridge", "kitchen_fridge", 1.0, 2.3, 21, 16, true),
            homeDecoration("Kitchen Rug", "carpet_green", 1.6, 1.25, 17, 21, false),
            homeInterior("Kitchen Dining Set", "if_dining_table_chairs", 2.25, 1.25, 14, 22, true),

            homeDecoration("Bathroom Toilet", "bathroom_toilet", 0.8, 1.35, 32, 17, true),
            homeDecoration("Bathroom Tub", "bathroom_tub", 2.0, 1.0, 29, 22, true),
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
        placeMountainObjects();
        placeLibraryObjects();
    }

    public void setNPC() {
        clear(gp.npc);

        if (gp.story.shouldShowApartmentShadow()) {
            StoryNPC mirrorShadow = new StoryNPC(gp, StoryManager.SHADOW_APARTMENT,
                    gp.tr("Тень", "Shadow"), "character:shadow", 0.62, false);
            placeNPCAtPixel(MapId.APARTMENT, 0, mirrorShadow,
                    getBedroomMirrorX() + gp.tileSize / 5,
                    getBedroomMirrorY() + gp.tileSize / 2);
        }

        placeNPC(MapId.FOREST_DOUBTS, 0, new SwingChildNPC(gp, gp.tr("Ребёнок", "Child")), 30, 8);
        placeNPC(MapId.FOREST_DOUBTS, 1,
                new StoryNPC(gp, StoryManager.SHADOW_FOREST, gp.tr("Тень", "Shadow"), "character:shadow"), 31, 21);

        placeNPC(MapId.VILLAGE, 0,
                new StoryNPC(gp, StoryManager.FRIEND, gp.tr("Друг", "Friend"), "character:friend", 1.35, true), 13, 10);

        placeNPC(MapId.LIBRARY, 0,
                new StoryNPC(gp, StoryManager.ELDER, gp.tr("Старик", "Elder"), "character:elder"), 24, 16);

        placeNPC(MapId.MOUNTAIN, 0,
                new StoryNPC(gp, StoryManager.WARRIOR, gp.tr("Воин", "Warrior"), "character:warrior_knight"), 35, 29);
        placeNPC(MapId.MOUNTAIN, 1,
                new StoryNPC(gp, StoryManager.TRAVELER, gp.tr("Путник", "Traveler"), "character:friend"), 28, 34);
        moveNPC(MapId.MOUNTAIN, 0, 35, 13);
        moveNPC(MapId.MOUNTAIN, 1, 26, 34);
    }

    public void removeObject(int map, String name) {
        for (int i = 0; i < gp.obj[map].length; i++) {
            if (gp.obj[map][i] != null && name.equals(gp.obj[map][i].name)) {
                gp.obj[map][i] = null;
                return;
            }
        }
    }

    private void placeApartmentObjects() {
        int index = 0;

        StaticObject bed = new StaticObject(gp, "Bed", "/objects/home/bed", 2.8, 2.8, true);
        bed.setSolidArea(gp.tileSize / 2, gp.tileSize / 5, gp.tileSize * 7 / 4, gp.tileSize * 2);
        int bedX = getBedroomBedX();
        int bedY = gp.tileSize * 7;
        index = placeObjectAtPixel(MapId.APARTMENT, index, bed, bedX, bedY);

        int dresserWidth = (int) Math.round(gp.tileSize * 1.25);
        int dresserX = bedX - dresserWidth - gp.tileSize / 8;
        int dresserY = gp.tileSize * 7;

        StaticObject mirror = new StaticObject(gp, "Mirror",
                "/objects/home/mirrors/mirror_floor_wood_brown", 1.6, 2.4, true);
        mirror.setSolidArea(gp.tileSize / 3, gp.tileSize / 3, gp.tileSize, gp.tileSize * 13 / 8);
        index = placeObjectAtPixel(MapId.APARTMENT, index, mirror, getBedroomMirrorX(), getBedroomMirrorY());

        StaticObject bedroomLamp = new StaticObject(gp, "Bedroom Lamp", "/objects/home/decor/bedroom_lamp_gold",
                0.65, 0.75, false);
        bedroomLamp.setSolidArea(gp.tileSize / 8, gp.tileSize / 8,
                gp.tileSize * 2 / 5, gp.tileSize / 2);
        bedroomLamp.setRenderSortY(dresserY + gp.tileSize);
        int lampWidth = (int) Math.round(gp.tileSize * 0.65);
        index = placeObjectAtPixel(MapId.APARTMENT, index, bedroomLamp,
                dresserX + (dresserWidth - lampWidth) / 2,
                dresserY - gp.tileSize / 3);

        StaticObject dresser = new StaticObject(gp, "Dresser", "/objects/home/dresser", 1.25, 1.25, true);
        index = placeObjectAtPixel(MapId.APARTMENT, index, dresser, dresserX, dresserY);

        StaticObject bedroomPlant = new StaticObject(gp, "Bedroom Plant", "/objects/home/decor/plant_tall_green",
                0.85, 1.25, true);
        bedroomPlant.setSolidArea(gp.tileSize / 6, gp.tileSize * 5 / 6,
                gp.tileSize / 2, gp.tileSize / 3);
        index = placeObjectAtPixel(MapId.APARTMENT, index, bedroomPlant,
                gp.tileSize * 22 - (int) Math.round(gp.tileSize * 0.85),
                gp.tileSize * 15 - (int) Math.round(gp.tileSize * 1.25));

        int bedroomTableX = gp.tileSize * 14 + gp.tileSize / 4;
        int bedroomTableY = gp.tileSize * 13;
        int bedroomTableWidth = (int) Math.round(gp.tileSize * 1.45);
        int bedroomTableHeight = (int) Math.round(gp.tileSize * 0.8);
        int oldPhotoWidth = (int) Math.round(gp.tileSize * 0.68);
        int oldPhotoHeight = (int) Math.round(gp.tileSize * 0.5);
        StaticObject oldPhoto = createStoryObject("Old Photo", "old_photo", 0.68, 0.5)
                .setRenderSortY(bedroomTableY + gp.tileSize + 1);
        index = placeObjectAtPixel(MapId.APARTMENT, index, oldPhoto,
                bedroomTableX + (bedroomTableWidth - oldPhotoWidth) / 2,
                bedroomTableY + (bedroomTableHeight - oldPhotoHeight) / 2);

        StaticObject bedroomTable = new StaticObject(gp, "Photo Table",
                "/objects/home/interiors/if_living_coffee_table", 1.45, 0.8, true);
        bedroomTable.setSolidArea(gp.tileSize / 12, gp.tileSize / 12,
                bedroomTableWidth - gp.tileSize / 6, bedroomTableHeight - gp.tileSize / 6);
        index = placeObjectAtPixel(MapId.APARTMENT, index, bedroomTable, bedroomTableX, bedroomTableY);

        StaticObject phoneDresser = new StaticObject(gp, "Phone Dresser",
                "/objects/home/dresser", 1.35, 1.2, true)
                .setAlternateImage("/objects/home/dresser_open_phone", () -> gp.story.isPhoneDresserOpen());
        phoneDresser.setSolidArea(gp.tileSize / 12, gp.tileSize / 8,
                gp.tileSize * 6 / 5, gp.tileSize);
        index = placeObjectAtPixel(MapId.APARTMENT, index, phoneDresser,
                gp.tileSize * 24 - gp.tileSize / 8,
                gp.tileSize * 7);

        if (gp.story.shouldShowDirtyDishes()) {
            StaticObject dirtyDishes = new StaticObject(gp, "Dirty Dishes",
                    "/objects/home/decor/dirty_dishes", 0.7, 0.42, false);
            dirtyDishes.setSolidArea(0, 0, gp.tileSize * 7 / 10, gp.tileSize * 4 / 3);
            dirtyDishes.setRenderSortY(gp.tileSize * 16 + 3);
            index = placeObjectAtPixel(MapId.APARTMENT, index, dirtyDishes,
                    gp.tileSize * 20 + gp.tileSize / 4,
                    gp.tileSize * 16 + gp.tileSize / 12);
        }

        StaticObject carpet = new StaticObject(gp, "Living Carpet", "/objects/home/carpet", 2.7, 2.0, false, true);
        index = placeObject(MapId.APARTMENT, index, carpet, 31, 10);

        StaticObject sofa = new StaticObject(gp, "Sofa", "/objects/home/interiors/if_living_sofa_gray",
                2.8, 1.75, true);
        int sofaWidth = (int) Math.round(gp.tileSize * 2.8);
        int sofaHeight = (int) Math.round(gp.tileSize * 1.75);
        sofa.setSolidArea(0, 0, sofaWidth, sofaHeight);
        sofa.setRenderSortY(gp.tileSize * 13);
        index = placeObject(MapId.APARTMENT, index, sofa, 32, 12);

        StaticObject tv = new StaticObject(gp, "TV", "/objects/home/tv_plasma_off", 2.45, 1.35, true)
                .setAlternateImage("/objects/home/tv_plasma_on", () -> gp.tvOn);
        int tvWidth = (int) Math.round(gp.tileSize * 2.45);
        int tvHeight = (int) Math.round(gp.tileSize * 1.35);
        tv.setSolidArea(gp.tileSize / 8, gp.tileSize / 8,
                tvWidth - gp.tileSize / 4, tvHeight - gp.tileSize / 4);
        int sofaCenterX = gp.tileSize * 32 + (int) Math.round(gp.tileSize * 2.8) / 2;
        index = placeObjectAtPixel(MapId.APARTMENT, index, tv,
                sofaCenterX - tvWidth / 2,
                gp.tileSize * 8);

        StaticObject bathroomMirror = new StaticObject(gp, "Bathroom Mirror", "/objects/home/mirror_sink",
                1.2, 1.35, true);
        bathroomMirror.setSolidArea(gp.tileSize / 5, gp.tileSize * 2 / 3,
                gp.tileSize * 4 / 5, gp.tileSize * 2 / 3);
        index = placeObject(MapId.APARTMENT, index, bathroomMirror, 28, 17);

        StaticObject door = new StaticObject(gp, "Door", "/objects/home/door", 1.45, 2.0, true);
        int doorWidth = (int) Math.round(gp.tileSize * 1.45);
        int doorHeight = (int) Math.round(gp.tileSize * 2.0);
        int apartmentBottomWallY = gp.tileSize * 24;
        int doorY = apartmentBottomWallY - doorHeight + gp.tileSize / 2;
        int doorCollisionY = apartmentBottomWallY - doorY;
        door.setSolidArea(0, doorCollisionY, doorWidth, doorHeight - doorCollisionY);
        door.setRenderSortY(apartmentBottomWallY + 1);
        int corridorDoorCenterX = gp.tileSize * 23 + gp.tileSize / 2;
        index = placeObjectAtPixel(MapId.APARTMENT, index, door,
                corridorDoorCenterX - doorWidth / 2,
                doorY);

        placeStaticObjects(MapId.APARTMENT, index, HOME_DECORATIONS);
    }

    private void placeForestObjects() {
        int index = 0;

        for (TreePlacement placement : FOREST_TREES) {
            index = placeObject(MapId.FOREST_DOUBTS, index, createTree(placement), placement.col, placement.row);
        }
        index = placeStaticObjects(MapId.FOREST_DOUBTS, index, FOREST_DECORATIONS);
        index = placeObject(MapId.FOREST_DOUBTS, index,
                createStoryObject("Lost Lantern", "lost_lantern", 0.8, 0.9), 18, 35);
        index = placeObject(MapId.FOREST_DOUBTS, index,
                createStoryObject("Wounded Bird", "wounded_bird", 0.75, 0.55), 28, 28);
        if (!gp.hasLantern) {
            placeForestLantern(index, 23, 41);
        }
    }

    private void placeVillageObjects() {
        int index = 0;

        for (VillageHousePlacement placement : VILLAGE_HOUSES) {
            index = placeVillageHouse(index, placement);
        }
        placeVillageLibraryDoorTrigger(index);
    }

    private void placeMountainObjects() {
        int index = placeStaticObjects(MapId.MOUNTAIN, 0, MOUNTAIN_DECORATIONS);
        index = placeObject(MapId.MOUNTAIN, index,
                createStoryObject("Mountain Fork", "mountain_fork", 0.9, 0.9), 31, 33);
        placeObject(MapId.MOUNTAIN, index,
                createStoryObject("Traveler Pack", "traveler_pack", 0.75, 0.75), 29, 34);
    }

    private void placeLibraryObjects() {
        int index = 0;

        StaticObject exitDoor = new StaticObject(gp, "Library Exit", "/objects/home/door", 1.45, 2.0, true);
        int doorWidth = (int) Math.round(gp.tileSize * 1.45);
        int doorHeight = (int) Math.round(gp.tileSize * 2.0);
        int bottomWallY = gp.tileSize * 23;
        int doorY = bottomWallY - doorHeight + gp.tileSize / 2;
        int doorCollisionY = bottomWallY - doorY;
        exitDoor.setSolidArea(0, doorCollisionY, doorWidth, doorHeight - doorCollisionY);
        exitDoor.setRenderSortY(bottomWallY + 1);
        index = placeObjectAtPixel(MapId.LIBRARY, index, exitDoor,
                gp.tileSize * 24 + gp.tileSize / 2 - doorWidth / 2,
                doorY);

        index = placeLibraryShelf(index, "Library Shelf Left", 17, 13);
        index = placeLibraryShelf(index, "Library Shelf Mid Left", 20, 13);
        index = placeLibraryShelf(index, "Library Shelf Mid Right", 28, 13);
        index = placeLibraryShelf(index, "Library Shelf Right", 31, 13);

        StaticObject readingTable = new StaticObject(gp, "Library Reading Table",
                "/objects/home/interiors/if_living_coffee_table", 1.9, 0.95, true);
        int tableWidth = (int) Math.round(gp.tileSize * 1.9);
        int tableHeight = (int) Math.round(gp.tileSize * 0.95);
        readingTable.setSolidArea(gp.tileSize / 12, gp.tileSize / 10,
                tableWidth - gp.tileSize / 6, tableHeight - gp.tileSize / 5);
        index = placeObject(MapId.LIBRARY, index, readingTable, 23, 19);

        StaticObject lamp = new StaticObject(gp, "Library Lamp", "/objects/home/decor/bedroom_lamp_gold",
                0.65, 0.75, true);
        lamp.setSolidArea(gp.tileSize / 8, gp.tileSize / 8,
                gp.tileSize * 2 / 5, gp.tileSize / 2);
        index = placeObject(MapId.LIBRARY, index, lamp, 27, 18);

        StaticObject plant = new StaticObject(gp, "Library Plant", "/objects/home/decor/plant_tall_green",
                0.85, 1.25, true);
        plant.setSolidArea(gp.tileSize / 6, gp.tileSize * 5 / 6,
                gp.tileSize / 2, gp.tileSize / 3);
        placeObject(MapId.LIBRARY, index, plant, 16, 21);
    }

    private void placeNPC(int map, int index, Entity npc, int col, int row) {
        placeNPCAtPixel(map, index, npc, gp.tileSize * col, gp.tileSize * row);
    }

    private void placeNPCAtPixel(int map, int index, Entity npc, int worldX, int worldY) {
        gp.npc[map][index] = npc;
        gp.npc[map][index].worldX = worldX;
        gp.npc[map][index].worldY = worldY;
    }

    private void moveNPC(int map, int index, int col, int row) {
        if (gp.npc[map][index] == null) {
            return;
        }
        gp.npc[map][index].worldX = gp.tileSize * col;
        gp.npc[map][index].worldY = gp.tileSize * row;
    }

    private int placeObject(int map, int index, StaticObject object, int col, int row) {
        return placeObjectAtPixel(map, index, object, gp.tileSize * col, gp.tileSize * row);
    }

    private int getBedroomBedX() {
        return gp.tileSize * 22 - (int) Math.round(gp.tileSize * 2.8);
    }

    private int getBedroomMirrorX() {
        int dresserWidth = (int) Math.round(gp.tileSize * 1.25);
        int dresserX = getBedroomBedX() - dresserWidth - gp.tileSize / 8;
        int mirrorWidth = (int) Math.round(gp.tileSize * 1.6);
        return dresserX - mirrorWidth - gp.tileSize / 4;
    }

    private int getBedroomMirrorY() {
        return gp.tileSize * 6 + gp.tileSize / 4;
    }

    private int placeObjectAtPixel(int map, int index, StaticObject object, int worldX, int worldY) {
        if (index >= gp.obj[map].length) {
            throw new IllegalStateException("Too many objects on map " + map);
        }
        gp.obj[map][index] = object;
        gp.obj[map][index].worldX = worldX;
        gp.obj[map][index].worldY = worldY;
        return index + 1;
    }

    private int placeLibraryShelf(int index, String name, int col, int row) {
        StaticObject shelf = new StaticObject(gp, name, "/objects/home/dresser", 1.25, 1.6, true);
        int shelfWidth = (int) Math.round(gp.tileSize * 1.25);
        int shelfHeight = (int) Math.round(gp.tileSize * 1.6);
        shelf.setSolidArea(gp.tileSize / 12, gp.tileSize / 8,
                shelfWidth - gp.tileSize / 6, shelfHeight - gp.tileSize / 6);
        return placeObject(MapId.LIBRARY, index, shelf, col, row);
    }

    private int placeStaticObjects(int map, int index, StaticPlacement[] placements) {
        for (StaticPlacement placement : placements) {
            StaticObject object = createStaticObject(placement);
            int worldX = gp.tileSize * placement.col;
            int worldY = gp.tileSize * placement.row;

            if ("Kitchen Wall Sink".equals(placement.name)) {
                worldX += gp.tileSize / 6;
                worldY -= gp.tileSize / 6;
                object.setRenderSortY(gp.tileSize * placement.row + 1);
            }
            else if ("Kitchen Counter Right".equals(placement.name)) {
                worldX -= gp.tileSize / 3;
            }
            else if ("Kitchen Counter Left".equals(placement.name)) {
                worldX -= gp.tileSize / 6;
            }
            else if ("Kitchen Stove".equals(placement.name)) {
                worldX -= gp.tileSize / 2;
            }

            index = placeObjectAtPixel(map, index, object, worldX, worldY);
        }
        return index;
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
        object.setSolidArea(gp.tileSize / 6, gp.tileSize / 6,
                Math.max(gp.tileSize, drawWidth - gp.tileSize / 3),
                Math.max(gp.tileSize, drawHeight - gp.tileSize / 3));
        return object;
    }

    private StaticObject createStaticObject(StaticPlacement placement) {
        return new StaticObject(
                gp,
                placement.name,
                placement.imagePath,
                placement.widthTiles,
                placement.heightTiles,
                placement.collision,
                isFloorPlacement(placement)
        );
    }

    private StaticObject createStoryObject(String name, String imageName, double widthTiles, double heightTiles) {
        StaticObject object = new StaticObject(
                gp,
                name,
                "/objects/story/" + imageName,
                widthTiles,
                heightTiles,
                false
        );
        object.setSolidArea(0, 0,
                Math.max(1, (int) Math.round(gp.tileSize * widthTiles)),
                Math.max(1, (int) Math.round(gp.tileSize * heightTiles)));
        return object;
    }

    private boolean isFloorPlacement(StaticPlacement placement) {
        return placement.name.contains("Rug") || placement.name.contains("Carpet");
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
        house.setSolidArea(0, 0,
                (int) Math.round(gp.tileSize * placement.widthTiles),
                (int) Math.round(gp.tileSize * placement.heightTiles));
        if ("village_house_north_east".equals(placement.name)) {
            house.setWorldLabel("Library",
                    (int) Math.round(gp.tileSize * placement.widthTiles / 2.0),
                    (int) Math.round(gp.tileSize * placement.heightTiles - gp.tileSize * 1.55));
        }
        return placeObject(MapId.VILLAGE, index, house, placement.col, placement.row);
    }

    private int placeVillageLibraryDoorTrigger(int index) {
        double widthTiles = 1.35;
        double heightTiles = 2.8;
        StaticObject door = new StaticObject(gp, "Village Library Door", "/objects/home/door",
                widthTiles, heightTiles, false);
        int doorWidth = (int) Math.round(gp.tileSize * widthTiles);
        int doorHeight = (int) Math.round(gp.tileSize * heightTiles);
        int houseX = gp.tileSize * 25;
        int houseY = gp.tileSize * 3;
        int houseWidth = (int) Math.round(gp.tileSize * 4.0 * VILLAGE_HOUSE_SCALE);
        int houseHeight = (int) Math.round(gp.tileSize * 3.75 * VILLAGE_HOUSE_SCALE);
        int doorX = houseX + houseWidth / 2 - doorWidth / 2;
        int doorY = houseY + houseHeight - gp.tileSize / 2;

        door.setSolidArea(0, 0, doorWidth, doorHeight);
        door.setRenderSortY(houseY + houseHeight + 1);
        door.setVisible(false);
        return placeObjectAtPixel(MapId.VILLAGE, index, door, doorX, doorY);
    }

    private int placeForestLantern(int index, int col, int row) {
        StaticObject lantern = new StaticObject(gp, "Lantern", "/objects/lantern", 1.7, 1.7, false);
        lantern.setSolidArea(gp.tileSize / 2, gp.tileSize / 4,
                gp.tileSize * 3 / 4, gp.tileSize + gp.tileSize / 4);
        return placeObject(MapId.FOREST_DOUBTS, index, lantern, col, row);
    }

    private static TreePlacement tree(String name, double widthTiles, double heightTiles, int col, int row) {
        return new TreePlacement(name, widthTiles, heightTiles, col, row);
    }

    private static StaticPlacement decoration(String name, double widthTiles, double heightTiles, int col, int row) {
        return new StaticPlacement(name, "/objects/forest_decorations/" + name, widthTiles, heightTiles, col, row, false);
    }

    private static StaticPlacement mountainDecoration(String name, String imageName, double widthTiles,
                                                      double heightTiles, int col, int row) {
        return new StaticPlacement(name, "/objects/forest_decorations/" + imageName,
                widthTiles, heightTiles, col, row, false);
    }

    private static StaticPlacement homeDecoration(String name, String imageName, double widthTiles, double heightTiles,
                                                  int col, int row, boolean collision) {
        return new StaticPlacement(name, "/objects/home/decor/" + imageName,
                widthTiles, heightTiles, col, row, collision);
    }

    private static StaticPlacement homeInterior(String name, String imageName, double widthTiles, double heightTiles,
                                                int col, int row, boolean collision) {
        return new StaticPlacement(name, "/objects/home/interiors/" + imageName,
                widthTiles, heightTiles, col, row, collision);
    }

    private static VillageHousePlacement house(String name, String imageName, double widthTiles, double heightTiles,
                                               int col, int row) {
        return new VillageHousePlacement(name, imageName,
                widthTiles * VILLAGE_HOUSE_SCALE,
                heightTiles * VILLAGE_HOUSE_SCALE,
                col, row);
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

}
