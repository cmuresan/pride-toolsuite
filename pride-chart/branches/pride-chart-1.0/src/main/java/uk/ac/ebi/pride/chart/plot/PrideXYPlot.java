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
public class PrideXYPlot extends XYPlot implements PridePlot {
    private PrideChartType type;

    protected PrideXYPlot(PrideChartType type, XYDataset dataset, XYItemRenderer renderer) {
        super(dataset, new NumberAxis(type.getDomainLabel()), new NumberAxis(type.getRangeLabel()), renderer);
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

    @Override
    public String getTitle() {
        return type.getTitle();
    }

    @Override
    public String getFullTitle() {
        return type.getFullTitle();
    }

    @Override
    public String getDomainLabel() {
        return type.getDomainLabel();
    }

    @Override
    public String getRangeLabel() {
        return type.getRangeLabel();
    }

    @Override
    public boolean isLegend() {
        return type.isLegend();
    }
}
