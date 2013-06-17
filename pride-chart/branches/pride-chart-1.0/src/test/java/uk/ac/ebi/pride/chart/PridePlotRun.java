package uk.ac.ebi.pride.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramBin;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import uk.ac.ebi.pride.chart.dataset.PrideDataSourceType;
import uk.ac.ebi.pride.chart.dataset.PrideDatasetFactory;
import uk.ac.ebi.pride.chart.dataset.PrideHistogramDataSource;
import uk.ac.ebi.pride.chart.dataset.PrideXYDataSource;
import uk.ac.ebi.pride.chart.plot.*;

import java.awt.*;
import java.util.Random;

/**
 * User: Qingwei
 * Date: 13/06/13
 */
public class PridePlotRun {
    private void drawChart(PrideXYPlot plot) {
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

    private void createChart(double[][] data, PrideChartType type) {
        PrideXYDataSource dataSource;

        PrideXYPlot plot;
        switch (type) {
            case DELTA_MASS:
                dataSource = new PrideXYDataSource(data, PrideDataSourceType.ALL_SPECTRA);
                plot = new DeltaMZPlot(PrideDatasetFactory.getXYDataset(dataSource));
                break;
            case PEPTIDES_PROTEIN:
                dataSource = new PrideXYDataSource(data);
                plot = new PeptidesProteinPlot(PrideDatasetFactory.getXYBarDataset(dataSource));
                break;
            case MISSED_CLEAVAGES:
                dataSource = new PrideXYDataSource(data, PrideDataSourceType.ALL_SPECTRA);
                plot = new MissedCleavagesPlot(PrideDatasetFactory.getXYBarDataset(dataSource));
                break;
            case AVERAGE_MS:
                dataSource = new PrideXYDataSource(data, PrideDataSourceType.ALL_SPECTRA);
                plot = new AverageMSPlot(PrideDatasetFactory.getXYBarDataset(dataSource));
                break;
            case PRECURSOR_CHARGE:
                dataSource = new PrideXYDataSource(data, PrideDataSourceType.IDENTIFIED_SPECTRA);
                plot = new PrecursorChargePlot(PrideDatasetFactory.getXYBarDataset(dataSource));
                break;
            case PRECURSOR_MASSES:
                dataSource = new PrideXYDataSource(data, PrideDataSourceType.ALL_SPECTRA);
                plot = new PrecursorMassesPlot(PrideDatasetFactory.getXYDataset(dataSource));
                break;
            default:
                plot = null;
        }

        drawChart(plot);
    }

    private void createChart(double[] data, PrideChartType type) {
        PrideHistogramDataSource dataSource;

        PrideXYPlot plot;
        switch (type) {
            case PEAKS_MS:
                dataSource = new PrideHistogramDataSource(data, PrideDataSourceType.ALL_SPECTRA);
                int binWidth = 400;
                plot = new PeaksMSPlot(PrideDatasetFactory.getHistogramDataset(dataSource, binWidth, true));
                break;
            case PEAK_INTENSITY:
                dataSource = new PrideHistogramDataSource(data, PrideDataSourceType.ALL_SPECTRA);
                dataSource.addBin(new HistogramBin(1, 100));
                dataSource.addBin(new HistogramBin(100, 1000));
                dataSource.addBin(new HistogramBin(1000, 2000));
                dataSource.addBin(new HistogramBin(2000, Integer.MAX_VALUE));
                plot = new PeakIntensityPlot(PrideDatasetFactory.getHistogramDataset(dataSource));
                break;
            default:
                plot = null;
        }

        drawChart(plot);
    }

    private double[][] generateXYData(double minX, double maxX, double minY, double maxY, int count) {
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

    private double[] generateXData(double minX, double maxX, int count) {
        double[] data = new double[count];

        int step = (int)((maxX - minX) * 1000) / count;
        if (step == 0) {
            if (minX < 0) {
                step = -1;
            } else {
                step = 1;
            }
        }

        double x;
        for (int i = 0; i < count; i++) {
            x = minX + step * i / 1000d;
            data[i] = x;
        }

        return data;
    }

    private double[][] generateXYData(int minX, int maxX, int minY, int maxY, int count) {
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

        // test XY style plots.
        double[][] xyData;
        xyData = run.generateXYData(-0.1, 0.1, 0.2, 1, 100);
        run.createChart(xyData, PrideChartType.DELTA_MASS);

        xyData = run.generateXYData(1, 6, 20, 300, 6);
        run.createChart(xyData, PrideChartType.PEPTIDES_PROTEIN);

        xyData = run.generateXYData(0, 4, 0, 125, 5);
        run.createChart(xyData, PrideChartType.MISSED_CLEAVAGES);

        xyData = run.generateXYData(5, 2000, 200, 300000, 400);
        run.createChart(xyData, PrideChartType.AVERAGE_MS);

        xyData = run.generateXYData(1, 8, 0, 100, 8);
        run.createChart(xyData, PrideChartType.PRECURSOR_CHARGE);

        xyData = run.generateXYData(0, 3500, 0, 0.125, 20);
        run.createChart(xyData, PrideChartType.PRECURSOR_MASSES);

        // test histogram plots.
        double[] xData;

        xData = run.generateXData(0, 3000, 1000);
        run.createChart(xData, PrideChartType.PEAKS_MS);

        xData = run.generateXData(0, 5000, 1000);
        run.createChart(xData, PrideChartType.PEAK_INTENSITY);
    }
}
