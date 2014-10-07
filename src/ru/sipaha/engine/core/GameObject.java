package ru.sipaha.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.core.animation.Animation;
import ru.sipaha.engine.core.animation.Animator;
import ru.sipaha.engine.gameobjectdata.*;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.scripts.Script;
import ru.sipaha.engine.utils.Array;

import java.util.BitSet;

public class GameObject {
    private static int idCounter = 0;
    public final int UID = idCounter++;

    public Transform transform; //root transform
    public GameObjectRenderer renderer = new GameObjectRenderer();
    public Life life = new Life();
    public RigidBody rigidBody;
    public boolean enable = true;

    protected final BitSet tag_bits;
    protected Engine engine;
    protected Replicator replicator;
    protected Rectangle bounds;

    private final ObjectIntMap<String> transformByEntityName;
    private final ObjectIntMap<Class> scriptsIdByClass;
    private final Array<Script> scripts;
    private final Array<Entity> entities;
    private final Array<Transform> transforms;
    private Animator animator;

    private boolean initialized = false;

    public GameObject() {
        scripts = new Array<>(true, 4, Script.class);
        entities = new Array<>(true, 4, Entity.class);
        transforms = new Array<>(true, 4, Transform.class);
        scriptsIdByClass = new ObjectIntMap<>();
        transformByEntityName = new ObjectIntMap<>();
        tag_bits = new BitSet();
        replicator = null;
    }

    public GameObject(Entity[] entities, Transform[] transforms, Script[] scripts,
                                                Texture t, ShaderProgram s, int zOrder) {
        this();
        transform = transforms[0];
        this.entities.addAll(entities);
        this.transforms.addAll(transforms);
        this.scripts.addAll(scripts);
        renderer.setTexture(t);
        renderer.setShader(s);
        renderer.setZOrder(zOrder);
    }

    public GameObject(TextureRegion region) {
        this(region, RenderUnit.DEFAULT_Z_ORDER, new Script[0]);
    }

    public GameObject(TextureRegion region, int zOrder) {
        this(region, zOrder, new Script[0]);
    }

    public GameObject(TextureRegion region, int zOrder, Script... scripts) {
        this();
        this.entities.add(new Entity(region));
        this.transforms.add(new Transform());
        this.scripts.addAll(scripts);
        renderer.setZOrder(zOrder);
        renderer.setTexture(region.getTexture());
        transform = transforms.get(0);
    }

    public GameObject(Texture texture) {
        this();
        entities.add(new Entity(texture));
        transforms.add(new Transform());
        transform = transforms.get(0);
        renderer.setTexture(texture);
    }

    public GameObject(GameObject prototype) {
        renderer = new GameObjectRenderer(prototype.renderer);
        life = new Life(prototype.life);
        entities = new Array<>(true, prototype.entities.size, Entity.class);
        for(int i = 0; i < prototype.entities.size; i++) {
            entities.add(new Entity(prototype.entities.items[i]));
        }
        transforms = new Array<>(true, prototype.transforms.size, Transform.class);
        for(int i = 0; i < prototype.transforms.size; i++) {
            transforms.add(new Transform(prototype.transforms.items[i]));
        }
        scripts = new Array<>(true, prototype.scripts.size, Script.class);
        for(int i = 0; i < prototype.scripts.size; i++) {
            Script script = prototype.scripts.items[i].copy();
            script.gameObject = this;
            scripts.add(script);
        }
        transform = transforms.items[0];
        renderer.setEntities(entities.items);
        transformByEntityName = prototype.transformByEntityName;
        scriptsIdByClass = prototype.scriptsIdByClass;
        tag_bits = prototype.tag_bits;
        if(prototype.rigidBody != null) {
            rigidBody = new RigidBody(prototype.rigidBody);
            transform.rigidBody = rigidBody;
        }
        if(prototype.animator != null) animator = new Animator(prototype.animator);
    }

    public void createBody(float boundsScale) {
        EntityRenderer renderer = entities.items[0].renderer;
        Vector2 position = transform.getPosition().sub(renderer.originX, renderer.originY);
        Rectangle rect = new Rectangle(position.x, position.y, renderer.width, renderer.height);
        Vector2 rectPos = rect.getPosition(new Vector2());
        Vector2 transformPos = transform.getPosition();
        rigidBody = new RigidBody(rect, boundsScale, transformPos.sub(rectPos));
        transform.rigidBody = rigidBody;
    }

