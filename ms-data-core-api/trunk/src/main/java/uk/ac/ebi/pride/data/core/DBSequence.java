package uk.ac.ebi.pride.data.core;

/**
 * A database sequence from the specified SearchDatabase (nucleic acid or amino acid). If the sequence is nucleic acid,
 * the source nucleic acid sequence should be given in the seq attribute rather than a translated sequence.
 * User: yperez
 * Date: 04/08/11
 * Time: 13:47
 */
public class DBSequence extends IdentifiableParamGroup {

    /**
     * The unique accession of this sequence.
     */
    private String accessionId = null;

    /**
     * protein accession version
     */
    private String accessionVersion = null;

    /**
     * The length of the sequence as a number of bases or residues.
     */
    private int length = -1;

    /**
     * The source database of this sequence.
     */
    private SearchDataBase searchDataBase = null;

    /**
     * The actual sequence of amino acids or nucleic acid.
     */
    private String sequence = null;

    /**
     * optional splice isoform
     */
    private String spliceIsoform = null;

    /**
     * Constructor for PRIDE DBSequence Objects
     *
     * @param accessionId The accession in the DataBase of the Sequence
     * @param searchDataBase The Search DataBase that contains the current Sequence
     * @param accessionVersion  The version of the accession
     * @param spliceIsoform  Splice Isoform
     */
    public DBSequence(String accessionId, SearchDataBase searchDataBase, String accessionVersion,
                      String spliceIsoform) {
        this(null, null, null, -1, accessionId, searchDataBase, null, accessionVersion, spliceIsoform);
    }

    /**
     * Constructor with ParamGroup Information
     *
     * @param params  ParamGroup (List of CvTerms and User Params)
     * @param id      ID of the DBSequence Object
     * @param name    Name
     * @param length  The Length of the sequence
     * @param accessionId The accession in the DataBase of the Sequence
     * @param searchDataBase The Search DataBase that contains the current Sequence
     * @param sequence       Sequence
     * @param accessionVersion  The version of the accession
     * @param spliceIsoform  Splice Isoform
     */
    public DBSequence(ParamGroup params, Comparable id, String name, int length, String accessionId,
                      SearchDataBase searchDataBase, String sequence, String accessionVersion, String spliceIsoform) {
        super(params, id, name);
        this.length           = length;
        this.accessionId      = accessionId;
        this.searchDataBase   = searchDataBase;
        this.sequence         = sequence;
        this.accessionVersion = accessionVersion;
        this.spliceIsoform    = spliceIsoform;
    }

    /**
     * Get the length of the Sequence
     *
     * @return Length of the Sequence
     */
    public int getLength() {
        return length;
    }

    /**
     * Set the length of the Sequence
     *
     * @param length Length of the Sequence
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     *  Get the Accession ID of the Sequence in the DataBase
     *
     * @return Accession ID of the Sequence in the DataBase
     */
    public String getAccessionId() {
        return accessionId;
    }

    /**
     * Set the Accession ID of the Sequence in the DataBase
     *
     * @param accessionId Accession ID of the Sequence in the DataBase
     */
    public void setAccessionId(String accessionId) {
        this.accessionId = accessionId;
    }

    /**
     * Get the Search DataBase that contains the Sequence
     *
     * @return Search DataBase that contains the Sequence
     */
    public SearchDataBase getSearchDataBase() {
        return searchDataBase;
    }

    /**
     * Set the Search DataBase that contains the Sequence
     *
     * @param searchDataBase Search DataBase that contains the Sequence
     */
    public void setSearchDataBase(SearchDataBase searchDataBase) {
        this.searchDataBase = searchDataBase;
    }

    /**
     * Get the Sequence of the Protein in the Database
     *
     * @return Sequence
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Set the Sequence of the Protein in the Database
     *
     * @param sequence Sequence
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Get the Accession Version of the Sequence in the DataBase
     *
     * @return Accession Version of the Sequence in the DataBase
     */
    public String getAccessionVersion() {
        return accessionVersion;
    }

    /**
     * Set the Accession Version of the Sequence in the DataBase
     *
     * @param accessionVersion Accession Version of the Sequence in the DataBase
     */
    public void setAccessionVersion(String accessionVersion) {
        this.accessionVersion = accessionVersion;
    }

    /**
     * Get the Splice Isoform
     * @return spliceIsoform (String)
     */
    public String getSpliceIsoform() {
        return spliceIsoform;
    }

    /**
     *  Set the Splice Isoform
     *
     * @param spliceIsoform Splice Isoform
     */
    public void setSpliceIsoform(String spliceIsoform) {
        this.spliceIsoform = spliceIsoform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        DBSequence that = (DBSequence) o;

        if (length != that.length) {
            return false;
        }

        if (!accessionId.equals(that.accessionId)) {
            return false;
        }

        if (!accessionVersion.equals(that.accessionVersion)) {
            return false;
        }

        if (!searchDataBase.equals(that.searchDataBase)) {
            return false;
        }

        if (!sequence.equals(that.sequence)) {
            return false;
        }

        if (!spliceIsoform.equals(that.spliceIsoform)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (accessionId != null ? accessionId.hashCode() : 0);
        result = 31 * result + (accessionVersion != null ? accessionVersion.hashCode() : 0);
        result = 31 * result + length;
        result = 31 * result + (searchDataBase != null ? searchDataBase.hashCode() : 0);
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        result = 31 * result + (spliceIsoform != null ? spliceIsoform.hashCode() : 0);
        return result;
    }
}



