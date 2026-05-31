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
            house("village_house_south_east", "building_022_x13_y568_86x80", 4.0, 3.75, 38, 36),
            house("village_library", "building_006_x218_y93_360x183", 8.5, 4.1, 31, 3)
    };

    private static final VillagePropPlacement[] VILLAGE_PROPS = {
            prop("village_bench_square", "prop_027_x4_y122_32x15", 1.4, 0.6, 20, 25, false),
            prop("village_bench_friend", "prop_027_x4_y122_32x15", 1.4, 0.6, 16, 16, false),
            prop("village_sign", "prop_007_x96_y29_20x19", 0.8, 0.8, 23, 18, false),
            prop("village_fence_left", "prop_016_x16_y96_16x16", 0.6, 0.8, 8, 23, true),
            prop("village_fence_right", "prop_017_x59_y96_16x16", 0.6, 0.8, 42, 23, true),
            prop("village_flowers_friend", "prop_010_x160_y43_56x16", 1.5, 0.45, 12, 16, false),
            prop("village_flowers_blue", "prop_010_x160_y43_56x16", 1.5, 0.45, 27, 13, false),
            prop("village_flowers_red", "prop_010_x160_y43_56x16", 1.5, 0.45, 36, 15, false),
            prop("village_flowers_green", "prop_010_x160_y43_56x16", 1.5, 0.45, 12, 28, false),
            prop("village_flowers_black", "prop_010_x160_y43_56x16", 1.5, 0.45, 31, 31, false),
            prop("village_flowers_white", "prop_010_x160_y43_56x16", 1.5, 0.45, 40, 26, false),
            prop("village_crates_friend", "prop_013_x96_y64_44x32", 1.2, 0.9, 17, 13, false),
            prop("village_crates_library", "prop_013_x96_y64_44x32", 1.2, 0.9, 29, 11, false),
            prop("village_rock_pond", "prop_033_x207_y149_38x19", 1.1, 0.55, 13, 37, false),
            prop("village_rock_north", "prop_034_x180_y158_22x10", 0.8, 0.45, 6, 7, false),
            prop("village_grass_patch_west", "prop_025_x203_y116_32x12", 1.0, 0.4, 6, 20, false),
            prop("village_grass_patch_east", "prop_026_x239_y120_17x8", 0.8, 0.35, 44, 20, false)
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
            homeInterior("Kitchen Food Crate", "if_kitchen_food_crate", 0.75, 1.0, 21, 22, false),

            homeDecoration("Bathroom Toilet", "bathroom_toilet", 0.8, 1.35, 34, 16, true),
            homeDecoration("Bathroom Tub", "bathroom_tub", 2.0, 1.0, 28, 22, true),
            homeDecoration("Bathroom Shower", "bathroom_shower", 1.0, 1.75, 35, 20, true),
            homeDecoration("Bathroom Rug", "bathroom_rug_blue", 0.8, 0.75, 31, 20, false),
            homeDecoration("Bathroom Towel Rail", "bathroom_towel_rail", 1.2, 0.45, 36, 16, false),
            homeInterior("Bathroom Laundry Basket", "if_bath_laundry_basket", 0.65, 0.85, 32, 22, false),
            homeInterior("Bathroom Water Bucket", "if_bath_water_bucket", 0.65, 0.85, 27, 22, false),

            homeDecoration("Corridor Lamp", "floor_lamp_gold", 0.7, 1.25, 23, 17, false),
            homeInterior("Corridor Key Holder", "if_corridor_key_holder", 1.1, 0.45, 23, 8, false),
            homeInterior("Corridor Plant", "if_corridor_plant", 0.75, 1.0, 25, 18, false)
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
    }

    public void setNPC() {
        clear(gp.npc);

        if (gp.story.shouldShowApartmentShadow()) {
            placeNPC(MapId.APARTMENT, 0, new StoryNPC(gp, StoryManager.SHADOW_APARTMENT, "Тень", "character:shadow"), 15, 12);
        }

        placeNPC(MapId.FOREST_DOUBTS, 0, new SwingChildNPC(gp, "Ребёнок"), 30, 8);
        placeNPC(MapId.FOREST_DOUBTS, 1, new StoryNPC(gp, StoryManager.SHADOW_FOREST, "Тень", "character:shadow"), 31, 21);

        placeNPC(MapId.VILLAGE, 0, new StoryNPC(gp, StoryManager.FRIEND, "Друг", "character:friend"), 13, 10);
        placeNPC(MapId.VILLAGE, 1, new StoryNPC(gp, StoryManager.ELDER, "Старик", "character:elder"), 36, 14);

        placeNPC(MapId.MOUNTAIN, 0, new StoryNPC(gp, StoryManager.WARRIOR, "Воин", "character:warrior"), 35, 29);
        placeNPC(MapId.MOUNTAIN, 1, new StoryNPC(gp, StoryManager.TRAVELER, "Путник", "character:friend"), 28, 34);
        moveNPC(MapId.MOUNTAIN, 0, 35, 13);
        moveNPC(MapId.MOUNTAIN, 1, 26, 34);
    }

    private void placeApartmentObjects() {
        int index = 0;

        StaticObject bed = new StaticObject(gp, "Bed", "/objects/home/bed", 2.8, 2.8, true);
        bed.setSolidArea(gp.tileSize / 2, gp.tileSize / 5, gp.tileSize * 7 / 4, gp.tileSize * 2);
        int bedX = gp.tileSize * 22 - (int) Math.round(gp.tileSize * 2.8);
        int bedY = gp.tileSize * 7;
        index = placeObjectAtPixel(MapId.APARTMENT, index, bed, bedX, bedY);

        int dresserWidth = (int) Math.round(gp.tileSize * 1.25);
        int dresserX = bedX - dresserWidth - gp.tileSize / 8;
        int dresserY = gp.tileSize * 7;

        StaticObject mirror = new StaticObject(gp, "Mirror",
                "/objects/home/mirrors/mirror_floor_wood_brown", 1.6, 2.4, true);
        mirror.setSolidArea(gp.tileSize / 3, gp.tileSize / 3, gp.tileSize, gp.tileSize * 13 / 8);
        int mirrorWidth = (int) Math.round(gp.tileSize * 1.6);
        index = placeObjectAtPixel(MapId.APARTMENT, index, mirror,
                dresserX - mirrorWidth - gp.tileSize / 4,
                gp.tileSize * 6 + gp.tileSize / 4);

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

        index = placeObject(MapId.APARTMENT, index,
                createStoryObject("Old Photo", "old_photo", 0.7, 0.55), 18, 14);
        index = placeObject(MapId.APARTMENT, index,
                createStoryObject("Phone Message", "phone_message", 0.65, 0.65), 23, 18);

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
        index = placeObject(MapId.APARTMENT, index, bathroomMirror, 30, 16);

        StaticObject door = new StaticObject(gp, "Door", "/objects/home/door", 1.2, 1.6, false);
        door.setSolidArea(gp.tileSize / 5, gp.tileSize / 3, gp.tileSize, gp.tileSize);
        index = placeObject(MapId.APARTMENT, index, door, 24, 22);

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
    }

    private void placeMountainObjects() {
        int index = placeStaticObjects(MapId.MOUNTAIN, 0, MOUNTAIN_DECORATIONS);
        index = placeObject(MapId.MOUNTAIN, index,
                createStoryObject("Mountain Fork", "mountain_fork", 0.9, 0.9), 31, 33);
        placeObject(MapId.MOUNTAIN, index,
                createStoryObject("Traveler Pack", "traveler_pack", 0.75, 0.75), 29, 34);
    }

    private void placeNPC(int map, int index, Entity npc, int col, int row) {
        gp.npc[map][index] = npc;
        gp.npc[map][index].worldX = gp.tileSize * col;
        gp.npc[map][index].worldY = gp.tileSize * row;
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

    private int placeObjectAtPixel(int map, int index, StaticObject object, int worldX, int worldY) {
        if (index >= gp.obj[map].length) {
            throw new IllegalStateException("Too many objects on map " + map);
        }
        gp.obj[map][index] = object;
        gp.obj[map][index].worldX = worldX;
        gp.obj[map][index].worldY = worldY;
        return index + 1;
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
            prop.setSolidArea(0, 0,
                    Math.max(1, (int) Math.round(gp.tileSize * placement.widthTiles)),
                    Math.max(1, (int) Math.round(gp.tileSize * placement.heightTiles)));
        }
        return placeObject(MapId.VILLAGE, index, prop, placement.col, placement.row);
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
