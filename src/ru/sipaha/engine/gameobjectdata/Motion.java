package ru.sipaha.engine.gameobjectdata;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.utils.MathHelper;

public class Motion {

    public float xy_velocity = 0f;
    public float vx = 0f;
    public float vy = 0f;
    public float a_velocity = 0f;
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
        set(prototype);
    }

    public void moveTo(Vector2 v) {
        moveTo(v.x, v.y);
    }

    public void moveTo(float x, float y) {
        xTarget = x;
        yTarget = y;
        xyTargetIsAdded = true;
        haveXYTarget = true;
    }

    public void rotateTo(float angle) {
        aTarget = MathHelper.angle360Limit(angle);
        aTargetIsAdded = true;
        haveATarget = true;
    }

    public void mul(Transform t) {
        float tx = vx;
        float ty = vy;
        vx = tx * t.m00 + ty * t.m01;
        vy = tx * t.m10 + ty * t.m11;
    }

    /*@Override
    public void reset() {
        xTarget = -1;
        yTarget = -1;
        aTarget = -1;
        vx = template.vx;
        vy = template.vy;
        va = template.va;
        xy_velocity = template.xy_velocity;
        a_velocity = template.a_velocity;
        haveXYTarget = false;
        haveATarget = false;
        xyTargetIsAdded = false;
        aTargetIsAdded = false;
        move_forward = template.move_forward;
    }*/

    public void update(Transform t, float delta) {

        if(xyTargetIsAdded) {
            if(!move_forward) {
                float vecX = xTarget - t.x;
                float vecY = yTarget - t.y;
                float length = (float)Math.sqrt(vecX*vecX + vecY*vecY);
                vx = vecX / length;
                vy = vecY / length;
            }
            xyTargetIsAdded = false;
        }

        if(aTargetIsAdded) {
            float direct = aTarget - t.absAngle;
            float absDirect = Math.abs(direct);
            float back = 360 - absDirect;
            float directSign = Math.signum(direct);
            va = Math.abs(direct) < Math.abs(back) ? directSign : -directSign;
            aTargetIsAdded = false;
        }

        float da = va * a_velocity * delta;
        if(da != 0) {
            if(haveATarget) {
                float absDelta = Math.abs(da);
                float absDistance = Math.abs(aTarget - t.absAngle);
                if(absDelta >= absDistance || absDelta >= (360-absDistance)) {
                    t.angle = aTarget;
                    haveATarget = false;
                    va = 0;
                } else {
                    t.angle += da;
                }
            } else t.angle += da;

            t.angle = MathHelper.angle360Limit(t.angle);
            t.cos = MathUtils.cosDeg(t.angle);
            t.sin = MathUtils.sinDeg(t.angle);
        }

        if(move_forward) {
            vx = -t.sin;
            vy = t.cos;
        }

        float deltaXY = delta * xy_velocity;
        t.x += vx * deltaXY;
        t.y += vy * deltaXY;
        if(haveXYTarget) {
            float vecX = xTarget - t.x;
            float vecY = yTarget - t.y;
            if(vecX * vx + vecY * vy <= 0) {
                vx = 0;
                vy = 0;
                haveXYTarget = false;
            }
        }

        t.dirty = true;
    }

    public void set(Motion source) {
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
