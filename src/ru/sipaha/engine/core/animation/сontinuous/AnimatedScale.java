package ru.sipaha.engine.core.animation.сontinuous;

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
        transforms[targetIdx].setScale(curves[0].get(time));
    }

    @Override
    public void start(Entity[] entities, Transform[] transforms) {
        Transform transform = transforms[targetIdx];
        if(transform.animatedScale != this && transform.animatedScale != null) {
            transform.animatedScale.continuousAnimation.stop();
        }
        transform.animatedScale = this;
    }
}
