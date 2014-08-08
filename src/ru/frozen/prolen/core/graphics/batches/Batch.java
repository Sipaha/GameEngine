package ru.frozen.prolen.core.graphics.batches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import ru.frozen.prolen.core.graphics.RenderUnit;

public abstract class Batch extends RenderUnit implements Disposable {

    private Mesh mesh;

    protected final float[] vertices;
    protected int verticesCount = 0;

    private Matrix4 combinedMatrix;

    private boolean blendingDisabled = false;
    private int blendSrcFunc = GL20.GL_SRC_ALPHA;
    private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;

    private Batch link = null;

    private boolean firstDraw = true;
    private boolean isStatic = false;

    public Batch (int maxVertices, int maxIndices, ShaderProgram shader, Texture texture, int z_order) {
        super(texture, shader, z_order);

        mesh = new Mesh(Mesh.VertexDataType.VertexArray, false, maxVertices, maxIndices,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        vertices = new float[maxVertices*5];
    }

    protected abstract void prepareIndices(Mesh mesh);

    public void draw() {
        if(firstDraw) {
            prepareIndices(mesh);
            firstDraw = false;
        }
        begin();
        if(!isStatic) {
            prepareVertices();
            Batch b = link;
            while (b != null) {
                System.arraycopy(b.vertices, 0, vertices, verticesCount, b.verticesCount);
                verticesCount += b.verticesCount;
                b = b.link;
            }
        }
        end();
    }

    protected abstract void prepareVertices();

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

        mesh.render(shader, GL20.GL_TRIANGLES, 0, count);

        verticesCount = 0;
    }

    public void setBlending(boolean blendingEnabled) {
        blendingDisabled = !blendingEnabled;
    }

    public void setBlendFunction (int srcFunc, int dstFunc) {
        blendSrcFunc = srcFunc;
        blendDstFunc = dstFunc;
    }

    public void dispose () {
        mesh.dispose();
        shader.dispose();
    }

    public void setCombinedMatrix (Matrix4 combined) {
        combinedMatrix = combined;
    }

    private void setupMatrices () {
        shader.setUniformMatrix("u_projTrans", combinedMatrix);
        shader.setUniformi("u_texture", 0);
    }

    public void setStatic(boolean isStatic) {
        if(isStatic) prepareVertices();
        this.isStatic = isStatic;
    }

    public void setLink(Batch b) {
        link = b;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
