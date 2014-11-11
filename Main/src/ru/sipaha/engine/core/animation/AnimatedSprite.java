package ru.sipaha.engine.core.animation;

import ru.sipaha.engine.core.LinkedValue;
import ru.sipaha.engine.core.Values;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.functions.Function1f1i;

/**
 * Created on 04.11.2014.
 */

public class AnimatedSprite extends LinkedValue<Values.FloatArray> implements LinkedAnimatedValue {

    private Function1f1i function;
    private int currentFrame = -1;
    private Array<float[]> frames;

    public AnimatedSprite(Values.FloatArray value, Array<float[]> frames, Function1f1i function) {
        super(value);
        this.function = function;
        this.frames = frames;
    }

    public AnimatedSprite(AnimatedSprite prototype) {
        super(prototype);
        this.function = prototype.function;
        this.frames = prototype.frames;
    }

    @Override
    public void update(float time) {
        int frame = function.get(time);
        if(frame != currentFrame) {
            currentFrame = frame;
            value.set(frames.get(frame));
        }
    }

    @Override
    public float getMaxDefinedTime() {
        return function.getMaxDefinedArgument();
    }

    @Override
    public LinkedAnimatedValue copy() {
        return new AnimatedSprite(this);
    }
}
