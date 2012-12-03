package uk.ac.ebi.pride.mzgraph.psm;

import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.mzgraph.gui.data.PeakSet;

/**
 * Creator: Qingwei-XU
 * Date: 05/11/12
 */

public interface SpectraMatcher {
    public IonAnnotation[][] match(PeakSet peakSet, ExperimentalFragmentedIonsTableModel tableModel);
}
