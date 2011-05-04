package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.CvTermReference;

import java.util.Arrays;
import java.util.List;

/**
 * A peak list including the underlying acquisitions.
 *
 * In mzML 1.1.0.1, it should the following cv terms:
 *
 * 1. It may have only one "scan polarity" (negative scan, positive scan).
 *
 * 2. It must have only one "spectrum type" (charge inversion mass spectrum,
 * constant neutral gain spectrum and et al)
 *
 * 3. It must have only one "spectrum representation" or any of its children.
 * (centroid spectrum, profile spectrum)
 *
 * 4. It may have one or more "spectrum attribute" (total ion current,
 * zoom scan, base peak m/z, ms level, spectrum title and etc al)
 *
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 08:46:05
 */
public class Spectrum extends MzGraph {
    
    /** the identifier for the spot on a MALDI or similar on */
    private String spotID = null;
    /** source file */
    private SourceFile sourceFile = null;
    /** list and descriptions of scans */
    private ScanList scanList = null;
    /** list and descriptions of precursor isolations to this spectrum*/
    private List<Precursor> precursors = null;
    /** list and descriptions of product isolations to this spectrum */
    private List<ParamGroup> products = null;
    /** list of fragment ions associate with this spectrum */
    private List<FragmentIon> fragmentIons = null;

    public Spectrum(String id,
                    int index,
                    String spotID,
                    DataProcessing dataProcessing,
                    int arrLength,
                    SourceFile sourceFile,
                    ScanList scanList,
                    List<Precursor> precursors,
                    List<ParamGroup> products,
                    List<BinaryDataArray> binaryArray,
                    ParamGroup params) {
        this(id, index, spotID, dataProcessing, arrLength, sourceFile,
             scanList, precursors, products, null, binaryArray, params);
    }

    /**
     * Constructor
     * @param id    required.
     * @param index required.
     * @param spotID    optional.
     * @param dataProcessing    optional.
     * @param arrLength required.
     * @param sourceFile    optional.
     * @param scanList  optional.
     * @param precursors    optional.
     * @param products  optional.
     * @param fragmentIons optional.
     * @param binaryArray   optional.
     * @param params    optional.
     */
    public Spectrum(String id,
                    int index,
                    String spotID,
                    DataProcessing dataProcessing,
                    int arrLength,
                    SourceFile sourceFile,
                    ScanList scanList,
                    List<Precursor> precursors,
                    List<ParamGroup> products,
                    List<FragmentIon> fragmentIons,
                    List<BinaryDataArray> binaryArray,
                    ParamGroup params) {
        super(id, index, dataProcessing,
              arrLength, binaryArray, params);
        setSpotID(spotID);
        setSourceFile(sourceFile);
        setScanList(scanList);
        setPrecursors(precursors);
        setProducts(products);
        setFragmentIons(fragmentIons);
    }

    public String getSpotID() {
        return spotID;
    }

    public void setSpotID(String spotID) {
        this.spotID = spotID;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public ScanList getScanList() {
        return scanList;
    }

    public void setScanList(ScanList scanList) {
        this.scanList = scanList;
    }

    public List<Precursor> getPrecursors() {
        return precursors;
    }

    public void setPrecursors(List<Precursor> precursors) {
        this.precursors = precursors;
    }

    public List<ParamGroup> getProducts() {
        return products;
    }

    public void setProducts(List<ParamGroup> products) {
        this.products = products;
    }

    public List<FragmentIon> getFragmentIons() {
        return fragmentIons;
    }

    public void setFragmentIons(List<FragmentIon> fragmentIons) {
        this.fragmentIons = fragmentIons;
    }

    public BinaryDataArray getMzBinaryDataArray() {
        return getBinaryDataArray(CvTermReference.MZ_ARRAY.getAccession());
    }

    public BinaryDataArray getIntensityBinaryDataArray() {
        return getBinaryDataArray(CvTermReference.INTENSITY_ARRAY.getAccession());
    }

    public int getNumberOfPeaks() {
        int numOfPeaks = -1;
        BinaryDataArray mzArr = this.getBinaryDataArrays().get(0);
        if (mzArr != null) {
            numOfPeaks = mzArr.getDoubleArray().length;
        }
        return numOfPeaks;
    }

    public double getMzRangeStart() {
        double start = -1;
        BinaryDataArray mzArr = getMzBinaryDataArray();
        if (mzArr != null) {
            double[] originalMzArr = mzArr.getDoubleArray();
            double[] doubleMzArr = Arrays.copyOf(originalMzArr, originalMzArr.length);
            Arrays.sort(doubleMzArr);
            if (doubleMzArr != null && doubleMzArr.length >= 1) {
                start = doubleMzArr[0];
            }
        }
        return start;
    }

    public double getMzRangeEnd() {
        double end = -1;
        BinaryDataArray mzArr = getMzBinaryDataArray();
        if (mzArr != null) {
            double[] originalMzArr = mzArr.getDoubleArray();
            double[] doubleMzArr = Arrays.copyOf(originalMzArr, originalMzArr.length);
            Arrays.sort(doubleMzArr);

            if (doubleMzArr != null && doubleMzArr.length >= 1) {
                end = doubleMzArr[doubleMzArr.length - 1];
            }
        }
        return end;
    }


    public double getIntentRangeStart() {
        double start = -1;
        BinaryDataArray intentArr = getIntensityBinaryDataArray();
        if (intentArr != null) {
            double[] originalIntentArr = intentArr.getDoubleArray();
            double[] doubleIntentArr = Arrays.copyOf(originalIntentArr, originalIntentArr.length);
            Arrays.sort(doubleIntentArr);
            if (doubleIntentArr != null && doubleIntentArr.length >= 1) {
                start = doubleIntentArr[0];
            }
        }
        return start;
    }


    public double getIntentRangeEnd() {
        double end = -1;
        BinaryDataArray intentArr = getIntensityBinaryDataArray();
        if (intentArr != null) {
            double[] originalIntentArr = intentArr.getDoubleArray();
            double[] doubleIntentArr = Arrays.copyOf(originalIntentArr, originalIntentArr.length);
            Arrays.sort(doubleIntentArr);
            if (doubleIntentArr != null && doubleIntentArr.length >= 1) {
                end = doubleIntentArr[doubleIntentArr.length - 1];
            }
        }
        return end;
    }
}