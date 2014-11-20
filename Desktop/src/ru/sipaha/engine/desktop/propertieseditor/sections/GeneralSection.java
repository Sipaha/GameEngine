package ru.sipaha.engine.desktop.propertieseditor.sections;

import ru.sipaha.engine.desktop.propertieseditor.property.EnumProperty;
import ru.sipaha.engine.desktop.propertieseditor.property.PrimitiveProperty;
import ru.sipaha.engine.desktop.propertieseditor.property.Property;
import ru.sipaha.engine.utils.Array;

import java.lang.reflect.Field;

/**
 * Created on 20.11.2014.
 */

public class GeneralSection extends PropertiesSection {

    private Array<Property> properties = new Array<>(true, 10, Property.class);

    public GeneralSection(Object object) {
        super(object.toString());
        for(Field field : object.getClass().getFields()) {
            Class type = field.getType();
            if(type.isPrimitive()) {
                properties.add(new PrimitiveProperty(object, field));
            } else if(Enum.class.isAssignableFrom(type)) {
                properties.add(new EnumProperty(object, field));
            }
        }
    }

    @Override
    public int getPropertiesCount() {
        return properties.size;
    }

    @Override
    public Property getProperty(int idx) {
        return properties.get(idx);
    }
}
