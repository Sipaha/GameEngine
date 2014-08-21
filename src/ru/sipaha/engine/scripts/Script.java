package ru.sipaha.engine.scripts;


import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;

public abstract class Script {

    public GameObject go;

    public Script copy() {
        try {
            Script copy = getClass().newInstance();
            copy.set(this);
            return copy;
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract void start(Engine engine);
    public void update(float delta) {}
    public void fixedUpdate(float delta) {}

    public abstract void set(Script source);
}
