package uk.ac.ebi.pride.gui.action;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.data.controller.impl.MzMLHibernateControllerImpl;
import uk.ac.ebi.pride.gui.PrideViewer;
import uk.ac.ebi.pride.gui.PrideViewerContext;

import java.awt.event.ActionEvent;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.impl.OpenHibernateSessionTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

/**
 * Created by IntelliJ IDEA.
 * User: Andreas Schoenegger <aschoen@ebi.ac.uk>
 * Date: 24.06.2010
 * Time: 15:26:10
 */
public class OpenPrideNextGenAction extends PrideAction {

    public OpenPrideNextGenAction() {
        super("Open Pride NextGen Hibernate Session");
        this.setMenuLocation("File");
        this.setAccelerator(java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OpenHibernateSessionTask newTask = new OpenHibernateSessionTask(MzMLHibernateControllerImpl.class, "In the process of opening Hibernate Session...");
        // set task's gui blocker
        newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
        // add task listeners
        // ToDo: this why we need a singleton DesktopContext
        Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
    }
}