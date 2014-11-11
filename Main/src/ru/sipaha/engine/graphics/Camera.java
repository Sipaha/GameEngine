package ru.sipaha.engine.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ru.sipaha.engine.utils.signals.Listener;
import ru.sipaha.engine.utils.signals.Signal;

public class Camera extends OrthographicCamera {

    private final Vector3 tmpVec3 = new Vector3();
    private final Vector2 tmpVec2 = new Vector2();
    private final Vector2 maxPosition = new Vector2(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    private final Vector2 minPosition = new Vector2(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    private final Vector2 maxView = new Vector2(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    private final Vector2 minView = new Vector2(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

    private final Signal<Camera> onUpdate = new Signal<>();

    public Camera() {
        setToOrtho(false);
    }

    public void setViewport(int width, int height) {
        viewportWidth = width;
        viewportHeight = height;
        position.set(width / 2f, height / 2f, 0);
        update();
        checkPosition(true);
    }

    public Vector2 unproject(float x, float y) {
        tmpVec3.set(x, y, 0);
        unproject(tmpVec3);
        return tmpVec2.set(tmpVec3.x, tmpVec3.y);
    }

    public Vector2 project(float x, float y) {
        tmpVec3.set(x, y, 0);
        project(tmpVec3);
        return tmpVec2.set(tmpVec3.x, tmpVec3.y);
    }

    public void zoomChange(float dz) {
        zoom += dz*zoom;
        update();
    }

    public void setViewLimits(float minX, float minY, float maxX, float maxY) {
        minView.set(minX, minY);
        maxView.set(maxX, maxY);
        updatePositionLimits();
    }

    public void updatePositionLimits() {
        float halfWidth = viewportWidth / 2f;
        float halfHeight = viewportHeight / 2f;
        minPosition.set(minView.x + halfWidth, minView.y + halfHeight);
        maxPosition.set(maxView.x - halfWidth, maxView.y - halfHeight);
        checkPosition(true);
    }

    public void setPosition(float x, float y) {
        move(x - position.x, y - position.y);
    }

    public void setZoom(float newZoom) {
        zoomChange(1/newZoom - zoom);
    }

    public void checkPosition(boolean lazyUpdate) {
        Vector3 pos = position;
        boolean needUpdate = !lazyUpdate;
        if(pos.x > maxPosition.x) {
            pos.x = maxPosition.x;
            needUpdate = true;
        } else if(pos.x < minPosition.x) {
            pos.x = minPosition.x;
            needUpdate = true;
        }
        if(pos.y > maxPosition.y) {
            pos.y = maxPosition.y;
            needUpdate = true;
        }
        else if(pos.y < minPosition.y) {
            pos.y = minPosition.y;
            needUpdate = true;
        }
        if(needUpdate) update();
    }

    public void move(float dx, float dy) {
        position.add(dx, dy, 0);
        checkPosition(false);
    }

    public void moveWithZoom(float dx, float dy) {
        position.add(dx*zoom, dy*zoom, 0);
        checkPosition(false);
    }

    public void addOnUpdateListener(Listener<Camera> listener) {
        onUpdate.add(listener);
    }

    public void lookAtCenter() {
        float x = (maxPosition.x - minPosition.x)/2f;
        float y = (maxPosition.y - minPosition.y)/2f;
        setPosition(x, y);
    }

    @Override
    public void update() {
        super.update();
        onUpdate.dispatch(this);
    }

    public void reset() {
        zoom = 1;
        setToOrtho(false);
        onUpdate.clear();
        update();
    }
}
