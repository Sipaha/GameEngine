package ru.sipaha.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectIntMap;
import ru.sipaha.engine.core.animation.Animation;
import ru.sipaha.engine.core.animation.Animator;
import ru.sipaha.engine.gameobjectdata.*;
import ru.sipaha.engine.graphics.RenderBuffer;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.structures.Bounds;

import java.util.BitSet;

public class GameObject extends RenderUnit {

    public final Life life;
    protected RigidBody rigidBody;

    protected final BitSet tag_bits;
    protected Engine engine;
    protected boolean enable = true;

    private boolean reqEnable = false;
    private boolean reqDisable = false;
    private boolean dynamicRenderSize = false;

    private final ObjectIntMap<String> entityIdByName;
    private final ObjectIntMap<Class> scriptsIdByClass;
    private final Array<Script> scripts;
    private final Array<Entity> entities;
    private Animator animator;

    private GameObject prototype;
    private Array<GameObject> cache;

    private Bounds bounds;

    public GameObject() {
        life = new Life();
        tag_bits = new BitSet();
        entityIdByName = new ObjectIntMap<>();
        scriptsIdByClass = new ObjectIntMap<>();
        scripts = new Array<>(true, 4, Script.class);
        entities = new Array<>(true, 4, Entity.class);
    }

    public GameObject(TextureRegion region) {
        this();
        entities.add(new Entity(region));
        setTexture(region.getTexture());
    }

    public GameObject(Texture texture) {
        this();
        entities.add(new Entity(texture));
        setTexture(texture);
    }

    public GameObject(GameObject prototype) {
        super(prototype);
        this.prototype = prototype;
        this.engine = prototype.engine;
        life = new Life(prototype.life);

        entities = new Array<>(true, prototype.entities.size, Entity.class);
        for(int i = 0; i < prototype.entities.size; i++) {
            Entity e = new Entity(prototype.entities.items[i]);
            entities.add(e);
        }
        for(Entity e : entities) {
            e.setLinks(entities);
        }

        dynamicRenderSize = prototype.dynamicRenderSize;
        if(!dynamicRenderSize) {
            renderData = new float[prototype.renderData.length];
            offset = 0;
            for(Entity e : entities) {
                offset = e.setRenderData(renderData, offset);
            }
            offset = 0;
        }

        scripts = new Array<>(true, prototype.scripts.size, Script.class);
        for(int i = 0; i < prototype.scripts.size; i++) {
            Script script = prototype.scripts.items[i].copy();
            script.gameObject = this;
            scripts.add(script);
        }
        entityIdByName = prototype.entityIdByName;
        scriptsIdByClass = prototype.scriptsIdByClass;
        tag_bits = prototype.tag_bits;
        if(prototype.rigidBody != null) {
            rigidBody = new RigidBody(prototype.rigidBody);
            entities.items[0].transform.rigidBody = rigidBody;
        }
        if(prototype.animator != null) {
            animator = new Animator(prototype.animator, entities);
        }
    }

    @Override
    public void setRenderData(RenderBuffer buffer) {
        super.setRenderData(buffer);
        for(Entity e : entities) {
            e.setRenderData(buffer);
        }
    }

    @Override
    public void render(RenderBuffer buffer) {
        if(enable) {
            if(dynamicRenderSize) {
                for(Entity e : entities) e.render(buffer);
            } else {
                buffer.render(renderData, 0, renderData.length);
            }
        }
    }

    @Override
    public int getRenderSize() {
        int sum = 0;
        for(Entity e : entities) sum += e.getRenderSize();
        return sum;
    }

    public void setRigidBody(RigidBody body) {
        this.rigidBody = body;
    }

