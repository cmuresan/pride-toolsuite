package uk.ac.ebi.pride.mzgraph.chart.graph;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYBoxAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.iongen.model.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.mol.Peptide;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.renderer.PeptideIonRenderer;
import uk.ac.ebi.pride.mzgraph.gui.ExperimentalFragmentedIonsScatterChartPanel;
import uk.ac.ebi.pride.mzgraph.gui.ExperimentalFragmentedIonsTable;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsDataset;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 15/10/12
 */

public class MzTablePanel extends JPanel {
    private ExperimentalFragmentedIonsTable table;
    private ExperimentalFragmentedIonsScatterChartPanel scatterChartPanel;

    private ExperimentalFragmentedIonsTableModel tableModel;

    private void init(ExperimentalFragmentedIonsTable table) {
        this.table = table;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 500));

        JScrollPane tablePane = new JScrollPane(table);
        this.tableModel = (ExperimentalFragmentedIonsTableModel) table.getModel();

        scatterChartPanel = new ExperimentalFragmentedIonsScatterChartPanel(this.tableModel);
        ChartPanel chartPanel = scatterChartPanel.getChartPanel();
        chartPanel.setPreferredSize(new Dimension(1000, 250));

        add(tablePane, BorderLayout.CENTER);
        add(scatterChartPanel, BorderLayout.SOUTH);

        addTableAction(table, chartPanel);
        addChartAction(table, chartPanel);
    }

    public MzTablePanel(Peptide peptide, double[] mzArray, double[] intensityArray) {
        PrecursorIon precursorIon = new DefaultPrecursorIon(peptide);

        init(new ExperimentalFragmentedIonsTable(precursorIon, mzArray, intensityArray));
    }

    public MzTablePanel(Peptide peptide) {
        this(peptide, null, null);
    }

    public MzTablePanel(Peptide peptide, List<IonAnnotation> ionAnnotationList) {
        PrecursorIon precursorIon = new DefaultPrecursorIon(peptide);
        init(new ExperimentalFragmentedIonsTable(precursorIon, ionAnnotationList));
    }

    private void addTableAction(ExperimentalFragmentedIonsTable table, ChartPanel chartPanel) {
        final ExperimentalFragmentedIonsTableModel tableModel = (ExperimentalFragmentedIonsTableModel) table.getModel();
        final ExperimentalFragmentedIonsDataset dataset = (ExperimentalFragmentedIonsDataset) chartPanel.getChart().getXYPlot().getDataset();

        JFreeChart chart = chartPanel.getChart();
        final XYPlot plot = (XYPlot) chart.getPlot();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ExperimentalFragmentedIonsTable target = (ExperimentalFragmentedIonsTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();

                Object[][] matchedData = tableModel.getMatchedData();
                ProductIon[][] xValues = dataset.getxValues();

                int item = -1;
                int series = -1;
                if (matchedData[row][column] != null) {
                    /**
                     * If there are matched data in the clicked cell, highlight the corresponding point in the chart.
                     */
                    double theoretical = ((ProductIon) tableModel.getValueAt(row, column)).getMassOverCharge();
                    boolean success = false;
                    for (int i = 0; i < xValues.length; i++) {
                        for (int j = 0; j < xValues[i].length; j++) {
                            if (xValues[i][j] != null && xValues[i][j].getMassOverCharge() == theoretical) {
                                series = i;
                                item = j;
                                success = true;
                                break;
                            }
                        }

                        if (success) {
                            break;
                        }
                    }

                    double x = dataset.getXValue(series, item);
                    double y = dataset.getYValue(series, item);

                    plot.clearAnnotations();
                    NumberAxis range = (NumberAxis) plot.getRangeAxis();
                    double ySize = range.getTickUnit().getSize() / 10;
                    NumberAxis domain = (NumberAxis) plot.getDomainAxis();
                    double xSize = domain.getTickUnit().getSize() / 10;
                    XYBoxAnnotation boxAnnotation = new XYBoxAnnotation(x - xSize, y - ySize, x + xSize, y + ySize, new BasicStroke(0.0f), Color.green, Color.green);

                    ProductIon ion = (ProductIon) tableModel.getValueAt(row, column);
                    NumberFormat formatter = NumberFormat.getInstance();
                    formatter.setMaximumFractionDigits(3);

                    String msg = ion.getName() + "(" + formatter.format(x) + ", " + formatter.format(y) + ")";

                    XYTextAnnotation textAnnotation;
                    if (y - ySize * 2 < 0) {
                        textAnnotation = new XYTextAnnotation(msg, x - xSize * 2, y + ySize * 2);
                    } else {
                        textAnnotation = new XYTextAnnotation(msg, x - xSize * 2, y - ySize * 2);
                    }


                    plot.addAnnotation(boxAnnotation);
                    plot.addAnnotation(textAnnotation);
                } else {
                    plot.clearAnnotations();
                }
            }
        });
    }

    /**
     * If click one point in the chart, highlight corresponding table cell.
     */
    private void addChartAction(final ExperimentalFragmentedIonsTable table, ChartPanel chartPanel) {
        final ExperimentalFragmentedIonsTableModel tableModel = (ExperimentalFragmentedIonsTableModel) table.getModel();
        final ExperimentalFragmentedIonsDataset dataset = (ExperimentalFragmentedIonsDataset) chartPanel.getChart().getXYPlot().getDataset();

        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                ChartEntity entity = chartMouseEvent.getEntity();

                XYItemEntity itemEntity;
                int item;
                int series;
                int row;
                int col;
                if (entity instanceof XYItemEntity) {
                    itemEntity = (XYItemEntity) entity;
                    item = itemEntity.getItem();
                    series = itemEntity.getSeriesIndex();

                    row = dataset.getRowNumber(series, item);
                    col = dataset.getColNumber(series, item);

                    if (row != -1 && col != -1) {
                        PeptideIonRenderer cellRenderer = (PeptideIonRenderer) table.getCellRenderer(row, col);
                        cellRenderer.setHighlight(row, col);
                        table.revalidate();
                        table.repaint();
                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {

            }
        });
    }

    public List<IonAnnotation> getAnnotationList() {
        return tableModel.getAnnotations();
    }

    private void flush() {
        table.flush();

        ChartPanel chartPanel = scatterChartPanel.getChartPanel();
        chartPanel.setPreferredSize(new Dimension(1000, 250));

        addTableAction(table, chartPanel);
        addChartAction(table, chartPanel);
    }

    public void addAnnotation(IonAnnotation annotation) {
        scatterChartPanel.addAnnotation(annotation);
        flush();
    }

    public void addAllAnnotations(List<IonAnnotation> ionAnnotationList) {
        scatterChartPanel.addAllAnnotations(ionAnnotationList);
        flush();
    }

    public void setShowAutoAnnotations(boolean showAuto) {
        scatterChartPanel.setShowAutoAnnotations(showAuto);
        table.setShowAutoAnnotations(showAuto);
        flush();
    }

    public void setShowManualAnnotations(boolean showManual) {
        scatterChartPanel.setShowManualAnnotations(showManual);
        flush();
    }

    public void setPeaks(double[] mzArray, double[] intensityArray) {
        this.scatterChartPanel.setPeaks(mzArray, intensityArray);
        flush();
    }

}
