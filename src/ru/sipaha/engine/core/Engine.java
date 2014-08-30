package ru.sipaha.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.graphics.SceneRenderer;
import ru.sipaha.engine.utils.GameObjectsArray;

public class Engine {
    public static final float FIXED_TIME = 0.02f;

    public final TagManager tagManager = new TagManager();
    public final ObjectMap<String, Replicator> replicatorsByName;
    public final IntMap<Replicator> replicatorsById;

    private float timeCounter = 0f;

    private final GameObjectsArray gameObjects;
    private final SceneRenderer renderer;
    private final PhysicsWorld physicsWorld = new PhysicsWorld();
    private boolean physicsDebugDrawing = false;

    private boolean isRunning = false;

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

    public Replicator setReplicator(GameObject go, String name) {
        if(isRunning) throw new RuntimeException("Unable to change the replicator while the engine is running!");
        Replicator replicator = replicatorsByName.get(name);
        if(replicator == null) {
            replicator = new Replicator(this, go);
            replicatorsByName.put(name, replicator);
        } else {
            replicator.setTemplate(go);
        }
        renderer.prepareBatchForGameObject(go);
        return replicator;
    }

    public Replicator setReplicator(GameObject go, int id) {
        if(isRunning) throw new RuntimeException("Unable to change the replicator while the engine is running!");
        Replicator replicator = replicatorsById.get(id);
        if(replicator == null) {
            replicator = new Replicator(this, go);
            replicatorsById.put(id, replicator);
        } else {
            replicator.setTemplate(go);
        }
        renderer.prepareBatchForGameObject(go);
        return replicator;
    }

    public Replicator setReplicator(GameObject go, String name, int id) {
        setReplicator(go, id);
        return setReplicator(go, name);
    }

    public Replicator getReplicator(String name) {
        Replicator replicator = replicatorsByName.get(name);
        if(replicator == null) {
            replicator = new Replicator(this);
            replicatorsByName.put(name, replicator);
        }
        return replicator;
    }

    public Replicator getReplicator(int id) {
        Replicator replicator = replicatorsById.get(id);
        if(replicator == null) {
            replicator = new Replicator(this);
            replicatorsById.put(id, replicator);
        }
        return replicator;
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
        if(isRunning) Gdx.app.error("GameEngine", "This engine is already initialized!");
        renderer.rebuildBatchesArrays();
        isRunning = true;
    }

    public void update(float delta) {
        if(!isRunning) throw new RuntimeException("Unable to update until the engine is not initialized!");

        renderer.render();
        if(physicsDebugDrawing) {
            physicsWorld.debugRender(renderer.getCamera());
        }

        float frameTime = Math.min(delta, 0.25f);
        timeCounter += frameTime;
        while(timeCounter >= FIXED_TIME) {
            physicsWorld.update(FIXED_TIME);
            for(GameObject g : gameObjects) {
                if(g.life.update(g, FIXED_TIME)) {
                    g.updateData(FIXED_TIME);
                }
            }
            for (GameObject g : gameObjects) g.fixedUpdate(FIXED_TIME);
            timeCounter -= FIXED_TIME;
        }
        for(GameObject g : gameObjects) g.update(frameTime);
    }

    public PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    public void setPhysicsDebugDrawing(boolean physicsDebugDrawing) {
        this.physicsDebugDrawing = physicsDebugDrawing;
    }

    public int getGameObjectsCount() {
        return gameObjects.size;
    }

    public SceneRenderer getSceneRenderer() {
        return renderer;
    }
}
