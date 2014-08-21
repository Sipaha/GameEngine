package ru.sipaha.engine.gameobjectdata;

import com.badlogic.gdx.math.MathUtils;

public class Transform {
    public float t00, t01, t10, t11, tx, ty;

    protected float x = 0, y = 0;
    protected float angle = 0;
    protected float scale = 1;

    protected float absAngle = 0;
    protected float cos = 1, sin = 0;

    protected boolean wasChanged;
    protected boolean dirty = true;

    public Transform(){}

    public Transform(Transform prototype) {
        set(prototype);
    }

    public void update() {
        wasChanged = dirty;
        if(dirty) {
            absAngle = angle;
            t00 = cos*scale;  t01 = -sin;
            t10 = sin;        t11 = cos*scale;
            tx = x; ty = y;
            dirty = false;
        }
    }

    public void update(Transform parent) {
        dirty |= parent.wasChanged;
        update();
        if(wasChanged) mul(parent);
    }

    public void forceUpdate(Transform parent) {
        dirty = true;
        update();
        if(parent != null) mul(parent);
    }

    public void unhook() {
        angle = absAngle;
        sin = t10;//MathUtils.sinDeg(angle);
        cos = MathUtils.cosDeg(angle);
        scale = cos != 0 ? t00 / cos : 1;
        x = tx; y = ty;
    }

    protected Transform mul(Transform trn) {
        float v00 = t00 * trn.t00 + t10 * trn.t01;
        float v01 = t01 * trn.t00 + t11 * trn.t01;
        float v02 = tx * trn.t00 + ty * trn.t01 + trn.tx;

        float v10 = t00 * trn.t10 + t10 * trn.t11;
        float v11 = t01 * trn.t10 + t11 * trn.t11;
        float v12 = tx * trn.t10 + ty * trn.t11 + trn.ty;

        t00 = v00; t10 = v10;
        t01 = v01; t11 = v11;
        tx = v02; ty = v12;

        absAngle += trn.absAngle;

        return this;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        sin = MathUtils.sinDeg(angle);
        cos = MathUtils.cosDeg(angle);
        dirty = true;
    }

    public void setPosition(float x, float y) {
        translate(x - this.x, y - this.y);
    }

    public void translate(float dx, float dy) {
        x += dx;
        y += dy;
        tx += dx;
        ty += dy;
    }

    public void set(Transform source) {
        x = source.x;
        y = source.y;
        angle = source.angle;
        scale = source.scale;
        cos = source.cos;
        sin = source.sin;
    }
}
