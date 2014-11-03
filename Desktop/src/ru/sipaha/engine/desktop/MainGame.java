package ru.sipaha.engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.sipaha.engine.test.TestGame;

public class MainGame {
    public static void main(String... args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Game";
        config.width = 1280;
        config.height = 720;
        config.vSyncEnabled = false;
        new LwjglApplication(new TestGame(), config);
    }
}
