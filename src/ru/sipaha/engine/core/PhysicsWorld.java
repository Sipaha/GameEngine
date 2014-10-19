package ru.sipaha.engine.core;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.graphics.renderlayers.RenderLayer;
import ru.sipaha.engine.utils.signals.Listener;

public class PhysicsWorld {
    public static final float WORLD_UNITS_TO_ENGINE_UNITS = 32f;
    public static final float ENGINE_UNITS_TO_WORLD_UNITS = 1f/WORLD_UNITS_TO_ENGINE_UNITS;
    public static final int VELOCITY_ITERATIONS = 2;
    public static final int POSITION_ITERATIONS = 2;

    private World world;
    private boolean enable = false;
    private Vector2 tmp = new Vector2();

    private Box2DDebugRenderLayer debugRenderLayer;

    public PhysicsWorld() {
        world = new World(new Vector2(0, 0), true);
        ContactListener contactListener = new ContactListener() {
            public void beginContact(Contact contact) {
                GameObject gameObjectA = (GameObject) contact.getFixtureA().getBody().getUserData();
                GameObject gameObjectB = (GameObject) contact.getFixtureB().getBody().getUserData();
                gameObjectA.rigidBody.beginContact(gameObjectB);
                gameObjectB.rigidBody.beginContact(gameObjectA);
            }
            public void endContact(Contact contact) {
                GameObject gameObjectA = (GameObject) contact.getFixtureA().getBody().getUserData();
                GameObject gameObjectB = (GameObject) contact.getFixtureB().getBody().getUserData();
                gameObjectA.rigidBody.endContact(gameObjectB);
                gameObjectB.rigidBody.endContact(gameObjectA);
            }
            public void preSolve(Contact contact, Manifold oldManifold) {
            }
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        };
        world.setContactListener(contactListener);
    }

    public void setGravity(float x, float y) {
        world.setGravity(tmp.set(x, y));
    }

    public void update(float delta) {
        if(enable) world.step(delta, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }

    public Body createBody(BodyDef bodyDef, FixtureDef... fixturesDef) {
        Body body = world.createBody(bodyDef);
        for(FixtureDef def : fixturesDef) body.createFixture(def);
        enable = true;
        return body;
    }

    public RenderLayer getDebugRenderLayer() {
        if(debugRenderLayer == null) debugRenderLayer = new Box2DDebugRenderLayer(world);
        return debugRenderLayer;
    }

    private class Box2DDebugRenderLayer extends RenderLayer {

        public static final String RENDER_LAYER_TAG = "Box2DDebug";

        public final Matrix4 cameraMatrix = new Matrix4();
        private final World world;
        private final Box2DDebugRenderer renderer;

        public Box2DDebugRenderLayer(World world) {
            super(RENDER_LAYER_TAG, null);
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
            renderer.render(world, cameraMatrix);
        }

        @Override
        public void resize(int width, int height) {}
    }
}
