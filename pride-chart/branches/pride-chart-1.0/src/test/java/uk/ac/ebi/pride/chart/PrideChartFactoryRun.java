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
    private void drawChart(String jsonString, PrideChartType chartType) {
        PrideDataReader reader;
        JFreeChart chart;
        try {
            reader = new ElderJSONReader(jsonString, chartType);
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

    private void drawChartList(File jsonFile) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
        String line;
        String[] items;
        int id;
        while ((line = reader.readLine()) != null) {
            items = line.split(", ");
            id = Integer.parseInt(items[0]);
            switch (id) {
                case 1:
                    drawChart(items[1], PrideChartType.PEAK_INTENSITY);
                    break;
                case 2:
                    drawChart(items[1], PrideChartType.PRECURSOR_CHARGE);
                    break;
                case 3:
                    drawChart(items[1], PrideChartType.AVERAGE_MS);
                    break;
                case 4:
                    drawChart(items[1], PrideChartType.PRECURSOR_MASSES);
                    break;
                case 5:
                    drawChart(items[1], PrideChartType.PEPTIDES_PROTEIN);
                    break;
                case 6:
                    drawChart(items[1], PrideChartType.PEAKS_MS);
                    break;
                case 7:
                    drawChart(items[1], PrideChartType.DELTA_MASS);
                    break;
                case 8:
                    drawChart(items[1], PrideChartType.MISSED_CLEAVAGES);
                    break;
            }
        }

        reader.close();
    }

    public static void main(String[] args) throws Exception {
        PrideChartFactoryRun run = new PrideChartFactoryRun();

//        File jsonFile = new File("testset/2.json");
        File jsonFile = new File("testset/10.json");
        run.drawChartList(jsonFile);
    }
}
