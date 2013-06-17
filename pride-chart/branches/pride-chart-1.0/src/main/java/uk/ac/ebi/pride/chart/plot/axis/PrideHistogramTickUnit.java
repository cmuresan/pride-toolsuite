package uk.ac.ebi.pride.chart.plot.axis;

import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.data.statistics.HistogramBin;

import java.text.DecimalFormat;
import java.util.List;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class PrideHistogramTickUnit extends NumberTickUnit {
    private List<HistogramBin> bins;

    public PrideHistogramTickUnit(List<HistogramBin> bins) {
        super(1);
        this.bins = bins;
    }

    @Override
    public String valueToString(double value) {
        int item = (int) value;

        if (item == 0) {
            return "0";
        }

        if (item == bins.size()) {
            return ">" + bins.get(item - 2).getEndBoundary();
        }

        HistogramBin bin = bins.get(item - 1);
        DecimalFormat format = new DecimalFormat("#,###,###");

        return  format.format(bin.getStartBoundary()) + "-" + format.format(bin.getEndBoundary());
    }


}
