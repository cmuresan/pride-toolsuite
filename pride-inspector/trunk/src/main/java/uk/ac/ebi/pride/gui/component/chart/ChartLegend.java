package uk.ac.ebi.pride.gui.component.chart;

import uk.ac.ebi.pride.chart.graphics.interfaces.PrideChartLegend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * <p>.</p>
 *
 * @author Antonio Fabregat
 *         Date: 31-ago-2010
 *         Time: 11:16:24
 */
public class ChartLegend extends JPanel {


    public ChartLegend(PrideChartManager managedPrideChart) {
        super(new BorderLayout());
        Dimension dimension = new Dimension(200,50);
        PrideChartLegend pcl = (PrideChartLegend) managedPrideChart.getPrideChart();

        setVisible(managedPrideChart.isLegendVisible());

        JTable chartLegend = new JTable();

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(pcl.getColumnNames());

        for (Object left : pcl.getOrderedLegend()) {
            String right = pcl.getLegendMeaning(left.toString());
            model.addRow(new Object[]{left, right});
        }
        
        chartLegend.setModel(model);
        //chartLegend.setAutoCreateRowSorter(false);
        chartLegend.setFillsViewportHeight(true);
        chartLegend.getColumnModel().getColumn(0).setMaxWidth(100);

        JScrollPane scrollPane = new JScrollPane(chartLegend,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.add(scrollPane, BorderLayout.CENTER);
        this.setPreferredSize(dimension);
    }
}
