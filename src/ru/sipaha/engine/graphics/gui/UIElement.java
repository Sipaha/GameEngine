package ru.sipaha.engine.graphics.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.sipaha.engine.utils.structures.Bounds;

/**
 * Created on 12.10.2014.
 */

public class UIElement {

    public boolean anchorLeft = false;
    public boolean anchorRight = false;
    public boolean anchorBottom = false;
    public boolean anchorTop = false;

    protected float width;
    protected float height;

    protected float leftPadding = 0f;
    protected float rightPadding = 0f;
    protected float topPadding = 0f;
    protected float bottomPadding = 0f;

    protected final Bounds bounds = new Bounds();

    public final UIRenderUnit renderer;

    public UIElement() {
        renderer = null;
    }

    public UIElement(TextureRegion region) {
        renderer = new UIElementRenderer(region);
        width = region.getRegionWidth();
        height = region.getRegionHeight();
    }

    public UIElement(Texture texture) {
        renderer = new UIElementRenderer(texture);
        width = texture.getWidth();
        height = texture.getHeight();
    }

    public UIElement(UIRenderUnit renderer) {
        this.renderer = renderer;
        width = bounds.getWidth();
        height = bounds.getHeight();
    }

    public void setLeftPadding(float leftPadding) {
        this.leftPadding = leftPadding;
        anchorLeft = true;
    }

    public void setRightPadding(float rightPadding) {
        this.rightPadding = rightPadding;
        anchorRight = true;
    }

    public void setBottomPadding(float bottomPadding) {
        this.bottomPadding = bottomPadding;
        anchorBottom = true;
    }

    public void setTopPadding(float topPadding) {
        this.topPadding = topPadding;
        anchorTop = true;
    }

    public void setSize(float newWidth, float newHeight) {
        width = newWidth;
        height = newHeight;
    }

    protected void setBounds(float left, float right, float top, float bottom) {
        bounds.set(left, right, top, bottom);
        if(renderer != null) renderer.setBounds(bounds.minX, bounds.maxX, bounds.maxY, bounds.minY);
    }
}
