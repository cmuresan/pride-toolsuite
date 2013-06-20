package uk.ac.ebi.pride.chart.plot;

import uk.ac.ebi.pride.chart.PrideChartType;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public interface PridePlot {
    public PrideChartType getType();

    public String getTitle();

    public String getFullTitle();

    public String getDomainLabel();

    public String getRangeLabel();

    public boolean isLegend();
}
