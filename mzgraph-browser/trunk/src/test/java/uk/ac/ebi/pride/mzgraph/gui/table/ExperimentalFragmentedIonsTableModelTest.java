package uk.ac.ebi.pride.mzgraph.gui.table;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.iongen.model.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.mol.Peptide;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mzgraph.ExampleUtil;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

import java.util.Random;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsTableModelTest {
    /**
     * this method just be used for test.
     *
     * random generate about thirty practice data based on the theoretical fragment ions mass over charge.
     */
    public Double[][] generateMatchedData() {
        Peptide peptide = ExampleUtil.generatePeptide();
        PrecursorIon precursorIon = new DefaultPrecursorIon(peptide, 2);
        ExperimentalFragmentedIonsTableModel model = new ExperimentalFragmentedIonsTableModel(precursorIon, ProductIonPair.B_Y);
        model.setPeaks(ExampleUtil.mzArr, ExampleUtil.intentArr);

        Double[][] matchedData = new Double[model.getRowCount()][model.getColumnCount()];

        int row;
        int column;
        Object theoreticalValue;
        double diff;
        ProductIon ion;
        for (int i = 0; i < 30; i++) {
            row = new Random().nextInt(model.getRowCount());
            column = new Random().nextInt(model.getColumnCount());
            theoreticalValue = model.getValueAt(row, column);
            if (theoreticalValue instanceof ProductIon) {
                ion = (ProductIon) theoreticalValue;
                diff = new Random().nextDouble() - 0.5;
                matchedData[row][column] = ion.getMassOverCharge() + diff;
            }
        }

        return matchedData;
    }

    public static void main(String[] args) {
        Peptide peptide = ExampleUtil.generatePeptide();
        PrecursorIon precursorIon = new DefaultPrecursorIon(peptide, 2);
        ExperimentalFragmentedIonsTableModel model = new ExperimentalFragmentedIonsTableModel(precursorIon, ProductIonPair.B_Y);
        model.setPeaks(ExampleUtil.mzArr, ExampleUtil.intentArr);

        java.util.List<IonAnnotation> annotationList = ExampleUtil.generateAnnotationList();
        model.addAllManualAnnotations(annotationList);

        for (int i = 0; i < model.getColumnCount(); i++) {
            System.out.print(model.getColumnName(i) + "\t");
        }
        System.out.println();

        IonAnnotation annotation;
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                annotation = model.getMatchedData()[i][j];

                if (annotation == null) {
                    System.out.print("N" + "\t");
                } else {
                    System.out.print(annotation.getMz().doubleValue() + "\t");
                }
            }
            System.out.println();
        }
    }
}
