package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * IdentTabPane displays protein identification and peptide related information.
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 16:23:02
 */
public class IdentTabPane extends JPanel implements PropertyChangeListener {
    private DataAccessController controller = null;
    private static final String PANE_TITLE = "Identification";
    private static final double INNER_SPLIT_PANE_RESIZE_WEIGHT = 0.6;
    private static final double OUTER_SPLIT_PANE_RESIZE_WEIGHT = 0.6;


    public IdentTabPane(DataAccessController controller) {

        this.controller = controller;
        initialize();
    }

    private void initialize() {
        if (SwingUtilities.isEventDispatchThread()) {
            addComponents();
        } else {
            Runnable eventDispatcher = new Runnable() {
                public void run() {
                    addComponents();
                }
            };
            SwingUtilities.invokeLater(eventDispatcher);
        }
    }

    /**
     * This method should be called on ETD
     */
    private void addComponents() {
        // set properties for IdentTabPane
        this.setLayout(new BorderLayout());
        this.setName(PANE_TITLE);

        // protein identification selection pane
        IdentificationSelectionPane identPane = new IdentificationSelectionPane(controller);
        // peptide selection pane
        PeptideSelectionPane peptidePane = new PeptideSelectionPane(controller);
        identPane.addIdentificationListener(peptidePane);
        // Spectrum view pane
        MzGraphViewPane mzViewPane = new MzGraphViewPane();
        peptidePane.addPeptideChangeListener(mzViewPane);

        JSplitPane innerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, identPane, peptidePane);
        innerPane.setOneTouchExpandable(true);
        innerPane.setResizeWeight(INNER_SPLIT_PANE_RESIZE_WEIGHT);
        JSplitPane outerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, innerPane, mzViewPane);
        outerPane.setOneTouchExpandable(true);
        outerPane.setResizeWeight(OUTER_SPLIT_PANE_RESIZE_WEIGHT);
        this.add(outerPane, BorderLayout.CENTER);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessController.FOREGROUND_EXPERIMENT_CHANGED.equals(evtName)) {

        }
    }
}
