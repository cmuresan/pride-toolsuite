package uk.ac.ebi.pride.mzgraph.chart.tooltip;

import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

/**
 * Creator: Qingwei-XU
 * Date: 15/10/12
 * Version: 0.1-SNAPSHOT
 */

public class ExperimentalFragmentedIonsScatterChartTooltipGenerator extends StandardXYToolTipGenerator {
    private int fraction;

    public ExperimentalFragmentedIonsScatterChartTooltipGenerator(int fraction) {
        this.fraction = fraction;
    }

    public String generateToolTip(XYDataset xyDataset, int series, int item) {
//        ProductIon[][] xValue = ((ExperimentalFragmentedIonsDataset) xyDataset).getxValues();
//        double diff = xyDataset.getYValue(series, item);
//        NumberFormat formatter = NumberFormat.getInstance();
//        formatter.setMaximumFractionDigits(fraction);
//
//        ProductIon productIon = xValue[series][item];

        StringBuilder sb = new StringBuilder();
//        sb.append(productIon.getName() + ": (");
//        sb.append("m/z:" + formatter.format(productIon.getMassOverCharge()) + ", ");
//        sb.append("Error:" + formatter.format(diff) + ")");

        return sb.toString();
    }
}
