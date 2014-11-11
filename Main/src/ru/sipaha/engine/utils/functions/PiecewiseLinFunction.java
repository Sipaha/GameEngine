package ru.sipaha.engine.utils.functions;


import com.badlogic.gdx.math.Vector2;

/**
 * Created on 15.09.2014.
 */

public class PiecewiseLinFunction extends Function1f1f {

    private float[] b;
    private float[] k;

    public PiecewiseLinFunction(PiecewiseLinFunction prototype) {
        super(prototype);
        b = prototype.b;
        k = prototype.k;
    }

    /**
     * @param points x = argument, y = result
     */
    public PiecewiseLinFunction(Vector2... points) {
        if(points.length < 2) {
            throw new RuntimeException("There are too few points to create a function. points.length = "+points.length);
        }
        int size = points.length-1;
        b = new float[size+1];
        k = new float[size];
        argPoints = new float[size+1];
        for(int i = 0; i < size; i++) {
            k[i] = (points[i+1].y - points[i].y) / (points[i+1].x - points[i].x);
            b[i] = points[i].y - k[i]*points[i].x;
            argPoints[i] = points[i].x;
        }
        int lastIdx = points.length - 1;
        maxDefinedArgument = points[lastIdx].x;
        b[lastIdx] = k[lastIdx-1]*maxDefinedArgument + b[lastIdx-1];
        argPoints[lastIdx] = points[lastIdx].x;
    }

    @Override
    public float get(float arg) {
        if(arg >= maxDefinedArgument) return b[b.length-1];
        if(arg <= minDefinedArgument) return b[0];
        int idx = getArgPointIdx(arg);
        return k[idx]*arg+b[idx];
    }
}
