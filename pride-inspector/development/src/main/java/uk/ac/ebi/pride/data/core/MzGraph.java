package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Abstract object can be extended by both Spectrum and Chromatogram.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 05-Feb-2010
 * Time: 15:41:15
 */
public abstract class MzGraph extends ParamGroup {

    /**
     * mzGraph's identifier
     */
    private Comparable id = null;
    /**
     * zero-based, consecutive index
     */
    private int index = -1;
    /**
     * appropriate data processing method
     */
    private DataProcessing dataProcessing = null;
    /**
     * default length of binary data arrays
     */
    private int defaultArrayLength = -1;
    /**
     * list of binary data arrays
     */
    private List<BinaryDataArray> binaryDataArrays = null;

    /**
     * Constructor
     *
     * @param id             required.
     * @param index          required.
     * @param dataProcessing optional.
     * @param arrLength      required.
     * @param binaryArr      required.
     * @param params         optional.
     */
    public MzGraph(Comparable id,
                   int index,
                   DataProcessing dataProcessing,
                   int arrLength,
                   List<BinaryDataArray> binaryArr,
                   ParamGroup params) {
        super(params);
        setId(id);
        setIndex(index);
        setDataProcessing(dataProcessing);
        setDefaultArrayLength(arrLength);
        setBinaryDataArrays(binaryArr);
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
        return dataProcessing;
    }

    public void setDataProcessing(DataProcessing dataProcessing) {
        this.dataProcessing = dataProcessing;
    }

    public int getDefaultArrayLength() {
        return defaultArrayLength;
    }

    public void setDefaultArrayLength(int defaultArrayLength) {
        this.defaultArrayLength = defaultArrayLength;
    }

    public Comparable getId() {
        return id;
    }

    public void setId(Comparable id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
