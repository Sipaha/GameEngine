package ru.sipaha.engine.graphics;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.sipaha.engine.utils.Shaders;

/**
 * Created on 18.10.2014.
 */

public abstract class RenderUnit implements Comparable<RenderUnit> {
    private RenderProperties properties;
    private boolean propertiesOwner;
    private int hash = 0;

    protected int offset;
    protected float[] renderData;

    public RenderUnit(){}

    public RenderUnit(Texture texture) {
        setTexture(texture);
    }

    public RenderUnit(RenderUnit renderUnit) {
        setPropertiesFrom(renderUnit);
    }

    public RenderUnit(Texture t, ShaderProgram s, int zOrder) {
        setTexture(t);
        setShader(s);
    }

    public void setTexture(Texture texture) {
        checkOwnerForChangeProperty();
        if(propertiesOwner) {
            properties.texture = texture;
            updateTextureFilter();
            hash = 0;
        }
    }
    public Texture getTexture() {
        return properties.texture;
    }

    public void setShader(ShaderProgram shader) {
        checkOwnerForChangeProperty();
        if(propertiesOwner) {
            properties.shader = shader;
            hash = 0;
        }
    }
    public ShaderProgram getShader() {
        return properties.shader;
    }

    public void setZOrder(int zOrder) {
        checkOwnerForChangeProperty();
        if(propertiesOwner) {
            properties.zOrder = zOrder;
            hash = 0;
        }
    }
    public int getZOrder() {
        return properties.zOrder;
    }

    public void setBlending(boolean blendingEnabled) {
        checkOwnerForChangeProperty();
        if(propertiesOwner) {
            properties.blendingDisabled = !blendingEnabled;
        }
    }
    public boolean isBlendingEnabled() {
        return !properties.blendingDisabled;
    }

    public void setBlendFunctions (int srcFunc, int dstFunc) {
        checkOwnerForChangeProperty();
        if(propertiesOwner) {
            properties.blendSrcFunc = srcFunc;
            properties.blendDstFunc = dstFunc;
        }
    }
    public int getBlendSrcFunc() {
        return properties.blendSrcFunc;
    }
    public int getBlendDstFunc() {
        return properties.blendDstFunc;
    }

    public void setRenderLayerTag(String renderLayerTag) {
        checkOwnerForChangeProperty();
        if(propertiesOwner) {
            properties.renderLayerTag = renderLayerTag;
        }
    }
    public String getRenderLayerTag() {
        return properties.renderLayerTag;
    }

    public void setLinearFilter() {
        checkOwnerForChangeProperty();
        if(propertiesOwner && !properties.isLinearFilter) {
            properties.isLinearFilter = true;
            if(properties.texture != null) updateTextureFilter();
        }
    }
    public void setNearestFilter() {
        checkOwnerForChangeProperty();
        if(propertiesOwner && properties.isLinearFilter) {
            properties.isLinearFilter = false;
            if(properties.texture != null) updateTextureFilter();
        }
    }

    public void setStatic(boolean isStatic) {
        checkOwnerForChangeProperty();
        if(propertiesOwner) {
            properties.isStatic = isStatic;
        }
    }
    public boolean isStatic() {
        return properties.isStatic;
    }

    private void updateTextureFilter() {
        if(properties.isLinearFilter) {
            properties.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            properties.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    private void checkOwnerForChangeProperty() {
        if(properties == null) {
            properties = new RenderProperties();
            propertiesOwner = true;
        }
        if(!propertiesOwner) throw new RuntimeException("This object can't change render properties!");
    }

    public boolean equalsIgnoreZOrder(RenderUnit r) {
        return properties.equalsIgnoreZOrder(r.properties);
    }

    public boolean canBeRendered() {
        return properties != null && properties.texture != null && properties.shader != null;
    }

    public void setPropertiesFrom(RenderUnit unit) {
        properties = unit.properties;
        propertiesOwner = properties == null;
    }

    public int setRenderData(float[] data, int offset) {
        this.offset = offset;
        this.renderData = data;
        return getRenderSize()+offset;
    }

    public abstract void render(RenderBuffer buffer);

    public abstract int getRenderSize();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RenderUnit && properties.equals(((RenderUnit)obj).properties);
    }

    @Override
    public int compareTo(RenderUnit unit) {
        if (properties.zOrder > unit.properties.zOrder) return 1;
        if (properties.zOrder < unit.properties.zOrder) return -1;
        return 0;
    }

    @Override
    public int hashCode() {
        if(hash != 0) return hash;
        hash = properties.hashCode();
        return hash;
    }

    private class RenderProperties {
        String renderLayerTag = Renderer.DEFAULT_LAYER;
        Texture texture;
        ShaderProgram shader = Shaders.defaultShader;
        int zOrder = Renderer.DEFAULT_Z_ORDER;
        boolean blendingDisabled = false;
        int blendSrcFunc = GL20.GL_SRC_ALPHA;
        int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
        boolean isStatic = false;
        boolean isLinearFilter = true;

        public boolean equalsIgnoreZOrder(RenderProperties r) {
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

            RenderProperties r = (RenderProperties) o;

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
            hash = texture.hashCode();
            hash = 31 * hash + shader.hashCode();
            hash = 31 * hash + zOrder;
            return hash;
        }
    }
}
