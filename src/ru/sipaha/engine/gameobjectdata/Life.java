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

    /**
     * @return true if game object was destroyed
     */
    public boolean update(GameObject go, float delta) {
        boolean isDestroyed = false;
        if(lives <= 0) {
            onDying.dispatch(go);
            isDestroyed = true;
        } else if(durability <= 0) {
            onBreak.dispatch(go);
            isDestroyed = true;
        } else if(lifeTime > 0) {
            lifeTime -= delta;
            if(lifeTime <= 0) {
                onLifetimeExpired.dispatch(go);
                isDestroyed = true;
            }
        }
        return isDestroyed;
    }

    public void reset(Life template) {
        lives = template.lives;
        lifeTime = template.lifeTime;
        durability = template.durability;
        onLifetimeExpired.clear();
        onDying.clear();
        onBreak.clear();
    }
}
