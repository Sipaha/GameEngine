package ru.sipaha.engine.utils;

public class IntIntMap {

    public final Array<Entry> entries = new Array<>(true, 16, Entry.class);

    public void put(int key, int value) {
        entries.add(new Entry(key, value));
    }

    public Entry get(int i) {
        return entries.get(i);
    }
    public int size() {
        return entries.size;
    }

    public class Entry {
        public int key, value;

        public Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
