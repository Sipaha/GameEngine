package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.ObjectIntMap;
import ru.sipaha.engine.utils.Array;

import java.util.BitSet;
import java.util.Iterator;

public class TagManager {

    private ObjectIntMap<String> tags = new ObjectIntMap<>();

    private Array<EngineUnitsArray> unitsByTag = new Array<>(true, 16, EngineUnitsArray.class);

    public void setTag(EngineUnit unit, String tag) {
        int id = tags.get(tag, -1);
        if(id == -1) id = addTag(tag);
        unit.getTagBits().set(id);
    }

    public boolean haveTag(EngineUnit unit, String tag) {
        int id = tags.get(tag, -1);
        return id >= 0 && unit.getTagBits().get(id);
    }

    public Iterable<EngineUnit> getUnitsWithTag(String tag) {
        int id = tags.get(tag, -1);
        if(id == -1) id = addTag(tag);
        return unitsByTag.get(id);
    }

    protected void add(EngineUnit units) {
        BitSet bits = units.getTagBits();
        if(bits != null) {
            for(int i = 0; i < unitsByTag.size; i++) {
                if(bits.get(i)) unitsByTag.items[i].add(units);
            }
        }
    }

    protected void remove(EngineUnit unit) {
        BitSet bits = unit.getTagBits();
        if(bits != null) {
            for(int i = 0; i < unitsByTag.size; i++) {
                if(bits.get(i)) unitsByTag.items[i].removeValue(unit, true);
            }
        }
    }

    private int addTag(String tag) {
        int id = unitsByTag.size;
        tags.put(tag, id);
        unitsByTag.add(new EngineUnitsArray(false, 16, EngineUnit.class));
        return id;
    }

    private class EngineUnitsArray extends Array<EngineUnit> implements Iterable<EngineUnit> {

        private EngineUnitsIterator iterator;

        public EngineUnitsArray(boolean ordered, int capacity, Class arrayType) {
            super(ordered, capacity, arrayType);
        }

        @Override
        public Iterator<EngineUnit> iterator () {
            if (iterator == null) {
                iterator = new EngineUnitsIterator((ArrayIterator<EngineUnit>)super.iterator());
            } else {
                iterator.reset();
            }
            return iterator;
        }

        public class EngineUnitsIterator implements Iterator<EngineUnit> {

            public ArrayIterator<EngineUnit> iterator;
            public boolean end = false;
            public boolean peeked = false;
            public EngineUnit next = null;

            public EngineUnitsIterator(ArrayIterator<EngineUnit> iterator) {
                this.iterator = iterator;
            }

            public void reset () {
                iterator.reset();
                end = peeked = false;
                next = null;
            }

            @Override
            public boolean hasNext () {
                if (end) return false;
                if (next != null) return true;
                peeked = true;
                while (iterator.hasNext()) {
                    final EngineUnit unit = iterator.next();
                    if (unit.isEnable()) {
                        next = unit;
                        return true;
                    }
                }
                end = true;
                return false;
            }

            @Override
            public EngineUnit next () {
                EngineUnit result = next;
                next = null;
                peeked = false;
                return result;
            }

            @Override
            public void remove() {}
        }
    }
}
