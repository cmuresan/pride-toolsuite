package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.CvTermReference;

import java.util.List;

/**
 * The method of precursor ion selection and activation
 * User: rwang
 * Date: 05-Feb-2010
 * Time: 14:03:21
 */
public class Precursor implements MassSpecObject {
    /**
     * precursor spectrum
     */
    private Spectrum spectrum = null;
    /**
     * source file
     */
    private SourceFile sourceFile = null;
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
     * the type and energy level used for activation
     */
    private ParamGroup activation = null;

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
    public Precursor(Spectrum spectrum,
                     SourceFile sourceFile,
                     String externalSpectrumID,
                     ParamGroup isolationWindow,
                     List<ParamGroup> selectedIon,
                     ParamGroup activation) {
        setSpectrum(spectrum);
        setSourceFile(sourceFile);
        setExternalSpectrumID(externalSpectrumID);
        setIsolationWindow(isolationWindow);
        setSelectedIons(selectedIon);
        setActivation(activation);
    }

    public ParamGroup getActivation() {
        return activation;
    }

    public void setActivation(ParamGroup activation) {
        if (activation == null) {
            throw new IllegalArgumentException("Precursor's activation can not be NULL");
        } else {
            this.activation = activation;
        }
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

    /**
     * Get the ion charge for a selected ion.
     *
     * @param index index of the selected ion.
     * @return Double   selected ion charge.
     */
    public Double getSelectedIonCharge(int index) {
        return getSelectedIonCvParamValue(index, CvTermReference.PSI_ION_SELECTION_CHARGE_STATE, CvTermReference.ION_SELECTION_CHARGE_STATE);
    }

    /**
     * Get the ion m/z value for a selected ion.
     *
     * @param index index of the selected ion.
     * @return Double   selected ion m/z.
     */
    public Double getSelectedIonMz(int index) {
        return getSelectedIonCvParamValue(index, CvTermReference.PSI_ION_SELECTION_MZ, CvTermReference.ION_SELECTION_MZ);
    }


    /**
     * Get the ion intensity value for a selected ion.
     * @param index index of the selected ion.
     * @return Double   selected ion intensity.
     */
    public Double getSelectedIonIntensity(int index) {
        return getSelectedIonCvParamValue(index, CvTermReference.PSI_ION_SELECTION_INTENSITY, CvTermReference.ION_SELECTION_INTENSITY);
    }

    /**
     * Get value of selected ion cv param.
     *
     * @param index index of the selecte ion.
     * @param refs  a list of possible cv terms to search for.
     * @return Double   value of the cv parameter.
     */
    private Double getSelectedIonCvParamValue(int index, CvTermReference... refs) {
        Double value = null;
        if (index >= 0 && selectedIons != null && index < selectedIons.size()) {
            ParamGroup selectedIon = selectedIons.get(index);
            // search PRIDE xml based charge
            for (CvTermReference ref : refs) {
                List<CvParam> cvParams = selectedIon.getCvParam(ref.getCvLabel(), ref.getAccession());
                if (cvParams != null && !cvParams.isEmpty()) {
                    value = new Double(cvParams.get(0).getValue());
                }
            }
        }
        return value;
    }
}


