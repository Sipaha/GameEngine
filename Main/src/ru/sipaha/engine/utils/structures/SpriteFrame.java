package ru.sipaha.engine.utils.structures;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created on 29.09.2014.
 */

public class SpriteFrame {
    public float time;
    public float u,v,u2,v2;

    public SpriteFrame(float time, TextureRegion region) {
        this.time = time;
        u = region.getU();
        v = region.getV();
        u2 = region.getU2();
        v2 = region.getV2();
    }

    public SpriteFrame(float time, float u, float v, float u2, float v2) {
        this.time = time;
        this.u = u;
        this.v = v;
        this.u2 = u2;
        this.v2 = v2;
    }
}
