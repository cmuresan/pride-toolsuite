package uk.ac.ebi.pride.chart.dataset;

import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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

    public static PrideHistogramDataset getHistogramDataset(PrideHistogramDataSource dataSource, double binWidth) {
        double[] domainValues = dataSource.getDomainData();
        PrideHistogramDataset dataset = new PrideHistogramDataset(binWidth);
        dataset.addSeries(dataSource.getType().toString(), domainValues);

        return dataset;
    }
}
