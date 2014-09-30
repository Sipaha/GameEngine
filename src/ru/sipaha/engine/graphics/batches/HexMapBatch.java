package ru.sipaha.engine.graphics.batches;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.sipaha.engine.graphics.RenderUnit;

public class HexMapBatch extends Batch {


    public HexMapBatch(RenderUnit batch) {
        super(batch);
    }

    public HexMapBatch(ShaderProgram shader, Texture texture, int z_order) {
        super(shader, texture, z_order);
    }

    @Override
    protected int prepareVertices(float[] vertices, int idx) {
        return 0;
    }

    @Override
    public int getWeight() {
        return 0;
    }
}
