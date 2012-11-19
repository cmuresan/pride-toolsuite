package uk.ac.ebi.pride.mzgraph.psm;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

/**
 * Creator: Qingwei-XU
 * Date: 06/11/12
 */

public interface ConflictFilter {
    public IonAnnotation[][] filterConflict(IonAnnotation[][] src, ExperimentalFragmentedIonsTableModel tableModel);
}
