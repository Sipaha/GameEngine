package ru.sipaha.engine.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import ru.sipaha.engine.utils.Array;

public class Batch extends RenderUnit {
    public static final int DEFAULT_MAX_SIZE = 1000;

    public static int renderCalls = 0;

    private Array<RenderUnit> renderUnits = new Array<>(true, 4, RenderUnit.class);

    private Mesh mesh;

    protected final float[] vertices;
    protected int verticesCount = 0;

    public Batch() {
        this(DEFAULT_MAX_SIZE);
    }

    public Batch(int size) {
        if (size > 5460) throw new IllegalArgumentException("Can't have more than 5460 sprites per BatchArray: " + size);

        mesh = new Mesh(Mesh.VertexDataType.VertexArray, false, size * 4, size * 6,
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

    public void add(RenderUnit unit) {
        if(renderUnits.size == 0) set(unit);
        renderUnits.add(unit);
        if(isStatic) {
            verticesCount = unit.render(vertices, verticesCount);
        }
    }

    public void draw(Matrix4 combined) {
        begin(combined);
        if(!isStatic) {
            verticesCount = 0;
            try {
                RenderUnit[] drawableArr = renderUnits.items;
                for (int i = 0, s = renderUnits.size; i < s; i++) {
                    verticesCount = drawableArr[i].render(vertices, verticesCount);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Gdx.app.error("Game Engine", "Vertices length is too low! Length = "+vertices.length);
            }
        }
        end();
    }

    @Override
    public int render(float[] vertices, int pos) {
        for(RenderUnit unit : renderUnits) pos = unit.render(vertices, pos);
        return pos;
    }

    public void begin (Matrix4 combined) {
        GL20 gl = Gdx.gl;
        gl.glDepthMask(false);
        shader.begin();
        shader.setUniformMatrix("u_projTrans", combined);
        shader.setUniformi("u_texture", 0);
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

    public void clear() {
        renderUnits.clear();
    }

    public int getSize() {
        return renderUnits.size;
    }

    public void dispose () {
        mesh.dispose();
    }
}
