package ru.sipaha.engine.utils.structures;

/**
 * Created on 12.10.2014.
 */

public class Bounds {
    public float minX, maxX;
    public float minY, maxY;

    /*public Bounds relativeTo(Bounds bounds) {
        return temp.set(this).translate(bounds.minX, bounds.minY);
    }*/

    public Bounds set(Bounds bounds) {
        minX = bounds.minX;
        maxX = bounds.maxX;
        minY = bounds.minY;
        maxY = bounds.maxY;
        return this;
    }

    public Bounds set(float left, float right, float top, float bottom) {
        minX = left;
        maxX = right;
        minY = bottom;
        maxY = top;
        return this;
    }

    public Bounds translate(float dx, float dy) {
        minX += dx;
        maxX += dx;
        minY += dy;
        maxY += dy;
        return this;
    }

    public float getWidth() {
        return maxX-minX;
    }

    public float getHeight() {
        return maxY-minY;
    }
}
