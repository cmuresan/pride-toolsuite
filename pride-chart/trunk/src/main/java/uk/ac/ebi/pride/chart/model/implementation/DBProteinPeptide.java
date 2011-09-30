package uk.ac.ebi.pride.chart.model.implementation;

/**
 * <p>Object is used to retrieve protein and peptides data from the database</p>
 * Three different steps are needed, and the peptide ID and spectrum reference have to
 * be stored in order to obtain the final needed data. 
 *
 * @author Antonio Fabregat
 * Date: 19-ago-2010
 * Time: 10:09:18
 */
public class DBProteinPeptide extends ProteinPeptide{
    /**
     * Contains the peptide ID
     */
    private int peptideID;

    /**
     * Contains the spectrum reference to be translate to the spectrum ID
     */
    private int spectrumRef;

    /**
     * <p> Creates an instance of this DBProteinPeptide object, setting all fields as per description below.</p>
     *
     * @param proteinID the protein identifier
     * @param peptide_ID the peptide identifier
     * @param sequence the peptide Amino-Acid sequence
     * @param spectrumRef the spectrum reference
     */
    public DBProteinPeptide(int proteinID, int peptide_ID, String sequence, int spectrumRef) {
        //SpectrumID set to -1 because it is not known for the moment
        super(-1, proteinID, sequence, 0.0);
        this.peptideID = peptide_ID;
        this.spectrumRef = spectrumRef;
    }

    /**
     * Returns the peptide ID
     *
     * @return the peptide ID
     */
    public int getPeptideID() {
        return peptideID;
    }

    /**
     * Returns the spectrum reference
     *
     * @return the spectrum reference
     */
    public int getSpectrumRef() {
        return spectrumRef;
    }

    /**
     * Set the spectrum ID
     *
     * @param spectrumID the protein ID
     */
    public void setSpectrumID(int spectrumID) {
        this.spectrumID = spectrumID;
    }

    /**
     * Set the PTM mass
     *
     * @param ptmMass the PTM mass
     */
    public void setPtmMass(double ptmMass) {
        this.ptmMass = ptmMass;
    }

    /**
     * Returns a useful String representation of this DBProteinPeptide instance that
     * includes details of all fields.
     *
     * @return a useful String representation of this DBProteinPeptide instance.
     */
    @Override
    public String toString() {
        return "ProteinPeptide{" +
                "proteinID=" + proteinID +
                ", peptideID=" + peptideID +
                ", spectrumID=" + spectrumID +
                ", sequence='" + sequence + '\'' +
                ", PTM_mass='" + ptmMass + '\'' +
                '}';
    }
}