package uk.ac.ebi.pride.mzgraph.gui.data;

/**
 * Creator: Qingwei-XU
 * Date: 24/10/12
 */
public interface ExperimentalTableModelObserver {
    /**
     * If ExperimentalFragmentedIonsDataset changed, such as add annotation, add peak,
     * and so on.
     */
    public void update(ExperimentalFragmentedIonsTableModel tableModel);
}
