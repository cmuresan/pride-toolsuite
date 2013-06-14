package uk.ac.ebi.pride.chart.dataset;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class PrideHistogramDataSource implements PrideDataSource {
    private double[] data;
    private PrideDataSourceType type;

    public PrideHistogramDataSource(double[] data) {
        this(data, PrideDataSourceType.UNKNOWN);
    }

    public PrideHistogramDataSource(double[] data, PrideDataSourceType type) {
        if (data == null) {
            throw new IllegalArgumentException("Input data not correct!");
        }

        this.data = data;
        this.type = type == null ? PrideDataSourceType.UNKNOWN : type;
    }

    public double[] getDomainData() {
        return data;
    }

    public PrideDataSourceType getType() {
        return type;
    }
}
