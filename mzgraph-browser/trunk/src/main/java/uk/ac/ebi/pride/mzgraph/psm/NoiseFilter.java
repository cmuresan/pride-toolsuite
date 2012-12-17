package uk.ac.ebi.pride.mzgraph.psm;

import uk.ac.ebi.pride.iongen.model.PeakSet;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

/**
 * Creator: Qingwei-XU
 * Date: 02/11/12
 */
public interface NoiseFilter {
    public PeakSet filterNoise(PeakSet peaks, ExperimentalFragmentedIonsTableModel tableModel);
}
