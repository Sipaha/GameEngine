package ru.frozen.prolen.core.gameobject;

import com.badlogic.gdx.math.MathUtils;

public class Transform {
    protected float m00, m01, m02, m10, m11, m12;

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
            m00 = cos*scale;  m01 = -sin;
            m10 = sin;        m11 = cos*scale;
            m02 = x; m12 = y;
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
        sin = m10;//MathUtils.sinDeg(angle);
        cos = MathUtils.cosDeg(angle);
        scale = cos != 0 ? m00 / cos : 1;
        x = m02; y = m12;
    }

    protected Transform mul(Transform trn) {
        float v00 = m00 * trn.m00 + m10 * trn.m01;
        float v01 = m01 * trn.m00 + m11 * trn.m01;
        float v02 = m02 * trn.m00 + m12 * trn.m01 + trn.m02;

        float v10 = m00 * trn.m10 + m10 * trn.m11;
        float v11 = m01 * trn.m10 + m11 * trn.m11;
        float v12 = m02 * trn.m10 + m12 * trn.m11 + trn.m12;

        m00 = v00; m10 = v10;
        m01 = v01; m11 = v11;
        m02 = v02; m12 = v12;

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
        m02 += dx;
        m12 += dy;
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