    public void initialize(Engine engine) {
        entities.shrink();
        scripts.shrink();

        for(int i = 0; i < entities.size; i++) {
            Entity e = entities.items[i];
            e.updateLinks(entities);
            String name = e.getName();
            if(name != null) entityIdByName.put(name, i);
        }

        for(Script s : scripts) {
            s.gameObject = this;
            s.initialize(engine);
        }

        int renderSize = 0;
        for(Entity e : entities) {
            int size = e.getRenderSize();
            if(size != -1) {
                renderSize += size;
            } else {
                dynamicRenderSize = true;
                break;
            }
        }
        if(!dynamicRenderSize) {
            renderData = new float[renderSize];
            offset = 0;
            for(Entity e : entities) {
                offset = e.setRenderData(renderData, offset);
            }
            offset = 0;
        }

        for(Entity e : entities) {
            e.start(engine);
        }

        if(animator != null) {
            animator.initialize(entities);
        }
    }

    public void start(Engine engine) {
        for(int i = 0; i < scripts.size; i++) scripts.get(i).start(engine);
        if(rigidBody != null) rigidBody.create(this, engine.physicsWorld);
        for(Entity e : entities) e.start(engine);
    }

    public void update(float delta) {
        if(enable) for (Script s : scripts) s.update(delta);
    }

    public void fixedUpdate(float delta) {
        if(enable) for (Script s : scripts) s.fixedUpdate(delta);
    }

    public void updateData(float delta) {
        if(reqEnable) {
            enable = true;
            reqEnable = false;
        }
        if(enable) {
            life.update(this, delta);
            if(!enable) return;
            if(animator != null) {
                animator.update(delta);
            }
            for(Entity e : entities) {
                e.update(delta);
            }
            for(Entity e : entities) {
                e.transform.wasChanged = false;
            }
        }
        if(reqDisable) {
            enable = false;
            reqDisable = false;
        }
    }

    public GameObject reset() {
        for(int i = 0; i < entities.size; i++) {
            entities.items[i].reset(prototype.entities.items[i]);
        }
        for (Script script : scripts) script.reset();
        life.reset(prototype.life);
        if(rigidBody != null) rigidBody.reset(prototype.rigidBody);
        if(animator != null) animator.reset(prototype.animator);
        return this;
    }

    public Bounds getBounds() {
        updateData(0);
        if(bounds == null) bounds = new Bounds();
        bounds.reset();
        for(Entity entity : entities) {
            bounds.union(entity.getBounds());
        }
        return bounds;
    }

    public void addAnimation(Animation animation) {
        if(animator == null) animator = new Animator();
        animator.add(animation);
    }

    public void startAnimation(String name) {
        animator.start(name);
    }

    public Animation getAnimation(String name) {
        return animator.get(name);
    }

    public Entity getEntity(String name) {
        return entities.items[entityIdByName.get(name, -1)];
    }
    public Entity getEntity(int idx) {
        return entities.items[idx];
    }
    public void addEntity(Entity e) {
        entities.add(e);
    }

    public <T> T getScript(Class<T> type) {
        int scriptId = scriptsIdByClass.get(type, -1);
        if(scriptId != -1) return (T)scripts.get(scriptId);
        for(Script s : scripts) if(type.isInstance(s)) return (T)s;
        Gdx.app.error("GameEngine","Script \""+type.getName()+"\" not found!");
        return null;
    }
    public void addScript(Script s) {
        scripts.add(s);
    }
    public void addScript(Class type, Script script) {
        scriptsIdByClass.put(type, scripts.size);
        scripts.add(script);
    }

    public Transform getTransform() {
        return entities.first().transform;
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
        reqDisable = true;
        if(rigidBody != null) rigidBody.disable();
        if(isStatic()) {
            for(Entity entity : entities) {
                entity.visible.set(false);
            }
        }
        return this;
    }

    public GameObject enable() {
        reqEnable = true;
        if(rigidBody != null) rigidBody.enable();
        if(isStatic()) {
            for(Entity entity : entities) {
                entity.visible.set(true);
            }
        }
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
            gameObject.enable();
            gameObject.reset();
            return gameObject;
        }
    }
}