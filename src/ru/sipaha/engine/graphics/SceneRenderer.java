package ru.sipaha.engine.graphics;

import ru.sipaha.engine.gameobjectdata.GameObjectRenderer;
import ru.sipaha.engine.graphics.batches.Batch;
import ru.sipaha.engine.graphics.batches.BatchArray;
import ru.sipaha.engine.graphics.batches.GameObjectsBatch;
import ru.sipaha.engine.graphics.batches.Batches;

public class SceneRenderer {

    public static int renderCalls = 0;

    private final Batches batches = new Batches();

    public void render() {
        BatchArray.renderCalls = 0;
        BatchArray[] bs = batches.batchesArrays.items;
        for(int i = 0, s = batches.batchesArrays.size; i < s; i++) bs[i].draw();
        renderCalls = BatchArray.renderCalls;
    }

    public void addGameObjectRenderer(GameObjectRenderer renderer) {
        batches.addGameObjectRenderer(renderer);
    }

    public void removeGameObjectRenderer(GameObjectRenderer renderer) {
        batches.removeGameObjectRenderer(renderer);
    }

    public void prepareBatchForGameObjectRenderer(GameObjectRenderer renderer) {
        if(!batches.goBatchesByUnit.containsKey(renderer)) {
            GameObjectsBatch batch = new GameObjectsBatch(renderer);
            batches.goBatchesByUnit.put(renderer, batch);
            batches.addBatch(batch);
        }
    }

    public void rebuildBatchesArrays() {
        batches.rebuildBatchesArrays();
    }

    public void addBatch(Batch b, boolean fixedCamera) {
        batches.addBatch(b);
    }
}
