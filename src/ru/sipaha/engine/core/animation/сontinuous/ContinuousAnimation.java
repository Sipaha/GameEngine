package ru.sipaha.engine.core.animation.сontinuous;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.core.animation.Animation;
import ru.sipaha.engine.gameobjectdata.Transform;

public class ContinuousAnimation extends Animation {

    private ContinuousAnimatedUnit[] animatedUnits;
    private float timeLimit = 0;

    public ContinuousAnimation(ContinuousAnimation prototype) {
        super(prototype);
        animatedUnits = prototype.animatedUnits;
        timeLimit = prototype.timeLimit;
    }

    public ContinuousAnimation(String name, ContinuousAnimatedUnit... units) {
        super(name);
        animatedUnits = units;
        timeLimit = units[0].getMaxDefinedTime();
        for(int i = 1; i < units.length; i++) {
            timeLimit = Math.max(units[i].getMaxDefinedTime(), timeLimit);
        }
    }

    @Override
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
            for(ContinuousAnimatedUnit u : animatedUnits) {
                u.update(entities, transforms, time);
            }
        } else {
            if ((pauseTimer += delta) >= pauseTime) {
                pauseTimer = 0;
                pause = false;
            }
        }
    }

    @Override
    public void start(Entity[] entities, Transform[] transforms) {
        super.start(entities, transforms);
        for(ContinuousAnimatedUnit u : animatedUnits) {
            u.start(entities, transforms);
        }
    }

    public void stop() {
        run = false;
    }
}
