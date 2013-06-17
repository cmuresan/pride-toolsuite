package uk.ac.ebi.pride.chart.dataset;

import org.jfree.data.statistics.HistogramBin;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.List;

/**
 * User: Qingwei
 * Date: 13/06/13
 */
public class PrideDatasetFactory {
    private PrideDatasetFactory() {}

    public static XYBarDataset getXYBarDataset(PrideXYDataSource dataSource) {
        return new XYBarDataset(getXYDataset(dataSource), 0.5);
    }

    public static XYDataset getXYDataset(PrideXYDataSource dataSource) {
        XYSeries series = new XYSeries(dataSource.getType().toString());
        double[] domainValues = dataSource.getDomainData();
        double[] rangeValues = dataSource.getRangeData();

        for (int i = 0; i < domainValues.length; i++) {
            series.add(domainValues[i], rangeValues[i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    public static HistogramBarDataset getHistogramDataset(PrideHistogramDataSource dataSource, double binWidth, boolean startZero) {
        XYSeries series = new XYSeries(dataSource.getType().toString());
        List<HistogramBin> bins = dataSource.generateBins(binWidth, startZero);
        dataSource.addAllBins(bins);

        double[][] data = dataSource.getHistogram();

        for (int i = 0; i < data[0].length; i++) {
            series.add(data[0][i], data[1][i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return new HistogramBarDataset(dataset, 0.5, dataSource.getBins());
    }

    public static HistogramBarDataset getHistogramDataset(PrideHistogramDataSource dataSource) {
        XYSeries series = new XYSeries(dataSource.getType().toString());
        double[][] data = dataSource.getHistogram();

        for (int i = 0; i < data[0].length; i++) {
            series.add(data[0][i], data[1][i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return new HistogramBarDataset(dataset, 0.5, dataSource.getBins());
    }
}
