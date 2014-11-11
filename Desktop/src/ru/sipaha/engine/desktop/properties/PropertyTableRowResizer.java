package ru.sipaha.engine.desktop.properties;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created on 09.11.2014.
 */

public class PropertyTableRowResizer extends MouseInputAdapter {
    public static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
    private int mouseYOffset;
    private int resizingRow;
    private Cursor otherCursor;
    private JTable table;

    public PropertyTableRowResizer(JTable table) {
        this.otherCursor = resizeCursor;
        this.table = table;
        table.addMouseListener(this);
        table.addMouseMotionListener(this);
    }

    private int getResizingRow(Point point) {
        return this.getResizingRow(point, table.rowAtPoint(point));
    }

    private int getResizingRow(Point point, int row) {
        if(row == -1) {
            return -1;
        } else {
            int column = table.columnAtPoint(point);
            if(column == -1) {
                return -1;
            } else {
                Rectangle cellRect = table.getCellRect(row, column, true);
                cellRect.grow(0, -3);
                if(cellRect.contains(point)) {
                    return -1;
                } else {
                    int y = cellRect.y + cellRect.height / 2;
                    return point.y < y ? row - 1 : row;
                }
            }
        }
    }

    public void mousePressed(MouseEvent event) {
        Point point = event.getPoint();
        resizingRow = getResizingRow(point);
        mouseYOffset = point.y - table.getRowHeight(resizingRow);
    }

    private void swapCursor() {
        Cursor cursor = table.getCursor();
        table.setCursor(otherCursor);
        otherCursor = cursor;
    }

    public void mouseMoved(MouseEvent event) {
        if(getResizingRow(event.getPoint()) >= 0 != (table.getCursor() == resizeCursor)) {
            swapCursor();
        }

    }

    public void mouseDragged(MouseEvent event) {
        int y = event.getY();
        if(this.resizingRow >= 0) {
            int height = y - mouseYOffset;
            if(height > 0) {
                table.setRowHeight(resizingRow, height);
            }
        }

    }
}
