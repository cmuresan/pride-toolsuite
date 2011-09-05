package uk.ac.ebi.pride.data.coreIdent;

/**
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
     * software version
     */
    private String version = null;
    /**
     * URI of the analysis software e.g. manufacturer's website
     */
    private String uri = null;
    /**
     * A reference to the Contact person that provide the mzIdentMl File.
     * (mzIndetMl description: When a ContactRole is used, it specifies which Contact the role is associated with.
     */
    private AbstractContact contact  = null;
    /**
     * Any customizations to the software, such as alternative scoring mechanisms implemented,
     * should be documented here as free text. The is very important at the for MzIdentML
     */
    private String customization = null;

    /**
     *
     * @param id
     * @param name
     * @param version
     * @param uri
     * @param contact
     * @param customization
     */
    public Software(Comparable id,
                    String name,
                    String version,
                    String uri,
                    AbstractContact contact,
                    String customization) {
        super(id, name);
        this.version = version;
        this.uri = uri;
        this.contact = contact;
        this.customization = customization;
    }

    /**
     * mZIdentMl Software Object
     * @param params
     * @param id
     * @param name
     * @param version
     * @param uri
     * @param contact
     * @param customization or comments
     */
    public Software(ParamGroup params,
                    Comparable id,
                    String name,
                    String version,
                    String uri,
                    AbstractContact contact,
                    String customization) {
        super(params, id, name);
        this.version = version;
        this.uri = uri;
        this.contact = contact;
        this.customization = customization;
    }

    /**
     * Pride Software Object
     * @param id
     * @param name
     * @param version
     * @param customization or comments
     */
    public Software(Comparable id,
                    String name,
                    String version,
                    String customization) {
        super(id, name);
        this.version = version;
        this.customization = customization;
    }

    /**
     * Pride Software Object
     * @param params
     * @param id
     * @param name
     * @param version
     * @param customization
     */
    public Software(ParamGroup params,
                    Comparable id,
                    String name,
                    String version,
                    String customization) {
        super(params, id, name);
        this.version = version;
        this.customization = customization;
    }

    public Software(ParamGroup params, Comparable id, String name, String version){
        super(params,id,null);
        this.version = version;
        this.uri = null;
        this.contact = null;
        this.customization = null;
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
}