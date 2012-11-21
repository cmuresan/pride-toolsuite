package uk.ac.ebi.pride.mzgraph.psm.match;

import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.mol.NeutralLoss;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.mzgraph.gui.data.Peak;
import uk.ac.ebi.pride.mzgraph.gui.data.PeakSet;
import uk.ac.ebi.pride.mzgraph.gui.data.TheoreticalFragmentedIonsTableModel;
import uk.ac.ebi.pride.mzgraph.psm.PSMParams;
import uk.ac.ebi.pride.mzgraph.psm.SpectraMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 05/11/12
 */

public class MaxSpectraMatcher implements SpectraMatcher {
    private IonAnnotation findAnnotation(double mz, List<IonAnnotation> annotationList) {
        if (annotationList == null) {
            return null;
        }

        for (IonAnnotation annotation : annotationList) {
            if (Double.compare(annotation.getMz().doubleValue(), mz) == 0) {
                return annotation;
            }
        }

        return null;
    }

    @Override
    public IonAnnotation[][] match(PeakSet peakSet, TheoreticalFragmentedIonsTableModel tableModel) {
        if (peakSet == null || tableModel == null) {
            return null;
        }

        double range = PSMParams.getInstance().getRange();

        IonAnnotation[][] autoData = new IonAnnotation[tableModel.getRowCount()][tableModel.getColumnCount()];
        List<IonAnnotation> autoAnnotations = new ArrayList<IonAnnotation>();

        double mz;
        double intensity;
        double theoretical;
        Object cell;
        ProductIon ion;
        IonAnnotation annotation;
        IonAnnotationInfo annotationInfo;
        int charge;
        FragmentIonType type;
        int location;
        NeutralLoss loss;
        PeakSet set;
        Peak peak;

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                cell = tableModel.getValueAt(row, col);
                if (cell instanceof ProductIon) {
                    ion = (ProductIon) cell;
                    theoretical = ion.getMassOverCharge();

                    set = peakSet.subSet(theoretical, range);
                    if (set.size() > 0) {
                        peak = set.getMaxIntensityPeak();
                        mz = peak.getMz();
                        intensity = peak.getIntensity();

                        charge = ion.getCharge();
                        location = ion.getPosition();
                        type = ion.getType().getGroup();
                        loss = ion.getType().getLoss();

                        annotation = findAnnotation(mz, autoAnnotations);
                        if (annotation == null) {
                            annotationInfo = new IonAnnotationInfo();
                            annotationInfo.addItem(charge, type, location, loss);
                            annotation = new IonAnnotation(mz, intensity, annotationInfo);
                            autoAnnotations.add(annotation);
                        } else {
                            annotationInfo = annotation.getAnnotationInfo();
                            annotationInfo.addItem(charge, type, location, loss);
                        }

                        autoData[row][col] = annotation;
                    }
                }
            }
        }

        return autoData;
    }
}
