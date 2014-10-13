package ru.sipaha.engine.core.animation.discrete;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.gameobjectdata.EntityRenderer;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.structures.SpriteFrame;

/**
 * Created on 29.09.2014.
 */

public class SpriteAnimation extends DiscreteAnimation {

    private SpriteFrame[] frames;

    public SpriteAnimation(SpriteAnimation prototype) {
        super(prototype);
        frames = prototype.frames;
    }

    public SpriteAnimation(String name, float timeBetweenFrames, TextureRegion[] frames) {
        super(name, new float[frames.length]);
        for(int i = 1; i < frames.length; i++) framesTime[i] = framesTime[i-1]+timeBetweenFrames;
    }

    public SpriteAnimation(String name, SpriteFrame[] frames) {
        super(name, new float[frames.length]);
        for(int i = 0; i < frames.length; i++) {
            framesTime[i] = frames[i].time;
        }
        this.frames = frames;
    }

    @Override
    public void start(Entity[] entities, Transform[] transforms) {
        EntityRenderer renderer = entities[targetIdx].renderer;
        if(renderer.animatedSprite != this && renderer.animatedSprite != null) {
            renderer.animatedSprite.stop();
        }
        renderer.animatedSprite = this;
    }

    @Override
    public void frameChanged(Entity[] entities, int newFrameIdx) {
        EntityRenderer renderer = entities[targetIdx].renderer;
        SpriteFrame frame = frames[newFrameIdx];
        renderer.setUV(frame.u, frame.v, frame.u2, frame.v2);
    }
}
