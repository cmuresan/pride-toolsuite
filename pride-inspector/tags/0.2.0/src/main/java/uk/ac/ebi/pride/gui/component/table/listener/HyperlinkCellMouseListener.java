package uk.ac.ebi.pride.gui.component.table.listener;

import uk.ac.ebi.pride.gui.utils.HttpUtilities;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 *
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 15:07:28
 */
public class HyperlinkCellMouseListener extends MouseAdapter {
    private JTable table;
    private int column;
    private String urlPrefix;
    private String urlEndfix;

    public HyperlinkCellMouseListener(JTable table, int column,
                                      String urlPrefix, String urlEndfix) {
        this.table = table;
        this.column = column;
        this.urlPrefix = urlPrefix;
        this.urlEndfix = urlEndfix;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
        if (col == column) {
            int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
            TableModel tableModel = table.getModel();
            Object val = tableModel.getValueAt(row, col);
            if (val != null) {
                HttpUtilities.openURL(urlPrefix + val.toString()+urlEndfix);
            }
        }
    }
}
