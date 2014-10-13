package ru.sipaha.engine.graphics.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.sipaha.engine.utils.Array;

/**
 * Created on 12.10.2014.
 */

public class UIContainer extends UIElement {

    private Array<UIElement> children = new Array<>(UIElement.class);

    protected UIContainer() {}

    protected UIContainer(TextureRegion region) {
        super(region);
    }

    protected UIContainer(Texture texture) {
        super(texture);
    }

    @Override
    protected void setBounds(float left, float right, float top, float bottom) {
        float width = bounds.maxX - bounds.minX;
        float height = bounds.maxY - bounds.minY;
        float newWidth = right - left;
        float newHeight = top - bottom;

        float wRatio = newWidth / (width==0?1:width);
        float hRatio = newHeight / (height==0?1:height);

        for (UIElement element : children) {
            float newLeft = 0, newRight = 0, newTop = 0, newBottom = 0;

            if(element.anchorLeft) {
                newLeft = left + element.leftPadding;
                if(!element.anchorRight) {
                    newRight = newLeft + element.width;
                }
            }
            if(element.anchorRight) {
                newRight = right - element.rightPadding;
                if(!element.anchorLeft) {
                    newLeft = newRight - element.width;
                }
            }
            if(!element.anchorLeft && !element.anchorRight) {
                float halfWidth = (element.bounds.maxX - element.bounds.minX) / 2f;
                float center = (element.bounds.minX + halfWidth) * wRatio;
                newLeft = left + center - halfWidth;
                newRight = left + center + halfWidth;
            }

            if(element.anchorBottom) {
                newBottom = bottom + element.bottomPadding;
                if(!element.anchorTop) {
                    newTop = newBottom + element.height;
                }
            }
            if(element.anchorTop) {
                newTop = top - element.topPadding;
                if(!element.anchorBottom) {
                    newBottom = newTop - element.height;
                }
            }
            if(!element.anchorBottom && !element.anchorTop) {
                float halfHeight = (element.bounds.maxY - element.bounds.minY) / 2f;
                float center = (element.bounds.minY + halfHeight) * hRatio;
                newTop = bottom + center + halfHeight;
                newBottom = bottom + center - halfHeight;
            }

            element.setBounds(newLeft, newRight, newTop, newBottom);
        }

        super.setBounds(left, right, top, bottom);
    }

    public void add(UIElement element) {
        children.add(element);

        float left = bounds.minX + element.leftPadding;
        float right = bounds.maxX - element.rightPadding;
        float top = bounds.maxY - element.topPadding;
        float bottom = bounds.minY + element.bottomPadding;

        if(element.anchorLeft && !element.anchorRight) right = left + element.width;
        if(element.anchorRight && !element.anchorLeft) left = right - element.width;
        if(element.anchorTop && !element.anchorBottom) bottom = top - element.height;
        if(element.anchorBottom && !element.anchorTop) top = bottom + element.height;

        element.setBounds(left, right, top, bottom);
    }

    public void remove(UIElement element) {
        children.removeValue(element, true);
    }
}
