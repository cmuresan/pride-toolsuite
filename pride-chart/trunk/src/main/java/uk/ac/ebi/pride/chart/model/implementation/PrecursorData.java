package uk.ac.ebi.pride.chart.model.implementation;

/**
 * <p>Container for the precursor mass and precursor charge of every SpectrumData of a PRIDE experiment.</p>
 *
 * @author Antonio Fabregat
 * Date: 08-jul-2010
 * Time: 9:52:02
 */
public class PrecursorData {

    /**
     * Contains the associated accession number
     */
    private String accession = null;

    /**
     * Contains the SpectrumData ID
     */
    private String spectrumID = null;

    /**
     * Contains the value of the precursor chartData (precursor charge | precursor mass)
     */
    private double value;    //value in mzdata_ion_selection_param is 'Text'

    /**
     * <p> Creates an instance of this PrecursorData object, setting all fields as per description below.</p>
     *
     * @param accession the associated accession number
     * @param spectrumID the SpectrumData ID
     * @param value the value of the precursor chartData (precursor charge | precursor mass)
     * @throws PrecursorDataException if the precursor chart data does not contain a valid value
     */
    public PrecursorData(String accession, String spectrumID, String value) throws PrecursorDataException {
        this.accession = accession;
        this.spectrumID = spectrumID;
        try{
            this.value = Double.valueOf(value);
        } catch (NumberFormatException nfe){
            String msg = "Precursor chartData value '" + value + "' is not valid";
            throw new PrecursorDataException(msg);
        }
    }

    /**
     * Returns the SpectrumData ID
     *
     * @return the SpectrumData ID
     */
    public String getSpectrumID(){
        return spectrumID;
    }

    /**
     * Returns the value of the precursor chartData (precursor charge | precursor mass)
     *
     * @return the value of the precursor chartData (precursor charge | precursor mass)
     */
    public double getValue(){
        return value;
    }

    /**
     * Returns a useful String representation of this Implementation instance that includes details of all fields.
     *
     * @return a useful String representation of this Implementation instance.
     */
    @Override
    public String toString() {
        return "PrecursorData{" +
                "accession='" + accession + '\'' +
                ", spectrumID='" + spectrumID + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
