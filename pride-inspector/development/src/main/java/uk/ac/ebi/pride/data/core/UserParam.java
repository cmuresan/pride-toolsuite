package uk.ac.ebi.pride.data.core;

/**
 * Uncontrolled user parameters, allowing free text.
 * Should be created with caution.
 * <p/>
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:34:30
 */
public class UserParam extends Parameter {
    /**
     * the data type of the parameter
     */
    private String type = null;

    /**
     * Constructor
     *
     * @param name           required.
     * @param type           optional.
     * @param value          optional.
     * @param unitAcc        optional.
     * @param unitName       optional.
     * @param unitCVLookupID optional.
     */
    public UserParam(String name,
                     String type,
                     String value,
                     String unitAcc,
                     String unitName,
                     String unitCVLookupID) {

        super(name, value, unitAcc, unitName, unitCVLookupID);
        setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
