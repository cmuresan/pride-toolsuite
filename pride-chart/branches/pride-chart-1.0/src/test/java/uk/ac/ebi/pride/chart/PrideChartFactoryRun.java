package uk.ac.ebi.pride.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import uk.ac.ebi.pride.chart.io.DataAccessReader;
import uk.ac.ebi.pride.chart.io.ElderJSONReader;
import uk.ac.ebi.pride.chart.io.PrideDataException;
import uk.ac.ebi.pride.chart.io.PrideDataReader;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzMLControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;

import java.awt.*;
import java.io.File;

/**
 * User: qingwei
 * Date: 21/06/13
 */
public class PrideChartFactoryRun {
    private void drawChart(PrideDataReader reader, PrideChartType chartType) {
        JFreeChart chart = PrideChartFactory.getChart(reader, chartType);

        if (chart == null) {
            return;
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        ApplicationFrame mainFrame = new ApplicationFrame("test");
        mainFrame.setContentPane(chartPanel);

        mainFrame.pack();
        RefineryUtilities.centerFrameOnScreen(mainFrame);
        mainFrame.setVisible(true);
    }

    public void drawChartList(PrideDataReader reader, PrideChartSummary summary) throws Exception {
        for (PrideChartType chartType : summary.getAll()) {
            drawChart(reader, chartType);
        }
    }

    public static void main(String[] args) throws Exception {
        PrideChartFactoryRun run = new PrideChartFactoryRun();

//        File jsonFile = new File("testset/old_2.json");
//        File jsonFile = new File("testset/old_1643.json");
//        File jsonFile = new File("testset/old_10.json");
//        run.drawChartList(new ElderJSONReader(jsonFile), PrideChartSummary.PROJECT_SUMMARY);

//        File prideXMLFile = new File("testset/PRIDE_Exp_Complete_Ac_2.xml");
//        run.drawChartList(new DataAccessReader(new PrideXmlControllerImpl(prideXMLFile)), PrideChartSummary.PROJECT_SUMMARY);

        File mzMLFile = new File("testset/mzml-example.mzML");
        run.drawChartList(new DataAccessReader(new MzMLControllerImpl(mzMLFile)), PrideChartSummary.PROJECT_SUMMARY);
    }
}
