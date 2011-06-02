package uk.ac.ebi.pride.gui.component.mzdata;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.mzgraph.MzGraphViewPane;
import uk.ac.ebi.pride.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.gui.task.TaskEvent;

import javax.swing.*;
import java.awt.*;

/**
 * MzTabPane displays MzGraph related data, such as: Spectra or Chromatogram
 * <p/>
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 15:08:48
 */
public class MzDataTabPane extends DataAccessControllerPane {

    private static final Logger logger = Logger.getLogger(MzDataTabPane.class.getName());

    /**
     * default split pane resize weight, this controls the resize behavior of the split pane
     */
    private static final double SPLIT_PANE_RESIZE_WEIGHT = 0.5;
    /**
     * Title for spectrum
     */
    private static final String SPECTRUM_TITLE = "Spectrum";
    /**
     * Title for chromatogram
     */
    private static final String CHROMATOGRAM_TITLE = "Chromatogram";

    /**
     * Reference to Desktop context
     */
    private PrideInspectorContext context;

    /**
     * Constructor
     *
     * @param controller data access controller
     * @param parentComp parent container
     */
    public MzDataTabPane(DataAccessController controller, JComponent parentComp) {
        super(controller, parentComp);
    }

    /**
     * Setup the main display pane and set the title
     */
    protected void setupMainPane() {
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();

        this.setLayout(new BorderLayout());

        // Tab Pane title
        try {
            String paneTitle = "";
            if (controller.hasSpectrum()) {
                paneTitle = SPECTRUM_TITLE;
                int ids = controller.getSpectrumIds().size();
                paneTitle += " (" + ids + ")";
            }
            if (controller.hasChromatogram()) {
                paneTitle += ((paneTitle.equals("")) ? "" : " & ") + CHROMATOGRAM_TITLE;
                int ids = controller.getChromatogramIds().size();
                paneTitle += " (" + ids + ")";
            }

            if ("".equals(paneTitle)) {
                paneTitle = SPECTRUM_TITLE + " (0)";
            }
            // set the title
            this.setTitle(paneTitle);
        } catch (DataAccessException dex) {
            String msg = String.format("%s failed on : %s", this, dex);
            logger.log(Level.ERROR, msg, dex);
            context.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));

        }
        // set the final icon
        this.setIcon(GUIUtilities.loadIcon(context.getProperty("mzdata.tab.icon.small")));

        // set the loading icon
        this.setLoadingIcon(GUIUtilities.loadIcon(context.getProperty("mzdata.tab.loading.icon.small")));
    }

    /**
     * Add mzdata selection pane and mzgraph pane
     */
    @Override
    public void populate() {
        // Selection pane to select different spectra or chromatogram
        MzDataSelectionPane mzSelectionPane = new MzDataSelectionPane(controller, this);

        // Display peak list or chromatogram
        MzGraphViewPane mzViewPane = new MzGraphViewPane(controller);
        mzViewPane.setPreferredSize(new Dimension(400, 500));
        mzSelectionPane.addPropertyChangeListener(mzViewPane);

        // add components to split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mzViewPane, mzSelectionPane);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setResizeWeight(SPLIT_PANE_RESIZE_WEIGHT);
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerSize(2);

        this.add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void started(TaskEvent event) {
        showIcon(getLoadingIcon());
    }

    @Override
    public void finished(TaskEvent event) {
        showIcon(getIcon());
    }

    /**
     * Show a different icon if the parent component is not null and an instance of DataContentDisplayPane
     *
     * @param icon icon to show
     */
    private void showIcon(Icon icon) {
        if (parentComponent != null && parentComponent instanceof ControllerContentPane && icon != null) {
            ControllerContentPane contentPane = (ControllerContentPane) parentComponent;
            contentPane.setTabIcon(contentPane.getMzDataTabIndex(), icon);
        }
    }
}
