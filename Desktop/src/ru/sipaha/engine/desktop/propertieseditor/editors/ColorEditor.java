package ru.sipaha.engine.desktop.propertieseditor.editors;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created on 09.11.2014.
 */

public class ColorEditor extends AbstractCellEditor
                            implements TableCellEditor, ActionListener {

    private JColorChooser colorChooser;
    private JDialog dialog;

    private Color currentColor;
    private JLabel label = new JLabel();

    public ColorEditor() {
        label.setOpaque(true);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                colorChooser.setColor(currentColor);
                dialog.setVisible(true);
                fireEditingStopped();
            }
        });

        //Set up the dialog that the button brings up.
        colorChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(null, "Pick a Color",
                                            true,  //modal
                                            colorChooser,
                                            this,  //OK button handler
                                            null); //no CANCEL button handler
    }

    public void actionPerformed(ActionEvent e) {
        currentColor = colorChooser.getColor();
    }

    public Object getCellEditorValue() {
        return currentColor;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        currentColor = (Color)value;
        label.setBackground(currentColor);
        return label;
    }
}
