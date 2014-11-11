package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.HashSet;

/**
 * Created on 15.10.2014.
 */

public class Factory {

    private final Engine engine;
    private final ObjectMap<String, EngineUnit> templatesByName = new ObjectMap<>();
    private final IntMap<EngineUnit> templatesById = new IntMap<>();
    private final HashSet<EngineUnit> hashSet = new HashSet<>();

    public Factory(Engine engine) {
        this.engine = engine;
    }

    public void addTemplate(EngineUnit template, String name) {
        EngineUnit oldValue = templatesByName.put(name, template);
        if(oldValue != null) {
            hashSet.remove(oldValue);
        }
        hashSet.add(template);
    }

    public void addTemplate(EngineUnit template, int id) {
        EngineUnit oldValue = templatesById.put(id, template);
        if(oldValue != null) {
            hashSet.remove(oldValue);
        }
        hashSet.add(template);
    }

    public void addTemplate(EngineUnit template, String name, int id) {
        addTemplate(template, name);
        addTemplate(template, id);
    }

    public <T extends EngineUnit> T getTemplate(String name) {
        return (T)templatesByName.get(name);
    }

    public <T extends EngineUnit> T getTemplate(int id) {
        return (T)templatesById.get(id);
    }

    public <T extends EngineUnit> T create(int id) {
        return (T)templatesById.get(id).copy();
    }

    public <T extends EngineUnit> T create(String name) {
        return (T)templatesByName.get(name).copy();
    }

    protected void initialize() {
        for(EngineUnit template : hashSet) {
            template.initialize(engine);
        }
    }
}
