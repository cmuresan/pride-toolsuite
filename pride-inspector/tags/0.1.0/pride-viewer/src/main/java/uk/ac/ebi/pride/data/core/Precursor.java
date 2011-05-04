package uk.ac.ebi.pride.data.core;

import java.util.Collection;
import java.util.List;

/**
 * The method of precursor ion selection and activation
 * User: rwang
 * Date: 05-Feb-2010
 * Time: 14:03:21
 */
public class Precursor {

    // ToDo: is this necessary?
    private int msLevel =  -1;
    private Spectrum spectrum = null;
    private SourceFile sourceFile = null;
    
    /** For precursor spectra that are external to this document */
    private String externalSpectrumID = null;

    private ParamGroup isolationWindow = null;

    private List<ParamGroup> selectedIon = null;

    private ParamGroup activation = null;

    public Precursor(ParamGroup activation,
                     String externalSpectrumID,
                     ParamGroup isolationWindow,
                     int msLevel,
                     List<ParamGroup> selectedIon,
                     SourceFile sourceFile,
                     Spectrum spectrum) {
        this.activation = activation;
        this.externalSpectrumID = externalSpectrumID;
        this.isolationWindow = isolationWindow;
        this.msLevel = msLevel;
        this.selectedIon = selectedIon;
        this.sourceFile = sourceFile;
        this.spectrum = spectrum;
    }

    public int getMsLevel() {
        return msLevel;
    }

    public void setMsLevel(int msLevel) {
        this.msLevel = msLevel;
    }

    public ParamGroup getActivation() {
        return activation;
    }

    public void setActivation(ParamGroup activation) {
        this.activation = activation;
    }

    public String getExternalSpectrumID() {
        return externalSpectrumID;
    }

    public void setExternalSpectrumID(String externalSpectrumID) {
        this.externalSpectrumID = externalSpectrumID;
    }

    public ParamGroup getIsolationWindow() {
        return isolationWindow;
    }

    public void setIsolationWindow(ParamGroup isolationWindow) {
        this.isolationWindow = isolationWindow;
    }

    public List<ParamGroup> getSelectedIon() {
        return selectedIon;
    }

    public void setSelectedIon(List<ParamGroup> selectedIon) {
        this.selectedIon = selectedIon;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }
}
