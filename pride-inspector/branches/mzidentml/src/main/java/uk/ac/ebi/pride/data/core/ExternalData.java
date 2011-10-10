package uk.ac.ebi.pride.data.core;

/**
 * Data external to the XML instance document.
 * The location of the data file is given in the location attribute.
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 09/08/11
 * Time: 14:35
 */
public class ExternalData extends Identifiable {
    /**
     * The location of the data file.
     */
    private String location = null;
    /**
     * The format of the ExternalData file, for example "tiff" for image files.
     */
    private CvParam FileFormat = null;
    /**
     * A URI to access documentation and tools to interpret the external format
     * of the ExternalData instance. For example, XML Schema or static libraries
     * (APIs) to access binary formats.
     */
    private String externalFormatDocumentationURI = null;

    /**
     *
     * @param id
     * @param name
     * @param location
     * @param fileFormat
     * @param externalFormatDocumentationURI
     */
    public ExternalData(String id, String name, String location, CvParam fileFormat, String externalFormatDocumentationURI) {
        super(id, name);
        this.location = location;
        FileFormat = fileFormat;
        this.externalFormatDocumentationURI = externalFormatDocumentationURI;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public CvParam getFileFormat() {
        return FileFormat;
    }

    public void setFileFormat(CvParam fileFormat) {
        FileFormat = fileFormat;
    }

    public String getExternalFormatDocumentationURI() {
        return externalFormatDocumentationURI;
    }

    public void setExternalFormatDocumentationURI(String externalFormatDocumentationURI) {
        this.externalFormatDocumentationURI = externalFormatDocumentationURI;
    }
}
