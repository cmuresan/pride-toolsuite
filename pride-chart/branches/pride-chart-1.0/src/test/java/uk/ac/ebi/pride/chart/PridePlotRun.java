package uk.ac.ebi.pride.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import uk.ac.ebi.pride.chart.dataset.PridePlotDatasetFactory;
import uk.ac.ebi.pride.chart.dataset.XYDataSource;
import uk.ac.ebi.pride.chart.plot.DeltaMZPlot;
import uk.ac.ebi.pride.chart.plot.PeptidesProteinPlot;
import uk.ac.ebi.pride.chart.plot.PrideXYPlot;

import java.awt.*;
import java.util.Random;

/**
 * User: Qingwei
 * Date: 13/06/13
 */
public class PridePlotRun {
    private void createChart(double[][] data, PrideChartType type) {
        XYDataSource dataSource = new XYDataSource(data);

        PrideXYPlot plot = null;
        switch (type) {
            case DELTA_MASS:
                plot = new DeltaMZPlot(PridePlotDatasetFactory.getDeltaMZDataset(type.name(), dataSource));
                break;
            case PEPTIDES_PROTEIN:
                plot = new PeptidesProteinPlot(PridePlotDatasetFactory.getXYBarDataset(type.name(), dataSource));
                break;
            case MISSED_TYPTIC_CLEAVAGES:
                break;
            case AVERAGE_MS:
                break;
            case PRECURSOR_CHARGE:
                break;
            case PRECURSOR_Masses:
                break;
            case PEAKS_MS:
                break;
            case PEAK_INTENSITY:
                break;
            default:
                plot = null;
        }

        if (plot != null) {
            JFreeChart chart = PrideChartFactory.getChart(plot);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(500, 300));
            ApplicationFrame mainFrame = new ApplicationFrame("test");
            mainFrame.setContentPane(chartPanel);

            mainFrame.pack();
            RefineryUtilities.centerFrameOnScreen(mainFrame);
            mainFrame.setVisible(true);
        }
    }

    private double[][] generateData(double minX, double maxX, double minY, double maxY, int count) {
        double[][] data = new double[2][count];

        int step = (int)((maxX - minX) * 1000) / count;
        if (step == 0) {
            if (minX < 0) {
                step = -1;
            } else {
                step = 1;
            }
        }
        int high = (int)((maxY - minY) * 1000);

        Random yRandom = new Random();
        double x;
        double y;
        for (int i = 0; i < count; i++) {
            x = minX + step * i / 1000d;
            y = minY + yRandom.nextInt(high) / 1000d;
            data[0][i] = x;
            data[1][i] = y;
        }

        return data;
    }

    private double[][] generateData(int minX, int maxX, int minY, int maxY, int count) {
        double[][] data = new double[2][count];

        int step = (maxX - minX) / count;
        step = step < 1 ? 1 : step;
        int high = maxY - minY;

        Random yRandom = new Random();
        double x;
        double y;
        for (int i = 0; i < count; i++) {
            x = minX + step * i;
            y = minY + yRandom.nextInt(high);
            data[0][i] = x;
            data[1][i] = y;
        }

        return data;
    }

    public static void main(String[] args) {
        PridePlotRun run = new PridePlotRun();
        double[][] data;

        data = run.generateData(-0.1, 0.1, 0.2, 1, 100);
        run.createChart(data, PrideChartType.DELTA_MASS);

//        data = run.generateData(1, 6, 20, 300, 6);
//        run.createChart(data, PrideChartType.PEPTIDES_PROTEIN);
    }
}
