package com.tandraym.rpgtactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** Игровой персонаж, живущий на клеточной карте. */
public class Player {

    /* --- публичное API (логические координаты в тайлах) ---------------- */
    public int x, y;           // текущая клетка
    public int dir = 0;        // 0=down,1=left,2=right,3=up  (просто int)

    /* --- визуальное сглаживание ---------------------------------------- */
    public float visX, visY;   // плавают к x,y
    private static final float LERP = 10f;  // скорость подъезда

    /* --- анимации ------------------------------------------------------- */
//    private final Animation<TextureRegion>[] walk = new Animation[4];
    private final TextureRegion[] walkFrames = new TextureRegion[4]; // 0 = down, ...
    private final TextureRegion idle;        // кадр стоя
    private float stateTime = 0f;

    public Player(TextureAtlas atlas, int startX, int startY) {

        this.x = startX; this.y = startY;
        this.visX = startX; this.visY = startY;

        // idle
        idle = atlas.findRegion("hero/hero_idle");
        // walk‑кадры (имена: hero_walk_down, hero_walk_left, …)
        walkFrames[0] = atlas.findRegion("hero/hero_walk_down");
        walkFrames[1] = atlas.findRegion("hero/hero_walk_left");
        walkFrames[2] = atlas.findRegion("hero/hero_walk_right");
        walkFrames[3] = atlas.findRegion("hero/hero_walk_up");
//        walk[0] = makeLoop(atlas, "hero/hero_walk_down");
//        walk[1] = makeLoop(atlas, "hero/hero_walk_left");
//        walk[2] = makeLoop(atlas, "hero/hero_walk_right");
//        walk[3] = makeLoop(atlas, "hero/hero_walk_up");

        String[] keys = {
            "hero/hero_idle",
            "hero/hero_walk_down",
            "hero/hero_walk_left",
            "hero/hero_walk_right",
            "hero/hero_walk_up"
        };
        for (String k : keys) {
            Gdx.app.log("ATLAS", k + " -> " + atlas.findRegion(k));
        }

        for (int i = 0; i < walkFrames.length; i++) {
            Gdx.app.log("DEBUG", "walkFrames[" + i + "] = " + walkFrames[i]);
        }
    }

    /** Создаёт циклическую анимацию из регионов, начинающихся с prefix. */
//    private static Animation<TextureRegion> makeLoop(TextureAtlas atlas, String prefix) {
//        Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(prefix);
//        return new Animation<>(0.12f, frames, Animation.PlayMode.LOOP);
//    }
    private Animation<TextureRegion> makeLoop(TextureAtlas atlas, String regionName) {
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(regionName);
        Gdx.app.log("ATLAS_FRAMES", regionName + " -> count: " + regions.size);
        if (regions.size == 0) {
            throw new GdxRuntimeException("No regions found for: " + regionName);
        }
        return new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
    }

    /* ------------------------------------------------------------------- */
    /** Вызываем каждый кадр из RootStateScreen.updateExplore(dt). */
    public void update(float dt) {
        stateTime += dt;
        visX += (x - visX) * LERP * dt;
        visY += (y - visY) * LERP * dt;
    }

    /** Рендер персонажа в переданный SpriteBatch (batch.begin() уже должен быть!). */
    public void render(SpriteBatch batch) {
        Gdx.app.log("DEBUG", "Rendering dir=" + dir + " frame=" + walkFrames[dir]);

        TextureRegion frame = walkFrames[dir];
        batch.draw(frame, visX, visY, 1, 1);
    }
}
