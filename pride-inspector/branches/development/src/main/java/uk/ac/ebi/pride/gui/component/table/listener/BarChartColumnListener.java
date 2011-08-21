package uk.ac.ebi.pride.gui.component.table.listener;

import no.uib.jsparklines.renderers.JSparklinesBarChartTableCellRenderer;
import org.jfree.chart.plot.PlotOrientation;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;

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
                    JSparklinesBarChartTableCellRenderer renderer = new JSparklinesBarChartTableCellRenderer(
                            PlotOrientation.HORIZONTAL,
                            10.0,
                            Color.green);
                    renderer.showNumberAndChart(true, 40);
                    column.setCellRenderer(renderer);
                }

                // set the new column counts
                columnCounts = newColumnCounts;
            }
        }
    }
}
