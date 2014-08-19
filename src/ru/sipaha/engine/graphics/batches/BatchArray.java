package ru.sipaha.engine.graphics.batches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.Array;

public class BatchArray extends RenderUnit {
    public static final int DEFAULT_MAX_SIZE = 1000;

    public static int renderCalls = 0;

    private Array<Batch> batches = new Array<>(true, 4, Batch.class);

    private Mesh mesh;

    protected final float[] vertices;
    protected int verticesCount = 0;

    private Matrix4 combinedMatrix;

    private boolean blendingDisabled;
    private int blendSrcFunc;
    private int blendDstFunc;

    private boolean isStatic = false;

    public BatchArray(BatchGroup group) {
        this(DEFAULT_MAX_SIZE, group);
    }

    public BatchArray(int size, BatchGroup group) {
        super(group);
        if (size > 5460) throw new IllegalArgumentException("Can't have more than 5460 sprites per BatchArray: " + size);
        Batch batch = group.batches.get(0);
        isStatic = batch.isStatic;
        combinedMatrix = batch.getCombinedMatrix();
        blendDstFunc = batch.getBlendDstFunc();
        blendSrcFunc = batch.getBlendSrcFunc();
        blendingDisabled = batch.isBlendingDisabled();
        add(group);

        mesh = new Mesh(Mesh.VertexDataType.VertexArray, false, size*4, size*6,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        vertices = new float[size*20];

        int len = size * 6;
        short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
        }
        mesh.setIndices(indices);
    }

    private void setupMatrices () {
        shader.setUniformMatrix("u_projTrans", combinedMatrix);
        shader.setUniformi("u_texture", 0);
    }

    public void add(BatchGroup group) {
        for(int i = 0; i < group.size(); i++) {
            Batch b = group.batches.get(i);
            if(isStatic) {
                verticesCount = b.prepareVertices(vertices, verticesCount);
            }
            batches.add(b);
        }
    }

    public void draw() {
        begin();
        if(!isStatic) {
            verticesCount = 0;
            Batch[] batchesArr = batches.items;
            for(int i = 0, s = batches.size; i < s; i++) {
                verticesCount = batchesArr[i].prepareVertices(vertices,verticesCount);
            }
        }
        end();
    }

    public void begin () {
        GL20 gl = Gdx.gl;
        gl.glDepthMask(false);
        shader.begin();
        setupMatrices();
    }

    public void end () {
        if (verticesCount > 0) flush();
        GL20 gl = Gdx.gl;
        gl.glDepthMask(true);
        shader.end();
    }

    public void flush () {
        int count = verticesCount * 6/20;

        texture.bind();
        Mesh mesh = this.mesh;
        mesh.setVertices(vertices, 0, verticesCount);
        mesh.getIndicesBuffer().position(0);
        mesh.getIndicesBuffer().limit(count);

        if (blendingDisabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        } else {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            if (blendSrcFunc != -1) Gdx.gl.glBlendFunc(blendSrcFunc, blendDstFunc);
        }

        renderCalls++;
        mesh.render(shader, GL20.GL_TRIANGLES, 0, count);
    }

    public void dispose () {
        mesh.dispose();
    }


}
