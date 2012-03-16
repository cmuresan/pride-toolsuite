package uk.ac.ebi.pride.gui.component.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.Title;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartException;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideChartManager;

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
    protected PrideChartManager managedPrideChart;
    protected JLabel title;
    protected JFreeChart chart;

    /**
     * <p> Creates an instance of this ChartPane object, setting all fields as per description below.</p>
     *
     * @param container The tab containing the chart pane
     * @param managedPrideChart The chart to be contained in this chart pane
     */
    public ChartPane(ChartTabPane container, PrideChartManager managedPrideChart) {
        super(new BorderLayout());
        
        this.container = container;
        this.managedPrideChart = managedPrideChart;

        //Set title
        title = new JLabel();
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        add(titleBar, BorderLayout.NORTH);

        try {
            chart = managedPrideChart.getPrideChart().getChart();
        } catch (PrideChartException e) {
            chart = null;
        }
    }

    protected void setChartProperties(JFreeChart chart){
        //Removing title and subtitle from the JFreeCharts
        chart.setTitle("");
        for (Object subtitle : chart.getSubtitles()) {
            //The legend is returned as a subtitle, but it has not to be deleted
            if (!(subtitle instanceof LegendTitle)) {
                chart.removeSubtitle((Title) subtitle);
            }
        }
    }
}
