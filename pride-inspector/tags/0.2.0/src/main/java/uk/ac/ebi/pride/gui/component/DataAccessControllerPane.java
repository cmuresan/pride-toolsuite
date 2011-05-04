package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * DataAccessControllerPane responsible for three things:
 * <p/>
 * 1. It maintains a reference to DataAccessController and also add itself as
 * a PropertyChangeListener to the DataAccessController. By default, it monitors
 * FOREGROUND_EXPERIMENT_CHANGE event, updatePropertyChange method should be overwritten
 * to provide concrete implementation.
 * <p/>
 * 2. It defines several methods for building the Panel.
 * <p/>
 * 3. It implements TaskListener interface for to listen to Task events, no concrete implementations
 * are given.
 * <p/>
 * User: rwang
 * Date: 03-May-2010
 * Time: 18:36:45
 */
public abstract class DataAccessControllerPane<T, V> extends JPanel
        implements PropertyChangeListener,
        TaskListener<T, V> {

    protected DataAccessController controller = null;
    private String title = null;

    protected DataAccessControllerPane(DataAccessController controller) {
        this(controller, null);
    }

    protected DataAccessControllerPane(DataAccessController controller, String title) {
        super();
        setController(controller);
        setTitle(title);
        setupMainPane();
        addComponents();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Setup the property this DataAccessControllerPane, such as: layout manager,
     * visibility and et.al.
     */
    protected void setupMainPane() {
    }

    /**
     * Add extra components into this DataAccessControllerPane
     */
    protected void addComponents() {
    }

    public DataAccessController getController() {
        return controller;
    }

    private void setController(DataAccessController controller) {
        // remove existing controller
        if (this.controller != null) {
            this.controller.removePropertyChangeListener(this);
        }
        this.controller = controller;
        // add new controller
        if (this.controller != null) {
            this.controller.addPropertyChangeListener(this);
        }
    }

    /**
     * Called when property change event is triggered.
     */
    protected void updatePropertyChange() {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessController.FOREGROUND_EXPERIMENT_CHANGED.equals(evtName) ||
                DataAccessController.DATA_SOURCE_RELOADED.equals(evtName)) {
            if (SwingUtilities.isEventDispatchThread()) {
                updatePropertyChange();
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        updatePropertyChange();
                    }
                };
                EventQueue.invokeLater(eventDispatcher);
            }
        }
    }

    @Override
    public void process(TaskEvent<List<V>> listTaskEvent) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void succeed(TaskEvent<T> tTaskEvent) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }
}
