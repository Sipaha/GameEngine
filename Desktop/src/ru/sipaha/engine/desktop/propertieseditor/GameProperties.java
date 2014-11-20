package ru.sipaha.engine.desktop.propertieseditor;

import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.Script;
import ru.sipaha.engine.core.Values;
import ru.sipaha.engine.desktop.propertieseditor.editors.*;
import ru.sipaha.engine.desktop.propertieseditor.renderers.SectionRenderer;
import ru.sipaha.engine.desktop.propertieseditor.sections.GeneralSection;
import ru.sipaha.engine.desktop.propertieseditor.sections.RenderUnitSection;
import ru.sipaha.engine.desktop.propertieseditor.sections.TransformSection;
import ru.sipaha.engine.utils.Array;

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
        addCellRenderer(GeneralSection.class, sectionRenderer);

        addCellEditor(Values.BlendFunction.class, new BlendingEditor());
        addCellEditor(Values.Float.class, new FloatValueEditor());
        addCellEditor(Values.Bool.class, new BoolEditor());
        addCellEditor(Values.ShaderValue.class, new ShaderEditor());
        addCellEditor(Values.Int.class, new IntEditor());

        setPreferredSize(new Dimension(320, 0));
    }

    public void set(GameObject gameObject) {
        model.clear();
        model.addPropertySection(transformSection.set(gameObject.transform));
        model.addPropertySection(renderUnitSection.set(gameObject));

        Array<Script> scripts = gameObject.getScripts();
        for(Script script : scripts) {
            model.addPropertySection(new GeneralSection(script));
        }

        repaint();
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
        addCellEditor(Values.RenderLayerValue.class, new RenderLayerValueEditor(engine.renderer.getLayers()));
    }
}
