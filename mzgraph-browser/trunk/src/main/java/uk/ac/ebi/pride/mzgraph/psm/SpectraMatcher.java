package uk.ac.ebi.pride.mzgraph.psm;

import uk.ac.ebi.pride.iongen.model.PeakSet;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

/**
 * Creator: Qingwei-XU
 * Date: 05/11/12
 */

public interface SpectraMatcher {
    public IonAnnotation[][] match(PeakSet peakSet, ExperimentalFragmentedIonsTableModel tableModel);
}
