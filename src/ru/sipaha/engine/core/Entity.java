package ru.sipaha.engine.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.NumberUtils;
import ru.sipaha.engine.core.animation.discrete.SpriteAnimation;
import ru.sipaha.engine.core.animation.сontinuous.*;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.graphics.RenderBuffer;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.structures.Bounds;

public class Entity extends RenderUnit {
    public static final int ENTITY_RENDER_SIZE = 20;

    public static final int X1 = 0, Y1 = 1, C1 = 2, U1 = 3, V1 = 4;
    public static final int X2 = 5, Y2 = 6, C2 = 7, U2 = 8, V2 = 9;
    public static final int X3 = 10, Y3 = 11, C3 = 12, U3 = 13, V3 = 14;
    public static final int X4 = 15, Y4 = 16, C4 = 17, U4 = 18, V4 = 19;

    private String name;
    private int transformId = 0;
    private Transform transform;

    private float a = 1, r = 1, g = 1, b = 1;
    private float u, v, u2, v2;
    private float width, height;
    private float originX, originY;
    private boolean fixedRotation = false;

    private boolean dirty = true;
    private boolean boundsWasChanged = true;

    protected boolean visible = true;

    protected Bounds bounds;

    public AnimatedAlpha animatedAlpha = null;
    public AnimatedColor animatedColor = null;
    public AnimatedOrigin animatedOrigin = null;
    public SpriteAnimation animatedSprite = null;

    public Entity(TextureRegion r) {
        this(r.getU(), r.getV(), r.getU2(), r.getV2(), r.getRegionWidth(), r.getRegionHeight());
    }

    public Entity(Texture t) {
        this(0, 0, 1, 1, t.getWidth(), t.getHeight());
    }

    public Entity(Entity entity) {
        this(entity.u, entity.v, entity.u2, entity.v2, entity.width, entity.height);
        transformId = entity.transformId;
        reset(entity);
    }

    public Entity(float u, float v, float u2, float v2, float width, float height) {
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
        if(renderData != null) updateUV();
    }

    private void updateUV() {
        renderData[offset+U1] = u;  renderData[offset+V1] = v2;
        renderData[offset+U2] = u;  renderData[offset+V2] = v;
        renderData[offset+U3] = u2; renderData[offset+V3] = v;
        renderData[offset+U4] = u2; renderData[offset+V4] = v2;
    }

    @Override
    public int setRenderData(float[] renderData, int offset) {
        int res = super.setRenderData(renderData, offset);
        dirty = true;
        updateColor();
        updateUV();
        return res;
    }

    @Override
    public void render(RenderBuffer buffer) {
        if(visible) {
            updateBody();
            buffer.render(renderData, offset, ENTITY_RENDER_SIZE);
        }
    }

    @Override
    public int getRenderSize() {
        return ENTITY_RENDER_SIZE;
    }

    private void updateColor() {
        int intBits = ((int) (255 * a) << 24)
                | ((int) (255 * b) << 16)
                | ((int) (255 * g) << 8)
                | ((int) (255 * r));
        float color = NumberUtils.intToFloatColor(intBits);
        renderData[offset+C1] = color;
        renderData[offset+C2] = color;
        renderData[offset+C3] = color;
        renderData[offset+C4] = color;
    }

    public void updateBody() {
        Transform t = this.transform;
        if(!t.wasChanged && !dirty) return;
        float localX  = -originX;
        float localY  = -originY;
        float localX2 = localX + width;
        float localY2 = localY + height;

        if(!fixedRotation) {
            final float x_m00  = localX  * t.t00, y_m01  = localY  * t.t01;
            final float x_m10  = localX  * t.t10, y_m11  = localY  * t.t11;
            final float x2_m00 = localX2 * t.t00, y2_m01 = localY2 * t.t01;
            final float x2_m10 = localX2 * t.t10, y2_m11 = localY2 * t.t11;
            final float x1 = x_m00  + y_m01  + t.tx;
            final float y1 = x_m10  + y_m11  + t.ty;
            final float x2 = x_m00  + y2_m01 + t.tx;
            final float y2 = x_m10  + y2_m11 + t.ty;
            final float x3 = x2_m00 + y2_m01 + t.tx;
            final float y3 = x2_m10 + y2_m11 + t.ty;
            renderData[offset+X1] = x1;
            renderData[offset+Y1] = y1;
            renderData[offset+X2] = x2;
            renderData[offset+Y2] = y2;
            renderData[offset+X3] = x3;
            renderData[offset+Y3] = y3;
            renderData[offset+X4] = x1 + (x3 - x2);
            renderData[offset+Y4] = y3 - (y2 - y1);
        } else {
            final float x1 = localX  + t.tx;
            final float y1 = localY  + t.ty;
            final float x3 = localX2 + t.tx;
            final float y3 = localY2 + t.ty;
            renderData[offset+X1] = x1;
            renderData[offset+Y1] = y1;
            renderData[offset+X3] = x3;
            renderData[offset+Y3] = y3;
            renderData[offset+X2] = x1;
            renderData[offset+Y2] = y3;
            renderData[offset+X4] = x3;
            renderData[offset+Y4] = y1;
        }
        dirty = false;
        boundsWasChanged = true;
    }

    public void reset(Entity source) {
        a = source.a;
        r = source.r;
        g = source.g;
        b = source.b;
        animatedAlpha = null;
        animatedColor = null;
        animatedOrigin = null;
        animatedSprite = null;
    }

    public Bounds getBounds() {
        if(bounds == null) {
            bounds = new Bounds();
        } else if(!boundsWasChanged) {
            return bounds;
        }
        final float[] vertices = renderData;

        float minX = Math.min(vertices[offset+X1],
                     Math.min(vertices[offset+X2],
                     Math.min(vertices[offset+X3],
                              vertices[offset+X4])));
        float maxX = Math.max(vertices[offset+X1],
                     Math.max(vertices[offset+X2],
                     Math.max(vertices[offset+X3],
                              vertices[offset+X4])));
        float minY = Math.min(vertices[offset+Y1],
                     Math.min(vertices[offset+Y2],
                     Math.min(vertices[offset+Y3],
                              vertices[offset+Y4])));
        float maxY = Math.max(vertices[offset+Y1],
                     Math.max(vertices[offset+Y2],
                     Math.max(vertices[offset+Y3],
                              vertices[offset+Y4])));

        boundsWasChanged = false;
        return bounds.set(minX, maxX, maxY, minY);
    }

    public void setAlpha(float alpha) {
        a = alpha;
        if(renderData != null) updateColor();
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        if(renderData != null) updateColor();
    }

    public void setOrigin(float x, float y) {
        originX = x;
        originY = y;
        dirty = true;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setTransformId(int id) {
        transformId = id;
    }
    public int getTransformId() {
        return transformId;
    }

    public void setTransform(Transform[] transforms) {
        transform = transforms[transformId];
    }

    public Transform getTransform() {
        return transform;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setWidth(float width) {
        this.width = width;
        dirty = true;
    }

    public void setHeight(float height) {
        this.height = height;
        dirty = true;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        dirty = true;
    }
}
