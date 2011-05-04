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
public class PrideViewerContext extends DesktopContext {
    /** DataAccessMonitor manages a list of */
    private final DataAccessMonitor dataAccessMonitor;
    /** */
    private final Map<DataAccessController, JComponent> dataBrowserCache;

    public PrideViewerContext() {
        this.dataAccessMonitor = new DataAccessMonitor();
        this.dataBrowserCache = new HashMap<DataAccessController, JComponent>();
    }

    public final synchronized DataAccessMonitor getDataAccessMonitor() {
        return dataAccessMonitor;
    }

    public final synchronized JComponent getDataBrowser(DataAccessController controller) {
        return dataBrowserCache.get(controller);
    }

    public final synchronized void addDataBrowser(DataAccessController controller, JComponent component) {
        dataBrowserCache.put(controller, component);
    }

    public final synchronized void removeDataBrowser(DataAccessController controller) {
        dataBrowserCache.remove(controller);
    }
}
