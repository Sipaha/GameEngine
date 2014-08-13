package ru.sipaha.game_engine.core.utils;

import com.badlogic.gdx.math.MathUtils;

public class MathHelper {

    public static float DOUBLE_PI = MathUtils.PI*2;

    public static float sqrDistance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return dx*dx+dy*dy;
    }

    public static float magnitude(float x, float y) {
        return (float)Math.sqrt(x*x + y*y);
    }

    public static float angle2PILimit(float angle) {
        while(angle >= DOUBLE_PI) angle -= DOUBLE_PI;
        while(angle < 0) angle += DOUBLE_PI;
        return angle;
    }

    public static float angle360Limit(float angle) {
        while(angle >= 360f) angle -= 360f;
        while(angle < 0) angle += 360f;
        return angle;
    }

    public static float getAcuteDegAngleDiff(float a1, float a2) {
        float d = Math.abs(a2 - a1);
        float dd = 360 - d;
        return Math.min(d, dd);
    }

    public static float getMinAngleDistance(float from, float to) {
        float direct = to - from;
        float absDirect = Math.abs(direct);
        float back = 360 - absDirect;
        return absDirect < back ? direct : -Math.signum(direct)*back;
    }

    public static float getMinAngleSign(float from, float to) {
        float direct = to - from;
        float absDirect = Math.abs(direct);
        float back = 360 - absDirect;
        float directSign = Math.signum(direct);
        return Math.abs(direct) < Math.abs(back) ? directSign : -directSign;
    }
}
