package uk.ac.ebi.pride.chart.dataset;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public class XYDataSource implements PridePlotDataSource<double[][]> {
    private double[][] data;

    public XYDataSource(double[][] data) {
        this.data = data;
    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }
}
