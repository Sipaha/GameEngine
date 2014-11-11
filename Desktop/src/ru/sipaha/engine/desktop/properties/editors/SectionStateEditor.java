package ru.sipaha.engine.desktop.properties.editors;

import ru.sipaha.engine.desktop.properties.sections.PropertiesSection;
import ru.sipaha.engine.desktop.properties.PropertiesTableModel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

/**
 * Created on 09.11.2014.
 */

public class SectionStateEditor extends AbstractCellEditor implements TableCellEditor {

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if(value instanceof PropertiesSection.State) {
            PropertiesSection.State sectionState = (PropertiesSection.State)value;
            if(sectionState.expanded) {
                sectionState.expanded = false;
            } else {
                sectionState.expanded = true;
            }

            if(table.getModel() instanceof PropertiesTableModel) {
                PropertiesTableModel model = (PropertiesTableModel)table.getModel();
                model.fireTableDataChanged();
            }
        }
        return null;
    }

    public Object getCellEditorValue() {
        return null;
    }
}
