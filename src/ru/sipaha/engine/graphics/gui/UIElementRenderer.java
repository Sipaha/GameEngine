package ru.sipaha.engine.graphics.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.NumberUtils;
import ru.sipaha.engine.graphics.RenderUnit;

/**
 * Created on 12.10.2014.
 */

public class UIElementRenderer extends UIRenderUnit {
    public static int X1 =  0, Y1 =  1, C1 =  2, U1 =  3, V1 =  4;
    public static int X2 =  5, Y2 =  6, C2 =  7, U2 =  8, V2 =  9;
    public static int X3 = 10, Y3 = 11, C3 = 12, U3 = 13, V3 = 14;
    public static int X4 = 15, Y4 = 16, C4 = 17, U4 = 18, V4 = 19;

    public boolean visible = true;

    private final float[] renderData;

    private boolean flippedX = false;
    private boolean flippedY = false;

    public UIElementRenderer(TextureRegion region) {
        super(region.getTexture());
        renderData = new float[20];
        texture = region.getTexture();
        setColor(1,1,1,1);
        setUV(region.getU(), region.getV(), region.getU2(), region.getV2());
    }

    public UIElementRenderer(Texture texture) {
        super(texture);
        renderData = new float[20];
        this.texture = texture;
        setColor(1,1,1,1);
        setUV(0, 0, 1, 1);
    }

    public void setColor(float r, float g, float b, float a) {
        int intBits = ((int) (255 * a) << 24)
                | ((int) (255 * b) << 16)
                | ((int) (255 * g) << 8)
                | ((int) (255 * r));
        float color = NumberUtils.intToFloatColor(intBits);
        renderData[C1] = color;
        renderData[C2] = color;
        renderData[C3] = color;
        renderData[C4] = color;
    }

    public void setUV(float u, float v, float u2, float v2) {
        renderData[U1] = u;  renderData[V1] = v2;
        renderData[U2] = u;  renderData[V2] = v;
        renderData[U3] = u2; renderData[V3] = v;
        renderData[U4] = u2; renderData[V4] = v2;
        flip(flippedX, flippedY);
    }

    public void flip(boolean x, boolean y) {
        if(x) {
            float u1 = renderData[U1];
            float u2 = renderData[U2];
            float u3 = renderData[U3];
            float u4 = renderData[U4];
            renderData[U1] = u4;
            renderData[U2] = u3;
            renderData[U3] = u2;
            renderData[U4] = u1;
        }
        if(y) {
            float v1 = renderData[V1];
            float v2 = renderData[V2];
            float v3 = renderData[V3];
            float v4 = renderData[V4];
            renderData[V1] = v2;
            renderData[V2] = v1;
            renderData[V3] = v4;
            renderData[V4] = v3;
        }
        flippedX = !flippedX;
        flippedY = !flippedY;
    }

    public int render(float[] vertices, int pos) {
        if(visible) {
            System.arraycopy(renderData, 0, vertices, pos, renderData.length);
            return pos + renderData.length;
        } else {
            return pos;
        }
    }

    public void setBounds(float left, float right, float top, float bottom) {
        renderData[X1] = left;
        renderData[Y1] = bottom;
        renderData[X3] = right;
        renderData[Y3] = top;
        renderData[X2] = left;
        renderData[Y2] = top;
        renderData[X4] = right;
        renderData[Y4] = bottom;
    }
}
