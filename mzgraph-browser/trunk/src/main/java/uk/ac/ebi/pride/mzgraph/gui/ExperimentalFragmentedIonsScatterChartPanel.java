package uk.ac.ebi.pride.mzgraph.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.tooltip.ExperimentalFragmentedIonsScatterChartTooltipGenerator;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsDataset;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

import javax.swing.*;
import java.awt.*;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsScatterChartPanel extends JPanel{
    private int fraction = 3;
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
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        XYPlot plot = (XYPlot) chart.getPlot();
        dataset.addChangeListener(plot);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeZeroBaselineVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.blue);
        renderer.setBaseToolTipGenerator(new ExperimentalFragmentedIonsScatterChartTooltipGenerator(fraction));

        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setTickUnit(new NumberTickUnit(0.25));
//        range.setUpperBound(0.5);
//        range.setLowerBound(-0.5);


        return chart;
    }

    private void init(ExperimentalFragmentedIonsTableModel tableModel) {
        this.tableModel = tableModel;
        JFreeChart chart = createChart(tableModel);

        this.chartPanel = new ChartPanel(chart);
        add(chartPanel);
    }

    /**
     * auto add annotation
     * @see uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel#addManualAnnotation(uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation)
     */
    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                       double[] mzArray, double[] intensityArray) {
        ExperimentalFragmentedIonsTableModel tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, ionPair, mzArray, intensityArray);
        init(tableModel);
    }

    /**
     * manual add annotation.
     */
    public ExperimentalFragmentedIonsScatterChartPanel(ExperimentalFragmentedIonsTableModel tableModel) {
        init(tableModel);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, double[] mzArray, double[] intensityArray) {
        this(precursorIon, ProductIonPair.B_Y, mzArray, intensityArray);
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

    public void flush() {
        removeAll();

        JFreeChart chart = createChart(this.tableModel);
        this.chartPanel = new ChartPanel(chart);
        add(chartPanel);
    }

    public void setShowAutoAnnotations(boolean showAuto) {
        this.tableModel.setShowAuto(showAuto);
        flush();
    }

    public void setShowManualAnnotations(boolean showManual) {
        this.tableModel.setShowManual(showManual);
        flush();
    }


    public void addAnnotation(IonAnnotation annotation) {
        this.tableModel.addManualAnnotation(annotation);
        flush();
    }

    public void addAllAnnotations(java.util.List<IonAnnotation> annotationList) {
        this.tableModel.addAllManualAnnotation(annotationList);
        flush();
    }

    public void setPeaks(double[] mzArray, double[] intensityArray) {
        this.tableModel.setPeaks(mzArray, intensityArray);
        flush();
    }
}
