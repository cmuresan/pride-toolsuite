package uk.ac.ebi.pride.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * Event triggered when a new foreground data source is set.
 *
 * User: rwang
 * Date: 27/05/11
 * Time: 11:30
 */
public class ForegroundDataSourceEvent<T> extends AbstractEventServiceEvent {

    private T oldForegroundDataSource, newForegroundDataSource;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param oldForeground previous foreground data source
     * @param newForeground new foreground data source
     */
    public ForegroundDataSourceEvent(Object source, T oldForeground, T newForeground) {
        super(source);
        setOldForegroundDataSource(oldForeground);
        setNewForegroundDataSource(newForeground);
    }

    public T getOldForegroundDataSource() {
        return oldForegroundDataSource;
    }

    public void setOldForegroundDataSource(T oldForegroundDataSource) {
        this.oldForegroundDataSource = oldForegroundDataSource;
    }

    public T getNewForegroundDataSource() {
        return newForegroundDataSource;
    }

    public void setNewForegroundDataSource(T newForegroundDataSource) {
        this.newForegroundDataSource = newForegroundDataSource;
    }
}
