package ru.sipaha.engine.core;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import ru.sipaha.engine.gameobjectdata.EntityRenderer;

public class Entity {
    public String name;
    public int transformId = 0;
    public EntityRenderer renderer;
    public FixtureDef fixture = null;

    public Entity(TextureRegion region) {
        renderer = new EntityRenderer(region);
    }

    public Entity(Entity prototype) {
        transformId = prototype.transformId;
        renderer = new EntityRenderer(prototype.renderer);
    }

    public void reset(Entity e) {
        renderer.reset(e.renderer);
    }
}
