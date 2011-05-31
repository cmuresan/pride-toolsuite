package uk.ac.ebi.pride.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * Indicates a database search event has been triggered, this will result showing the database search pane
 *
 * User: rwang
 * Date: 27/05/11
 * Time: 15:03
 */
public class DatabaseSearchEvent extends AbstractEventServiceEvent{

    /**
     * Default constructor
     *
     * @param source the source of the event
     */
    public DatabaseSearchEvent(Object source) {
        super(source);
    }
}
