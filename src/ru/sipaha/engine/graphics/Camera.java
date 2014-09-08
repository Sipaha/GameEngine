package ru.sipaha.engine.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Camera {

    private static final OrthographicCamera camera = new OrthographicCamera();
    private static final Vector3 tmpVec3 = new Vector3();
    private static final Vector2 tmpVec2 = new Vector2();
    private static final Vector2 maxPosition = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
    private static final Vector2 minPosition = new Vector2(Float.MIN_VALUE, Float.MIN_VALUE);
    private static final Vector2 maxView = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
    private static final Vector2 minView = new Vector2(Float.MIN_VALUE, Float.MIN_VALUE);
    public static final Matrix4 projection = camera.projection;
    public static final Matrix4 combined = camera.combined;

    private Camera(){}

    public static void setViewport(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width / 2, height / 2, 0);
        camera.update();
        checkPosition(true);
    }

    public static Vector2 unproject(float x, float y) {
        tmpVec3.set(x, y, 0);
        camera.unproject(tmpVec3);
        return tmpVec2.set(tmpVec3.x, tmpVec3.y);
    }

    public static Vector2 project(float x, float y) {
        tmpVec3.set(x, y, 0);
        camera.project(tmpVec3);
        return tmpVec2.set(tmpVec3.x, tmpVec3.y);
    }

    public static void zoomChange(float dz) {
        camera.zoom += dz;
        camera.update();
    }

    public static void setViewLimits(float minX, float minY, float maxX, float maxY) {
        minView.set(minX, minY);
        maxView.set(maxX, maxY);
        updatePositionLimits();
    }

    public static void updatePositionLimits() {
        float halfWidth = camera.viewportWidth / 2f;
        float halfHeight = camera.viewportHeight / 2f;
        minPosition.set(minView.x + halfWidth, minView.y + halfHeight);
        maxPosition.set(maxView.x - halfWidth, maxView.y - halfHeight);
        checkPosition(true);
    }

    public static void setPosition(float x, float y) {
        move(x - camera.position.x, y - camera.position.y);
    }

    public static void checkPosition(boolean lazyUpdate) {
        Vector3 pos = camera.position;
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
        if(needUpdate) camera.update();
    }

    public static void move(float dx, float dy) {
        Vector3 pos = camera.position;
        pos.add(dx, dy, 0);
        checkPosition(false);
    }

    public static void lookAtCenter() {
        float x = (maxPosition.x - minPosition.x)/2f;
        float y = (maxPosition.y - minPosition.y)/2f;
        setPosition(x, y);
    }

    public static void reset() {
        camera.zoom = 1;
        camera.update();
    }
}
