package ru.sipaha.engine.desktop.propertieseditor.sections;

import ru.sipaha.engine.desktop.propertieseditor.property.Property;
import ru.sipaha.engine.graphics.RenderUnit;

/**
 * Created on 10.11.2014.
 */

public class RenderUnitSection extends PropertiesSection {

    private Property renderLayerTag = new Property("Render layer");
    private Property shader = new Property("Shader");
    private Property zOrder = new Property("Z order");
    private Property blendingEnabled = new Property("Blending enabled");
    private Property blendSrcFunc = new Property("Blending sfactor");
    private Property blendDstFunc = new Property("Blending dfactor");
    private Property isStatic = new Property("Static");

    public RenderUnitSection() {
        super("Render unit");
    }

    public RenderUnitSection set(RenderUnit unit) {
        renderLayerTag.setPropertyObject(unit.renderLayer);
        shader.setPropertyObject(unit.shader);
        zOrder.setPropertyObject(unit.zOrder);
        blendingEnabled.setPropertyObject(unit.blendingEnabled);
        blendSrcFunc.setPropertyObject(unit.blendSrcFunc);
        blendDstFunc.setPropertyObject(unit.blendDstFunc);
        isStatic.setPropertyObject(unit.isStatic);
        return this;
    }

    @Override
    public int getPropertiesCount() {
        return 7;
    }

    @Override
    public Property getProperty(int idx) {
        switch (idx) {
            case 0: return renderLayerTag;
            case 1: return shader;
            case 2: return zOrder;
            case 3: return blendingEnabled;
            case 4: return blendSrcFunc;
            case 5: return blendDstFunc;
            case 6: return isStatic;
        }
        return null;
    }
}
