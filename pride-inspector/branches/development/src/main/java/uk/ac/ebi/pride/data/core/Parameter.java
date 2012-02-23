package uk.ac.ebi.pride.data.core;

/**
 * Parameter exist in neither PRIDE XML nor mzML, it created for method sharing.
 * <p/>
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:36:25
 */
public abstract class Parameter implements MassSpecObject {

    /**
     * The name of cv term
     */
    private String name = null;
    /**
     * The value of cv term
     */
    private String value = null;
    /**
     * The unit accession number from controlled vocabulary
     */
    private String unitAcc = null;
    /**
     * The unit name of unit cv term
     */
    private String unitName = null;
    /**
     * The cvlookup id for unit
     */
    private String unitCVLookupID = null;

    /**
     * Constructor
     *
     * @param name           required.
     * @param value          optional.
     * @param unitAcc        optional.
     * @param unitName       optional.
     * @param unitCVLookupID optional.
     */
    public Parameter(String name,
                     String value,
                     String unitAcc,
                     String unitName,
                     String unitCVLookupID) {
        this.name = name;
        this.value = value;
        this.unitAcc = unitAcc;
        this.unitName = unitName;
        this.unitCVLookupID = unitCVLookupID;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String v) {
        value = v;
    }

    public String getUnitAcc() {
        return unitAcc;
    }

    public void setUnitAcc(String ua) {
        unitAcc = ua;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String un) {
        unitName = un;
    }

    public String getUnitCVLookupID() {
        return unitCVLookupID;
    }

    public void setUnitCVLookupID(String unitCVRef) {
        unitCVLookupID = unitCVRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;
        if (unitAcc != null ? !unitAcc.equals(parameter.unitAcc) : parameter.unitAcc != null) return false;
        if (unitCVLookupID != null ? !unitCVLookupID.equals(parameter.unitCVLookupID) : parameter.unitCVLookupID != null)
            return false;
        if (unitName != null ? !unitName.equals(parameter.unitName) : parameter.unitName != null) return false;
        if (value != null ? !value.equals(parameter.value) : parameter.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (unitAcc != null ? unitAcc.hashCode() : 0);
        result = 31 * result + (unitName != null ? unitName.hashCode() : 0);
        result = 31 * result + (unitCVLookupID != null ? unitCVLookupID.hashCode() : 0);
        return result;
    }
}