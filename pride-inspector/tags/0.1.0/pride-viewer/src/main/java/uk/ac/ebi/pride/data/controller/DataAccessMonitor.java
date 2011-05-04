package uk.ac.ebi.pride.data.controller;

import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DataAccessMonitor acts as a data model for DataSourceViewer.
 * It maintains a list of DataAccessControllers currently in use in PRIDE GUI.
 * DataAccessControllers represent data sources, which could be either file or database.
 * 
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 11:10:57
 */
public class DataAccessMonitor extends PropertyChangeHelper {
    /** Property for add new data source */
    public static final String ADD_DATA_SOURCE_PROP = "add_new_data_source";
    /** Property for remove a data source */
    public static final String REMOVE_DATA_SOURCE_PROP = "remove_new_data_source";
    /** Property for set the foreground data source*/
    public static final String NEW_FOREGROUND_DATA_SOURCE_PROP = "new_foreground_data_source";
    /** A list of existing DataAccessControllers */
    private List<DataAccessController> controllers = null;
    /** Foreground DataAccessController */
    private DataAccessController foregroundController = null;


    public DataAccessMonitor() {
        this.controllers = new ArrayList<DataAccessController>();
    }

    public void addDataAccessController(DataAccessController controller) {
        // new controller should always be added to the end of the list
        List<DataAccessController> oldControllers, newControllers;

        synchronized(controllers) {
            oldControllers = new ArrayList<DataAccessController>(controllers);
            controllers.add(controller);
            if (foregroundController ==  null) {
                setForegroundDataAccessController(controller);
            }
            newControllers = new ArrayList<DataAccessController>(controllers);
        }
        firePropertyChange(ADD_DATA_SOURCE_PROP, oldControllers, newControllers);
    }

    public void removeDataAccessController(DataAccessController controller) {
        List<DataAccessController> oldControllers, newControllers;

        synchronized(controllers) {
            oldControllers = new ArrayList<DataAccessController>(controllers);
            controllers.remove(controller);
            controller.close();
            if (foregroundController.equals(controller)) {
                setForegroundDataAccessController(null);    
            }
            newControllers = new ArrayList<DataAccessController>(controllers);
        }
        firePropertyChange(REMOVE_DATA_SOURCE_PROP, oldControllers, newControllers);
    }

    public void setForegroundDataAccessController(DataAccessController controller) {
        DataAccessController oldController, newController;
        oldController = this.foregroundController;
        foregroundController = controller;
        newController = controller;
        firePropertyChange(NEW_FOREGROUND_DATA_SOURCE_PROP, oldController, newController);
    }

    public synchronized DataAccessController getForegroundDataAccessController() {
        return this.foregroundController;
    }

    public List<DataAccessController> getControllers() {
        return copyControllerList();
    }

    public synchronized void close() {
        // ToDo: Exception handling
        for(DataAccessController controller : controllers) {
            controller.close();
        }
    }

    private List<DataAccessController> copyControllerList() {
        synchronized(controllers) {
            if (controllers.isEmpty()) {
                return Collections.emptyList();
            } else {
                return new ArrayList<DataAccessController>(controllers);
            }
        }
    }
}