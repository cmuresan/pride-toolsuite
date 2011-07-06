package uk.ac.ebi.pride.gui.component.table.listener;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * DynamicColumnListener listens to adding new columns
 * <p/>
 * This listener needs to couple with table.setAutoCreateColumnsFromModel(false)
 * If auto creation is true, then all the previous set renderer, sorters will be lost
 * <p/>
 * User: rwang
 * Date: 06/07/2011
 * Time: 09:46
 */
public class DynamicColumnListener implements TableModelListener {
    private JTable table;
    private int columnCounts;

    public DynamicColumnListener(JTable table) {
        this.table = table;
        this.columnCounts = table.getColumnCount();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        // detect event type
        if (e.getType() == TableModelEvent.UPDATE) {
            // new columns has been added
            TableModel model = (TableModel) e.getSource();
            int newColumnCounts = model.getColumnCount();
            if (newColumnCounts > columnCounts) {
                // set the new column counts
                columnCounts = newColumnCounts;

                // new column index
                int newColIndex = model.getColumnCount() - 1;
                // create a new column based on the last column
                TableColumn column = table instanceof JXTable ? new TableColumn(newColIndex) : new TableColumnExt(newColIndex);
                column.setHeaderValue(model.getColumnName(newColIndex));

                // add the new column
                TableColumnModel columnModel = table.getColumnModel();
                columnModel.addColumn(column);
            }
        }
    }
}
