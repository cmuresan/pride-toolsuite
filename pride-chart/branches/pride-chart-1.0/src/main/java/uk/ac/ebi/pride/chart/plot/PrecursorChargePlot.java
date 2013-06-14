package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYBarDataset;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.plot.axis.PrideNumberTickUnit;
import uk.ac.ebi.pride.chart.plot.label.NumberLabel;
import uk.ac.ebi.pride.chart.plot.label.PercentageLabel;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class PrecursorChargePlot extends PrideXYPlot {
    public PrecursorChargePlot(XYBarDataset dataset) {
        super(PrideChartType.PRECURSOR_CHARGE, dataset, new XYBarRenderer());

        XYBarRenderer renderer = (XYBarRenderer) getRenderer();
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelGenerator(new NumberLabel());

        NumberAxis domainAxis = (NumberAxis) getDomainAxis();
        PrideNumberTickUnit unit = new PrideNumberTickUnit(1, new DecimalFormat("0"));
        int barCount = dataset.getItemCount(0);
        unit.setMaxValue(barCount - 1);
        domainAxis.setTickUnit(unit);

        NumberAxis rangeAxis = (NumberAxis) getRangeAxis();
        rangeAxis.setMinorTickCount(barCount);
    }
}
