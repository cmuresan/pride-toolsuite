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
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalTableModelObserver;

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

public class MzTablePanel extends JPanel implements ExperimentalTableModelObserver {
    private ExperimentalFragmentedIonsTable table;
    private ExperimentalFragmentedIonsScatterChartPanel scatterChartPanel;

    private ExperimentalFragmentedIonsTableModel tableModel;
    private ExperimentalFragmentedIonsDataset dataset;

    private void init(ExperimentalFragmentedIonsTable table) {
        this.table = table;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 500));

        JScrollPane tablePane = new JScrollPane(table);
        this.tableModel = (ExperimentalFragmentedIonsTableModel) table.getModel();

        // the second table model observer.
        this.tableModel.addObserver(2, this);

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
                int item = -1;
                int series = -1;

                if (matchedData[row][column] != null) {
                    ProductIon ion = (ProductIon) tableModel.getValueAt(row, column);

                    /**
                     * If there are matched data in the clicked cell, highlight the corresponding point in the chart.
                     */
                    double theoretical = ion.getMassOverCharge();
                    item = getItemNumber(row, column);
                    series = getSeriesNumber(row, column);

                    double x = dataset.getXValue(series, item);
                    double y = dataset.getYValue(series, item);

                    plot.clearAnnotations();
                    NumberAxis range = (NumberAxis) plot.getRangeAxis();
                    double ySize = range.getTickUnit().getSize() / 10;
                    NumberAxis domain = (NumberAxis) plot.getDomainAxis();
                    double xSize = domain.getTickUnit().getSize() / 10;
                    XYBoxAnnotation boxAnnotation = new XYBoxAnnotation(x - xSize, y - ySize, x + xSize, y + ySize, new BasicStroke(0.0f), Color.green, Color.green);

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

                    row = getRowNumber(series, item);
                    col = getColNumber(series, item);

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

    public List<IonAnnotation> getManualAnnotationList() {
        return tableModel.getManualAnnotations();
    }

    public List<IonAnnotation> getAutoAnnotationList() {
        return tableModel.getAutoAnnotations();
    }

    public void addManualAnnotation(IonAnnotation annotation) {
        this.tableModel.addManualAnnotation(annotation);
    }

    public void addAllManualAnnotations(List<IonAnnotation> ionAnnotationList) {
        this.tableModel.addAllManualAnnotations(ionAnnotationList);
    }

    public void setShowAutoAnnotations(boolean showAuto) {
        this.tableModel.setShowAuto(showAuto);
    }

    public void setShowManualAnnotations(boolean showManual) {
        this.tableModel.setShowManual(showManual);
    }

    public void setPeaks(double[] mzArray, double[] intensityArray) {
        this.tableModel.setPeaks(mzArray, intensityArray);
    }

    public void flush() {
        this.tableModel.notifyObservers();
    }

    private class Point {
        int series;
        int item;

        Point(int series, int item) {
            this.series = series;
            this.item = item;
        }

        public int getSeries() {
            return series;
        }

        public int getItem() {
            return item;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (item != point.item) return false;
            if (series != point.series) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = series;
            result = 31 * result + item;
            return result;
        }
    }

    /**
     * this matrix use to store the series and item value of each matched data.
     */
    private Point[][] pointMatrix;

    private int getItemIndex(XYSeries series, double x, double y) {
        XYDataItem item;
        for (int i = 0; i < series.getItemCount(); i++) {
            item = series.getDataItem(i);

            if (item.getXValue() == x && item.getYValue() == y) {
                return i;
            }
        }

        return -1;
    }

    public int getRowNumber(int seriesIndex, int itemIndex) {
        Point point;
        for (int row = 0; row < pointMatrix.length; row++) {
            for (int col = 0; col < pointMatrix[row].length; col++) {
                point = pointMatrix[row][col];
                if (point != null && point.getSeries() == seriesIndex && point.getItem() == itemIndex) {
                    return row;
                }
            }
        }

        return -1;
    }

    public int getColNumber(int series, int itemIndex) {
        Point point;
        for (int row = 0; row < pointMatrix.length; row++) {
            for (int col = 0; col < pointMatrix[row].length; col++) {
                point = pointMatrix[row][col];
                if (point != null && point.getSeries() == series && point.getItem() == itemIndex) {
                    return col;
                }
            }
        }

        return -1;
    }

    public int getItemNumber(int row, int col) {
        Point point = pointMatrix[row][col];
        if (point == null) {
            return -1;
        } else {
            return point.getItem();
        }
    }

    public int getSeriesNumber(int row, int col) {
        Point point = pointMatrix[row][col];
        if (point == null) {
            return -1;
        } else {
            return point.getSeries();
        }
    }

    /**
     * update point matrix
     */
    @Override
    public void update(ExperimentalFragmentedIonsTableModel tableModel) {
        Double[][] matchedData = tableModel.getMatchedData();
        XYSeriesCollection dataset = (XYSeriesCollection) scatterChartPanel.getChartPanel().getChart().getXYPlot().getDataset();

        this.pointMatrix = new Point[tableModel.getRowCount()][tableModel.getColumnCount()];

        Point point;
        Double matchedMass;

        XYSeries series;
        ProductIon ion;
        int seriesIndex;
        int itemIndex;
        double x;
        double y;
        for (int col = 1; col < tableModel.getColumnCount(); col++) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                if (matchedData[row][col] != null && (ion = (ProductIon) tableModel.getValueAt(row, col)) != null) {
                    try {
                    matchedMass = matchedData[row][col];
                    series = dataset.getSeries(ion.getType().getGroup().getName());
                    seriesIndex = dataset.indexOf(ion.getType().getGroup().getName());
                    x = ion.getMassOverCharge();
                    y = matchedMass - x;
                    itemIndex = getItemIndex(series, x, y);
                    point = new Point(seriesIndex, itemIndex);
                    pointMatrix[row][col] = point;
                    }catch (NullPointerException e) {
                        System.out.println(ion.toString());
                    }
                }
            }
        }
    }
}
