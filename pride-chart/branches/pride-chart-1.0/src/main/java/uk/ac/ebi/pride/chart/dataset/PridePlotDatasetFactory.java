package uk.ac.ebi.pride.chart.dataset;

import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * User: Qingwei
 * Date: 13/06/13
 */
public class PridePlotDatasetFactory {
    private PridePlotDatasetFactory() {}

    public static XYBarDataset getXYBarDataset(String serialKey, XYDataSource dataSource) {
        XYSeries series = new XYSeries(serialKey);
        double[][] data = dataSource.getData();
        for (int i = 0; i < data[0].length; i++) {
            series.add(data[0][i], data[1][i]);
        }

        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series);
        return new XYBarDataset(collection, 0.5);
    }

    public static XYDataset getDeltaMZDataset(String serialKey, XYDataSource dataSource) {
        XYSeries series = new XYSeries(serialKey);
        double[][] data = dataSource.getData();
        for (int i = 0; i < data[0].length; i++) {
            series.add(data[0][i], data[1][i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }
}
