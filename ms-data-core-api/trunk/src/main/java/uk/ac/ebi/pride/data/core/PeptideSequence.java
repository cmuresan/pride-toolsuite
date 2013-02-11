package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * One (poly)peptide (a sequence with modifications).
 * The combination of Peptide sequence and modifications must be unique in the file.
 * User: yperez
 * Date: 04/08/11
 * Time: 10:29
 */
public class PeptideSequence extends IdentifiableParamGroup implements Comparable{

    /**
     * A molecule modification specification. If n modifications have been found on a peptide,
     * there should be n instances of Modification.
     */
    private List<Modification> modificationList = null;

    /**
     * The amino acid sequence of the (poly)peptide. If a substitution modification has been found,
     * the original sequence should be reported.
     */
    private String sequence = null;

    /**
     * A modification where one residue is substituted by another (amino acid change).
     * This attribute is used by the MzIdentMl Peptide Object.
     */
    private List<SubstitutionModification> substitutionModificationList = null;

    /**
     * Constructor for peptides without SubstitutionModificationList (PRIDE Peptides)
     *
     * @param id
     * @param name
     * @param sequence
     * @param modificationList
     */
    public PeptideSequence(String id, String name, String sequence, List<Modification> modificationList) {
        this(null, id, name, sequence, modificationList, null);
    }

    /**
     * Constructor for peptides without SubstitutionModificationList (PRIDE Peptides)
     *
     * @param params
     * @param id
     * @param name
     * @param sequence
     * @param modificationList
     */
    public PeptideSequence(ParamGroup params, String id, String name, String sequence,
                           List<Modification> modificationList) {
        this(params, id, name, sequence, modificationList, null);
    }

    /**
     * Constructor for peptides with SubstitutionModificationList
     *
     * @param id
     * @param name
     * @param sequence
     * @param modificationList
     * @param substitutionModificationList
     */
    public PeptideSequence(String id, String name, String sequence, List<Modification> modificationList,
                           List<SubstitutionModification> substitutionModificationList) {
        this(null, id, name, sequence, modificationList, substitutionModificationList);
    }

    /**
     * Constructor for peptides without SubstitutionModificationList (PRIDE Peptides)
     *
     * @param cvParams
     * @param userParams
     * @param id
     * @param name
     * @param sequence
     * @param modificationList
     */
    public PeptideSequence(List<CvParam> cvParams, List<UserParam> userParams, String id, String name, String sequence,
                           List<Modification> modificationList) {
        this(new ParamGroup(cvParams, userParams), id, name, sequence, modificationList, null);
    }

    /**
     * Constructor for peptides with SubstitutionModificationList
     *
     * @param params
     * @param id
     * @param name
     * @param sequence
     * @param modificationList
     * @param substitutionModificationList
     */
    public PeptideSequence(ParamGroup params, String id, String name, String sequence,
                           List<Modification> modificationList,
                           List<SubstitutionModification> substitutionModificationList) {
        super(params, id, name);
        this.sequence                     = sequence;
        this.modificationList             = modificationList;
        this.substitutionModificationList = substitutionModificationList;
    }

    /**
     * Constructor for peptides with SubstitutionModificationList
     *
     * @param cvParams
     * @param userParams
     * @param id
     * @param name
     * @param sequence
     * @param modificationList
     * @param substitutionModificationList
     */
    public PeptideSequence(List<CvParam> cvParams, List<UserParam> userParams, String id, String name, String sequence,
                           List<Modification> modificationList,
                           List<SubstitutionModification> substitutionModificationList) {
        this(new ParamGroup(cvParams, userParams), id, name, sequence, modificationList, substitutionModificationList);
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public List<Modification> getModificationList() {
        return modificationList;
    }

    public void setModificationList(List<Modification> modificationList) {
        this.modificationList = modificationList;
    }

    public List<SubstitutionModification> getSubstitutionModificationList() {
        return substitutionModificationList;
    }

    public void setSubstitutionModificationList(List<SubstitutionModification> substitutionModificationList) {
        this.substitutionModificationList = substitutionModificationList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeptideSequence)) return false;
        if (!super.equals(o)) return false;

        PeptideSequence that = (PeptideSequence) o;

        if (modificationList != null ? !modificationList.equals(that.modificationList) : that.modificationList != null)
            return false;
        if (sequence != null ? !sequence.equals(that.sequence) : that.sequence != null) return false;
        if (substitutionModificationList != null ? !substitutionModificationList.equals(that.substitutionModificationList) : that.substitutionModificationList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (modificationList != null ? modificationList.hashCode() : 0);
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        result = 31 * result + (substitutionModificationList != null ? substitutionModificationList.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        return (((PeptideSequence)o).getSequence().compareToIgnoreCase(this.getSequence()));
    }

    @Override
    public String toString() {
        return sequence;
    }
}



