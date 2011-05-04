package uk.ac.ebi.pride.data.core;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:33:02
 */
public class CvParam extends Parameter {

    private String accession = null;
    private String cvLookupID = null;
    // ToDo: OntologyLookup object ?

    public CvParam(String accession, String name, String cvLookupID,
                   String value, String unitAcc, String unitName,
                   String unitCVLookupID, int index, boolean internal) {
        super(name, value, unitAcc, unitName, unitCVLookupID, index, internal);
        this.accession = accession;
        this.cvLookupID = cvLookupID;
    }

    /**
     * cvRef - both PRIDE XML and mzML
     * @return
     */
    public String getCvLookupID() {
        return cvLookupID;
    }

    public void setCvLookupID(String cvLookupID) {
        this.cvLookupID = cvLookupID;
    }

    /**
     * accession
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getAccession() {
        return accession;
    }

}
