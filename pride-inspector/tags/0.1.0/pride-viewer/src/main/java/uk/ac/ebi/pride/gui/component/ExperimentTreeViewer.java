package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.component.model.ExperimentTreeModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * //ToDo: actionListener
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 10:42:46
 */
public class ExperimentTreeViewer extends JPanel implements PropertyChangeListener {

    private JTree experimentTree = null;

    public ExperimentTreeViewer() {
        experimentTree = new JTree();
        experimentTree.setVisible(false);
        this.setBorder(BorderFactory.createEtchedBorder());
        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());
        this.add(experimentTree, BorderLayout.CENTER);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessMonitor.NEW_FOREGROUND_DATA_SOURCE_PROP.equals(evtName)) {
            DataAccessController newController = (DataAccessController)evt.getNewValue();
            if (newController != null && newController.isExperimentFriendly()) {
                // new controller
                System.out.println("Set new tree model");
                try {
                    experimentTree.setModel(new ExperimentTreeModel(newController.getExperimentIds()));
                    experimentTree.setVisible(true);
                } catch(DataAccessException dex) {
                    // ToDo: need to be optimized: Exception
                    dex.printStackTrace();
                }
            } else {
                experimentTree.setVisible(false);
            }
            this.getParent().repaint();
        }
    }
}
