package ru.sipaha.engine.graphics.batches.utils;

import ru.sipaha.engine.graphics.batches.Batch;
import ru.sipaha.engine.utils.Array;

import java.util.Comparator;

public class BatchGroup {
    public int z_order;
    public Array<Batch> batches;

    public BatchGroup(Batch batch) {
        z_order = batch.getZOrder();
        batches = new Array<>(true, 4, Batch.class);
        batches.add(batch);
    }

    public int size() {
        return batches.size;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BatchGroup
                && batches.size > 0
                && batches.items[0].equals(((BatchGroup)obj).batches.items[0]);
    }

    public boolean equalsIgnoreZOrder(BatchGroup group) {
        Batch firstGroupBatch = batches.items[0];
        Batch secondGroupBatch = group.batches.items[0];
        return firstGroupBatch.getTexture() == secondGroupBatch.getTexture()
                && firstGroupBatch.getShader() == secondGroupBatch.getShader();
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
