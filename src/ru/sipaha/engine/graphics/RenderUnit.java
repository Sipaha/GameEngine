package ru.sipaha.engine.graphics;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.sipaha.engine.utils.Shaders;

import javax.xml.soap.Text;

public abstract class RenderUnit {
    public static int defaulZOrder = 3;

    protected final Texture texture;
    protected final ShaderProgram shader;
    protected final int zOrder;
    protected boolean blendingDisabled = false;
    protected int blendSrcFunc = GL20.GL_SRC_ALPHA;
    protected int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
    protected boolean inWorldSpace = true;
    protected boolean isStatic = false;

    private int hash;

    public RenderUnit(RenderUnit renderUnit) {
        texture = renderUnit.texture;
        shader = renderUnit.shader;
        zOrder = renderUnit.zOrder;
        blendingDisabled = renderUnit.blendingDisabled;
        blendSrcFunc = renderUnit.blendSrcFunc;
        blendDstFunc = renderUnit.blendDstFunc;
        inWorldSpace = renderUnit.inWorldSpace;
        isStatic = renderUnit.isStatic;
        hash = renderUnit.hash;
    }

    public RenderUnit(Texture t) {
        this(t, null, defaulZOrder);
    }

    public RenderUnit(Texture t, int zOrder) {
        this(t, null, zOrder);
    }

    public RenderUnit(Texture t, ShaderProgram s, int zOrder) {
        texture = t;
        if(s == null) {
            this.shader = Shaders.defaultShader;
        } else {
            this.shader = s;
        }
        this.zOrder = zOrder;
    }

    public Texture getTexture() {
        return texture;
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public int getZOrder() {
        return zOrder;
    }

    public void setBlending(boolean blendingEnabled) {
        blendingDisabled = !blendingEnabled;
    }

    public void setBlendFunction (int srcFunc, int dstFunc) {
        blendSrcFunc = srcFunc;
        blendDstFunc = dstFunc;
    }

    public boolean equalsIgnoreZOrder(RenderUnit r) {
        return this == r
                || r != null
                && shader == r.shader
                && texture == r.texture
                && inWorldSpace == r.inWorldSpace
                && isStatic == r.isStatic
                && blendingDisabled == r.blendingDisabled
                && blendDstFunc == r.blendDstFunc
                && blendSrcFunc == r.blendSrcFunc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RenderUnit r = (RenderUnit) o;

        return zOrder == r.zOrder
                && shader == r.shader
                && texture == r.texture
                && inWorldSpace == r.inWorldSpace
                && isStatic == r.isStatic
                && blendingDisabled == r.blendingDisabled
                && blendDstFunc == r.blendDstFunc
                && blendSrcFunc == r.blendSrcFunc;
    }

    @Override
    public int hashCode() {
        if(hash != 0) return hash;
        hash = texture.hashCode();
        hash = 31 * hash + shader.hashCode();
        hash = 31 * hash + zOrder;
        return hash;
    }
}
