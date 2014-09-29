package ru.sipaha.engine.core.animation.сontinuous;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.gameobjectdata.Transform;

/**
 * Created on 18.09.2014.
 */

public abstract class ContinuousAnimatedUnit {

    public ContinuousAnimation continuousAnimation;
    protected int targetIdx = 0;

    public abstract void update(Entity[] entities, Transform[] transforms, float time);

    public abstract void start(Entity[] entities, Transform[] transforms);

    public abstract float getMaxDefinedTime();

    public void setTargetIdx(int targetIdx) {
        this.targetIdx = targetIdx;
    }
}
