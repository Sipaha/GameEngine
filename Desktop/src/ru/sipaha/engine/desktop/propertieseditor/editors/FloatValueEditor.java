package ru.sipaha.engine.desktop.propertieseditor.editors;

import ru.sipaha.engine.core.Values;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;

/**
 * Created on 09.11.2014.
 */

public class FloatValueEditor extends DefaultCellEditor
                            implements TableCellEditor {

    private JTextField edit;
    private Values.Float value;

    public FloatValueEditor() {
        super(new JTextField());
        edit = (JTextField)getComponent();


        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField's focusLostBehavior property.)
        edit.getInputMap().put(KeyStroke.getKeyStroke(
                                KeyEvent.VK_ENTER, 0),
                                "check");
        edit.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();
            }
        });
    }

    //Override to invoke setValue on the formatted text field.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected,
                                                 int row, int column) {
        /*JFormattedTextField ftf =
                (JFormattedTextField)super.getTableCellEditorComponent(
                        table, value, isSelected, row, column);*/
        edit.setText(String.valueOf(((Values.Float) value).get()));
        this.value = (Values.Float)value;
        return edit;
    }

    public Object getCellEditorValue() {
        return value;
    }

    //Override to check whether the edit is valid,
    //setting the value if it is and complaining if
    //it isn't.  If it's OK for the editor to go
    //away, we need to invoke the superclass's version
    //of this method so that everything gets cleaned up.
    public boolean stopCellEditing() {
        try {
            value.set(Float.valueOf(edit.getText()));
        } catch (NumberFormatException e) {}
        return super.stopCellEditing();
    }
}
