package ru.sipaha.engine.graphics.batches.utils;

import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.graphics.batches.Batch;
import ru.sipaha.engine.utils.Array;

import java.util.Comparator;

public class BatchGroup extends RenderUnit {
    public Array<Batch> batches;

    protected int upPriority = 0;
    protected BatchGroup nextLink;
    protected BatchGroup prevLink;
    protected boolean replaced = false;

    public BatchGroup(Batch batch) {
        super(batch);
        batches = new Array<>(true, 4, Batch.class);
        batches.add(batch);
    }

    public int size() {
        return batches.size;
    }

    public void reset() {
        upPriority = 0;
        nextLink = null;
        prevLink = null;
        replaced = false;
    }

    @Override
    public String toString() {
        return super.toString()+" size="+batches.size;
    }

    public static final Comparator<BatchGroup> batchGroupsComparator = new Comparator<BatchGroup>() {
        @Override
        public int compare(BatchGroup group1, BatchGroup group2) {
            if (group1.z_order > group2.z_order) return 1;
            if (group1.z_order < group2.z_order) return -1;
            return 0;
        }
    };
}
