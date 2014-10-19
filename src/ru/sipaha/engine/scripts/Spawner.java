package ru.sipaha.engine.scripts;

import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.Script;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.signals.Listener;
import ru.sipaha.engine.utils.signals.Signal;
import ru.sipaha.engine.utils.structures.Spawn;

public class Spawner extends Script {

    public boolean active;
    public int spawnCounter = 0;
    public int spawnLimit = 0;
    public boolean loop;
    public Spawn[] spawns;
    public int currentSpawn;
    public float timer;
    public final Signal<GameObject> onAllSpawnObjectsRemoved = new Signal<>();
    public final Signal<GameObject> onSpawnObjectDie = new Signal<>();
    public final Signal<GameObject> onSpawnObjectBroken = new Signal<>();

    private Engine engine;
    private final Listener<GameObject> spawnedDead;
    private final Listener<GameObject> spawnedBroken;

    private Transform transform;

    public Spawner() {
        spawnedDead = new Listener<GameObject>() {
            @Override
            public void receive(GameObject object) {
                spawnObjectDied(object);
            }
        };
        spawnedBroken = new Listener<GameObject>() {
            @Override
            public void receive(GameObject object) {
                spawnObjectBroken(object);
            }
        };
    }

    @Override
    public void start(Engine engine) {
        this.engine = engine;
        transform = gameObject.getTransform();
    }

    @Override
    public void fixedUpdate(float delta) {
        if(!active) return;
        Spawn currSpawn = spawns[currentSpawn];
        if(timer < currSpawn.time) timer += delta;
        if(timer >= currSpawn.time
                && (spawnLimit == 0 || spawnCounter < spawnLimit)) {
            timer -= currSpawn.time;
            if(++currentSpawn == spawns.length) {
                currentSpawn = 0;
                active = loop;
            }
            GameObject gObject = engine.factory.create(currSpawn.unit);
            gObject.getTransform().setPosition(transform.tx, transform.ty);
            spawnCounter++;
            //gObject.components.get(Life.class).death.add(spawner.spawnedDead);
        }
    }


    public void reset() {

    }

    public void startSpawn() {
        active = true;
    }

    public void stopSpawn() {
        active = false;
    }

    private void spawnObjectDied(GameObject go) {
        onSpawnObjectDie.dispatch(go);
        if(--spawnCounter == 0) onAllSpawnObjectsRemoved.dispatch(go);
    }

    private void spawnObjectBroken(GameObject go) {
        onSpawnObjectBroken.dispatch(go);
        if(--spawnCounter == 0) onAllSpawnObjectsRemoved.dispatch(go);
    }
}
