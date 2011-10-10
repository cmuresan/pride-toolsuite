package uk.ac.ebi.pride.data.core;

/**
 * PeptideEvidence links a specific Peptide Element to a specific position in a DBSequence.
 * There must only be one PeptideEvidence item per Peptide-to-DBSequence-position.
 * <p>
 *     The Peptide Evidence Element contains the following information:
 *     - start position: Start position of the peptide inside the protein sequence.
 *     - end position: The index position of the last amino acid of the peptide inside the protein sequence.
 *     - preResidue and posResidue: Previous and Post flanking residues.
 *     - decoy: Set to true if the peptide is matched to a decoy sequence.
 *     - peptide Reference: The reference to the Peptide Sequence in the Database.
 *     - Protein Reference: The reference to the Protein or Nucleotide Sequence in the database.
 *     - frame: The translation frame of this sequence if this is PeptideEvidence derived from nucleic acid sequence.
 *     - Translation Table: A reference to the translation table used if this is PeptideEvidence derived from nucleic acid sequence
 * </p>
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 04/08/11
 * Time: 14:48
 * */
public class PeptideEvidence extends IdentifiableParamGroup {
    /**
     * Start position of the peptide inside the protein sequence, where the first amino acid of the
     * protein sequence is position 1. Must be provided unless this is a de novo search.
     */
    private int startPosition = -1;
    /**
     * The index position of the last amino acid of the peptide inside the protein sequence,
     * where the first amino acid of the protein sequence is position 1.
     * Must be provided unless this is a de novo search.
     */
    private int endPosition = -1;
    /**
     * Previous flanking residue. If the peptide is N-terminal, pre="-" and not pre="".
     * If for any reason it is unknown (e.g. denovo), pre="?" should be used.
     */
    private char preResidue = '\u0000';
    /**
     * Post flanking residue. If the peptide is C-terminal, post="-" and not post="".
     * If for any reason it is unknown (e.g. denovo), post="?" should be used.
     */
    private char postResidue = '\u0000';
    /**
     * The details specifying this translation table are captured as cvParams, e.g. translation table,
     * translation start codons and translation table description (see specification document and mapping file)
     */
    private IdentifiableParamGroup translationTable = null;
    /**
     * The translation frame of this sequence if this is PeptideEvidence derived from nucleic acid sequence.
     */
    private int frame = -1;
    /**
     * Set to true if the peptide is matched to a decoy sequence.
     */
    private boolean decoy = false;
    /**
     * A reference to the identified (poly)peptide sequence in the Peptide element.
     */
    private PeptideSequence peptideSequence = null;
     /**
     * A reference to the protein sequence in which the specified peptide has been linked.
     */
    private DBSequence dbSequence = null;

    public PeptideEvidence(String id,
                           String name,
                           int startPosition,
                           int endPosition,
                           boolean decoy,
                           PeptideSequence peptideSequence,
                           DBSequence dbSequence) {
        super(id, name);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.decoy = decoy;
        this.peptideSequence = peptideSequence;
        this.dbSequence = dbSequence;
    }

    public PeptideEvidence(ParamGroup params,
                           String id,
                           String name,
                           int startPosition,
                           int endPosition,
                           boolean decoy,
                           PeptideSequence peptideSequence,
                           DBSequence dbSequence) {
        super(params, id, name);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.decoy = decoy;
        this.peptideSequence = peptideSequence;
        this.dbSequence = dbSequence;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public char getPreResidue() {
        return preResidue;
    }

    public void setPreResidue(char preResidue) {
        this.preResidue = preResidue;
    }

    public char getPostResidue() {
        return postResidue;
    }

    public void setPostResidue(char postResidue) {
        this.postResidue = postResidue;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public boolean isDecoy() {
        return decoy;
    }

    public void setDecoy(boolean decoy) {
        this.decoy = decoy;
    }

    public PeptideSequence getPeptideSequence() {
        return peptideSequence;
    }

    public void setPeptideSequence(PeptideSequence peptideSequence) {
        this.peptideSequence = peptideSequence;
    }

    public DBSequence getDbSequence() {
        return dbSequence;
    }

    public void setDbSequence(DBSequence dbSequence) {
        this.dbSequence = dbSequence;
    }

    public IdentifiableParamGroup getTranslationTable() {
        return translationTable;
    }

    public void setTranslationTable(IdentifiableParamGroup translationTable) {
        this.translationTable = translationTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeptideEvidence that = (PeptideEvidence) o;

        if (decoy != that.decoy) return false;
        if (endPosition != that.endPosition) return false;
        if (frame != that.frame) return false;
        if (postResidue != that.postResidue) return false;
        if (preResidue != that.preResidue) return false;
        if (startPosition != that.startPosition) return false;
        if (!dbSequence.equals(that.dbSequence)) return false;
        if (!peptideSequence.equals(that.peptideSequence)) return false;
        if (!translationTable.equals(that.translationTable)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startPosition;
        result = 31 * result + endPosition;
        result = 31 * result + (int) preResidue;
        result = 31 * result + (int) postResidue;
        result = 31 * result + translationTable.hashCode();
        result = 31 * result + frame;
        result = 31 * result + (decoy ? 1 : 0);
        result = 31 * result + peptideSequence.hashCode();
        result = 31 * result + dbSequence.hashCode();
        return result;
    }
}
