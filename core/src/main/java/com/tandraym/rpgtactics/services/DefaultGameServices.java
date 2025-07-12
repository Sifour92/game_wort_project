package com.tandraym.rpgtactics.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;

import java.util.HashMap;
import java.util.Map;

/** Базовая реализация локатора: всё хранится в HashMap<Class, Object>. */
public final class DefaultGameServices implements GameServices {

    private final Map<Class<?>, Object> services = new HashMap<>();

    public DefaultGameServices() {
        // ⚠️  Здесь есть ОДНО обращение к Gdx.* — мы изолируем его внутри локатора.
        services.put(AssetManager.class, new AssetManager());
        services.put(Input.class, Gdx.input);
    }

    /* --- доступ к часто‑используемым подсистемам --- */

    @Override
    public AssetManager assets() {
        return (AssetManager) services.get(AssetManager.class);
    }

    @Override
    public Input input() {
        return (Input) services.get(Input.class);
    }

    /* --- универсальные методы --- */

    @Override
    public <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        return (T) services.get(type);
    }
}
