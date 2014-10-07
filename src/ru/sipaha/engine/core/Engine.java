package ru.sipaha.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.graphics.renderlayers.Box2DDebugRenderLayer;
import ru.sipaha.engine.graphics.renderlayers.RenderLayer;
import ru.sipaha.engine.graphics.renderlayers.RenderLayers;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.GameObjectsArray;

public class Engine {
    public static final float FIXED_TIME = 0.02f;

    public final Input input = new Input();
    public final TagManager tagManager = new TagManager();
    public final ObjectMap<String, Replicator> replicatorsByName;
    public final IntMap<Replicator> replicatorsById;
    private final Array<Replicator> replicators = new Array<>(false, 4, Replicator.class);
    private Replicator cachedReplicator;
    private int cachedReplicatorId = -1;
    private String cachedReplicatorName = null;

    private float timeCounter = 0f;

    private final GameObjectsArray gameObjects;

    private RenderLayers renderLayers;

    private final PhysicsWorld physicsWorld = new PhysicsWorld();
    private boolean physicsDebugDrawing = false;

    private boolean isRunning = false;

    public Engine() {
        gameObjects = new GameObjectsArray(false, 128);
        replicatorsByName = new ObjectMap<>();
        replicatorsById = new IntMap<>();
        renderLayers = new RenderLayers();
    }

    public GameObject createGameObject(String name) {
        if(!name.equals(cachedReplicatorName)) cachedReplicator = replicatorsByName.get(name);
        cachedReplicatorName = name;
        cachedReplicatorId = -1;
        return cachedReplicator.get();
    }

    public GameObject createGameObject(int id) {
        if(id < 0) throw new RuntimeException("id can't be less than zero! id = " + id);
        if(id != cachedReplicatorId) cachedReplicator = replicatorsById.get(id);
        cachedReplicatorId = id;
        cachedReplicatorName = null;
        return cachedReplicator.get();
    }

    public Replicator setReplicator(GameObject go, String name) {
        if(isRunning) throw new RuntimeException("Unable to change the replicator while the engine is running!");
        Replicator replicator = replicatorsByName.get(name);
        if(replicator == null) {
            replicator = new Replicator(this, go);
            replicatorsByName.put(name, replicator);
            replicators.add(replicator);
        } else {
            replicator.setTemplate(go);
        }
        if(go.renderer != null) renderLayers.prepareBatchForGameObject(go);
        return replicator;
    }

    public Replicator setReplicator(GameObject go, int id) {
        if(isRunning) throw new RuntimeException("Unable to change the replicator while the engine is running!");
        Replicator replicator = replicatorsById.get(id);
        if(replicator == null) {
            replicator = new Replicator(this, go);
            replicatorsById.put(id, replicator);
            replicators.add(replicator);
        } else {
            replicator.setTemplate(go);
        }
        if(go.renderer != null) renderLayers.prepareBatchForGameObject(go);
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
            replicators.add(replicator);
        }
        return replicator;
    }

    public Replicator getReplicator(int id) {
        Replicator replicator = replicatorsById.get(id);
        if(replicator == null) {
            replicator = new Replicator(this);
            replicatorsById.put(id, replicator);
            replicators.add(replicator);
        }
        return replicator;
    }

    public void addGameObject(GameObject go) {
        gameObjects.add(go);
        tagManager.add(go);
        if(go.renderer != null) renderLayers.addGameObject(go);
        go.start(this);
    }

    public void removeGameObject(GameObject go) {
        gameObjects.removeValue(go, true);
        tagManager.remove(go);
        if(go.renderer != null) renderLayers.removeGameObject(go);
    }

    public void initialize() {
        if(isRunning) Gdx.app.error("GameEngine", "This engine is already initialized!");
        Gdx.input.setInputProcessor(input);
        renderLayers.initialize();
        for(Replicator r : replicators) r.initialize(this);
        isRunning = true;
    }

    public void update(float delta) {
        if(!isRunning) throw new RuntimeException("Unable to update until the engine is not initialized!");

        renderLayers.render();

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
        boolean oldVal = this.physicsDebugDrawing;
        if(physicsDebugDrawing && !oldVal) {
            renderLayers.addRenderLayer(new Box2DDebugRenderLayer(physicsWorld.getWorld(),
                                        renderLayers.getRenderLayer("Default").camera));
        } else if(!physicsDebugDrawing && oldVal) {
            renderLayers.removeRenderLayer(Box2DDebugRenderLayer.RENDER_LAYER_TAG);
        }
        this.physicsDebugDrawing = physicsDebugDrawing;
    }

    public int getGameObjectsCount() {
        return gameObjects.size;
    }

    public RenderLayer getRenderLayer(String name) {
        return renderLayers.getRenderLayer(name);
    }

    public RenderLayer getRenderLayer() {return renderLayers.getRenderLayer();}
}
