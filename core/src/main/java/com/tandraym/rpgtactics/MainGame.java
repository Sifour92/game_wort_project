package com.tandraym.rpgtactics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.tandraym.rpgtactics.services.DefaultGameServices;
import com.tandraym.rpgtactics.services.GameServices;

public class MainGame extends Game {

    private final Engine engine = new Engine();
    private final GameServices services = new DefaultGameServices();

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        setScreen(new RootStateScreen(this, GameState.BOOT));
    }

    public Engine getEngine()   {
        return engine;
    }
    public GameServices sv()    {
        return services;
    }
}
