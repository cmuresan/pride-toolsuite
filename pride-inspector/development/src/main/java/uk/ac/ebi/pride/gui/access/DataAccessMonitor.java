package uk.ac.ebi.pride.gui.access;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DataAccessMonitor acts as a data model for DataSourceViewer.
 * It maintains a list of DataAccessControllers currently in use in PRIDE GUI.
 * DataAccessControllers represent data sources, which could be either file or database.
 * <p/>
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 11:10:57
 */
public class DataAccessMonitor extends PropertyChangeHelper {
    /**
     * Property for add new data source
     */
    public static final String ADD_DATA_SOURCE_PROP = "add_new_data_source";
    /**
     * Property for remove a data source
     */
    public static final String REMOVE_DATA_SOURCE_PROP = "remove_new_data_source";
    /**
     * Property for set the foreground data source
     */
    public static final String NEW_FOREGROUND_DATA_SOURCE_PROP = "new_foreground_data_source";
    /**
     * A list of existing DataAccessControllers
     */
    private final List<DataAccessController> controllers;
    /**
     * Foreground DataAccessController
     */
    private DataAccessController foregroundController = null;


    public DataAccessMonitor() {
        this.controllers = Collections.synchronizedList(new ArrayList<DataAccessController>());
    }

    public void addDataAccessController(DataAccessController controller) {
        // new controller should always be added to the end of the list
        List<DataAccessController> oldControllers = null, newControllers = null;

        synchronized (this) {
            if (!controllers.contains(controller)) {
                oldControllers = new ArrayList<DataAccessController>(controllers);
                controllers.add(controller);
                newControllers = new ArrayList<DataAccessController>(controllers);
            }
        }
        firePropertyChange(ADD_DATA_SOURCE_PROP, oldControllers, newControllers);
        
        setForegroundDataAccessController(controller);
    }

    public void removeDataAccessController(DataAccessController controller) {
        List<DataAccessController> oldControllers = null, newControllers = null;

        synchronized (this) {
            int index = controllers.indexOf(controller);
            if (index >= 0) {
                oldControllers = new ArrayList<DataAccessController>(controllers);
                // get the next available controller's index
                int nextIndex = controllers.size() - 1 > index ? index : index - 1;
                controllers.remove(controller);
                if (foregroundController.equals(controller)) {
                    if (nextIndex >= 0) {
                        setForegroundDataAccessController(controllers.get(nextIndex));
                    } else {
                        setForegroundDataAccessController(null);
                    }
                }
                controller.close();
                newControllers = new ArrayList<DataAccessController>(controllers);
            }
        }
        firePropertyChange(REMOVE_DATA_SOURCE_PROP, oldControllers, newControllers);
    }

    /**
     * Replace one data access controller with another, and retain the position in the DataAccessMonitor.
     *
     * @param original  original data access controller
     * @param replacement   replacement data access controller
     */
    public void replaceDataAccessController(DataAccessController original, DataAccessController replacement) {
        List<DataAccessController> oldControllers = null, newControllers = null;

        synchronized (this) {
            int index = controllers.indexOf(original);
            if (index >= 0) {
                oldControllers = new ArrayList<DataAccessController>(controllers);
                controllers.add(index, replacement);
                controllers.remove(original);
                if (foregroundController.equals(original)) {
                    setForegroundDataAccessController(replacement);
                }
                original.close();
                newControllers = new ArrayList<DataAccessController>(controllers);
                // notify others
                firePropertyChange(ADD_DATA_SOURCE_PROP, oldControllers, newControllers);
            } else {
                // add as a new data access controller
                addDataAccessController(replacement);
            }
        }
    }

    public void setForegroundDataAccessController(DataAccessController controller) {
        DataAccessController oldController, newController;
        synchronized (this) {
            oldController = this.foregroundController;
            foregroundController = controller;
            newController = controller;
        }
        firePropertyChange(NEW_FOREGROUND_DATA_SOURCE_PROP, oldController, newController);
    }

    public synchronized DataAccessController getForegroundDataAccessController() {
        return this.foregroundController;
    }

    /**
     * Check whether the given data access controller is foreground data access controller
     *
     * @param controller data access controller
     * @return boolean  true if it is foreground data access controller
     */
    public synchronized boolean isForegroundDataAccessController(DataAccessController controller) {
        return foregroundController.equals(controller);
    }

    public List<DataAccessController> getControllers() {
        return copyControllerList();
    }

    public int getNumberOfControllers() {
        return controllers.size();
    }

    public synchronized void close() {
        // ToDo: Exception handling
        for (DataAccessController controller : controllers) {
            controller.close();
        }
    }

    private List<DataAccessController> copyControllerList() {
        synchronized (this) {
            if (controllers.isEmpty()) {
                return Collections.emptyList();
            } else {
                return new ArrayList<DataAccessController>(controllers);
            }
        }
    }
}