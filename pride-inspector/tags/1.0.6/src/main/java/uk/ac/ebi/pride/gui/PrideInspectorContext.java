package uk.ac.ebi.pride.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.cache.CachedMap;
import uk.ac.ebi.pride.gui.access.DataAccessMonitor;
import uk.ac.ebi.pride.gui.component.startup.DataSourceBrowser;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.task.TaskManager;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Overall context of the GUI, this object should only have one instance per application.
 * <p/>
 * It contains all the information which is shared by the whole application
 * <p/>
 * This class contains a list of delegate methods, you should use the methods provided here when possible
 * <p/>
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 16:45:38
 */
public class PrideInspectorContext extends DesktopContext {
    private static final Logger logger = LoggerFactory.getLogger(PrideInspector.class);

    /**
     * triggered when the visibility property of data source browser is changed
     */
    public static final String DATA_SOURCE_BROWSER_VISIBILITY = "dataSourceBrowserVisible";

    /**
     * default map size to store protein names
     */
    public static final int DEFAULT_PROTEIN_NAME_TRACKER_SIZE = 10000;

    /**
     * DataAccessMonitor manages a list of
     */
    private final DataAccessMonitor dataAccessMonitor;

    /**
     * This map maintains a reference between DataAccessController and its data content display pane
     */
    private final Map<DataAccessController, JComponent> dataContentPaneCache;

    /**
     * Data source browser
     */
    private DataSourceBrowser dataSourceBrowser;

    /**
     * data source browser visibility
     */
    private boolean dataSourceBrowserVisible;

    /**
     * Protein name tracker
     */
    private Map<String, String> proteinNameTracker;

    /**
     * The main help set for PRIDE Inspector
     */
    private HelpSet mainHelpSet;

    /**
     * The main help broker
     */
    private HelpBroker mainHelpBroker;

//    /** memory usage timer */
//    private Timer memoryUsageCheckTimer;

    /**
     * The path to open file
     */
    private String openFilePath;

    /**
     * Constructor
     */
    public PrideInspectorContext() {
        // instantiate data access monitor
        this.dataAccessMonitor = new DataAccessMonitor();

        // data content pane cache
        this.dataContentPaneCache = Collections.synchronizedMap(new HashMap<DataAccessController, JComponent>());

        // protein name tracker
        this.proteinNameTracker = Collections.synchronizedMap(new CachedMap<String, String>(DEFAULT_PROTEIN_NAME_TRACKER_SIZE));

        // by default the data source browser is invisible
        this.dataSourceBrowserVisible = false;

//        // start the memory usage check timer
//        this.memoryUsageCheckTimer = new Timer(1000, new MemoryUsageListener());
//
//        // start the timer
//        memoryUsageCheckTimer.start();
        //
        this.setOpenFilePath(System.getProperty("user.dir"));
    }

    /**
     * Get data access monitor
     *
     * @return DataAccessMonitor    data access monitor manages all data access controllers
     */
    public final DataAccessMonitor getDataAccessMonitor() {
        return dataAccessMonitor;
    }

    /**
     * Add a property change listener
     *
     * @param listener
     */
    public final void addPropertyChangeListenerToDataAccessMonitor(PropertyChangeListener listener) {
        dataAccessMonitor.addPropertyChangeListener(listener);
    }

    /**
     * Get a list of existing data access controllers
     * <p/>
     * Delegate method
     *
     * @return List<DataAccessController>   a list of existing data access controller
     */
    public final synchronized List<DataAccessController> getControllers() {
        return dataAccessMonitor.getControllers();
    }

    /**
     * Get the number of existing data access controllers
     * <p/>
     * Delegate method
     *
     * @return int  the number of data access controllers
     */
    public final synchronized int getNumberOfControllers() {
        return dataAccessMonitor.getNumberOfControllers();
    }

    /**
     * Check whether the data access controller is a foreground data access controller
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     * @return boolean  true if it is data access controller
     */
    public final synchronized boolean isForegroundDataAccessController(DataAccessController controller) {
        return dataAccessMonitor.isForegroundDataAccessController(controller);
    }

    /**
     * Get foreground data access controller
     * <p/>
     * Delegate method
     *
     * @return DataAccessController data access controller
     */
    public final synchronized DataAccessController getForegroundDataAccessController() {
        return dataAccessMonitor.getForegroundDataAccessController();
    }

    /**
     * Set foreground data access controller
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     */
    public final synchronized void setForegroundDataAccessController(DataAccessController controller) {
        dataAccessMonitor.setForegroundDataAccessController(controller);
    }

    /**
     * Remove data access controller
     * <p/>
     * This method will close data access controller, it will also stop all ongoing tasks related to this
     * data access controller.
     *
     * @param controller  data access controller
     * @param cancelTasks whether to cancel the tasks associated with this controller
     */
    public final synchronized void removeDataAccessController(DataAccessController controller, boolean cancelTasks) {
        if (cancelTasks) {
            // cancel all the tasks related to this data access controller
            TaskManager taskMgr = this.getTaskManager();
            taskMgr.cancelTasksByOwner(controller);
        }

        // remove data access controller
        dataAccessMonitor.removeDataAccessController(controller);

        // remove gui component associated with this data access controller
        removeDataContentPane(controller);
    }

