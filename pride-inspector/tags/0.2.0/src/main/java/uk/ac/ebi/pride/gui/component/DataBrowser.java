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
    private DataAccessController controller = null;

    public DataBrowser() {
        setupMainPane();
        PrideViewerContext viewerContext = (PrideViewerContext)Desktop.getInstance().getDesktopContext();
        viewerContext.getDataAccessMonitor().addPropertyChangeListener(this);
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
        this.setForeground(Color.white);
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
                setDataContentComponent();
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        setDataContentComponent();
                    }
                };
                EventQueue.invokeLater(eventDispatcher);
            }
        }
    }

    private void setDataContentComponent() {
        // get context
        PrideViewerContext viewerContext = (PrideViewerContext)Desktop.getInstance().getDesktopContext();
        // handle existing tab pane
        this.removeAll();

        if (controller != null) {
            DataContentDisplayPane dataContentPane = (DataContentDisplayPane) viewerContext.getDataContentPane(controller);
            if (dataContentPane == null) {
                // create new tabs
                dataContentPane = new DataContentDisplayPane(controller);
                // save the tab pane
                viewerContext.addDataContentPane(controller, dataContentPane);
            }
            DataBrowser.this.add(dataContentPane, BorderLayout.CENTER);
        }
        DataBrowser.this.revalidate();
        DataBrowser.this.repaint();
    }

}
