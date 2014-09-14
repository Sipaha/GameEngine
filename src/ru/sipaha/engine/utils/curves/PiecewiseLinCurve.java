package ru.sipaha.engine.utils.curves;

/**
 * Created on 15.09.2014.
 */

public class PiecewiseLinCurve implements Curve{

    private float[] b;
    private float[] k;
    private float[] time;

    public PiecewiseLinCurve(float[] points, float[] time) {

    }

    @Override
    public float get(float arg) {
        float h = time.length;
        float l = 0;

        return 0;
    }
}
