package uk.ac.ebi.pride.gui.component.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.access.DataAccessMonitor;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * DataBrowser is the main display area for the data, it is responsible for the followings:
 * <p/>
 * 1. when a new data source is selected, it should rebuild itself to display the data.
 * <p/>
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 10:58:28
 */
public class DataContentBrowser extends JPanel implements PropertyChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(DataContentBrowser.class);

    /**
     * provide easy access to desktop context
     */
    private DataAccessController controller = null;

    /**
     * Reference to pride inspector context
     */
    private PrideInspectorContext inspectorContext;
    /**
     * welcome pane
     */
    private WelcomePane welcomePane = null;

    public DataContentBrowser() {
        setupMainPane();
        addComponents();

    }

    /**
     * Setup the main pane
     */
    private void setupMainPane() {
        this.setLayout(new BorderLayout());
        this.setForeground(Color.white);

        // add data content browser as property change listener to data access monitor
        inspectorContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        inspectorContext.addPropertyChangeListenerToDataAccessMonitor(this);
    }

    /**
     * Add the welcome pane
     */
    private void addComponents() {
        welcomePane = new WelcomePane();

        this.add(welcomePane, BorderLayout.CENTER);
    }

    /**
     * Monitoring the changes on foreground DataSourceController
     *
     * @param evt should be NEW_FOREGROUND_DATA_SOURCE_PROP.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessMonitor.NEW_FOREGROUND_DATA_SOURCE_PROP.equals(evtName)) {
            logger.debug("A new foreground data access controller has been selected");
            // add new tabs
            controller = (DataAccessController) evt.getNewValue();
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

    /**
     * Set data content display
     */
    private void setDataContentComponent() {
        // handle existing tab pane
        if (controller != null) {
            DataContentDisplayPane dataContentPane = (DataContentDisplayPane) inspectorContext.getDataContentPane(controller);
            if (dataContentPane == null) {
                DataContentDisplayPane contentDisplayPane = new DataContentDisplayPane(controller);
                addNewContentDisplayPane(contentDisplayPane, controller);
            } else {
                addNewContentDisplayPane(dataContentPane, null);
            }
        } else {
            addNewContentDisplayPane(welcomePane, null);
        }
    }

    /**
     * Add a new content display pane
     *
     * @param panel content display panel
     * @param con   data access controller which the content display panel belongs.
     */
    private void addNewContentDisplayPane(JComponent panel, DataAccessController con) {
        if (panel != null) {
            this.removeAll();
            this.add(panel, BorderLayout.CENTER);
            this.revalidate();
            this.repaint();
            if (con != null) {
                inspectorContext.addDataContentPane(con, panel);
            }
        }
    }
}
