package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.Array;
import ru.sipaha.engine.graphics.SceneRenderer;

public class Engine {
    public static final float FIXED_TIME = 0.02f;

    public TagManager tagManager = new TagManager();


    private GOFactory goFactory = new GOFactory();

    private float counter = 0f;

    private Array<GameObject> gameObjects;
    private SceneRenderer renderer;

    public Engine() {
        gameObjects = new Array<>(false, 128, GameObject.class);
        renderer = new SceneRenderer();
    }

    public GameObject createGameObject(String name) {
        GameObject go = goFactory.createGameObject(name);
        addGameObject(go);
        return go;
    }

    public GameObject createGameObject(int id) {
        GameObject go = goFactory.createGameObject(id);
        addGameObject(go);
        return go;
    }

    private void addGameObject(GameObject go) {
        gameObjects.add(go);
        tagManager.add(go);
        renderer.addGO(go);
    }

    public void removeGO(GameObject go) {
        gameObjects.removeValue(go, true);
        tagManager.remove(go);
        renderer.removeGO(go);
    }

    public void update(float delta) {
        GameObject[] g = gameObjects.items;
        int count = gameObjects.size;
        for(int i = 0; i < count; i++) {
            GameObject go = g[i];
            if(go.enable) go.updateData(delta);
        }
        for(int i = 0; i < count; i++) {
            GameObject go = g[i];
            if(go.enable) go.update(delta);
        }
        counter += delta;
        while(counter >= FIXED_TIME) {
            for(int i = 0; i < count; i++) {
                GameObject go = g[i];
                if(go.enable) go.fixedUpdate(FIXED_TIME);
            }
            counter -= FIXED_TIME;
        }

        renderer.render();
    }
}
