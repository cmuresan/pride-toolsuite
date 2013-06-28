package uk.ac.ebi.pride.chart.io;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.dataset.PrideHistogramDataSource;
import uk.ac.ebi.pride.chart.dataset.PrideXYDataSource;
import uk.ac.ebi.pride.chart.utils.PridePlotConstants;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public abstract class PrideDataReader {
    private long startTime;
    private Logger logger = Logger.getLogger(PrideDataReader.class);

    protected SortedMap<PrideChartType, PrideXYDataSource> xyDataSourceMap = new TreeMap<PrideChartType, PrideXYDataSource>();
    protected SortedMap<PrideChartType, PrideHistogramDataSource> histogramDataSourceMap = new TreeMap<PrideChartType, PrideHistogramDataSource>();

    public void readData() throws PrideDataException {
        start();
        reading();
        end();
    }

    protected abstract void start();

    protected void start(String source) {
        logger.debug("Start load data by calling " + source + "." + PridePlotConstants.NEW_LINE);
        startTime = System.currentTimeMillis();
    }

    protected abstract void reading() throws PrideDataException;

    protected abstract void end();

    protected void end(String source) {
        logger.debug("End load data from " + source + "." + PridePlotConstants.NEW_LINE);
        long endTime = System.currentTimeMillis();
        double costTime = (endTime - startTime) / 1000d;
        logger.debug("Cost time: " + String.format("%1$.2f", costTime) + "(s)" + PridePlotConstants.NEW_LINE);
    }

    public SortedMap<PrideChartType, PrideXYDataSource> getXYDataSourceMap() {
        return xyDataSourceMap;
    }

    public SortedMap<PrideChartType, PrideHistogramDataSource> getHistogramDataSourceMap() {
        return histogramDataSourceMap;
    }

    public Set<PrideChartType> getChartTypeList() {
        Set<PrideChartType> chartTypeList = new TreeSet<PrideChartType>();

        chartTypeList.addAll(xyDataSourceMap.keySet());
        chartTypeList.addAll(histogramDataSourceMap.keySet());

        return chartTypeList;
    }
}
