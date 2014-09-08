package ru.sipaha.engine.gameobjectdata;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.graphics.RenderUnit;

public class GameObjectRenderer extends RenderUnit {

    public boolean visible = true;
    public float[] renderData;

    public GameObjectRenderer(GameObjectRenderer renderer) {
        super(renderer);
    }

    public GameObjectRenderer(Texture t) {
        super(t);
    }

    public GameObjectRenderer(Texture t, int zOrder) {
        super(t, zOrder);
    }

    public GameObjectRenderer(Texture t, ShaderProgram s, int zOrder) {
        super(t, s, zOrder);
    }

    public void setEntities(Entity[] entities) {
        renderData = new float[entities.length*EntityRenderer.ENTITY_RENDER_SIZE];
        for(int i = 0; i < entities.length; i++) {
            entities[i].renderer.setRenderData(renderData, i*EntityRenderer.ENTITY_RENDER_SIZE);
        }
    }

    public int render(float[] vertices, int pos) {
        if(visible) {
            System.arraycopy(renderData, 0, vertices, pos, renderData.length);
            return pos + renderData.length;
        } else {
            return pos;
        }
    }

    public void reset(GameObjectRenderer template) {
        visible = template.visible;
    }

    public void setLinearFilter() {
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void setNearestFilter() {
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }
}
