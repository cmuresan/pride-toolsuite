package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Close controller action close the assigned controller, if there is not assigned controller,
 * then the foreground data access controller is closed.
 * <p/>
 * User: rwang
 * Date: 17-Aug-2010
 * Time: 09:22:58
 */
public class CloseControllerAction extends PrideAction implements PropertyChangeListener {
    private final DataAccessController controller;

    public CloseControllerAction(String name, Icon icon, String menuLocation) {
        this(name, icon, menuLocation, null);
    }

    public CloseControllerAction(String name, Icon icon, String menuLocation, DataAccessController controller) {
        super(name, icon, menuLocation);
        this.controller = controller;
        setAccelerator(java.awt.event.KeyEvent.VK_C, ActionEvent.CTRL_MASK);
        if (controller == null) {
            this.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DesktopContext context = Desktop.getInstance().getDesktopContext();
        if (context instanceof PrideViewerContext) {
            PrideViewerContext prideContext = (PrideViewerContext) context;
            DataAccessController controllerToClose;
            if (controller == null) {
                controllerToClose = prideContext.getForegroundDataAccessController();
            } else {
                controllerToClose = controller;
                this.setEnabled(false);
            }
            prideContext.removeDataAccessController(controllerToClose);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (controller == null) {
            if (DataAccessMonitor.ADD_DATA_SOURCE_PROP.equals(evtName)) {
                this.setEnabled(true);
            } else if (DataAccessMonitor.REMOVE_DATA_SOURCE_PROP.equals(evtName)) {
                DesktopContext context = Desktop.getInstance().getDesktopContext();
                if (context instanceof PrideViewerContext) {
                    if (((PrideViewerContext) context).getDataAccessMonitor().getNumberOfControllers() == 0) {
                        this.setEnabled(false);
                    }
                }
            }
        }
    }
}
