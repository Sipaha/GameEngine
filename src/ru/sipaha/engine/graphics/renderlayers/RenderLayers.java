package ru.sipaha.engine.graphics.renderlayers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.GameObjectRenderer;
import ru.sipaha.engine.utils.Array;

/**
 * Created on 30.09.2014.
 */

public class RenderLayers {
    public final static String DEFAULT_LAYER = "Default";

    private ObjectMap<String, RenderLayer> layersByName;
    private Array<RenderLayer> layers;

    public RenderLayers() {
        layersByName = new ObjectMap<>();
        layers = new Array<>(true, 2, RenderLayer.class);
    }

    public void render() {
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

    public RenderLayer getRenderLayer(String name) {
        return layersByName.get(name);
    }

    public RenderLayer getRenderLayer() {
        return layersByName.get(DEFAULT_LAYER);
    }

    public void prepareBatchForGameObject(GameObject gameObject) {
        GameObjectRenderer renderer = gameObject.renderer;
        BatchesRenderLayer layer = (BatchesRenderLayer)layersByName.get(renderer.renderLayerTag);
        if(layer == null) {
            layer = new BatchesRenderLayer(renderer.renderLayerTag);
            addRenderLayer(layer);
        }
        layer.prepareBatchForGameObject(renderer);
    }

    public void addGameObject(GameObject gameObject) {
        GameObjectRenderer renderer = gameObject.renderer;
        BatchesRenderLayer layer = (BatchesRenderLayer)layersByName.get(renderer.renderLayerTag);
        try {
            layer.addGameObject(gameObject);
        } catch (NullPointerException e) {
            Gdx.app.error("GameEngine","Render layer is not created for this game object! " +
                                                                    "layer name = "+renderer.renderLayerTag);
        }
    }

    public void removeGameObject(GameObject gameObject) {
        GameObjectRenderer renderer = gameObject.renderer;
        BatchesRenderLayer layer = (BatchesRenderLayer)layersByName.get(renderer.renderLayerTag);
        try {
            layer.removeGameObject(gameObject);
        } catch (NullPointerException e) {
            Gdx.app.error("GameEngine","Render layer is not created for this game object! " +
                                                                    "layer name = "+renderer.renderLayerTag);
        }
    }

    public void initialize() {
        for(RenderLayer layer : layers) layer.initialize();
    }
}
