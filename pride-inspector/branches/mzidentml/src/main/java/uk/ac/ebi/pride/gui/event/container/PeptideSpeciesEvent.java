package uk.ac.ebi.pride.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;
import uk.ac.ebi.pride.gui.component.table.model.PeptideSpecies;

/**
 * @author yperez, rwang
 * @version $Id$
 */
public class PeptideSpeciesEvent extends AbstractEventServiceEvent {

    private final PeptideSpecies peptideSpecies;
    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param peptideSpecies    Peptide species
     */
    public PeptideSpeciesEvent(Object source, PeptideSpecies peptideSpecies) {
        super(source);
        this.peptideSpecies = peptideSpecies;
    }

    public PeptideSpecies getPeptideSpecies() {
        return peptideSpecies;
    }
}