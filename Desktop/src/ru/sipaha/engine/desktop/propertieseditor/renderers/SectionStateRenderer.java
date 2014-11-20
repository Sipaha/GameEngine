package ru.sipaha.engine.desktop.propertieseditor.renderers;

import ru.sipaha.engine.desktop.propertieseditor.sections.PropertiesSection;
import ru.sipaha.engine.desktop.propertieseditor.PropertiesTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.net.URL;

/**
 * Created on 09.11.2014.
 */

public class SectionStateRenderer extends DefaultTableCellRenderer {
    private static final String basePath = "/ru/sipaha/engine/desktop/propertieseditor/images/";

    private Icon expandSection;
    private Icon collapseSection;

    public SectionStateRenderer() {
        URL url = getClass().getResource(basePath + "expand.png");
        if(url != null) {
            expandSection = new ImageIcon(url);
        }
        url = getClass().getResource("/ru/sipaha/engine/desktop/propertieseditor/images/collapse.png");
        if(url != null) {
            collapseSection = new ImageIcon(url);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                    boolean hasFocus, int row, int col) {
        Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        setBackground(PropertiesTable.borderColor);
        setBorder(BorderFactory.createLineBorder(PropertiesTable.borderColor, 2));
        return renderer;
    }

    @Override
    public void setValue(Object value) {
        if(value instanceof PropertiesSection.State) {
            PropertiesSection.State state = (PropertiesSection.State)value;
            if(state.expanded) {
                setIcon(collapseSection);
            } else {
                setIcon(expandSection);
            }
        } else {
            setIcon(null);
        }

    }
}
