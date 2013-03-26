package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.CollectionUtils;

import java.util.List;

/**
 * One (poly)peptide (a sequence with modifications).
 * The combination of Peptide sequence and modifications must be unique in the file.
 * User: yperez
 * Date: 04/08/11
 * Time: 10:29
 *
 * todo: compareTo method is implemented using bad practice, need to be refactored
 */
public class PeptideSequence extends IdentifiableParamGroup implements Comparable{

    /**
     * A molecule modification specification. If n modifications have been found on a peptide,
     * there should be n instances of Modification.
     */
    private List<Modification> modificationList;

    /**
     * The amino acid sequence of the (poly)peptide. If a substitution modification has been found,
     * the original sequence should be reported.
     */
    private String sequence;

    /**
     * A modification where one residue is substituted by another (amino acid change).
     * This attribute is used by the MzIdentMl Peptide Object.
     */
    private List<SubstitutionModification> substitutionModificationList;

    /**
     * Constructor for peptides without SubstitutionModificationList (PRIDE Peptides)
     *
     * @param id    Generic Id
     * @param name  Generic Name
     * @param sequence Sequence
     * @param modificationList  Modification List
     */
    public PeptideSequence(String id, String name, String sequence, List<Modification> modificationList) {
        this(null, id, name, sequence, modificationList, null);
    }

    /**
     * Constructor for peptides without SubstitutionModificationList (PRIDE Peptides)
     *
     *
     * @param params   ParamGroup for PeptideSequence
     * @param id       Generic Id
     * @param name  Generic Name
     * @param sequence Sequence
     * @param modificationList  Modification List
     */
    public PeptideSequence(ParamGroup params, String id, String name, String sequence,
                           List<Modification> modificationList) {
        this(params, id, name, sequence, modificationList, null);
    }

    /**
     * Constructor for peptides with SubstitutionModificationList
     *
     * @param id    Generic Id
     * @param name  Generic Name
     * @param sequence Sequence
     * @param modificationList  Modification List
     * @param substitutionModificationList Substitution Modification List
     */
    public PeptideSequence(String id, String name, String sequence, List<Modification> modificationList,
                           List<SubstitutionModification> substitutionModificationList) {
        this(null, id, name, sequence, modificationList, substitutionModificationList);
    }

    /**
     * Constructor for peptides without SubstitutionModificationList (PRIDE Peptides)
     *
     * @param cvParams    CvParams
     * @param userParams  UserParams
     * @param id    Generic Id
     * @param name  Generic Name
     * @param sequence Sequence
     * @param modificationList  Modification List
     */
    public PeptideSequence(List<CvParam> cvParams, List<UserParam> userParams, String id, String name, String sequence,
                           List<Modification> modificationList) {
        this(new ParamGroup(cvParams, userParams), id, name, sequence, modificationList, null);
    }

    /**
     * Constructor for peptides with SubstitutionModificationList
     *
     * @param cvParams    CvParams
     * @param userParams  UserParams
     * @param id    Generic Id
     * @param name  Generic Name
     * @param sequence Sequence
     * @param modificationList  Modification List
     * @param substitutionModificationList Substitution Modification List
     */
    public PeptideSequence(List<CvParam> cvParams, List<UserParam> userParams, String id, String name, String sequence,
                           List<Modification> modificationList,
                           List<SubstitutionModification> substitutionModificationList) {
        this(new ParamGroup(cvParams, userParams), id, name, sequence, modificationList, substitutionModificationList);
    }

    /**
     * Constructor for peptides with SubstitutionModificationList
     *
     * @param params ParamGroup for PeptideSequence
     * @param id    Generic Id
     * @param name  Generic Name
     * @param sequence Sequence
     * @param modificationList  Modification List
     * @param substitutionModificationList Substitution Modification List
     */
    public PeptideSequence(ParamGroup params, String id, String name, String sequence,
                           List<Modification> modificationList,
                           List<SubstitutionModification> substitutionModificationList) {
        super(params, id, name);
        this.sequence                     = sequence;
        this.modificationList             = CollectionUtils.createListFromList(modificationList);
        this.substitutionModificationList = CollectionUtils.createListFromList(substitutionModificationList);
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
        CollectionUtils.replaceValuesInCollection(modificationList, this.modificationList);
    }

    public List<SubstitutionModification> getSubstitutionModificationList() {
        return substitutionModificationList;
    }

    public void setSubstitutionModificationList(List<SubstitutionModification> substitutionModificationList) {
        CollectionUtils.replaceValuesInCollection(substitutionModificationList, this.substitutionModificationList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeptideSequence)) return false;
        if (!super.equals(o)) return false;

        PeptideSequence that = (PeptideSequence) o;

        if (!modificationList.equals(that.modificationList)) return false;
        if (sequence != null ? !sequence.equals(that.sequence) : that.sequence != null) return false;
        if (!substitutionModificationList.equals(that.substitutionModificationList)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + modificationList.hashCode();
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        result = 31 * result + substitutionModificationList.hashCode();
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



