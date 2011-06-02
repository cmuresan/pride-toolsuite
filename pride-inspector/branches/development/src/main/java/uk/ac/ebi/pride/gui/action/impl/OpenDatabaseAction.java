package uk.ac.ebi.pride.gui.action.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.CentralContentPaneLockEvent;
import uk.ac.ebi.pride.gui.task.impl.OpenDatabaseSearchPaneTask;
import uk.ac.ebi.pride.gui.task.impl.OpenPrideDatabaseTask;
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

    public OpenDatabaseAction(String desc, Icon icon) {
        super(desc, icon);
        setAccelerator(java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // get desktop context
        PrideInspectorContext context = (PrideInspectorContext)Desktop.getInstance().getDesktopContext();

        // create a new connection to pride database
        OpenDatabaseSearchPaneTask newTask = new OpenDatabaseSearchPaneTask();
        // set task's gui blocker
        newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
        context.addTask(newTask);
    }
}
