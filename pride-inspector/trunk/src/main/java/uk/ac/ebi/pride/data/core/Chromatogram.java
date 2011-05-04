package uk.ac.ebi.pride.data.core;


import uk.ac.ebi.pride.term.CvTermReference;

import java.util.List;

/**
 * Chromatogram object.
 * <p/>
 * In mzML 1.1.0.1, the following cv terms must be added:
 * <p/>
 * 1. May include one or more child terms of "chromatogram attribute"
 * (highest observed m/z, highest observed wavelength and et al)
 * <p/>
 * 2. Must include only one child term of "chromatogram type"
 * (total ion current chromatogram, selected ion current chromatogram,
 * basepeak chromatogram)
 * <p/>
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 09:07:38
 */
public class Chromatogram extends MzGraph {

    /**
     * Constructor
     *
     * @param id             required.
     * @param index          required and non negative.
     * @param dataProcessing optional.
     * @param arrLength      required.
     * @param binaryArr      required.
     * @param params         optional.
     */
    public Chromatogram(Comparable id,
                        int index,
                        DataProcessing dataProcessing,
                        int arrLength,
                        List<BinaryDataArray> binaryArr,
                        ParamGroup params) {
        super(id, index, dataProcessing, arrLength, binaryArr, params);
    }

    public BinaryDataArray getIntensityArray() {
        return getBinaryDataArray(CvTermReference.INTENSITY_ARRAY.getAccession());
    }

    public BinaryDataArray getTimeArray() {
        return getBinaryDataArray(CvTermReference.TIME_ARRAY.getAccession());
    }
}