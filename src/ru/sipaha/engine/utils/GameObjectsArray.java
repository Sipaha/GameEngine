package ru.sipaha.engine.utils;

import ru.sipaha.engine.core.GameObject;

import java.util.Iterator;

public class GameObjectsArray extends Array<GameObject> implements Iterable<GameObject> {

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

    public static class GameObjectsIterator implements Iterator<GameObject> {

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
    }
}
