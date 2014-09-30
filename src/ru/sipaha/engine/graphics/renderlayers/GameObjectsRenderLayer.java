package ru.sipaha.engine.graphics.renderlayers;

import com.badlogic.gdx.Gdx;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.GameObjectRenderer;
import ru.sipaha.engine.graphics.batches.Batch;
import ru.sipaha.engine.graphics.batches.BatchArray;
import ru.sipaha.engine.graphics.batches.Batches;
import ru.sipaha.engine.graphics.batches.GameObjectsBatch;
import ru.sipaha.engine.utils.Array;

/**
 * Created on 30.09.2014.
 */

public class GameObjectsRenderLayer extends RenderLayer {

    private final Batches batches = new Batches();
    private boolean initialized = false;

    public GameObjectsRenderLayer(String name) {
        super(name);
    }

    @Override
    public void render() {
        Array<BatchArray> batchesArrays = batches.batchesArrays;
        BatchArray[] bs = batchesArrays.items;
        for(int i = 0, s = batchesArrays.size; i < s; i++) {
            bs[i].draw(camera.combined);
        }
    }

    public void addBatch(Batch b) {
        batches.addBatch(b);
    }

    public void prepareBatchForGameObject(GameObjectRenderer renderer) {
        if(!batches.goBatchesByUnit.containsKey(renderer)) {
            GameObjectsBatch batch = new GameObjectsBatch(renderer);
            batches.goBatchesByUnit.put(renderer, batch);
            batches.addBatch(batch);
        }
    }

    public void addGameObject(GameObject gameObject) {
        GameObjectRenderer renderer = gameObject.renderer;
        try {
            batches.addGameObjectRenderer(gameObject.renderer);
        } catch (NullPointerException n) {
            Gdx.app.error("GameEngine","Game object not initialized before!! "+gameObject);
            GameObjectsBatch batch = new GameObjectsBatch(renderer);
            batches.goBatchesByUnit.put(renderer, batch);
            batches.addBatch(batch);
            if(initialized) {
                batch.addGameObjectRenderer(renderer);
                batches.rebuildBatchesArrays();
            }
        }
    }

    @Override
    public void update() {
        super.update();
        batches.rebuildBatchesArrays();
    }

    public void removeGameObject(GameObject gameObject) {
        batches.removeGameObjectRenderer(gameObject.renderer);
    }

    public void initialize() {
        batches.rebuildBatchesArrays();
        camera.reset();
        initialized = true;
    }
}
