package uk.ac.ebi.pride.gui;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Overall context of the GUI
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 16:45:38
 */
public class PrideGUIContext extends DesktopContext {
    /** DataAccessMonitor manages a list of */
    private final DataAccessMonitor dataAccessMonitor;
    /** */
    private final Map<DataAccessController, JComponent> dataViewerCache;

    public PrideGUIContext() {
        this.dataAccessMonitor = new DataAccessMonitor();
        this.dataViewerCache = new HashMap<DataAccessController, JComponent>();
    }

    public final synchronized DataAccessMonitor getDataAccessMonitor() {
        return dataAccessMonitor;
    }

    public final synchronized JComponent getDataViewPane(DataAccessController controller) {
        return dataViewerCache.get(controller);
    }

    public final synchronized void addDataViewPane(DataAccessController controller, JComponent component) {
        dataViewerCache.put(controller, component);    
    }

    public final synchronized void removeDataViewPane(DataAccessController controller) {
        dataViewerCache.remove(controller);
    }
}
