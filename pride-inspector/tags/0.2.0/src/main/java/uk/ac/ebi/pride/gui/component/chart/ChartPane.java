package uk.ac.ebi.pride.gui.component.chart;

import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Abstract class to define the chart common characteristics.</p>
 *
 * @author Antonio Fabregat
 * Date: 25-ago-2010
 * Time: 15:25:59
 */
public abstract class ChartPane extends JPanel {
    protected ChartTabPane container;
    protected PrideChart prideChart;

    /**
     * <p> Creates an instance of this ChartPane object, setting all fields as per description below.</p>
     *
     * @param container The tab containing the chart pane
     * @param prideChart The chart to be contained in this chart pane
     */
    public ChartPane(ChartTabPane container, PrideChart prideChart) {
        super(new BorderLayout());
        
        this.container = container;
        this.prideChart = prideChart;
    }

    /**
     * Show a dialog message with the information associated to the chart shown in the pane
     */
    public void showChartInformation() {
        JOptionPane.showMessageDialog(this,
                prideChart.getChartInfo(),
                prideChart.getChart().getTitle().getText(),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
