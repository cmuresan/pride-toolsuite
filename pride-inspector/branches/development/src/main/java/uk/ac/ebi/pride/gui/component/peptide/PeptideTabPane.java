package uk.ac.ebi.pride.gui.component.peptide;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.mzdata.MzDataTabPane;
import uk.ac.ebi.pride.gui.component.mzgraph.MzGraphViewPane;
import uk.ac.ebi.pride.gui.component.startup.DataContentDisplayPane;
import uk.ac.ebi.pride.gui.task.TaskEvent;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * PeptideTabPane provides a peptide centric view to all peptides in one experiment.
 * <p/>
 * User: rwang
 * Date: 03-Sep-2010
 * Time: 10:46:06
 */
public class PeptideTabPane extends DataAccessControllerPane {

    private static final Logger logger = Logger.getLogger(MzDataTabPane.class.getName());

    /**
     * title
     */
    private static final String PEPTIDE_TITLE = "Peptide";
    /**
     * resize weight for inner split pane
     */
    private static final double INNER_SPLIT_PANE_RESIZE_WEIGHT = 0.6;
    /**
     * resize weight for outer split pane
     */
    private static final double OUTER_SPLIT_PANE_RESIZE_WEIGHT = 0.6;
    /**
     * the size of the divider for split pane
     */
    private static final int DIVIDER_SIZE = 2;

    /**
     * Inner split pane contains peptideDescPane and peptidePTMPane
     */
    private JSplitPane innerSplitPane;
    /**
     * Outer split pane contains inner split pane and mzViewPane
     */
    private JSplitPane outterSplitPane;
    /**
     * Display peptide details
     */
    private PeptideDescriptionPane peptideDescPane;
    /**
     * Display ptm details
     */
    private PeptidePTMPane peptidePTMPane;
    /**
     * mzgraph view pane
     */
    private MzGraphViewPane mzViewPane;

    /**
     * Indicate whether the top panel has been expanded
     */
    private boolean topPanelExpanded = false;

    /**
     * Constructor
     *
     * @param controller data access controller
     * @param parentComp parent container
     */
    public PeptideTabPane(DataAccessController controller, JComponent parentComp) {
        super(controller, parentComp);
    }

    /**
     * Setup the main display main
     */
    @Override
    protected void setupMainPane() {
        PrideInspectorContext context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();

        // set properties for IdentTabPane
        this.setLayout(new BorderLayout());
        // title for the tab pane
        try {
            int numberOfPeptides = controller.getNumberOfPeptides();
            String title = " (" + numberOfPeptides + ")";
            this.setTitle(PEPTIDE_TITLE + title);
        } catch (DataAccessException dex) {
            String msg = String.format("%s failed on : %s", this, dex);
            logger.log(Level.ERROR, msg, dex);
            context.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        // set the final icon
        this.setIcon(GUIUtilities.loadIcon(context.getProperty("peptide.tab.icon.small")));

        // set the loading icon
        this.setLoadingIcon(GUIUtilities.loadIcon(context.getProperty("peptide.tab.loading.icon.small")));
    }

    /**
     * Add the rest of components
     */
    @Override
    public void populate() {
        // inner split pane
        innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        innerSplitPane.setBorder(BorderFactory.createEmptyBorder());
        innerSplitPane.setOneTouchExpandable(false);
        innerSplitPane.setDividerSize(DIVIDER_SIZE);
        innerSplitPane.setDividerSize(0);
        innerSplitPane.setResizeWeight(INNER_SPLIT_PANE_RESIZE_WEIGHT);

        // outer split pane
        outterSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        outterSplitPane.setBorder(BorderFactory.createEmptyBorder());
        outterSplitPane.setOneTouchExpandable(false);
        outterSplitPane.setResizeWeight(OUTER_SPLIT_PANE_RESIZE_WEIGHT);

        // protein identification selection pane
        peptideDescPane = new PeptideDescriptionPane(controller);
        outterSplitPane.setTopComponent(peptideDescPane);

        // peptide selection pane
        peptidePTMPane = new PeptidePTMPane(controller);
        peptidePTMPane.setMinimumSize(new Dimension(200, 150));
        peptideDescPane.addPropertyChangeListener(peptidePTMPane);
        innerSplitPane.setTopComponent(peptidePTMPane);


        // Spectrum view pane
        mzViewPane = new MzGraphViewPane(controller);
        mzViewPane.setVisible(false);
        peptidePTMPane.addPropertyChangeListener(mzViewPane);

        peptideDescPane.addPropertyChangeListener(this);
        innerSplitPane.setBottomComponent(mzViewPane);
        outterSplitPane.setBottomComponent(innerSplitPane);

        this.add(outterSplitPane, BorderLayout.CENTER);
    }

    /**
     * Get a reference to peptide pane
     *
     * @return PeptideDescriptionPane   peptide pane
     */
    public PeptideDescriptionPane getPeptidePane() {
        return peptideDescPane;
    }

    /**
     * Triggered when a new peptide has been selected
     * It sets the visibility of both the ptm pane and mz view pane
     *
     * @param evt property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        String evtName = evt.getPropertyName();

        if (DataAccessController.PEPTIDE_TYPE.equals(evtName)) {
            // set the visibility of the peptidePTMPane
            Peptide peptide = (Peptide) evt.getNewValue();
            if (peptide != null) {
                java.util.List<Modification> mods = peptide.getModifications();
                boolean peptidePTMPaneVisibility = (mods != null && !mods.isEmpty());
                setPeptidePTMPaneVisibility(peptidePTMPaneVisibility);

                // set the visibility of the mzGraph pane
                MzGraph mzGraph = peptide.getSpectrum();
                boolean mzGraphPaneVisibility = (mzGraph != null && mzGraph.getBinaryDataArrays() != null);
                setMzGraphPaneVisibility(mzGraphPaneVisibility);

                if (!topPanelExpanded) {
                    setInnerPaneVisibility(peptidePTMPaneVisibility || mzGraphPaneVisibility);
                    innerSplitPane.setDividerSize(peptidePTMPaneVisibility && mzGraphPaneVisibility ? DIVIDER_SIZE : 0);
                }
            } else {
                setPeptidePTMPaneVisibility(false);
                setMzGraphPaneVisibility(false);
                setInnerPaneVisibility(false);
            }
        } else if (PeptideDescriptionPane.EXPAND_PEPTIDE_PANEl.equals(evt.getPropertyName())) {
            topPanelExpanded = !topPanelExpanded;
            setInnerPaneVisibility(!topPanelExpanded);
        }
    }

    /**
     * Set the visibility of the ptm pane
     *
     * @param visibility true is visible
     */
    private void setPeptidePTMPaneVisibility(boolean visibility) {
        peptidePTMPane.setVisible(visibility);
        outterSplitPane.resetToPreferredSizes();
    }

    private void setInnerPaneVisibility(boolean visibility) {
        innerSplitPane.setVisible(visibility);
        outterSplitPane.setDividerSize(visibility ? DIVIDER_SIZE : 0);
        outterSplitPane.resetToPreferredSizes();
    }

    /**
     * Set the visibility of the mzgraph pane
     *
     * @param visibility true is visible
     */
    private void setMzGraphPaneVisibility(boolean visibility) {
        mzViewPane.setVisible(visibility);
        outterSplitPane.resetToPreferredSizes();
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
        if (parentComponent != null && parentComponent instanceof DataContentDisplayPane && icon != null) {
            DataContentDisplayPane contentPane = (DataContentDisplayPane) parentComponent;
            contentPane.setTabIcon(contentPane.getPeptideTabIndex(), icon);
        }
    }
}
