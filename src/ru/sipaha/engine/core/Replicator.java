package ru.sipaha.engine.core;

import ru.sipaha.engine.utils.Array;

public class Replicator {
    private GameObject template;
    private Engine engine;
    private final Array<GameObject> cache;

    protected Replicator(Engine engine, GameObject template) {
        this(engine);
        setTemplate(template);
    }

    protected Replicator(Engine engine) {
        cache = new Array<>(false, 32, GameObject.class);
        this.engine = engine;
    }

    protected void setTemplate(GameObject template) {
        this.template = template;
    }

    /**
     * Get game object from cache or creates new
     */
    public GameObject get() {
        if(cache.size > 0) {
            return cache.pop().reset(template);
        } else {
            return create();
        }
    }

    /**
     * Creates new game object.
     */
    public GameObject create() {
        if(template == null) throw new RuntimeException("This replicator is not initialized");
        GameObject gameObject = new GameObject(template);
        gameObject.replicator = this;
        engine.addGameObject(gameObject);
        return gameObject;
    }

    protected void free(GameObject gameObject) {
        cache.add(gameObject);
        gameObject.enable = false;
        if(gameObject.renderer != null) gameObject.renderer.visible = false;
    }

    protected void initialize(Engine engine) {
        template.initialize(engine);
    }

    protected void remove(GameObject gameObject) {
        engine.removeGameObject(gameObject);
    }
}
