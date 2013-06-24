package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.plot.label.CategoryPercentageLabel;

/**
* User: Qingwei
* Date: 14/06/13
*/
public class PeaksMSPlot extends PrideCategoryPlot {
    public PeaksMSPlot(CategoryDataset dataset) {
        this(dataset, true);
    }

    public PeaksMSPlot(CategoryDataset dataset, boolean smallPlot) {
        super(PrideChartType.PEAKS_MS, dataset, smallPlot);

        BarRenderer renderer = (BarRenderer) getRenderer();
        renderer.setBaseItemLabelGenerator(new CategoryPercentageLabel());
        renderer.setBaseItemLabelsVisible(true);
    }
}
