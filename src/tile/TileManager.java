package tile;

import main.GamePanel;
import main.MapId;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class TileManager {
    private final GamePanel gp;
    public Tile[] tile;
    public int[][][] mapTileNum;
    private BufferedImage[] cachedMapImages;

    public TileManager(GamePanel gp) {
        this.gp = gp;

        tile = new Tile[80];
        mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
        cachedMapImages = new BufferedImage[gp.maxMap];

        getTileImage();
        loadMap("/maps/apartment.txt", MapId.APARTMENT);
        loadMap("/maps/forest_doubts.txt", MapId.FOREST_DOUBTS);
        loadMap("/maps/map02.txt", MapId.VILLAGE);
        loadMap("/maps/map03.txt", MapId.MOUNTAIN);
        loadMap("/maps/library.txt", MapId.LIBRARY);
    }

    private void getTileImage() {
        setup(0, "grass00", false);
        setup(1, "grass00", true);
        setup(2, "grass00", true);
        setup(3, "grass00", false);
        setup(4, "grass00", true);
        setup(5, "grass00", false);
        setup(6, "grass00", false);
        setup(7, "grass00", false);
        setup(8, "grass00", false);
        setup(9, "grass00", false);
        setup(10, "grass00", false);
        setup(11, "grass01", false);
        setup(12, "water00", true);
        setup(13, "water01", true);
        setup(14, "water02", true);
        setup(15, "water03", true);
        setup(16, "water04", true);
        setup(17, "water05", true);
        setup(18, "water06", true);
        setup(19, "water07", true);
        setup(20, "water08", true);
        setup(21, "water09", true);
        setup(22, "water10", true);
        setup(23, "water11", true);
        setup(24, "water12", true);
        setup(25, "water13", true);
        setup(26, "road00", false);
        setup(27, "road01", false);
        setup(28, "road02", false);
        setup(29, "road03", false);
        setup(30, "road04", false);
        setup(31, "road05", false);
        setup(32, "road06", false);
        setup(33, "road07", false);
        setup(34, "road08", false);
        setup(35, "road09", false);
        setup(36, "road10", false);
        setup(37, "road11", false);
        setup(38, "road12", false);
        setup(39, "earth", false);
        setup(40, "wall", true);
        setup(41, "tree", true);
        setup(42, "hut", false);
        setup(43, "floor01", false);
        setup(44, "table01", true);
        setup(45, "home_generated/void", true);
        setup(46, "home_generated/floor_wood", false);
        setup(47, "home_generated/wall_white", true);
        setup(48, "home_generated/wall_top", true);
        setup(49, "home_generated/floor_dark", false);
        setup(50, "forest_generated/ground", false);
        setup(51, "forest_generated/path", false);
        setup(52, "forest_generated/edge", true);
        setup(53, "forest_generated/flowers", false);
        setup(54, "village_stone_road", false);
        setup(55, "village_stone_road_alt", false);
        setup(56, "village_stone_road_dark", false);
        setup(57, "mountain_void", true);
        setup(58, "mountain_ground", false);
        setup(59, "mountain_path", false);
        setup(60, "mountain_cliff", true);
        setup(61, "mountain_snow", false);
        setup(62, "mountain_rock", true);
        setup(63, "mountain_stairs", false);
    }
    private void setup(int index, String imageName, boolean collision) {
        try {
            tile[index] = new Tile();
            BufferedImage image = ImageIO.read(getClass().getResource("/tiles/" + imageName + ".png"));
            tile[index].image = new UtilityTool().scaleImage(image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;
        }
        catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot load tile image: " + imageName, e);
        }
    }

    private void loadMap(String filePath, int map) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                throw new FileNotFoundException(filePath);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                int col = 0;
                int row = 0;

                while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                    String line = br.readLine();
                    if (line == null) {
                        throw new IOException("Map has fewer rows than expected: " + filePath);
                    }
                    String[] numbers = line.trim().split("\\s+");
                    if (numbers.length < gp.maxWorldCol) {
                        throw new IOException("Map row has fewer columns than expected: " + filePath + " row " + row);
                    }

                    while (col < gp.maxWorldCol) {
                        mapTileNum[map][col][row] = Integer.parseInt(numbers[col]);
                        col++;
                    }
                    if (col == gp.maxWorldCol) {
                        col = 0;
                        row++;
                    }
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("Cannot load map: " + filePath, e);
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage mapImage = getCachedMapImage(gp.currentMap);
        int cameraX = gp.getCameraX();
        int cameraY = gp.getCameraY();

        int srcX1 = Math.max(0, cameraX);
        int srcY1 = Math.max(0, cameraY);
        int srcX2 = Math.min(mapImage.getWidth(), cameraX + gp.screenWidth);
        int srcY2 = Math.min(mapImage.getHeight(), cameraY + gp.screenHeight);
        if (srcX2 <= srcX1 || srcY2 <= srcY1) {
            return;
        }

        int dstX1 = srcX1 - cameraX;
        int dstY1 = srcY1 - cameraY;
        int dstX2 = dstX1 + (srcX2 - srcX1);
        int dstY2 = dstY1 + (srcY2 - srcY1);
        g2.drawImage(mapImage, dstX1, dstY1, dstX2, dstY2, srcX1, srcY1, srcX2, srcY2, null);
    }

    private BufferedImage getCachedMapImage(int map) {
        if (cachedMapImages[map] == null) {
            cachedMapImages[map] = buildMapImage(map);
        }
        return cachedMapImages[map];
    }

    private BufferedImage buildMapImage(int map) {
        int width = gp.maxWorldCol * gp.tileSize;
        int height = gp.maxWorldRow * gp.tileSize;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D mapGraphics = image.createGraphics();
        mapGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        mapGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        mapGraphics.setColor(Color.black);
        mapGraphics.fillRect(0, 0, width, height);

        for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {
            for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
                int tileNum = mapTileNum[map][worldCol][worldRow];
                if (tileNum < 0 || tileNum >= tile.length || tile[tileNum] == null) {
                    continue;
                }

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;
                mapGraphics.drawImage(tile[tileNum].image, worldX, worldY, null);
            }
        }
        mapGraphics.dispose();
        return image;
    }
}
