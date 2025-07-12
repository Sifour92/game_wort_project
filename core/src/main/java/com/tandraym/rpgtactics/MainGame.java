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
        services.loadBootAssets();
        setScreen(new RootStateScreen(this));
    }

    public Engine getEngine()   {
        return engine;
    }
    public GameServices sv()    {
        return services;
    }

    @Override
    public void dispose() {
        // освобождаем ассеты, чтобы не текла память при перезапуске SuperDev
        services.assets().dispose();
        super.dispose();// диспоуз текущего экрана
    }

    GameServices getGameServices() {
        return services;
    }
}
