package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.dataset.HistogramBarDataset;
import uk.ac.ebi.pride.chart.plot.axis.PrideHistogramTickUnit;
import uk.ac.ebi.pride.chart.plot.label.PercentageLabel;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class PeakIntensityPlot extends PrideXYPlot {
    public PeakIntensityPlot(HistogramBarDataset dataset) {
        super(PrideChartType.PEAK_INTENSITY, dataset, new XYBarRenderer());

        XYBarRenderer renderer = (XYBarRenderer) getRenderer();
        renderer.setBaseItemLabelGenerator(new PercentageLabel());
        renderer.setBaseItemLabelsVisible(true);

        NumberAxis domainAxis = (NumberAxis) getDomainAxis();
        domainAxis.setTickUnit(new PrideHistogramTickUnit(dataset.getBins()));
    }
}
