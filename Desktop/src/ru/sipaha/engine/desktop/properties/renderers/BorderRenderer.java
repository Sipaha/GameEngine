package ru.sipaha.engine.desktop.properties.renderers;


import ru.sipaha.engine.desktop.properties.PropertiesTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created on 09.11.2014.
 */

public class BorderRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                    boolean hasFocus, int row, int col) {
        Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        if(value == null) {
            setBackground(PropertiesTable.borderColor);
            if(hasFocus) {
                setBorder(BorderFactory.createEmptyBorder());
            }
        }
        return renderer;
    }
}
