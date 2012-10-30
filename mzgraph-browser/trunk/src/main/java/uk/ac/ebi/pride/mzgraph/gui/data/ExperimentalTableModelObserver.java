package uk.ac.ebi.pride.mzgraph.gui.data;

/**
 * Creator: Qingwei-XU
 * Date: 24/10/12
 */
public interface ExperimentalTableModelObserver {
    /**
     * This is observer pattern. Every experimental table model observer will check
     * {@link ExperimentalFragmentedIonsDataset} content change, such as call following methods:
     * {@link ExperimentalFragmentedIonsTableModel#addManualAnnotation(uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation)},
     * {@link ExperimentalFragmentedIonsTableModel#setPeaks(double[], double[])},
     * {@link ExperimentalFragmentedIonsTableModel#setRange(double)},
     * and so on. System will call
     * {@link uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel#notifyObservers()}
     * to update all observers contents.
     */
    public void update(ExperimentalFragmentedIonsTableModel tableModel);
}
