package ru.sipaha.engine.utils.structures;

import com.badlogic.gdx.math.Vector2;

/**
 * Created on 12.10.2014.
 */

public class Bounds {
    public final Vector2 min = new Vector2();
    public final Vector2 max = new Vector2();
    private final Vector2 temp = new Vector2();

    public Bounds() {}

    public Bounds set(Bounds bounds) {
        min.set(bounds.min);
        max.set(bounds.max);
        return this;
    }

    public Bounds set(float left, float right, float top, float bottom) {
        min.x = left;
        max.x = right;
        min.y = bottom;
        max.y = top;
        return this;
    }

    public Bounds translate(float dx, float dy) {
        min.x += dx;
        max.x += dx;
        min.y += dy;
        max.y += dy;
        return this;
    }

    public Bounds union(Bounds bounds) {
        if(!bounds.isEmpty()) {
            if(isEmpty()) {
                set(bounds);
            } else {
                min.x = Math.min(min.x, bounds.min.x);
                max.x = Math.max(max.x, bounds.max.x);
                min.y = Math.min(min.y, bounds.min.y);
                max.y = Math.max(max.y, bounds.max.y);
            }
        }
        return this;
    }

    public Vector2 getCenter() {
        return temp.set(getWidth()/2f, getHeight()/2f);
    }

    public boolean pointIn(float x, float y) {
        return x >= min.x && x <= max.x && y >= min.y && y <= max.y;
    }

    public boolean overlaps(Bounds bounds) {
        return min.x <= bounds.max.x && min.x >= bounds.min.x
                || max.x <= bounds.max.x && max.x >= bounds.min.x
                || min.y <= bounds.max.y && min.y >= bounds.min.y
                || max.y <= bounds.max.y && max.y >= bounds.min.y;
    }

    public boolean isEmpty() {
        return getWidth() == 0 && getHeight() == 0;
    }

    public float getWidth() {
        return max.x-min.x;
    }

    public float getHeight() {
        return max.y-min.y;
    }

    public void reset() {
        set(0,0,0,0);
    }
}
