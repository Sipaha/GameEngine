package ru.sipaha.engine.graphics;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.sipaha.engine.utils.Array;

/**
 * Created on 27.10.2014.
 */

public class RenderBuffer {
    private static final int DEFAULT_SIZE = 1000;
    private static final int QUADS_LIMIT = 5460;//short
    private static final int QUAD_SIZE = 20;
    private static final float BUFFER_INCREASE_RATE = 2f;

    private static final Array<RenderBuffer> staticBuffersPool = new Array<>(RenderBuffer.class);
    private static final RenderBuffer dynamicBuffer = new RenderBuffer();

    public static int renderCalls = 0;

    private float[] buffer;
    private int size = 0;
    private Mesh mesh;

    private ShaderProgram shader;

    public RenderBuffer() {
        this(DEFAULT_SIZE);
    }

    public RenderBuffer(int capacity) {
        setSize(capacity);
    }

    public void render(RenderUnit unit) {
        unit.render(this);
    }

    public void render(Iterable<RenderUnit> renderUnits) {
        for(RenderUnit unit : renderUnits) render(unit);
    }

    public void begin(ShaderProgram shaderProgram) {
        shader = shaderProgram;
    }

    public void end() {
        flush();
        size = 0;
    }

    public boolean setRenderDataTo(RenderUnit unit) {
        size = unit.setRenderData(buffer, size);
        return size < buffer.length;
    }

    public void render(float[] vertices, int from, int length) {
        if(buffer.length <= size+length) {
            flush();
            size = 0;
            ensureCapacity();
        }
        System.arraycopy(vertices, from, buffer, size, length);
        size += length;
    }

    public boolean ensureCapacity() {
        if(buffer.length*QUAD_SIZE < QUADS_LIMIT) {
            setSize(Math.min((int)(buffer.length*BUFFER_INCREASE_RATE/QUAD_SIZE), QUADS_LIMIT));
            return true;
        }
        return false;
    }

    public void setSize(int size) {
        buffer = new float[size*20];

        mesh = new Mesh(Mesh.VertexDataType.VertexArray, false, size * 4, size * 6,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

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

        this.size = 0;
    }

    public void flush () {
        if (size == 0) return;
        int count = size * 6/QUAD_SIZE;

        Mesh mesh = this.mesh;
        mesh.setVertices(buffer, 0, size);
        mesh.getIndicesBuffer().position(0);
        mesh.getIndicesBuffer().limit(count);

        mesh.render(shader, GL20.GL_TRIANGLES, 0, count);

        renderCalls++;
    }

    public int size() {
        return size;
    }

    public void reset() {
        size = 0;
    }

    public void dispose () {
        mesh.dispose();
    }

    public void free() {
        if(this != dynamicBuffer) staticBuffersPool.add(this);
    }

    public static RenderBuffer getDynamicBuffer() {
        return dynamicBuffer;
    }

    public static RenderBuffer getStaticBuffer() {
        return staticBuffersPool.size > 0 ? staticBuffersPool.pop() : new RenderBuffer();
    }
}
