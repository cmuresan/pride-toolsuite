package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
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
        this.setName(PANE_TITLE);
        this.setVisible(controller.hasIdentification());
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
        innerPane.setOneTouchExpandable(false);
        innerPane.setDividerSize(5);
        innerPane.setResizeWeight(INNER_SPLIT_PANE_RESIZE_WEIGHT);
        JSplitPane outerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, innerPane, mzViewPane);
        outerPane.setOneTouchExpandable(false);
        outerPane.setDividerSize(5);
        outerPane.setResizeWeight(OUTER_SPLIT_PANE_RESIZE_WEIGHT);
        this.add(outerPane, BorderLayout.CENTER);
    }

    @Override
    protected void updatePropertyChange() {
        setupMainPane();    
    }
}
