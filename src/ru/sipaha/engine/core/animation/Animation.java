package ru.sipaha.engine.core.animation;

import com.badlogic.gdx.Gdx;
import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.core.animation.сontinuous.ContinuousAnimation;
import ru.sipaha.engine.gameobjectdata.Transform;

import java.lang.reflect.InvocationTargetException;

/**
 * Created on 29.09.2014.
 */

public abstract class Animation {
    public String name;

    protected boolean loop = false;
    protected boolean run = false;
    protected float pauseTime = 0;
    protected boolean pause = false;
    protected float pauseTimer = 0;
    protected float time = 0;
    protected boolean back = false;
    protected boolean needBackMove = false;

    public Animation(String name) {
        if(name == null) throw new RuntimeException("Name can't be null!");
        this.name = name;
    }

    public Animation(Animation prototype) {
        name = prototype.name;
        loop = prototype.loop;
        run = prototype.run;
        pauseTime = prototype.pauseTime;
        pause = prototype.pause;
        needBackMove = prototype.needBackMove;
    }

    public abstract void update(Entity[] entities, Transform[] transforms, float delta);

    public void start(Entity[] entities, Transform[] transforms) {
        run = true;
        time = 0;
        pause = false;
        pauseTimer = 0;
    }

    public Animation setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public Animation setPauseTime(float pauseTime) {
        this.pauseTime = pauseTime;
        return this;
    }

    public Animation setNeedBackMove(boolean needBackMove) {
        this.needBackMove = needBackMove;
        return this;
    }

    public void reset(Animation prototype) {
        run = prototype.run;
        time = 0;
        pause = false;
        pauseTimer = 0;
    }

    public Animation copy() {
        Class<? extends Animation> clazz = getClass();
        Animation script = null;
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
