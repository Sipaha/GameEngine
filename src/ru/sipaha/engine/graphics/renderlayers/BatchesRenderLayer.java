package ru.sipaha.engine.graphics.renderlayers;

import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.graphics.Batch;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.IntIntMap;

/**
 * Created on 30.09.2014.
 */

public class BatchesRenderLayer extends RenderLayer {

    private ObjectMap<RenderUnit, RenderUnitsGroup> drawableGroups = new ObjectMap<>();
    private Array<Batch> batches = new Array<>(true, 16, Batch.class);

    private boolean notSorted = true;

    public BatchesRenderLayer(String name) {
        super(name);
    }

    @Override
    public void render() {
        if(notSorted) sort();
        Batch[] bs = batches.items;
        for(int i = 0, s = batches.size; i < s; i++) {
            bs[i].draw(camera.combined);
        }
    }

    public void add(RenderUnit unit) {
        getGroup(unit).array.add(unit);
    }

    public void remove(RenderUnit unit) {
        drawableGroups.get(unit).array.removeValue(unit, true);
    }

    public void prepare(RenderUnit unit) {
        getGroup(unit);
    }

    private RenderUnitsGroup getGroup(RenderUnit unit) {
        RenderUnitsGroup group = drawableGroups.get(unit);
        if(group == null) {
            group = new RenderUnitsGroup(unit);
            drawableGroups.put(unit, group);
            notSorted = true;
        }
        return group;
    }

    public void sort() {
        com.badlogic.gdx.utils.Array<RenderUnitsGroup> groups = drawableGroups.values().toArray();
        groups.sort();
        for(RenderUnitsGroup b : groups) b.reset();

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
                RenderUnitsGroup currGroup = groups.get(currIdx);
                for (int nextIdx = nextFirst; nextIdx <= nextLast; nextIdx++) {
                    RenderUnitsGroup nextGroup = groups.get(nextIdx);
                    if(currGroup.equalsIgnoreZOrder(nextGroup)) {
                        int priority = currGroup.array.size+nextGroup.array.size;
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

        for(int i = 0, batchIdx = 0; i < groups.size; i++, batchIdx++) {
            RenderUnitsGroup group = groups.get(i);
            Batch batch;
            if(batchIdx == batches.size) {
                batch = new Batch();
                batches.add(batch);
            } else {
                batch = batches.get(batchIdx);
                batch.clear();
            }
            batch.add(group);
            while (i < groups.size-1 && group.equalsIgnoreZOrder(groups.get(i+1))) {
                batch.add(groups.get(++i));
            }
        }

        notSorted = false;
    }

    private int moveGroupWithMaxPriority(com.badlogic.gdx.utils.Array<RenderUnitsGroup> groups, int from, int to) {
        if(from == to) return -1;
        RenderUnitsGroup fromGroup = groups.get(from);
        int max_idx = fromGroup.replaced || fromGroup.nextLink != null && fromGroup.nextLink.replaced ? from+1 : from;
        RenderUnitsGroup maxGroup = groups.get(max_idx);

        for(int c = from+1; c <= to; c++) {
            RenderUnitsGroup currGroup = groups.get(c);
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
        sort();
    }

    private class RenderUnitsGroup extends RenderUnit {

        Array<RenderUnit> array = new Array<>(RenderUnit.class);

        int upPriority = 0;
        RenderUnitsGroup nextLink;
        boolean replaced = false;

        public RenderUnitsGroup(RenderUnit unit) {
            super(unit);
        }

        @Override
        public int render(float[] vertices, int pos) {
            for(RenderUnit unit : array) pos = unit.render(vertices, pos);
            return pos;
        }

        public void reset() {
            upPriority = 0;
            nextLink = null;
            replaced = false;
        }
    }
}
