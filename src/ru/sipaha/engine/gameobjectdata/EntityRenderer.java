package ru.sipaha.engine.gameobjectdata;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.NumberUtils;

public class EntityRenderer {
    public static final int ENTITY_RENDER_SIZE = 20;

    private int X1,Y1,C1,U1,V1;
    private int X2,Y2,C2,U2,V2;
    private int X3,Y3,C3,U3,V3;
    private int X4,Y4,C4,U4,V4;

    private float[] renderData;
    private float a = 1, r = 1, g = 1, b = 1;
    private float cachedA,cachedR,cachedG,cachedB;
    private float u,v,u2,v2,dv,du;
    public float width, height;
    public float originX, originY;
    public float offsetV = 0, offsetU = 0;
    public float repeatX = 1, repeatY = 1;
    public boolean fixedRotation = false;

    private boolean dirtyBody = false;

    public EntityRenderer(TextureRegion r) {
        this(r.getU(), r.getV(), r.getU2(), r.getV2(), r.getRegionWidth(), r.getRegionHeight());
    }

    public EntityRenderer(EntityRenderer renderer) {
        this(renderer.u, renderer.v, renderer.u2, renderer.v2, renderer.width, renderer.height);
        reset(renderer);
    }

    public EntityRenderer(float u, float v, float u2, float v2, float width, float height) {
        setUV(u, v, u2, v2);
        this.width = width;
        this.height = height;
        originX = width / 2f;
        originY = height / 2f;
    }

    public void setUV(float u, float v, float u2, float v2) {
        this.u = u;
        this.v = v;
        this.u2 = u2;
        this.v2 = v2;
        dv = v2 - v;
        du = u2 - u;
    }

    public void setRenderData(float[] renderData, int offset) {
        X1 =   +offset; Y1 = 1 +offset; C1 = 2 +offset; U1 = 3 +offset; V1 = 4 +offset;
        X2 = 5 +offset; Y2 = 6 +offset; C2 = 7 +offset; U2 = 8 +offset; V2 = 9 +offset;
        X3 = 10+offset; Y3 = 11+offset; C3 = 12+offset; U3 = 13+offset; V3 = 14+offset;
        X4 = 15+offset; Y4 = 16+offset; C4 = 17+offset; U4 = 18+offset; V4 = 19+offset;
        this.renderData = renderData;
        updateUV();
    }

    public void update(Transform t) {
        updateBody(t);
        updateColor();
    }

    private void updateUV() {
        float u  = this.u  + offsetU;
        float v  = this.v  + offsetV;
        float u2 = this.u2 + offsetU;
        float v2 = this.v2 + offsetV;
        renderData[U1] = u;  renderData[V1] = v2;
        renderData[U2] = u;  renderData[V2] = v;
        renderData[U3] = u2; renderData[V3] = v;
        renderData[U4] = u2; renderData[V4] = v2;
    }

    private void updateColor() {
        if(a != cachedA || b != cachedB || g != cachedG || r != cachedR) {
            int intBits = ((int) (255 * a) << 24)
                    | ((int) (255 * b) << 16)
                    | ((int) (255 * g) << 8)
                    | ((int) (255 * r));
            float color = NumberUtils.intToFloatColor(intBits);
            renderData[C1] = color;
            renderData[C2] = color;
            renderData[C3] = color;
            renderData[C4] = color;

            cachedA = a;
            cachedB = b;
            cachedG = g;
            cachedR = r;
        }
    }

    private void updateBody(Transform t) {
        if(!t.wasChanged && !dirtyBody) return;
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
            renderData[X1] = x1;
            renderData[Y1] = y1;
            renderData[X2] = x2;
            renderData[Y2] = y2;
            renderData[X3] = x3;
            renderData[Y3] = y3;
            renderData[X4] = x1 + (x3 - x2);
            renderData[Y4] = y3 - (y2 - y1);
        } else {
            float x1 = localX  + t.tx;
            float y1 = localY  + t.ty;
            float x3 = localX2 + t.tx;
            float y3 = localY2 + t.ty;
            renderData[X1] = x1;
            renderData[Y1] = y1;
            renderData[X3] = x3;
            renderData[Y3] = y3;
            renderData[X2] = x1;
            renderData[Y2] = y3;
            renderData[X4] = x3;
            renderData[Y4] = y1;
        }
        dirtyBody = false;
    }

    public void reset(EntityRenderer source) {
        a = source.a;
        r = source.r;
        g = source.g;
        b = source.b;
        offsetV = source.offsetV;
        offsetU = source.offsetU;
        repeatX = source.repeatX;
        repeatY = source.repeatY;
    }

    public Rectangle getBounds(Rectangle bounds) {
        final float[] vertices = renderData;

        float minx = vertices[X1];
        float miny = vertices[Y1];
        float maxx = vertices[X1];
        float maxy = vertices[Y1];

        minx = minx > vertices[X2] ? vertices[X2] : minx;
        minx = minx > vertices[X3] ? vertices[X3] : minx;
        minx = minx > vertices[X4] ? vertices[X4] : minx;

        maxx = maxx < vertices[X2] ? vertices[X2] : maxx;
        maxx = maxx < vertices[X3] ? vertices[X3] : maxx;
        maxx = maxx < vertices[X4] ? vertices[X4] : maxx;

        miny = miny > vertices[Y2] ? vertices[Y2] : miny;
        miny = miny > vertices[Y3] ? vertices[Y3] : miny;
        miny = miny > vertices[Y4] ? vertices[Y4] : miny;

        maxy = maxy < vertices[Y2] ? vertices[Y2] : maxy;
        maxy = maxy < vertices[Y3] ? vertices[Y3] : maxy;
        maxy = maxy < vertices[Y4] ? vertices[Y4] : maxy;

        if (bounds == null) {
            bounds = new Rectangle();
            bounds.x = minx;
            bounds.y = miny;
            bounds.width = maxx - minx;
            bounds.height = maxy - miny;
        } else {
            float maxWidth = maxx - minx;
            float maxHeight = maxy - miny;
            bounds.x = Math.min(bounds.x, minx);
            bounds.y = Math.min(bounds.y, miny);
            bounds.width = Math.max(bounds.width, maxWidth);
            bounds.height = Math.max(bounds.height, maxHeight);
        }

        return bounds;
    }

    public void setAlpha(float alpha) {
        a = alpha;
    }

    public void setOrigin(float x, float y) {
        originX = x;
        originY = y;
        dirtyBody = true;
    }
}
