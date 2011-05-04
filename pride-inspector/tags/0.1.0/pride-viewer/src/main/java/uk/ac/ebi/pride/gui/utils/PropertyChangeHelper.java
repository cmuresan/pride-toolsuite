package uk.ac.ebi.pride.gui.utils;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This is a adapter class on PropertyChangeSupport with improved thread support
 * User: rwang
 * Date: 21-Jan-2010
 * Time: 11:44:29
 */
public class PropertyChangeHelper {

    private final PropertyChangeSupport supporter; 

    public PropertyChangeHelper() {
        supporter = new ImprovedPropertyChangerSuport(this);
    }

    public PropertyChangeHelper(Object source) {
        supporter = new ImprovedPropertyChangerSuport(source);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        supporter.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propName, PropertyChangeListener listener) {
        supporter.addPropertyChangeListener(propName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        supporter.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propName, PropertyChangeListener listener) {
        supporter.removePropertyChangeListener(propName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return supporter.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propName) {
        return supporter.getPropertyChangeListeners(propName);
    }

    public void firePropertyChange(final PropertyChangeEvent event) {
        supporter.firePropertyChange(event);
    }

    public void firePropertyChange(String propName, Object oldValue, Object newValue) {
        supporter.firePropertyChange(propName, oldValue, newValue);
    }

    private static class ImprovedPropertyChangerSuport extends PropertyChangeSupport {
        public ImprovedPropertyChangerSuport(Object o) {
            super(o);
        }

        @Override
        public void firePropertyChange(final PropertyChangeEvent event){
            if (SwingUtilities.isEventDispatchThread()) {
                super.firePropertyChange(event);
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        firePropertyChange(event);
                    }
                };
                SwingUtilities.invokeLater(eventDispatcher);
            }
        }
    }
}
