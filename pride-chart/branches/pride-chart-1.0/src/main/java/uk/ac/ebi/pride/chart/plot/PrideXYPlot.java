package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import uk.ac.ebi.pride.chart.PrideChartType;

import java.awt.*;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public abstract class PrideXYPlot extends XYPlot implements PridePlot {
    private PrideChartType type;

    protected PrideXYPlot(PrideChartType type, XYDataset dataset, XYItemRenderer renderer) {
        super(dataset, new NumberAxis(type.getXLabel()), new NumberAxis(type.getYLabel()), renderer);
        this.type = type;

        setOrientation(PlotOrientation.VERTICAL);
        setDomainZeroBaselineVisible(true);
        setBackgroundAlpha(0f);
        setDomainGridlinePaint(Color.red);
        setRangeGridlinePaint(Color.blue);
        getRangeAxis().setUpperMargin(0.15);
    }

    @Override
    public PrideChartType getType() {
        return type;
    }
}
