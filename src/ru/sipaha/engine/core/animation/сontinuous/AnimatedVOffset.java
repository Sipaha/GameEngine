package ru.sipaha.engine.core.animation.сontinuous;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.gameobjectdata.EntityRenderer;
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
        entities[targetIdx].renderer.setOffsetV(curves[0].get(time));
    }

    @Override
    public void start(Entity[] entities, Transform[] transforms) {
        EntityRenderer renderer = entities[targetIdx].renderer;
        if(renderer.animatedVOffset != this && renderer.animatedVOffset != null) {
            renderer.animatedVOffset.continuousAnimation.stop();
        }
        renderer.animatedVOffset = this;
    }
}
