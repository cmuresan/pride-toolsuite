package uk.ac.ebi.pride.gui.task;

import java.util.List;

/**
 * User: rwang
 * Date: 22-Jan-2010
 * Time: 13:38:59
 */
public interface TaskListener<T, V> {
    public void process(TaskEvent<List<V>> event);
    public void finished(TaskEvent<Void> event);
    public void failed(TaskEvent<Throwable> event);
    public void succeed(TaskEvent<T> event);
    public void cancelled(TaskEvent<Void> event);
    public void interrupted(TaskEvent<InterruptedException> iex);
}
