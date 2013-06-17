package uk.ac.ebi.pride.chart.dataset;

import org.jfree.data.statistics.HistogramBin;

import java.util.*;

/**
 * Based on Histogram bins list {@link #bins}, system translate the {@link #values}
 * into a histogram. Every bin, the value should in [bin_lowBound, bin_highBound) range,
 * which means the low bound value is included and high bound value is excluded.
 *
 * Notice:
 * 1) If user not provide any histogram bin, default bin is (minValue, Integer.MAX_VALUE).
 * 2) If there exist overlap of these bins' boundary, then the statistic also exist overlap.
 * 3) The {@link #values} should be a non-negative double array.
 *
 * User can call {@link #getHistogram()} method to get a two dimension array. The first array record
 * domain values, which value from 1 to size of bin. The second array record the count of
 * this histogram bin.
 *
 * User: Qingwei
 * Date: 14/06/13
 */
public class PrideHistogramDataSource implements PrideDataSource {
    private double[] values;
    private PrideDataSourceType type;
    private List<HistogramBin> bins = new ArrayList<HistogramBin>();

    private double minValue;
    private double maxValue;

    public PrideHistogramDataSource(double[] data) {
        this(data, PrideDataSourceType.UNKNOWN);
    }

    public PrideHistogramDataSource(double[] values, PrideDataSourceType type) {
        if (values == null) {
            throw new NullPointerException("Input data is null!");
        }

        Arrays.sort(values);
        if (values[0] < 0) {
            throw new IllegalArgumentException("Only accept non-negative double array.");
        }

        this.values = values;
        this.minValue = values[0];
        this.maxValue = values[values.length - 1];
        this.type = type == null ? PrideDataSourceType.UNKNOWN : type;
    }

    public List<HistogramBin> getBins() {
        return bins;
    }

    public void addBin(HistogramBin bin) {
        if (bin.getEndBoundary() < minValue || bin.getStartBoundary() > maxValue) {
            throw new IllegalArgumentException("The bin's bound " + bin.getStartBoundary() + "-" +
                    bin.getEndBoundary() + " overflow the values range.");
        }

        this.bins.add(bin);
    }

    public void addAllBins(Collection<HistogramBin> bins) {
        for (HistogramBin bin : bins) {
            if (bin.getEndBoundary() < minValue || bin.getStartBoundary() > maxValue) {
                throw new IllegalArgumentException("The bin's bound " + bin.getStartBoundary() + "-" +
                        bin.getEndBoundary() + " overflow the values range.");
            }
        }
        this.bins.addAll(bins);
    }

    public void removeBins(double start, double end) {
        if (end <= start) {
            throw new IllegalArgumentException("Start value " + start + " should be less than end value " + end);
        }

        List<HistogramBin> newBins = new ArrayList<HistogramBin>();
        for (HistogramBin bin : bins) {
            if (bin.getStartBoundary() >= start && bin.getEndBoundary() < end) {
                continue;
            }
            newBins.add(bin);
        }

        this.bins = newBins;
    }

    public void clearBins() {
        bins.clear();
    }

    /**
     * Generate a serials of histogram bins, which have same bin width.
     * NOTICE: The high bound of the last histogram bin always Integer.MAX_VALUE.
     *
     * @param startZero is true, means system use 0 as the low bound of first histogram bin.
     *                  Otherwise, the lowest value of {@link #values} as the low bound.
     */
    public List<HistogramBin> generateBins(double binWidth, boolean startZero) {
        double minValue = startZero ? 0 : this.minValue;
        double maxValue = this.maxValue;
        double width = maxValue - minValue;

        int binCount = (int) (width / binWidth);
        if (width % binWidth != 0) {
            binCount++;
        }

        List<HistogramBin> newBins = new ArrayList<HistogramBin>();
        double low = startZero ? 0 : minValue;
        double high;
        for (int i = 0; i < binCount; i++) {
            high  = low + binWidth;
            if (high >= maxValue) {
                high = Integer.MAX_VALUE;
            }
            newBins.add(new HistogramBin(low, high));
            low = high;
        }

        return newBins;
    }

    public double[][] getHistogram() {
        Iterator<HistogramBin> it = bins.iterator();
        double[][] data = new double[2][bins.size()];

        HistogramBin bin;
        int offset = 0;
        int count;
        do {
            bin = bins.isEmpty() ? new HistogramBin(minValue, Integer.MAX_VALUE) : it.next();
            count = 0;
            for (double v : values) {
                if (v >= bin.getStartBoundary() && v < bin.getEndBoundary()) {
                    count++;
                }
            }
            data[0][offset] = offset + 1;
            data[1][offset] = count;
            offset++;
        } while (it.hasNext());

        return data;
    }

    public PrideDataSourceType getType() {
        return type;
    }
}
