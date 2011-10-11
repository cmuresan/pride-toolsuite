package uk.ac.ebi.pride.data.core;

/**
 * A database sequence from the specified SearchDatabase (nucleic acid or amino acid). If the sequence is nucleic acid,
 * the source nucleic acid sequence should be given in the seq attribute rather than a translated sequence.
 * Created by IntelliJ IDEA.
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
     * @param accessionId
     * @param searchDataBase
     * @param accessionVersion
     * @param spliceIsoform
     */
    public DBSequence(String accessionId, SearchDataBase searchDataBase, String accessionVersion,
                      String spliceIsoform) {
        this(null, null, null, -1, accessionId, searchDataBase, null, accessionVersion, spliceIsoform);
    }

    /**
     * Constructor without ParamGroup Information
     *
     * @param id
     * @param name
     * @param length
     * @param accessionId
     * @param searchDataBase
     * @param sequence
     * @param accessionVersion
     * @param spliceIsoform
     */
    public DBSequence(Comparable id, String name, int length, String accessionId, SearchDataBase searchDataBase,
                      String sequence, String accessionVersion, String spliceIsoform) {
        this(null, id, name, length, accessionId, searchDataBase, sequence, accessionVersion, spliceIsoform);
    }

    /**
     * Constructor with ParamGroup Information
     *
     * @param params
     * @param id
     * @param name
     * @param length
     * @param accessionId
     * @param searchDataBase
     * @param sequence
     * @param accessionVersion
     * @param spliceIsoform
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getAccessionId() {
        return accessionId;
    }

    public void setAccessionId(String accessionId) {
        this.accessionId = accessionId;
    }

    public SearchDataBase getSearchDataBase() {
        return searchDataBase;
    }

    public void setSearchDataBase(SearchDataBase searchDataBase) {
        this.searchDataBase = searchDataBase;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getAccessionVersion() {
        return accessionVersion;
    }

    public void setAccessionVersion(String accessionVersion) {
        this.accessionVersion = accessionVersion;
    }

    public String getSpliceIsoform() {
        return spliceIsoform;
    }

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
        int result = length;

        result = 31 * result + accessionId.hashCode();
        result = 31 * result + searchDataBase.hashCode();
        result = 31 * result + sequence.hashCode();
        result = 31 * result + accessionVersion.hashCode();
        result = 31 * result + spliceIsoform.hashCode();

        return result;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
