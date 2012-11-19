package uk.ac.ebi.pride.mzgraph.psm;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.mzgraph.gui.data.PeakSet;

/**
 * Creator: Qingwei-XU
 * Date: 02/11/12
 */
public interface NoiseFilter {
    public PeakSet filterNoise(PeakSet peaks, ExperimentalFragmentedIonsTableModel tableModel);
}
