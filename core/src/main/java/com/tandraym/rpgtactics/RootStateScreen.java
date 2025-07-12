package com.tandraym.rpgtactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class RootStateScreen implements Screen {

    private GameState state;
    private final MainGame game;

    public RootStateScreen(MainGame game, GameState initial) {
        this.game = game;
        this.state = initial;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        switch (state) {
            case BOOT:
                clear(0.1f, 0.1f, 0.1f);
                break;
            case EXPLORE:
                clear(0f, 0.2f, 0f);
                break;
            case BATTLE:
                clear(0.2f, 0f, 0f);
                break;
            case MENU:
                clear(0f, 0f, 0.2f);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            state = state.next(); // простой round‑robin, реализуйте сами
        }
    }

    @Override
    public void resize(int width, int height) {

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

    @Override
    public void dispose() {

    }

    private void clear(float r, float g, float b) {
        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    /* остальные методы Screen — пустые */
}
