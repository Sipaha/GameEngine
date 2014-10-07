package ru.sipaha.engine.graphics;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.sipaha.engine.utils.Shaders;

public abstract class RenderUnit {
    public static int DEFAULT_Z_ORDER = 3;

    public String renderLayerTag = "Default";
    protected Texture texture;
    protected ShaderProgram shader;
    protected int zOrder;
    protected boolean blendingDisabled = false;
    protected int blendSrcFunc = GL20.GL_SRC_ALPHA;
    protected int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
    protected boolean isStatic = false;

    private int hash;
    private boolean isLinearFilter = true;

    public RenderUnit() {
        shader = Shaders.defaultShader;
    }

    public RenderUnit(RenderUnit renderUnit) {
        texture = renderUnit.texture;
        shader = renderUnit.shader;
        zOrder = renderUnit.zOrder;
        blendingDisabled = renderUnit.blendingDisabled;
        blendSrcFunc = renderUnit.blendSrcFunc;
        blendDstFunc = renderUnit.blendDstFunc;
        isStatic = renderUnit.isStatic;
        hash = renderUnit.hash;
        isLinearFilter = renderUnit.isLinearFilter;
    }

    public RenderUnit(Texture t) {
        this(t, null, DEFAULT_Z_ORDER);
    }

    public RenderUnit(Texture t, int zOrder) {
        this(t, null, zOrder);
    }

    public RenderUnit(Texture t, ShaderProgram s, int zOrder) {
        setTexture(t);

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

    public void setTexture(Texture texture) {
        this.texture = texture;
        updateTextureFilter();
        hash = 0;
    }

    public void setZOrder(int zOrder) {
        this.zOrder = zOrder;
        hash = 0;
    }

    public void setShader(ShaderProgram shader) {
        this.shader = shader;
        hash = 0;
    }

    public void setRenderLayerTag(String renderLayerTag) {
        this.renderLayerTag = renderLayerTag;
    }

    public void setLinearFilter() {
        if(!isLinearFilter) {
            isLinearFilter = true;
            if(texture != null) updateTextureFilter();
        }
    }

    public void setNearestFilter() {
        if(isLinearFilter) {
            isLinearFilter = false;
            if(texture != null) updateTextureFilter();
        }
    }

    private void updateTextureFilter() {
        if(isLinearFilter) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    public boolean equalsIgnoreZOrder(RenderUnit r) {
        return this == r
                || r != null
                && shader == r.shader
                && texture == r.texture
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
