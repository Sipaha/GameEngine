package ru.sipaha.engine.utils.functions;

import com.badlogic.gdx.math.Vector2;

/**
 * Created on 04.11.2014.
 */

public class IntFunction extends Function1f1i {

    private int[] values;

    public IntFunction(IntFunction prototype) {
        super(prototype);
        values = prototype.values;
    }

    /**
     * @param points x = argument, y = result
     */
    public IntFunction(Vector2... points) {
        if(points.length < 2) {
            throw new RuntimeException("There are too few points to create a function. points.length = "+points.length);
        }
        int size = points.length;
        values = new int[size];
        argPoints = new float[size];
        for(int i = 0; i < size; i++) {
            Vector2 point = points[i];
            argPoints[i] = point.x;
            values[i] = (int)point.y;
        }
        minDefinedArgument = argPoints[0];
        maxDefinedArgument = argPoints[size-1];
    }

    @Override
    public int get(float arg) {
        return values[getArgPointIdx(arg)];
    }
}
