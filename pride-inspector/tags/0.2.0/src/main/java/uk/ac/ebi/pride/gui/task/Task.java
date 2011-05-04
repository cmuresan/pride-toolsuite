package uk.ac.ebi.pride.gui.task;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 25-Jan-2010
 * Time: 11:03:14
 */
public abstract class Task<T, V> extends SwingWorker<T, V> {
    
    private static final Logger logger = Logger.getLogger(Task.class.getName());

    public static final String NAME_PROP = "name";
    public static final String DESCRIPTION_PROP = "description";
    public static final String COMPLETED_PROP = "completed";
    public static final String GUI_BLOCKER_PROP = "blocker";
    
    private String name;
    private String description;
    private final Collection<TaskListener<T, V>> taskListeners;
    private GUIBlocker blocker;

    public Task() {
        taskListeners = new LinkedList<TaskListener<T, V>>();
    }

    public synchronized String getName() {
        return name;
    }

    public void setName(String n) {
        if (n == null) {
            throw new IllegalArgumentException("Null Task Name");
        } else {
            String oldName, newName;
            synchronized(this) {
                oldName = this.name;
                this.name = n;
                newName = n;
                firePropertyChange(NAME_PROP, oldName, newName);
            }
        }
    }

    public synchronized String getDescription() {
        return description;
    }

    public void setDescription(String desc){
        if (desc == null) {
            throw new IllegalArgumentException("Null Task Description");
        } else {
            String oldDesc, newDesc;
            synchronized(this) {
                oldDesc = this.description;
                this.description = desc;
                newDesc = desc;
                firePropertyChange(DESCRIPTION_PROP, oldDesc, newDesc);
            }
        }
    }

    public synchronized GUIBlocker getGUIBlocker() {
        return blocker;
    }

    public void setGUIBlocker(GUIBlocker bl) {
        if (bl == null) {
            throw new IllegalArgumentException("Null GUI Blocker");
        } else {
            GUIBlocker oldBlocker, newBlocker;
            synchronized(this) {
                oldBlocker = this.blocker;
                this.blocker = bl;
                newBlocker = bl;
                firePropertyChange(GUI_BLOCKER_PROP, oldBlocker, newBlocker);
            }
        }
    }

    public void addTaskListener(TaskListener<T, V> listener){
        if (listener == null)
            throw new IllegalArgumentException("Null Task Listener");
        synchronized(this) {
            taskListeners.add(listener);
        }
    }

    public void removeTaskListener(TaskListener<T, V> listener) {
        if (listener == null)
            throw new IllegalArgumentException("Null Task Listener");
        synchronized(this){
            taskListeners.remove(listener);
        }
    }

    public boolean hasTaskListener(TaskListener<T, V> listener) {
        synchronized (this) {
            return taskListeners.contains(listener);
        }
    }

    public synchronized Collection<TaskListener<T, V>> getTaskListeners() {
        return taskListeners;
    }

    protected void process(List<V> values) {
        fireProcessListeners(values);
    }

    //ToDo: Interrupted() and failed() does not cover exceptions during doinbackground(), this is not ideal!
    protected final void done() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isCancelled())
                        cancelled();
                    else
                        succeed(get());
                } catch(InterruptedException iex) {
                    interrupted(iex);
                } catch(ExecutionException eex) {
                    failed(eex.getCause());
                } finally {
                    finished();
                    try {
                        fireCompletionListeners();
                    }finally {
                        firePropertyChange(COMPLETED_PROP, false, true);
                    }
                }
            }
        });
    }

    /**
     * finished method is called by SwingWorker's done method
     */
    protected abstract void finished();

    /**
     * failed method is called by done method from SwingWorker when the task has failed.
     * @param error Throwable generated by failed task
     */
    protected void failed(Throwable error) {
        String msg = String.format("%s failed on : %s", this, error);
        logger.log(Level.ERROR, msg, error);
    }

    /**
     * succeed method is called by done method from SwingWorker when the task has succeed.
     * @param results
     */
    protected abstract void succeed(T results);
    protected abstract void cancelled();
    protected abstract void interrupted(InterruptedException iex);

    private void fireProcessListeners(List<V> values) {
        TaskEvent<List<V>> event = new TaskEvent<List<V>>(this, values);
        for(TaskListener listener : taskListeners) {
            listener.process(event);
        }
    }

    private void fireCompletionListeners() {
        try {
            if (isCancelled())
                fireCancelledListeners();
            else
                fireSucceedListeners(get());
        } catch (InterruptedException iex) {
            fireInterruptedListeners(iex);
        } catch (ExecutionException eex) {
            fireFailedListeners(eex.getCause());
        } finally {
            fireFinishedListeners();
        }
    }

    private void fireCancelledListeners() {
        TaskEvent<Void> event = new TaskEvent<Void>(this, null);
        for(TaskListener listener : taskListeners) {
            listener.cancelled(event);
        }
    }

    private void fireInterruptedListeners(InterruptedException iex) {
        TaskEvent<InterruptedException> event = new TaskEvent<InterruptedException>(this, iex);
        for(TaskListener listener : taskListeners) {
            listener.interrupted(event);
        }
    }

    private void fireSucceedListeners(T result) {
        TaskEvent<T> event = new TaskEvent<T>(this, result);
        for(TaskListener listener : taskListeners) {
            listener.succeed(event);
        }
    }

    private void fireFailedListeners(Throwable error) {
        TaskEvent<Throwable> event = new TaskEvent<Throwable>(this, error);
        for(TaskListener listener : taskListeners) {
            listener.failed(event);
        }
    }

    private void fireFinishedListeners() {
        TaskEvent<Void> event = new TaskEvent<Void>(this, null);
        for(TaskListener listener : taskListeners) {
            listener.finished(event);
        }
    }
}