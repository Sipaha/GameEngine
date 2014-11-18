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
    private final Array<GameObject> unitsToAdd = new Array<>(false, 4, GameObject.class);
    private final Array<GameObject> unitsToDelete = new Array<>(false, 2, GameObject.class);

    private boolean physicsDebugDrawing = false;
    private boolean isRunning = false;
    private boolean inUpdateLoop = false;
    private boolean editorMode = false;

    public void add(GameObject gameObject) {
        if(inUpdateLoop) {
            unitsToAdd.add(gameObject);
        } else {
            gameObjects.add(gameObject);
            tagManager.add(gameObject);
            renderer.addRenderUnit(gameObject);
            gameObject.start(this);
        }
    }

    protected void remove(GameObject gameObject) {
        if(inUpdateLoop) {
            unitsToDelete.add(gameObject);
        } else {
            gameObjects.removeValue(gameObject, true);
            tagManager.remove(gameObject);
            renderer.removeRenderUnit(gameObject);
        }
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

        inUpdateLoop = true;
        if(editorMode) {
            for (GameObject g : gameObjects) {
                g.updateData(FIXED_TIME);
            }
        } else {
            timeCounter += frameTime;
            while(timeCounter >= FIXED_TIME) {
                physicsWorld.update(FIXED_TIME);
                for (GameObject g : gameObjects) {
                    g.updateData(FIXED_TIME);
                }
                for (GameObject g : gameObjects) {
                    g.fixedUpdate(FIXED_TIME);
                }
                timeCounter -= FIXED_TIME;
            }
            for(GameObject g : gameObjects) {
                g.update(frameTime);
            }
        }
        inUpdateLoop = false;

        for(GameObject gameObject : unitsToDelete) remove(gameObject);
        unitsToDelete.size = 0;
        for(GameObject gameObject : unitsToAdd) add(gameObject);
        unitsToAdd.size = 0;
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

    public void setEditorMode(boolean mode) {
        editorMode = mode;
    }

    public int unitsSize() {
        return gameObjects.size;
    }
}
