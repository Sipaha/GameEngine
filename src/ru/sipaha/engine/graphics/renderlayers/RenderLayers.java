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

    public void prepareBatchForGameObject(GameObject gameObject) {
        GameObjectRenderer renderer = gameObject.renderer;
        GameObjectsRenderLayer layer = (GameObjectsRenderLayer)layersByName.get(renderer.renderLayer);
        if(layer == null) {
            layer = new GameObjectsRenderLayer(renderer.renderLayer);
            addRenderLayer(layer);
        }
        layer.prepareBatchForGameObject(renderer);
    }

    public void addGameObject(GameObject gameObject) {
        GameObjectRenderer renderer = gameObject.renderer;
        GameObjectsRenderLayer layer = (GameObjectsRenderLayer)layersByName.get(renderer.renderLayer);
        try {
            layer.addGameObject(gameObject);
        } catch (NullPointerException e) {
            Gdx.app.error("GameEngine","Render layer is not created for this game object! " +
                                                                    "layer name = "+renderer.renderLayer);
        }
    }

    public void removeGameObject(GameObject gameObject) {
        GameObjectRenderer renderer = gameObject.renderer;
        GameObjectsRenderLayer layer = (GameObjectsRenderLayer)layersByName.get(renderer.renderLayer);
        try {
            layer.removeGameObject(gameObject);
        } catch (NullPointerException e) {
            Gdx.app.error("GameEngine","Render layer is not created for this game object! " +
                                                                    "layer name = "+renderer.renderLayer);
        }
    }

    public void update() {
        for(RenderLayer layer : layers) layer.update();
    }
}
