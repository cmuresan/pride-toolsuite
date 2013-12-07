package uk.ac.ebi.pride.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;
import uk.ac.ebi.pride.data.controller.DataAccessController;

/**
 * Created with IntelliJ IDEA.
 * User: yperez
 * Date: 12/6/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpectrumAddEvent<T> extends AbstractEventServiceEvent {

    public static enum Status {SPECTRUM_ADDED, SPECTRUM_REMOVED}

    private DataAccessController controller;

    private Status status;

    public SpectrumAddEvent(Object source, DataAccessController controller, Status status) {
        super(source);
        setStatus(status);
        setController(controller);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public DataAccessController getController() {
        return controller;
    }

    public void setController(DataAccessController controller) {
        this.controller = controller;
    }
}
