package uk.ac.ebi.pride.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import uk.ac.ebi.pride.chart.io.ElderJSONReader;
import uk.ac.ebi.pride.chart.io.PrideDataException;
import uk.ac.ebi.pride.chart.io.PrideDataReader;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * User: qingwei
 * Date: 21/06/13
 */
public class PrideChartFactoryRun {
    private void drawChart(ElderJSONReader reader, PrideChartType chartType) {
        JFreeChart chart;
        try {
            chart = PrideChartFactory.getChart(reader, chartType);
        } catch (PrideDataException e) {
            System.err.println(e.getMessage());
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

    public void drawChartList(File jsonFile) throws Exception {
        ElderJSONReader jsonReader = new ElderJSONReader(jsonFile);

        for (PrideChartType chartType : jsonReader.getChartTypeList()) {
            drawChart(jsonReader, chartType);
        }
    }

    public static void main(String[] args) throws Exception {
        PrideChartFactoryRun run = new PrideChartFactoryRun();

//        File jsonFile = new File("testset/old_2.json");
        File jsonFile = new File("testset/old_1643.json");
//        File jsonFile = new File("testset/old_10.json");

        run.drawChartList(jsonFile);
    }
}
