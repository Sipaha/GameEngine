package ru.frozen.prolen.core.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by Sipaha on 10.05.2014
 */
public class Camera extends OrthographicCamera {
    public void setViewport(int width, int height) {
        viewportWidth = width;
        viewportHeight = height;
        position.set(width/2, height/2, 0);
        update();
    }
}
