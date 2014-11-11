package ru.sipaha.engine.desktop.properties.editors;

import ru.sipaha.engine.core.Values;
import ru.sipaha.engine.graphics.renderlayers.RenderLayer;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.Shaders;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created on 11.11.2014.
 */

public class ShaderEditor extends AbstractCellEditor
                                    implements TableCellEditor {

    private JComboBox<String> comboBox;
    private Values.ShaderValue value;

    public ShaderEditor() {
        comboBox = new JComboBox<>();
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    value.set((String)comboBox.getSelectedItem());
                    stopCellEditing();
                }
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected,
                                                 int row, int column) {
        this.value = (Values.ShaderValue)value;
        comboBox.removeAllItems();
        for(String shaderName : Shaders.getNamesOfShaders()) {
            comboBox.addItem(shaderName);
        }
        comboBox.setSelectedItem(this.value.getName());
        return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }
}
