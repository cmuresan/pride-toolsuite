package uk.ac.ebi.pride.chart.dataset;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public class PrideXYDataSource implements PrideDataSource {
    private double[][] data;
    private PrideDataSourceType type;

    public PrideXYDataSource(double[][] data) {
        this(data, PrideDataSourceType.UNKNOWN);
    }

    public PrideXYDataSource(double[][] data, PrideDataSourceType type) {
        if (data.length != 2 || data[0].length != data[1].length) {
            throw new IllegalArgumentException("Input data not correct!");
        }

        this.data = data;
        this.type = type == null ? PrideDataSourceType.UNKNOWN : type;
    }

    public double[] getDomainData() {
        return data[0];
    }

    public double[] getRangeData() {
        return data[1];
    }

    public PrideDataSourceType getType() {
        return type;
    }
}
