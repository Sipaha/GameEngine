package ru.sipaha.engine.graphics.batches;

import com.sun.istack.internal.NotNull;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.Array;

import java.util.Comparator;

public class BatchGroup extends RenderUnit implements Comparable<BatchGroup>{

    public final Array<Batch> batches;

    protected int upPriority = 0;
    protected BatchGroup nextLink;
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
        replaced = false;
    }

    @Override
    public String toString() {
        return super.toString()+" size="+batches.size;
    }

    @Override
    public int compareTo(BatchGroup group) {
        if (zOrder > group.zOrder) return 1;
        if (zOrder < group.zOrder) return -1;
        return 0;
    }

    /*public static final Comparator<BatchGroup> batchGroupsComparator = new Comparator<BatchGroup>() {
        @Override
        public int compare(BatchGroup group1, BatchGroup group2) {
            if (group1.zOrder > group2.zOrder) return 1;
            if (group1.zOrder < group2.zOrder) return -1;
            return 0;
        }
    };*/
}
