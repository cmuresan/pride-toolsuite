package uk.ac.ebi.pride.gui.task;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TaskManager should do the following tasks:
 * 1. maintain a list of Tasks
 * 2. manage a queue of Tasks
 * 3. 
 * User: rwang
 * Date: 22-Jan-2010
 * Time: 11:34:32
 */
public class TaskManager extends PropertyChangeHelper {
    private final static Logger logger = Logger.getLogger(TaskManager.class.getName());
    public final static String ADD_TASK_PROP = "add_new_task";
    public final static String REMOVE_TASK_PROP = "remove_new_task";

    private final static int CORE_POOL_SIZE = 4;
    private final static int MAXIMUM_POOL_SIZE = 10;
    private final ExecutorService executor;
    private final List<Task> tasks;
    private final PropertyChangeListener taskPropListener;

    public TaskManager() {
        this(new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                                    1L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
    }

    public TaskManager(ExecutorService executor) {
        this.executor = executor;
        this.tasks = new ArrayList<Task>();
        this.taskPropListener = new TaskPropertyListener();
    }

    public void addTask(Task task) {
        List<Task> oldTasks, newTasks; 
        synchronized(tasks) {
            oldTasks = new ArrayList<Task>(tasks);
            tasks.add(task);
            newTasks = new ArrayList<Task>(tasks);
            task.addPropertyChangeListener(taskPropListener);
        }
        // notify the status bar
        firePropertyChange(ADD_TASK_PROP, oldTasks, newTasks);
        // block gui
        // ToDo: this might need a separate thread
        GUIBlocker blocker = task.getGUIBlocker();
        if (blocker != null)
            blocker.block();

        executor.execute(task);
    }

    /**
     * orderly shutdown, all existing tasks are allowed to finish
     * no task is submitted. 
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * attempt to stop all running tasks at once
     */
    public void shutdownNow() {
        executor.shutdownNow();
    }

    private class TaskPropertyListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            if(Task.COMPLETED_PROP.equals(propName)) {
                Task task = (Task) evt.getSource();
                List<Task> oldTasks, newTasks;
                synchronized (tasks) {
                    oldTasks = new ArrayList<Task>(tasks);
                    tasks.remove(task);
                    newTasks = new ArrayList<Task>(tasks);
                    task.removePropertyChangeListener(taskPropListener);
                }
                firePropertyChange(REMOVE_TASK_PROP, oldTasks, newTasks);
                GUIBlocker blocker = task.getGUIBlocker();
                if (blocker != null)
                    blocker.unblock();
            }
        }
    }
}
