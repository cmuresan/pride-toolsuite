package uk.ac.ebi.pride.chart.dataset;

import java.util.*;

/**
* Based on Histogram bins list {@link #bins}, system translate the {@link #values}
* into a histogram. Every bin, the value should in [bin_lowBound, bin_highBound) range,
* which means the low bound value is included and high bound value is excluded.
*
* Notice:
* NOT exists overlap between the different histogram bins.
*
* User: Qingwei
* Date: 14/06/13
*/
public class PrideHistogramDataSource {
    protected SortedSet<PrideHistogramBin> bins = new TreeSet<PrideHistogramBin>();

    protected PrideData[] values;
    protected PrideDataType type;

    public PrideHistogramDataSource(PrideData[] values, PrideDataType type) {
        for (PrideData value : values) {
            if (! value.getType().compatible(type)) {
                throw new IllegalArgumentException("There exists incompatible value " + value + " in range array!");
            }
        }

        this.values = values;
        this.type = type;
    }

    public Iterator<PrideHistogramBin> iterator() {
        return bins.iterator();
    }

    public PrideHistogramDataSource filter(PrideDataType type) {
        List<PrideData> filterData = new ArrayList<PrideData>();

        for (PrideData value : values) {
            if (value.getType().compatible(type)) {
                filterData.add(value);
            }
        }

        int size = filterData.size();
        PrideHistogramDataSource subDataSource = new PrideHistogramDataSource(
                filterData.toArray(new PrideData[size]),
                type
        );
        subDataSource.bins = this.bins;

        return subDataSource;
    }

    /**
     * add a bin to the end of set.
     */
    public void appendBin(PrideHistogramBin bin) {
        PrideHistogramBin lastBin = bins.isEmpty() ? null : bins.last();

        if (lastBin != null && bin.getStartBoundary() < lastBin.getEndBoundary()) {
            throw new IllegalArgumentException("There exists overlap between the last bin " + lastBin +
                    " and the new append bin " + bin);
        }

        this.bins.add(bin);
    }

    public void removeBins(double lowerBound, double upperBound) {
        if (upperBound <= lowerBound) {
            throw new IllegalArgumentException("the upperBound <= lowerBound");
        }

        SortedSet<PrideHistogramBin> newBins = new TreeSet<PrideHistogramBin>();
        for (PrideHistogramBin bin : bins) {
            if (bin.getStartBoundary() >= lowerBound && bin.getEndBoundary() < upperBound) {
                continue;
            }
            newBins.add(bin);
        }

        this.bins = newBins;
    }

    public void clearBins() {
        bins.clear();
    }

    public PrideHistogramBin getFirstBin() {
        return bins.first();
    }

    public int getStart() {
        return getFirstBin().getStartBoundary();
    }

    public PrideHistogramBin getLastBin() {
        return bins.last();
    }

    public int getEnd() {
        return getLastBin().getEndBoundary();
    }

    public SortedMap<PrideHistogramBin, Integer> getHistogram() {
        if (values == null) {
            throw new NullPointerException("Input data is null!");
        }

        // initial map.
        SortedMap<PrideHistogramBin, Integer> histogram = new TreeMap<PrideHistogramBin, Integer>();
        for (PrideHistogramBin bin : bins) {
            histogram.put(bin, 0);
        }

        // calculate count.
        Integer count;
        Set<PrideHistogramBin> keySet = histogram.keySet();
        for (PrideData d : values) {
            for (PrideHistogramBin bin : keySet) {
                if (d.getData() >= bin.getStartBoundary() && d.getData() < bin.getEndBoundary()) {
                    count = histogram.get(bin);
                    count = count + 1;
                    histogram.put(bin, count);
                    break;
                }
            }
        }

        return histogram;
    }

    public PrideDataType getType() {
        return type;
    }

    public PrideData[] getValues() {
        return values;
    }
}
