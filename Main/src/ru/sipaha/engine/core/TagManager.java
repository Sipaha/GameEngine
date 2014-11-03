package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.ObjectIntMap;
import ru.sipaha.engine.utils.Array;

import java.util.BitSet;
import java.util.Iterator;

public class TagManager {

    private ObjectIntMap<String> tags = new ObjectIntMap<>();

    private Array<GameObjectsArray> gameObjectsByTag = new Array<>(true, 16, GameObjectsArray.class);

    public void setTag(GameObject go, String tag) {
        int id = tags.get(tag, -1);
        if(id == -1) id = addTag(tag);
        go.tag_bits.set(id);
    }

    public boolean haveTag(GameObject go, String tag) {
        int id = tags.get(tag, -1);
        return id >= 0 && go.tag_bits.get(id);
    }

    public Iterable<GameObject> getGameObjectsWithTag(String tag) {
        int id = tags.get(tag, -1);
        if(id == -1) id = addTag(tag);
        return gameObjectsByTag.get(id);
    }

    protected void add(GameObject go) {
        BitSet bits = go.tag_bits;
        for(int i = 0; i < gameObjectsByTag.size; i++) {
            if(bits.get(i)) gameObjectsByTag.items[i].add(go);
        }
    }

    protected void remove(GameObject go) {
        BitSet bits = go.tag_bits;
        for(int i = 0; i < gameObjectsByTag.size; i++) {
            if(bits.get(i)) gameObjectsByTag.items[i].removeValue(go, true);
        }
    }

    private int addTag(String tag) {
        int id = gameObjectsByTag.size;
        tags.put(tag, id);
        gameObjectsByTag.add(new GameObjectsArray(false, 16));
        return id;
    }

    private class GameObjectsArray extends Array<GameObject> implements Iterable<GameObject> {

        private GameObjectsIterator iterator;

        public GameObjectsArray(boolean ordered, int capacity) {
            super(ordered, capacity, GameObject.class);
        }

        @Override
        public Iterator<GameObject> iterator () {
            if (iterator == null) {
                iterator = new GameObjectsIterator((ArrayIterator<GameObject>)super.iterator());
            } else {
                iterator.reset();
            }
            return iterator;
        }

        public class GameObjectsIterator implements Iterator<GameObject> {

            public ArrayIterator<GameObject> iterator;
            public boolean end = false;
            public boolean peeked = false;
            public GameObject next = null;

            public GameObjectsIterator(ArrayIterator<GameObject> iterator) {
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
                    final GameObject gameObject = iterator.next();
                    if (gameObject.enable) {
                        next = gameObject;
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
