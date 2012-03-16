package uk.ac.ebi.pride.gui.component.chart;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideChartManager;

/**
 * <p>A.</p>
 *
 * @author Antonio Fabregat
 * Date: 20-ago-2010
 * Time: 11:58:35
  */
class PrideChartMouseEvent implements ChartMouseListener {
    private ChartTabPane chartTabPane;
    private PrideChartManager managedPrideChart;

    PrideChartMouseEvent(ChartTabPane chartTabPane, PrideChartManager managedPrideChart) {
        this.chartTabPane = chartTabPane;
        this.managedPrideChart = managedPrideChart;
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
        chartTabPane.showChart(managedPrideChart);
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {
        //Nothing
    }
}