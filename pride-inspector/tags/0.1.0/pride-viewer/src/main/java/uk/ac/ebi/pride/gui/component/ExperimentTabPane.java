package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 16:22:21
 */
public class ExperimentTabPane extends JPanel implements PropertyChangeListener {
    private DataAccessController controller = null;
    
    public ExperimentTabPane(DataAccessController controller) {
        this.setLayout(new BorderLayout());
        this.controller = controller;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();

        if (DataAccessController.FOREGROUND_EXPERIMENT_CHANGED.equals(evtName)) {
            // ToDo: repaint myself
        }
    }
}
