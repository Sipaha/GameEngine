package ru.sipaha.engine.core.animation.animatedunit.animatedfloat;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.curves.Curve;

/**
 * Created on 19.09.2014.
 */

public class AnimatedScale extends AnimatedFloat {

    public AnimatedScale(Curve curve) {
        super(curve);
    }

    @Override
    public void update(Entity[] entities, Transform[] transforms, float time) {
        transforms[id].setScale(curves[0].get(time));
    }

    @Override
    public void start(Entity[] entities, Transform[] transforms) {
        Transform transform = transforms[id];
        if(transform.animatedScale != this && transform.animatedScale != null) {
            transform.animatedScale.animation.stop();
        }
        transform.animatedScale = this;
    }
}
