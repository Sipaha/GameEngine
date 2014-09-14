package ru.sipaha.engine.core.animation;

import ru.sipaha.engine.gameobjectdata.EntityRenderer;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.curves.Curve;

/**
 * Created on 15.09.2014.
 */

public class FloatAnimation extends Animation {
    public enum Category {POSITION, SCALE, ALPHA, ORIGIN}

    private Curve[] curves;
    private Category category;
    private Transform transform;
    private EntityRenderer renderer;

    private int transformIdx, entityRendererIdx;

    @Override
    public void updateTarget(float time) {
        switch (category) {
            case POSITION:
                transform.setPosition(curves[0].get(time), curves[1].get(time));
                break;
            case SCALE:
                transform.setScale(curves[0].get(time));
                break;
            case ALPHA:
                renderer.setAlpha(curves[0].get(time));
                break;
            case ORIGIN:
                renderer.setOrigin(curves[0].get(time), curves[1].get(time));
                break;
        }
    }

    public void setData(Transform t, EntityRenderer r) {
        transform = t;
        renderer = r;
    }
}
