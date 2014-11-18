package ru.sipaha.engine.core;

import com.badlogic.gdx.Gdx;
import ru.sipaha.engine.graphics.RenderBuffer;
import ru.sipaha.engine.graphics.Renderable;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.structures.Bounds;

import java.lang.reflect.InvocationTargetException;

/**
 * Created on 16.11.2014.
 */

public class Entity extends Renderable {

    public final Transform transform = new Transform();

    private String name;

    private int parentIdx = -1;

    public Entity() {}

    public Entity(String name) {
        this.name = name;
    }

    public Entity(Entity prototype) {
        name = prototype.name;
        transform.reset(prototype.transform);
        parentIdx = prototype.parentIdx;
    }

    public void initialize(Engine engine, Array<Entity> entities) {
        if(transform.parent != null) {
            for(int i = 0; i < entities.size; i++) {
                if(transform.parent == entities.get(i).transform) {
                    parentIdx = i;
                    break;
                }
            }
        }
    }

    public void start(Engine engine, Transform baseTransform, Array<Entity> entities) {
        if(parentIdx < 0) {
            transform.parent = baseTransform;
        } else {
            transform.parent = entities.get(parentIdx).transform;
        }
    }

    public void update(float delta) {
        transform.update();
    }

    public void reset() {}

    @Override
    public void render(RenderBuffer buffer) {}

    @Override
    public int getRenderSize() {
        return 0;
    }

    @Override
    public Bounds getBounds() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Entity copy() {
        Entity entity = null;
        Class<? extends Entity> clazz = getClass();
        try {
            entity = clazz.getConstructor(clazz).newInstance(this);
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {
            String className = clazz.getSimpleName();
            String constructor = "public "+className+"("+className+" prototype) {}";
            Gdx.app.error("GameEngine","Constructor \""+constructor+"\" is not implemented! " +
                                                                "Full name is "+clazz.getCanonicalName());
            e.printStackTrace();
        }
        return entity;
    }
}
