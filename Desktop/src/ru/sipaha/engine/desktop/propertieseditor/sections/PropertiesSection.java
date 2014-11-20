package ru.sipaha.engine.desktop.propertieseditor.sections;

import ru.sipaha.engine.desktop.propertieseditor.property.Property;

/**
 * Created on 09.11.2014.
 */

public abstract class PropertiesSection {
    private String name = "";
    private final State state = new State(true);

    public PropertiesSection(String name) {
        this.name = name;
    }

    public String getSectionName() {
        return this.name;
    }

    public final int getVisiblePropertiesCount() {
        return (state.expanded ? getPropertiesCount() : 0)+1;
    }

    public abstract int getPropertiesCount();

    public abstract Property getProperty(int idx);

    public void setExpanded(boolean expanded) {
        state.expanded = expanded;
    }

    public boolean isExpanded() {
        return state.expanded;
    }

    public String getName() {
        return name;
    }

    public State getState() {
        return state;
    }

    public static class State {
        public boolean expanded = true;
        public State(boolean expanded) {
            this.expanded = expanded;
        }
    }
}
