package uk.ac.ebi.pride.chart;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import uk.ac.ebi.pride.chart.dataset.*;
import uk.ac.ebi.pride.chart.io.PrideDataException;
import uk.ac.ebi.pride.chart.io.PrideDataReader;
import uk.ac.ebi.pride.chart.plot.*;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public class PrideChartFactory {
    private static ChartTheme currentTheme = new StandardChartTheme("JFree");

    public static PrideXYPlot getXYPlot(Double[] domainData, PrideData[] rangeData, PrideChartType type) {
        PrideXYDataSource dataSource;

        switch (type) {
            case DELTA_MASS:
                dataSource = new PrideXYDataSource(domainData, rangeData, PrideDataType.ALL_SPECTRA);
                break;
            case PEPTIDES_PROTEIN:
                dataSource = new PrideXYDataSource(domainData, rangeData, PrideDataType.ALL_SPECTRA);
                break;
            case MISSED_CLEAVAGES:
                dataSource = new PrideXYDataSource(domainData, rangeData, PrideDataType.ALL_SPECTRA);
                break;
            case AVERAGE_MS:
                dataSource = new PrideXYDataSource(domainData, rangeData, PrideDataType.ALL_SPECTRA);
                break;
            case PRECURSOR_CHARGE:
                dataSource = new PrideXYDataSource(domainData, rangeData, PrideDataType.IDENTIFIED_SPECTRA);
                break;
            case PRECURSOR_MASSES:
                dataSource = new PrideXYDataSource(domainData, rangeData, PrideDataType.ALL_SPECTRA);
                break;
            default:
                throw new IllegalArgumentException("Can not create XY style plot.");
        }

        return getXYPlot(dataSource, type);
    }

    public static PrideXYPlot getXYPlot(PrideXYDataSource dataSource, PrideChartType type) {
        PrideXYPlot plot;
        switch (type) {
            case DELTA_MASS:
                plot = new DeltaMZPlot(PrideDatasetFactory.getXYDataset(dataSource));
                break;
            case PEPTIDES_PROTEIN:
                plot = new PeptidesProteinPlot(PrideDatasetFactory.getXYDataset(dataSource));
                break;
            case MISSED_CLEAVAGES:
                plot = new MissedCleavagesPlot(PrideDatasetFactory.getXYDataset(dataSource));
                break;
            case AVERAGE_MS:
                plot = new AverageMSPlot(PrideDatasetFactory.getXYDataset(dataSource), PrideDataType.ALL_SPECTRA);
                break;
            case PRECURSOR_CHARGE:
                plot = new PrecursorChargePlot(PrideDatasetFactory.getXYDataset(dataSource));
                break;
            case PRECURSOR_MASSES:
                plot = new PrecursorMassesPlot(PrideDatasetFactory.getXYDataset(dataSource), PrideDataType.ALL_SPECTRA);
                break;
            default:
                throw new IllegalArgumentException("Can not create XY style plot.");
        }

        return plot;
    }

    public static PrideCategoryPlot getHistogramPlot(PrideData[] data, PrideChartType type) {
        PrideHistogramDataSource dataSource;

        switch (type) {
            case PEAKS_MS:
                dataSource = new PrideEqualWidthHistogramDataSource(data, PrideDataType.ALL_SPECTRA);
                dataSource.appendBins(((PrideEqualWidthHistogramDataSource)dataSource).generateBins(0, 400, 10));
                break;
            case PEAK_INTENSITY:
                dataSource = new PrideHistogramDataSource(data, PrideDataType.ALL_SPECTRA);
                break;
            default:
                throw new IllegalArgumentException("Can not create Histogram style plot.");
        }

        return getHistogramPlot(dataSource, type);
    }

    public static PrideCategoryPlot getHistogramPlot(PrideHistogramDataSource dataSource, PrideChartType type) {
        PrideCategoryPlot plot;

        switch (type) {
            case PEAKS_MS:
                plot = new PeaksMSPlot(PrideDatasetFactory.getHistogramDataset(dataSource));
                break;
            case PEAK_INTENSITY:
                plot = new PeakIntensityPlot(PrideDatasetFactory.getHistogramDataset(dataSource), PrideDataType.ALL_SPECTRA);
                break;
            default:
                throw new IllegalArgumentException("Can not create Histogram style plot.");
        }

        return plot;
    }

    public static JFreeChart getChart(PrideCategoryPlot plot) {
        String title = plot.isSmallPlot() ? plot.getTitle() : plot.getFullTitle();

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, plot.isLegend());
        currentTheme.apply(chart);
        return chart;
    }

    public static JFreeChart getChart(PrideXYPlot plot) {
        String title = plot.isSmallPlot() ? plot.getTitle() : plot.getFullTitle();

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, plot.isLegend());
        currentTheme.apply(chart);
        return chart;
    }

    public static JFreeChart getChart(PrideDataReader reader, PrideChartType chartType) throws PrideDataException {
        if (reader == null) {
            throw new NullPointerException("PrideDataReader is null!");
        }

        SortedMap<PrideChartType, PrideXYDataSource> xyDataSourceMap = reader.getXYDataSourceMap();
        SortedMap<PrideChartType, PrideHistogramDataSource> histogramDataSourceMap = reader.getHistogramDataSourceMap();

        PrideXYDataSource xyDataSource;
        PrideHistogramDataSource histogramDataSource;

        xyDataSource = xyDataSourceMap.get(chartType);
        if (xyDataSource != null) {
            return getChart(getXYPlot(xyDataSource, chartType));
        }

        histogramDataSource = histogramDataSourceMap.get(chartType);
        if (histogramDataSource != null) {
            return getChart(getHistogramPlot(histogramDataSource, chartType));
        }

        return null;
    }

    public static List<JFreeChart> getChartList(PrideDataReader reader, PrideChartSummary summary) throws PrideDataException {
        if (reader == null) {
            throw new NullPointerException("PrideDataReader is null!");
        }
        reader.readData();

        List<JFreeChart> charts = new ArrayList<JFreeChart>();

        SortedMap<PrideChartType, PrideXYDataSource> xyDataSourceMap = reader.getXYDataSourceMap();
        SortedMap<PrideChartType, PrideHistogramDataSource> histogramDataSourceMap = reader.getHistogramDataSourceMap();

        PrideXYDataSource xyDataSource;
        PrideHistogramDataSource histogramDataSource;
        for (PrideChartType chartType : summary.getAll()) {
            xyDataSource = xyDataSourceMap.get(chartType);
            if (xyDataSource != null) {
                charts.add(getChart(getXYPlot(xyDataSource, chartType)));
                continue;
            }

            histogramDataSource = histogramDataSourceMap.get(chartType);
            if (histogramDataSource != null) {
                charts.add(getChart(getHistogramPlot(histogramDataSource, chartType)));
            }
        }

        return charts;
    }


}
