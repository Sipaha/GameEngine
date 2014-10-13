package ru.sipaha.engine.graphics.renderlayers;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import ru.sipaha.engine.core.PhysicsWorld;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.utils.signals.Listener;

/**
 * Created on 30.09.2014.
 */

public class Box2DDebugRenderLayer extends RenderLayer {

    public static final String RENDER_LAYER_TAG = "Box2DDebug";

    public final Matrix4 cameraMatrix = new Matrix4();
    private final World world;
    private final Box2DDebugRenderer renderer;

    public Box2DDebugRenderLayer(World world, Camera worldCamera) {
        super(RENDER_LAYER_TAG, worldCamera);
        this.world = world;
        renderer = new Box2DDebugRenderer();
    }

    @Override
    public void initialize() {
        camera.addOnUpdateListener(new Listener<Camera>() {
            @Override
            public void receive(Camera object) {
                cameraMatrix.set(camera.combined);
                cameraMatrix.scl(PhysicsWorld.WORLD_UNITS_TO_ENGINE_UNITS);
            }
        });
    }

    @Override
    public void render() {
        camera.setToOrtho(false);
        renderer.render(world, cameraMatrix);
    }

    @Override
    public void resize(int width, int height) {}
}
