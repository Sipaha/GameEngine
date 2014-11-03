package ru.sipaha.engine.utils;

public class Array<T> extends com.badlogic.gdx.utils.Array<T> {

    private Class arrayType;

    public Array(Array<? extends T> prototype) {
        this(prototype.ordered, prototype.size, prototype.arrayType);
    }

    public Array(Class arrayType) {
        this(true, 4, arrayType);
    }

    public Array (boolean ordered, int capacity, Class arrayType) {
        super(ordered, capacity, arrayType);
        this.arrayType = arrayType;
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

    /*public T find(Predicate<T> predicate) {
        for(int i = 0, n = size; i <= n; i++) {
            if(predicate.test(items[i])) return items[i];
        }
        return null;
    }*/

    public T last() {
        return items[size-1];
    }

    public T first() {
        return items[0];
    }
}
