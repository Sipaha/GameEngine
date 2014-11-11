package ru.sipaha.engine.desktop.properties.editors;

import ru.sipaha.engine.core.Values;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created on 11.11.2014.
 */

public class BlendingEditor extends AbstractCellEditor
                                implements TableCellEditor {

    private JComboBox<Values.BlendFunction.Function> comboBox;
    private Values.BlendFunction value;

    public BlendingEditor() {
        comboBox = new JComboBox<>();
        for(Values.BlendFunction.Function function : Values.BlendFunction.Function.values()) {
            comboBox.addItem(function);
        }
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    value.set((Values.BlendFunction.Function) comboBox.getSelectedItem());
                    stopCellEditing();
                }
            }}
        );
    }

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected,
                                                 int row, int column) {
        this.value = (Values.BlendFunction)value;
        comboBox.setSelectedIndex(this.value.getIndex());
        return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }
}
