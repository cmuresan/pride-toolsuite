package uk.ac.ebi.pride.gui.component.ident;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.DataContentDisplayPane;
import uk.ac.ebi.pride.gui.component.mzdata.MzDataTabPane;
import uk.ac.ebi.pride.gui.component.mzgraph.MzGraphViewPane;

import javax.swing.*;
import java.awt.*;

/**
 * IdentTabPane displays protein identification and peptide related information.
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 16:23:02
 */
public class IdentTabPane extends DataAccessControllerPane {

    private static final Logger logger = Logger.getLogger(MzDataTabPane.class.getName());

    private static final String PANE_TITLE = "Identification";
    private static final double INNER_SPLIT_PANE_RESIZE_WEIGHT = 0.6;
    private static final double OUTER_SPLIT_PANE_RESIZE_WEIGHT = 0.6;


    public IdentTabPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        // set properties for IdentTabPane
        this.setLayout(new BorderLayout());
        resetTitle();
    }

    private void resetTitle() {
        try {
            int ids = controller.getNumberOfIdentifications();
            PrideViewerContext viewerContext = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
            DataContentDisplayPane dataContentPane = (DataContentDisplayPane) viewerContext.getDataContentPane(controller);
            String numIdent = "";
            if (ids != 0){
                numIdent = " (" + ids + ")";
            }

            if (dataContentPane != null){
                dataContentPane.setTabTitle(DataContentDisplayPane.IDENTIFICATION_TAB, PANE_TITLE + numIdent);
            }
            else{
                this.setTitle(PANE_TITLE + numIdent);
            }
        } catch (DataAccessException dex) {
            String msg = String.format("%s failed on : %s", this, dex);
            logger.log(Level.ERROR, msg, dex); //To change body of catch statement use File | Settings | File Templates.

        }
    }

    @Override
    protected void addComponents() {
        // protein identification selection pane
        IdentificationSelectionPane identPane = new IdentificationSelectionPane(controller);
        // peptide selection pane
        PeptideSelectionPane peptidePane = new PeptideSelectionPane(controller);
        identPane.addPropertyChangeListener(peptidePane);
        // Spectrum view pane
        MzGraphViewPane mzViewPane = new MzGraphViewPane();
        peptidePane.addPropertyChangeListener(mzViewPane);

        JSplitPane innerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, identPane, peptidePane);
        innerPane.setBorder(BorderFactory.createEmptyBorder());
        innerPane.setOneTouchExpandable(false);
        innerPane.setDividerSize(5);
        innerPane.setResizeWeight(INNER_SPLIT_PANE_RESIZE_WEIGHT);
        JSplitPane outerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, innerPane, mzViewPane);
        outerPane.setBorder(BorderFactory.createEmptyBorder());
        outerPane.setOneTouchExpandable(false);
        outerPane.setDividerSize(5);
        outerPane.setResizeWeight(OUTER_SPLIT_PANE_RESIZE_WEIGHT);
        this.add(outerPane, BorderLayout.CENTER);
    }

    @Override
    protected void updatePropertyChange() {
        resetTitle();
    }
}
