package uk.ac.ebi.pride.chart.graphics.interfaces;

import uk.ac.ebi.pride.chart.graphics.implementation.data.DataSeriesType;

/**
 * <p>Define the interface for all the charts with options</p>
 *
 * @author Antonio Fabregat
 * Date: 22-nov-2010
 * Time: 14:07:55
 */
public interface PrideChartSpectraOptions {
    public void setSpectraTypeVisibility(DataSeriesType type, boolean visible);
    public boolean isSpectraMultipleChoice();
    public boolean isSpectraSeriesEmpty(DataSeriesType type);
}
