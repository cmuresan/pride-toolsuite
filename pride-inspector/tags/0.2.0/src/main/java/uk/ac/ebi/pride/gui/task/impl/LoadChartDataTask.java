package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.chart.graphics.implementation.*;
import uk.ac.ebi.pride.chart.model.implementation.*;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessControllerType;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Class to load the charts data in background.</p>
 *
 * @author Antonio Fabregat
 *         Date: 26-ago-2010
 *         Time: 11:59:12
 */
public class LoadChartDataTask extends TaskAdapter<List<PrideChart>, String> {
    private static final int NUMBER_OF_CHARTS = 6;

    private DataAccessController controller;

    public LoadChartDataTask(DataAccessController controller) {
        this.controller = controller;
        this.setName("Loading chart data");
        this.setDescription("Loading chart data");
    }

    @Override
    protected List<PrideChart> doInBackground() throws Exception {
        SpectralDataPerExperiment spectral = null;
        try {
            if (DataAccessControllerType.DATABASE.equals(controller.getType())) {
                String accessionNumber = controller.getForegroundExperimentAcc().toString();
                Connection connection = (Connection) controller.getSource();
                spectral = new PrideChartSpectralData(accessionNumber, connection);
            } else {
                spectral = new PrideViewerSpectralData(controller);
            }
        } catch (SpectralDataPerExperimentException e) {
            System.err.println(e);
        }


        int i = 1;

        //The list is reset every time the experiment accession is changed
        List<PrideChart> prideCharts = new ArrayList<PrideChart>();

        // Chart 1
        publishChartSequence(i++);
        prideCharts.add(new IntensityHistogramChart(spectral));

        // Chart 2
        publishChartSequence(i++);
        prideCharts.add(new FreqPrecIonChargeChart(spectral));

        // Chart 3
        publishChartSequence(i++);
        prideCharts.add(new MZHistogramChart(spectral));

        // Chart 4
        publishChartSequence(i++);
        URL url = ClassLoader.getSystemResource("out.fasta");
        try {
            prideCharts.add(new PreMSvsTheMSChart(spectral, url.getFile()));
        } catch (MSDataArrayException e) {
            System.err.println(e);
        }

        // Chart 5
        publishChartSequence(i++);
        prideCharts.add(new MassDeltaHistogramChart(spectral));

        // Chart 6
        publishChartSequence(i++);
        prideCharts.add(new ProteinsPeptidesChart(spectral));

        return prideCharts;
    }

    void publishChartSequence(int i) {
        publish("Creating the charts (1/" + NUMBER_OF_CHARTS + ")");
    }
}
