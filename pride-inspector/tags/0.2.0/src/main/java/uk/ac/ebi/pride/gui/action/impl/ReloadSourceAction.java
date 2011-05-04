package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.task.impl.ReloadSourceTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 18-Aug-2010
 * Time: 12:29:32
 */
public class ReloadSourceAction extends PrideAction implements PropertyChangeListener {
    public ReloadSourceAction(String name, Icon icon, String menuLocation) {
        super(name, icon, menuLocation);
        setAccelerator(java.awt.event.KeyEvent.VK_R, ActionEvent.CTRL_MASK);
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DesktopContext context = Desktop.getInstance().getDesktopContext();
        if (context instanceof PrideViewerContext) {
            PrideViewerContext prideContext = (PrideViewerContext) context;
            DataAccessController controllerToReload = prideContext.getForegroundDataAccessController();
            ReloadSourceTask newTask = new ReloadSourceTask(controllerToReload);
            // set task's gui blocker
            newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
            // add task listeners
            // ToDo: this why we need a singleton DesktopContext
            Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
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
