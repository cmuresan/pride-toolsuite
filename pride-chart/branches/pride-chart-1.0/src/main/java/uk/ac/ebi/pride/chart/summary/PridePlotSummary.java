package uk.ac.ebi.pride.chart.summary;

import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.io.PrideDataReader;
import uk.ac.ebi.pride.chart.plot.PridePlot;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: Qingwei
 * Date: 11/06/13
 */
public abstract class PridePlotSummary {
    private Map<Integer, PridePlot> plotMap = new TreeMap<Integer, PridePlot>();

    protected PrideDataReader reader;

    protected PridePlotSummary() {
    }

    public PridePlotSummary(PrideDataReader reader) {
        if (reader == null) {
            throw new NullPointerException("PrideDataReader is null!");
        }

        this.reader = reader;
    }

    public void addPlot(PridePlot plot) {
        plotMap.put(plot.getType().getOrder(), plot);
    }

    public PridePlot getPlot(PrideChartType type) {
        return plotMap.get(type.getOrder());
    }

    public boolean contains(PrideChartType type) {
        return plotMap.containsKey(type.getOrder());
    }

    public Collection<PridePlot> getAllPlots() {
        return plotMap.values();
    }

    public void removePlot(PrideChartType type) {
        plotMap.remove(type.getOrder());
    }

    public void clear() {
        plotMap.clear();
    }
}
