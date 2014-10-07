package ru.sipaha.engine.core.animation.discrete;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.core.animation.Animation;
import ru.sipaha.engine.core.animation.сontinuous.ContinuousAnimatedUnit;
import ru.sipaha.engine.gameobjectdata.Transform;

/**
 * Created on 29.09.2014.
 */

public abstract class DiscreteAnimation extends Animation {

    protected final float[] framesTime;
    protected int targetIdx;
    private int currentFrame = 0;

    public DiscreteAnimation(DiscreteAnimation prototype) {
        super(prototype);
        framesTime = prototype.framesTime;
    }

    public DiscreteAnimation(String name, float[] framesTime) {
        super(name);
        this.framesTime = framesTime;
    }

    @Override
    public void update(Entity[] entities, Transform[] transforms, float delta) {
        if (!run) return;
        if (pause && (pauseTimer += delta) >= pauseTime) {
            delta = pauseTimer - pauseTime;
            frameChanged(entities, currentFrame);
            pauseTimer = 0;
            pause = false;
        }
        if (!pause) {
            if (!back) {
                time += delta;
                if (time >= framesTime[currentFrame]) {
                    if (++currentFrame == framesTime.length) {
                        if (needBackMove) {
                            back = true;
                            currentFrame -= 2;
                            time = framesTime[currentFrame] - (framesTime[currentFrame+1] - time);
                            frameChanged(entities, currentFrame);
                        } else if (loop) {
                            time = 0;
                            currentFrame = 0;
                            if(pauseTime > 0) pause = true;
                            else frameChanged(entities, currentFrame);
                        } else run = false;
                    } else frameChanged(entities, currentFrame);
                }
            } else {
                time -= delta;
                if (currentFrame >= 1) {
                    if (time <= framesTime[currentFrame - 1]) {
                        currentFrame--;
                        frameChanged(entities, currentFrame);
                    }
                } else {
                    if(time <= 0) {
                        back = false;
                        run = loop;
                        if(run) {
                            currentFrame = 1;
                            if(pauseTime > 0) pause = true;
                            else frameChanged(entities, currentFrame);
                            time = framesTime[0]-time;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void start(Entity[] entities, Transform[] transforms) {
        super.start(entities, transforms);
        frameChanged(entities, 0);
        back = false;
        currentFrame = 0;
    }

    @Override
    public void reset(Animation prototype) {
        super.reset(prototype);
        currentFrame = 0;
    }

    public abstract void frameChanged(Entity[] entities, int newFrameIdx);

    public void setTargetIdx(int targetIdx) {
        this.targetIdx = targetIdx;
    }
}
