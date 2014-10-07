package ru.sipaha.engine.scripts;

import com.badlogic.gdx.math.MathUtils;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.gameobjectdata.Transform;

/**
 * Created on 03.10.2014.
 */

public class AngleTracking extends Script {

    private String trackingTransformName;
    private Transform trackingTransform;
    private AngleTracking template;
    private Search search;

    private float idleTimer;
    private float idleLimit = 4;

    public AngleTracking(){}

    public AngleTracking(String trackingTransformName) {
        this.trackingTransformName = trackingTransformName;
    }

    @Override
    public void start(Engine engine) {
        trackingTransform = gameObject.getTransform(trackingTransformName);
        search = gameObject.getScript(Search.class);
    }

    @Override
    public void fixedUpdate(float delta) {
        if(search != null && search.target != null) {
            float angle = calcAngle(trackingTransform, search.target.transform, search.distance);
            gameObject.transform.motion.rotateTo(angle);
            idleTimer = 0;
        } else {
            if(updateInactive(delta)) {
                float randAngle = MathUtils.random(-160, 160);
                while (Math.abs(randAngle) < 40) randAngle = MathUtils.random(-120, 160);
                trackingTransform.motion.rotateTo(trackingTransform.getAngle() + randAngle);
            }
        }
    }

    private boolean updateInactive(float delta) {
        if(!trackingTransform.motion.haveATarget && (idleTimer+=delta) >= idleLimit) {
            idleTimer -= idleLimit;
            return true;
        }
        return false;
    }

    private float calcAngle(Transform from, Transform to, float distance) {
        float dx = to.tx - from.tx;
        float dy = to.ty - from.ty;

        float acos = (float) Math.acos(dy / distance);
        float radiansAngle = (dx < 0 ? 1 : -1) * acos;
        return radiansAngle * MathUtils.radiansToDegrees;
    }

    public AngleTracking(AngleTracking prototype) {
        template = prototype;
    }

    @Override
    public Script reset() {
        return this;
    }
}