    public void createBody(float boundsScale, float density, float friction, float restitution) {
        EntityRenderer renderer = entities.items[0].renderer;
        Vector2 position = transform.getPosition().sub(renderer.originX, renderer.originY);
        Rectangle rect = new Rectangle(position.x, position.y, renderer.width, renderer.height);
        Vector2 rectPos = rect.getPosition(new Vector2());
        rigidBody = new RigidBody(rect, boundsScale, transform.getPosition().sub(rectPos), density, friction, restitution);
        transform.rigidBody = rigidBody;
    }

    public void createBody(BodyDef def, FixtureDef... fixturesDef) {
        rigidBody = new RigidBody(def, fixturesDef);
        transform.rigidBody = rigidBody;
    }

    public void initialize(Engine engine) {
        entities.shrink();
        transforms.shrink();
        scripts.shrink();
        renderer.setEntities(entities.items);

        for(Entity e : entities) if(e.name != null) {
            transformByEntityName.put(e.name, e.transformId);
        }
        for(Script s : scripts) {
            s.gameObject = this;
            s.initialize(engine);
        }
        initialized = true;
    }

    public void start(Engine engine) {
        if(rigidBody != null) rigidBody.create(this, engine.getPhysicsWorld());
        for(int i = 0; i < scripts.size; i++) scripts.get(i).start(engine);
    }

    public void update(float delta) {
        for(Script s : scripts) s.update(delta);
    }

    public void fixedUpdate(float delta) {
        for(Script s : scripts) s.fixedUpdate(delta);
    }

    public GameObject updateData(float delta) {
        if(animator != null) animator.update(entities.items, transforms.items, delta);
        transform.update(delta);
        for (int i = 1; i < transforms.size; i++) {
            Transform t = transforms.items[i];
            t.update(transforms.items[t.parentId], delta);
        }
        for (Entity e : entities) {
            e.renderer.update(transforms.items[e.transformId]);
        }
        for (Transform transform : transforms) {
            transform.wasChanged = false;
        }
        return this;
    }

    public GameObject reset(GameObject prototype) {
        for(int i = 0; i < entities.size; i++) {
            entities.items[i].reset(prototype.entities.items[i]);
        }
        for(int i = 0; i < transforms.size; i++) {
            transforms.items[i].reset(prototype.transforms.items[i]);
        }
        for (Script script : scripts) script.reset();
        life.reset(prototype.life);
        if(rigidBody != null) rigidBody.reset(prototype.rigidBody);
        if(renderer != null) renderer.reset(prototype.renderer);
        enable = prototype.enable;
        return this;
    }

    public Rectangle getBounds() {
        if(bounds == null) bounds = entities.items[0].renderer.getBounds(null);
        for(Entity e : entities) e.renderer.getBounds(bounds);
        return bounds;
    }

    public void free() {
        replicator.free(this);
    }

    public void remove() {
        if(replicator != null) {
            replicator.remove(this);
        } else {
            engine.removeGameObject(this);
        }
        engine = null;
    }

    public void disable() {
        enable = false;
        rigidBody.disable();
    }

    public void enable() {
        enable = true;
        rigidBody.enable();
    }

    public void addAnimation(Animation animation) {
        if(animator == null) animator = new Animator();
        animator.add(animation);
    }

    public void startAnimation(String name) {
        animator.start(name, entities.items, transforms.items);
    }

    public void startAnimation(Animation animation) {
        if(animation != null) animation.start(entities.items, transforms.items);
    }

    public Animation getAnimation(String name) {
        return animator.get(name);
    }

    public <T> T getScript(Class<T> type) {
        int scriptId = scriptsIdByClass.get(type, -1);
        if(scriptId != -1) return (T)scripts.get(scriptId);
        for(Script s : scripts) if(type.isInstance(s)) return (T)s;
        Gdx.app.error("GameEngine","Script \""+type.getName()+"\" not found!");
        return null;
    }

    public Transform getTransform(String name) {
        if(name == null) return transform;
        int transformId = transformByEntityName.get(name, -1);
        if(transformId == -1) return transform;
        return transforms.items[transformId];
    }

    public void addTransform(Transform t) {
        transforms.add(t);
        if(transform == null) transform = t;
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void addScript(Script s) {
        scripts.add(s);
    }

    public void addScript(Class type, Script script) {
        scriptsIdByClass.put(type, scripts.size);
        scripts.add(script);
    }

    public void setTexture(Texture t) {
        if(initialized) Gdx.app.error("GameEngine","Texture setting not available after initializing!");
        else renderer.setTexture(t);
    }

    public void setZOrder(int zOrder) {
        if(initialized) Gdx.app.error("GameEngine","Z order setting not available after initializing!");
        else renderer.setZOrder(zOrder);
    }

    public void setShader(ShaderProgram shader) {
        if(initialized) Gdx.app.error("GameEngine","Shader setting not available after initializing!");
        else renderer.setShader(shader);
    }
}
