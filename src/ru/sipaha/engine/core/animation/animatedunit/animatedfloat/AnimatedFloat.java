package ru.sipaha.engine.core.animation.animatedunit.animatedfloat;

import ru.sipaha.engine.core.animation.animatedunit.AnimatedUnit;
import ru.sipaha.engine.utils.curves.Curve;

/**
 * Created on 15.09.2014.
 */

public abstract class AnimatedFloat extends AnimatedUnit {

    protected final Curve[] curves;

    public AnimatedFloat(Curve[] curves) {
        this.curves = curves;
    }
}
