package ru.sipaha.engine.gameobjectdata;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.NumberUtils;
import ru.sipaha.engine.graphics.RenderUnit;

public class MeshRenderer extends RenderUnit {

    public final int X1 = 0, Y1 = 1, C1 = 2, U1 = 3, V1 = 4;
    public final int X2 = 5, Y2 = 6, C2 = 7, U2 = 8, V2 = 9;
    public final int X3 = 10,Y3 = 11,C3 = 12,U3 = 13,V3 = 14;
    public final int X4 = 15,Y4 = 16,C4 = 17,U4 = 18,V4 = 19;

    public float[] data = new float[20];

    public float a = 1, r = 1, g = 1, b = 1;
    public float u,v,u2,v2,dv,du;
    public boolean visible = true;
    public boolean fixedCamera = false;
    public boolean blendingDisabled = false;
    public float offsetV = 0, offsetU = 0;
    public float repeatX = 1, repeatY = 1;

    private float cachedA,cachedR,cachedG,cachedB;

    public float width, height;
    public float originX, originY;
    public boolean fixedRotation = false;

    public MeshRenderer(TextureRegion region, ShaderProgram s, int z_order) {
        super(region.getTexture(), s, z_order);
        u = region.getU();
        v = region.getV();
        u2 = region.getU2();
        v2 = region.getV2();
        dv = v2 - v;
        du = u2 - u;

        width = region.getRegionWidth();
        height = region.getRegionHeight();
        originX = width / 2;
        originY = height / 2;
    }

    public MeshRenderer(MeshRenderer prototype) {
        super(prototype);
        set(prototype);
    }

    public void update(Transform t) {
        updateBody(t);
        updateColor();
        updateUV();
    }

    public void updateUV() {
        float u  = this.u  + offsetU;
        float v  = this.v  + offsetV;
        float u2 = this.u2 + offsetU;
        float v2 = this.v2 + offsetV;
        data[U1] = u;  data[V1] = v2;
        data[U2] = u;  data[V2] = v;
        data[U3] = u2; data[V3] = v;
        data[U4] = u2; data[V4] = v2;
    }

    public void updateColor() {
        if(a != cachedA || b != cachedB || g != cachedG || r != cachedR) {
            int intBits = ((int) (255 * a) << 24)
                        | ((int) (255 * b) << 16)
                        | ((int) (255 * g) << 8)
                        | ((int) (255 * r));
            float color = NumberUtils.intToFloatColor(intBits);
            data[C1] = color;
            data[C2] = color;
            data[C3] = color;
            data[C4] = color;

            cachedA = a;
            cachedB = b;
            cachedG = g;
            cachedR = r;
        }
    }

    private void updateBody(Transform t) {
        if(!t.wasChanged) return;
        float localX  = -originX;
        float localY  = -originY;
        float localX2 = localX + width;
        float localY2 = localY + height;

        if(!fixedRotation) {
            final float x_m00  = localX  * t.t00, y_m01  = localY  * t.t01;
            final float x_m10  = localX  * t.t10, y_m11  = localY  * t.t11;
            final float x2_m00 = localX2 * t.t00, y2_m01 = localY2 * t.t01;
            final float x2_m10 = localX2 * t.t10, y2_m11 = localY2 * t.t11;
            float x1 = x_m00  + y_m01  + t.tx;
            float y1 = x_m10  + y_m11  + t.ty;
            float x2 = x_m00  + y2_m01 + t.tx;
            float y2 = x_m10  + y2_m11 + t.ty;
            float x3 = x2_m00 + y2_m01 + t.tx;
            float y3 = x2_m10 + y2_m11 + t.ty;
            data[X1] = x1;
            data[Y1] = y1;
            data[X2] = x2;
            data[Y2] = y2;
            data[X3] = x3;
            data[Y3] = y3;
            data[X4] = x1 + (x3 - x2);
            data[Y4] = y3 - (y2 - y1);
        } else {
            float x1 = localX  + t.tx;
            float y1 = localY  + t.ty;
            float x3 = localX2 + t.tx;
            float y3 = localY2 + t.ty;
            data[X1] = x1;
            data[Y1] = y1;
            data[X3] = x3;
            data[Y3] = y3;
            data[X2] = x1;
            data[Y2] = y3;
            data[X4] = x3;
            data[Y4] = y1;
        }
    }

    public void setLinearFilter() {
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void setNearestFilter() {
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void set(MeshRenderer source) {
        a = source.a;
        r = source.r;
        g = source.g;
        b = source.b;
        u = source.u;
        v = source.v;
        u2 = source.u2;
        v2 = source.v2;
        dv = source.dv;
        du = source.du;
        visible = source.visible;
        fixedCamera = source.fixedCamera;
        offsetV = source.offsetV;
        offsetU = source.offsetU;
        repeatX = source.repeatX;
        repeatY = source.repeatY;
        width = source.width;
        height = source.height;
        originX = source.originX;
        originY = source.originY;
        fixedRotation = source.fixedRotation;
        blendingDisabled = source.blendingDisabled;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
