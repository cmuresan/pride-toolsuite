package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.PrideGUIContext;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * DataViewer is the main display area for the data.
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 10:58:28
 */
public class DataViewer extends JPanel implements PropertyChangeListener {
    /** Data are arranged in tabs and displayed in TabbedPane */
    private JTabbedPane viewPane = null;

    public DataViewer() {
        this.setLayout(new BorderLayout());
        this.setForeground(Color.white);
    }

    private void addViewPane(JTabbedPane tabPane) {
        if (tabPane != null) {
            clearViewPane();
            this.viewPane = tabPane;
            this.add(tabPane, BorderLayout.CENTER);
            this.repaint();
        }
    }

    private void clearViewPane() {
        if (viewPane != null) {
            this.remove(viewPane);
            viewPane = null;
        }
    }

    /**
     * Monitoring the changes on foreground DataSourceController
     * @param evt
     */
    @Override
    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessMonitor.NEW_FOREGROUND_DATA_SOURCE_PROP.equals(evtName)) {
            // add new tabs
            DataAccessController newController = (DataAccessController)evt.getNewValue();
            if (newController != null) {
                SwingUtilities.invokeLater(new BuildTabsRunnable(newController));
            }
        } else if (DataAccessMonitor.REMOVE_DATA_SOURCE_PROP.equals(evtName)) {
            // ToDo: need to finish this implementation.
            // get the controller to be removed
            java.util.List<DataAccessController> oldControllers = (java.util.List<DataAccessController>)evt.getOldValue();
            java.util.List<DataAccessController> newControllers = (java.util.List<DataAccessController>)evt.getNewValue();
            oldControllers.removeAll(newControllers);
            DataAccessController oldController = oldControllers.get(0);

            if (oldController != null) {
                PrideGUIContext guiContext = (PrideGUIContext)Desktop.getInstance().getDesktopContext();
                guiContext.removeDataViewPane(oldController);
            }
        }
    }


    private void addTabs(JTabbedPane tabbedPane, DataAccessController controller) {
        // the type of data contains experiment data
        MetaDataTabPane metaDataTabPane = new MetaDataTabPane(controller);
        if (controller instanceof AbstractDataAccessController) {
            ((AbstractDataAccessController)controller).addPropertyChangeListener(metaDataTabPane);
        }
        tabbedPane.add(metaDataTabPane.getName(), metaDataTabPane);

        // spectra data tab
        if (controller.isSpectrumFriendly()) {
            // create spectra view tab
            MzDataTabPane spectraTabPane = new MzDataTabPane(controller);
            tabbedPane.add(spectraTabPane.getName(), spectraTabPane);
        }
        // identification data tab
        if (controller.isIdentificationFriendly()) {
            IdentTabPane identTabPane = new IdentTabPane(controller);
            tabbedPane.add(identTabPane.getName(), identTabPane);
        }
    }

    /**
     * This internal class is designed to run on EDT
     */
    private class BuildTabsRunnable implements Runnable {
        private DataAccessController controller = null;
        private PrideGUIContext guiContext = null;

        private BuildTabsRunnable(DataAccessController controller) {
            this.controller = controller;
            this.guiContext = (PrideGUIContext)Desktop.getInstance().getDesktopContext();
        }

        @Override
        public void run() {
            // handle existing tab pane
            clearViewPane();
            
            JComponent existingTabPane = guiContext.getDataViewPane(controller);
            if (existingTabPane == null) {
                // create new tabs
                JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP);
                addViewPane(tabPane);
                addTabs(tabPane, controller);
                guiContext.addDataViewPane(controller, tabPane);
            } else {
                // reload the existing tabs
                addViewPane((JTabbedPane)existingTabPane);
            }
        }
    }

}
