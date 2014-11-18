package ru.sipaha.engine.utils.structures;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created on 12.10.2014.
 */

public class Bounds {
    public final Vector2 min = new Vector2();
    public final Vector2 max = new Vector2();
    private final Vector2 temp = new Vector2();
    private boolean empty = true;

    public Bounds() {}

    public Bounds set(Bounds bounds) {
        min.set(bounds.min);
        max.set(bounds.max);
        empty = false;
        return this;
    }

    public Bounds set(float left, float right, float top, float bottom) {
        min.x = left;
        max.x = right;
        min.y = bottom;
        max.y = top;
        empty = false;
        return this;
    }

    public Bounds set(Vector2 p1, Vector2 p2) {
        min.x = Math.min(p1.x, p2.x);
        min.y = Math.min(p1.y, p2.y);
        max.x = Math.max(p1.x, p2.x);
        max.y = Math.max(p1.y, p2.y);
        empty = false;
        return this;
    }

    /*public Bounds check() {
        float x1 = min.x;
        float x2 = max.x;
        float y1 = min.y;
        float y2 = max.y;
        min.x = Math.min(x1, x2);
        min.y = Math.min(y1, y2);
        max.x = Math.max(x1, x2);
        max.y = Math.max(y1, y2);
        return this;
    }*/

    public Bounds translate(float dx, float dy) {
        min.x += dx;
        max.x += dx;
        min.y += dy;
        max.y += dy;
        empty = false;
        return this;
    }

    public Bounds union(Bounds bounds) {
        if(bounds != null && !bounds.isEmpty()) {
            if(isEmpty()) {
                set(bounds);
            } else {
                min.x = Math.min(min.x, bounds.min.x);
                max.x = Math.max(max.x, bounds.max.x);
                min.y = Math.min(min.y, bounds.min.y);
                max.y = Math.max(max.y, bounds.max.y);
            }
            empty = false;
        }
        return this;
    }

    public void expand(Vector2 point) {
        if(isEmpty()) {
            max.set(point);
            min.set(point);
        } else {
            max.x = Math.max(max.x, point.x);
            max.y = Math.max(max.y, point.y);
            min.x = Math.min(min.x, point.x);
            min.y = Math.min(min.y, point.y);
        }
        empty = false;
    }

    public void expand(float x, float y) {
        expand(temp.set(x,y));
    }

    public Vector2 getCenter() {
        return temp.set(getWidth()/2f, getHeight()/2f);
    }

    public boolean pointIn(float x, float y) {
        return !empty && x >= min.x && x <= max.x && y >= min.y && y <= max.y;
    }

    public boolean pointIn(Vector2 point) {
        return pointIn(point.x, point.y);
    }

    public boolean overlaps(Bounds bounds) {
        return !empty && !bounds.empty &&
                (min.x <= bounds.max.x && min.x >= bounds.min.x
                || max.x <= bounds.max.x && max.x >= bounds.min.x
                || min.y <= bounds.max.y && min.y >= bounds.min.y
                || max.y <= bounds.max.y && max.y >= bounds.min.y);
    }

    public boolean overlapsHalfOf(Bounds bounds) {
        if(empty || bounds.empty) return false;
        float halfWidth = getWidth()/2;
        float halfHeight = getHeight()/2;
        if(pointIn(bounds.min.x, bounds.min.y)) {
            return pointIn(bounds.min.x+halfWidth, bounds.min.y+halfHeight);
        } else if(pointIn(bounds.min.x, bounds.max.y)) {
            return pointIn(bounds.min.x+halfWidth, bounds.max.y-halfHeight);
        } else if(pointIn(bounds.max.x, bounds.min.y)) {
            return pointIn(bounds.max.x-halfWidth, bounds.min.y+halfHeight);
        } else if(pointIn(bounds.max.x, bounds.max.y)) {
            return pointIn(bounds.max.x-halfWidth, bounds.max.y-halfHeight);
        }
        return false;
    }

    public boolean isEmpty() {
        return empty;
    }

    public float getWidth() {
        return max.x-min.x;
    }

    public float getHeight() {
        return max.y-min.y;
    }

    public void reset() {
        set(0,0,0,0);
        empty = true;
    }

    public static boolean pointIn(Vector2 min, Vector2 max, Vector2 point) {
        return point.x >= min.x && point.x <= max.x && point.y >= min.y && point.y <= max.y;
    }
}
