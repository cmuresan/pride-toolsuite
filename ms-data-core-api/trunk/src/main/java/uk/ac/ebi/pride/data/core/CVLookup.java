package uk.ac.ebi.pride.data.core;

/**
 * CVLookup is referenced in every CvParam.
 * It serves as a reference to the original controlled vocabulary source.
 * <p/>
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 16:16:31
 */
public class CVLookup implements MassSpecObject {

    /**
     * The URI for the controlled vocabulary
     */
    private String address = null;

    /**
     * Cv Label name, it is also the Id for this CVLookup
     */
    private String cvLabel = null;

    /**
     * The full name of the controlled vocabulary
     */
    private String fullName = null;

    /**
     * The version of the controlled vocabulary
     */
    private String version = null;

    /**
     * Constructor
     *
     * @param cvLabel  required.
     * @param fullName required.
     * @param version  optional.
     * @param address  required.
     */
    public CVLookup(String cvLabel, String fullName, String version, String address) {
        setCvLabel(cvLabel);
        setFullName(fullName);
        setVersion(version);
        setAddress(address);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCvLabel() {
        return cvLabel;
    }

    public void setCvLabel(String cvLabel) {
        this.cvLabel = cvLabel;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}



