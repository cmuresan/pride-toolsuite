package uk.ac.ebi.pride.data.core;

import java.util.Arrays;

/**
 * BinaryDataArray 
 * User: rwang
 * Date: 05-Feb-2010
 * Time: 14:13:09
 */
public class BinaryDataArray extends ParamGroup {
    private DataProcessing dataProcessing = null;
    private double[] binaryDoubleArray = null;

    public BinaryDataArray(DataProcessing dataProcessing, double[] binaryDoubleArr, ParamGroup params) {
        super(params);
        setDataProcessing(dataProcessing);
        setBinaryDoubleArray(binaryDoubleArr);
    }

    public double[] getBinaryDoubleArray() {
        return binaryDoubleArray == null ? null : Arrays.copyOf(binaryDoubleArray, binaryDoubleArray.length);
    }

    public void setBinaryDoubleArray(double[] binaryDoubleArr) {
        this.binaryDoubleArray = (binaryDoubleArr == null ? null : Arrays.copyOf(binaryDoubleArr, binaryDoubleArr.length));
    }

    public DataProcessing getDataProcessing() {
        return dataProcessing;
    }

    public void setDataProcessing(DataProcessing dataProcessing) {
        this.dataProcessing = dataProcessing;
    }
}
