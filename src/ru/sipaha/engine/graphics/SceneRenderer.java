package ru.sipaha.engine.graphics;

import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.MeshRenderer;
import ru.sipaha.engine.graphics.batches.Batch;
import ru.sipaha.engine.graphics.batches.BatchArray;
import ru.sipaha.engine.graphics.batches.GOBatch;
import ru.sipaha.engine.graphics.batches.Batches;

public class SceneRenderer {

    public static int renderCalls = 0;

    private final Camera camera = new Camera();
    private final Camera staticCamera = new Camera();

    private final Batches batches = new Batches();

    public void render() {
        BatchArray.renderCalls = 0;
        BatchArray[] bs = batches.batchesArrays.items;
        for(int i = 0, s = batches.batchesArrays.size; i < s; i++) bs[i].draw();
        renderCalls = BatchArray.renderCalls;
    }

    public void resize(int width, int height) {
        camera.setViewport(width, height);
        staticCamera.setViewport(width, height);
    }

    public void addGameObject(GameObject go) {
        batches.addGameObject(go);
    }

    public void removeGameObject(GameObject go) {
        batches.removeGameObject(go);
    }

    public void prepareBatchForGameObject(GameObject go) {
        if(!batches.goBatchesByUnit.containsKey(go.renderer)) {
            MeshRenderer renderer = go.renderer;
            GOBatch batch = new GOBatch(renderer);
            batch.setCombinedMatrix(go.renderer.fixedCamera ? staticCamera.combined : camera.combined);
            batches.goBatchesByUnit.put(renderer, batch);
            batches.addBatch(batch);
        }
    }

    public void rebuildBatchesArrays() {
        batches.rebuildBatchesArrays();
    }

    public void addBatch(Batch b, boolean fixedCamera) {
        b.setCombinedMatrix(fixedCamera ? staticCamera.combined : camera.combined);
        batches.addBatch(b);
    }
}
