package ru.sipaha.engine.graphics.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.sipaha.engine.graphics.RenderUnit;

/**
 * Created on 13.10.2014.
 */

public abstract class UIRenderUnit extends RenderUnit {
    protected UIRenderUnit() {
    }

    protected UIRenderUnit(RenderUnit unit) {
        super(unit);
    }

    protected UIRenderUnit(Texture t) {
        super(t);
    }

    protected UIRenderUnit(Texture t, int zOrder) {
        super(t, zOrder);
    }

    protected UIRenderUnit(Texture t, ShaderProgram s, int zOrder) {
        super(t, s, zOrder);
    }

    public abstract void setBounds(float left, float right, float top, float bottom);

}
