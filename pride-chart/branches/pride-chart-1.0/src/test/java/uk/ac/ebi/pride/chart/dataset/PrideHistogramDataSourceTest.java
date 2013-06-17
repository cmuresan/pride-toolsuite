package uk.ac.ebi.pride.chart.dataset;

import org.jfree.data.statistics.HistogramBin;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class PrideHistogramDataSourceTest {
    private double[] generateXData(double minX, double maxX, int count) {
        double[] data = new double[count];

        int step = (int)((maxX - minX) * 1000) / count;
        if (step == 0) {
            if (minX < 0) {
                step = -1;
            } else {
                step = 1;
            }
        }

        double x;
        for (int i = 0; i < count; i++) {
            x = minX + step * i / 1000d;
            data[i] = x;
        }

        return data;
    }

    private double getSum(double[] data) {
        double sum = 0;

        for (double d : data) {
            sum += d;
        }

        return sum;
    }

    @Test
    public void testEqualHistogram() throws Exception {
        double[] value;
        double[][] data;

        int count = 10000;
        value = generateXData(20, 2000, count);
        PrideHistogramDataSource dataSource = new PrideHistogramDataSource(value);

        List<HistogramBin> bins = dataSource.generateBins(50, true);
        dataSource.addAllBins(bins);
        data = dataSource.getHistogram();
        assertTrue(getSum(data[1]) == count);

        dataSource.clearBins();
        bins = dataSource.generateBins(50, false);
        dataSource.addAllBins(bins);
        data = dataSource.getHistogram();
        assertTrue(getSum(data[1]) == count);
    }

    @Test
    public void testBinHistogram() throws Exception {
        double[] value;
        double[][] data;

        int count = 20;

        value = generateXData(20, 2000, count);
        PrideHistogramDataSource dataSource = new PrideHistogramDataSource(value);
        dataSource.addBin(new HistogramBin(20, 200));
        dataSource.addBin(new HistogramBin(200, 1000));
        dataSource.addBin(new HistogramBin(1000, 2001));

        data = dataSource.getHistogram();
        assertTrue(getSum(data[1]) == count);
    }
}
