package ru.sipaha.engine.core;

import com.badlogic.gdx.Gdx;

import java.lang.reflect.InvocationTargetException;

public abstract class Script {
    protected GameObject gameObject;

    protected void update(float delta) {}
    protected void fixedUpdate(float delta) {}
    protected void initialize(Engine engine) {}
    protected void start(Engine engine) {}

    protected abstract void reset();

    public Script copy() {
        Script script = null;
        Class<? extends Script> clazz = getClass();
        try {
            try {
                script = clazz.getConstructor(clazz).newInstance(this);
            } catch (NoSuchMethodException e) {
                script = clazz.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            String className = clazz.getSimpleName();
            String constructor = "public "+className+"("+className+" source) {}";
            Gdx.app.error("GameEngine","Constructor \""+constructor+"\" is not implemented! " +
                    "Full name is "+clazz.getCanonicalName());
            e.printStackTrace();
        }
        return script;
    }
}
