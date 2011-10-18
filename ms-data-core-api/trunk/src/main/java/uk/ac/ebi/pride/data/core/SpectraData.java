package uk.ac.ebi.pride.data.core;

/**
 * A data set containing spectra data (consisting of one or more spectra).
 * User: yperez
 * Date: 08/08/11
 * Time: 12:07
 */
public class SpectraData extends ExternalData {
    private CvParam spectrumIdFormat = null;

    /**
     * @param id
     * @param name
     * @param location
     * @param fileFormat
     * @param externalFormatDocumentationURI
     * @param spectrumIdFormat
     */
    public SpectraData(String id, String name, String location, CvParam fileFormat,
                       String externalFormatDocumentationURI, CvParam spectrumIdFormat) {
        super(id, name, location, fileFormat, externalFormatDocumentationURI);
        this.spectrumIdFormat = spectrumIdFormat;
    }

    public CvParam getSpectrumIdFormat() {
        return spectrumIdFormat;
    }

    public void setSpectrumIdFormat(CvParam spectrumIdFormat) {
        this.spectrumIdFormat = spectrumIdFormat;
    }
}



