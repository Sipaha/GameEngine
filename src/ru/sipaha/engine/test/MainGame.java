package ru.sipaha.engine.test;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class MainGame extends Game {

    public static void main(String... args) {
        new LwjglApplication(new MainGame(), "Game", 480, 320);
    }

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
