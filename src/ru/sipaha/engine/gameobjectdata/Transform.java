package ru.sipaha.engine.gameobjectdata;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.core.animation.сontinuous.AnimatedPosition;
import ru.sipaha.engine.core.animation.сontinuous.AnimatedScale;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Transform {
    public float t00, t01, t10, t11, tx, ty;
    public boolean wasChanged;
    public boolean childUpdateRequest;
    public int parentId = 0;
    public Motion motion;
    public RigidBody rigidBody;

    protected float x = 0, y = 0;
    protected float scaleX = 1, scaleY = 1;

    protected float angle = 0;
    protected float absAngle = 0;
    protected float cos = 1, sin = 0;

    protected boolean dirty = true;

    private Vector2 positionTemp;

    public AnimatedPosition animatedPosition = null;
    public AnimatedScale animatedScale = null;

    private boolean unhooked = false;

    public Transform(){
        motion = new Motion();
    }

    public Transform(Transform prototype) {
        motion = new Motion(prototype.motion);
        parentId = prototype.parentId;
        reset(prototype);
    }

    public void update(float delta) {
        if(unhooked) {
            wasChanged = true;
            childUpdateRequest = true;
            unhooked = false;
        } else {
            if(rigidBody != null) {
                if(rigidBody.manualMoving) {
                    motion.update(this, delta);
                    if(dirty) updateData();
                    if(wasChanged) rigidBody.setTransform(x, y, angle);
                } else {
                    Vector2 v = rigidBody.getPosition();
                    x = v.x;
                    y = v.y;
                    angle = rigidBody.getAngle();
                    updateData();
                }
            } else {
                motion.update(this, delta);
                if(dirty) updateData();
            }
        }
    }

    private void updateData() {
        absAngle = angle;
        t00 = cos*scaleX;
        t01 = -sin;
        t10 = sin;
        t11 = cos*scaleY;
        tx = x; ty = y;
        dirty = false;
        wasChanged = true;
        childUpdateRequest = true;
    }

    public void update(Transform parent, float delta) {
        motion.update(this, delta);
        if(dirty || parent.childUpdateRequest) {
            updateData();
            mul(parent);
        }
    }

    public void unhook(Transform parent) {
        updateData();
        mul(parent);
        angle = absAngle;
        scaleX = parent.scaleX*scaleX;
        scaleY = parent.scaleY*scaleY;
        sin = t10;
        cos = MathUtils.cosDeg(angle);
        x = tx;
        y = ty;
        unhooked = true;
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

    public float getAngle() {
        return absAngle;
    }

    public Transform setPosition(double x, double y) {
        return setPosition((float)x, (float)y);
    }

    public Transform setPosition(float x, float y) {
        translate(x - this.x, y - this.y);
        return this;
    }

    public void setScale(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
        t00 = cos*scale;
        t11 = t00;
        wasChanged = true;
        childUpdateRequest = true;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
        t00 = cos*scaleX;
        wasChanged = true;
        childUpdateRequest = true;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
        t11 = cos*scaleY;
        wasChanged = true;
        childUpdateRequest = true;
    }

    public Vector2 getPosition() {
        if(positionTemp == null) positionTemp = new Vector2();
        positionTemp.set(x, y);
        return positionTemp;
    }

    public void translate(float dx, float dy) {
        x += dx;
        y += dy;
        tx += dx;
        ty += dy;
        if(rigidBody != null) rigidBody.translate(dx, dy);
        wasChanged = true;
        childUpdateRequest = true;
    }

    public Transform reset(Transform source) {
        x = source.x;
        y = source.y;
        angle = source.angle;
        scaleX = source.scaleX;
        scaleY = source.scaleY;
        cos = source.cos;
        sin = source.sin;
        dirty = true;
        return this;
    }
}
