package uk.ac.ebi.pride.chart.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.dataset.PrideHistogramDataSource;
import uk.ac.ebi.pride.chart.dataset.PrideXYDataSource;
import uk.ac.ebi.pride.chart.utils.PridePlotConstants;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public abstract class PrideDataReader {
    private long startTime;
    private Logger logger = LoggerFactory.getLogger(PrideDataReader.class);

    protected SortedMap<PrideChartType, PrideXYDataSource> xyDataSourceMap = new TreeMap<PrideChartType, PrideXYDataSource>();
    protected SortedMap<PrideChartType, PrideHistogramDataSource> histogramDataSourceMap = new TreeMap<PrideChartType, PrideHistogramDataSource>();

    public void readData() {
        start();
        reading();
        end();
    }

    protected abstract void start();

    protected void start(String source) {
        logger.debug("Start load data by calling " + source + "." + PridePlotConstants.NEW_LINE);
        startTime = System.currentTimeMillis();
    }

    protected abstract void reading();

    protected abstract void end();

    protected void end(String source) {
        logger.debug("End load data from " + source + "." + PridePlotConstants.NEW_LINE);
        long endTime = System.currentTimeMillis();
        double costTime = (endTime - startTime) / 1000d / 60;
        logger.debug("Cost time: " + String.format("%1$.2f", costTime) + "(min)" + PridePlotConstants.NEW_LINE);
    }

    public SortedMap<PrideChartType, PrideXYDataSource> getXYDataSourceMap() {
        return xyDataSourceMap;
    }

    public SortedMap<PrideChartType, PrideHistogramDataSource> getHistogramDataSourceMap() {
        return histogramDataSourceMap;
    }
}
