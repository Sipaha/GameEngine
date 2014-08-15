package ru.sipaha.engine.graphics;

import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.graphics.batches.Batch;
import ru.sipaha.engine.graphics.batches.utils.Batches;

public class SceneRenderer {

    private final Camera camera = new Camera();
    private final Camera staticCamera = new Camera();

    private final Batches batches = new Batches();

    public void render() {
        Batch[] bs = batches.sortedBatches.items;
        for(int i = 0, s = batches.sortedBatches.size; i < s; i++) bs[i].draw();
    }

    public void resize(int width, int height) {
        camera.setViewport(width, height);
        staticCamera.setViewport(width, height);
    }

    public void addGO(GameObject go) {
        batches.addGO(go);
    }

    public void removeGO(GameObject go) {
        batches.removeGO(go);
    }
}
