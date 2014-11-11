package ru.sipaha.engine.core.animation;

import com.badlogic.gdx.utils.ObjectMap;
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

    public Animator(Animator prototype, Array objects) {
        animations = new Array<>(false, prototype.animations.size, Animation.class);
        for(Animation animation : prototype.animations) {
            animations.add(new Animation(animation));
        }
        animationIdByName = prototype.animationIdByName;
        for(Animation animation : animations) {
            animation.setLinkedValues(objects);
        }
    }

    public void initialize(Array objects) {
        for(Animation animation : animations) {
            animation.findLinks(objects);
        }
    }

    public void add(Animation animation) {
        animationIdByName.put(animation.name, animations.size);
        animations.add(animation);
    }

    public void start(String name) {
        animations.get(animationIdByName.get(name)).start();
    }

    public Animation get(String name) {
        return animations.get(animationIdByName.get(name));
    }

    public void update(float delta) {
        for(Animation a : animations) a.update(delta);
    }

    public void reset(Animator prototype) {
        for(int i = 0; i < animations.size; i++) {
            animations.items[i].reset(prototype.animations.items[i]);
        }
    }
}
