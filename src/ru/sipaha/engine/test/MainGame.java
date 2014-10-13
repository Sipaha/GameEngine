package ru.sipaha.engine.test;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class MainGame extends Game {

    public static void main(String... args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Game";
        config.width = 1280;
        config.height = 720;
        config.vSyncEnabled = false;
        new LwjglApplication(new MainGame(), config);
    }

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
