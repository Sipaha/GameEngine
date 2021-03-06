package ru.sipaha.engine.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.structures.Bounds;

public class Batch extends RenderUnit {

    private Array<RenderUnit> renderUnits = new Array<>(true, 4, RenderUnit.class);
    private Array<RenderBuffer> renderBuffers = new Array<>(true, 2, RenderBuffer.class);
    private int currentStaticBuffer = 0;
    private boolean reqStaticUpdate = true;

    public Batch() {}

    public void add(RenderUnit unit) {
        if(renderUnits.size == 0) {
            setPropertiesFrom(unit);
            if(!isStatic.get()) renderBuffers.add(RenderBuffer.getDynamicBuffer());
        }
        renderUnits.add(unit);
        if(isStatic.get()) reqStaticUpdate = !addStaticUnit(unit);
    }

    public void remove(RenderUnit unit) {
        renderUnits.removeValue(unit, true);
        if(isStatic.get()) reqStaticUpdate = true;
    }

    private RenderBuffer getCurrentStaticBuffer() {
        RenderBuffer buffer;
        if(currentStaticBuffer == renderBuffers.size) {
            buffer = RenderBuffer.getStaticBuffer();
            renderBuffers.add(buffer);
        } else {
            buffer = renderBuffers.get(currentStaticBuffer);
        }
        return buffer;
    }

    private boolean addStaticUnit(RenderUnit unit) {
        RenderBuffer buffer = getCurrentStaticBuffer();
        unit.setRenderData(buffer);
        if(buffer.isOverflow()) {
            if(!buffer.ensureCapacity()) {
                currentStaticBuffer++;
                unit.setRenderData(getCurrentStaticBuffer());
            } else {
                return false;
            }
        }
        return true;
    }

    public void updateStaticRenderData() {
        reqStaticUpdate = true;
    }

    public void draw(Matrix4 combined) {
        begin(combined);
        if(isStatic.get()) {
            if(reqStaticUpdate) {
                for(RenderBuffer buffer : renderBuffers) {
                    buffer.reset();
                }
                currentStaticBuffer = 0;
                int currBuff = 0;
                int currentBufferStartUnitIdx = 0;
                for(int i = 0; i < renderUnits.size; i++) {
                    RenderUnit unit = renderUnits.get(i);
                    if(!addStaticUnit(unit)) {
                        i = currentBufferStartUnitIdx-1;
                    } else if(currBuff < currentStaticBuffer) {
                        currentBufferStartUnitIdx = i;
                        currBuff = currentStaticBuffer;
                    }
                }
                reqStaticUpdate = false;
            }
        } else {
            RenderBuffer buffer = renderBuffers.get(0);
            for(RenderUnit unit : renderUnits) {
                unit.render(buffer);
            }
        }
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
        ShaderProgram shader = this.shader.get();
        shader.begin();
        shader.setUniformMatrix("u_projTrans", combined);
        shader.setUniformi("u_texture", 0);
        getTexture().bind();
        if (blendingEnabled.get()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(blendSrcFunc.get(), blendDstFunc.get());
        } else {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        for(RenderBuffer buffer : renderBuffers) {
            buffer.begin(shader);
        }
    }

    public void end () {
        if(isStatic.get()) {
            for(RenderBuffer buffer : renderBuffers) {
                buffer.flush();
            }
        } else {
            renderBuffers.get(0).end();
        }
        GL20 gl = Gdx.gl;
        gl.glDepthMask(true);
        shader.get().end();
    }

    public void clear() {
        renderUnits.clear();
        for(int i = 0; i < renderBuffers.size; i++) {
            renderBuffers.items[i].free();
            renderBuffers.items[i] = null;
        }
        renderBuffers.size = 0;
        currentStaticBuffer = 0;
    }

    public int getSize() {
        return renderUnits.size;
    }
}
