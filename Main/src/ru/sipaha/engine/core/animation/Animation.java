package ru.sipaha.engine.core.animation;

import ru.sipaha.engine.utils.Array;

/**
 * Created on 02.11.2014.
 */

public class Animation {
    public final String name;

    private boolean pause = false;
    private float pauseTimer = 0;
    private float time = 0;
    private boolean back = false;
    private boolean loop = false;
    private boolean run = false;
    private float pauseTime = 0;
    private boolean pingPong = false;

    private final Array<LinkedAnimatedValue> values;
    private float timeLimit = 0;

    public Animation(String name) {
        this.name = name;
        values = new Array<>(LinkedAnimatedValue.class);
    }

    public Animation(Animation prototype) {
        name = prototype.name;
        loop = prototype.loop;
        run = prototype.run;
        pauseTime = prototype.pauseTime;
        pause = prototype.pause;
        pingPong = prototype.pingPong;
        timeLimit = prototype.timeLimit;
        values = new Array<>(prototype.values);
        for(LinkedAnimatedValue value : prototype.values) {
            values.add(value.copy());
        }
    }

    public Animation(String name, LinkedAnimatedValue... animatedValues) {
        this.name = name;
        values = new Array<>(true, animatedValues.length, LinkedAnimatedValue.class);
        for(LinkedAnimatedValue value : animatedValues) addAnimatedValue(value);
    }

    public void update(float delta) {
        if (!run) return;
        if (pause && (pauseTimer += delta) >= pauseTime) {
            delta = pauseTimer - pauseTime;
            pauseTimer = 0;
            pause = false;
        }
        if (!pause) {
            time += delta;
            if (time >= timeLimit) {
                run = loop;
                if (run) {
                    if (pauseTime > 0) {
                        pause = true;
                        time = 0;
                    } else {
                        time -= timeLimit;
                    }
                } else {
                    time = timeLimit;
                }
            }
            updateValues(time);
        }
    }

    public void addAnimatedValue(LinkedAnimatedValue value) {
        values.add(value);
        timeLimit = Math.max(timeLimit, value.getMaxDefinedTime());
    }

    public void updateValues(float time) {
        for(LinkedAnimatedValue value : values) {
            value.update(time);
        }
    }

    public Animation start() {
        run = true;
        time = 0;
        pause = false;
        back = false;
        pauseTimer = 0;
        updateValues(0);
        return this;
    }

    public void stop() {
        run = false;
    }

    public void resume() {
        run = true;
    }

    public Animation setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public Animation setPauseTime(float pauseTime) {
        this.pauseTime = pauseTime;
        return this;
    }

    public Animation setPingPong(boolean pingPong) {
        this.pingPong = pingPong;
        return this;
    }

    public Animation reset(Animation prototype) {
        run = prototype.run;
        time = 0;
        pause = false;
        pauseTimer = 0;
        return this;
    }

    public void randomizeTimeOffset() {
        time = (float)(Math.random()*timeLimit);
    }

    public void findLinks(Array objects) {
        for(LinkedAnimatedValue anim : values) {
            anim.findLink(objects);
        }
    }

    public void setLinkedValues(Array objects) {
        for(LinkedAnimatedValue anim : values) {
            anim.setLinkedValue(objects);
        }
    }
}
