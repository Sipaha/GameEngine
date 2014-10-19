package ru.sipaha.engine.core.animation.сontinuous;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.curves.Curve;

/**
 * Created on 19.09.2014.
 */

public class AnimatedVOffset extends AnimatedFloat {

    public AnimatedVOffset(Curve[] curves) {
        super(curves);
    }

    @Override
    public void update(Entity[] entities, Transform[] transforms, float time) {
        entities[targetIdx].setOffsetV(curves[0].get(time));
    }

    @Override
    public void start(Entity[] entities, Transform[] transforms) {
        Entity entity = entities[targetIdx];
        if(entity.animatedVOffset != this && entity.animatedVOffset != null) {
            entity.animatedVOffset.continuousAnimation.stop();
        }
        entity.animatedVOffset = this;
    }
}
