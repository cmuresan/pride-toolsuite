package uk.ac.ebi.pride.data.core;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ToDo: change the order of constructor's params
 * Note: PRIDE XML's supDesc, supDataArrayBinary and supDataArray have never been used.
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 08:46:05
 */
public class Spectrum extends MzGraph {
    
    // mzML
    private String spotID = null;

    private ScanList scanList = null;

    public Spectrum(String id,
                    BigInteger index,
                    String spotID,
                    Instrument instrument,
                    DataProcessing dataProcessing,
                    int arrLength,
                    SourceFile sourceFile,
                    Sample sample,
                    Date timeStamp,
                    ScanList scanList,
                    List<Precursor> precursors,
                    List<Product> products,
                    List<BinaryDataArray> binaryArray,
                    ParamGroup params) {
        super(id, index, instrument, dataProcessing,
              arrLength, sourceFile, sample,
              timeStamp, precursors, products,
              binaryArray, params);
        this.spotID = spotID;
        this.scanList = scanList;
    }

    public ScanList getScanList() {
        return scanList;
    }

    public void setScanList(ScanList scanList) {
        this.scanList = scanList;
    }

    public String getSpotID() {
        return spotID;
    }

    public void setSpotID(String spotID) {
        this.spotID = spotID;
    }

    public int getNumberOfPeaks() {
        int numOfPeaks = -1;
        BinaryDataArray mzArr = this.getBinaryDataArrays().get(0);
        if (mzArr != null) {
            numOfPeaks = mzArr.getBinaryDoubleArray().length;            
        }
        return numOfPeaks;
    }

    public double getMzRangeStart() {
        double start = -1;
        BinaryDataArray mzArr = this.getBinaryDataArrays().get(0);
        if (mzArr != null) {
            double[] originalMzArr = mzArr.getBinaryDoubleArray();
            double[] doubleMzArr = Arrays.copyOf(originalMzArr, originalMzArr.length);
            Arrays.sort(doubleMzArr);
            start = doubleMzArr[0];
        }
        return start;
    }

    public double getMzRangeEnd() {
        double start = -1;
        BinaryDataArray mzArr = this.getBinaryDataArrays().get(0);
        if (mzArr != null) {
            double[] originalMzArr = mzArr.getBinaryDoubleArray();
            double[] doubleMzArr = Arrays.copyOf(originalMzArr, originalMzArr.length);
            Arrays.sort(doubleMzArr);
            start = doubleMzArr[doubleMzArr.length - 1];
        }
        return start;
    }


    public double getIntentRangeStart() {
        double start = -1;
        BinaryDataArray intentArr = this.getBinaryDataArrays().get(1);
        if (intentArr != null) {
            double[] originalIntentArr = intentArr.getBinaryDoubleArray();
            double[] doubleIntentArr = Arrays.copyOf(originalIntentArr, originalIntentArr.length);
            Arrays.sort(doubleIntentArr);
            start = doubleIntentArr[0];
        }
        return start;
    }


    public double getIntentRangeEnd() {
        double end = -1;
        BinaryDataArray intentArr = this.getBinaryDataArrays().get(1);
        if (intentArr != null) {
            double[] originalIntentArr = intentArr.getBinaryDoubleArray();
            double[] doubleIntentArr = Arrays.copyOf(originalIntentArr, originalIntentArr.length);
            Arrays.sort(doubleIntentArr);
            end = doubleIntentArr[doubleIntentArr.length - 1];
        }
        return end;
    }
}