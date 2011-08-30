package uk.ac.ebi.pride.data.coreIdent;



import java.util.List;

/**
 * Abstract object can be extended by both Spectrum and Chromatogram.
 * This is a General Class representation of Spectrums and Chromatograms.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 05-Feb-2010
 * Time: 15:41:15
 */
public abstract class MzGraph extends IdentifiableParamGroup {

    /**
     * zero-based, consecutive index
     */
    private int index = -1;
    /**
     * appropriate data processing method
     */
    private DataProcessing defaultDataProcessing = null;
    /**
     * default length of binary data arrays
     */
    private int defaultArrayLength = -1;
    /**
     * list of binary data arrays
     */
    private List<BinaryDataArray> binaryDataArrays = null;

    /**
     *
     * @param id
     * @param name
     * @param index
     * @param defaultDataProcessing
     * @param defaultArrayLength
     * @param binaryDataArrays
     */
    protected MzGraph(Comparable id,
                      String name,
                      int index,
                      DataProcessing defaultDataProcessing,
                      int defaultArrayLength,
                      List<BinaryDataArray> binaryDataArrays) {
        super(id, name);
        this.index = index;
        this.defaultDataProcessing = defaultDataProcessing;
        this.defaultArrayLength = defaultArrayLength;
        this.binaryDataArrays = binaryDataArrays;
    }

    /**
     *
     * @param params
     * @param id
     * @param name
     * @param index
     * @param defaultDataProcessing
     * @param defaultArrayLength
     * @param binaryDataArrays
     */
    protected MzGraph(ParamGroup params,
                      Comparable id,
                      String name,
                      int index,
                      DataProcessing defaultDataProcessing,
                      int defaultArrayLength,
                      List<BinaryDataArray> binaryDataArrays) {
        super(params, id, name);
        this.index = index;
        this.defaultDataProcessing = defaultDataProcessing;
        this.defaultArrayLength = defaultArrayLength;
        this.binaryDataArrays = binaryDataArrays;
    }

    public List<BinaryDataArray> getBinaryDataArrays() {
        return binaryDataArrays;
    }

    public void setBinaryDataArrays(List<BinaryDataArray> binaryDataArrays) {
        this.binaryDataArrays = binaryDataArrays;
    }

    /**
     * Get either m/z array or intensity array
     *
     * @param cvAcc Controlled vocabulary's accession number.
     * @return BinaryDataArray  data array.
     */
    protected BinaryDataArray getBinaryDataArray(String cvAcc) {
        BinaryDataArray arr = null;

        List<BinaryDataArray> binaries = getBinaryDataArrays();
        if (binaries != null) {
            for (BinaryDataArray binary : binaries) {
                List<CvParam> cvParams = binary.getCvParams();
                for (CvParam cvParam : cvParams) {
                    String accession = cvParam.getAccession();
                    if (cvAcc.equals(accession)) {
                        arr = binary;
                    }
                }
            }
        }

        return arr;
    }

    public DataProcessing getDataProcessing() {
        return this.defaultDataProcessing;
    }

    public void setDataProcessing(DataProcessing dataProcessing) {
        this.defaultDataProcessing = dataProcessing;
    }

    public int getDefaultArrayLength() {
        return defaultArrayLength;
    }

    public void setDefaultArrayLength(int defaultArrayLength) {
        this.defaultArrayLength = defaultArrayLength;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
