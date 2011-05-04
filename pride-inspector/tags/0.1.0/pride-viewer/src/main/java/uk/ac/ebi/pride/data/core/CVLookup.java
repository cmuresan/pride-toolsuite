package uk.ac.ebi.pride.data.core;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 16:16:31
 */
public class CVLookup {

    /** map to mzML's cvList id */
    private String cvLabel = null;
    private String fullName = null;
    private String version = null;
    private String address = null;

    public CVLookup(String cvLabel, String fullName, String version, String address) {
        this.cvLabel = cvLabel;
        this.fullName = fullName;
        this.version = version;
        this.address = address;
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
