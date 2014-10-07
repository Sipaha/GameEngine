package ru.sipaha.engine.graphics.renderlayers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.GameObjectRenderer;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.graphics.batches.*;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.IntIntMap;

/**
 * Created on 30.09.2014.
 */

public class BatchesRenderLayer extends RenderLayer {

    private ObjectMap<RenderUnit, BatchGroup> batchesGroups = new ObjectMap<>();
    private Array<BatchArray> batchesArrays = new Array<>(true, 16, BatchArray.class);
    private ObjectMap<RenderUnit, GameObjectsBatch> goBatchesByUnit = new ObjectMap<>();
    private boolean initialized = false;

    public BatchesRenderLayer(String name) {
        super(name);
    }

    @Override
    public void render() {
        BatchArray[] bs = batchesArrays.items;
        for(int i = 0, s = batchesArrays.size; i < s; i++) {
            bs[i].draw(camera.combined);
        }
    }

    public void prepareBatchForGameObject(GameObjectRenderer renderer) {
        if(!goBatchesByUnit.containsKey(renderer)) {
            GameObjectsBatch batch = new GameObjectsBatch(renderer);
            goBatchesByUnit.put(renderer, batch);
            addBatch(batch);
        }
    }

    public void addGameObject(GameObject gameObject) {
        GameObjectRenderer renderer = gameObject.renderer;
        renderer.renderLayer = this;
        try {
            goBatchesByUnit.get(renderer).addGameObjectRenderer(renderer);
        } catch (NullPointerException n) {
            Gdx.app.error("GameEngine","Game object not initialized before!! "+gameObject);
            GameObjectsBatch batch = new GameObjectsBatch(renderer);
            goBatchesByUnit.put(renderer, batch);
            addBatch(batch);
            if(initialized) {
                batch.addGameObjectRenderer(renderer);
                rebuildBatchesArrays();
            }
        }
    }

    public void removeGameObject(GameObject gameObject) {
        GameObjectRenderer renderer = gameObject.renderer;
        goBatchesByUnit.get(renderer).removeGameObjectRenderer(renderer);
    }

    public void addBatch(Batch batch) {
        BatchGroup batchGroup = batchesGroups.get(batch);
        if(batchGroup == null) {
            batchGroup = new BatchGroup(batch);
            batchesGroups.put(batch, batchGroup);
        } else batchGroup.batches.add(batch);
    }

    public void rebuildBatchesArrays() {
        com.badlogic.gdx.utils.Array<BatchGroup> groups = batchesGroups.values().toArray();
        groups.sort();//groups.sort(BatchGroup.batchGroupsComparator);
        for(BatchGroup b : groups) b.reset();

        IntIntMap layers = new IntIntMap();
        for(int i = 0, layer = -1, size = groups.size; i < size; i++) {
            int currLayer = groups.get(i).getZOrder();
            if(currLayer > layer) {
                layer = currLayer;
                layers.put(currLayer, i);
            }
        }

        for(int i = 0; i < layers.size()-1; i++) {
            IntIntMap.Entry currLayer = layers.getByIndex(i);
            IntIntMap.Entry nextLayer = layers.getByIndex(i + 1);
            final int currFirst = currLayer.value;
            final int currLast = nextLayer.value-1;
            final int nextFirst = nextLayer.value;
            final int nextLast = i < layers.size()-2 ? layers.getByIndex(i + 2).value-1 : groups.size-1;

            for (int currIdx = currFirst; currIdx <= currLast; currIdx++) {
                BatchGroup currGroup = groups.get(currIdx);
                for (int nextIdx = nextFirst; nextIdx <= nextLast; nextIdx++) {
                    BatchGroup nextGroup = groups.get(nextIdx);
                    if(currGroup.equalsIgnoreZOrder(nextGroup)) {
                        int priority = currGroup.size()+nextGroup.size();
                        if(currGroup.upPriority < priority) {
                            currGroup.upPriority = priority;
                            currGroup.nextLink = nextGroup;
                        }
                    }
                }
            }
        }

        int max_idx = 0;
        for(int i = 1; i < groups.size; i++) {
            if(groups.get(i).upPriority > groups.get(max_idx).upPriority) max_idx = i;
        }
        int start_layer_idx = layers.getIndex(groups.get(max_idx).getZOrder());
        for(int layer_idx = start_layer_idx; layer_idx >= 0; layer_idx--) {
            int currFirst = layers.getByIndex(layer_idx).value;
            int currLast = layer_idx < layers.size()-1 ? layers.getByIndex(layer_idx + 1).value-1 : groups.size-1;
            moveGroupWithMaxPriority(groups, currFirst, currLast);
        }
        for (int layer_idx = start_layer_idx+1; layer_idx < layers.size()-1; layer_idx++) {
            int currFirst = layers.getByIndex(layer_idx).value;
            int currLast = layers.getByIndex(layer_idx + 1).value-1;
            moveGroupWithMaxPriority(groups, currFirst, currLast);
        }

        batchesArrays.clear();
        for(int i = 0; i < groups.size; i++) {
            BatchGroup group = groups.get(i);
            BatchArray batchArray = new BatchArray(group);
            while (i < groups.size-1 && group.equalsIgnoreZOrder(groups.get(i+1))) {
                batchArray.add(groups.get(++i));
            }
            batchesArrays.add(batchArray);
        }
    }

    private int moveGroupWithMaxPriority(com.badlogic.gdx.utils.Array<BatchGroup> groups, int from, int to) {
        if(from == to) return -1;
        BatchGroup fromGroup = groups.get(from);
        int max_idx = fromGroup.replaced || fromGroup.nextLink != null && fromGroup.nextLink.replaced ? from+1 : from;
        BatchGroup maxGroup = groups.get(max_idx);

        for(int c = from+1; c <= to; c++) {
            BatchGroup currGroup = groups.get(c);
            if(currGroup.nextLink != null
                    && !currGroup.nextLink.replaced
                    && (currGroup.upPriority > maxGroup.upPriority
                    || currGroup.upPriority == maxGroup.upPriority
                    && currGroup.nextLink.upPriority < maxGroup.nextLink.upPriority)) {
                maxGroup = currGroup;
                max_idx = c;
            }
        }
        if(maxGroup.upPriority > 0) {
            maxGroup.nextLink.replaced = true;
            maxGroup.replaced = true;
            groups.swap(max_idx, to);
            groups.swap(groups.indexOf(maxGroup.nextLink, true), to + 1);
            return max_idx;
        }
        return -1;
    }


    @Override
    public void initialize() {
        rebuildBatchesArrays();
        initialized = true;
    }
}
