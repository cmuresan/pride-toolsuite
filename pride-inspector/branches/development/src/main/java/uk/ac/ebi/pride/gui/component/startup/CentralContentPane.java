package uk.ac.ebi.pride.gui.component.startup;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.CentralContentPaneLockEvent;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.gui.event.ForegroundDataSourceEvent;
import uk.ac.ebi.pride.gui.event.ShowWelcomePaneEvent;
import uk.ac.ebi.pride.gui.task.impl.OpenWelcomePaneTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.EDTUtils;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.*;

/**
 * DataBrowser is the main display area for the data, it is responsible for the followings:
 * <p/>
 * 1. when a new data source is selected, it should rebuild itself to display the data.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 10:58:28
 */
public class CentralContentPane extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(CentralContentPane.class);

    private boolean locked = false;
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

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Setup the main pane
     */
    private void setupMainPane() {
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new BorderLayout());
        this.setForeground(Color.white);

        // add data content browser as property change listener to data access monitor
        inspectorContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
    }

    /**
     * Add the welcome pane
     */
    private void addComponents() {
        getWelcomePane();
    }

    /**
     * Open welcome pane
     */
    private void getWelcomePane() {
        OpenWelcomePaneTask task = new OpenWelcomePaneTask();
        task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));
        inspectorContext.addTask(task);
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
        DataAccessController controller = (DataAccessController) evt.getNewForegroundDataSource();
        // handle existing tab pane
        if (isLocked()) {
            // 1. open file
            // 2. select an experiment
            if (ForegroundDataSourceEvent.Status.DUMMY.equals(evt.getStatus()) ||
                    ForegroundDataSourceEvent.Status.DATA.equals(evt.getStatus())) {
                ControllerContentPane dataContentPane = getControllerContentPane(controller);
                setContentPane(dataContentPane);
            }
        } else {
            if (ForegroundDataSourceEvent.Status.EMPTY.equals(evt.getStatus())) {
                getWelcomePane();
            } else {
                ControllerContentPane dataContentPane = getControllerContentPane(controller);
                setContentPane(dataContentPane);
            }
        }
    }

    /**
     * Create an new controllerContentPane is non-exist.
     *
     * @param controller    data access controller
     * @return  ControllerContentPane   content pane
     */
    private ControllerContentPane getControllerContentPane(DataAccessController controller) {
        ControllerContentPane dataContentPane = (ControllerContentPane) inspectorContext.getDataContentPane(controller);
        if (dataContentPane == null) {
            dataContentPane = new ControllerContentPane(controller);
            inspectorContext.addDataContentPane(controller, dataContentPane);
        }
        return dataContentPane;
    }

    @EventSubscriber(eventClass = DatabaseSearchEvent.class)
    public void onDatabaseSearchEvent(DatabaseSearchEvent evt) {
        logger.debug("Database search pane is to be displayed");

        if (DatabaseSearchEvent.Status.SHOW.equals(evt.getStatus())) {
            // show database search pane
            setContentPane(inspectorContext.getDatabaseSearchPane());
            // lock the central content panel
            setLocked(true);
            // deselect the foreground data access controller
            inspectorContext.setForegroundDataAccessController(null);
        } else if (DatabaseSearchEvent.Status.HIDE.equals(evt.getStatus())) {
            // hide database search pane
            getWelcomePane();
        }
    }

    @EventSubscriber(eventClass = ShowWelcomePaneEvent.class)
    public synchronized void onShowWelcomePaneEvent(ShowWelcomePaneEvent evt) {
        logger.debug("Welcome pane is to be displayed");

        setContentPane(inspectorContext.getWelcomePane());
        setLocked(false);
    }

    @EventSubscriber(eventClass = CentralContentPaneLockEvent.class)
    public synchronized void onCentralContentPanelLockEvent(CentralContentPaneLockEvent evt) {
        logger.debug("Set the lock state of the central content panel");

        setLocked(CentralContentPaneLockEvent.Status.LOCK.equals(evt.getStatus()));
    }

    /**
     * Set the current display pane within the central content panel
     *
     * @param panel visible panel
     */
    public void setContentPane(final JComponent panel) {
        // code to run
        Runnable code = new Runnable() {
            public void run() {
                CentralContentPane.this.removeAll();
                CentralContentPane.this.add(panel, BorderLayout.CENTER);
                CentralContentPane.this.revalidate();
                CentralContentPane.this.repaint();
            }
        };

        // run on EDT
        EDTUtils.invokeLater(code);
    }
}
