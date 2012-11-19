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
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.renderer.TheoreticalFragmentedIonsRenderer;
import uk.ac.ebi.pride.mzgraph.gui.ExperimentalFragmentedIonsScatterChartPanel;
import uk.ac.ebi.pride.mzgraph.gui.ExperimentalFragmentedIonsTable;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsDataset;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalTableModelObserver;
import uk.ac.ebi.pride.mzgraph.gui.data.PeakSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;

/**
 * This is a ExperimentalTableModelObserver, which maintain the mapping between
 * table model Cell (row, column) and chart panel XYSeries (series, item).
 * When user click one cell, the chart panel point will highlight. On the contrary,
 * when user click a point, the table cell background color will display grey.
 *
 * Creator: Qingwei-XU
 * Date: 15/10/12
 */

public class MzTablePanel extends JPanel implements ExperimentalTableModelObserver {
    private ExperimentalFragmentedIonsScatterChartPanel scatterChartPanel;

    private JScrollPane tablePanel;
    private ChartPanel chartPanel;

    private ExperimentalFragmentedIonsTableModel tableModel;

    private JPanel configPanel;
    private JLabel commentLabel = new JLabel();
    private JLabel ionPairLabel;
    private JComboBox ionPairChooser;
    private JLabel rangeLabel;
    private JSlider rangeSlider;

    private void flushConfigPanel() {
        if (this.tableModel == null) {
            commentLabel.setText(MzGraphConstants.DELTA_MZ_ERROR);
            ionPairLabel.setVisible(false);
            ionPairChooser.setVisible(false);
            rangeLabel.setVisible(false);
            rangeSlider.setVisible(false);
        } else if (this.tableModel.getAllManualAnnotations().size() == 0) {
            commentLabel.setText(MzGraphConstants.AUTO_ANNOTATIONS);
            setShowAuto(true);
            ionPairLabel.setVisible(true);
            ionPairChooser.setVisible(true);
            rangeLabel.setVisible(true);
            rangeSlider.setVisible(true);
        } else {
            commentLabel.setText(MzGraphConstants.MANUAL_ANNOTATIONS);
            setShowAuto(false);
            ionPairLabel.setVisible(true);
            ionPairChooser.setVisible(true);
            rangeLabel.setVisible(false);
            rangeSlider.setVisible(false);
        }
    }

    private void init(final ExperimentalFragmentedIonsTable table) {
        tablePanel = new JScrollPane(table);
        this.tableModel = (ExperimentalFragmentedIonsTableModel) table.getModel();

        // the second table model observer.
        this.tableModel.addObserver(2, this);

        scatterChartPanel = new ExperimentalFragmentedIonsScatterChartPanel(this.tableModel);
        chartPanel = scatterChartPanel.getChartPanel();

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(scatterChartPanel, BorderLayout.EAST);
        contentPane.add(tablePanel, BorderLayout.CENTER);

        ionPairLabel = new JLabel("Choose Ion Type: ");
        ionPairChooser = new JComboBox();
        ionPairChooser.addItem(ProductIonPair.B_Y);
        ionPairChooser.addItem(ProductIonPair.A_X);
        ionPairChooser.addItem(ProductIonPair.C_Z);
        ionPairChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox chooser = (JComboBox)e.getSource();
                ProductIonPair ionPair = (ProductIonPair) chooser.getSelectedItem();
                table.setProductIonPair(ionPair);
            }
        });

        rangeLabel = new JLabel("Range(Da):");
        rangeSlider = new JSlider(
                JSlider.HORIZONTAL,
                1,        // minimum range is 0.1 Da
                10,        // maximum range is 1 Da
                5         // default range is 0.5 Da
        );
        rangeSlider.setMinorTickSpacing(1);
        rangeSlider.setPaintLabels(false);
        rangeSlider.setPaintTicks(true);
        rangeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (! source.getValueIsAdjusting()) {
                    double newRange = source.getValue() / 10d;
                    setRange(newRange);
                    table.revalidate();
                    table.repaint();
                    source.setToolTipText(newRange + "Da");
                }
            }
        });

        configPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        configPanel.setMinimumSize(new Dimension(1000, 30));
        configPanel.add(commentLabel);
        configPanel.add(ionPairLabel);
        configPanel.add(ionPairChooser);
        configPanel.add(rangeLabel);
        configPanel.add(rangeSlider);

        flushConfigPanel();

        add(configPanel, BorderLayout.NORTH);
        add(contentPane, BorderLayout.CENTER);


        addTableAction(table, chartPanel);
        addChartAction(table, chartPanel);
    }

    public MzTablePanel() {

    }

    public MzTablePanel(Peptide peptide, PeakSet peakSet) {
        PrecursorIon precursorIon = new DefaultPrecursorIon(peptide);

        init(new ExperimentalFragmentedIonsTable(precursorIon, peakSet));
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
        ExperimentalFragmentedIonsTable table = new ExperimentalFragmentedIonsTable(precursorIon);
        table.addAllAnnotations(ionAnnotationList);
        init(table);
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public JScrollPane getTablePanel() {
        return tablePanel;
    }

    public void setTable(ExperimentalFragmentedIonsTable table) {
        removeAll();
        init(table);
    }

    private void setRange(double range) {
        this.tableModel.setRange(range);
    }

    private void setShowAuto(boolean showAuto) {
        this.tableModel.setShowAuto(showAuto);
    }

    public void addManualAnnotation(IonAnnotation annotation) {
        this.tableModel.addManualAnnotation(annotation);
        flushConfigPanel();
    }

    public void addAllManualAnnotations(List<IonAnnotation> ionAnnotationList) {
        this.tableModel.addAllManualAnnotations(ionAnnotationList);
        flushConfigPanel();
    }

    /**
     * whether calculate auto annotations, or not.
     */
    public void setCalcuteAuto(boolean calcuteAuto) {
        this.tableModel.setCalcuteAuto(calcuteAuto);
    }

    public void setPeaks(double[] mzArray, double[] intensityArray) {
        this.tableModel.setPeaks(mzArray, intensityArray);
    }

    public void setPeaks(PeakSet peakSet) {
        this.tableModel.setPeaks(peakSet);
    }

    /**
     * If there are matched data in the clicked cell, highlight the corresponding point in the chart.
     */
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
                int item;
                int series;

                if (matchedData[row][column] != null) {
                    ProductIon ion = (ProductIon) tableModel.getValueAt(row, column);

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

                    String msg = ion.getType() + "(" + formatter.format(x) + ", " + formatter.format(y) + ")";

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
                        TheoreticalFragmentedIonsRenderer cellRenderer = (TheoreticalFragmentedIonsRenderer) table.getCellRenderer(row, col);
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

            return item == point.item && series == point.series;
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
     * update point matrix based on the experimental fragmentation table model.
     */
    @Override
    public void update(ExperimentalFragmentedIonsTableModel tableModel) {
        IonAnnotation[][] matchedData = tableModel.getMatchedData();
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
                    matchedMass = matchedData[row][col].getMz().doubleValue();
                    series = dataset.getSeries(ion.getType().getGroup().getName());
                    seriesIndex = dataset.indexOf(ion.getType().getGroup().getName());
                    x = ion.getMassOverCharge();
                    y = matchedMass - x;
                    itemIndex = getItemIndex(series, x, y);
                    point = new Point(seriesIndex, itemIndex);
                    pointMatrix[row][col] = point;
                }
            }
        }
    }
}
