package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.gameobjectdata.Life;
import ru.sipaha.engine.gameobjectdata.MeshRenderer;
import ru.sipaha.engine.gameobjectdata.Motion;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.scripts.Script;

import java.util.BitSet;

public class GameObject {
    public final String name;

    public MeshRenderer renderer;
    public Transform transform;
    public Motion motion;
    public Life life;

    public boolean enable;

    protected BitSet tag_bits;

    private ObjectMap<Class<? extends Script>, Script> scriptsByClass;
    private Array<Script> scripts;

    protected Replicator replicator;
    private Array<GameObject> children;
    private ObjectMap<String, GameObject> childByName;

    public GameObject(String name) {
        this.name = name;
        enable = true;
        scripts = new Array<>(false, 8, Script.class);
        tag_bits = new BitSet();
    }

    public void start(Engine engine) {
        for (int i = 0, s = scripts.size; i < s; i++) {
            scripts.items[i].start(engine);
        }
        if(children != null) {
            for(int i = 0, s = children.size; i < s; i++) {
                children.items[i].start(engine);
            }
        }
    }

    public void update(float delta) {
        for (int i = 0, s = scripts.size; i < s; i++) {
            scripts.items[i].update(delta);
        }
        if(children != null) {
            for(int i = 0, s = children.size; i < s; i++) {
                children.items[i].update(delta);
            }
        }
    }

    public void fixedUpdate(float delta) {
        for (int i = 0, s = scripts.size; i < s; i++) {
            scripts.items[i].fixedUpdate(delta);
        }
        if(children != null) {
            for(int i = 0, s = children.size; i < s; i++) {
                children.items[i].fixedUpdate(delta);
            }
        }
    }

    public GameObject updateData(float delta) {
        return updateData(delta, null);
    }

    private GameObject updateData(float delta, GameObject parent) {
        motion.update(transform, delta);
        if(parent == null) {
            transform.update();
        } else {
            transform.update(parent.transform);
        }
        if(children != null) {
            for(int i = 0, s = children.size; i < s; i++) {
                children.items[i].updateData(delta, this);
            }
        }
        renderer.update(transform);
        return this;
    }

    public void addChild(GameObject go) {
        if(children == null) children = new Array<>(false,6,GameObject.class);
        children.add(go);
        go.transform.forceUpdate(transform);
        go.renderer.update(go.transform);
        if(go.name != null) {
            if (childByName == null) childByName = new ObjectMap<>();
            childByName.put(go.name, go);
        }
    }

    public void shoot(GameObject shell) {
        shell.transform.forceUpdate(transform);
        shell.transform.unhook();
        shell.renderer.update(shell.transform);
        //shell.motion.mul(transform);
    }

    public void free() {
        replicator.free(this);
    }

    public void remove() {
        replicator.remove(this);
    }

    public GameObject getChildrenByName(String name) {
        return childByName.get(name);
    }
}
