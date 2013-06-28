package uk.ac.ebi.pride.chart.dataset;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.text.DecimalFormat;
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

    public static XYSeriesCollection getXYDataset(PrideXYDataSource dataSource) {
        XYSeries series = getSeries(dataSource.filter(dataSource.getType()));

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        if (dataSource.isIncludeSubType()) {
            Collection<PrideDataType> subTypes = dataSource.getType().getChildren();
            for (PrideDataType subType : subTypes) {
                PrideXYDataSource filterDataSource = dataSource.filter(subType);
                if (filterDataSource != null) {
                    series = getSeries(filterDataSource);
                    dataset.addSeries(series);
                }
            }
        }

        return dataset;
    }

    public static CategoryDataset getHistogramDataset(PrideHistogramDataSource dataSource) {
        Map<PrideHistogramBin, Collection<PrideData>> histogram = dataSource.getHistogram();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String category;
        String seriesKey;

        Iterator<PrideHistogramBin> it = dataSource.filter(dataSource.getType()).iterator();
        PrideHistogramBin bin;
        while (it.hasNext()) {
            bin = it.next();
            category = bin.getEndBoundary() == Integer.MAX_VALUE ? ">" + bin.getStartBoundary() : bin.toString(new DecimalFormat("#"));
            seriesKey = dataSource.getType().getTitle();
            dataset.addValue(histogram.get(bin).size(), seriesKey, category);
        }

        if (dataSource.isIncludeSubType()) {
            Collection<PrideDataType> subTypes = dataSource.getType().getChildren();
            for (PrideDataType subType : subTypes) {
                PrideHistogramDataSource subDataSource = dataSource.filter(subType);
                Map<PrideHistogramBin, Collection<PrideData>> subHistogram = subDataSource.getHistogram();

                Iterator<PrideHistogramBin> subIt = subDataSource.iterator();
                while (subIt.hasNext()) {
                    bin = subIt.next();
                    category = bin.getEndBoundary() == Integer.MAX_VALUE ? ">" + bin.getStartBoundary() : bin.toString(new DecimalFormat("#"));
                    seriesKey = subType.getTitle();
                    dataset.addValue(subHistogram.get(bin).size(), seriesKey, category);
                }
            }
        }

        return dataset;
    }
}
