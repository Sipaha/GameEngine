package ru.frozen.prolen.core.gameobject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.NumberUtils;
import javafx.scene.shape.Mesh;
import ru.frozen.prolen.core.graphics.RenderUnit;

public class MeshRenderer extends RenderUnit {

    public final int X1 = 0, Y1 = 1, C1 = 2, U1 = 3, V1 = 4;
    public final int X2 = 5, Y2 = 6, C2 = 7, U2 = 8, V2 = 9;
    public final int X3 = 10,Y3 = 11,C3 = 12,U3 = 13,V3 = 14;
    public final int X4 = 15,Y4 = 16,C4 = 17,U4 = 18,V4 = 19;

    public float[] data = new float[20];

    public float a = 1, r = 1, g = 1, b = 1;
    public float u,v,u2,v2,dv,du;
    public boolean visible = true;
    public boolean fixed_camera = false;
    public float offsetV = 0, offsetU = 0;
    public float repeatX = 1, repeatY = 1;

    private float cachedA,cachedR,cachedG,cachedB;

    public float width, height;
    public float origin_x, origin_y;
    public boolean fixed_rotation = false;

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
        origin_x = width / 2;
        origin_y = height / 2;
    }

    public MeshRenderer(MeshRenderer prototype) {
        super(prototype.texture, prototype.shader, prototype.z_order);
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
        float localX  = -origin_x;
        float localY  = -origin_y;
        float localX2 = localX + width;
        float localY2 = localY + height;

        if(!fixed_rotation) {
            final float x_m00  = localX  * t.m00, y_m01  = localY  * t.m01;
            final float x_m10  = localX  * t.m10, y_m11  = localY  * t.m11;
            final float x2_m00 = localX2 * t.m00, y2_m01 = localY2 * t.m01;
            final float x2_m10 = localX2 * t.m10, y2_m11 = localY2 * t.m11;
            float x1 = x_m00  + y_m01  + t.m02;
            float y1 = x_m10  + y_m11  + t.m12;
            float x2 = x_m00  + y2_m01 + t.m02;
            float y2 = x_m10  + y2_m11 + t.m12;
            float x3 = x2_m00 + y2_m01 + t.m02;
            float y3 = x2_m10 + y2_m11 + t.m12;
            data[X1] = x1;
            data[Y1] = y1;
            data[X2] = x2;
            data[Y2] = y2;
            data[X3] = x3;
            data[Y3] = y3;
            data[X4] = x1 + (x3 - x2);
            data[Y4] = y3 - (y2 - y1);
        } else {
            float x1 = localX  + t.m02;
            float y1 = localY  + t.m12;
            float x3 = localX2 + t.m02;
            float y3 = localY2 + t.m12;
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
        fixed_camera = source.fixed_camera;
        offsetV = source.offsetV;
        offsetU = source.offsetU;
        repeatX = source.repeatX;
        repeatY = source.repeatY;
        width = source.width;
        height = source.height;
        origin_x = source.origin_x;
        origin_y = source.origin_y;
        fixed_rotation = source.fixed_rotation;
    }
}
