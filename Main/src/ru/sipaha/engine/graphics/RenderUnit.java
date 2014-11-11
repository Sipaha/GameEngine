package ru.sipaha.engine.graphics;

import com.badlogic.gdx.graphics.Texture;
import ru.sipaha.engine.core.Values;
import ru.sipaha.engine.utils.Shaders;

/**
 * Created on 18.10.2014.
 */

public abstract class RenderUnit extends Renderable implements Comparable<RenderUnit> {

    private int hash = 0;

    private final Values.Flag changed = new Values.Flag(true);
    public final Values.RenderLayerValue renderLayer = new Values.RenderLayerValue(changed, Renderer.DEFAULT_LAYER);
    public final Values.ShaderValue shader = new Values.ShaderValue(changed, Shaders.DEFAULT_SHADER_NAME);
    public final Values.Int zOrder = new Values.Int(changed, Renderer.DEFAULT_Z_ORDER);
    public final Values.Bool blendingEnabled = new Values.Bool(changed, true);
    public final Values.BlendFunction blendSrcFunc = new Values.BlendFunction(changed, "GL_SRC_ALPHA");
    public final Values.BlendFunction blendDstFunc = new Values.BlendFunction(changed, "GL_ONE_MINUS_SRC_ALPHA");
    public final Values.Bool isStatic = new Values.Bool(changed, false);

    private Texture texture;

    public RenderUnit(){}

    public RenderUnit(Texture texture) {
        setTexture(texture);
    }

    public RenderUnit(RenderUnit renderUnit) {
        setPropertiesFrom(renderUnit);
    }

    public RenderUnit(Texture t, String shader, int zOrder) {
        setTexture(t);
        this.shader.set(shader);
        this.zOrder.set(zOrder);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        changed.value = true;
        hash = 0;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setPropertiesFrom(RenderUnit unit) {
        texture = unit.texture;
        renderLayer.set(unit.renderLayer);
        shader.set(unit.shader);
        zOrder.set(unit.zOrder);
        blendingEnabled.set(unit.blendingEnabled);
        blendSrcFunc.set(unit.blendSrcFunc);
        blendDstFunc.set(unit.blendDstFunc);
        isStatic.set(unit.isStatic);
        hash = unit.hash;
    }

    public boolean checkChanges() {
        if(changed.value) {
            hash = 0;
            changed.value = false;
            return true;
        }
        return false;
    }

    public boolean equalsIgnoreZOrder(RenderUnit r) {
        return this == r
                || r != null
                && texture == r.texture
                && shader.equals(r.shader)
                && isStatic.equals(r.isStatic)
                && blendingEnabled.equals(r.blendingEnabled)
                && blendDstFunc.equals(r.blendDstFunc)
                && blendSrcFunc.equals(r.blendSrcFunc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof RenderUnit)) {
            return false;
        }

        RenderUnit r = (RenderUnit) o;

        return texture == r.texture
                && zOrder.equals(r.zOrder)
                && shader.equals(r.shader)
                && isStatic.equals(r.isStatic)
                && blendingEnabled.equals(r.blendingEnabled)
                && blendDstFunc.equals(r.blendDstFunc)
                && blendSrcFunc.equals(r.blendSrcFunc);
    }

    @Override
    public int hashCode() {
        if(hash == 0) {
            hash = texture.hashCode();
            hash = 31 * hash + shader.hashCode();
            hash = 31 * hash + zOrder.get();
        }
        return hash;
    }

    @Override
    public int compareTo(RenderUnit unit) {
        int thisZ = zOrder.get();
        int unitZ = unit.zOrder.get();
        if (thisZ > unitZ) return 1;
        if (thisZ < unitZ) return -1;
        return 0;
    }
}
