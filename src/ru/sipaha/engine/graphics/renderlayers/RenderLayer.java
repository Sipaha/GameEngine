package ru.sipaha.engine.graphics.renderlayers;

import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.graphics.RenderBuffer;
import ru.sipaha.engine.graphics.RenderUnit;

/**
 * Created on 30.09.2014.
 */

public abstract class RenderLayer implements Comparable<RenderLayer> {

    public final String name;
    public Camera camera;
    private int order;

    public RenderLayer(String name) {
        this(name, new Camera());
    }

    public RenderLayer(String name, Camera camera) {
        this.name = name;
        this.camera = camera;
    }

    public void draw() {
        render();
    }

    protected abstract void render();

    public RenderLayer setOrder(int order) {
        this.order = order;
        return this;
    }

    public RenderLayer reset() {
        camera.reset();
        return this;
    }

    public void initialize(){}
    public void add(RenderUnit renderUnit){}
    public void prepare(RenderUnit renderUnit){}
    public void remove(RenderUnit renderUnit){}

    public void resize(int width, int height) {
        camera.setViewport(width, height);
    }

    public RenderLayer setCamera(Camera camera) {
        this.camera = camera;
        return this;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(RenderLayer layer) {
        return order - layer.order;
    }
}
