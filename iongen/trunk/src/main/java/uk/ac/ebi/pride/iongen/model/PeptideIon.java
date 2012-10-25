package uk.ac.ebi.pride.iongen.model;

import uk.ac.ebi.pride.mol.Peptide;

/**
 * The peptide ion including N- and C-terminal Groups, and a list of Residues.
 *
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public interface PeptideIon {
    public Peptide getPeptide();

    public int getCharge();

    public double getMass();

    public double getMassOverCharge();
}
