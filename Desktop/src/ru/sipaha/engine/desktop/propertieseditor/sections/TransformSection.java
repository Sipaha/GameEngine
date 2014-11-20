package ru.sipaha.engine.desktop.propertieseditor.sections;

import ru.sipaha.engine.core.Transform;
import ru.sipaha.engine.desktop.propertieseditor.property.Property;

/**
 * Created on 10.11.2014.
 */

public class TransformSection extends PropertiesSection {

    private Property positionX = new Property("Position.X");
    private Property positionY = new Property("Position.Y");
    private Property scaleX = new Property("Scale.X");
    private Property scaleY = new Property("Scale.Y");
    private Property angle = new Property("Angle");

    public TransformSection() {
        super("Transform");
    }

    public TransformSection set(Transform transform) {
        positionX.setPropertyObject(transform.x);
        positionY.setPropertyObject(transform.y);
        scaleX.setPropertyObject(transform.scaleX);
        scaleY.setPropertyObject(transform.scaleY);
        angle.setPropertyObject(transform.angle);
        return this;
    }

    @Override
    public int getPropertiesCount() {
        return 5;
    }

    @Override
    public Property getProperty(int idx) {
        switch (idx) {
            case 0: return positionX;
            case 1: return positionY;
            case 2: return scaleX;
            case 3: return scaleY;
            case 4: return angle;
        }
        return null;
    }
}
