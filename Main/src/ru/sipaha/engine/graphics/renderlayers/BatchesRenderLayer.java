package ru.sipaha.engine.graphics.renderlayers;

import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.graphics.Batch;
import ru.sipaha.engine.graphics.RenderBuffer;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.structures.Bounds;

/**
 * Created on 30.09.2014.
 */

public class BatchesRenderLayer extends RenderLayer {

    private ObjectMap<RenderUnit, RenderUnitsGroup> drawableGroups = new ObjectMap<>();
    private Array<RenderUnitsGroup> unitsGroupsCache = new Array<>(false, 2, RenderUnitsGroup.class);
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
        getGroup(unit).add(unit);
    }

    public boolean remove(RenderUnit unit) {
        RenderUnitsGroup group = drawableGroups.get(unit);
        if(group != null) {
            group.remove(unit);
        } else {
            for(RenderUnitsGroup g : drawableGroups.values()) {
                if(g.remove(unit)) {
                    group = g;
                    break;
                }
            }
        }
        if(group == null) return false;
        if(group.isEmpty()) {
            drawableGroups.remove(group);
            unitsGroupsCache.add(group);
        }
        return true;
    }

    public void prepare(RenderUnit unit) {
        getGroup(unit);
    }

    private RenderUnitsGroup getGroup(RenderUnit unit) {
        RenderUnitsGroup group = drawableGroups.get(unit);
        if(group == null) {
            if(unitsGroupsCache.size > 0) {
                group = unitsGroupsCache.pop();
                group.setPropertiesFrom(unit);
            } else {
                group = new RenderUnitsGroup(unit);
            }
            drawableGroups.put(group, group);
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
            int currLayer = groups.get(i).zOrder.get();
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
        int start_layer_idx = layers.getIndex(groups.get(max_idx).zOrder.get());
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

            group.setBatch(batch);
            while (i < groups.size-1 && group.equalsIgnoreZOrder(groups.get(i+1))) {
               groups.get(++i).setBatch(batch);
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

        private Array<RenderUnit> units = new Array<>(RenderUnit.class);
        private Batch batch;

        int upPriority = 0;
        RenderUnitsGroup nextLink;
        boolean replaced = false;

        public RenderUnitsGroup(RenderUnit unit) {
            super(unit);
        }

        @Override
        public void setRenderData(RenderBuffer buffer) {
            throw new RuntimeException("Method setRenderData in RenderUnitsGroup is not defined!");
        }

        @Override
        public void render(RenderBuffer buffer) {
            for(RenderUnit unit : units) unit.render(buffer);
        }

        @Override
        public int getRenderSize() {
            int sum = 0;
            for(RenderUnit u : units) sum += u.getRenderSize();
            return sum;
        }

        public void add(RenderUnit unit) {
            units.add(unit);
            if(isStatic.get() && batch != null) batch.add(unit);
        }

        public boolean remove(RenderUnit unit) {
            if(units.removeValue(unit, true)) {
                if(isStatic.get() && batch != null) batch.remove(unit);
                return true;
            }
            return false;
        }

        public boolean isEmpty() {
            return units.size == 0;
        }

        public void setBatch(Batch batch) {
            this.batch = batch;
            if(isStatic.get()) {
                for(RenderUnit unit : units) batch.add(unit);
            } else {
                batch.add(this);
            }
        }

        public int size() {
            return units.size;
        }

        public void reset() {
            upPriority = 0;
            nextLink = null;
            replaced = false;
            batch = null;
        }
    }

    private class IntIntMap {

        public final Array<Entry> entries = new Array<>(true, 16, Entry.class);

        public void put(int key, int value) {
            entries.add(new Entry(key, value));
        }

        public Entry getByIndex(int i) {
            return entries.get(i);
        }

        public Entry get(int key) {
            for(Entry e : entries) if(e.key == key) return e;
            return null;
        }

        public int getIndex(int key) {
            for(int i = 0; i < entries.size; i++) if(entries.get(i).key == key) return i;
            return -1;
        }

        public int size() {
            return entries.size;
        }

        public class Entry {
            public int key, value;

            public Entry(int key, int value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public String toString() {
                return "key="+key+" value="+value;
            }
        }
    }
}
