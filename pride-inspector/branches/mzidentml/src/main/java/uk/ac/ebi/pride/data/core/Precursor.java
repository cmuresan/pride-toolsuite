package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * The method of precursor ion selection and activation
 * User: rwang
 * Date: 05-Feb-2010
 * Time: 14:03:21
 */
public class Precursor implements MassSpecObject {

    /**
     * the type and energy level used for activation
     */
    private ParamGroup activation = null;

    /**
     * For precursor spectra that are external to this document
     */
    private String externalSpectrumID = null;

    /**
     * the isolation window configured to isolate one or more ions
     */
    private ParamGroup isolationWindow = null;

    /**
     * a list of ions selected
     */
    private List<ParamGroup> selectedIons = null;

    /**
     * source file
     */
    private SourceFile sourceFile = null;

    /**
     * precursor spectrum
     */
    private Spectrum spectrum = null;

    /**
     * Constructor
     *
     * @param spectrum           optional.
     * @param sourceFile         optional.
     * @param externalSpectrumID optional.
     * @param isolationWindow    optional.
     * @param selectedIon        optional.
     * @param activation         required.
     */
    public Precursor(Spectrum spectrum, SourceFile sourceFile, String externalSpectrumID, ParamGroup isolationWindow,
                     List<ParamGroup> selectedIon, ParamGroup activation) {
        this.spectrum           = spectrum;
        this.sourceFile         = sourceFile;
        this.externalSpectrumID = externalSpectrumID;
        this.isolationWindow    = isolationWindow;
        this.selectedIons       = selectedIon;
        this.activation         = activation;
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

    public List<ParamGroup> getSelectedIons() {
        return selectedIons;
    }

    public void setSelectedIons(List<ParamGroup> selectedIon) {
        this.selectedIons = selectedIon;
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


//~ Formatted by Jindent --- http://www.jindent.com
