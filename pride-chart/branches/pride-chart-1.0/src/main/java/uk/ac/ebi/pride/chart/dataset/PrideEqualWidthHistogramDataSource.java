package uk.ac.ebi.pride.chart.dataset;

/**
 * User: qingwei
 * Date: 20/06/13
 */
public class PrideEqualWidthHistogramDataSource extends PrideHistogramDataSource {
    private int binWidth;

    public PrideEqualWidthHistogramDataSource(PrideData[] values, PrideDataType type, int start, int binWidth, int count) {
        super(values, type);

        if (binWidth <= 0) {
            throw new IllegalArgumentException("Bin width should be great than 0");
        }

        this.binWidth = binWidth;

        int lowerBound = start;
        int higherBound;
        for (int i = 0; i < count; i++) {
            higherBound = lowerBound + binWidth;
            bins.add(new PrideHistogramBin(lowerBound, higherBound));
            lowerBound = higherBound;
        }
    }

    public int getBinWidth() {
        return binWidth;
    }

    public void appendBin(PrideHistogramBin bin) {
        if (bin.getBinWidth() != binWidth) {
            throw new IllegalArgumentException("the bin width not be same with datasource's bin width.");
        }

        super.appendBin(bin);
    }
}
