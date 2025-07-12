package com.tandraym.rpgtactics.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

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

    /** Ассеты, которые нужны сразу при старте игры (экран Boot). */
    public void loadBootAssets() {
        // 1 Получаем общий AssetManager (он уже зарегистрирован в конструкторе)
        AssetManager am = assets();

        // 2 Говорим менеджеру, каким лоадером читать файлы .tmx
        //  — без этого попытка am.load("…tmx", TiledMap.class) бросит UnknownLoaderException
        am.setLoader(TiledMap.class, new TmxMapLoader());

        // 3 Ставим файлы в очередь на асинхронную загрузку
        // Пути записываем ОТНОСИТЕЛЬНО папки assets/ (LibGDX сам добавит prefix).
        am.load("maps/home_map.tmx", TiledMap.class);       // карта из Tiled
        am.load("atlases/tiles.atlas", TextureAtlas.class); // atlas, собранный TexturePacker’ом
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
