package uk.ac.ebi.pride.mzgraph.gui;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.renderer.PracticeIonRenderer;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsTable extends TheoreticalFragmentedIonsTable {
    private ExperimentalFragmentedIonsTableModel tableModel;
    private Object[][] matchedData;

    public void flush() {
        this.matchedData = this.tableModel.getMatchedData();
    }

    private void init(ExperimentalFragmentedIonsTableModel tableModel) {
        this.tableModel = tableModel;
        setModel(this.tableModel);
        flush();
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, ProductIonPair pair, int fraction,
                                           double[] mzArray, double[] intensityArray) {
        super(precursorIon, pair, fraction);

        init(new ExperimentalFragmentedIonsTableModel(precursorIon, pair, mzArray, intensityArray));
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, ProductIonPair pair, int fraction) {
        this(precursorIon, pair, fraction, null, null);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, double[] mzArray, double[] intensityArray) {
        this(precursorIon, ProductIonPair.B_Y, 3, mzArray, intensityArray);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon) {
        this(precursorIon, ProductIonPair.B_Y, 3);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, ProductIonPair pair, int fraction,
                                           List<IonAnnotation> ionAnnotationList) {
        super(precursorIon, pair, fraction);

        init(new ExperimentalFragmentedIonsTableModel(precursorIon, pair, ionAnnotationList));
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, List<IonAnnotation> ionAnnotationList) {
        this(precursorIon, ProductIonPair.B_Y, 3, ionAnnotationList);
    }

    public void setShowAutoAnnotations(boolean showAuto) {
        tableModel.setShowAuto(showAuto);
        flush();
    }

    public void setShowManualAnnotations(boolean showManual) {
        this.tableModel.setShowManual(showManual);
        flush();
    }

    public void setPeaks(double[] mzArray, double[] intensityArray) {
        this.tableModel.setPeaks(mzArray, intensityArray);
    }

    public void addAnnotation(IonAnnotation annotation) {
        this.tableModel.addManualAnnotation(annotation);
    }

    public void addAllAnnotations(List<IonAnnotation> annotationList) {
        this.tableModel.addAllManualAnnotation(annotationList);
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        if (this.tableModel.isMassColumn(column)) {
            return new PracticeIonRenderer(getFraction(), this.matchedData, row, column);
        } else {
            return super.getCellRenderer(row, column);
        }
    }

    public Component prepareRenderer(TableCellRenderer renderer,
                                     int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        Object to = getValueAt(rowIndex, vColIndex);
        Object po = this.matchedData[rowIndex][vColIndex];

        if (po != null) {
            double practice = (Double) po;
            double theoretical = ((ProductIon) to).getMassOverCharge();

            NumberFormat formatter = NumberFormat.getInstance();
            formatter.setMaximumFractionDigits(getFraction());

            JComponent jc = (JComponent)c;
            jc.setToolTipText(
                    "m/z:" + formatter.format(practice) + " " +
                    "Error: " + formatter.format(practice - theoretical)
            );
        }


        return c;
    }
}
