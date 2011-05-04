package uk.ac.ebi.pride.gui.action.impl;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.impl.DBAccessControllerImpl;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.impl.OpenDatabaseTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Open database action will open a connection PRIDE public instance.
 *
 * User: rwang
 * Date: 11-Feb-2010
 * Time: 11:49:36
 */
public class OpenDatabaseAction extends PrideAction {
    private static final Logger logger = Logger.getLogger(DBAccessControllerImpl.class.getName());

    public OpenDatabaseAction(String desc, Icon icon, String menuLocation) {
        super(desc, icon, menuLocation);
        setAccelerator(java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        OpenDatabaseTask newTask = new OpenDatabaseTask();
        // set task's gui blocker
        newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
        // add task listeners
        // ToDo: this why we need a singleton DesktopContext
        Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
     
    }
}
