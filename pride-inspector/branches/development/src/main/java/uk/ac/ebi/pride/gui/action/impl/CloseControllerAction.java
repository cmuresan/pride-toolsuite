package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.access.DataAccessMonitor;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Close controller action close the assigned controller, if there is not assigned controller,
 * then the foreground data access controller is closed.
 * <p/>
 *
 * User: rwang
 * Date: 17-Aug-2010
 * Time: 09:22:58
 */
public class CloseControllerAction extends PrideAction implements PropertyChangeListener {
    private final DataAccessController controller;
    private PrideInspectorContext context;

    public CloseControllerAction(String name, Icon icon) {
        this(name, icon, null);
    }

    public CloseControllerAction(String name, Icon icon, DataAccessController controller) {
        super(name, icon);
        this.controller = controller;

        if (controller == null) {
            this.setEnabled(false);
        } else {
            this.setEnabled(true);
        }
        // register this action as property listener to database access monitor
        context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        context.addPropertyChangeListenerToDataAccessMonitor(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DataAccessController controllerToClose;
        if (controller == null) {
            controllerToClose = context.getForegroundDataAccessController();
        } else {
            controllerToClose = controller;
            this.setEnabled(false);
        }
        context.removeDataAccessController(controllerToClose, true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (controller == null) {
            if (DataAccessMonitor.NEW_FOREGROUND_DATA_SOURCE_PROP.equals(evtName)) {
                DataAccessController foregroundController = context.getForegroundDataAccessController();
                this.setEnabled(foregroundController != null);
            }
        }
    }
}
