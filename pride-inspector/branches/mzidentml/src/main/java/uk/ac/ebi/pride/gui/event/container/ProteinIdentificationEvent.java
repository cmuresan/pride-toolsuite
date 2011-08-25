package uk.ac.ebi.pride.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;
import uk.ac.ebi.pride.data.controller.DataAccessController;

/**
 * Event to identify a protein identification
 *
 * User: rwang
 * Date: 09/06/11
 * Time: 13:17
 */
public class ProteinIdentificationEvent extends AbstractEventServiceEvent {
    private Comparable identificationId;
    private DataAccessController controller;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param controller    data access controller
     * @param identId   Identification id
     */
    public ProteinIdentificationEvent(Object source, DataAccessController controller, Comparable identId) {
        super(source);
        this.controller = controller;
        this.identificationId = identId;
    }

    public Comparable getIdentificationId() {
        return identificationId;
    }

    public void setIdentificationId(Comparable identificationId) {
        this.identificationId = identificationId;
    }

    public DataAccessController getController() {
        return controller;
    }

    public void setController(DataAccessController controller) {
        this.controller = controller;
    }
}
