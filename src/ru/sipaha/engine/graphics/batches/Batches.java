package ru.sipaha.engine.graphics.batches;

import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.GameObjectRenderer;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.IntIntMap;

public class Batches {

    public ObjectMap<RenderUnit, BatchGroup> batchesGroups = new ObjectMap<>();
    public Array<BatchArray> batchesArrays = new Array<>(true, 16, BatchArray.class);
    public ObjectMap<RenderUnit, GameObjectsBatch> goBatchesByUnit = new ObjectMap<>();

    public void addGameObjectRenderer(GameObjectRenderer renderer) {
        goBatchesByUnit.get(renderer).addGameObjectRenderer(renderer);
    }

    public void removeGameObjectRenderer(GameObjectRenderer renderer) {
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
}
