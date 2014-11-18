package ru.sipaha.engine.test.scripts;

import com.badlogic.gdx.math.MathUtils;
import ru.sipaha.engine.core.*;

/**
 * Created on 03.10.2014.
 */

public class AngleTracking extends Script implements TargetCatcher {

    private String trackingTransformName;
    private Motion trackObject;
    private AngleTracking template;
    private TargetHolder targetHolder;

    private float idleTimer;
    private float idleLimit = 4;

    public AngleTracking(){}

    public AngleTracking(String trackingTransformName) {
        this.trackingTransformName = trackingTransformName;
    }

    @Override
    public void start(Engine engine) {
        if(trackingTransformName != null) {
            trackObject = new Motion(gameObject.getEntity(trackingTransformName).transform);
        } else {
            trackObject = new Motion(gameObject.transform);
        }
        targetHolder = gameObject.getScript(TargetHolder.class);
    }

    @Override
    public void fixedUpdate(float delta) {
        if(targetHolder != null) {
            GameObject target = targetHolder.getTarget();
            if (target != null) {
                float distance = targetHolder.getDistanceToTarget();
                float angle = calcAngle(trackObject.transform, target.transform, distance);
                trackObject.rotateTo(angle);
                idleTimer = 0;
            } else {
                if(updateInactive(delta)) {
                    float randAngle = MathUtils.random(-160, 160);
                    while (Math.abs(randAngle) < 40) randAngle = MathUtils.random(-120, 160);
                    trackObject.rotateTo(trackObject.transform.getAbsAngle() + randAngle);
                }
            }
        }
    }

    private boolean updateInactive(float delta) {
        if(!trackObject.haveATarget && (idleTimer+=delta) >= idleLimit) {
            idleTimer -= idleLimit;
            return true;
        }
        return false;
    }

    private float calcAngle(Transform from, Transform to, float distance) {
        float dx = to.data[Transform.TX] - from.data[Transform.TX];
        float dy = to.data[Transform.TY] - from.data[Transform.TY];

        float acos = (float) Math.acos(dy / distance);
        float radiansAngle = (dx < 0 ? 1 : -1) * acos;
        return radiansAngle * MathUtils.radiansToDegrees;
    }

    public AngleTracking(AngleTracking prototype) {
        template = prototype;
    }

    @Override
    public void reset(){
    }

    @Override
    public boolean targetIsCatched() {
        return targetHolder.getTarget() != null && !trackObject.haveATarget;
    }
}
