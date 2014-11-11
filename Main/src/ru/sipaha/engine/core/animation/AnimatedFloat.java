package ru.sipaha.engine.core.animation;

import ru.sipaha.engine.core.LinkedValue;
import ru.sipaha.engine.core.Values;
import ru.sipaha.engine.utils.functions.Function1f1f;

/**
 * Created on 02.11.2014.
 */

public class AnimatedFloat extends LinkedValue<Values.Float> implements LinkedAnimatedValue {

    private Function1f1f function1f1f;

    public AnimatedFloat(Values.Float value, Function1f1f function1f1f) {
        super(value);
        this.function1f1f = function1f1f;
    }

    public AnimatedFloat(AnimatedFloat prototype) {
        super(prototype);
        function1f1f = prototype.function1f1f;
    }

    public AnimatedFloat copy() {
        return new AnimatedFloat(this);
    }

    @Override
    public void update(float time) {
        value.set(function1f1f.get(time));

    }

    @Override
    public float getMaxDefinedTime() {
        return function1f1f.getMaxDefinedArgument();
    }
}
