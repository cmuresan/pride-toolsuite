package uk.ac.ebi.pride.chart.summary;

import org.jfree.data.xy.XYBarDataset;
import uk.ac.ebi.pride.chart.dataset.PridePlotDatasetFactory;
import uk.ac.ebi.pride.chart.dataset.XYDataSource;
import uk.ac.ebi.pride.chart.io.PrideDataReader;
import uk.ac.ebi.pride.chart.plot.DeltaMZPlot;
import uk.ac.ebi.pride.chart.plot.PridePlot;

import static uk.ac.ebi.pride.chart.PrideChartType.DELTA_MASS;

/**
 * User: Qingwei
 * Date: 11/06/13
 */
public class SpectrumSummary extends PridePlotSummary {
    public SpectrumSummary(PrideDataReader reader) {
        super(reader);
        init();
    }

    private void init() {
        getDeltaMZPlot();
    }

    public PridePlot getDeltaMZPlot() {
        if (contains(DELTA_MASS)) {
            return getPlot(DELTA_MASS);
        }

        XYDataSource dataSource = new XYDataSource(reader.readDeltaMZ());
        XYBarDataset dataset = PridePlotDatasetFactory.getXYBarDataset("Peptides Number", dataSource);
        PridePlot plot = new DeltaMZPlot(dataset);
        addPlot(plot);

        return plot;
    }
}
