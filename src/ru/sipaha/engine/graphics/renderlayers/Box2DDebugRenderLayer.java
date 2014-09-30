package ru.sipaha.engine.graphics.renderlayers;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import ru.sipaha.engine.core.PhysicsWorld;

/**
 * Created on 30.09.2014.
 */

public class Box2DDebugRenderLayer extends RenderLayer {

    public static final String RENDER_LAYER_TAG = "Box2DDebug";

    private final World world;
    private final Box2DDebugRenderer renderer;

    public Box2DDebugRenderLayer(World world) {
        super(RENDER_LAYER_TAG);
        this.world = world;
        renderer = new Box2DDebugRenderer();
        camera.zoom = PhysicsWorld.ENGINE_UNITS_TO_WORLD_UNITS;
        camera.setPosition(camera.zoom*camera.viewportWidth/2,camera.zoom*camera.viewportHeight/2);
    }

    @Override
    public void render() {
        camera.setToOrtho(false);
        renderer.render(world, camera.combined);
    }
}
