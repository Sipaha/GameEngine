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

    public Animation(AnimatedUnit... units) {

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
