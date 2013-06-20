package uk.ac.ebi.pride.chart.dataset;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Qingwei
 * Date: 13/06/13
 */
public class PrideDatasetFactory {
    private PrideDatasetFactory() {}

    private static XYSeries getSeries(PrideXYDataSource dataSource) {
        XYSeries series = new XYSeries(dataSource.getType().toString());
        Double[] domainValues = dataSource.getDomainData();
        PrideData[] rangeValues = dataSource.getRangeData();

        for (int i = 0; i < domainValues.length; i++) {
            series.add(domainValues[i], rangeValues[i].getData());
        }

        return series;
    }

    public static XYSeriesCollection getXYDataset(PrideXYDataSource dataSource, boolean includeCompatibleSubType) {
        XYSeries series = getSeries(dataSource);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        if (includeCompatibleSubType) {
            Collection<PrideDataType> subTypes = dataSource.getType().getChildren();
            for (PrideDataType subType : subTypes) {
                series = getSeries(dataSource.filter(subType));
                dataset.addSeries(series);
            }
        }

        return dataset;
    }

    public static CategoryDataset getHistogramDataset(PrideHistogramDataSource dataSource, boolean includeCompatibleSubType) {
        Map<PrideHistogramBin, Integer> histogram = dataSource.getHistogram();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String category;
        String seriesKey;

        Iterator<PrideHistogramBin> it = dataSource.iterator();
        PrideHistogramBin bin;
        while (it.hasNext()) {
            bin = it.next();
            category = bin.getEndBoundary() == Integer.MAX_VALUE ? ">" + bin.getStartBoundary() : bin.toString();
            seriesKey = dataSource.getType().getTitle();
            dataset.addValue(histogram.get(bin), seriesKey, category);
        }

        if (includeCompatibleSubType) {
            Collection<PrideDataType> subTypes = dataSource.getType().getChildren();
            for (PrideDataType subType : subTypes) {
                PrideHistogramDataSource subDataSource = dataSource.filter(subType);
                Map<PrideHistogramBin, Integer> subHistogram = subDataSource.getHistogram();

                Iterator<PrideHistogramBin> subIt = subDataSource.iterator();
                while (subIt.hasNext()) {
                    bin = subIt.next();
                    category = bin.getEndBoundary() == Integer.MAX_VALUE ? ">" + bin.getStartBoundary() : bin.toString();
                    seriesKey = subType.getTitle();
                    dataset.addValue(subHistogram.get(bin), seriesKey, category);
                }
            }
        }

        return dataset;
    }
}
