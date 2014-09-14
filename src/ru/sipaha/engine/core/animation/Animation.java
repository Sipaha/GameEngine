package ru.sipaha.engine.core.animation;

public abstract class Animation {
    public String name;

    private boolean loop = false;
    private boolean run = false;
    private float pause_time = 0;
    private boolean pause = false;
    private float pauseTimer = 0;
    private float time = 0;
    private float timeLimit = 0;

    public void update(float delta) {
        if (!run) return;
        if (!pause) {
            time += delta;
            if (time >= timeLimit) {
                run = loop;
                if (run) {
                    if (pause_time > 0) {
                        pause = true;
                        time = 0;
                    } else {
                        time -= timeLimit;
                    }
                } else {
                    time = timeLimit;
                }
            }
            updateTarget(time);
        } else {
            if ((pauseTimer += delta) >= pause_time) {
                pauseTimer = 0;
                pause = false;
            }
        }
    }
    public abstract void updateTarget(float time);
}
