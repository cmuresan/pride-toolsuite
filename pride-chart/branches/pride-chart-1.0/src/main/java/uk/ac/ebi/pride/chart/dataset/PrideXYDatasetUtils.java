package uk.ac.ebi.pride.chart.dataset;

import org.jfree.data.xy.XYDataset;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class PrideXYDatasetUtils {
    private double maxDomainValue = Double.MIN_VALUE;
    private double minDomainValue = Double.MAX_VALUE;
    private double maxRangeValue = Double.MIN_VALUE;
    private double minRangeValue = Double.MAX_VALUE;

    public PrideXYDatasetUtils(XYDataset dataset) {
        int series = 0;

        for (int i = 0; i < dataset.getItemCount(series); i++) {
            double x = dataset.getXValue(series, i);
            if (x < minDomainValue) {
                minDomainValue = x;
            }
            if (x > maxDomainValue) {
                maxDomainValue = x;
            }
        }

        for (int i = 0; i < dataset.getItemCount(series); i++) {
            double y = dataset.getYValue(series, i);
            if (y < minRangeValue) {
                minRangeValue = y;
            }
            if (y > maxRangeValue) {
                maxRangeValue = y;
            }
        }
    }

    public double getMaxDomainValue() {
        return maxDomainValue;
    }

    public double getMinDomainValue() {
        return minDomainValue;
    }

    public double getWidth() {
        return maxDomainValue - minDomainValue;
    }

    public double getMaxRangeValue() {
        return maxRangeValue;
    }

    public double getMinRangeValue() {
        return minRangeValue;
    }

    public double getHigh() {
        return maxRangeValue - minRangeValue;
    }
}
