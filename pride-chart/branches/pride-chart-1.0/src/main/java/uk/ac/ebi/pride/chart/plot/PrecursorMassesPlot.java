package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import uk.ac.ebi.pride.chart.PrideChartType;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class PrecursorMassesPlot extends PrideXYPlot {
    public PrecursorMassesPlot(XYDataset dataset) {
        super(PrideChartType.PRECURSOR_MASSES, dataset, new XYSplineRenderer());

        setDomainUnitSize(500);
        setRangeUnitSize(0.025);

        XYSplineRenderer renderer = (XYSplineRenderer) getRenderer();
        for (int i = 0; i < getSeriesCount(); i++) {
            renderer.setSeriesShapesVisible(i, false);
        }
    }

    public void setDomainUnitSize(double domainUnitSize) {
        NumberAxis domainAxis = (NumberAxis) getDomainAxis();
        domainAxis.setTickUnit(new NumberTickUnit(domainUnitSize, new DecimalFormat("###,###")));
    }

    public void setRangeUnitSize(double rangeUnitSize) {
        NumberAxis rangeAxis = (NumberAxis) getRangeAxis();
        rangeAxis.setTickUnit(new NumberTickUnit(rangeUnitSize, new DecimalFormat("0.000")));
    }
}
