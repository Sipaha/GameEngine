package ru.sipaha.engine.core;

import com.badlogic.gdx.Gdx;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.graphics.renderlayers.RenderLayer;
import ru.sipaha.engine.graphics.Renderer;
import ru.sipaha.engine.utils.Array;

public class Engine {
    public static final float FIXED_TIME = 0.02f;

    public final Input input = new Input();
    public final TagManager tagManager = new TagManager();
    public final Renderer renderer = new Renderer();
    public final Factory factory = new Factory(this);
    public final PhysicsWorld physicsWorld = new PhysicsWorld();

    private float timeCounter = 0f;

    private final Array<GameObject> gameObjects = new Array<>(false, 32, GameObject.class);

    private boolean physicsDebugDrawing = false;
    private boolean isRunning = false;

    public void addGameObject(GameObject go) {
        gameObjects.add(go);
        tagManager.add(go);
        if(go.canBeRendered()) renderer.addRenderUnit(go);
        go.start(this);
    }

    protected void removeGameObject(GameObject go) {
        gameObjects.removeValue(go, true);
        tagManager.remove(go);
        if(go.canBeRendered()) renderer.removeRenderUnit(go);
    }

    public void initialize() {
        if(isRunning) Gdx.app.error("GameEngine", "This engine is already initialized!");
        Gdx.input.setInputProcessor(input);
        renderer.initialize();
        factory.initialize();
        isRunning = true;
    }

    public void update(float delta) {
        if(!isRunning) throw new RuntimeException("Unable to update until the engine is not initialized!");

        renderer.render();

        float frameTime = Math.min(delta, 0.25f);

        timeCounter += frameTime;
        while(timeCounter >= FIXED_TIME) {
            for(GameObject g : gameObjects) g.updateData(frameTime);
            physicsWorld.update(FIXED_TIME);
            for (GameObject g : gameObjects) g.fixedUpdate(FIXED_TIME);
            timeCounter -= FIXED_TIME;
        }
        for(GameObject g : gameObjects) g.update(frameTime);
    }

    public void setPhysicsDebugDrawing(boolean physicsDebugDrawing) {
        boolean oldVal = this.physicsDebugDrawing;
        if(physicsDebugDrawing && !oldVal) {
            Camera camera = renderer.getRenderLayer().camera;
            RenderLayer layer = physicsWorld.getDebugRenderLayer();
            layer.setCamera(camera).initialize();
            renderer.addRenderLayer(layer);
        } else if(!physicsDebugDrawing && oldVal) {
            renderer.removeRenderLayer(physicsWorld.getDebugRenderLayer());
        }
        this.physicsDebugDrawing = physicsDebugDrawing;
    }

    public int gameObjectsCount() {
        return gameObjects.size;
    }
}
