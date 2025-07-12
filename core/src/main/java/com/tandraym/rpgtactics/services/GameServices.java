package com.tandraym.rpgtactics.services;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;

/**
 * Централизованный доступ к подсистемам игры.
 *
 * Главное правило: любая игровая логика получает объекты **через** этот
 * интерфейс, а не обращается к статическим Gdx.*‑полям напрямую.
 */
public interface GameServices {

    /** Менеджер ассетов (текстуры, звуки, шрифты). */
    AssetManager assets();

    void loadBootAssets();

    /** Абстракция ввода (клавиатура, мышь, тач). */
    Input input();

    /* --- универсальные методы для расширения локатора --- */

    /** Зарегистрировать любой дополнительный сервис. */
    <T> void register(Class<T> type, T instance);

    /** Достать сервис по классу (generic‑safe cast). */
    <T> T get(Class<T> type);
}
