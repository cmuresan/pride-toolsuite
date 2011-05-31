package uk.ac.ebi.pride.gui.component.startup;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.access.DataAccessMonitor;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.gui.event.ForegroundDataSourceEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * DataBrowser is the main display area for the data, it is responsible for the followings:
 * <p/>
 * 1. when a new data source is selected, it should rebuild itself to display the data.
 * <p/>
 *
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 10:58:28
 */
public class CentralContentPane extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(CentralContentPane.class);

    /**
     * provide easy access to desktop context
     */
    private DataAccessController controller = null;

    /**
     * Reference to pride inspector context
     */
    private PrideInspectorContext inspectorContext;

    public CentralContentPane() {
        // enable annotation
        AnnotationProcessor.process(this);

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
    }

    /**
     * Add the welcome pane
     */
    private void addComponents() {
        // set welcome pane, since this will be the first it's used
        WelcomePane welcomePane = new WelcomePane();
        this.add(welcomePane, BorderLayout.CENTER);
        inspectorContext.setWelcomePane(welcomePane);
    }

    /**
     * Monitoring the changes on foreground DataSourceController
     *
     * @param evt should be ForegroundDataSourceEvent from EventBus.
     */
    @EventSubscriber(eventClass = ForegroundDataSourceEvent.class)
    public void onForegroundDataSourceEvent(ForegroundDataSourceEvent evt) {
        logger.debug("A new foreground data access controller has been selected");
        // add new tabs
        controller = (DataAccessController) evt.getNewForegroundDataSource();
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

    @EventSubscriber(eventClass = DatabaseSearchEvent.class)
    public void onDatabaseSearchEvent(DatabaseSearchEvent evt) {
        if (SwingUtilities.isEventDispatchThread()) {
            setContentPane(inspectorContext.getDatabaseSearchPane());
        } else {
            Runnable eventDispatcher = new Runnable() {
                public void run() {
                    setContentPane(inspectorContext.getDatabaseSearchPane());
                }
            };
            EventQueue.invokeLater(eventDispatcher);
        }
    }

    /**
     * Set data content display
     */
    private void setDataContentComponent() {
        // handle existing tab pane
        if (controller != null) {
            DataAccessControllerContentPane dataContentPane = (DataAccessControllerContentPane) inspectorContext.getDataContentPane(controller);
            if (dataContentPane == null) {
                dataContentPane = new DataAccessControllerContentPane(controller);
                inspectorContext.addDataContentPane(controller, dataContentPane);
            }
            setContentPane(dataContentPane);
        } else {
            boolean isDataAccessControllerContentPane = false;
            Component[] components = this.getComponents();
            for (Component component : components) {
                if (component instanceof  DataAccessControllerContentPane) {
                    isDataAccessControllerContentPane = true;
                    break;
                }
            }

            if (isDataAccessControllerContentPane) {
                setContentPane(inspectorContext.getWelcomePane());
            }
        }
    }

    /**
     * Set the current display pane within the central content panel
     *
     * @param panel visible panel
     */
    public void setContentPane(JComponent panel) {
        this.removeAll();
        this.add(panel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }
}
