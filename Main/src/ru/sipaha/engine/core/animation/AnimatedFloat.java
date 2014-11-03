package ru.sipaha.engine.core.animation;

import ru.sipaha.engine.core.LinkedValue;
import ru.sipaha.engine.core.Values;
import ru.sipaha.engine.utils.curves.Curve;

/**
 * Created on 02.11.2014.
 */

public class AnimatedFloat extends LinkedValue<Values.Float> implements LinkedAnimatedValue {

    private Curve curve;

    public AnimatedFloat(Values.Float value, Curve curve) {
        super(value);
        this.curve = curve;
    }

    public AnimatedFloat(AnimatedFloat prototype) {
        super(prototype);
        curve = prototype.curve;
    }

    public AnimatedFloat copy() {
        return new AnimatedFloat(this);
    }

    @Override
    public void update(float time) {
        value.set(curve.get(time));

    }

    @Override
    public float getMaxDefinedTime() {
        return curve.getMaxArgument();
    }
}
