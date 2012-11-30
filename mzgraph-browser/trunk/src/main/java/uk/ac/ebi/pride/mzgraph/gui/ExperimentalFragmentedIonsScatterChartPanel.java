package uk.ac.ebi.pride.mzgraph.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mzgraph.chart.axis.DiffDaltonAxis;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.mzgraph.chart.tooltip.ExperimentalFragmentedIonsScatterChartTooltipGenerator;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsDataset;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.mzgraph.gui.data.PeakSet;

import javax.swing.*;
import java.awt.*;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsScatterChartPanel extends JPanel{
    /**
     * m/z fraction, the values is {@value}.
     */
    private ChartPanel chartPanel;
    private ExperimentalFragmentedIonsTableModel tableModel;
    private ExperimentalFragmentedIonsDataset dataset;

    private JFreeChart createChart(ExperimentalFragmentedIonsTableModel tableModel) {
        dataset = new ExperimentalFragmentedIonsDataset(tableModel);

        JFreeChart chart = ChartFactory.createScatterPlot(
                null,
                "Mass (Da)",
                "Error (Da)",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,
                true,
                false);

        XYPlot plot = (XYPlot) chart.getPlot();

        dataset.addChangeListener(plot);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeZeroBaselineVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeCrosshairVisible(false);
        plot.setOutlineVisible(false);

        final Marker startMarker = new ValueMarker(-0.5);
        startMarker.setPaint(Color.black);
        final Marker zeroMarker = new ValueMarker(0);
        zeroMarker.setPaint(Color.black);
        final Marker endMarker = new ValueMarker(0.5);
        endMarker.setPaint(Color.black);
        plot.addRangeMarker(startMarker);
        plot.addRangeMarker(zeroMarker);
        plot.addRangeMarker(endMarker);

        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.blue);
        renderer.setBaseToolTipGenerator(new ExperimentalFragmentedIonsScatterChartTooltipGenerator());

        chart.removeLegend();

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setTickUnit(new NumberTickUnit(200d));
        yAxis.setAutoRange(true);
        plot.setDomainAxis(yAxis);

        DiffDaltonAxis xAxis = new DiffDaltonAxis(-1, 1);
        xAxis.setRange(-1, 1);
        xAxis.setTickUnit(new NumberTickUnit(MzGraphConstants.INTERVAL_RANGE));
        xAxis.setAutoRange(true);
        xAxis.setTickLabelsVisible(true);
        plot.setRangeAxis(xAxis);

        return chart;
    }

    private void init(ExperimentalFragmentedIonsTableModel tableModel) {
        this.tableModel = tableModel;
        JFreeChart chart = createChart(tableModel);

        this.chartPanel = new ChartPanel(chart);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                       PeakSet peakSet) {
        ExperimentalFragmentedIonsTableModel tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, ionPair);
        tableModel.setPeaks(peakSet);
        init(tableModel);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                       double[] mzArray, double[] intensityArray) {
        ExperimentalFragmentedIonsTableModel tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, ionPair);
        tableModel.setPeaks(mzArray, intensityArray);
        init(tableModel);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(ExperimentalFragmentedIonsTableModel tableModel) {
        init(tableModel);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, double[] mzArray, double[] intensityArray) {
        this(precursorIon, ProductIonPair.B_Y, mzArray, intensityArray);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, PeakSet peakSet) {
        this(precursorIon, ProductIonPair.B_Y, peakSet);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                       java.util.List<IonAnnotation> manualAnnotations) {
        ExperimentalFragmentedIonsTableModel tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, ionPair, manualAnnotations);
        init(tableModel);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, java.util.List<IonAnnotation> manualAnnotations) {
        this(precursorIon, ProductIonPair.B_Y, manualAnnotations);
    }


    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public void setShowAutoAnnotations(boolean showAuto) {
        this.tableModel.setShowAuto(showAuto);
    }

    public void addManualAnnotation(IonAnnotation annotation) {
        this.tableModel.addManualAnnotation(annotation);
    }

    public void addAllManualAnnotations(java.util.List<IonAnnotation> annotationList) {
        this.tableModel.addAllManualAnnotations(annotationList);
    }

    public void setPeaks(double[] mzArray, double[] intensityArray) {
        this.tableModel.setPeaks(mzArray, intensityArray);
    }
}
