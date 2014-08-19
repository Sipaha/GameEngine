package ru.sipaha.engine.graphics.batches;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import ru.sipaha.engine.graphics.RenderUnit;

public abstract class Batch extends RenderUnit {

    public static final int SPRITE_SIZE = 20;

    private Matrix4 combinedMatrix;

    private boolean blendingDisabled = false;
    private int blendSrcFunc = GL20.GL_SRC_ALPHA;
    private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;

    public boolean isStatic = false;

    public Batch (ShaderProgram shader, Texture texture, int z_order) {
        super(texture, shader, z_order);
    }

    public Batch(RenderUnit batch) {
        super(batch);
    }

    protected abstract int prepareVertices(float[] vertices, int idx);

    public void setCombinedMatrix (Matrix4 combined) {
        combinedMatrix = combined;
    }

    public Matrix4 getCombinedMatrix() {
        return combinedMatrix;
    }

    public int getBlendSrcFunc() {
        return blendSrcFunc;
    }

    public int getBlendDstFunc() {
        return blendDstFunc;
    }

    public boolean isBlendingDisabled() {
        return blendingDisabled;
    }

    public void setBlending(boolean blendingEnabled) {
        blendingDisabled = !blendingEnabled;
    }

    public void setBlendFunction (int srcFunc, int dstFunc) {
        blendSrcFunc = srcFunc;
        blendDstFunc = dstFunc;
    }

    @Override
    public boolean equalsIgnoreZOrder(RenderUnit renderUnit) {
        return super.equalsIgnoreZOrder(renderUnit)
                && renderUnit instanceof Batch && equalsBatches((Batch) renderUnit);

    }

    private boolean equalsBatches(Batch b) {
        return isStatic == b.isStatic
                && combinedMatrix == b.combinedMatrix
                && blendingDisabled == b.blendingDisabled
                && blendSrcFunc == b.blendSrcFunc
                && blendDstFunc == b.blendDstFunc;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof Batch && equalsBatches((Batch) obj);
    }
}
