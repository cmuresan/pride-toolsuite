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
    private final Map<DataAccessController, JComponent> dataContentPaneCache;

    private final Map<DataAccessController, JComponent> experimentTreeCache;

    public PrideViewerContext() {
        this.dataAccessMonitor = new DataAccessMonitor();
        this.dataContentPaneCache = new HashMap<DataAccessController, JComponent>();
        this.experimentTreeCache = new HashMap<DataAccessController, JComponent>();
    }

    public final synchronized DataAccessMonitor getDataAccessMonitor() {
        return dataAccessMonitor;
    }

    public final synchronized JComponent getDataContentPane(DataAccessController controller) {
        return dataContentPaneCache.get(controller);
    }

    public final synchronized void addDataContentPane(DataAccessController controller, JComponent component) {
        dataContentPaneCache.put(controller, component);
    }

    public final synchronized void removeDataContentPane(DataAccessController controller) {
        dataContentPaneCache.remove(controller);
    }

    public final synchronized JComponent getExperimentTree(DataAccessController controller) {
        return experimentTreeCache.get(controller);
    }

    public final synchronized void addExperimentTree(DataAccessController controller, JComponent component) {
        experimentTreeCache.put(controller, component);
    }

    public final synchronized void removeExperimentTree(DataAccessController controller) {
        experimentTreeCache.remove(controller);
    }

    public final synchronized DataAccessController getForegroundDataAccessController() {
        return dataAccessMonitor.getForegroundDataAccessController();
    }

    public final synchronized void removeDataAccessController(DataAccessController controller) {
        dataAccessMonitor.removeDataAccessController(controller);
        removeDataContentPane(controller);
        removeExperimentTree(controller);
    }
}
