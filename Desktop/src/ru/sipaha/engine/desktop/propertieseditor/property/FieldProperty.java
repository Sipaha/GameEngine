package ru.sipaha.engine.desktop.propertieseditor.property;

import java.lang.reflect.Field;

/**
 * Created on 20.11.2014.
 */

public abstract class FieldProperty extends Property {

    protected Field field;

    public FieldProperty(Object object, Field field) {
        super.setPropertyObject(object);
        this.field = field;
        StringBuilder nameBuilder = new StringBuilder(field.getName());
        nameBuilder.setCharAt(0, Character.toUpperCase(nameBuilder.charAt(0)));
        for(int i = nameBuilder.length()-1; i > 1; i--) {
            if(Character.isUpperCase(nameBuilder.charAt(i))
                    && Character.isLowerCase(nameBuilder.charAt(i-1))) {
                nameBuilder.insert(i, ' ');
            }
        }
        super.setName(nameBuilder.toString());
    }

    @Override
    public Object getPropertyObject() {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setPropertyObject(Object value) {
        try {
            Object newValue = parse((String) value);
            if(newValue != null) field.set(object, newValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setField(Field field) {
        this.field = field;
    }

    protected abstract Object parse(String str);
}
