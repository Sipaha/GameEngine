package ru.sipaha.engine.core.animation.сontinuous;

import ru.sipaha.engine.utils.curves.Curve;

/**
 * Created on 15.09.2014.
 */

public abstract class AnimatedFloat extends ContinuousAnimatedUnit {

    protected final Curve[] curves;
    protected final float maxDefinedTime;

    public AnimatedFloat(Curve... curves) {
        this.curves = curves;
        float maxDefTime = curves[0].getMaxArgument();
        for(int i = 1; i < curves.length; i++) {
            maxDefTime = Math.max(maxDefTime, curves[i].getMaxArgument());
        }
        maxDefinedTime = maxDefTime;
    }

    @Override
    public float getMaxDefinedTime() {
        return maxDefinedTime;
    }
}
