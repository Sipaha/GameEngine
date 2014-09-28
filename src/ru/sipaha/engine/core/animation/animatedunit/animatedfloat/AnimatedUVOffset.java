package ru.sipaha.engine.core.animation.animatedunit.animatedfloat;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.gameobjectdata.EntityRenderer;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.curves.Curve;

/**
 * Created on 19.09.2014.
 */

public class AnimatedUVOffset extends AnimatedFloat {

    public AnimatedUVOffset(Curve[] curves) {
        super(curves);
    }

    @Override
    public void update(Entity[] entities, Transform[] transforms, float time) {
        entities[id].renderer.setOffsetUV(curves[0].get(time), curves[1].get(time));
    }

    @Override
    public void start(Entity[] entities, Transform[] transforms) {
        EntityRenderer renderer = entities[id].renderer;
        if(renderer.animatedUVOffset != this && renderer.animatedUVOffset != null) {
            renderer.animatedUVOffset.animation.stop();
        }
        renderer.animatedUVOffset = this;
    }
}
