package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.CvTermReference;

import java.util.List;

/**
 * FragmentIon stores details about peptide fragment ion information.
 * Note: FragmentIon represents a ParamGroup object, but it also provides
 * a list of convenient methods to access the values of m/z, intensity and etc.
 *
 * User: rwang
 * Date: 04-Jun-2010
 * Time: 11:33:37
 */
public class FragmentIon extends ParamGroup {
    /** m/z value */
    private double mz = -1;
    /** intensity of the fragment ion */
    private double intensity = -1;
    /** mass error margin of the fragment ion */
    private double massError = -1;
    /** retention time error margin of the fragment ion */
    private double retentionTimeError = -1;
    /** charge of the fragment ion*/
    private int charge = 0;
    /** ion type */
    private String ionType = null;
    /** location */
    private int location = -1;
    /** ion type accession */
    private String ionTypeAccession = null;


    /**
     * Constructor
     *
     * @param params    required.
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

            if (value != null) {
                if (CvTermReference.PRODUCT_ION_MZ.getAccession().equals(accession)) {
                     mz = Double.parseDouble(value);
                } else if (CvTermReference.PRODUCT_ION_INTENSITY.getAccession().equals(accession)){
                    intensity = Double.parseDouble(value);
                } else if (CvTermReference.PRODUCT_ION_MASS_ERROR.getAccession().equals(accession)) {
                    massError = Double.parseDouble(value);
                } else if (CvTermReference.PRODUCT_ION_RETENTION_TIME_ERROR.getAccession().equals(accession)) {
                    retentionTimeError = Double.parseDouble(value);
                } else if (CvTermReference.PRODUCT_ION_CHARGE.getAccession().equals(accession)) {
                    charge = Integer.parseInt(value);
                } else {
                    ionType = cvParam.getName();
                    location = Integer.parseInt(cvParam.getValue());
                    ionTypeAccession = cvParam.getAccession();
                }
            }

        }
    }

    public double getMz() {
        return mz;
    }

    public void setMz(double mz) {
        if (mz < 0) {
            throw new IllegalArgumentException("Fragment ion's m/z value can not be less than zero: " + mz);
        } else {
            this.mz = mz;
        }
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        if (intensity < 0) {
            throw new IllegalArgumentException("Fragment ion's intensity can not be less than zero: " + intensity);
        } else {
            this.intensity = intensity;
        }
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
        if (location < 0) {
            throw new IllegalArgumentException("Fragment ion's location can not be less than zero: " + location);
        } else {
            this.location = location;
        }
    }
}
