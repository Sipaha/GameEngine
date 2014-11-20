package ru.sipaha.engine.desktop.propertieseditor.property;

/**
 * Created on 09.11.2014.
 */

public class Property {
    protected String name;
    protected Object object = null;

    public Property() {}

    public Property(String name) {
        this.name = name;
    }

    public Property(String name, Object value) {
        this.name = name;
        this.object = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getPropertyObject() {
        return object;
    }

    public void setPropertyObject(Object value) {
        object = value;
    }
}
