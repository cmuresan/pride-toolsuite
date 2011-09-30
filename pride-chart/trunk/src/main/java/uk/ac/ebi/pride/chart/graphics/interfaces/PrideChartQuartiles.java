package uk.ac.ebi.pride.chart.graphics.interfaces;

import uk.ac.ebi.pride.chart.graphics.implementation.data.QuartilesType;

/**
 * <p>Define the interface for all the charts with options</p>
 *
 * @author Antonio Fabregat
 * Date: 09-mar-2011
 * Time: 15:47:36
 */
public interface PrideChartQuartiles {
    public void setQuartileVisibility(QuartilesType type, boolean visible);
    public boolean isMultipleQuartile();
    public boolean isQuartileEmpty(QuartilesType type);
}
