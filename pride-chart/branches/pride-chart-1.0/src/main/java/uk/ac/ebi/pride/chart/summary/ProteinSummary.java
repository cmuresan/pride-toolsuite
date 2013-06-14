package uk.ac.ebi.pride.chart.summary;

import org.jfree.data.xy.XYBarDataset;
import uk.ac.ebi.pride.chart.dataset.PrideDatasetFactory;
import uk.ac.ebi.pride.chart.dataset.PrideXYDataSource;
import uk.ac.ebi.pride.chart.io.PrideDataReader;
import uk.ac.ebi.pride.chart.plot.PeptidesProteinPlot;
import uk.ac.ebi.pride.chart.plot.PridePlot;

import static uk.ac.ebi.pride.chart.PrideChartType.PEPTIDES_PROTEIN;

/**
 * User: Qingwei
 * Date: 11/06/13
 */
public class ProteinSummary extends PridePlotSummary {
    public ProteinSummary(PrideDataReader reader) {
        super(reader);
        init();
    }

    private void init() {
        getPeptidesProteinPlot();
    }

    public PridePlot getPeptidesProteinPlot() {
        if (contains(PEPTIDES_PROTEIN)) {
            return getPlot(PEPTIDES_PROTEIN);
        }

        PrideXYDataSource dataSource = new PrideXYDataSource(reader.readPeptidesProtein());
        XYBarDataset dataset = PrideDatasetFactory.getXYBarDataset(dataSource);
        PridePlot plot = new PeptidesProteinPlot(dataset);
        addPlot(plot);

        return plot;
    }
}
