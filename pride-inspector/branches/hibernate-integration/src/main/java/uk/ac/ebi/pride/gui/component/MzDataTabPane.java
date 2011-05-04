package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.component.mzgraph.MzGraphViewPane;

import javax.swing.*;
import java.awt.*;

/**
 * MzTabPane displays MzGraph related data, such as: Spectra or Chromatograms
 *
 * It listens to the following property change event:
 * 1. FOREGROUND_EXPERIMENT_CHANGED, update the tab pane title and check for the
 * visibility of this tab.
 * 
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 15:08:48
 */
public class MzDataTabPane extends DataAccessControllerPane{

    private static final double SPLIT_PANE_RESIZE_WEIGHT = 0.7;

    public MzDataTabPane(DataAccessController controller) {
        super(controller);
    }

    protected void setupMainPane() {
        this.setLayout(new BorderLayout());
        // Tab Pane title
        // ToDo: find a better way here
        String paneTitle = "";
        if (controller.hasSpectrum()) {
            paneTitle = DataAccessController.SPECTRUM_TYPE;
        }
        if (controller.hasChromatogram()) {
            paneTitle += ((paneTitle.equals("")) ? "" : " & ") + DataAccessController.CHROMATOGRAM_TYPE;
        }
        this.setName(paneTitle);
        // visibility
        this.setVisible(controller.hasSpectrum() || controller.hasChromatogram());
    }

    protected void addComponents() {
        // Selection pane to select different spectra or chromatogram
        MzDataSelectionPane mzSelectionPane = new MzDataSelectionPane(controller);

        // Display peak list or chromatogram
        MzGraphViewPane mzViewPane = new MzGraphViewPane();
        mzSelectionPane.addPropertyChangeListener(mzViewPane);

        // add components to split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mzViewPane, mzSelectionPane);
        splitPane.setResizeWeight(SPLIT_PANE_RESIZE_WEIGHT);
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerSize(5);

        this.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Update tab pane title and visibility when the foreground experiment changed.
     */
    @Override
    protected void updatePropertyChange() {
        setupMainPane();
    }
}
