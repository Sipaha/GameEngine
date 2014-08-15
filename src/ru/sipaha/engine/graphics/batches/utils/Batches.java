package ru.sipaha.engine.graphics.batches.utils;

import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.MeshRenderer;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.graphics.batches.Batch;
import ru.sipaha.engine.graphics.batches.GOBatch;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.IntIntMap;

public class Batches {

    public ObjectMap<RenderUnit, BatchGroup> batchesGroups = new ObjectMap<>();
    public Array<Batch> sortedBatches = new Array<>(true, 16, Batch.class);
    public ObjectMap<RenderUnit, GOBatch> GOBatchesByUnit = new ObjectMap<>();

    public void addGO(GameObject go) {
        GOBatch b = GOBatchesByUnit.get(go.renderer);
        if(b.isFull()) {
            GOBatch newBatch = new GOBatch(b.size*2, b.getShader(), b.getTexture(), b.getZOrder());
            newBatch.addGameObjects(b);
            GOBatchesByUnit.put(go.renderer, b);
            sortedBatches.replace(b, newBatch);
            b = newBatch;
        }
        b.addGameObject(go);
    }

    public void addBatch(Batch batch) {
        BatchGroup batchGroup = batchesGroups.get(batch);
        if(batchGroup == null) {
            batchGroup = new BatchGroup(batch);
            batchesGroups.put(batch, batchGroup);
        } else batchGroup.batches.add(batch);

        Batch[] batches = sortedBatches.items;
        int idx = sortedBatches.size;
        if(idx == batches.length) {
            sortedBatches.ensureCapacity((int)(idx*0.5f));
            batches = sortedBatches.items;
        }
        int z_order = batch.getZOrder();
        while (idx > 0 && z_order < batches[idx].getZOrder()) idx--;
        while (idx > 0 && z_order == batches[idx-1].getZOrder() && batches[idx].equals(batches[idx+1])) idx--;
        sortedBatches.insert(idx, batch);
    }

    public void prepareBatchForGameObject(GameObject go) {
        if(GOBatchesByUnit.containsKey(go.renderer)) return;
        MeshRenderer renderer = go.renderer;
        GOBatch batch = new GOBatch(32, renderer.getShader(), renderer.getTexture(), renderer.getZOrder());
        GOBatchesByUnit.put(renderer, batch);
        addBatch(batch);
    }

    public void prepareBatchesForGameObjects(Array<GameObject> gameObjects) {
        for(GameObject go : gameObjects) prepareBatchForGameObject(go);
        rebuildSortedBatches();
    }

    public void rebuildSortedBatches() {
        com.badlogic.gdx.utils.Array<BatchGroup> groups = batchesGroups.values().toArray();
        groups.sort(BatchGroup.batchGroupsComparator);

        IntIntMap layers = new IntIntMap();
        BatchGroup[] groupsArr = groups.items;
        for(int i = 0, layer = -1, size = groups.size; i < size; i++) {
            int currLayer = groupsArr[i].z_order;
            if(currLayer > layer) {
                layer = currLayer;
                layers.put(currLayer, i);
            }
        }

        int prevStickBatchesSize = 0;
        for(int i = 1; i < layers.size(); i+=2) {
            int stickBatchesSize = 0;
            IntIntMap.Entry layer = layers.get(i);
            final int currLast = layers.get(i+1).value-1;
            final int currFirst = layer.value;
            for (int currIdx = currFirst; currIdx <= currLast; currIdx++) {
                BatchGroup currGroup = groupsArr[currIdx];
                final int prevFirst = layers.get(i-1).value;
                final int prevLast = layer.value-1;
                for (int prevIdx = prevFirst; prevIdx <= prevLast; prevIdx++) {
                    BatchGroup prevGroup = groupsArr[prevIdx];
                    int unitedSize = currGroup.size()+prevGroup.size();
                    if(currGroup.equalsIgnoreZOrder(prevGroup)
                            && unitedSize > stickBatchesSize
                            && unitedSize > prevStickBatchesSize) {
                        groups.swap(prevIdx, prevLast);
                        groups.swap(currIdx, currFirst);
                        stickBatchesSize = currGroup.size() + prevGroup.size();
                    }
                }
            }

            if(i < layers.size()-1) {
                stickBatchesSize = 0;
                for (int currIdx = currFirst; currIdx <= currLast; currIdx++) {
                    BatchGroup currGroup = groupsArr[currIdx];
                    final int nextFirst = layers.get(i+1).value;
                    final int nextLast = i < layers.size()-2 ? layers.get(i+2).value-1 : layers.size()-1;
                    for (int nextIdx = nextFirst; nextIdx <= nextLast; nextIdx++) {
                        BatchGroup nextGroup = groupsArr[nextIdx];
                        int unitedSize = currGroup.size()+nextGroup.size();
                        if(currGroup.equalsIgnoreZOrder(nextGroup) && unitedSize > stickBatchesSize) {
                            groups.swap(nextIdx, nextFirst);
                            groups.swap(currIdx, currLast);
                            stickBatchesSize = unitedSize;
                            prevStickBatchesSize = unitedSize;
                        }
                    }
                }
            }
        }

        sortedBatches.size = 0;
        for(int i = 0; i < groups.size; i++) {
            BatchGroup group = groups.get(i);
            for(int g = 0; g < group.size(); g++) {
                sortedBatches.add(group.batches.get(g));
            }
        }

        for(int i = 0, s = sortedBatches.size-1; i < s; i++) {
            Batch curr = sortedBatches.get(i);
            Batch next = sortedBatches.get(i+1);
            if(curr.equals(next)) {
                curr.setLink(next);
                next.setLinked(true);
            } else {
                curr.setLink(null);
                next.setLinked(false);
            }
        }
    }

    public void removeGO(GameObject go) {
        GOBatchesByUnit.get(go.renderer).removeGameObject(go);
    }

}
