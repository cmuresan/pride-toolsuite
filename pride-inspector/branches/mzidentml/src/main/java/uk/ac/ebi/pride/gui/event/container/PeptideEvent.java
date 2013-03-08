package uk.ac.ebi.pride.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;
import uk.ac.ebi.pride.data.controller.DataAccessController;

/**
 * Event to trigger when a peptide is selected
 *
 * User: rwang
 * Date: 10/06/11
 * Time: 11:33
 */
public class PeptideEvent extends AbstractEventServiceEvent {

    private Comparable identificationId;
    private Comparable peptideId;
    private DataAccessController controller;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param controller    data access controller
     * @param identId   identification id
     * @param pepId peptide id
     */
    public PeptideEvent(Object source, DataAccessController controller, Comparable identId, Comparable pepId) {
        super(source);
        this.controller = controller;
        this.identificationId = identId;
        this.peptideId = pepId;
    }

    public Comparable getIdentificationId() {
        return identificationId;
    }

    public void setIdentificationId(Comparable identificationId) {
        this.identificationId = identificationId;
    }

    public Comparable getPeptideId() {
        return peptideId;
    }

    public void setPeptideId(Comparable peptideId) {
        this.peptideId = peptideId;
    }

    public DataAccessController getController() {
        return controller;
    }

    public void setController(DataAccessController controller) {
        this.controller = controller;
    }
}
