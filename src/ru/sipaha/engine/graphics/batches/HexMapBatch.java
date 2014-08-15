package ru.sipaha.engine.graphics.batches;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class HexMapBatch extends Batch {

    public HexMapBatch(int maxVertices, int maxIndices, ShaderProgram shader, Texture texture, int z_order) {
        super(maxVertices, maxIndices, shader, texture, z_order);
    }

    @Override
    protected void prepareIndices(Mesh mesh) {

    }

    @Override
    protected void prepareVertices() {

    }
}