    /**
     * Replace one data access controller with another.
     *
     * @param original  original data access controller
     * @param replacement   replacement data access controller
     * @param cancelTasks   whether to cancel the tasks associated with this controller
     */
    public final synchronized void replaceDataAccessController(DataAccessController original, DataAccessController replacement, boolean cancelTasks) {
        if (cancelTasks) {
            // cancel all the tasks related to this data access controller
            TaskManager taskMgr = this.getTaskManager();
            taskMgr.cancelTasksByOwner(original);
        }

        // replace
        dataAccessMonitor.replaceDataAccessController(original, replacement);

        // remove gui component
        removeDataContentPane(original);
    }

    /**
     * Add a new data access controller, this will register the controller with PRIDE inspector system.
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     */
    public final synchronized void addDataAccessController(DataAccessController controller) {
        dataAccessMonitor.addDataAccessController(controller);
    }

    /**
     * Get data content pane created using the input data access controller
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     * @return JComponent   DataContentDisplayPane
     */
    public final synchronized JComponent getDataContentPane(DataAccessController controller) {
        return dataContentPaneCache.get(controller);
    }

    /**
     * Cache a content display pane for a data access controller
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     * @param component  data content pane
     */
    public final void addDataContentPane(DataAccessController controller, JComponent component) {
        dataContentPaneCache.put(controller, component);
    }

    /**
     * Remove a data content pane from cache
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     */
    public final void removeDataContentPane(DataAccessController controller) {
        dataContentPaneCache.remove(controller);
    }

    /**
     * Get data source browser
     *
     * @return DataSourceBrowser data source browser
     */
    public final synchronized DataSourceBrowser getDataSourceBrowser() {
        return dataSourceBrowser;
    }

    /**
     * Register data source browser
     *
     * @param dataSourceBrowser data source browser
     */
    public final synchronized void setDataSourceBrowser(DataSourceBrowser dataSourceBrowser) {
        this.dataSourceBrowser = dataSourceBrowser;
    }

    /**
     * Get protein name
     *
     * @param protAcc protein accession
     * @return String    protein name, if null, then the protein accession has not been checked before
     */
    public final String getProteinName(String protAcc) {
        return proteinNameTracker.get(protAcc);
    }

    /**
     * Store protein name and protein accession
     * This is for saving tasks from retrieving the protein names many times
     *
     * @param protAcc  protein accession
     * @param protName protein name
     */
    public final void addProteinName(String protAcc, String protName) {
        if (protAcc == null || protName == null) {
            String msg = "Protein accession or protein name cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        proteinNameTracker.put(protAcc, protName);
    }

    /**
     * Return the visibility of data source browser
     *
     * @return boolean visibility of the data source browser
     */
    public final synchronized boolean isDataSourceBrowserVisible() {
        return dataSourceBrowserVisible;
    }

    /**
     * Set the visibility of the data source browser
     * A DATA_SOURCE_BROWSER_VISIBILITY property change event will be triggered.
     *
     * @param dataSourceBrowserVisible the new visibility of the data source browser
     */
    public void setDataSourceBrowserVisible(boolean dataSourceBrowserVisible) {
        logger.debug("Set data source browser visibility to: {}", dataSourceBrowserVisible);
        boolean oldVis, newVis;
        synchronized (this) {
            oldVis = this.dataSourceBrowserVisible;
            this.dataSourceBrowserVisible = dataSourceBrowserVisible;
            newVis = dataSourceBrowserVisible;
        }
        firePropertyChange(DATA_SOURCE_BROWSER_VISIBILITY, oldVis, newVis);
    }

    private void createHelp() {
        try {
            ClassLoader cl = PrideInspectorContext.class.getClassLoader();
            URL url = HelpSet.findHelpSet(cl, this.getProperty("help.main.set"));
            mainHelpSet = new HelpSet(cl, url);
            mainHelpBroker = mainHelpSet.createHelpBroker();
        } catch (HelpSetException e) {
            logger.error("Failed to initialize help documents", e);
        }
    }

    public HelpSet getMainHelpSet() {
        if (mainHelpSet == null) {
            createHelp();
        }
        return mainHelpSet;
    }

    public HelpBroker getMainHelpBroker() {
        if (mainHelpBroker == null) {
            createHelp();
        }
        return mainHelpBroker;
    }

//    public Timer getMemoryUsageCheckTimer() {
//        return memoryUsageCheckTimer;
//    }

    /**
     * Get the current file open path
     *
     * @return String file path
     */
    public String getOpenFilePath() {
        return openFilePath;
    }

    /**
     * Set the file open path, this will effect the starting directory of
     * the <code>OpenFileDialog</code>
     *
     * @param openFilePath file open path
     */
    public void setOpenFilePath(String openFilePath) {
        this.openFilePath = openFilePath;
    }
}
