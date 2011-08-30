package uk.ac.ebi.pride.data.coreIdent;

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
     * The length of the sequence as a number of bases or residues.
     */
    private int length = -1;
    /**
     * The unique accession of this sequence.
     */
    private String accessionId = null;
    /**
     * The source database of this sequence.
     */
    private SearchDataBase searchDataBase = null;
    /**
     * The actual sequence of amino acids or nucleic acid.
     */
    private String sequence = null;
    /**
     * protein accession version
     */
    private String accessionVersion = null;
    /**
     * optional splice isoform
     */
    private String spliceIsoform = null;

    public DBSequence(Comparable id,
                      String name,
                      int length,
                      String accessionId,
                      SearchDataBase searchDataBase,
                      String sequence,
                      String accessionVersion,
                      String spliceIsoform) {
        super(id, name);
        this.length = length;
        this.accessionId = accessionId;
        this.searchDataBase = searchDataBase;
        this.sequence = sequence;
        this.accessionVersion = accessionVersion;
        this.spliceIsoform = spliceIsoform;
    }

    public DBSequence(ParamGroup params,
                      Comparable id,
                      String name,
                      int length,
                      String accessionId,
                      SearchDataBase searchDataBase,
                      String sequence,
                      String accessionVersion,
                      String spliceIsoform) {
        super(params, id, name);
        this.length = length;
        this.accessionId = accessionId;
        this.searchDataBase = searchDataBase;
        this.sequence = sequence;
        this.accessionVersion = accessionVersion;
        this.spliceIsoform = spliceIsoform;
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
}
