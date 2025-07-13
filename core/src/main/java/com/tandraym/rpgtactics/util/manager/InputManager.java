package com.tandraym.rpgtactics.util.manager;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.tandraym.rpgtactics.util.support_enum.InputAction;

public class InputManager extends InputAdapter {

    // Карта действия → список клавиш
    private final ObjectMap<InputAction, int[]> keyBindings = new ObjectMap<>();

    // Карта: код клавиши → нажата ли она
    private final IntMap<Boolean> keyState = new IntMap<>();

    public InputManager() {
        // Привязка по умолчанию
        keyBindings.put(InputAction.MOVE_LEFT,  new int[]{Input.Keys.A, Input.Keys.LEFT});
        keyBindings.put(InputAction.MOVE_RIGHT, new int[]{Input.Keys.D, Input.Keys.RIGHT});
        keyBindings.put(InputAction.MOVE_UP,    new int[]{Input.Keys.W, Input.Keys.UP});
        keyBindings.put(InputAction.MOVE_DOWN,  new int[]{Input.Keys.S, Input.Keys.DOWN});
    }

    // Обновляется каждый кадр через Gdx.input.setInputProcessor(this)
    @Override
    public boolean keyDown(int keycode) {
        keyState.put(keycode, true);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        keyState.put(keycode, false);
        return false;
    }

    // Проверка, активно ли действие (нажата хотя бы одна привязанная клавиша)
    public boolean isActionPressed(InputAction action) {
        int[] keys = keyBindings.get(action);
        if (keys == null) return false;
        for (int key : keys) {
            if (Boolean.TRUE.equals(keyState.get(key))) return true;
        }
        return false;
    }
}
