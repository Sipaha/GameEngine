package ru.sipaha.engine.core;

import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.utils.MathHelper;

public class Motion {

    public float xy_velocity = 20f;
    public float vx = 0f;
    public float vy = 0f;
    public float a_velocity = 45f;
    public float va = 0;

    public float xTarget = -1;
    public float yTarget = -1;
    public float aTarget = -1;
    public boolean xyTargetIsAdded = false;
    public boolean aTargetIsAdded = false;

    public boolean haveXYTarget = false;
    public boolean haveATarget = false;

    public boolean move_forward = false;

    public Motion() {}

    public Motion(Motion prototype) {
        reset(prototype);
    }

    public void moveTo(Vector2 v) {
        moveTo(v.x, v.y);
    }

    public void moveTo(float x, float y) {
        xTarget = x;
        yTarget = y;
        xyTargetIsAdded = true;
    }

    public void rotateTo(float angle) {
        aTarget = MathHelper.angle360Limit(angle);
        aTargetIsAdded = true;
    }

    public void mul(Transform t) {
        float tx = vx;
        float ty = vy;
        vx = tx * t.t00 + ty * t.t01;
        vy = tx * t.t10 + ty * t.t11;
    }

    public void update(Transform t, float delta) {

        if(xyTargetIsAdded) {
            if(!move_forward) {
                float vecX = xTarget - t.x.value;
                float vecY = yTarget - t.y.value;
                float length = (float)Math.sqrt(vecX*vecX + vecY*vecY);
                vx = vecX / length;
                vy = vecY / length;
            }
            xyTargetIsAdded = false;
            haveXYTarget = true;
        }

        if(aTargetIsAdded) {
            float direct = aTarget - t.absAngle;
            if(direct != 0) {
                float absDirect = Math.abs(direct);
                float back = 360 - absDirect;
                float directSign = Math.signum(direct);
                va = Math.abs(direct) < Math.abs(back) ? directSign : -directSign;
                aTargetIsAdded = false;
                haveATarget = true;
            } else haveATarget = false;
        }

        float da = va * a_velocity * delta;
        if(da != 0) {
            float angle = t.angle.value;
            if(haveATarget) {
                float absDelta = Math.abs(da);
                float absDistance = Math.abs(aTarget - t.absAngle);
                if(absDelta >= absDistance || absDelta >= (360-absDistance)) {
                    angle = aTarget;
                    haveATarget = false;
                    va = 0;
                } else {
                    angle += da;
                }
            } else angle += da;

            t.angle.value = MathHelper.angle360Limit(angle);
            t.updateAngle();
        }

        if(move_forward) {
            vx = -t.sin;
            vy = t.cos;
        }

        if(vx != 0 || vy != 0) {
            float deltaXY = delta * xy_velocity;
            t.translate(vx * deltaXY, vy * deltaXY);
            if(haveXYTarget) {
                float vecX = xTarget - t.x.value;
                float vecY = yTarget - t.y.value;
                if(vecX * vx + vecY * vy <= 0) {
                    vx = 0;
                    vy = 0;
                    haveXYTarget = false;
                }
            }
        }
    }

    public void reset(Motion source) {
        xy_velocity = source.xy_velocity;
        vx = source.vx;
        vy = source.vy;
        a_velocity = source.a_velocity;
        va = source.va;

        xTarget = source.xTarget;
        yTarget = source.yTarget;
        aTarget = source.aTarget;
        xyTargetIsAdded = source.xyTargetIsAdded;
        aTargetIsAdded = source.aTargetIsAdded;

        haveXYTarget = source.haveXYTarget;
        haveATarget = source.haveATarget;

        move_forward = source.move_forward;
    }
}
