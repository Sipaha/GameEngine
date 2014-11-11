package ru.sipaha.engine.desktop.properties;

/**
 * Created on 09.11.2014.
 */

public class Property {
    private String name;
    private Object object = null;

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

    public Object getPropertyObject() {
        return object;
    }

    public void setPropertyObject(Object value) {
        object = value;
    }
}
