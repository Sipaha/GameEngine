package ru.sipaha.engine.core;

import ru.sipaha.engine.gameobjectdata.Life;
import ru.sipaha.engine.gameobjectdata.MeshRenderer;
import ru.sipaha.engine.gameobjectdata.Motion;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.Array;

public class Replicator {
    private GameObject template;
    private Engine engine;
    private final Array<GameObject> cache;

    protected Replicator(Engine engine, GameObject template) {
        cache = new Array<>(false, 32, GameObject.class);
        this.engine = engine;
        this.template = template;
    }

    /**
     * Get game object from cache or creates new
     */
    public GameObject get() {
        if(cache.size > 0) {
            return reset(cache.pop());
        } else {
            return create();
        }
    }

    /**
     * Creates new game object.
     */
    public GameObject create() {
        GameObject gameObject = new GameObject(template.name);
        gameObject.replicator = this;
        gameObject.transform = new Transform(template.transform);
        gameObject.motion = new Motion(template.motion);
        gameObject.renderer = new MeshRenderer(template.renderer);
        gameObject.life = new Life(template.life);
        engine.addGameObject(gameObject);
        return gameObject;
    }

    protected GameObject reset(GameObject gameObject) {
        gameObject.transform.set(template.transform);
        gameObject.motion.set(template.motion);
        gameObject.renderer.set(template.renderer);
        return gameObject;
    }

    protected void free(GameObject gameObject) {
        cache.add(gameObject);
        gameObject.enable = false;
    }

    protected void remove(GameObject gameObject) {
        engine.removeGameObject(gameObject);
    }
}
