package uk.ac.ebi.pride.chart.plot.axis;

import org.jfree.chart.axis.TickUnit;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class PrideHistogramTickUnit extends TickUnit {
    public PrideHistogramTickUnit(double size) {
        super(size);
    }

    public PrideHistogramTickUnit(double size, int minorTickCount) {
        super(size, minorTickCount);
    }


}
