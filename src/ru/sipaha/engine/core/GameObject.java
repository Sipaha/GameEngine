package ru.sipaha.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.ObjectIntMap;
import ru.sipaha.engine.gameobjectdata.*;
import ru.sipaha.engine.scripts.Script;

import java.util.BitSet;

public class GameObject {
    private static int idCounter = 0;
    public final int UID = idCounter++;

    public final Transform transform; //root transform
    public final GameObjectRenderer renderer;
    public final Life life;
    public RigidBody body;
    public boolean enable = true;

    protected Replicator replicator;
    protected BitSet tag_bits = new BitSet();
    protected Rectangle bounds;

    private final ObjectIntMap<String> transformByEntityName;
    private final Entity[] entities;
    private final Transform[] transforms;
    private final Script[] scripts;

    public GameObject(Entity[] entities, Transform[] transforms, Script[] scripts,
                                                Texture t, ShaderProgram s, int zOrder) {
        renderer = new GameObjectRenderer(t, s, zOrder);
        this.entities = entities;
        this.transforms = transforms;
        this.scripts = scripts;
        transform = transforms[0];
        life = new Life();
        transformByEntityName = new ObjectIntMap<>();
        setup();
    }

    public GameObject(TextureRegion region, int zOrder) {
        this(region, zOrder, new Script[0]);
    }

    public GameObject(TextureRegion region, int zOrder, Script... scripts) {
        entities = new Entity[]{new Entity(region)};
        renderer = new GameObjectRenderer(region.getTexture(), null, zOrder);
        life = new Life();
        transforms = new Transform[]{new Transform()};
        transform = transforms[0];
        this.scripts = scripts;
        transformByEntityName = new ObjectIntMap<>();
        setup();
    }

    public GameObject(GameObject prototype) {
        renderer = new GameObjectRenderer(prototype.renderer);
        life = new Life(prototype.life);
        entities = new Entity[prototype.entities.length];
        for(int i = 0; i < entities.length; i++) {
            entities[i] = new Entity(prototype.entities[i]);
        }
        transforms = new Transform[prototype.transforms.length];
        for(int i = 0; i < transforms.length; i++) {
            transforms[i] = new Transform(prototype.transforms[i]);
        }
        scripts = new Script[prototype.scripts.length];
        for(int i = 0; i < scripts.length; i++) {
            Script script = prototype.scripts[i].copy();
            script.gameObject = this;
            scripts[i] = script;
        }
        transform = transforms[0];
        renderer.setEntities(entities);
        transformByEntityName = prototype.transformByEntityName;
        if(prototype.body != null) {
            body = new RigidBody(prototype.body);
            transform.rigidBody = body;
        }
    }

    private void setup() {
        renderer.setEntities(entities);
        for(Entity e : entities) if(e.name != null) {
            transformByEntityName.put(e.name, e.transformId);
        }
        for(Script s : scripts) s.gameObject = this;
    }

    public void createBody(float boundsScale) {
        EntityRenderer renderer = entities[0].renderer;
        Vector2 position = transform.getPosition().sub(renderer.originX, renderer.originY);
        Rectangle rect = new Rectangle(position.x, position.y, renderer.width, renderer.height);
        Vector2 rectPos = rect.getPosition(new Vector2());
        Vector2 transformPos = transform.getPosition();
        body = new RigidBody(rect, boundsScale, transformPos.sub(rectPos));
        transform.rigidBody = body;
    }

    public void createBody(float boundsScale, float density, float friction, float restitution) {
        EntityRenderer renderer = entities[0].renderer;
        Vector2 position = transform.getPosition().sub(renderer.originX, renderer.originY);
        Rectangle rect = new Rectangle(position.x, position.y, renderer.width, renderer.height);
        Vector2 rectPos = rect.getPosition(new Vector2());
        body = new RigidBody(rect, boundsScale, transform.getPosition().sub(rectPos), density, friction, restitution);
        transform.rigidBody = body;
    }

    public void createBody(BodyDef def, FixtureDef... fixturesDef) {
        body = new RigidBody(def, fixturesDef);
        transform.rigidBody = body;
    }

    public void start(Engine engine) {
        if(body != null) body.create(this, engine.getPhysicsWorld());
        for(Script s : scripts) s.start(engine);
    }

    public void update(float delta) {
        for(Script s : scripts) s.update(delta);
    }

    public void fixedUpdate(float delta) {
        for(Script s : scripts) s.fixedUpdate(delta);
    }

    public int render(float[] vertices, int pos) {
        if(renderer.visible) {
            float[] data = renderer.renderData;
            System.arraycopy(data, 0, vertices, pos, data.length);
            return pos + data.length;
        } else {
            return pos;
        }
    }

    public GameObject updateData(float delta) {
        transform.update(delta);
        for (int i = 1; i < transforms.length; i++) {
            Transform t = transforms[i];
            t.update(transforms[t.parentId], delta);
        }
        for (Entity e : entities) {
            e.renderer.update(transforms[e.transformId]);
        }
        for (Transform transform : transforms) {
            transform.wasChanged = false;
        }
        return this;
    }

    public GameObject reset(GameObject prototype) {
        for(int i = 0; i < entities.length; i++) {
            entities[i].reset(prototype.entities[i]);
        }
        for(int i = 0; i < transforms.length; i++) {
            transforms[i].reset(prototype.transforms[i]);
        }
        for (Script script : scripts) script.reset();
        if(body != null) body.reset(prototype.body);
        return this;
    }

    public Rectangle getBounds() {
        if(bounds == null) bounds = entities[0].renderer.getBounds(null);
        for(Entity e : entities) e.renderer.getBounds(bounds);
        return bounds;
    }

    public void free() {
        replicator.free(this);
    }

    public void remove() {
        replicator.remove(this);
    }

    public void disable() {
        enable = false;
        body.disable();
    }

    public void enable() {
        enable = true;
        body.enable();
    }

    public <T extends Script> T getScript(Class<T> type) {
        for(Script s : scripts) if(type.isInstance(s)) return (T)s;
        Gdx.app.error("GameEngine","Script \""+type.getName()+"\" not found!");
        return null;
    }

    public Transform getTransform(String name) {
        int transformId = transformByEntityName.get(name, -1);
        if(transformId == -1) return null;
        return transforms[transformId];
    }
}
