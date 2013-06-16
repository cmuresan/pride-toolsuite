package uk.ac.ebi.pride.gui.task;

import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

/**
 * @author Rui Wang
 * @version $Id$
 */
public final class TaskUtil {

    private static final DesktopContext context = Desktop.getInstance().getDesktopContext();

    private TaskUtil() {}

    public static void startBackgroundTask(Task task) {
        task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));

        context.addTask(task);
    }


    public static void startBackgroundTask(Task task, Object taskOwner) {
        task.addOwner(taskOwner);
        startBackgroundTask(task);
    }
}
