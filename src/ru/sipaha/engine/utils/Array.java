package ru.sipaha.engine.utils;

import java.util.function.Predicate;

public class Array<T> extends com.badlogic.gdx.utils.Array<T> {

    public Array (boolean ordered, int capacity, Class arrayType) {
        super(ordered, capacity, arrayType);
    }

    public Array(Class arrayType) {
        super(true, 4, arrayType);
    }

    /**
     * Replace element a by element b
     */
    public void replace(T a, T b) {
        T[] items = this.items;
        for (int i = 0, n = size; i < n; i++) {
            if (items[i] == a) {
                items[i] = b;
                break;
            }
        }
    }

    public T find(Predicate<T> predicate) {
        for(int i = 0, n = size; i <= n; i++) {
            if(predicate.test(items[i])) return items[i];
        }
        return null;
    }

    public T last() {
        return items[size-1];
    }

    public T first() {
        return items[0];
    }
}
