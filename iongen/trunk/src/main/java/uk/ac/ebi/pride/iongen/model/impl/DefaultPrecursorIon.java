package uk.ac.ebi.pride.iongen.model.impl;

import uk.ac.ebi.pride.mol.Peptide;

/**
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public class DefaultPrecursorIon extends AbstractPrecursorIon {
    public DefaultPrecursorIon(Peptide peptide, int charge) {
        super(peptide, charge);
    }

    public DefaultPrecursorIon(Peptide peptide) {
        this(peptide, 2);
    }

    public DefaultPrecursorIon(String sequence, int charge) {
        this(new Peptide(sequence), charge);
    }

    /**
     * default charge is 2, that means this type of precursor ion can generate
     * product ions which charges up to +2.
     *
     * @param sequence
     */
    public DefaultPrecursorIon(String sequence) {
        this(sequence, 2);
    }

}
