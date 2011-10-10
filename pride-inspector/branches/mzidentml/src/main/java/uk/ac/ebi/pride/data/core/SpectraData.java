package uk.ac.ebi.pride.data.core;



/**
 * A data set containing spectra data (consisting of one or more spectra).
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 08/08/11
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class SpectraData extends ExternalData{

    private CvParam spectrumIdFormat = null;
    /**
     * The format of the spectrum identifier within the source file
     */

    /**
     *
     * @param id
     * @param name
     * @param location
     * @param fileFormat
     * @param externalFormatDocumentationURI
     * @param spectrumIdFormat
     */
    public SpectraData(String id, String name, String location, CvParam fileFormat, String externalFormatDocumentationURI, CvParam spectrumIdFormat) {
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
