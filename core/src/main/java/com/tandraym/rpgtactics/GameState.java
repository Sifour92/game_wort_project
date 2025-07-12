package com.tandraym.rpgtactics;

/**
 * Перечисление экранных/игровых режимов.
 * <pre>
 *  BOOT     – загрузка ассетов, показ логотипа
 *  EXPLORE  – свободное перемещение по карте‑миру
 *  BATTLE   – тактический бой на отдельной арене
 *  MENU     – главное меню / пауза
 * </pre>
 */
public enum GameState {

    BOOT,
    EXPLORE,
    BATTLE,
    MENU;

    /** Круговой переход к следующему состоянию (удобно для тестов). */
    public GameState next() {
        int nextOrdinal = (ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }

    /** Можно добавить метод previous(), если понадобится. */
    public GameState previous() {
        int prevOrdinal = (ordinal() - 1 + values().length) % values().length;
        return values()[prevOrdinal];
    }
}
