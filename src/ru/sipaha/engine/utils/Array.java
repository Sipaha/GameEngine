package ru.sipaha.engine.utils;

public class Array<T> extends com.badlogic.gdx.utils.Array<T> {

    public Array (boolean ordered, int capacity, Class arrayType) {
        super(ordered, capacity, arrayType);
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
}
