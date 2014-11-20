package ru.sipaha.engine.desktop.propertieseditor.property;

import java.lang.reflect.Field;

/**
 * Created on 20.11.2014.
 */

public class EnumProperty extends FieldProperty {

    public EnumProperty(Object object, Field field) {
        super(object, field);
    }

    @Override
    protected Object parse(String str) {
        try {
            return Enum.valueOf((Class<? extends Enum>)field.getType(), str);
        } catch (IllegalArgumentException e) {
            System.out.println("\""+str+"\" is not correct value!");
            return null;
        }
    }
}
