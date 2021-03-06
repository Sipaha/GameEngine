package ru.sipaha.engine.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.NumberUtils;
import ru.sipaha.engine.graphics.RenderBuffer;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.structures.Bounds;

import static ru.sipaha.engine.core.Transform.*;

public class Sprite extends Entity {
    public static final int ENTITY_RENDER_SIZE = 20;

    public static final int X1 = 0, Y1 = 1, C1 = 2, U1 = 3, V1 = 4;
    public static final int X2 = 5, Y2 = 6, C2 = 7, U2 = 8, V2 = 9;
    public static final int X3 = 10, Y3 = 11, C3 = 12, U3 = 13, V3 = 14;
    public static final int X4 = 15, Y4 = 16, C4 = 17, U4 = 18, V4 = 19;

    private final Values.Flag colorChanged = new Values.Flag();
    public final Values.Float colorA = new Values.Float(colorChanged, 1);
    public final Values.Float colorR = new Values.Float(colorChanged, 1);
    public final Values.Float colorG = new Values.Float(colorChanged, 1);
    public final Values.Float colorB = new Values.Float(colorChanged, 1);

    public final Values.Flag uvChanged = new Values.Flag();
    public final Values.FloatArray uv = new Values.FloatArray(uvChanged, new float[4]);

    private final Values.Flag originChanged = new Values.Flag();
    public final Values.Float pivotX = new Values.Float(originChanged);
    public final Values.Float pivotY = new Values.Float(originChanged);

    private boolean boundsWasChanged = true;

    private final Values.Flag verticesUpdateRequest = new Values.Flag(false);
    public final Values.Bool visible = new Values.Bool(verticesUpdateRequest, true);
    public final Values.Bool fixedRotation = new Values.Bool(verticesUpdateRequest, false);

    public final Values.Float width = new Values.Float(verticesUpdateRequest);
    public final Values.Float height = new Values.Float(verticesUpdateRequest);

    protected Bounds bounds;

    private Sprite prototype;

    public Sprite(TextureRegion r) {
        this(null, r);
    }

    public Sprite(String name, TextureRegion r) {
        this(name, r.getRegionWidth(), r.getRegionHeight(), r.getU(), r.getV(), r.getU2(), r.getV2());
    }

    public Sprite(Texture t) {
        this(null, t);
    }

    public Sprite(String name, Texture t) {
        this(name, t.getWidth(), t.getHeight(), 0, 0, 1, 1);
    }

    public Sprite(String name, float width, float height, float... uv) {
        super(name);
        this.uv.setValues(uv);
        this.width.set(width);
        this.height.set(height);
        pivotX.set(width / 2f);
        pivotY.set(height / 2f);
    }

    public Sprite(Sprite prototype) {
        super(prototype);
        width.set(prototype.width);
        height.set(prototype.height);
        uv.setValues(prototype.uv);
        pivotX.set(prototype.pivotX);
        pivotY.set(prototype.pivotY);
        this.prototype = prototype;
        reset();
    }

    @Override
    public void start(Engine engine, Transform baseTransform, Array<Entity> entities)  {
        super.start(engine, baseTransform, entities);
        if(renderData == null) {
            setRenderData(new float[ENTITY_RENDER_SIZE], 0);
        }
    }

