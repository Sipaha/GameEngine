package ru.sipaha.engine.graphics.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import ru.sipaha.engine.core.Sprite;
import ru.sipaha.engine.graphics.RenderBuffer;
import ru.sipaha.engine.utils.Array;

/**
 * Created on 12.10.2014.
 */

public class UIElement extends Sprite {

    private Array<UIElement> children;
    private Layout layout = null;
    private ObjectMap<String, String> parameters;

    public UIElement(TextureRegion r) {
        super(r);
    }

    public UIElement(Texture t) {
        super(t);
    }

    public UIElement(Sprite sprite) {
        super(sprite);
    }

    public UIElement(float u, float v, float u2, float v2, float width, float height) {
        super(u, v, u2, v2, width, height);
    }

    public void setParameter(String name, String value) {
        if(parameters == null) parameters = new ObjectMap<>();
        parameters.put(name, value);
        if(name.equalsIgnoreCase("width")) width.set(Float.parseFloat(value));
        if(name.equalsIgnoreCase("height")) height.set(Float.parseFloat(value));
        if(name.equalsIgnoreCase("size")) {
            String[] params = value.split("\\s+");
            setSize(Float.parseFloat(params[0]), Float.parseFloat(params[1]));
        }
    }

    public void addChild(UIElement child) {
        if(children == null) children = new Array<>(false, 4, UIElement.class);
        children.add(child);
    }

    @Override
    public void render(RenderBuffer buffer) {
        super.render(buffer);
    }
}
