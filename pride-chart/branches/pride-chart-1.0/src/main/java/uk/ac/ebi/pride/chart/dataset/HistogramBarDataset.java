package uk.ac.ebi.pride.chart.dataset;

import org.jfree.data.statistics.HistogramBin;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Qingwei
 * Date: 16/06/13
 */
public class HistogramBarDataset extends XYBarDataset {
    private List<HistogramBin> bins;

    /**
     * Creates a new dataset.
     *
     * @param underlying the underlying dataset (<code>null</code> not
     *                   permitted).
     * @param barWidth   the width of the bars.
     */
    public HistogramBarDataset(XYDataset underlying, double barWidth, List<HistogramBin> bins) {
        super(underlying, barWidth);

        if (bins == null) {
            bins = new ArrayList<HistogramBin>();
        }
        this.bins = bins;
    }

    public List<HistogramBin> getBins() {
        return bins;
    }
}