    @Override
    public void render(RenderBuffer buffer) {
        if(visible.value) {
            buffer.render(renderData, offset, ENTITY_RENDER_SIZE);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(renderDataChanged) {
            updateBody();
            updateColor();
            updateUV();
            renderDataChanged = false;
        } else if(renderData != null && visible.value) {
            if(colorChanged.value) {
                updateColor();
            }
            if(uvChanged.value) {
                updateUV();
            }
            if(transform.wasChanged || verticesUpdateRequest.value) {
                updateBody();
            }
        }
    }

    @Override
    public int getRenderSize() {
        return ENTITY_RENDER_SIZE;
    }

    protected void updateColor() {
        int intBits = ((int) (255 * colorA.value) << 24)
                | ((int) (255 * colorB.value) << 16)
                | ((int) (255 * colorG.value) << 8)
                | ((int) (255 * colorR.value));
        float color = NumberUtils.intToFloatColor(intBits);
        renderData[offset+C1] = color;
        renderData[offset+C2] = color;
        renderData[offset+C3] = color;
        renderData[offset+C4] = color;
        colorChanged.value = false;
    }

    protected void updateUV() {
        float[] renderData = this.renderData;
        renderData[offset+U1] = uv.value[0];
        renderData[offset+V1] = uv.value[3];
        renderData[offset+U2] = uv.value[0];
        renderData[offset+V2] = uv.value[1];
        renderData[offset+U3] = uv.value[2];
        renderData[offset+V3] = uv.value[1];
        renderData[offset+U4] = uv.value[2];
        renderData[offset+V4] = uv.value[3];
        uvChanged.value = false;
    }

    protected void updateBody() {
        if(visible.value) {
            Transform t = this.transform;
            float localX = -pivotX.value;
            float localY = -pivotY.value;
            float localX2 = localX + width.value;
            float localY2 = localY + height.value;

            if (!fixedRotation.value) {
                final float x_m00 = localX * t.data[T00], y_m01 = localY * t.data[T01];
                final float x_m10 = localX * t.data[T10], y_m11 = localY * t.data[T11];
                final float x2_m00 = localX2 * t.data[T00], y2_m01 = localY2 * t.data[T01];
                final float x2_m10 = localX2 * t.data[T10], y2_m11 = localY2 * t.data[T11];
                final float x1 = x_m00 + y_m01 + t.data[TX];
                final float y1 = x_m10 + y_m11 + t.data[TY];
                final float x2 = x_m00 + y2_m01 + t.data[TX];
                final float y2 = x_m10 + y2_m11 + t.data[TY];
                final float x3 = x2_m00 + y2_m01 + t.data[TX];
                final float y3 = x2_m10 + y2_m11 + t.data[TY];
                renderData[offset + X1] = x1;
                renderData[offset + Y1] = y1;
                renderData[offset + X2] = x2;
                renderData[offset + Y2] = y2;
                renderData[offset + X3] = x3;
                renderData[offset + Y3] = y3;
                renderData[offset + X4] = x1 + (x3 - x2);
                renderData[offset + Y4] = y3 - (y2 - y1);
            } else {
                final float x1 = localX + t.data[TX];
                final float y1 = localY + t.data[TY];
                final float x3 = localX2 + t.data[TX];
                final float y3 = localY2 + t.data[TY];
                renderData[offset + X1] = x1;
                renderData[offset + Y1] = y1;
                renderData[offset + X2] = x1;
                renderData[offset + Y2] = y3;
                renderData[offset + X3] = x3;
                renderData[offset + Y3] = y3;
                renderData[offset + X4] = x3;
                renderData[offset + Y4] = y1;
            }
        } else {
            renderData[offset+X1] = 0;
            renderData[offset+Y1] = 0;
            renderData[offset+X3] = 0;
            renderData[offset+Y3] = 0;
            renderData[offset+X2] = 0;
            renderData[offset+Y2] = 0;
            renderData[offset+X4] = 0;
            renderData[offset+Y4] = 0;
        }
        boundsWasChanged = true;
        verticesUpdateRequest.value = false;
    }

    @Override
    public void reset() {
        colorA.set(prototype.colorA);
        colorR.set(prototype.colorR);
        colorG.set(prototype.colorG);
        colorB.set(prototype.colorB);
    }

    @Override
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

    public void setColor(float r, float g, float b) {
        colorR.set(r);
        colorG.set(g);
        colorB.set(b);
    }

    public void setColor(float r, float g, float b, float a) {
        colorR.set(r);
        colorG.set(g);
        colorB.set(b);
        colorA.set(a);
    }

    public void setPivot(float x, float y) {
        pivotX.set(x);
        pivotY.set(y);
    }

    public void setSize(float width, float height) {
        this.width.set(width);
        this.height.set(height);
    }
}

