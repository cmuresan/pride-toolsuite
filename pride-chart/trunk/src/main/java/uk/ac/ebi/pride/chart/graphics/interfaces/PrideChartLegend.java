package uk.ac.ebi.pride.chart.graphics.interfaces;

/**
 * <p> </p>
 *
 * @user: Antonio Fabregat
 * Date: 27-ago-2010
 * Time: 12:17:35
 */
public interface PrideChartLegend {
    public String[] getColumnNames();
    public Object[] getOrderedLegend();
    public String getLegendMeaning(String key);
}
