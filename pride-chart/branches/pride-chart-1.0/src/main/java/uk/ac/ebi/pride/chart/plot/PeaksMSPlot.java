package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.dataset.PrideHistogramDataset;
import uk.ac.ebi.pride.chart.plot.label.PercentageLabel;

import java.text.DecimalFormat;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class PeaksMSPlot extends PrideXYPlot {
    public PeaksMSPlot(PrideHistogramDataset dataset) {
        super(PrideChartType.PEAKS_MS, dataset, new XYBarRenderer());

        XYBarRenderer renderer = (XYBarRenderer) getRenderer();
        renderer.setBaseItemLabelGenerator(new PercentageLabel());
        renderer.setBaseItemLabelsVisible(true);

        renderer.setMargin(0.7);
    }
}
