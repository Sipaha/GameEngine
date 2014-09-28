package ru.sipaha.engine.core.animation.animatedunit;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.core.animation.Animation;
import ru.sipaha.engine.gameobjectdata.Transform;

/**
 * Created on 18.09.2014.
 */

public abstract class AnimatedUnit {

    public Animation animation;
    protected int id = 0;

    public abstract void update(Entity[] entities, Transform[] transforms, float time);

    public abstract void start(Entity[] entities, Transform[] transforms);

    public abstract float getMaxDefinedTime();
}
