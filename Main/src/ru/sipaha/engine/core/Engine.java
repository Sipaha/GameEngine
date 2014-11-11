package ru.sipaha.engine.core;

import com.badlogic.gdx.Gdx;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.graphics.RenderUnit;
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

    private final Array<EngineUnit> units = new Array<>(false, 32, EngineUnit.class);
    private final Array<EngineUnit> unitsToAdd = new Array<>(false, 4, EngineUnit.class);
    private final Array<EngineUnit> unitsToDelete = new Array<>(false, 2, EngineUnit.class);

    private boolean physicsDebugDrawing = false;
    private boolean isRunning = false;
    private boolean inUpdateLoop = false;

    public void add(EngineUnit unit) {
        if(inUpdateLoop) {
            unitsToAdd.add(unit);
        } else {
            units.add(unit);
            tagManager.add(unit);
            if(unit instanceof RenderUnit) {
                renderer.addRenderUnit((RenderUnit)unit);
            }
            unit.start(this);
        }
    }

    protected void remove(EngineUnit unit) {
        if(inUpdateLoop) {
            unitsToDelete.add(unit);
        } else {
            units.removeValue(unit, true);
            tagManager.remove(unit);
            if(unit instanceof RenderUnit) {
                renderer.removeRenderUnit((RenderUnit) unit);
            }
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

        timeCounter += frameTime;
        inUpdateLoop = true;
        while(timeCounter >= FIXED_TIME) {
            physicsWorld.update(FIXED_TIME);
            for (EngineUnit g : units) {
                g.fixedUpdate(FIXED_TIME);
            }
            timeCounter -= FIXED_TIME;
        }
        for(EngineUnit g : units) {
            g.update(frameTime);
        }
        inUpdateLoop = false;

        for(EngineUnit unit : unitsToDelete) remove(unit);
        unitsToDelete.size = 0;
        for(EngineUnit unit : unitsToAdd) add(unit);
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

    public int unitsSize() {
        return units.size;
    }
}
