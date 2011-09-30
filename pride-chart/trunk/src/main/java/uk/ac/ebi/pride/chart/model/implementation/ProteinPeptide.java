package uk.ac.ebi.pride.chart.model.implementation;

import uk.ac.ebi.pride.chart.model.interfaces.ProteinPeptideInterface;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.term.CvTermReference;

/**
 * <p>Object is used to retrieve protein and peptides data from the database</p>
 *
 * @author Antonio Fabregat
 * Date: 05-oct-2010
 * Time: 14:44:13
 */
public class ProteinPeptide implements ProteinPeptideInterface {

    /**
     * Contains the spectrum ID (-1 means spectrum ID unavailable)
     */
    protected int spectrumID = -1;

    /**
     * Contains the protein ID
     */
    protected int proteinID;

    /**
     * Contains the sequence
     */
    protected String sequence;

    /**
     * Contains the PTM mass
     */
    protected double ptmMass;

    /**
     * Contains a number of peptide scores for a list of search engines 
     */
    protected PeptideScore scores = new PeptideScore();

    /**
     * <p> Creates an instance of this ProteinPeptide object, setting all fields as per description below.</p>
     *
     * @param spectrumID the spectrum ID
     * @param proteinID the protein ID
     * @param sequence the peptide Amino-Acid sequence
     * @param ptmMass the PTM mass
     */
    public ProteinPeptide(int spectrumID, int proteinID, String sequence, double ptmMass) {
        this.spectrumID = spectrumID;
        this.proteinID = proteinID;
        this.sequence = sequence;
        this.ptmMass = ptmMass;
    }

    /**
     * <p> Creates an instance of this ProteinPeptide object, setting all fields as per description below.</p>
     *
     * @param proteinID the protein ID
     * @param sequence the peptide Amino-Acid sequence
     * @param ptmMass the PTM mass
     */
    public ProteinPeptide(int proteinID, String sequence, double ptmMass) {
        this.proteinID = proteinID;
        this.sequence = sequence;
        this.ptmMass = ptmMass;
    }

    /**
     * Returns the protein ID
     *
     * @return the protein ID
     */
    @Override
    public int getProteinID() {
        return proteinID;
    }

    /**
     * Returns the peptide Amino-Acid sequence
     *
     * @return the peptide Amino-Acid sequence
     */
    @Override
    public String getSequence() {
        return sequence;
    }

    /**
     * Returns the spectrum ID
     *
     * @return the spectrum ID
     */
    @Override
    public int getSpectrumID() throws ProteinPeptideException {
        if (spectrumID==-1) throw new ProteinPeptideException();
        return spectrumID;
    }

    /**
     * Returns the PTM mass
     *
     * @return the PTM mass
     */
    @Override
    public double getPtmMass() {
        return ptmMass;
    }

    /**
     * Add a new peptide score
     *
     * @param se    search engine
     * @param ref   cv term reference for the score type
     * @param num   peptide score
     */
    public void addPeptideScore(SearchEngineType se, CvTermReference ref, Number num) {
        scores.addPeptideScore(se, ref, num);
    }

    public void setPeptideScore(PeptideScore peptideScore) {
        scores = peptideScore;
    }

    public PeptideScore getScores() {
        return scores;
    }

    /**
     * Returns a useful String representation of this ProteinPeptide instance that
     * includes details of all fields.
     *
     * @return a useful String representation of this ProteinPeptide instance. 
     */
    @Override
    public String toString() {
        return "ProteinPeptide{" +
                "proteinID=" + proteinID +
                ", spectrumID=" + spectrumID +
                ", sequence='" + sequence + '\'' +
                ", PTM_mass='" + ptmMass + '\'' +
                '}';
    }
}
