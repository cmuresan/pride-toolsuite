package uk.ac.ebi.pride.chart;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import uk.ac.ebi.pride.chart.graphics.implementation.data.QuartilesType;
import uk.ac.ebi.pride.chart.plot.PridePlot;
import uk.ac.ebi.pride.chart.plot.PrideXYPlot;
import uk.ac.ebi.pride.chart.summary.PridePlotSummary;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public class PrideChartFactory {
    private static ChartTheme currentTheme = new StandardChartTheme("JFree");

    /**
     * Contains the supported quartiles as Data Series Types
     */
    protected List<QuartilesType> supportedQuartiles = new ArrayList<QuartilesType>();

    /**
     * Contains the visible quatiles as Data Series Types
     */
    protected QuartilesType visibleQuartiles = QuartilesType.NONE;

    private PrideChartFactory() {}

    public static JFreeChart getChart(PrideXYPlot plot) {
        PrideChartType type = plot.getType();

        JFreeChart chart = new JFreeChart(type.getTitle(), JFreeChart.DEFAULT_TITLE_FONT, plot, type.isLegend());
        currentTheme.apply(chart);

        return chart;
    }

    public static List<JFreeChart> getChartList(PridePlotSummary summary) {
        List<JFreeChart> charts = new ArrayList<JFreeChart>();

        for (PridePlot plot : summary.getAllPlots()) {
            if (plot instanceof PrideXYPlot) {
                charts.add(getChart((PrideXYPlot)plot));
            }
        }

        return charts;
    }
}
