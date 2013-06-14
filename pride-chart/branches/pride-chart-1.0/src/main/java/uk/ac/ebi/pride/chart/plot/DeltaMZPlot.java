package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import uk.ac.ebi.pride.chart.PrideChartType;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public class DeltaMZPlot extends PrideXYPlot {
    public DeltaMZPlot(XYDataset dataset) {
        this(dataset, new XYLineAndShapeRenderer(true, false));
    }

    public DeltaMZPlot(XYDataset dataset, XYItemRenderer renderer) {
        super(PrideChartType.DELTA_MASS, dataset, renderer);

        setDomainZeroBaselineVisible(true);
        setBackgroundAlpha(0f);
        setDomainGridlinePaint(Color.red);
        setRangeGridlinePaint(Color.blue);
        setDomainGridlinesVisible(true);
        setRangeGridlinesVisible(true);

        NumberAxis domainAxis = (NumberAxis) getDomainAxis();
        domainAxis.setAutoTickUnitSelection(false);
        domainAxis.setTickUnit(new NumberTickUnit(0.05, new DecimalFormat("0.00")));
        domainAxis.setAutoRange(false);
        domainAxis.setRange(new Range(-0.16, 0.16));

        NumberAxis rangeAxis = (NumberAxis) getRangeAxis();
        rangeAxis.setAutoTickUnitSelection(false);
        rangeAxis.setTickUnit(new NumberTickUnit(0.25, new DecimalFormat("0.00")));
    }
}
