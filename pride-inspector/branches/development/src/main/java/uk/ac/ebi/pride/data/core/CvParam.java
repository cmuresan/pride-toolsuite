package uk.ac.ebi.pride.data.core;

/**
 * This object holds additional data in controlled vocabulary.
 * <p/>
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:33:02
 */
public class CvParam extends Parameter {

    /**
     * Cv term accession
     */
    private String accession = null;
    /**
     * Id of the referenced CvLookup object
     */
    private String cvLookupID = null;

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
    public CvParam(String accession,
                   String name,
                   String cvLookupID,
                   String value,
                   String unitAcc,
                   String unitName,
                   String unitCVLookupID) {
        super(name, value, unitAcc, unitName, unitCVLookupID);
        setAccession(accession);
        setCvLookupID(cvLookupID);
    }

    public String getCvLookupID() {
        return cvLookupID;
    }

    public void setCvLookupID(String cvLookupID) {
        this.cvLookupID = cvLookupID;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getAccession() {
        return accession;
    }

}
