package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;

import java.util.BitSet;

public class TagManager {

    private int tag_counter = 0;
    private ObjectIntMap<String> tags = new ObjectIntMap<>();

    private Array<GameObject>[] gameObjectsByTag = new Array[16];

    public void setTag(GameObject go, String tag) {
        int id = tags.get(tag, -1);
        if(id == -1) id = addTag(tag);
        go.tag_bits.set(id);
    }

    public boolean haveTag(GameObject go, String tag) {
        int id = tags.get(tag, -1);
        return id >= 0 && go.tag_bits.get(id);
    }

    public int addTag(String tag) {
        int id = tag_counter++;
        tags.put(tag, id);
        if(id == gameObjectsByTag.length) {
            Array<GameObject>[] temp = new Array[(int)(gameObjectsByTag.length*1.5)];
            System.arraycopy(gameObjectsByTag, 0, temp, 0, gameObjectsByTag.length);
            gameObjectsByTag = temp;
        }
        gameObjectsByTag[id] = new Array<>();
        return id;
    }

    public Array<GameObject> getGameObjectsWithTag(String tag) {
        int id = tags.get(tag, -1);
        if(id == -1) id = addTag(tag);
        return gameObjectsByTag[id];
    }

    public void add(GameObject go) {
        BitSet bits = go.tag_bits;
        for(int id = bits.nextSetBit(0); id != -1; id = bits.nextSetBit(id+1)) {
            gameObjectsByTag[id].add(go);
        }
    }

    public void remove(GameObject go) {
        BitSet bits = go.tag_bits;
        for(int id = bits.nextSetBit(0); id != -1; id = bits.nextSetBit(id+1)) {
            gameObjectsByTag[id].removeValue(go, true);
        }
    }
}
