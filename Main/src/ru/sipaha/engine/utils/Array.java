package ru.sipaha.engine.utils;

public class Array<T> extends com.badlogic.gdx.utils.Array<T> {

    private Class arrayType;

    public Array(Array<? extends T> prototype) {
        this(prototype.ordered, prototype.size, prototype.arrayType);
        arrayType = prototype.arrayType;
    }

    public Array(Class arrayType) {
        this(true, 4, arrayType);
        this.arrayType = arrayType;
    }

    public Array (boolean ordered, int capacity, Class arrayType) {
        super(ordered, capacity, arrayType);
        this.arrayType = arrayType;
    }

    public Array(boolean ordered, int capacity, T firstElement) {
        super(ordered, capacity, firstElement.getClass());
        this.arrayType = firstElement.getClass();
        add(firstElement);
    }

    /*@Override
    public void add(T value) {
        Class<?> type = value.getClass();
        if(!type.isAssignableFrom(arrayType)) {
            if(size > 0) {
                do {
                    arrayType = arrayType.getSuperclass();
                } while (!type.isAssignableFrom(arrayType));
            } else {
                arrayType = type;
            }
            T[] newItems = (T[]) ArrayReflection.newInstance(arrayType, items.length);
            System.arraycopy(items, 0, newItems, 0, size);
            items = newItems;
        }
        super.add(value);
    }*/

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
