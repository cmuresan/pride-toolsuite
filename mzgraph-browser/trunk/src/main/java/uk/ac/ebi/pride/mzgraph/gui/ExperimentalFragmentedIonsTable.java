package uk.ac.ebi.pride.mzgraph.gui;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.mzgraph.chart.renderer.ExperimentalFragmentedIonsRenderer;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.mzgraph.gui.data.PeakSet;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsTable extends TheoreticalFragmentedIonsTable {
    private ExperimentalFragmentedIonsTableModel tableModel;

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, ProductIonPair pair, PeakSet peakSet) {
        super(precursorIon, pair);

        this.tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, pair);
        this.tableModel.setPeaks(peakSet);
        setModel(this.tableModel);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, ProductIonPair pair,
                                           double[] mzArray, double[] intensityArray) {
        super(precursorIon, pair);

        this.tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, pair);
        this.tableModel.setPeaks(mzArray, intensityArray);
        setModel(this.tableModel);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, ProductIonPair pair) {
        this(precursorIon, pair, null, null);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, PeakSet peakSet) {
        this(precursorIon, ProductIonPair.B_Y, peakSet);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, double[] mzArray, double[] intensityArray) {
        this(precursorIon, ProductIonPair.B_Y, mzArray, intensityArray);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon) {
        this(precursorIon, ProductIonPair.B_Y);
    }

    public void setProductIonPair(ProductIonPair ionPair) {
        this.tableModel.setProductIonPair(ionPair);

        TableColumnModel columnModel = getColumnModel();
        TableColumn column;
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            column = columnModel.getColumn(i);
            column.setHeaderValue(tableModel.getColumnName(i));
        }
    }

    public void setShowAuto(boolean showAuto) {
        this.tableModel.setShowAuto(showAuto);
    }

    public void setShowWaterLoss(boolean showWaterLoss) {
        this.tableModel.setShowWaterLoss(showWaterLoss);

        revalidate();
        repaint();
    }

    public void setShowAmmoniaLoss(boolean showAmmoniaLoss) {
        this.tableModel.setShowAmmoniaLoss(showAmmoniaLoss);

        revalidate();
        repaint();
    }

    public void setPeaks(double[] mzArray, double[] intensityArray) {
        this.tableModel.setPeaks(mzArray, intensityArray);
    }

    public void addManualAnnotation(IonAnnotation annotation) {
        this.tableModel.addManualAnnotation(annotation);
    }

    public void addAllManualAnnotations(List<IonAnnotation> annotationList) {
        this.tableModel.addAllManualAnnotations(annotationList);
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        if (this.tableModel.isMassColumn(column)) {
            return new ExperimentalFragmentedIonsRenderer(this.tableModel.getMatchedData(), row, column);
        } else {
            return super.getCellRenderer(row, column);
        }
    }

    public Component prepareRenderer(TableCellRenderer renderer,
                                     int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        Object to = getValueAt(rowIndex, vColIndex);

        IonAnnotation[][] matchedData = tableModel.getMatchedData();
        IonAnnotation po = matchedData[rowIndex][vColIndex];

        if (po != null && to != null) {
            double practice = po.getMz().doubleValue();
            double theoretical = ((ProductIon) to).getMassOverCharge();

            NumberFormat formatter = NumberFormat.getInstance();
            formatter.setMaximumFractionDigits(MzGraphConstants.TABLE_FRACTION);

            JComponent jc = (JComponent)c;
            jc.setToolTipText("m/z:" + formatter.format(practice) + " " +
                              "Error: " + formatter.format(practice - theoretical));
        }


        return c;
    }
}
