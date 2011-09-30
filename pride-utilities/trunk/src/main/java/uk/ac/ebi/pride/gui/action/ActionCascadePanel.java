package uk.ac.ebi.pride.gui.action;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ActionCascadePanel is JPanel that cascade all the action events to its child components.
 *
 * The default layout is BoxLayout on Y_AXIS.
 * The default border is an etched border.
 *
 * todo: this class should include more method override.
 *
 * User: rwang
 * Date: 16-Jul-2010
 * Time: 12:17:13
 */
public class ActionCascadePanel extends JPanel implements ActionListenable, ActionListener {
    private EventListenerList listeners;

    public ActionCascadePanel() {
        // default layout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.listeners = new EventListenerList();
        // border
        this.setBorder(BorderFactory.createEtchedBorder());
    }

    /**
     * Add a component to the panel, also register it as a action listener.
     *
     * @param comp  child component.
     * @return Component    the original component.
     */
    @Override
    public Component add(Component comp) {
        Component cp = super.add(comp);
        if (cp instanceof ActionListenable) {
            ((ActionListenable) cp).addActionListener(this);
        }
        return cp;
    }

    /**
     * Remove a component from the panel.
     *
     * @param comp  child component.
     */
    @Override
    public void remove(Component comp) {
        super.remove(comp);
        if (comp instanceof ActionListenable) {
            ((ActionListenable) comp).removeActionListener(this);
        }
    }

    /**
     * Cascade all the action event.
     * @param e action event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        ActionListener[] actionListeners = listeners.getListeners(ActionListener.class);
        for (ActionListener actionListener : actionListeners) {
            actionListener.actionPerformed(e);
        }
    }

    /**
     * Add an action listener.
     *
     * @param listener  action listener.
     */
    public void addActionListener(ActionListener listener) {
        listeners.add(ActionListener.class, listener);
    }

    /**
     * Remove an listeners
     * @param listener  action listener.
     */
    public void removeActionListener(ActionListener listener) {
        listeners.remove(ActionListener.class, listener);
    }
}
