package ru.sipaha.engine.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.graphics.renderlayers.BatchesRenderLayer;
import ru.sipaha.engine.graphics.renderlayers.RenderLayer;
import ru.sipaha.engine.utils.Array;

/**
 * Created on 30.09.2014.
 */

public class Renderer {
    public final static String DEFAULT_LAYER = "Default";

    private ObjectMap<String, RenderLayer> layersByName;
    private Array<RenderLayer> layers;

    public Renderer() {
        layersByName = new ObjectMap<>();
        layers = new Array<>(true, 2, RenderLayer.class);
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for(RenderLayer layer : layers) layer.render();
    }

    public void addRenderLayer(RenderLayer layer) {
        layersByName.put(layer.name, layer);
        layers.add(layer);
        layers.sort();
    }

    public void removeRenderLayer(String name) {
        RenderLayer layer = layersByName.remove(name);
        layers.removeValue(layer, true);
    }

    public void removeRenderLayer(RenderLayer layer) {
        layersByName.remove(layer.name);
        layers.removeValue(layer, true);
    }

    public RenderLayer getRenderLayer() {
        return getRenderLayer(DEFAULT_LAYER);
    }

    public RenderLayer getRenderLayer(String name) {
        RenderLayer layer = layersByName.get(name);
        if(layer == null) {
            layer = new BatchesRenderLayer(name);
            addRenderLayer(layer);
        }
        return layer;
    }

    public void addRenderUnit(RenderUnit renderUnit) {
        getRenderLayer(renderUnit.renderLayerTag).add(renderUnit);
    }

    public void removeRenderUnit(RenderUnit renderUnit) {
        layersByName.get(renderUnit.renderLayerTag).remove(renderUnit);
    }

    public void prepareRenderUnit(RenderUnit renderUnit) {
        getRenderLayer(renderUnit.renderLayerTag).prepare(renderUnit);
    }

    public void resize(int width ,int height) {
        for(RenderLayer layer : layers) layer.resize(width, height);
    }

    public void initialize() {
        for(RenderLayer layer : layers) layer.initialize();
    }
}
