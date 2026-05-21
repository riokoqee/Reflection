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

    public TileManager(GamePanel gp) {
        this.gp = gp;

        tile = new Tile[80];
        mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap("/maps/apartment.txt", MapId.APARTMENT);
        loadMap("/maps/forest_doubts.txt", MapId.FOREST_DOUBTS);
        loadMap("/maps/map02.txt", MapId.VILLAGE);
        loadMap("/maps/map03.txt", MapId.MOUNTAIN);
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
        int cameraX = gp.getCameraX();
        int cameraY = gp.getCameraY();
        int startCol = Math.max(0, cameraX / gp.tileSize);
        int startRow = Math.max(0, cameraY / gp.tileSize);
        int endCol = Math.min(gp.maxWorldCol - 1, (cameraX + gp.screenWidth) / gp.tileSize + 1);
        int endRow = Math.min(gp.maxWorldRow - 1, (cameraY + gp.screenHeight) / gp.tileSize + 1);

        for (int worldCol = startCol; worldCol <= endCol; worldCol++) {
            for (int worldRow = startRow; worldRow <= endRow; worldRow++) {
                int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];
                if (tileNum < 0 || tileNum >= tile.length || tile[tileNum] == null) {
                    continue;
                }

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;
                int screenX = worldX - cameraX;
                int screenY = worldY - cameraY;

                g2.drawImage(tile[tileNum].image, screenX, screenY, null);
            }
        }
    }
}
