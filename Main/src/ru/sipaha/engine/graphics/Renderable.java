package ru.sipaha.engine.graphics;

import ru.sipaha.engine.utils.structures.Bounds;

/**
 * Created on 01.11.2014.
 */

public abstract class Renderable {

    protected boolean renderDataChanged = false;
    protected int offset = -1;
    protected float[] renderData;

    public void setRenderData(RenderBuffer buffer) {
        offset = buffer.getAndAddSize(getRenderSize());
        renderData = buffer.getBuffer();
        renderDataChanged = true;
    }

    public int setRenderData(float[] data, int offset) {
        this.offset = offset;
        this.renderData = data;
        renderDataChanged = true;
        return offset+getRenderSize();
    }

    public abstract void render(RenderBuffer buffer);

    public abstract int getRenderSize();

    public Bounds getBounds() {
        return null;
    }
}
