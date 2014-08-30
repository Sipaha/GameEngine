package ru.sipaha.engine.graphics;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.PhysicsWorld;
import ru.sipaha.engine.gameobjectdata.GameObjectRenderer;
import ru.sipaha.engine.graphics.batches.Batch;
import ru.sipaha.engine.graphics.batches.BatchArray;
import ru.sipaha.engine.graphics.batches.GameObjectsBatch;
import ru.sipaha.engine.graphics.batches.Batches;

public class SceneRenderer {

    public static int renderCalls = 0;

    private final Camera camera = new Camera();
    private final Camera staticCamera = new Camera();

    private final Batches batches = new Batches();

    private int screenWidth, screenHeight;

    public void render() {
        BatchArray.renderCalls = 0;
        BatchArray[] bs = batches.batchesArrays.items;
        for(int i = 0, s = batches.batchesArrays.size; i < s; i++) bs[i].draw();
        renderCalls = BatchArray.renderCalls;
    }

    public void resize(int width, int height) {
        camera.setViewport(width, height);
        staticCamera.setViewport(width, height);
        screenWidth = width;
        screenHeight = height;
    }

    public void addGameObject(GameObject go) {
        batches.addGameObject(go);
    }

    public void removeGameObject(GameObject go) {
        batches.removeGameObject(go);
    }

    public void prepareBatchForGameObject(GameObject gameObject) {
        if(!batches.goBatchesByUnit.containsKey(gameObject.renderer)) {
            GameObjectRenderer renderer = gameObject.renderer;
            GameObjectsBatch batch = new GameObjectsBatch(renderer);
            batch.setCombinedMatrix(gameObject.renderer.fixedCamera ? staticCamera.combined : camera.combined);
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

    public Camera getCamera() {
        return camera;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
