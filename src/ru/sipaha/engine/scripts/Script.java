package ru.sipaha.engine.scripts;

import com.badlogic.gdx.Gdx;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;

import java.lang.reflect.InvocationTargetException;

public abstract class Script {

    public GameObject gameObject;

    public void start(Engine engine){}
    public void initialize(Engine engine){}
    public void update(float delta) {}
    public void fixedUpdate(float delta) {}

    public abstract Script reset();

    public Script copy() {
        Class<? extends Script> clazz = getClass();
        Script script = null;
        try {
            script = clazz.getConstructor(clazz).newInstance(this);
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {
            String className = clazz.getSimpleName();
            String constructor = className+"("+className+" source) {}";
            Gdx.app.error("GameEngine","Constructor \""+constructor+"\" is not implemented! " +
                          "Full name is "+clazz.getCanonicalName());
            e.printStackTrace();
        }
        return script;
    }
}
