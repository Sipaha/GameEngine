package ru.sipaha.engine.desktop.propertieseditor.renderers;

import ru.sipaha.engine.desktop.propertieseditor.sections.PropertiesSection;
import ru.sipaha.engine.desktop.propertieseditor.PropertiesTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created on 09.11.2014.
 */

public class SectionRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                boolean hasFocus, int row, int column) {
        Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setFont(new Font("Dialog", Font.BOLD, 12));
        setBackground(PropertiesTable.borderColor);
        setBorder(BorderFactory.createLineBorder(PropertiesTable.borderColor, 2));
        return renderer;
    }

    public void setValue(Object value) {
        if(value instanceof PropertiesSection) {
            PropertiesSection var2 = (PropertiesSection)value;
            setText(var2.getSectionName());
        } else {
            setText("");
        }
    }
}
