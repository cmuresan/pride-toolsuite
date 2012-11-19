package uk.ac.ebi.pride.mzgraph.psm.conflict;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.mol.ProductIonType;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.mzgraph.psm.ConflictFilter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Creator: Qingwei-XU
 * Date: 06/11/12
 */

public class IonTypeConflictFilter implements ConflictFilter {
    /**
     * generate the prior score based on the fragment ion type.
     * <P>b > y > a > c</P>
     * Currently, we not consider about the neutral loss.
     */
    private int getScore(IonAnnotationInfo.Item item) {
        if (item == null) {
            return 50;
        }

        FragmentIonType type = item.getType();
        if (type.equals(FragmentIonType.B_ION)) {
            return 100;
        } else if (type.equals(FragmentIonType.Y_ION)) {
            return 90;
        } else if (type.equals(FragmentIonType.A_ION)) {
            return 80;
        } else if (type.equals(FragmentIonType.C_ION)) {
            return 70;
        }

        return 50;
    }

    private void filterConflictMatrixCell(IonAnnotation[][] src, IonAnnotation annotation,
                                          ExperimentalFragmentedIonsTableModel tableModel) {
        IonAnnotationInfo info = annotation.getAnnotationInfo();

        // there exists more than one annotation for one peak.
        IonAnnotationInfo.Item item, maxItem = null;
        int score, maxScore = 0;
        int row, col;
        for (java.util.Iterator<IonAnnotationInfo.Item> it = info.iterator(); it.hasNext(); ) {
            item = it.next();
            score = getScore(item);
            if (score > maxScore) {
                maxScore = score;
                maxItem = item;
            } else {
                if (item.getLocation() == tableModel.getRowCount()) {
                    continue;
                }
                // erase mirror item from matrix
                row = tableModel.getRowNumber(item.getType(), item.getLocation());
                col = tableModel.getColumnNumber(item.getType(), item.getCharge(), item.getNeutralLoss());
                try {
                    src[row][col] = null;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(item);
                    System.out.println("row:" + row + " col:" + col);
                }
            }
        }

        info = new IonAnnotationInfo();
        info.addItem(maxItem);
        annotation.setInfo(info);
    }

    @Override
    public IonAnnotation[][] filterConflict(IonAnnotation[][] src, ExperimentalFragmentedIonsTableModel tableModel) {
        if (tableModel == null) {
            throw new NullPointerException("ExperimentalFragmentedIonsTableModel is null!");
        }

        if (src == null) {
            return new IonAnnotation[tableModel.getRowCount()][tableModel.getColumnCount()];
        }

        IonAnnotation annotation;
        for (int row = 0; row < src.length; row++) {
            for (int col = 0; col < src[row].length; col++) {
                annotation = src[row][col];
                // there exists more than one annotation for one peak.
                if (annotation != null && annotation.getAnnotationInfo().getNumberOfItems() > 1) {
                    filterConflictMatrixCell(src, annotation, tableModel);
                }
            }
        }

        return src;
    }
}
