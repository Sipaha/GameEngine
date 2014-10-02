package ru.sipaha.engine.graphics.renderlayers;

import ru.sipaha.engine.graphics.Camera;

/**
 * Created on 30.09.2014.
 */

public abstract class RenderLayer implements Comparable<RenderLayer> {
    private static int orderCounter = 0;

    public final String name;
    public final Camera camera;
    private int order = orderCounter++;

    public RenderLayer(String name) {
        this(name, new Camera());
    }

    public RenderLayer(String name, Camera camera) {
        this.name = name;
        if(camera != null) {
            this.camera = camera;
        } else {
            this.camera = new Camera();
        }
    }

    public abstract void render();

    public void setOrder(int order) {
        this.order = order;
    }

    public void reset() {
        camera.reset();
    }

    public void initialize(){}

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(RenderLayer layer) {
        return order - layer.order;
    }
}
