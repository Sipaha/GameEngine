package ru.frozen.prolen.core;

import com.badlogic.gdx.utils.Array;
import ru.frozen.prolen.core.gameobject.GameObject;
import ru.frozen.prolen.core.graphics.SceneRenderer;

public class Engine {
    public static final float FIXED_TIME = 0.02f;
    private float counter = 0f;

    private Array<GameObject> gameObjects;
    private SceneRenderer renderer;

    public Engine() {
        gameObjects = new Array<>(false, 128, GameObject.class);
        renderer = new SceneRenderer();
    }

    public void update(float delta) {
        GameObject[] g = gameObjects.items;
        int count = gameObjects.size;
        for(int i = 0; i < count; i++) {
            g[i].updateData(delta);
        }
        for(int i = 0; i < count; i++) {
            g[i].update(delta);
        }
        counter += delta;
        while(counter >= FIXED_TIME) {
            for(int i = 0; i < count; i++) {
                g[i].fixedUpdate(FIXED_TIME);
            }
            counter -= FIXED_TIME;
        }

        renderer.render();
    }
}
