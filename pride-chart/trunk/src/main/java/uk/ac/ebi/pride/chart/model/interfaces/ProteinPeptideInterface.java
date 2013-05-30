package uk.ac.ebi.pride.chart.model.interfaces;

import uk.ac.ebi.pride.chart.model.implementation.ProteinPeptideException;

/**
 * <p></p>
 *
 * @author Antonio Fabregat
 *         Date: 05-oct-2010
 *         Time: 14:17:56
 */
public interface ProteinPeptideInterface {
    public Comparable getSpectrumID() throws ProteinPeptideException;

    public int getProteinID();

    public String getSequence();

    public double getPtmMass();
}