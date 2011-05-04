package uk.ac.ebi.pride.data.core;

import java.util.Collection;
import java.util.List;

/**
 * Scan or acquisition from original raw file used to create this
 * peak list, as specified in sourceFile
 * User: rwang
 * Date: 20-Feb-2010
 * Time: 18:58:53
 */
public class Scan extends ParamGroup {
    private String spectrumRef = null;
    private SourceFile sourceFile = null;
    private Instrument instrument = null;
    private List<ParamGroup> scanWindows = null;

    public Scan(String spectrum, SourceFile sourceFile,
                Instrument instrument, List<ParamGroup> scanWindows,
                ParamGroup params) {
        super(params);
        this.spectrumRef = spectrum;
        this.sourceFile = sourceFile;
        this.instrument = instrument;
        this.scanWindows = scanWindows;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getSpectrumRef() {
        return spectrumRef;
    }

    public void setSpectrumRef(String spectrumRef) {
        this.spectrumRef = spectrumRef;
    }

    public List<ParamGroup> getScanWindows() {
        return scanWindows;
    }

    public void setScanWindows(List<ParamGroup> scanWindows) {
        this.scanWindows = scanWindows;
    }
}
