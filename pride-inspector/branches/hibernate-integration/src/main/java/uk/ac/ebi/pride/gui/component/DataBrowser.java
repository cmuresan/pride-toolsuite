package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * DataBrowser is the main display area for the data, it is responsible for the followings:
 *
 * 1. when a new data source is selected, it should rebuild itself to display the data.
 *
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 10:58:28
 */
public class DataBrowser extends JPanel implements PropertyChangeListener {
    /** provide easy access to desktop context */
    private PrideViewerContext viewerContext = null;
    private DataAccessController controller = null;
    private JTabbedPane tabPane = null;
    private static final int MZGRAPH_TAB = 1;
    private static final int IDENTIFICATION_TAB = 2;

    public DataBrowser() {
        setupMainPane();    
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
        this.setForeground(Color.white);
        this.viewerContext = (PrideViewerContext)Desktop.getInstance().getDesktopContext();
    }

    /**
     * Monitoring the changes on foreground DataSourceController
     * @param evt   should be NEW_FOREGROUND_DATA_SOURCE_PROP.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessMonitor.NEW_FOREGROUND_DATA_SOURCE_PROP.equals(evtName)) {
            // add new tabs
            controller = (DataAccessController)evt.getNewValue();
            if (SwingUtilities.isEventDispatchThread()) {
                setTabComponents();
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        setTabComponents();
                    }
                };
                EventQueue.invokeLater(eventDispatcher);
            }
        } else if (DataAccessController.FOREGROUND_EXPERIMENT_CHANGED.equals(evtName)) {
            if (SwingUtilities.isEventDispatchThread()) {
                setTabVisibility();
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        setTabVisibility();
                    }
                };
                EventQueue.invokeLater(eventDispatcher);
            }
        }
    }

    private void setTabVisibility() {
        tabPane.setEnabledAt(MZGRAPH_TAB, (controller.hasChromatogram() || controller.hasSpectrum()));
        tabPane.setEnabledAt(IDENTIFICATION_TAB, (controller.hasIdentification()));
    }

    private void setTabComponents() {
        // handle existing tab pane
        this.removeAll();

        if (controller != null) {
            tabPane = (JTabbedPane) viewerContext.getDataBrowser(controller);
            if (tabPane == null) {
                // create new tabs
                tabPane = createTabbedPane();
                setTabVisibility();
                // save the tab pane
                viewerContext.addDataBrowser(controller, tabPane);
            }
            DataBrowser.this.add(tabPane, BorderLayout.CENTER);
            DataBrowser.this.repaint();
        }
    }

    /**
     * 
     * @return
     */
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabPane = new JTabbedPane();

        // the type of data contains experiment data
        MetaDataTabPane metaDataTabPane = new MetaDataTabPane(controller);
        tabPane.add(metaDataTabPane.getName(), metaDataTabPane);
        // spectra data tab
        MzDataTabPane spectraTabPane = new MzDataTabPane(controller);
        tabPane.add(spectraTabPane.getName(), spectraTabPane);
        // identification data tab
        IdentTabPane identTabPane = new IdentTabPane(controller);
        tabPane.add(identTabPane.getName(), identTabPane);
        return tabPane;
    }
}
