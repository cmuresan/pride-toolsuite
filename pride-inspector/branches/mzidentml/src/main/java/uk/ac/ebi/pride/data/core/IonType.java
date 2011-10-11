package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.Map;

/**
 * IonType defines the index of fragmentation ions being reported,
 * importing a CV term for the type of ion e.g. b ion. Example: if b3 b7 b8 and b10
 * have been identified, the index attribute will contain 3 7 8 10, and the
 * corresponding values will be reported in parallel arrays below.
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 08/08/11
 * Time: 14:01
 */
public class IonType extends CvParam {

    /**
     * The charge of the identified fragmentation ions.
     */
    private int charge = -1;

    /**
     * The index of ions identified as integers, following standard notation for
     * a-c, x-z e.g. if b3 b5 and b6 have been identified, the index would store
     * "3 5 6". For internal ions, the index contains pairs defining the start
     * and end point - see specification document for examples.
     * For immonium ions, the index is the position of the identified ion within
     * the peptide sequence - if the peptide contains the same amino acid in
     * multiple positions that cannot be distinguished, all positions should be
     * given.
     */
    List<Integer> index = null;

    /**
     * An array of values for a given type of measure and for a particular ion
     * type, in parallel to the index of ions identified.
     */
    private Map<IdentifiableParamGroup, List<Integer>> measureListHashMap = null;

    /**
     * Constructor
     *
     * @param accession      required.
     * @param name           required.
     * @param cvLookupID     required.
     * @param value          optional.
     * @param unitAcc        optional.
     * @param unitName       optional.
     * @param unitCVLookupID optional.
     */
    public IonType(String accession, String name, String cvLookupID, String value, String unitAcc, String unitName,
                   String unitCVLookupID) {
        super(accession, name, cvLookupID, value, unitAcc, unitName, unitCVLookupID);
    }

    public List<Integer> getIndex() {
        return index;
    }

    public void setIndex(List<Integer> index) {
        this.index = index;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public Map<IdentifiableParamGroup, List<Integer>> getMeasureListHashMap() {
        return measureListHashMap;
    }

    public void setMeasureListHashMap(Map<IdentifiableParamGroup, List<Integer>> measureListHashMap) {
        this.measureListHashMap = measureListHashMap;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
