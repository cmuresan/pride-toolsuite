package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYBarDataset;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.plot.axis.PrideIntegerTickUnit;
import uk.ac.ebi.pride.chart.plot.label.PercentageLabel;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public class PeptidesProteinPlot extends PrideXYPlot {
    public PeptidesProteinPlot(XYBarDataset dataset) {
        this(dataset, new XYBarRenderer());
    }

    public PeptidesProteinPlot(XYBarDataset dataset, XYItemRenderer renderer) {
        super(PrideChartType.PEPTIDES_PROTEIN, dataset, renderer);
        renderer.setBaseItemLabelGenerator(new PercentageLabel());
        renderer.setBaseItemLabelsVisible(true);

        NumberAxis domainAxis = (NumberAxis) getDomainAxis();
        domainAxis.setAutoTickUnitSelection(false);
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        PrideIntegerTickUnit newUnit = new PrideIntegerTickUnit(domainAxis.getTickUnit());
        newUnit.setMaxValue(dataset.getItemCount(0) - 1);
        domainAxis.setTickUnit(newUnit);
    }
}
