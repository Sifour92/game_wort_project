package com.tandraym.rpgtactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
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
    private static final float TILE_SIZE = 1f;


    private float worldW, worldH;   // реальный размер мира
    private float targetX, targetY;


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

        // Размеры карты в тайлах
        MapProperties prop = map.getProperties();
        int tilesX = prop.get("width", Integer.class);   // из .tmx <map width="…">
        int tilesY = prop.get("height", Integer.class);

// Ширина/высота мира в «юнитах» (у нас 1 юнит = 1 тайл, потому что unitScale = 1/16f)
        worldW = tilesX * TILE_SIZE;    // TILE_SIZE = 1f
        worldH = tilesY * TILE_SIZE;

        targetX = camera.position.x = worldW / 2f;
        targetY = camera.position.y = worldH / 2f;

    }

    private void updateExplore(float delta) {

        handleInput(delta);
        lerpCamera();

        mapRenderer.setView(camera);     // ← используем новую матрицу
        mapRenderer.render();
    }

    private void handleInput(float delta) {
        // Шаг перемещения (скорость) зависит от времени кадра — это обеспечивает одинаковую скорость на любом FPS
        //💡 Смысл:
        //Ты хочешь, чтобы объект (в данном случае — камера или цель камеры) двигался со скоростью 10 тайлов в секунду,
        // НЕ зависимо от FPS. Именно это и делает умножение на delta.

        //⏱ Что такое delta?
        //delta — это значение, которое возвращает метод:
//        float delta = Gdx.graphics.getDeltaTime();
        float speed = 10f * delta;

        //🧠 ЧТО ТАКОЕ Gdx?
        //Gdx — это главный статический "мост" (Facade) ко всем основным подсистемам LibGDX.
        Input in = game.getGameServices().input();

        if (in.isKeyPressed(Input.Keys.A) || in.isKeyPressed(Input.Keys.LEFT)) targetX -= speed;
        if (in.isKeyPressed(Input.Keys.D) || in.isKeyPressed(Input.Keys.RIGHT)) targetX += speed;
        if (in.isKeyPressed(Input.Keys.W) || in.isKeyPressed(Input.Keys.UP)) targetY += speed;
        if (in.isKeyPressed(Input.Keys.S) || in.isKeyPressed(Input.Keys.DOWN)) targetY -= speed;

// Ширина* и высота* «видимой области» (в мировых единицах) ——
// это те самые значения, которые мы передавали при создании FitViewport,
// например 20 × 12 тайлов.
//
// getWorldWidth()/getWorldHeight() возвращают их текущие значения.
// Делим на 2, чтобы получить **«полу‑размеры»** камеры.
//
// *Почему «в мировых единицах», а не в пикселях?
//   Мы заранее решили, что 1 единица мира = 1 тайл (16 × 16 px),
//   поэтому камера размером 20 × 12 показывает ровно 20 × 12 тайлов.
//   Пиксели нас тут не интересуют.
        float halfW = viewport.getWorldWidth() / 2f;   // половина ширины камеры
        float halfH = viewport.getWorldHeight() / 2f;   // половина высоты камеры


        // Ограничиваем (clamp) координаты «центра камеры» так, чтобы
// она ни при каких обстоятельствах не вышла за пределы карты.

// 1)  targetX = MathUtils.clamp(
//         targetX,                // текущее желаемое X‑положение центра
//         halfW,                  // минимально допустимое: левый край = 0
//         worldW - halfW);        // максимально допустимое: правый край = worldW
//
// 2)  targetY = MathUtils.clamp(
//         targetY,                // текущее желаемое Y‑положение центра
//         halfH,                  // нижний край карты
//         worldH - halfH);        // верхний край карты

        //Что делает MathUtils.clamp(value, min, max)?
        //Возвращает value, ограничив его диапазоном [min, max]:
        //
        //Если value < min → вернёт min.
        //
        //Если value > max → вернёт max.
        //
        //Иначе вернёт сам value.
        targetX = MathUtils.clamp(targetX, halfW, worldW - halfW);
        targetY = MathUtils.clamp(targetY, halfH, worldH - halfH);

    }

    private void lerpCamera() {
        camera.position.x += (targetX - camera.position.x) * 0.12f; //0.12f — коэффициент сглаживания (12 % расстояния за кадр). Измените на вкус.
        camera.position.y += (targetY - camera.position.y) * 0.12f;
        camera.update();                 // ← сохраняем
    }

}
