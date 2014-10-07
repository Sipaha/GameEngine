package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.ObjectIntMap;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.GameObjectsArray;

import java.util.BitSet;

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

    public GameObjectsArray getGameObjectsWithTag(String tag) {
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
}
