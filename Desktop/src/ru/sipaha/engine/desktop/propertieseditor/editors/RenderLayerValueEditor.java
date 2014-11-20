package ru.sipaha.engine.desktop.propertieseditor.editors;

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

public class RenderLayerValueEditor extends AbstractCellEditor
                                        implements TableCellEditor {

    private JComboBox<RenderLayer> comboBox;
    private Array<RenderLayer> layers;
    private Values.RenderLayerValue value;

    public RenderLayerValueEditor(Array<RenderLayer> layers) {
        this.layers = layers;
        comboBox = new JComboBox<>();
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    value.set((RenderLayer)comboBox.getSelectedItem());
                    stopCellEditing();
                }
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected,
                                                 int row, int column) {
        this.value = (Values.RenderLayerValue)value;
        comboBox.removeAllItems();
        for(RenderLayer layer : layers) {
            comboBox.addItem(layer);
        }
        comboBox.setSelectedItem(this.value.getLayer());
        return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }
}
