package uk.ac.ebi.pride.gui.component.table.listener;

import uk.ac.ebi.pride.gui.component.table.renderer.BarChartRenderer;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 18/08/2011
 * Time: 16:58
 */
public class BarChartColumnListener extends DynamicColumnListener {

    private int columnCounts;

    public BarChartColumnListener(JTable table) {
        super(table);
        this.columnCounts = table.getColumnCount();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        super.tableChanged(e);

        // detect event type
        if (e.getType() == TableModelEvent.UPDATE) {
            // new columns has been added
            TableModel model = (TableModel) e.getSource();
            int newColumnCounts = model.getColumnCount();
            if (newColumnCounts > columnCounts) {

                JTable table = getTable();
                for (int i = columnCounts; i < newColumnCounts; i++) {
                    String columnName = table.getModel().getColumnName(i);
                    TableColumn column = getTable().getColumn(columnName);
                    BarChartRenderer renderer = new BarChartRenderer(2, 0, 1);
                    column.setCellRenderer(renderer);
                }

                // set the new column counts
                columnCounts = newColumnCounts;
            }
        }
    }
}
