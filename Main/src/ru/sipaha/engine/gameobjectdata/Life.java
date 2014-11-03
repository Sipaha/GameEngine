package ru.sipaha.engine.gameobjectdata;

import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.utils.signals.Signal;

public class Life {
    public float lives = 1;
    public float lifeTime = -1;
    public float durability = 1;

    public final Signal<GameObject> onLifetimeExpired = new Signal<>();
    public final Signal<GameObject> onDying = new Signal<>();
    public final Signal<GameObject> onBreak = new Signal<>();

    public Life(){}

    public Life(Life life) {
        reset(life);
    }

    public void update(GameObject go, float delta) {
        if(lives <= 0) {
            onDying.dispatch(go);
            go.free();
        } else if(durability <= 0) {
            onBreak.dispatch(go);
            go.free();
        } else if(lifeTime > 0) {
            lifeTime -= delta;
            if(lifeTime <= 0) {
                onLifetimeExpired.dispatch(go);
                go.free();
            }
        }
    }

    public void reset(Life template) {
        lives = template.lives;
        lifeTime = template.lifeTime;
        durability = template.durability;
        onLifetimeExpired.set(template.onLifetimeExpired);
        onDying.set(template.onDying);
        onBreak.set(template.onBreak);
    }
}
