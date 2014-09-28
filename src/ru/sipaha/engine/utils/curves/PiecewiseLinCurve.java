package ru.sipaha.engine.utils.curves;


/**
 * Created on 15.09.2014.
 */

public class PiecewiseLinCurve implements Curve{

    private float[] b;
    private float[] k;
    private float[] time;

    public PiecewiseLinCurve(float[] points, float[] args) {
        String errorMsg = null;
        if(points.length < 2 || args.length < 2) errorMsg = "There are too few points to create a curve";
        if(points.length != args.length) errorMsg = "Count of points and arguments must be equal";
        if(errorMsg != null) {
            throw new RuntimeException(errorMsg + ". points.length = "+points.length+" args.length = "+args.length);
        }

    }

    @Override
    public float get(float arg) {
        int low = 0;
        int high = time.length - 1;

        if(arg >= time[high]) return k[high]*arg+b[high];
        if(arg <= time[low]) return k[low]*arg+b[low];
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
        return time[time.length-1];
    }
}
