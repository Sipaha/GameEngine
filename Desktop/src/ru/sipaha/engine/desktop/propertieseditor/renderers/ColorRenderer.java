package ru.sipaha.engine.desktop.propertieseditor.renderers;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created on 09.11.2014.
 */

public class ColorRenderer extends JLabel
        implements TableCellRenderer {

    Border unselectedBorder = null;
    Border selectedBorder = null;

    public ColorRenderer() {
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(
            JTable table, Object color,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        Color newColor = (Color)color;
        setBackground(newColor);

        if (isSelected) {
            if (selectedBorder == null) {
                selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                            table.getSelectionBackground());
            }
            setBorder(selectedBorder);
        } else {
            if (unselectedBorder == null) {
                unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                            table.getBackground());
            }
            setBorder(unselectedBorder);
        }

        setToolTipText("RGB value: " + newColor.getRed() + ", "
                + newColor.getGreen() + ", "
                + newColor.getBlue());
        return this;
    }
}
