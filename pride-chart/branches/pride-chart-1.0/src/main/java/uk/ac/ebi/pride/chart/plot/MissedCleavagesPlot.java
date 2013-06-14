package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYBarDataset;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.plot.axis.PrideNumberTickUnit;
import uk.ac.ebi.pride.chart.plot.label.PercentageLabel;

import java.text.DecimalFormat;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class MissedCleavagesPlot extends PrideXYPlot {
    public MissedCleavagesPlot(XYBarDataset dataset) {
        super(PrideChartType.MISSED_CLEAVAGES, dataset, new XYBarRenderer());

        setDomainZeroBaselineVisible(false);

        XYBarRenderer renderer = (XYBarRenderer) getRenderer();
        renderer.setBaseItemLabelGenerator(new PercentageLabel());
        renderer.setBaseItemLabelsVisible(true);

        NumberAxis domainAxis = (NumberAxis) getDomainAxis();
        PrideNumberTickUnit unit = new PrideNumberTickUnit(1, new DecimalFormat("0"));
        int barCount = dataset.getItemCount(0);
        unit.setMaxValue(barCount - 2);
        domainAxis.setTickUnit(unit);

        NumberAxis rangeAxis = (NumberAxis) getRangeAxis();
        rangeAxis.setMinorTickCount(barCount);
    }
}
