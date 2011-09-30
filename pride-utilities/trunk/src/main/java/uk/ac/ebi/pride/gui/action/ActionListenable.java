package uk.ac.ebi.pride.gui.action;

import java.awt.event.ActionListener;

/**
 * Implement ActionListenable means the class is actionlistener aware.
 *
 * User: rwang
 * Date: 21-Oct-2010
 * Time: 14:34:01
 */
public interface ActionListenable {
        /**
     * Add an action listener.
     *
     * @param listener  action listener
     */
    public void addActionListener(ActionListener listener);

    /**
     * Remove an action listener.
     *
     * @param listener  action listener
     */
    public void removeActionListener(ActionListener listener);
}
