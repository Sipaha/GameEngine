package ru.sipaha.engine.graphics.gui;

import ru.sipaha.engine.graphics.Batch;
import ru.sipaha.engine.graphics.renderlayers.RenderLayer;

import java.awt.*;

/**
 * Created on 09.10.2014.
 */

public class InterfaceLayer extends RenderLayer {
    public static final String DEFAULT_NAME = "InterfaceLayer";
    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;

    private Batch batch = new Batch();
    private UIContainer container = new UIContainer();

    public InterfaceLayer() {
        this(DEFAULT_NAME);
    }

    public InterfaceLayer(String name) {
        super(name);
        container.setBounds(0, DEFAULT_WIDTH, DEFAULT_HEIGHT, 0);
    }

    public void add(UIElement element) {
        container.add(element);
        /*if(element.renderer != null) {
            batch.add(element.renderer);
        }*/
    }

    public void remove(UIElement element) {
        container.remove(element);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        container.setBounds(0, width, height, 0);
    }

    @Override
    public void render() {
        batch.draw(camera.combined);
    }
}
