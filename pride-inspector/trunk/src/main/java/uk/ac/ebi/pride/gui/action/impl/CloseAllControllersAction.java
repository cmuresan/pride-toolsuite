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
 * Close all existing data access controller
 *
 * User: rwang
 * Date: 11-Oct-2010
 * Time: 13:55:43
 */
public class CloseAllControllersAction extends PrideAction implements PropertyChangeListener {
    private PrideInspectorContext context;

    public CloseAllControllersAction(String name, Icon icon) {
        super(name, icon);
        // register this action as property listener to database access monitor
        context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        context.addPropertyChangeListenerToDataAccessMonitor(this);
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (DataAccessController controller : context.getControllers()) {
            context.removeDataAccessController(controller, true);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessMonitor.NEW_FOREGROUND_DATA_SOURCE_PROP.equals(evtName)) {
            DataAccessController foregroundController = context.getForegroundDataAccessController();
            this.setEnabled(foregroundController != null);
        }
    }
}