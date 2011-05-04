package uk.ac.ebi.pride.gui.component.mzdata;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.DataContentDisplayPane;
import uk.ac.ebi.pride.gui.component.mzgraph.MzGraphViewPane;

import javax.swing.*;
import java.awt.*;

/**
 * MzTabPane displays MzGraph related data, such as: Spectra or Chromatograms
 * <p/>
 * It listens to the following property change event:
 * 1. FOREGROUND_EXPERIMENT_CHANGED, update the tab pane title and check for the
 * visibility of this tab.
 * <p/>
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 15:08:48
 */
public class MzDataTabPane extends DataAccessControllerPane {

    private static final Logger logger = Logger.getLogger(MzDataTabPane.class.getName());

    private static final double SPLIT_PANE_RESIZE_WEIGHT = 0.7;

    public MzDataTabPane(DataAccessController controller) {
        super(controller);
    }

    protected void setupMainPane() {
        this.setLayout(new BorderLayout());
        resetTitle();
    }

    private void resetTitle() {
        // Tab Pane title
        // ToDo: find a better way here
        // default title is spectrum even there is neither spectrum nor chromatogram
        try {
            String paneTitle = DataAccessController.SPECTRUM_TYPE;
            if (controller.hasSpectrum()) {
                int ids = controller.getSpectrumIds().size();
                paneTitle += " (" + ids + ")";
            }
            if (controller.hasChromatogram()) {
                paneTitle += ((paneTitle.equals("")) ? "" : " & ") + DataAccessController.CHROMATOGRAM_TYPE;
                int ids = controller.getChromatogramIds().size();
                paneTitle += " (" + ids + ")";          
            }

            PrideViewerContext viewerContext = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
            DataContentDisplayPane dataContentPane = (DataContentDisplayPane) viewerContext.getDataContentPane(controller);
            if (dataContentPane != null) {
                dataContentPane.setTabTitle(DataContentDisplayPane.MZGRAPH_TAB, paneTitle);
            } else {
                this.setTitle(paneTitle);
            }
        }
        catch (DataAccessException dex) {
            String msg = String.format("%s failed on : %s", this, dex);
            logger.log(Level.ERROR, msg, dex); //To change body of catch statement use File | Settings | File Templates.

        }
    }

    protected void addComponents() {
        // Selection pane to select different spectra or chromatogram
        MzDataSelectionPane mzSelectionPane = new MzDataSelectionPane(controller);

        // Display peak list or chromatogram
        MzGraphViewPane mzViewPane = new MzGraphViewPane();
        mzSelectionPane.addPropertyChangeListener(mzViewPane);

        // add components to split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mzViewPane, mzSelectionPane);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
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
        resetTitle();
    }
}
