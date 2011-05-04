package uk.ac.ebi.pride.data.core;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 09:07:38
 */
public class Chromatogram extends MzGraph {

    public Chromatogram(String id,
                        BigInteger index,
                        Instrument instrument,
                        DataProcessing dataProcessing,
                        int arrLength,
                        SourceFile sourceFile,
                        Sample sample,
                        Date timeStamp,
                        List<Precursor> precursors,
                        List<Product> products,
                        List<BinaryDataArray> binaryArr,
                        ParamGroup params) {
        super(id, index, instrument, dataProcessing,
              arrLength, sourceFile, sample, timeStamp,
              precursors, products,
              binaryArr, params);
    }
}
