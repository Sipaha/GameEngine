package ru.sipaha.engine.desktop.propertieseditor;

import ru.sipaha.engine.desktop.propertieseditor.sections.PropertiesSection;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created on 09.11.2014.
 */

public class PropertiesTableModel extends AbstractTableModel {
    private ArrayList<PropertiesSection> sections = new ArrayList<>(1);
    private ArrayList<ValueListener> setValueListeners = new ArrayList<>();

    public void addPropertySection(PropertiesSection section) {
        this.sections.add(section);
    }

    public PropertiesSection getPropertySection(String name) {
        for(PropertiesSection section : sections) {
           if(section.getName().equals(name)) {
               return section;
           }
        }
        return null;
    }

    public int getPropertSectionCount() {
        return sections.size();
    }

    public String getColumnName(int col) {
        return "";
    }

    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        int sum = 0;
        for(PropertiesSection section : sections) {
            sum += section.getVisiblePropertiesCount();
        }
        return sum;
    }

    public boolean isCellEditable(int row, int col) {
        int line = 0;
        for(PropertiesSection section : sections) {
            if(line == row) {
                return col == 0 || col == 1;
            }
            line += section.getVisiblePropertiesCount();
            if(row < line) {
                return col == 2;
            }
        }
        System.out.println("Strange!");
        return false;
    }

    public Object getValueAt(int row, int col) {
        int line = 0;
        for(PropertiesSection section : sections) {
            if (line == row) {
                if (col == 0) {
                    return section.getState();
                } else if (col == 1) {
                    return section;
                } else {
                    return null;
                }
            }
            int sectionEnd = line + section.getVisiblePropertiesCount();
            if (row < sectionEnd) {
                int idx = row - line - 1;
                if (col == 1) {
                    return section.getProperty(idx).getName();
                } else if(col == 2) {
                    return section.getProperty(idx).getPropertyObject();
                } else {
                    return null;
                }
            }
            line = sectionEnd;
        }
        System.out.println("Strange!");
        return null;
    }

    public void setValueAt(Object value, int row, int col) {
        if(col == 2) {
            int line = 0;
            for(PropertiesSection section : sections) {
                if(row == line) break;
                int sectionEnd = line + section.getVisiblePropertiesCount();
                if(row < sectionEnd) {
                    int idx = row - line - 1;
                    section.getProperty(idx).setPropertyObject(value);
                    break;
                } else {
                    line = sectionEnd;
                }
            }
            fireTableCellUpdated(row, col);
            notifySetValueListeners();
        }
    }

    public void addSetValueListener(ValueListener listener) {
        if(!this.setValueListeners.contains(listener)) {
            this.setValueListeners.add(listener);
        }

    }

    public void removeSetValueListener(ValueListener listener) {
        if(this.setValueListeners.contains(listener)) {
            this.setValueListeners.remove(listener);
        }

    }

    private void notifySetValueListeners() {
        for(ValueListener listener : setValueListeners) {
            listener.valueSet();
        }
    }

    public void clear() {
        sections.clear();
    }

    public interface ValueListener {
        void valueSet();
    }
}
