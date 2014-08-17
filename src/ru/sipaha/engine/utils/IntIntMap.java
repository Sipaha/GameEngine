package ru.sipaha.engine.utils;

public class IntIntMap {

    public final Array<Entry> entries = new Array<>(true, 16, Entry.class);

    public void put(int key, int value) {
        entries.add(new Entry(key, value));
    }

    public Entry getByIndex(int i) {
        return entries.get(i);
    }

    public Entry get(int key) {
        for(Entry e : entries) if(e.key == key) return e;
        return null;
    }

    public int getIndex(int key) {
        for(int i = 0; i < entries.size; i++) if(entries.get(i).key == key) return i;
        return -1;
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

        @Override
        public String toString() {
            return "key="+key+" value="+value;
        }
    }
}
