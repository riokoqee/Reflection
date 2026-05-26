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
            mountainDecoration("mountain_crystal_blue_01", "decoration_00_crystal_blue", 1.0, 1.0, 20, 38),
            mountainDecoration("mountain_crystal_blue_02", "decoration_00_crystal_blue", 1.0, 1.0, 34, 37),
            mountainDecoration("mountain_blue_bulb_01", "decoration_04_blue_bulb", 1.0, 1.0, 25, 39),
            mountainDecoration("mountain_blue_bulb_02", "decoration_04_blue_bulb", 1.0, 1.0, 37, 31),
            mountainDecoration("mountain_mushroom_gold_01", "decoration_01_mushroom_gold", 0.9, 0.9, 12, 31),
            mountainDecoration("mountain_mushroom_blue_01", "decoration_07_mushroom_blue", 0.8, 0.8, 20, 8),
            mountainDecoration("mountain_mushroom_purple_01", "decoration_03_mushroom_purple", 0.9, 0.9, 35, 41),
            mountainDecoration("mountain_mushroom_orange_01", "decoration_08_mushroom_orange", 0.9, 0.9, 38, 37),
            mountainDecoration("mountain_sprout_01", "decoration_02_sprout", 0.8, 0.9, 21, 20),
            mountainDecoration("mountain_sprout_02", "decoration_02_sprout", 0.8, 0.9, 36, 20),
            mountainDecoration("mountain_leaf_01", "decoration_11_leaf_curled", 0.9, 0.9, 29, 20),
            mountainDecoration("mountain_leaf_02", "decoration_15_leaves_green", 0.9, 0.9, 23, 24),
            mountainDecoration("mountain_flowers_pink_01", "decoration_10_flowers_pink", 0.9, 0.9, 20, 36),
            mountainDecoration("mountain_flowers_purple_01", "decoration_14_flowers_purple", 0.9, 0.9, 26, 36),
            mountainDecoration("mountain_berries_red_01", "decoration_09_berries_red", 0.9, 0.9, 34, 36),
            mountainDecoration("mountain_berries_green_01", "decoration_13_berries_green", 0.9, 0.9, 37, 36),
            mountainDecoration("mountain_mushroom_red_01", "decoration_12_mushroom_red", 0.8, 0.8, 20, 42),
            mountainDecoration("mountain_mushroom_brown_01", "decoration_05_mushroom_brown", 0.8, 0.8, 38, 42),
            mountainDecoration("mountain_crystal_blue_03", "decoration_00_crystal_blue", 1.0, 1.0, 34, 39),
            mountainDecoration("mountain_leaf_03", "decoration_15_leaves_green", 0.9, 0.9, 31, 40)
    };

    private static final StaticPlacement[] HOME_DECORATIONS = {
            homeInterior("Bedroom Window", "if_window_curtains", 1.8, 1.2, 11, 7, false),
            homeInterior("Bedroom Wardrobe", "if_bedroom_wardrobe", 1.1, 2.0, 17, 7, true),
            homeInterior("Bedroom Dresser", "if_bedroom_dresser", 1.9, 0.9, 11, 13, true),
            homeDecoration("Bedroom Lamp", "bedroom_lamp_gold", 0.65, 0.75, 18, 11, false),
            homeInterior("Bedroom Plant", "if_plant_tall", 0.7, 1.0, 15, 12, false),
            homeDecoration("Bedroom Rug", "carpet_striped", 1.2, 1.75, 13, 10, false),

            homeInterior("Hall Bookshelf", "if_living_bookshelf", 1.5, 1.5, 27, 7, true),
            homeInterior("Hall Coffee Table", "if_living_coffee_table", 1.6, 1.0, 31, 11, false),
            homeInterior("Hall Wall Shelf", "if_living_wall_shelf", 1.4, 1.0, 30, 7, false),
            homeInterior("Hall Globe", "if_living_globe", 1.6, 1.6, 36, 9, false),
            homeDecoration("Hall Floor Lamp", "floor_lamp_gold", 0.7, 1.25, 28, 11, false),
            homeDecoration("Hall Window", "window_wide", 1.5, 1.5, 33, 7, false),

            homeDecoration("Kitchen Fridge", "kitchen_fridge", 1.0, 2.3, 6, 16, true),
            homeDecoration("Kitchen Counter Left", "kitchen_counter_left", 1.15, 1.1, 10, 16, true),
            homeDecoration("Kitchen Counter Right", "kitchen_counter_right", 1.15, 1.1, 12, 16, true),
            homeDecoration("Kitchen Stove", "kitchen_stove", 1.15, 1.1, 14, 16, true),
            homeInterior("Kitchen Low Cabinet", "if_kitchen_low_cabinet", 1.8, 0.85, 17, 16, true),
            homeDecoration("Kitchen Wall Sink", "kitchen_sink_wall", 0.8, 0.75, 20, 16, false),
            homeInterior("Kitchen Food Crate", "if_kitchen_food_crate", 0.75, 1.0, 7, 22, false),
            homeInterior("Kitchen Side Counter", "if_kitchen_side_counter", 0.85, 1.0, 8, 16, true),
            homeDecoration("Kitchen Rug", "carpet_green", 1.6, 1.25, 15, 21, false),
            homeInterior("Kitchen Plant", "if_plant_table", 0.75, 0.95, 20, 21, false),

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
            placeNPC(MapId.APARTMENT, 0, new StoryNPC(gp, StoryManager.SHADOW_APARTMENT, "Тень", "character:shadow"), 11, 12);
        }

        placeNPC(MapId.FOREST_DOUBTS, 0, new SwingChildNPC(gp, "Ребёнок"), 30, 8);
        placeNPC(MapId.FOREST_DOUBTS, 1, new StoryNPC(gp, StoryManager.SHADOW_FOREST, "Тень", "character:shadow"), 31, 21);

        placeNPC(MapId.VILLAGE, 0, new StoryNPC(gp, StoryManager.FRIEND, "Друг", "character:friend"), 13, 14);
        placeNPC(MapId.VILLAGE, 1, new StoryNPC(gp, StoryManager.ELDER, "Старик", "character:elder"), 36, 14);

        placeNPC(MapId.MOUNTAIN, 0, new StoryNPC(gp, StoryManager.WARRIOR, "Воин", "character:warrior"), 35, 29);
    }

    private void placeApartmentObjects() {
        int index = 0;

        StaticObject bed = new StaticObject(gp, "Bed", "/objects/home/bed", 2.5, 2.5, true);
        bed.setSolidArea(gp.tileSize * 3 / 4, gp.tileSize / 4,
                gp.tileSize + gp.tileSize / 12, gp.tileSize + gp.tileSize * 5 / 8);
        index = placeObject(MapId.APARTMENT, index, bed, 19, 8);

        StaticObject mirror = new StaticObject(gp, "Mirror",
                "/objects/home/mirrors/mirror_floor_wood_brown", 1.6, 2.4, true);
        mirror.setSolidArea(gp.tileSize / 3, gp.tileSize / 3, gp.tileSize, gp.tileSize * 13 / 8);
        index = placeObject(MapId.APARTMENT, index, mirror, 7, 8);

        StaticObject dresser = new StaticObject(gp, "Dresser", "/objects/home/dresser", 1.25, 1.25, true);
        index = placeObject(MapId.APARTMENT, index, dresser, 6, 13);

        StaticObject kitchenTable = new StaticObject(gp, "Kitchen Table",
                "/objects/home/interiors/if_dining_table_chairs", 2.2, 1.35, true);
        kitchenTable.setSolidArea(gp.tileSize / 8, gp.tileSize / 2,
                gp.tileSize * 2, gp.tileSize * 3 / 4);
        index = placeObject(MapId.APARTMENT, index, kitchenTable, 9, 21);

        StaticObject carpet = new StaticObject(gp, "Living Carpet", "/objects/home/carpet", 2.7, 2.0, false);
        index = placeObject(MapId.APARTMENT, index, carpet, 29, 10);

        StaticObject sofa = new StaticObject(gp, "Sofa", "/objects/home/interiors/if_living_sofa_gray",
                2.8, 1.75, true);
        sofa.setSolidArea(gp.tileSize / 5, gp.tileSize * 3 / 5,
                gp.tileSize * 2 + gp.tileSize / 3, gp.tileSize);
        index = placeObject(MapId.APARTMENT, index, sofa, 28, 12);

        StaticObject tv = new StaticObject(gp, "TV", "/objects/home/tv", 1.7, 1.0, true);
        tv.setSolidArea(gp.tileSize / 6, gp.tileSize / 2, gp.tileSize * 4 / 3, gp.tileSize / 2);
        index = placeObject(MapId.APARTMENT, index, tv, 34, 8);

        StaticObject window = new StaticObject(gp, "Window", "/objects/home/window", 1.5, 1.5, false);
        index = placeObject(MapId.APARTMENT, index, window, 33, 7);

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

    private void placeMountainObjects() {
        placeStaticObjects(MapId.MOUNTAIN, 0, MOUNTAIN_DECORATIONS);
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

    private int placeStaticObjects(int map, int index, StaticPlacement[] placements) {
        for (StaticPlacement placement : placements) {
            index = placeObject(map, index, createStaticObject(placement), placement.col, placement.row);
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
