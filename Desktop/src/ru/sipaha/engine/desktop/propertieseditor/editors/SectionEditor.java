package ru.sipaha.engine.desktop.propertieseditor.editors;

import ru.sipaha.engine.desktop.propertieseditor.sections.PropertiesSection;
import ru.sipaha.engine.desktop.propertieseditor.PropertiesTableModel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * Created on 09.11.2014.
 */

public class SectionEditor extends AbstractCellEditor implements TableCellEditor {

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
        if(value instanceof PropertiesSection) {
            PropertiesSection section = (PropertiesSection)value;
            if(section.isExpanded()) {
                section.setExpanded(false);
            } else {
                section.setExpanded(true);
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

    @Override
    public boolean isCellEditable(EventObject event) {
        return !(event instanceof MouseEvent) || ((MouseEvent) event).getClickCount() >= 2;
    }
}
