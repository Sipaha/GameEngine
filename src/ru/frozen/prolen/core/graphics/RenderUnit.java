package ru.frozen.prolen.core.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.frozen.prolen.core.utils.Shaders;

public abstract class RenderUnit {
    protected final Texture texture;
    protected final ShaderProgram shader;
    protected final int z_order;
    private final int hashCode;

    public RenderUnit(Texture t, ShaderProgram s, int z_order) {
        texture = t;
        if(s == null) {
            this.shader = Shaders.defaultShader;
        } else {
            this.shader = s;
        }
        this.z_order = z_order;

        int textureId = texture.getTextureObjectHandle();
        int shaderId = Shaders.getShaderId(this.shader);
        hashCode = ((textureId&0xFF)<<16)|((shaderId&0xFF)<<8)|(z_order&0xFF);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof RenderUnit) && (hashCode == obj.hashCode());
    }
}
