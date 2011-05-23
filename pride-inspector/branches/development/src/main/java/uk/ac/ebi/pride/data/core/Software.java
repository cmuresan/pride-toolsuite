package uk.ac.ebi.pride.data.core;

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
public class Software extends ParamGroup {

    /**
     * identifier for this software, could be software name
     */
    private String id = null;
    /**
     * software version
     */
    private String version = null;

    /**
     * Constructor
     *
     * @param id      required.
     * @param version required.
     * @param params  optional.
     */
    public Software(String id,
                    String version,
                    ParamGroup params) {
        super(params);
        setId(id);
        setVersion(version);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}