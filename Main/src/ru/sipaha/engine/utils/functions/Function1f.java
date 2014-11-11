package ru.sipaha.engine.utils.functions;

/**
 * Created on 04.11.2014.
 */

public abstract class Function1f {
    protected float maxDefinedArgument = 0;
    protected float minDefinedArgument = 0;
    protected float[] argPoints;
    private int cache = 0;

    public Function1f() {}

    public Function1f(Function1f prototype) {
        maxDefinedArgument = prototype.maxDefinedArgument;
        minDefinedArgument = prototype.minDefinedArgument;
        argPoints = prototype.argPoints;
        cache = prototype.cache;
    }

    public float getMaxDefinedArgument() {
        return maxDefinedArgument;
    }

    public float getMinDefinedArgument() {
        return minDefinedArgument;
    }

    public int getArgPointIdx(float arg) {
        if(arg > argPoints[cache] && arg < argPoints[cache + 1]) {
            return cache;
        }
        int low = 0;
        int high = argPoints.length - 1;

        if(arg >= argPoints[high]) return high;
        if(arg <= argPoints[low]) return low;
        while (low != high-1) {
            int mid = (low + high) >>> 1;
            float midVal = argPoints[mid];
            if (midVal <= arg){
                low = mid;
            } else if (midVal > arg) {
                high = mid;
            }
        }
        cache = low;
        return low;
    }
}
