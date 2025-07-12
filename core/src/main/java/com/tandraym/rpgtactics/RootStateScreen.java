package com.tandraym.rpgtactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class RootStateScreen implements Screen {

    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();   // системный шрифт LibGDX
    private OrthographicCamera camera;
    private FitViewport viewport;
    private OrthogonalTiledMapRenderer mapRenderer;
    private boolean mapInit = false;                      // чтобы один раз инициализировать карту
    private GameState state;
    private final MainGame game;

    private static final float VIEW_W = 20f;   // ширина в тайлах
    private static final float VIEW_H = 12f;   // высота

    //float aspect = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
    //float VIEW_W = 20f;
    //float VIEW_H = VIEW_W / aspect;


    public RootStateScreen(MainGame game) {
        this.game = game;
        this.state = GameState.BOOT;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //    camera.update();
        //    batch.setProjectionMatrix(camera.combined);
        //    batch.begin();
        //    // draw stuff
        //    batch.end();

        ScreenUtils.clear(0.12f, 0.12f, 0.15f, 1f);

        /* ---- BOOT ------------------------------------------------------ */
        if (state == GameState.BOOT) {
            AssetManager am = game.getGameServices().assets();

            if (am.update()) {
                Gdx.app.log("EXPLORE", "rendering explore");
                state = GameState.EXPLORE;
            }

            float pct = am.getProgress() * 100f;
            batch.begin();
            font.draw(batch, "Loading " + (int) (pct) + "%", 20, 40);
            batch.end();
            return;
        }

        /* ---- EXPLORE (init once) --------------------------------------- */
        if (!mapInit) {
            initExplore();
            mapInit = true;
        }
        updateExplore(delta);
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    /* ------------------------------------------------------------------- */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }

    private void clear(float r, float g, float b) {
        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void initExplore() {
        TiledMap map = game.getGameServices().assets().get("maps/home_map.tmx", TiledMap.class);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / 16f);
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEW_W, VIEW_H, camera);

        viewport.apply();
        camera.position.set(10, 6, 0); // центр камеры (половина от 20x12)
        camera.update();

    }

    private void updateExplore(float delta) {
        float speed = 5f * delta;
        Input in = game.getGameServices().input();
        if (in.isKeyPressed(Input.Keys.A)) camera.position.x -= speed;
        if (in.isKeyPressed(Input.Keys.D)) camera.position.x += speed;
        if (in.isKeyPressed(Input.Keys.W)) camera.position.y += speed;
        if (in.isKeyPressed(Input.Keys.S)) camera.position.y -= speed;

        camera.update();                 // ← сохраняем
        mapRenderer.setView(camera);     // ← используем новую матрицу
        mapRenderer.render();
    }
}
