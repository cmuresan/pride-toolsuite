package uk.ac.ebi.pride.data.core;

/**
 * An Analysis which assembles a set of peptides (e.g. from a spectra search analysis) to proteins.
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 05/08/11
 * Time: 15:27
 */
public class ProteinDetection extends ProtocolApplication{
    /**
     * A reference to the ProteinDetectionList.
     */
    private ProteinDetectionList proteinDetectionListRef = null;
    /**
     * A reference to the detection protocol used for this ProteinDetection.
     */
    private Protocol proteinDetectionProtocolRef = null;
    /**
     * A reference to the list of spectrum identifications that were input to the process.
     */
    private SpectrumIdentificationList spectrumIdentificationListRef = null;


    public ProteinDetection(String id, String name, String activeDate, ProteinDetectionList proteinDetectionListRef, Protocol proteinDetectionProtocolRef, SpectrumIdentificationList spectrumIdentificationListRef) {
        super(id, name, activeDate);
        this.proteinDetectionListRef = proteinDetectionListRef;
        this.proteinDetectionProtocolRef = proteinDetectionProtocolRef;
        this.spectrumIdentificationListRef = spectrumIdentificationListRef;
    }

    public ProteinDetectionList getProteinDetectionListRef() {
        return proteinDetectionListRef;
    }

    public void setProteinDetectionListRef(ProteinDetectionList proteinDetectionListRef) {
        this.proteinDetectionListRef = proteinDetectionListRef;
    }

    public Protocol getProteinDetectionProtocolRef() {
        return proteinDetectionProtocolRef;
    }

    public void setProteinDetectionProtocolRef(Protocol proteinDetectionProtocolRef) {
        this.proteinDetectionProtocolRef = proteinDetectionProtocolRef;
    }

    public SpectrumIdentificationList getSpectrumIdentificationListRef() {
        return spectrumIdentificationListRef;
    }

    public void setSpectrumIdentificationListRef(SpectrumIdentificationList spectrumIdentificationListRef) {
        this.spectrumIdentificationListRef = spectrumIdentificationListRef;
    }
}
