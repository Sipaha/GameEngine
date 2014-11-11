package ru.sipaha.engine.desktop.properties.editors;

import ru.sipaha.engine.core.Values;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created on 11.11.2014.
 */

public class IntEditor extends DefaultCellEditor
        implements TableCellEditor {

    private JTextField edit;
    private Values.Int value;

    public IntEditor() {
        super(new JTextField());
        edit = (JTextField) getComponent();

        edit.getInputMap().put(KeyStroke.getKeyStroke(
                                KeyEvent.VK_ENTER, 0),
                                "check");
        edit.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected,
                                                 int row, int column) {
        this.value = (Values.Int) value;
        edit.setText(String.valueOf(this.value));
        return edit;
    }

    public Object getCellEditorValue() {
        return value;
    }

    public boolean stopCellEditing() {
        try {
            value.set(Integer.valueOf(edit.getText()));
        } catch (NumberFormatException e) {}
        return super.stopCellEditing();
    }
}