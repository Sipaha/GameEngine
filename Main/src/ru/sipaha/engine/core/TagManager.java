package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.ObjectIntMap;
import ru.sipaha.engine.utils.Array;

import java.util.BitSet;
import java.util.Iterator;

public class TagManager {

    private ObjectIntMap<String> tags = new ObjectIntMap<>();

    private Array<GameObjectsArray> unitsByTag = new Array<>(true, 16, GameObjectsArray.class);

    public void setTag(GameObject gameObject, String tag) {
        int id = tags.get(tag, -1);
        if(id == -1) id = addTag(tag);
        gameObject.tagBits.set(id);
    }

    public boolean haveTag(GameObject gameObject, String tag) {
        int id = tags.get(tag, -1);
        return id >= 0 && gameObject.tagBits.get(id);
    }

    public Iterable<GameObject> getUnitsWithTag(String tag) {
        int id = tags.get(tag, -1);
        if(id == -1) id = addTag(tag);
        return unitsByTag.get(id);
    }

    protected void add(GameObject units) {
        BitSet bits = units.tagBits;
        if(bits != null) {
            for(int i = 0; i < unitsByTag.size; i++) {
                if(bits.get(i)) unitsByTag.items[i].add(units);
            }
        }
    }

    protected void remove(GameObject unit) {
        BitSet bits = unit.tagBits;
        if(bits != null) {
            for(int i = 0; i < unitsByTag.size; i++) {
                if(bits.get(i)) unitsByTag.items[i].removeValue(unit, true);
            }
        }
    }

    private int addTag(String tag) {
        int id = unitsByTag.size;
        tags.put(tag, id);
        unitsByTag.add(new GameObjectsArray(false, 16, GameObject.class));
        return id;
    }

    private class GameObjectsArray extends Array<GameObject> implements Iterable<GameObject> {

        private EngineUnitsIterator iterator;

        public GameObjectsArray(boolean ordered, int capacity, Class arrayType) {
            super(ordered, capacity, arrayType);
        }

        @Override
        public Iterator<GameObject> iterator () {
            if (iterator == null) {
                iterator = new EngineUnitsIterator((ArrayIterator<GameObject>)super.iterator());
            } else {
                iterator.reset();
            }
            return iterator;
        }

        public class EngineUnitsIterator implements Iterator<GameObject> {

            public ArrayIterator<GameObject> iterator;
            public boolean end = false;
            public boolean peeked = false;
            public GameObject next = null;

            public EngineUnitsIterator(ArrayIterator<GameObject> iterator) {
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
                    final GameObject unit = iterator.next();
                    if (unit.enable) {
                        next = unit;
                        return true;
                    }
                }
                end = true;
                return false;
            }

            @Override
            public GameObject next () {
                GameObject result = next;
                next = null;
                peeked = false;
                return result;
            }

            @Override
            public void remove() {}
        }
    }
}
