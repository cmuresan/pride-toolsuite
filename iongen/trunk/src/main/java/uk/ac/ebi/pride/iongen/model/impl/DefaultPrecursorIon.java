package uk.ac.ebi.pride.iongen.model.impl;

import uk.ac.ebi.pride.mol.Peptide;

/**
 * Provide different construct methods to create precursor ion.
 *
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public class DefaultPrecursorIon extends AbstractPrecursorIon {
    public DefaultPrecursorIon(Peptide peptide, int charge) {
        super(peptide, charge);
    }

    /**
     * Default charge is 2, that means this type of precursor ion can generate
     * product ions which charges up to +2.
     * @param peptide {@link Peptide}
     */
    public DefaultPrecursorIon(Peptide peptide) {
        this(peptide, 2);
    }

    /**
     * @see Peptide#Peptide(String)
     */
    public DefaultPrecursorIon(String sequence, int charge) {
        this(new Peptide(sequence), charge);
    }

    /**
     * Default charge is 2, that means this type of precursor ion can generate
     * product ions which charges up to +2.
     *
     * @param sequence {@link Peptide#Peptide(String)}
     */
    public DefaultPrecursorIon(String sequence) {
        this(sequence, 2);
    }

}
