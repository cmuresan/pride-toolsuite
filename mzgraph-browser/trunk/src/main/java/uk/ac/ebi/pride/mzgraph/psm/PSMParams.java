package uk.ac.ebi.pride.mzgraph.psm;

import uk.ac.ebi.pride.mzgraph.chart.graph.MzGraphConstants;

/**
 * Creator: Qingwei-XU
 * Date: 13/11/12
 */

public class PSMParams {
    private static PSMParams params;

    /**
     * default psm range interval is [-0.5Da, 0.5Da]
     */
    private double range = MzGraphConstants.INTERVAL_RANGE;

    private PSMParams() {
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        if (Double.compare(range, 0) == 1) {
            this.range = range;
        } else {
            throw new IllegalArgumentException(range + " should great than 0!");
        }
    }

    public static synchronized PSMParams getInstance() {
        if (params == null) {
            params = new PSMParams();
        }

        return params;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("PSMParams is singleton, not support clone!");
    }
}
