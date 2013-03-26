package uk.ac.ebi.pride.data.core;

/**
 * An Analysis which assembles a set of peptides (e.g. from a spectra search analysis) to proteins.
 * User: yperez
 * Date: 05/08/11
 * Time: 15:27
 */
public class ProteinDetection extends ProtocolApplication {

    /**
     * A reference to the ProteinDetectionList.
     */
    private ProteinDetectionList proteinDetectionList;

    /**
     * A reference to the detection protocol used for this ProteinDetection.
     */
    private Protocol proteinDetectionProtocol;

    /**
     * A reference to the list of spectrum identifications that were input to the process.
     */
    private SpectrumIdentificationList spectrumIdentificationList;

    public ProteinDetection(String id, String name, String activeDate,
                            ProteinDetectionList proteinDetectionList,
                            Protocol proteinDetectionProtocol,
                            SpectrumIdentificationList spectrumIdentificationList) {
        super(id, name, activeDate);
        this.proteinDetectionList = proteinDetectionList;
        this.proteinDetectionProtocol = proteinDetectionProtocol;
        this.spectrumIdentificationList = spectrumIdentificationList;
    }

    public ProteinDetectionList getProteinDetectionList() {
        return proteinDetectionList;
    }

    public void setProteinDetectionList(ProteinDetectionList proteinDetectionList) {
        this.proteinDetectionList = proteinDetectionList;
    }

    public Protocol getProteinDetectionProtocol() {
        return proteinDetectionProtocol;
    }

    public void setProteinDetectionProtocol(Protocol proteinDetectionProtocol) {
        this.proteinDetectionProtocol = proteinDetectionProtocol;
    }

    public SpectrumIdentificationList getSpectrumIdentificationList() {
        return spectrumIdentificationList;
    }

    public void setSpectrumIdentificationList(SpectrumIdentificationList spectrumIdentificationList) {
        this.spectrumIdentificationList = spectrumIdentificationList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ProteinDetection that = (ProteinDetection) o;

        return !(proteinDetectionList != null ? !proteinDetectionList.equals(that.proteinDetectionList) : that.proteinDetectionList != null) && !(proteinDetectionProtocol != null ? !proteinDetectionProtocol.equals(that.proteinDetectionProtocol) : that.proteinDetectionProtocol != null) && !(spectrumIdentificationList != null ? !spectrumIdentificationList.equals(that.spectrumIdentificationList) : that.spectrumIdentificationList != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (proteinDetectionList != null ? proteinDetectionList.hashCode() : 0);
        result = 31 * result + (proteinDetectionProtocol != null ? proteinDetectionProtocol.hashCode() : 0);
        result = 31 * result + (spectrumIdentificationList != null ? spectrumIdentificationList.hashCode() : 0);
        return result;
    }
}



