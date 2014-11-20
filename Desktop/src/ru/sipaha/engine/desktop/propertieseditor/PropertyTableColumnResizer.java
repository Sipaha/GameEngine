package ru.sipaha.engine.desktop.propertieseditor;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created on 09.11.2014.
 */

public class PropertyTableColumnResizer extends MouseInputAdapter {

    public static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
    private int mouseXOffset;
    private Cursor otherCursor;
    private JTable table;

    public PropertyTableColumnResizer(JTable table) {
        this.otherCursor = resizeCursor;
        this.table = table;
        table.addMouseListener(this);
        table.addMouseMotionListener(this);
    }

    private boolean canResize(TableColumn col) {
        return col != null && table.getTableHeader().getResizingAllowed() && col.getResizable();
    }

    private TableColumn getResizingColumn(Point point) {
        return this.getResizingColumn(point, table.columnAtPoint(point));
    }

    private TableColumn getResizingColumn(Point point, int col) {
        if(col == -1) {
            return null;
        } else {
            int row = this.table.rowAtPoint(point);
            if(row == -1) {
                return null;
            } else {
                Rectangle cellRect = this.table.getCellRect(row, col, true);
                cellRect.grow(-3, 0);
                if(cellRect.contains(point)) {
                    return null;
                } else {
                    int x = cellRect.x + cellRect.width / 2;
                    int resultCol;
                    if(table.getTableHeader().getComponentOrientation().isLeftToRight()) {
                        resultCol = point.x < x ? col - 1 : col;
                    } else {
                        resultCol = point.x < x ? col : col - 1;
                    }

                    return resultCol == -1 ? null : table.getTableHeader().getColumnModel().getColumn(resultCol);
                }
            }
        }
    }

    public void mousePressed(MouseEvent var1) {
        table.getTableHeader().setDraggedColumn(null);
        table.getTableHeader().setResizingColumn(null);
        table.getTableHeader().setDraggedDistance(0);
        Point var2 = var1.getPoint();
        int var3 = table.columnAtPoint(var2);
        if(var3 != -1) {
            TableColumn var4 = getResizingColumn(var2, var3);
            if(canResize(var4)) {
                table.getTableHeader().setResizingColumn(var4);
                if(table.getTableHeader().getComponentOrientation().isLeftToRight()) {
                    mouseXOffset = var2.x - var4.getWidth();
                } else {
                    mouseXOffset = var2.x + var4.getWidth();
                }

            }
        }
    }

    private void swapCursor() {
        Cursor cursor = table.getCursor();
        table.setCursor(otherCursor);
        otherCursor = cursor;
    }

    public void mouseMoved(MouseEvent var1) {
        if(canResize(getResizingColumn(var1.getPoint())) != (table.getCursor() == resizeCursor)) {
            swapCursor();
        }

    }

    public void mouseDragged(MouseEvent event) {
        int x = event.getX();
        TableColumn column = table.getTableHeader().getResizingColumn();
        boolean leftToRight = table.getTableHeader().getComponentOrientation().isLeftToRight();
        if(column != null) {
            int width = column.getWidth();
            int newWidth;
            if(leftToRight) {
                newWidth = x - this.mouseXOffset;
            } else {
                newWidth = this.mouseXOffset - x;
            }

            column.setWidth(newWidth);
            Container container;
            if(this.table.getTableHeader().getParent() == null
                    || (container = table.getTableHeader().getParent().getParent()) == null
                    || !(container instanceof JScrollPane)) {
                return;
            }

            if(!container.getComponentOrientation().isLeftToRight() && !leftToRight && table != null) {
                JViewport viewport = ((JScrollPane)container).getViewport();
                int viewportWidth = viewport.getWidth();
                int widthDelta = newWidth - width;
                int newTableWidth = table.getWidth() + widthDelta;
                Dimension tableDim = table.getSize();
                tableDim.width += widthDelta;
                table.setSize(tableDim);
                if(newTableWidth >= viewportWidth && table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF) {
                    Point pos = viewport.getViewPosition();
                    pos.x = Math.max(0, Math.min(newTableWidth - viewportWidth, pos.x + widthDelta));
                    viewport.setViewPosition(pos);
                    mouseXOffset += widthDelta;
                }
            }
        }

    }

    public void mouseReleased(MouseEvent var1) {
        this.table.getTableHeader().setResizingColumn(null);
        this.table.getTableHeader().setDraggedColumn(null);
    }
}
