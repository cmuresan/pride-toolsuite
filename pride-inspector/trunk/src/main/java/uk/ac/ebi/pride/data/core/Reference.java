package uk.ac.ebi.pride.data.core;

/**
 * Reference is added by PRIDE XML 2.0.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:45:42
 */
public class Reference extends ParamGroup {

    /**
     * the full reference line
     */
    private String fullReference = null;
    //ToDo: toHTML() ?


    /**
     * Constructor
     *
     * @param fullReference required.
     * @param params        optional.
     */
    public Reference(String fullReference,
                     ParamGroup params) {
        super(params);
        this.fullReference = fullReference;
    }

    public String getFullReference() {
        return fullReference;
    }

    public void setFullReference(String fullReference) {
        this.fullReference = fullReference;
    }
}
