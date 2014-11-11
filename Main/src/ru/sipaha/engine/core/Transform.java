package ru.sipaha.engine.core;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.gameobjectdata.RigidBody;

public class Transform {
    public float t00, t01, t10, t11, tx, ty;
    public boolean meshUpdateRequest;
    public boolean wasChanged;
    public Motion motion;

    public RigidBody rigidBody;

    private final Values.Flag positionChanged = new Values.Flag();
    public final Values.Float x = new Values.Float(positionChanged);
    public final Values.Float y = new Values.Float(positionChanged);
    private final Values.Flag scaleChanged = new Values.Flag();
    public final Values.Float scaleX = new Values.Float(scaleChanged, 1);
    public final Values.Float scaleY = new Values.Float(scaleChanged, 1);
    private final Values.Flag angleChanged = new Values.Flag();
    public final Values.Float angle = new Values.Float(angleChanged);
    protected float absAngle = 0;
    protected float cos = 1, sin = 0;

    private Vector2 positionTemp;

    private boolean unhooked = false;
    private boolean forceUpdate = false;

    public Transform(){
        motion = new Motion();
    }

    public Transform(Transform prototype) {
        motion = new Motion(prototype.motion);
        reset(prototype);
    }

    public void update(Transform parent, float delta) {
        if(unhooked) {
            meshUpdateRequest = true;
            wasChanged = true;
            unhooked = false;
        } else {
            if (rigidBody != null && !rigidBody.manualMoving) {
                setPosition(rigidBody.getPosition());
                angle.set(rigidBody.getAngle());
            } else {
                motion.update(this, delta);
            }
            if(parent == null) {
                updateData();
            } else {
                forceUpdate = parent.wasChanged;
                updateData();
                if(wasChanged) mul(parent);
            }
        }
    }

    private void updateData() {
        if(forceUpdate || positionChanged.value) {
            updatePosition();
        }
        if(forceUpdate || angleChanged.value) {
            updateAngle();
        } else if(scaleChanged.value) {
            updateScale();
        }
        meshUpdateRequest |= wasChanged;
        forceUpdate = false;
    }

    protected void updateAngle() {
        sin = MathUtils.sinDeg(angle.value);
        cos = MathUtils.cosDeg(angle.value);
        t00 = cos*scaleX.value;
        t01 = -sin;
        t10 = sin;
        t11 = cos*scaleY.value;
        absAngle = angle.value;
        angleChanged.value = false;
        scaleChanged.value = false;
        wasChanged = true;
    }

    protected void updatePosition() {
        tx = x.value;
        ty = y.value;
        if(rigidBody != null && rigidBody.manualMoving) {
            rigidBody.setTransform(tx, ty, angle.value);
        }
        positionChanged.value = false;
        wasChanged = true;
    }

    protected void updateScale() {
        t00 = cos * scaleX.value;
        t11 = cos * scaleY.value;
        scaleChanged.value = false;
        wasChanged = true;
    }

    public void unhook(Transform parent) {
        updateData();
        mul(parent);
        angle.value = absAngle;
        scaleX.value = parent.scaleX.value*scaleX.value;
        scaleY.value = parent.scaleY.value*scaleY.value;
        sin = t10;
        cos = MathUtils.cosDeg(angle.value);
        x.value = tx;
        y.value = ty;
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

    public float getAbsAngle() {
        return absAngle;
    }

    public Transform setPosition(Vector2 position) {
        x.set(position.x);
        y.set(position.y);
        return this;
    }

    public Transform setPosition(float x, float y) {
        this.x.set(x);
        this.y.set(y);
        return this;
    }

    public Transform setScale(float scale) {
        scaleX.set(scale);
        scaleY.set(scale);
        return this;
    }

    public Transform translate(float dx, float dy) {
        x.add(dx);
        y.add(dy);
        return this;
    }

    public Vector2 getPosition() {
        if(positionTemp == null) positionTemp = new Vector2();
        positionTemp.set(x.value, y.value);
        return positionTemp;
    }

    public Transform reset(Transform source) {
        motion.reset(source.motion);
        x.set(source.x);
        y.set(source.y);
        angle.set(source.angle);
        scaleX.set(source.scaleX);
        scaleY.set(source.scaleY);
        return this;
    }
}
