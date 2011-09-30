package uk.ac.ebi.pride.gui.component.chart;

import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;

/**
 * <p>Class for manage the behaviour of the chart in PRIDE-Inspector</p>
 * In terms of legend and options panel, the visibility of this and other functions
 *
 * User: Antonio Fabregat
 * Date: 23-nov-2010
 * Time: 9:27:49
 */
public class PrideChartManager {

    private PrideChart chart;

    private boolean legendVisible = false;

    private boolean optionsVisible = true;

    public PrideChartManager(PrideChart chart) {
        this.chart = chart;
    }

    public PrideChart getPrideChart() {
        return chart;
    }

    public boolean hasLegend() {
        return chart.isValid() && chart.hasLegend();
    }

    public boolean hasSpectraOptions() {
        return chart.isValid() && chart.hasSpectraOptions();
    }

    public boolean hasQuartiles() {
        return chart.isValid() && chart.hasQuartiles();
    }

    public boolean isLegendVisible() {
        return chart.isValid() && legendVisible;
    }

    public void toggleLegendVisibility(){
        legendVisible = !legendVisible;
    }

    public boolean isOptionsVisible() {
        return chart.isValid() && optionsVisible;
    }

    public void toggleOptionsVisibility(){
        optionsVisible = !optionsVisible;
    }
}
