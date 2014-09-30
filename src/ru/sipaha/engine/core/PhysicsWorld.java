package ru.sipaha.engine.core;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ru.sipaha.engine.graphics.Camera;

public class PhysicsWorld {
    public static final float WORLD_UNITS_TO_ENGINE_UNITS = 32f;
    public static final float ENGINE_UNITS_TO_WORLD_UNITS = 1f/WORLD_UNITS_TO_ENGINE_UNITS;
    public static final int VELOCITY_ITERATIONS = 2;
    public static final int POSITION_ITERATIONS = 2;

    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private Matrix4 projectionDebugMatrix;
    private boolean enable = false;
    private Vector2 tmp = new Vector2();

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

    public World getWorld() {
        return world;
    }

}
