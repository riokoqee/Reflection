package reflection.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;

final class ReflectionGdxPrototype extends ApplicationAdapter {

    private static final float WORLD_WIDTH = 960f;
    private static final float WORLD_HEIGHT = 540f;

    private SpriteBatch batch;
    private FitViewport viewport;
    private Texture titleBackground;
    private Texture heroSheet;
    private TextureRegion heroIdleFrame;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        titleBackground = loadNearestTexture("ui/title_reflection_bg.png");
        heroSheet = loadNearestTexture("player/new/Amelia_idle_anim_16x16.png");
        heroIdleFrame = new TextureRegion(heroSheet, 0, 0, 16, 32);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(titleBackground, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(heroIdleFrame, WORLD_WIDTH * 0.5f - 32f, 132f, 64f, 128f);
        batch.end();
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (titleBackground != null) {
            titleBackground.dispose();
        }
        if (heroSheet != null) {
            heroSheet.dispose();
        }
    }

    private Texture loadNearestTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
    }
}
