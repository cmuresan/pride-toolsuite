package uk.ac.ebi.pride.data.core;

/**
 * <p/>
 * Software details.
 * <p/>
 * In mzML 1.1.0.1, the follow cv terms must be included:
 * <p/>
 * 1. Must have one "software" term (Xcalbur, Bioworks, Masslynx and et al).
 * <p/>
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 16:06:45
 */
public class Software extends IdentifiableParamGroup {

    /**
     * A reference to the Contact person that provide the mzIdentMl File.
     * (mzIndetMl description: When a ContactRole is used, it specifies which Contact the role is associated with.
     */
    private AbstractContact contact = null;

    /**
     * Any customizations to the software, such as alternative scoring mechanisms implemented,
     * should be documented here as free text. The is very important at the for MzIdentML
     */
    private String customization = null;

    /**
     * URI of the analysis software e.g. manufacturer's website
     */
    private String uri = null;

    /**
     * software version
     */
    private String version = null;

    /**
     * Constructor of Software Object without ParamGroup
     *
     * @param id ID
     * @param name Name
     * @param contact Contact (Person or Organization)
     * @param customization Customization Description
     * @param uri UrI of the Software
     * @param version Version
     */
    public Software(Comparable id,
                    String name,
                    AbstractContact contact,
                    String customization,
                    String uri,
                    String version) {
        super(id, name);
        this.contact = contact;
        this.customization = customization;
        this.uri = uri;
        this.version = version;
    }

    /**
     * Constructor with (CvParam List and User Param List) Information
     *
     * @param params ParamGroup (CvParam List and User Param List)
     * @param id ID
     * @param name Name
     * @param contact Contact (Person or Organization)
     * @param customization Customization Description
     * @param uri UrI of the Software
     * @param version Version
     */
    public Software(ParamGroup params,
                    Comparable id,
                    String name,
                    AbstractContact contact,
                    String customization,
                    String uri, String version) {
        super(params, id, name);
        this.contact = contact;
        this.customization = customization;
        this.uri = uri;
        this.version = version;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public AbstractContact getContact() {
        return contact;
    }

    public void setContact(AbstractContact contact) {
        this.contact = contact;
    }

    public String getCustomization() {
        return customization;
    }

    public void setCustomization(String customization) {
        this.customization = customization;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Software software = (Software) o;

        return !(contact != null ? !contact.equals(software.contact) : software.contact != null) && !(customization != null ? !customization.equals(software.customization) : software.customization != null) && !(uri != null ? !uri.equals(software.uri) : software.uri != null) && !(version != null ? !version.equals(software.version) : software.version != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (contact != null ? contact.hashCode() : 0);
        result = 31 * result + (customization != null ? customization.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}



