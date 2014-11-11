package ru.sipaha.engine.desktop.properties;

import ru.sipaha.engine.desktop.properties.editors.SectionEditor;
import ru.sipaha.engine.desktop.properties.editors.SectionStateEditor;
import ru.sipaha.engine.desktop.properties.renderers.BorderRenderer;
import ru.sipaha.engine.desktop.properties.renderers.SectionRenderer;
import ru.sipaha.engine.desktop.properties.renderers.SectionStateRenderer;
import ru.sipaha.engine.desktop.properties.sections.PropertiesSection;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.HashMap;

/**
 * Created on 09.11.2014.
 */

public class PropertiesTable extends JTable {
    protected MouseInputAdapter rowResizer = null;
    protected MouseInputAdapter columnResizer = null;
    protected PropertiesTableModel model = (PropertiesTableModel)getModel();
    private HashMap<Class<?>, TableCellEditor> editors = new HashMap<>();
    private HashMap<Class<?>, TableCellRenderer> renderers = new HashMap<>();
    public static Color borderColor = new Color(214, 225, 249);

    public PropertiesTable() {
        super(new PropertiesTableModel());
        renderers.put(null, new BorderRenderer());
        renderers.put(PropertiesSection.class, new SectionRenderer());
        renderers.put(PropertiesSection.State.class, new SectionStateRenderer());
        editors.put(PropertiesSection.class, new SectionEditor());
        editors.put(PropertiesSection.State.class, new SectionStateEditor());

        setColumnConstraints();
        setRowHeight(18);
        setGridColor(borderColor);
        setShowVerticalLines(false);
    }

    public void setColumnConstraints() {
        TableColumn col = getColumnModel().getColumn(0);
        col.setPreferredWidth(10);
        col.setMaxWidth(col.getPreferredWidth());
        col.setResizable(false);
        col = getColumnModel().getColumn(2);
        col.setResizable(false);
        getTableHeader().setReorderingAllowed(false);
    }

    public void addCellRenderer(Class<?> type, TableCellRenderer renderer) {
        this.renderers.put(type, renderer);
    }

    public void addCellEditor(Class<?> type, TableCellEditor editor) {
        this.editors.put(type, editor);
    }

    public TableCellRenderer getCellRenderer(int row, int col) {
        Object value = getModel().getValueAt(row, col);
        TableCellRenderer renderer = renderers.get(value == null ? null : value.getClass());
        if(renderer == null) {
            renderer = super.getCellRenderer(row, col);
        }
        return renderer;
    }

    public TableCellEditor getCellEditor(int row, int col) {
        Object value = getModel().getValueAt(row, col);
        TableCellEditor editor = editors.get(value == null ? null : value.getClass());
        if(editor == null) {
            editor = super.getCellEditor(row, col);
        }
        return editor;
    }

    public void setResizable(boolean rows, boolean cols) {
        if(rows) {
            if(rowResizer == null) {
                rowResizer = new PropertyTableRowResizer(this);
            }
        } else if(rowResizer != null) {
            removeMouseListener(rowResizer);
            removeMouseMotionListener(rowResizer);
            rowResizer = null;
        }

        if(cols) {
            if(columnResizer == null) {
                columnResizer = new PropertyTableColumnResizer(this);
            }
        } else if(columnResizer != null) {
            removeMouseListener(columnResizer);
            removeMouseMotionListener(columnResizer);
            columnResizer = null;
        }
    }

    public void changeSelection(int row, int col, boolean toggle, boolean extend) {
        if(getCursor() != PropertyTableColumnResizer.resizeCursor
                && getCursor() != PropertyTableRowResizer.resizeCursor) {
            super.changeSelection(row, col, toggle, extend);
        }
    }
}
