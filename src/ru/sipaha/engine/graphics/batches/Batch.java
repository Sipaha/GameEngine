package ru.sipaha.engine.graphics.batches;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import ru.sipaha.engine.gameobjectdata.GameObjectRenderer;
import ru.sipaha.engine.graphics.RenderUnit;

public abstract class Batch extends RenderUnit {

    public Batch (ShaderProgram shader, Texture texture, int zOrder) {
        super(texture, shader, zOrder);
    }

    public Batch(RenderUnit renderUnit) {
        super(renderUnit);
    }

    protected abstract int prepareVertices(float[] vertices, int idx);
}
