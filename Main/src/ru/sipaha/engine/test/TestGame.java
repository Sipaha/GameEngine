package ru.sipaha.engine.test;

import com.badlogic.gdx.Game;

/**
 * Created on 04.11.2014.
 */

public class TestGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
