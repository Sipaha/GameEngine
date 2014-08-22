package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.graphics.SceneRenderer;
import ru.sipaha.engine.utils.GameObjectsArray;

public class Engine {
    public static final float FIXED_TIME = 0.02f;

    public TagManager tagManager = new TagManager();
    public ObjectMap<String, Replicator> replicatorsByName;
    public IntMap<Replicator> replicatorsById;

    private float timeCounter = 0f;

    private GameObjectsArray gameObjects;
    private SceneRenderer renderer;

    public Engine() {
        gameObjects = new GameObjectsArray(false, 128);
        replicatorsByName = new ObjectMap<>();
        replicatorsById = new IntMap<>();
        renderer = new SceneRenderer();
    }

    public GameObject createGameObject(String name) {
        return replicatorsByName.get(name).get();
    }

    public GameObject createGameObject(int id) {
        return replicatorsById.get(id).get();
    }

    public Replicator createReplicator(GameObject go, String name) {
        return createReplicator(go, name, -1);
    }

    public Replicator createReplicator(GameObject go, int id) {
        return createReplicator(go, null, id);
    }

    public Replicator createReplicator(GameObject go, String name, int id) {
        Replicator replicator = new Replicator(this, go);
        if(id >= 0) replicatorsById.put(id, replicator);
        if(name != null) replicatorsByName.put(name, replicator);
        renderer.prepareBatchForGameObject(go);
        return replicator;
    }

    public Replicator getReplicator(String name) {
        return replicatorsByName.get(name);
    }

    public Replicator getReplicator(int id) {
        return replicatorsById.get(id);
    }

    public void addGameObject(GameObject go) {
        gameObjects.add(go);
        tagManager.add(go);
        renderer.addGameObject(go);
        go.start(this);
    }

    public void removeGameObject(GameObject go) {
        gameObjects.removeValue(go, true);
        tagManager.remove(go);
        renderer.removeGameObject(go);
    }

    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    public void initialize() {
        renderer.rebuildBatchesArrays();
    }

    public void update(float delta) {
        for(GameObject g : gameObjects) {
            if(g.life.update(g, delta)) {
                g.updateData(delta);
            }
        }
        for(GameObject g : gameObjects) g.update(delta);
        timeCounter += delta;
        while(timeCounter >= FIXED_TIME) {
            for (GameObject g : gameObjects) g.fixedUpdate(FIXED_TIME);
            timeCounter -= FIXED_TIME;
        }
        renderer.render();
    }
}
