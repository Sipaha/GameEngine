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
        while (idx > 0 && z_order < batches[idx-1].getZOrder()) idx--;
        while (idx > 0 && z_order == batches[idx-1].getZOrder()
                       && idx+1 < batches.length
                       && batches[idx+1] != null
                       && batches[idx].equalsIgnoreZOrder(batches[idx+1])) idx--;
        sortedBatches.insert(idx, batch);
    }

    public GOBatch prepareBatchForGameObject(GameObject go) {
        GOBatch batch = GOBatchesByUnit.get(go.renderer);
        if(batch == null) {
            MeshRenderer renderer = go.renderer;
            batch = new GOBatch(32, renderer.getShader(), renderer.getTexture(), renderer.getZOrder());
            GOBatchesByUnit.put(renderer, batch);
            addBatch(batch);
        }
        return batch;
    }

    public void rebuildSortedBatches() {
        com.badlogic.gdx.utils.Array<BatchGroup> groups = batchesGroups.values().toArray();
        groups.sort(BatchGroup.batchGroupsComparator);
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
                            nextGroup.prevLink = currGroup;
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
            int currLast = layers.getByIndex(layer_idx + 1).value-1;
            moveGroupWithMaxPriority(groups, currFirst, currLast);
        }
        for (int layer_idx = start_layer_idx+1; layer_idx < layers.size()-1; layer_idx++) {
            int currFirst = layers.getByIndex(layer_idx).value;
            int currLast = layers.getByIndex(layer_idx + 1).value-1;
            moveGroupWithMaxPriority(groups, currFirst, currLast);
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

    public void removeGO(GameObject go) {
        GOBatchesByUnit.get(go.renderer).removeGameObject(go);
    }

}
