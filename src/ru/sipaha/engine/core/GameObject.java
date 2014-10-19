package ru.sipaha.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ObjectIntMap;
import ru.sipaha.engine.core.animation.Animation;
import ru.sipaha.engine.core.animation.Animator;
import ru.sipaha.engine.gameobjectdata.*;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.Array;

import java.util.BitSet;

public class GameObject extends RenderUnit {

    public final Life life;
    protected RigidBody rigidBody;

    protected final BitSet tag_bits;
    protected Engine engine;
    protected Rectangle bounds;
    protected boolean enable = true;

    private boolean reqEnable = false;

    private final ObjectIntMap<String> transformByEntityName;
    private final ObjectIntMap<Class> scriptsIdByClass;
    private final Array<Script> scripts;
    private final Array<Entity> entities;
    private final Array<Transform> transforms;
    private Animator animator;

    private GameObject prototype;
    private Array<GameObject> cache;

    public GameObject() {
        life = new Life();
        tag_bits = new BitSet();
        transformByEntityName = new ObjectIntMap<>();
        scriptsIdByClass = new ObjectIntMap<>();
        scripts = new Array<>(true, 4, Script.class);
        entities = new Array<>(true, 4, Entity.class);
        transforms = new Array<>(true, 4, Transform.class);
    }

    public GameObject(TextureRegion region) {
        this();
        entities.add(new Entity(region));
        transforms.add(new Transform());
        setTexture(region.getTexture());
    }

    public GameObject(Texture texture) {
        this();
        entities.add(new Entity(texture));
        transforms.add(new Transform());
        setTexture(texture);
    }

    @Override
    public int setRenderData(float[] data, int offset) {
        super.setRenderData(data, offset);
        for(Entity e : entities) {
            offset = e.setRenderData(data, offset);
        }
        return offset;
    }

    @Override
    public int render(float[] renderData, int position) {
        if(enable) {
            for(Entity e : entities) {
                position = e.render(renderData, position);
            }
        }
        return position;
    }

    @Override
    public int getRenderSize() {
        int sum = 0;
        for(Entity e : entities) sum += e.getRenderSize();
        return sum;
    }

    public GameObject(GameObject prototype) {
        super(prototype);
        this.prototype = prototype;
        this.engine = prototype.engine;
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
        transformByEntityName = prototype.transformByEntityName;
        scriptsIdByClass = prototype.scriptsIdByClass;
        tag_bits = prototype.tag_bits;
        if(prototype.rigidBody != null) {
            rigidBody = new RigidBody(prototype.rigidBody);
            transforms.items[0].rigidBody = rigidBody;
        }
        if(prototype.animator != null) animator = new Animator(prototype.animator);

        if(!isStatic()) {
            float[] data = new float[getRenderSize()];
            setRenderData(data, 0);
        }
    }

    public void setRigidBody(RigidBody body) {
        this.rigidBody = body;
    }

    public void initialize(Engine engine) {
        entities.shrink();
        transforms.shrink();
        scripts.shrink();

        for(Entity e : entities) {
            String name = e.getName();
            if(name != null) {
                transformByEntityName.put(name, e.getTransformId());
            }
        }
        for(Script s : scripts) {
            s.gameObject = this;
            s.initialize(engine);
        }
    }

    public void start(Engine engine) {
        for(int i = 0; i < scripts.size; i++) scripts.get(i).start(engine);
        if(rigidBody != null) rigidBody.create(this, engine.physicsWorld);
    }

    public void update(float delta) {
        if(enable) for (Script s : scripts) s.update(delta);
    }

    public void fixedUpdate(float delta) {
        if(enable) for (Script s : scripts) s.fixedUpdate(delta);
    }

    public void updateData(float delta) {
        if(reqEnable) {
            enable();
            reqEnable = false;
        }
        if(enable) {
            life.update(this, delta);
            if(!enable) return;
            if(animator != null) animator.update(entities.items, transforms.items, delta);
            transforms.items[0].update(delta);
            for (int i = 1; i < transforms.size; i++) {
                Transform t = transforms.items[i];
                t.update(transforms.items[t.parentId], delta);
            }
            for (Entity e : entities) {
                e.update(transforms.items);
            }
            for (Transform transform : transforms) {
                transform.wasChanged = false;
            }
        }
    }

    public GameObject reset() {
        for(int i = 0; i < entities.size; i++) {
            entities.items[i].reset(prototype.entities.items[i]);
        }
        for(int i = 0; i < transforms.size; i++) {
            transforms.items[i].reset(prototype.transforms.items[i]);
        }
        for (Script script : scripts) script.reset();
        life.reset(prototype.life);
        if(rigidBody != null) rigidBody.reset(prototype.rigidBody);
        if(animator != null) animator.reset(prototype.animator);
        return this;
    }

    public Rectangle getBounds() {
        updateData(0);
        bounds = new Rectangle();
        for(Entity e : entities) e.getBounds(bounds);
        return bounds;
    }

    public void addAnimation(Animation animation) {
        addAnimation(animation, false);
    }

    public void addAnimation(Animation animation, boolean start) {
        if(animator == null) animator = new Animator();
        animator.add(animation);
        if(start) animation.start(entities.items, transforms.items);
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
        int transformId = transformByEntityName.get(name, -1);
        return transforms.items[transformId];
    }

    public Transform getTransform() {
        return transforms.items[0];
    }

    public void addTransform(Transform t) {
        transforms.add(t);
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

    public void free() {
        cache.add(this);
        disable();
    }

    public void remove() {
        engine.removeGameObject(this);
        engine = null;
    }

    public GameObject disable() {
        enable = false;
        if(rigidBody != null) rigidBody.disable();
        return this;
    }

    public GameObject enable() {
        enable = true;
        if(rigidBody != null) rigidBody.enable();
        return this;
    }

    public GameObject copy() {
        if(cache == null) cache = new Array<>(true, 4, GameObject.class);
        if(cache.size == 0) {
            GameObject gameObject = new GameObject(this);
            gameObject.cache = cache;
            engine.addGameObject(gameObject);
            return gameObject;
        } else {
            GameObject gameObject = cache.pop();
            gameObject.reqEnable = true;
            gameObject.reset();
            return gameObject;
        }
    }
}
