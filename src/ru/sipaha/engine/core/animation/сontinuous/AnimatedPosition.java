package ru.sipaha.engine.core.animation.сontinuous;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.curves.Curve;

/**
 * Created on 19.09.2014.
 */

public class AnimatedPosition extends AnimatedFloat {

    public AnimatedPosition(Curve[] curves) {
        super(curves);
    }

    @Override
    public void update(Entity[] entities, Transform[] transforms, float time) {
        transforms[targetIdx].setPosition(curves[0].get(time), curves[1].get(time));
    }

    @Override
    public void start(Entity[] entities, Transform[] transforms) {
        Transform transform = transforms[targetIdx];
        if(transform.animatedPosition != this && transform.animatedPosition != null) {
            transform.animatedPosition.continuousAnimation.stop();
        }
        transform.animatedPosition = this;
    }
}
