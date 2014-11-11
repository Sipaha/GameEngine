package ru.sipaha.engine.desktop.properties.editors;

import ru.sipaha.engine.core.Values;
import ru.sipaha.engine.graphics.renderlayers.RenderLayer;
import ru.sipaha.engine.utils.Array;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created on 11.11.2014.
 */

public class BoolEditor extends AbstractCellEditor
                            implements TableCellEditor {

    private JComboBox<Boolean> comboBox;
    private Values.Bool value;

    public BoolEditor() {
        comboBox = new JComboBox<>();
        comboBox.addItem(Boolean.TRUE);
        comboBox.addItem(Boolean.FALSE);
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    value.set((Boolean)comboBox.getSelectedItem());
                    stopCellEditing();
                }
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected,
                                                 int row, int column) {
        this.value = (Values.Bool)value;
        comboBox.setSelectedItem(this.value.get());
        return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }
}