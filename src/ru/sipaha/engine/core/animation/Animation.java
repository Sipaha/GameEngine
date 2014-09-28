package ru.sipaha.engine.core.animation;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.core.animation.animatedunit.AnimatedUnit;
import ru.sipaha.engine.gameobjectdata.Transform;

public class Animation {
    public String name;
    private AnimatedUnit[] animatedUnits;

    private boolean loop = false;
    private boolean run = false;
    private float pauseTime = 0;
    private boolean pause = false;
    private float pauseTimer = 0;
    private float time = 0;
    private float timeLimit = 0;

    public Animation(Animation prototype) {
        name = prototype.name;
        animatedUnits = prototype.animatedUnits;
        loop = prototype.loop;
        run = prototype.run;
        pauseTime = prototype.pauseTime;
        pause = prototype.pause;
        pauseTimer = prototype.pauseTimer;
        time = prototype.time;
        timeLimit = prototype.timeLimit;
    }

    public Animation(String name, AnimatedUnit... units) {
        if(name == null) throw new RuntimeException("Name can't be null!");
        this.name = name;
        animatedUnits = units;
        timeLimit = units[0].getMaxDefinedTime();
        for(int i = 1; i < units.length; i++) {
            timeLimit = Math.max(units[i].getMaxDefinedTime(), timeLimit);
        }
    }

    public void update(Entity[] entities, Transform[] transforms, float delta) {
        if (!run) return;
        if (!pause) {
            time += delta;
            if (time >= timeLimit) {
                run = loop;
                if (run) {
                    if (pauseTime > 0) {
                        pause = true;
                        time = 0;
                    } else {
                        time -= timeLimit;
                    }
                } else {
                    time = timeLimit;
                }
            }
            for(AnimatedUnit u : animatedUnits) {
                u.update(entities, transforms, time);
            }
        } else {
            if ((pauseTimer += delta) >= pauseTime) {
                pauseTimer = 0;
                pause = false;
            }
        }
    }

    public Animation setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public Animation setPauseTime(float pauseTime) {
        this.pauseTime = pauseTime;
        return this;
    }

    public void start(Entity[] entities, Transform[] transforms) {
        run = true;
        time = 0;
        pause = false;
        pauseTimer = 0;
        for(AnimatedUnit u : animatedUnits) {
            u.start(entities, transforms);
        }
    }

    public void stop() {
        run = false;
    }
}
