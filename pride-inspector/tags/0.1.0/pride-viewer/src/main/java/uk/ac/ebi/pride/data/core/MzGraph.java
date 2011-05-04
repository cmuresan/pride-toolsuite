package uk.ac.ebi.pride.data.core;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 05-Feb-2010
 * Time: 15:41:15
 */
public class MzGraph extends ParamGroup {

    private String id = null;
    // mzML
    /** zero-based, consecutive index of the specturm in the spectrum list */
    private BigInteger index = null;

    private Instrument instrument = null;
    // mzML
    private DataProcessing dataProcessing = null;
    // mzML
    private int defaultArrayLength = -1;

    private SourceFile sourceFile = null;

    private Sample sample = null;

    private Date startTimeStamp = null;

    private List<Precursor> precursors = null;

    private List<Product> products = null;

    private List<BinaryDataArray> binaryDataArrays = null;

    public MzGraph(String id,
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
        super(params);
        this.id = id;
        this.index = index;
        this.instrument = instrument;
        this.dataProcessing = dataProcessing;
        this.defaultArrayLength = arrLength;
        this.sourceFile = sourceFile;
        this.sample = sample;
        setStartTimeStamp(timeStamp);
        this.binaryDataArrays = binaryArr;
        this.precursors = precursors;
        this.products = products;
    }

    public List<BinaryDataArray> getBinaryDataArrays() {
        return binaryDataArrays;
    }

    public void setBinaryDataArrays(List<BinaryDataArray> binaryDataArrays) {
        this.binaryDataArrays = binaryDataArrays;
    }

    public DataProcessing getDataProcessing() {
        return dataProcessing;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigInteger getIndex() {
        return index;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Date getStartTimeStamp() {
        return startTimeStamp == null ? null : new Date(startTimeStamp.getTime());
    }

    public void setStartTimeStamp(Date startTimeStamp) {
        this.startTimeStamp = (startTimeStamp == null ? null : new Date(startTimeStamp.getTime()));
    }

    public List<Precursor> getPrecursors() {
        return precursors;
    }

    public void setPrecursors(List<Precursor> precursors) {
        this.precursors = precursors;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
