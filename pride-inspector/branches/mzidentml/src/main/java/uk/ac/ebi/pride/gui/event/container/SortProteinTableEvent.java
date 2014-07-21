package uk.ac.ebi.pride.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;
import uk.ac.ebi.pride.data.controller.DataAccessController;

/**
 * Created by yperez on 18/07/2014.
 */
public class SortProteinTableEvent extends AbstractEventServiceEvent {
    public enum Type {ENABLE_SORT, DISABLE_SORT}

    private Type type;

    /**
     * Default constructor
     *
     * @param type the type of load batch
     */
    public SortProteinTableEvent(DataAccessController controller, Type type) {
        super(controller);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
