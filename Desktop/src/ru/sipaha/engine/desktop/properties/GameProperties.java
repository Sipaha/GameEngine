package ru.sipaha.engine.desktop.properties;

import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.Values;
import ru.sipaha.engine.desktop.properties.editors.*;
import ru.sipaha.engine.desktop.properties.renderers.SectionRenderer;
import ru.sipaha.engine.desktop.properties.sections.RenderUnitSection;
import ru.sipaha.engine.desktop.properties.sections.TransformSection;

import java.awt.*;

/**
 * Created on 11.11.2014.
 */

public class GameProperties extends PropertiesTable {

    private Engine engine;

    private TransformSection transformSection = new TransformSection();
    private RenderUnitSection renderUnitSection = new RenderUnitSection();

    public GameProperties() {
        SectionRenderer sectionRenderer = new SectionRenderer();
        addCellRenderer(TransformSection.class, sectionRenderer);
        addCellRenderer(RenderUnitSection.class, sectionRenderer);

        addCellEditor(Values.BlendFunction.class, new BlendingEditor());
        addCellEditor(Values.Float.class, new FloatValueEditor());
        addCellEditor(Values.Bool.class, new BoolEditor());
        addCellEditor(Values.ShaderValue.class, new ShaderEditor());
        addCellEditor(Values.Int.class, new IntEditor());

        setPreferredSize(new Dimension(320, 0));
    }

    public void set(GameObject gameObject) {
        model.clear();
        model.addPropertySection(transformSection.set(gameObject.getTransform()));
        model.addPropertySection(renderUnitSection.set(gameObject));
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
        addCellEditor(Values.RenderLayerValue.class, new RenderLayerValueEditor(engine.renderer.getLayers()));
    }
}
