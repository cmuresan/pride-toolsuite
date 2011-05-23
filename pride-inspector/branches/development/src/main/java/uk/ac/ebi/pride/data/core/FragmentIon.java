package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.util.List;

/**
 * FragmentIon stores details about peptide fragment ion information.
 * Note: FragmentIon represents a ParamGroup object, but it also provides
 * a list of convenient methods to access the values of m/z, intensity and etc.
 * <p/>
 * User: rwang
 * Date: 04-Jun-2010
 * Time: 11:33:37
 */
public class FragmentIon extends ParamGroup {
    // todo: incorrect default values
    /**
     * m/z value
     */
    private double mz = -1;
    /**
     * intensity of the fragment ion
     */
    private double intensity = -1;
    /**
     * mass error margin of the fragment ion
     */
    private double massError = -1;
    /**
     * retention time error margin of the fragment ion
     */
    private double retentionTimeError = -1;
    /**
     * charge of the fragment ion
     */
    private int charge = 0;
    /**
     * ion type
     */
    private String ionType = null;
    /**
     * location
     */
    private int location = -1;
    /**
     * ion type accession
     */
    private String ionTypeAccession = null;


    /**
     * Constructor
     *
     * @param params required.
     */
    public FragmentIon(ParamGroup params) {
        super(params);
        init();
    }

    private void init() {
        List<CvParam> cvParams = this.getCvParams();

        for (CvParam cvParam : cvParams) {
            String accession = cvParam.getAccession();
            String value = cvParam.getValue();

            if (CvTermReference.PRODUCT_ION_MZ.getAccession().equals(accession)
                    || CvTermReference.PRODUCT_ION_MZ_PLGS.getAccession().equals(accession)) {
                mz = NumberUtilities.isNumber(value) ? Double.parseDouble(value) : mz;
            } else if (CvTermReference.PRODUCT_ION_INTENSITY.getAccession().equals(accession)
                    || CvTermReference.PRODUCT_ION_INTENSITY_PLGS.getAccession().equals(accession)) {
                intensity = NumberUtilities.isNumber(value) ? Double.parseDouble(value) : intensity;
            } else if (CvTermReference.PRODUCT_ION_MASS_ERROR.getAccession().equals(accession)
                    || CvTermReference.PRODUCT_ION_MASS_ERROR_PLGS.getAccession().equals(accession)) {
                massError = NumberUtilities.isNumber(value) ? Double.parseDouble(value) : massError;
            } else if (CvTermReference.PRODUCT_ION_RETENTION_TIME_ERROR.getAccession().equals(accession)
                    || CvTermReference.PRODUCT_ION_RETENTION_TIME_ERROR_PLGS.getAccession().equals(accession)) {
                retentionTimeError = NumberUtilities.isNumber(value) ? Double.parseDouble(value) : retentionTimeError;
            } else if (CvTermReference.PRODUCT_ION_CHARGE.getAccession().equals(accession)) {
                charge = NumberUtilities.isInteger(value) ? Integer.parseInt(value) : charge;
            } else if (ionType == null) {
                if (cvParam.getName().contains("ion")) {
                    ionType = cvParam.getName();
                    location = NumberUtilities.isInteger(value) ? Integer.parseInt(value) : location;
                    ionTypeAccession = accession;
                }
            }

        }
    }

    public double getMz() {
        return mz;
    }

    public void setMz(double mz) {
        this.mz = mz;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getMassError() {
        return massError;
    }

    public void setMassError(double massError) {
        this.massError = massError;
    }

    public double getRetentionTimeError() {
        return retentionTimeError;
    }

    public void setRetentionTimeError(double retentionTimeError) {
        this.retentionTimeError = retentionTimeError;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public String getIonType() {
        return ionType;
    }

    public void setIonType(String ionType) {
        this.ionType = ionType;
    }

    public String getIonTypeAccession() {
        return ionTypeAccession;
    }

    public void setIonTypeAccession(String ionTypeAccession) {
        this.ionTypeAccession = ionTypeAccession;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}
