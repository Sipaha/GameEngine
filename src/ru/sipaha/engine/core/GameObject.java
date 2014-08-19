package ru.sipaha.engine.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
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

    public boolean enable;

    protected BitSet tag_bits;

    private ObjectMap<Class<? extends Script>, Script> scriptsByClass;
    private Array<Script> scripts;

    private GameObject prototype;
    private Array<GameObject> children;
    private ObjectMap<String, GameObject> childByName;

    public GameObject(String name) {
        this.name = name;
        enable = true;
        scripts = new Array<>(false, 8, Script.class);
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

    public void enable() {
        enable = true;
    }
    public void disable() {
        enable = false;
    }

    public GameObject getChildrenByName(String name) {
        return childByName.get(name);
    }

    public void reset() {
        if (prototype == null) throw new RuntimeException("Reset was called from prototype! name = "+name);
        transform.set(prototype.transform);
        motion.set(prototype.motion);
        renderer.set(prototype.renderer);
    }

    public GameObject copy() {
        GameObject go = new GameObject(name);
        go.prototype = this;
        go.transform = new Transform(transform);
        go.motion = new Motion(motion);
        go.renderer = new MeshRenderer(renderer);
        return go;
    }
}
