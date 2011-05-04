package uk.ac.ebi.pride.data.core;

/**
 * ToDo: make these methods final?
 * ToDo: add Param type constants ?
 * ToDo: ClassName? this shouldn't be needed for PRIDE 3
 * ToDo: Parent_element_fk? from PRIDE 2 database
 * ToDo: do we realy need index parameter for CvParam?
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:36:25
 */
public abstract class Parameter {
    
    private String name = null;
    private String value = null;
    private String unitAcc = null;
    private String unitName = null;
    private String unitCVLookupID = null;
    private int index = -1;
    private boolean internal = false;
    /** from PRIDE database */
    private int paramType = -1;

    public Parameter(String name,
                     String value,
                     String unitAcc,
                     String unitName,
                     String unitCVLookupID,
                     int index,
                     boolean internal) {
        this.name = name;
        this.value = value;
        this.unitAcc = unitAcc;
        this.unitName = unitName;
        this.unitCVLookupID = unitCVLookupID;
        this.index = index;
        this.internal = internal;
    }

    public int getParamType() {
        return paramType;
    }

    public void setParamType(int paramType) {
        this.paramType = paramType;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    // value - optional
    public String getValue(){
        return value;
    }
    
    public void setValue(String v) {
        value = v;
    }

    // Unit accession - mzML - optional
    public String getUnitAcc() {
        return unitAcc;    
    }

    public void setUnitAcc(String ua) {
        unitAcc = ua;
    }

    // Unit name - mzML - optional
    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String un) {
        unitName = un;
    }

    // Unit CV Reference - mzML - optional
    public String getUnitCVLookupID() {
        return unitCVLookupID;
    }
    
    public void setUnitCVLookupID(String unitCVRef) {
        unitCVLookupID = unitCVRef;
    }

    // index - PRIDE XML - optional
    public int getIndex() {
        return index;
    }

    public void setIndex(int idx) {
        index = idx;
    }

    // internal
    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean itnal) {
        internal = itnal;
    }
}