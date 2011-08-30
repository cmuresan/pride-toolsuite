package uk.ac.ebi.pride.data.coreIdent;

import java.util.List;

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
    private IdentifiableParamGroup translationTableRef = null;
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
    private PeptideSequence peptideRef = null;
     /**
     * A reference to the protein sequence in which the specified peptide has been linked.
     */
    private DBSequence dbSequenceRef = null;

    public PeptideEvidence(String id, String name, int startPosition, int endPosition, boolean decoy, PeptideSequence peptideRef, DBSequence dbSequenceRef) {
        super(id, name);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.decoy = decoy;
        this.peptideRef = peptideRef;
        this.dbSequenceRef = dbSequenceRef;
    }

    public PeptideEvidence(ParamGroup params, String id, String name, int startPosition, int endPosition, boolean decoy, PeptideSequence peptideRef, DBSequence dbSequenceRef) {
        super(params, id, name);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.decoy = decoy;
        this.peptideRef = peptideRef;
        this.dbSequenceRef = dbSequenceRef;
    }

    public PeptideEvidence(List<CvParam> cvParams, List<UserParam> userParams, String id, String name, int startPosition, int endPosition, boolean decoy, PeptideSequence peptideRef, DBSequence dbSequenceRef) {
        super(cvParams, userParams, id, name);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.decoy = decoy;
        this.peptideRef = peptideRef;
        this.dbSequenceRef = dbSequenceRef;
    }

    public PeptideEvidence(List<CvParam> cvParams, String id, String name, int startPosition, int endPosition, boolean decoy, PeptideSequence peptideRef, DBSequence dbSequenceRef) {
        super(cvParams, id, name);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.decoy = decoy;
        this.peptideRef = peptideRef;
        this.dbSequenceRef = dbSequenceRef;
    }

    public PeptideEvidence(String id, String name, int startPosition, int endPosition, char preResidue, char postResidue, IdentifiableParamGroup translationTableRef, int frame, boolean decoy, PeptideSequence peptideRef, DBSequence dbSequenceRef) {
        super(id, name);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.preResidue = preResidue;
        this.postResidue = postResidue;
        this.translationTableRef = translationTableRef;
        this.frame = frame;
        this.decoy = decoy;
        this.peptideRef = peptideRef;
        this.dbSequenceRef = dbSequenceRef;
    }

    public PeptideEvidence(ParamGroup params, String id, String name, int startPosition, int endPosition, char preResidue, char postResidue, IdentifiableParamGroup translationTableRef, int frame, boolean decoy, PeptideSequence peptideRef, DBSequence dbSequenceRef) {
        super(params, id, name);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.preResidue = preResidue;
        this.postResidue = postResidue;
        this.translationTableRef = translationTableRef;
        this.frame = frame;
        this.decoy = decoy;
        this.peptideRef = peptideRef;
        this.dbSequenceRef = dbSequenceRef;
    }

    public PeptideEvidence(List<CvParam> cvParams, List<UserParam> userParams, String id, String name, int startPosition, int endPosition, char preResidue, char postResidue, IdentifiableParamGroup translationTableRef, int frame, boolean decoy, PeptideSequence peptideRef, DBSequence dbSequenceRef) {
        super(cvParams, userParams, id, name);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.preResidue = preResidue;
        this.postResidue = postResidue;
        this.translationTableRef = translationTableRef;
        this.frame = frame;
        this.decoy = decoy;
        this.peptideRef = peptideRef;
        this.dbSequenceRef = dbSequenceRef;
    }

    public PeptideEvidence(List<CvParam> cvParams, String id, String name, int startPosition, int endPosition, char preResidue, char postResidue, IdentifiableParamGroup translationTableRef, int frame, boolean decoy, PeptideSequence peptideRef, DBSequence dbSequenceRef) {
        super(cvParams, id, name);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.preResidue = preResidue;
        this.postResidue = postResidue;
        this.translationTableRef = translationTableRef;
        this.frame = frame;
        this.decoy = decoy;
        this.peptideRef = peptideRef;
        this.dbSequenceRef = dbSequenceRef;
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

    public PeptideSequence getPeptideRef() {
        return peptideRef;
    }

    public void setPeptideRef(PeptideSequence peptideRef) {
        this.peptideRef = peptideRef;
    }

    public DBSequence getDbSequenceRef() {
        return dbSequenceRef;
    }

    public void setDbSequenceRef(DBSequence dbSequenceRef) {
        this.dbSequenceRef = dbSequenceRef;
    }

    public IdentifiableParamGroup getTranslationTableRef() {
        return translationTableRef;
    }

    public void setTranslationTableRef(IdentifiableParamGroup translationTableRef) {
        this.translationTableRef = translationTableRef;
    }
}
