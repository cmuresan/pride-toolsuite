package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.component.model.ExperimentTreeModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 * //ToDo: actionListener
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 10:42:46
 */
public class ExperimentTreeViewer extends JPanel implements PropertyChangeListener, TreeSelectionListener {

    private JTree experimentTree = null;
    private DataAccessController controller = null;

    public ExperimentTreeViewer() {
        experimentTree = new JTree();
        experimentTree.setVisible(false);
        experimentTree.addTreeSelectionListener(this);
        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());
        this.add(experimentTree, BorderLayout.CENTER);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessMonitor.NEW_FOREGROUND_DATA_SOURCE_PROP.equals(evtName)) {
            controller = (DataAccessController)evt.getNewValue();
            if (controller != null) {
                // new controller
                try {
                    Collection<Comparable> expIds = controller.getExperimentIds();
                    if (expIds != null && expIds.size() > 0) {
                        experimentTree.setModel(new ExperimentTreeModel(expIds));
                        experimentTree.setVisible(true);
                    }
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


    @Override
    public void valueChanged(TreeSelectionEvent e) {
        String node = (String)experimentTree.getLastSelectedPathComponent();

        if (node != null) {
            try {
                controller.setForegroundExperimentId(node);
            } catch (DataAccessException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
