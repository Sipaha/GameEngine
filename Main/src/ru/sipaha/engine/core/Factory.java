package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.HashSet;

/**
 * Created on 15.10.2014.
 */

public class Factory {

    private final Engine engine;
    private final ObjectMap<String, GameObject> templatesByName = new ObjectMap<>();
    private final IntMap<GameObject> templatesById = new IntMap<>();
    private final HashSet<GameObject> hashSet = new HashSet<>();

    public Factory(Engine engine) {
        this.engine = engine;
    }

    public void addTemplate(GameObject template, String name) {
        GameObject oldValue = templatesByName.put(name, template);
        if(oldValue != null) {
            hashSet.remove(oldValue);
        }
        hashSet.add(template);
    }

    public void addTemplate(GameObject template, int id) {
        GameObject oldValue = templatesById.put(id, template);
        if(oldValue != null) {
            hashSet.remove(oldValue);
        }
        hashSet.add(template);
    }

    public void addTemplate(GameObject template, String name, int id) {
        addTemplate(template, name);
        addTemplate(template, id);
    }

    public GameObject getTemplate(String name) {
        return templatesByName.get(name);
    }

    public GameObject getTemplate(int id) {
        return templatesById.get(id);
    }

    public GameObject create(int id) {
        return templatesById.get(id).copy();
    }

    public GameObject create(String name) {
        return templatesByName.get(name).copy();
    }

    protected void initialize() {
        for(GameObject template : hashSet) {
            template.initialize(engine);
        }
    }
}
