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

    private Array<RenderUnit> renderUnits = new Array<>(true, 4, RenderUnit.class);
    private RenderBuffer renderBuffer;

    public Batch() {}

    public void add(RenderUnit unit) {
        if(renderUnits.size == 0) {
            setPropertiesFrom(unit);
            renderBuffer = isStatic() ? RenderBuffer.getStaticBuffer() : RenderBuffer.getDynamicBuffer();
        }
        renderUnits.add(unit);
        if(isStatic() && !renderBuffer.setRenderDataTo(unit)) {
            renderBuffer.ensureCapacity();
            for(RenderUnit u : renderUnits) {
                renderBuffer.setRenderDataTo(u);
            }
        }
    }

    public void draw(Matrix4 combined) {
        begin(combined);
        if(!isStatic()) for(RenderUnit unit : renderUnits) renderBuffer.render(unit);
        end();
    }

    @Override
    public void render(RenderBuffer buffer) {
        for(RenderUnit unit : renderUnits) unit.render(buffer);
    }

    @Override
    public int getRenderSize() {
        int sum = 0;
        for(RenderUnit u : renderUnits) sum += u.getRenderSize();
        return sum;
    }

    public void begin (Matrix4 combined) {
        GL20 gl = Gdx.gl;
        gl.glDepthMask(false);
        ShaderProgram shader = getShader();
        shader.begin();
        shader.setUniformMatrix("u_projTrans", combined);
        shader.setUniformi("u_texture", 0);
        getTexture().bind();
        if (isBlendingEnabled()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(getBlendSrcFunc(), getBlendDstFunc());
        } else {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        renderBuffer.begin(getShader());
    }

    public void end () {
        if(isStatic()) renderBuffer.flush();
        else renderBuffer.end();
        GL20 gl = Gdx.gl;
        gl.glDepthMask(true);
        getShader().end();
    }

    public void clear() {
        renderUnits.clear();
        if(renderBuffer != null) {
            renderBuffer.free();
            renderBuffer = null;
        }
    }

    public int getSize() {
        return renderUnits.size;
    }

    public void dispose () {
        renderBuffer.dispose();
    }
}
