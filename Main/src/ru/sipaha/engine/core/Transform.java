package ru.sipaha.engine.core;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.core.Values.Float;
import ru.sipaha.engine.core.Values.Flag;
import ru.sipaha.engine.core.Values.Bool;

public class Transform {
    protected static final int T00 = 0, T01 = 1, T10 = 2, T11 = 3, TX = 4, TY = 5;
    protected final float[] data = new float[6];
    public boolean wasChanged;
    public Transform parent;

    private final Flag positionChanged = new Flag();
    public final Float x = new Float(positionChanged);
    public final Float y = new Float(positionChanged);
    private final Flag scaleChanged = new Flag();
    public final Float scaleX = new Float(scaleChanged, 1);
    public final Float scaleY = new Float(scaleChanged, 1);
    private final Flag angleChanged = new Flag();
    public final Float angle = new Float(angleChanged);
    private final Flag dependencyChanged = new Flag();
    public final Bool dependent = new Bool(dependencyChanged, false);

    protected float absAngle = 0;
    protected float cos = 1, sin = 0;

    private Vector2 positionTemp;

    private boolean unhooked = false;
    private boolean forceUpdate = false;

    public Transform(){}

    public void update() {
        if(unhooked) {
            wasChanged = true;
            unhooked = false;
        } else {
            if(parent == null) {
                updateData();
            } else {
                forceUpdate = parent.wasChanged || dependencyChanged.value;
                if(dependent.value) {
                    if(forceUpdate) {
                        System.arraycopy(parent.data, 0, data, 0, 6);
                        wasChanged = true;
                    }
                } else {
                    updateData();
                    if(wasChanged) mul(parent);
                }
                dependencyChanged.value = false;
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
        forceUpdate = false;
    }

    protected void updateAngle() {
        sin = MathUtils.sinDeg(angle.value);
        cos = MathUtils.cosDeg(angle.value);
        data[T00] = cos*scaleX.value;
        data[T01] = -sin*scaleX.value;
        data[T10] = sin*scaleY.value;
        data[T11] = cos*scaleY.value;
        absAngle = angle.value;
        angleChanged.value = false;
        scaleChanged.value = false;
        wasChanged = true;
    }

    protected void updatePosition() {
        data[TX] = x.value;
        data[TY] = y.value;
        positionChanged.value = false;
        wasChanged = true;
    }

    protected void updateScale() {
        data[T00] = cos*scaleX.value;
        data[T01] = -sin*scaleX.value;
        data[T10] = sin*scaleY.value;
        data[T11] = cos*scaleY.value;
        scaleChanged.value = false;
        wasChanged = true;
    }

    public void unhook(Transform parent) {
        updateData();
        mul(parent);
        angle.value = absAngle;
        scaleX.value = parent.scaleX.value*scaleX.value;
        scaleY.value = parent.scaleY.value*scaleY.value;
        sin = data[T10];
        cos = MathUtils.cosDeg(angle.value);
        x.value = data[TX];
        y.value = data[TY];
        unhooked = true;
    }

    protected Transform mul(Transform trn) {
        float v00 = data[T00] * trn.data[T00] + data[T10] * trn.data[T01];
        float v01 = data[T01] * trn.data[T00] + data[T11] * trn.data[T01];
        float v02 = data[TX] * trn.data[T00] + data[TY] * trn.data[T01] + trn.data[TX];

        float v10 = data[T00] * trn.data[T10] + data[T10] * trn.data[T11];
        float v11 = data[T01] * trn.data[T10] + data[T11] * trn.data[T11];
        float v12 = data[TX] * trn.data[T10] + data[TY] * trn.data[T11] + trn.data[TY];

        data[T00] = v00; data[T10] = v10;
        data[T01] = v01; data[T11] = v11;
        data[TX] = v02; data[TY] = v12;

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

    public Transform translate(Vector2 delta) {
        x.add(delta.x);
        y.add(delta.y);
        return this;
    }

    public Vector2 getPosition() {
        if(positionTemp == null) positionTemp = new Vector2();
        positionTemp.set(x.value, y.value);
        return positionTemp;
    }

    public Transform reset(Transform prototype) {
        x.set(prototype.x);
        y.set(prototype.y);
        angle.set(prototype.angle);
        scaleX.set(prototype.scaleX);
        scaleY.set(prototype.scaleY);
        return this;
    }
}
