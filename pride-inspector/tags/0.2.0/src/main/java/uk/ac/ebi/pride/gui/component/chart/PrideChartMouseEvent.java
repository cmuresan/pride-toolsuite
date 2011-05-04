package uk.ac.ebi.pride.gui.component.chart;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;

/**
 * <p>A.</p>
 *
 * @author Antonio Fabregat
 * Date: 20-ago-2010
 * Time: 11:58:35
  */
class PrideChartMouseEvent implements ChartMouseListener {
    private ChartTabPane chartTabPane;
    private PrideChart prideChart;

    PrideChartMouseEvent(ChartTabPane chartTabPane, PrideChart prideChart) {
        this.chartTabPane = chartTabPane;
        this.prideChart = prideChart;
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
        chartTabPane.showChart(prideChart);
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {
        //Nothing
    }
}