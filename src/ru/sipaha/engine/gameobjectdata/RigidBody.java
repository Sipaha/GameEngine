package ru.sipaha.engine.gameobjectdata;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.PhysicsWorld;
import ru.sipaha.engine.utils.*;

public class RigidBody {

    private final BodyDef bodyDef;
    private final FixtureDef[] fixturesDef;

    private Body body;
    public boolean manualMoving;
    private Array<ContactListener> contactListeners = new Array<>(false, 2, ContactListener.class);

    public RigidBody(RigidBody rigidBody) {
        manualMoving = rigidBody.manualMoving;
        bodyDef = rigidBody.bodyDef;
        fixturesDef = rigidBody.fixturesDef;
    }

    public RigidBody(Rectangle rect, float rectScale, Vector2 offset) {
        float scale = rectScale * PhysicsWorld.ENGINE_UNITS_TO_WORLD_UNITS;
        float worldWidth = rect.width * scale;
        float worldHeight = rect.height * scale;
        Vector2 worldOffset = new Vector2(offset);
        worldOffset.scl(scale);

        bodyDef = new BodyDef();
        bodyDef.fixedRotation = false;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(rect.x * PhysicsWorld.ENGINE_UNITS_TO_WORLD_UNITS + worldOffset.x,
                             rect.y * PhysicsWorld.ENGINE_UNITS_TO_WORLD_UNITS + worldOffset.y);
        bodyDef.allowSleep = false;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        float hw = worldWidth / 2f;
        float hh = worldHeight / 2f;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hw, hh, worldOffset.sub(hw, hh), 0f);
        fixtureDef.shape = shape;

        manualMoving = true;
        fixturesDef = new FixtureDef[]{ fixtureDef };
    }

    public RigidBody(Rectangle rect, float rectScale, Vector2 offset,
                                                    float density, float friction, float restitution) {
        this(rect, rectScale, offset);
        bodyDef.allowSleep = true;
        fixturesDef[0].isSensor = false;
        fixturesDef[0].density = density;
        fixturesDef[0].friction = friction;
        fixturesDef[0].restitution = restitution;
        manualMoving = false;
    }

    public RigidBody(BodyDef bodyDef, FixtureDef... fixturesDef) {
        this.bodyDef = bodyDef;
        this.fixturesDef = fixturesDef;
        manualMoving = true;
        for(FixtureDef def : fixturesDef) if(!def.isSensor){
            manualMoving = false;
            break;
        }
        if(bodyDef.allowSleep && manualMoving) bodyDef.allowSleep = false;
    }

    public void setCategoryAndMask(int category, int mask) {
        for(FixtureDef def : fixturesDef) {
            def.filter.categoryBits = (short) category;
            def.filter.maskBits = (short) mask;
        }
    }

    public void setTransform(float x, float y, float angle) {
        float worldX = x * PhysicsWorld.ENGINE_UNITS_TO_WORLD_UNITS;
        float worldY = y * PhysicsWorld.ENGINE_UNITS_TO_WORLD_UNITS;
        float radAngle = angle * MathUtils.degreesToRadians;
        body.setTransform(worldX, worldY, radAngle);
    }

    public void translate(float dx, float dy) {
        float worldDx = dx * PhysicsWorld.ENGINE_UNITS_TO_WORLD_UNITS;
        float worldDy = dy * PhysicsWorld.ENGINE_UNITS_TO_WORLD_UNITS;
        Vector2 position = body.getPosition().add(worldDx, worldDy);
        float angle = body.getAngle();
        body.setTransform(position.x, position.y, angle);
    }

    public void create(GameObject gameObject, PhysicsWorld world) {
        body = world.createBody(bodyDef, fixturesDef);
        body.setUserData(gameObject);
    }

    public void beginContact(GameObject gameObject) {
        for(ContactListener listener : contactListeners) {
            listener.beginContact(gameObject);
        }
    }

    public void endContact(GameObject gameObject) {
        for(ContactListener listener : contactListeners) {
            listener.endContact(gameObject);
        }
    }

    public void addContactListener(ContactListener contactListener) {
        contactListeners.add(contactListener);
    }

    public void enable() {
        body.setActive(true);
    }

    public void disable() {
        body.setActive(false);
    }

    public Vector2 getPosition() {
        return body.getPosition().scl(PhysicsWorld.WORLD_UNITS_TO_ENGINE_UNITS);
    }

    public float getAngle() {
        return body.getAngle() * MathUtils.degreesToRadians;
    }

    public void reset(RigidBody template) {
        body.setTransform(template.body.getPosition(), template.body.getAngle());
        contactListeners.clear();
    }
}
