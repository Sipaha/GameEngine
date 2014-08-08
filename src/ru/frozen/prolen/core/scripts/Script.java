package ru.frozen.prolen.core.scripts;

import ru.frozen.prolen.core.gameobject.GameObject;

public abstract class Script<T extends Script> {

    public GameObject go;
    protected T template;

    public T copy() {
        try {
            T copy = (T)getClass().newInstance();
            copy.template = this;
            copy.copied();
            return copy;
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract void start();
    public abstract void update(float delta);
    public abstract void fixedUpdate(float delta);

    protected abstract void copied();
}
