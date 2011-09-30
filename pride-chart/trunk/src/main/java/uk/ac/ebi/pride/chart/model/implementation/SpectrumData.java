package uk.ac.ebi.pride.chart.model.implementation;


/**
 * <p>Container for a SpectrumData of a PRIDE experiment.</p>
 * This class has not a constructor because the chartData that make up the spectrum is obtained in
 * different queries and is set step by step
 *
 * @author Antonio Fabregat
 * Date: 13-jul-2010
 * Time: 14:13:12
 */
public class SpectrumData {
    /**
     * Contains the accession number of the SpectrumData
     */
    private String accession = null;

    /**
     * True if the spectrum is identified
     */
    private boolean identified = false;

    /**
     * Contains the precursor mass of the SpectrumData
     */
    private double precursorMass;

    /**
     * True if the precursor mass has been loaded
     */
    private boolean precursorMassLoaded = false;

    /**
     * Contains the precursor charge of the SpectrumData
     */
    private double precursorCharge;

    /**
     * True if the precursor charge has been loaded
     */
    private boolean precursorChargeLoaded = false;

    /**
     * Returns the accession number of the SpectrumData
     *
     * @return the accession number of the SpectrumData
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Returns the precursor mass of the SpectrumData
     *
     * @return the precursor mass
     * @throws SpectrumDataException a spectrum data exception
     */
    public double getPrecursorMass() throws SpectrumDataException {
        if(!precursorMassLoaded) throw new SpectrumDataException("Precursor mass not found");
        return precursorMass;
    }

    /**
     * Returns the precursor charge of the SpectrumData
     *
     * @return the precursor charge
     * @throws SpectrumDataException a spectrum data exception
     */
    public double getPrecursorCharge() throws SpectrumDataException {
        if(!precursorChargeLoaded) throw new SpectrumDataException("Precursor charge not found");
        return precursorCharge;
    }

    /**
     * Returns the mass of the SpectrumData calculated as the precursor mass multiplied by the precursor charge
     *
     * @return the mass of the SpectrumData
     * @throws SpectrumDataException a spectrum data exception
     */
    public double getMass() throws SpectrumDataException {
        if (!precursorMassLoaded || !precursorChargeLoaded)
            throw new SpectrumDataException("There is not enough chartData for calculate the mass");
        return precursorMass * precursorCharge;
    }

    /**
     * Set the accession number of the SpectrumData
     *
     * @param accession the accession number
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    /**
     * Set if the spectrum is identified
     *
     * @param identified true if the spectrum is identified
     */
    public void setIdentified(boolean identified) {
        this.identified = identified;
    }

    /**
     * Set the precursor mass of the SpectrumData
     *
     * @param precursorMass the precursor mass
     */
    public void setPrecursorMass(double precursorMass) {
        this.precursorMass = precursorMass;
        precursorMassLoaded = true;
    }

    /**
     * Set the precursor charge of the SpectrumData
     *
     * @param precursorCharge the precursor charge
     */
    public void setPrecursorCharge(double precursorCharge) {
        this.precursorCharge = precursorCharge;
        precursorChargeLoaded = true;
    }

    /**
     * Returns true if the precursor mass has been loaded
     *
     * @return true if the precursor mass has been loaded
     */
    public boolean isPrecursorMassLoaded() {
        return precursorMassLoaded;
    }

    /**
     * Returns true if the precursor charge has been loaded
     *
     * @return true if the precursor charge has been loaded
     */
    public boolean isPrecursorChargeLoaded() {
        return precursorChargeLoaded;
    }

    /**
     * Returns true if the spectrum is identified
     *
     * @return true if the spectrum is identified
     */
    public boolean isIdentified() {
        return identified;
    }
}
