package uk.ac.ebi.pride.data.core;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:34:30
 */
public class UserParam extends Parameter {
    // ToDo: mzML also has a type element, indicate the data type ?
    // Note: mzidentml doesn't seem to have this
    public UserParam(String name,
                     String value,
                     String unitAcc,
                     String unitName,
                     String unitCVLookupID,
                     int index,
                     boolean internal) {
        super(name, value, unitAcc, unitName, unitCVLookupID, index, internal);
    }
}
