package ru.sipaha.engine.core.animation.animatedunit.animatedfloat;

import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.gameobjectdata.EntityRenderer;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.curves.Curve;

/**
 * Created on 19.09.2014.
 */

public class AnimatedAlpha extends AnimatedFloat {

    public AnimatedAlpha(Curve curves) {
        super(curves);
    }

    @Override
    public void update(Entity[] entities, Transform[] transforms, float time) {
        entities[id].renderer.setAlpha(curves[0].get(time));
    }

    @Override
    public void start(Entity[] entities, Transform[] transforms) {
        EntityRenderer renderer = entities[id].renderer;
        if(renderer.animatedAlpha != this && renderer.animatedAlpha != null) {
            renderer.animatedAlpha.animation.stop();
        }
        renderer.animatedAlpha = this;
    }
}
