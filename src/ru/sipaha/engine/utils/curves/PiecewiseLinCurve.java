package ru.sipaha.engine.utils.curves;


import com.badlogic.gdx.math.Vector2;

/**
 * Created on 15.09.2014.
 */

public class PiecewiseLinCurve implements Curve{

    private float[] b;
    private float[] k;
    private float[] time;
    private float maxDefinedArg;

    /**
     * @param points x = argument, y = result
     */
    public PiecewiseLinCurve(Vector2... points) {
        if(points.length < 2) {
            throw new RuntimeException("There are too few points to create a curve. points.length = "+points.length);
        }
        int size = points.length-1;
        b = new float[size+1];
        k = new float[size];
        time = new float[size+1];
        for(int i = 0; i < size; i++) {
            k[i] = (points[i+1].y - points[i].y) / (points[i+1].x - points[i].x);
            b[i] = points[i].y - k[i]*points[i].x;
            time[i] = points[i].x;
        }
        int lastIdx = points.length - 1;
        maxDefinedArg = points[lastIdx].x;
        b[lastIdx] = k[lastIdx-1]*maxDefinedArg + b[lastIdx-1];
        time[lastIdx] = points[lastIdx].x;
    }

    @Override
    public float get(float arg) {
        int low = 0;
        int high = time.length - 1;

        if(arg >= time[high]) return b[high];
        if(arg <= time[low]) return b[low];
        while (low != high-1) {
            int mid = (low + high) >>> 1;
            float midVal = time[mid];
            if (midVal <= arg){
                low = mid;
            } else if (midVal > arg) {
                high = mid;
            }
        }
        return k[low]*arg+b[low];
    }

    @Override
    public float getMaxArgument() {
        return maxDefinedArg;
    }
}
