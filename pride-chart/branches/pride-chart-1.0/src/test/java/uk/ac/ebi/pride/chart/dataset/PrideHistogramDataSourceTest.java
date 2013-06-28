package uk.ac.ebi.pride.chart.dataset;

import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class PrideHistogramDataSourceTest {
    private PrideData[] generateXData(double minX, double maxX, int count) {
        PrideData[] data = new PrideData[count];

        int step = (int)((maxX - minX) * 1000) / count;
        if (step == 0) {
            if (minX < 0) {
                step = -1;
            } else {
                step = 1;
            }
        }

        double x;
        PrideDataType type;
        for (int i = 0; i < count; i++) {
            x = minX + step * i / 1000d;
            type = (int)x % 2 == 0 ? PrideDataType.IDENTIFIED_SPECTRA : PrideDataType.UNIDENTIFIED_SPECTRA;
            data[i] = new PrideData(x, type);
        }

        return data;
    }

    private int getSum(Collection<Collection<PrideData>> data) {
        int sum = 0;

        for (Collection<PrideData> cell : data) {
            sum += cell.size();
        }

        return sum;
    }

    @Test
    public void testEqualHistogram() throws Exception {
        PrideData[] value;

        int count = 100;
        value = generateXData(20, 200, count);
        PrideHistogramDataSource dataSource = new PrideEqualWidthHistogramDataSource(value, PrideDataType.ALL_SPECTRA);
        dataSource.appendBins(((PrideEqualWidthHistogramDataSource)dataSource).generateBins(0, 50, 4));

        Map<PrideHistogramBin, Collection<PrideData>> histogram = dataSource.getHistogram();
        assertTrue(histogram.keySet().size() == 4);
        assertEquals(getSum(histogram.values()), count);
    }

    @Test
    public void testBinHistogram() throws Exception {
        PrideData[] values;

        int count = 20;
        values = generateXData(20, 2000, count);

        PrideHistogramDataSource dataSource = new PrideHistogramDataSource(values, PrideDataType.ALL_SPECTRA);
        dataSource.appendBin(new PrideHistogramBin(20, 200));
        dataSource.appendBin(new PrideHistogramBin(200, 1000));
        dataSource.appendBin(new PrideHistogramBin(1000, Integer.MAX_VALUE));

        Map<PrideHistogramBin, Collection<PrideData>> histogram = dataSource.getHistogram();
        assertEquals(getSum(histogram.values()), count);
    }
}
