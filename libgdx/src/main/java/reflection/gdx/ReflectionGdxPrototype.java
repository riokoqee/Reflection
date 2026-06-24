package reflection.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;

final class ReflectionGdxPrototype extends ApplicationAdapter {

    private static final float WORLD_WIDTH = 960f;
    private static final float WORLD_HEIGHT = 540f;
    private static final int TILE_SIZE = 48;
    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 32;
    private static final int FRAMES_PER_DIRECTION = 6;
    private static final int DIRECTION_RIGHT = 0;
    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_LEFT = 2;
    private static final int DIRECTION_DOWN = 3;
    private static final float PLAYER_DRAW_WIDTH = TILE_SIZE * 0.82f;
    private static final float PLAYER_DRAW_HEIGHT = TILE_SIZE * 1.64f;
    private static final float PLAYER_HITBOX_X = 12f;
    private static final float PLAYER_HITBOX_Y = 32f;
    private static final float PLAYER_HITBOX_WIDTH = 24f;
    private static final float PLAYER_HITBOX_HEIGHT = 12f;
    private static final float WALK_SPEED = 240f;
    private static final float SPRINT_SPEED = 420f;

    private SpriteBatch batch;
    private FitViewport viewport;
    private GdxTileCatalog tileCatalog;
    private GdxTextureStore textureStore;
    private GdxScene scene;
    private GdxMapData[] maps;
    private int currentMapIndex;
    private Texture heroIdleSheet;
    private Texture heroRunSheet;
    private TextureRegion[][] heroIdleFrames;
    private TextureRegion[][] heroRunFrames;
    private float playerX;
    private float playerY;
    private int direction = DIRECTION_DOWN;
    private float animationTime;
    private float titleUpdateTimer;
    private final Rectangle playerHitbox = new Rectangle();

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        tileCatalog = new GdxTileCatalog();
        textureStore = new GdxTextureStore();
        scene = GdxScene.create(textureStore);
        maps = new GdxMapData[] {
                GdxMapData.load("Apartment", "maps/apartment.txt", TILE_SIZE, 16, 12),
                GdxMapData.load("Forest of Doubts", "maps/forest_doubts.txt", TILE_SIZE, 23, 43),
                GdxMapData.load("Village", "maps/map02.txt", TILE_SIZE, 23, 23),
                GdxMapData.load("Mountain", "maps/map03.txt", TILE_SIZE, 24, 38),
                GdxMapData.load("Library", "maps/library.txt", TILE_SIZE, 24, 21)
        };
        heroIdleSheet = loadNearestTexture("player/new/Amelia_idle_anim_16x16.png");
        heroRunSheet = loadNearestTexture("player/new/Amelia_run_16x16.png");
        heroIdleFrames = sliceHeroSheet(heroIdleSheet);
        heroRunFrames = sliceHeroSheet(heroRunSheet);
        switchMap(0);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        updateCamera();
    }

    @Override
    public void render() {
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
        handleMapShortcuts();
        updatePlayer(delta);
        updateCamera();
        updateTitle(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply(false);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        drawVisibleTiles();
        scene.drawFloorObjects(batch, currentMapIndex, currentMap().pixelHeight(TILE_SIZE));
        scene.drawSortedActors(batch, currentMapIndex, currentMap().pixelHeight(TILE_SIZE), playerY,
                () -> drawPlayer(isMovingInputActive()));
        batch.end();
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (tileCatalog != null) {
            tileCatalog.dispose();
        }
        if (scene != null) {
            scene.dispose();
        }
        if (textureStore != null) {
            textureStore.dispose();
        }
        if (heroIdleSheet != null) {
            heroIdleSheet.dispose();
        }
        if (heroRunSheet != null) {
            heroRunSheet.dispose();
        }
    }

    private Texture loadNearestTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
    }

    private TextureRegion[][] sliceHeroSheet(Texture sheet) {
        TextureRegion[][] frames = new TextureRegion[4][FRAMES_PER_DIRECTION];
        for (int directionIndex = 0; directionIndex < frames.length; directionIndex++) {
            for (int frameIndex = 0; frameIndex < FRAMES_PER_DIRECTION; frameIndex++) {
                int sourceColumn = directionIndex * FRAMES_PER_DIRECTION + frameIndex;
                frames[directionIndex][frameIndex] = new TextureRegion(
                        sheet,
                        sourceColumn * FRAME_WIDTH,
                        0,
                        FRAME_WIDTH,
                        FRAME_HEIGHT
                );
            }
        }
        return frames;
    }

    private void handleMapShortcuts() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            switchMap(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            switchMap(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            switchMap(2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            switchMap(3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            switchMap(4);
        }
    }

    private void switchMap(int index) {
        currentMapIndex = MathUtils.clamp(index, 0, maps.length - 1);
        GdxMapData map = currentMap();
        playerX = map.startX;
        playerY = map.startY;
        animationTime = 0f;
        updateCamera();
    }

    private void updatePlayer(float delta) {
        float moveX = 0f;
        float moveY = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY += 1f;
        }

        if (moveX != 0f || moveY != 0f) {
            float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            moveX /= length;
            moveY /= length;
            if (Math.abs(moveX) > Math.abs(moveY)) {
                direction = moveX < 0f ? DIRECTION_LEFT : DIRECTION_RIGHT;
            } else {
                direction = moveY < 0f ? DIRECTION_UP : DIRECTION_DOWN;
            }

            float speed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? SPRINT_SPEED : WALK_SPEED;
            moveHorizontally(moveX * speed * delta);
            moveVertically(moveY * speed * delta);
        }
        animationTime += delta;
    }

    private boolean isMovingInputActive() {
        return Gdx.input.isKeyPressed(Input.Keys.A) ||
                Gdx.input.isKeyPressed(Input.Keys.D) ||
                Gdx.input.isKeyPressed(Input.Keys.W) ||
                Gdx.input.isKeyPressed(Input.Keys.S) ||
                Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
                Gdx.input.isKeyPressed(Input.Keys.UP) ||
                Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    private void moveHorizontally(float deltaX) {
        if (deltaX != 0f && !collides(playerX + deltaX, playerY)) {
            playerX += deltaX;
        }
    }

    private void moveVertically(float deltaY) {
        if (deltaY != 0f && !collides(playerX, playerY + deltaY)) {
            playerY += deltaY;
        }
    }

    private boolean collides(float nextX, float nextY) {
        GdxMapData map = currentMap();
        float left = nextX + PLAYER_HITBOX_X;
        float right = left + PLAYER_HITBOX_WIDTH;
        float top = nextY + PLAYER_HITBOX_Y;
        float bottom = top + PLAYER_HITBOX_HEIGHT;

        if (left < 0f || top < 0f || right >= map.pixelWidth(TILE_SIZE) || bottom >= map.pixelHeight(TILE_SIZE)) {
            return true;
        }

        playerHitbox.set(left, top, PLAYER_HITBOX_WIDTH, PLAYER_HITBOX_HEIGHT);
        if (scene.collides(currentMapIndex, playerHitbox)) {
            return true;
        }

        int leftColumn = (int) Math.floor(left / TILE_SIZE);
        int rightColumn = (int) Math.floor((right - 0.01f) / TILE_SIZE);
        int topRow = (int) Math.floor(top / TILE_SIZE);
        int bottomRow = (int) Math.floor((bottom - 0.01f) / TILE_SIZE);

        for (int row = topRow; row <= bottomRow; row++) {
            for (int column = leftColumn; column <= rightColumn; column++) {
                if (tileCatalog.isBlocked(map.tileAt(column, row))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateCamera() {
        if (viewport == null || maps == null) {
            return;
        }
        GdxMapData map = currentMap();
        Camera camera = viewport.getCamera();
        float mapWidth = map.pixelWidth(TILE_SIZE);
        float mapHeight = map.pixelHeight(TILE_SIZE);
        float halfWidth = viewport.getWorldWidth() * 0.5f;
        float halfHeight = viewport.getWorldHeight() * 0.5f;
        float targetX = playerX + TILE_SIZE * 0.5f;
        float targetY = mapHeight - playerY - TILE_SIZE * 0.5f;

        if (mapWidth <= viewport.getWorldWidth()) {
            targetX = mapWidth * 0.5f;
        } else {
            targetX = MathUtils.clamp(targetX, halfWidth, mapWidth - halfWidth);
        }

        if (mapHeight <= viewport.getWorldHeight()) {
            targetY = mapHeight * 0.5f;
        } else {
            targetY = MathUtils.clamp(targetY, halfHeight, mapHeight - halfHeight);
        }

        camera.position.set(targetX, targetY, 0f);
        camera.update();
    }

    private void drawVisibleTiles() {
        GdxMapData map = currentMap();
        Camera camera = viewport.getCamera();
        float mapHeight = map.pixelHeight(TILE_SIZE);
        float left = camera.position.x - viewport.getWorldWidth() * 0.5f;
        float right = camera.position.x + viewport.getWorldWidth() * 0.5f;
        float bottom = camera.position.y - viewport.getWorldHeight() * 0.5f;
        float top = camera.position.y + viewport.getWorldHeight() * 0.5f;

        int firstColumn = Math.max(0, (int) Math.floor(left / TILE_SIZE) - 1);
        int lastColumn = Math.min(map.columns - 1, (int) Math.floor(right / TILE_SIZE) + 1);
        int firstRow = Math.max(0, (int) Math.floor((mapHeight - top) / TILE_SIZE) - 1);
        int lastRow = Math.min(map.rows - 1, (int) Math.floor((mapHeight - bottom) / TILE_SIZE) + 1);

        for (int row = firstRow; row <= lastRow; row++) {
            float drawY = mapHeight - (row + 1) * TILE_SIZE;
            for (int column = firstColumn; column <= lastColumn; column++) {
                GdxTileCatalog.TileDef tile = tileCatalog.get(map.tileAt(column, row));
                if (tile != null) {
                    batch.draw(tile.texture, column * TILE_SIZE, drawY, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    private void drawPlayer(boolean moving) {
        TextureRegion[][] frames = moving ? heroRunFrames : heroIdleFrames;
        float frameDuration = moving ? 0.09f : 0.18f;
        int frameIndex = ((int) (animationTime / frameDuration)) % FRAMES_PER_DIRECTION;
        TextureRegion frame = frames[direction][frameIndex];
        GdxMapData map = currentMap();
        float drawX = playerX - (PLAYER_DRAW_WIDTH - TILE_SIZE) * 0.5f;
        float drawY = map.pixelHeight(TILE_SIZE) - playerY - TILE_SIZE;
        batch.draw(frame, drawX, drawY, PLAYER_DRAW_WIDTH, PLAYER_DRAW_HEIGHT);
    }

    private void updateTitle(float delta) {
        titleUpdateTimer += delta;
        if (titleUpdateTimer >= 0.5f) {
            titleUpdateTimer = 0f;
            Gdx.graphics.setTitle("Reflection LibGDX - " + currentMap().name + " - " +
                    Gdx.graphics.getFramesPerSecond() + " FPS");
        }
    }

    private GdxMapData currentMap() {
        return maps[currentMapIndex];
    }
}
