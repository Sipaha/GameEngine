package ru.frozen.prolen.core.graphics;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import ru.frozen.prolen.core.gameobject.MeshRenderer;
import ru.frozen.prolen.core.gameobject.GameObject;
import ru.frozen.prolen.core.graphics.batches.Batch;
import ru.frozen.prolen.core.graphics.batches.GOBatch;

public class SceneRenderer {

    private final Camera camera = new Camera();
    private final Camera staticCamera = new Camera();

    private Array<Batch> batches = new Array<>(true, 16, Batch.class);
    private ObjectMap<RenderUnit, GOBatch> GOBatchesByUnit = new ObjectMap<>();

    public void render() {
        Batch[] bs = batches.items;
        for(int i = 0, s = batches.size; i < s; i++) {
            bs[i].draw();
        }
    }

    public void addGO(GameObject go) {
        GOBatch b = GOBatchesByUnit.get(go.renderer);
        if(b == null) {
            MeshRenderer renderer = go.renderer;
            b = new GOBatch(32, renderer.shader, renderer.texture, renderer.z_order);
            GOBatchesByUnit.put(renderer, b);
            addBatch(b, renderer.fixed_camera);
        } else if(b.isFull()) {
            batches.removeValue(b, true);
            GOBatch new_b = new GOBatch(b.size*2, b.shader, b.texture, b.z_order);
            new_b.addGameObjects(b);
            b = new_b;
            GOBatchesByUnit.put(go.renderer, b);
            addBatch(b, go.renderer.fixed_camera);
        }
        b.addGameObject(go);
    }

    public void addBatch(Batch batch, boolean fixed_camera) {
        batch.setCombinedMatrix(fixed_camera ? staticCamera.combined : camera.combined);
        int idx = 0;
        Batch[] bs = batches.items;
        while (bs[idx] != null
                && idx < batches.size
                && batch.z_order > bs[idx].z_order) idx++;
        if(idx == 0 || !bs[idx-1].equals(bs[idx])) {
            while (bs[idx] != null
                    && idx < batches.size
                    && batch.z_order == bs[idx].z_order
                    && !bs[idx].equals(batch)) idx++;
        }
        while (bs[idx] != null
                && idx < batches.size
                && bs[idx].equals(batch)) idx++;
        if(idx == batches.size) batches.ensureCapacity((int)(batches.size * 1.5f));
        this.batches.insert(idx, batch);
        for(int i = 0, s = batches.size-1; i < s; i++) {
            if(bs[i].equals(bs[i+1])) bs[i].setLink(bs[i+1]);
            else bs[i].setLink(null);
        }
    }

    public void removeGO(GameObject go) {
        GOBatchesByUnit.get(go.renderer).removeGameObject(go);
    }

    public void resize(int width, int height) {
        camera.setViewport(width, height);
        staticCamera.setViewport(width, height);
    }
}
