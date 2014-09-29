package ru.sipaha.engine.core.animation;

import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.core.animation.сontinuous.ContinuousAnimation;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.Array;

/**
 * Created on 18.09.2014.
 */

public class Animator {

    private ObjectMap<String, Integer> animationIdByName = new ObjectMap<>();
    private Array<Animation> animations;

    public Animator() {
        animations = new Array<>(false, 2, Animation.class);
    }

    public Animator(Animator prototype) {
        animations = new Array<>(false, prototype.animations.size, Animation.class);
        for(Animation a : prototype.animations) animations.add(a);
        animationIdByName = prototype.animationIdByName;
    }

    public void add(Animation animation) {
        animationIdByName.put(animation.name, animations.size);
        animations.add(animation);
    }

    public void start(String name, Entity[] entities, Transform[] transforms) {
        animations.get(animationIdByName.get(name)).start(entities, transforms);
    }

    public void update(Entity[] entities, Transform[] transforms, float delta) {
        for(Animation a : animations) a.update(entities, transforms, delta);
    }
}
