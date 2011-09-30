package uk.ac.ebi.pride.chart.model.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Container for the Spectral chartData of a PRIDE experiment.</p>
 *
 * @author Antonio Fabregat
 * Date: 09-ago-2010
 * Time: 14:08:43
  */
public abstract class ExperimentSummaryData {
    private static final Logger logger = LoggerFactory.getLogger(ExperimentSummaryData.class);
    /**
     * Contains all the SpectrumData indexed by the SpectrumData ID
     */
    protected Map<String, SpectrumData> spectra = null;

    /**
     * Contains the found proteins in the experiment based in the peptides
     */
    protected List<ProteinPeptide> proteinsPeptides = null;

    /**
     * Contains the state of spectral chartData
     */
    protected SpectralDataPerExperimentState state = new SpectralDataPerExperimentState();

    /**
     * Map<PrecursorCharge, PrideHistogram> : to store the histogram of mz values of the identified spectra
     */
    protected Map<Integer, PrideHistogram> mzIdentifiedHist = new HashMap<Integer, PrideHistogram>();

    /**
     * Map<PrecursorCharge, PrideHistogram> : to store the histogram of mz values of the unidentified spectra
     */
    protected Map<Integer, PrideHistogram> mzUnidentifiedHist = new HashMap<Integer, PrideHistogram>();

    /**
     * Map<Bin, Number> : to store the histogram of intensity values of the identified spectra
     */
    protected Map<Integer, Integer> intensityIdentifiedHist = new HashMap<Integer, Integer>();

    /**
     * Map<Bin, Number> : to store the histogram of intensity values of the unidentified spectra
     */
    protected Map<Integer, Integer> intensityUnidentifiedHist = new HashMap<Integer, Integer>();

    /**
     * Map<Bin, Number> : to store the histogram of the number of peaks per spectrum
     */
    protected Map<Integer, Integer> peaksHist = new HashMap<Integer, Integer>();

    /**
     * True means that state messages have to be printed
     */
    protected static boolean verbose = false;

    /**
     * If exists, returns the spectrum associated to the spectrum ID. If there is not a
     * spectrum associated to the spectrum ID, then creates a new one and returns it.
     *
     * @param spectrumID the spectrum ID
     * @return the spectrum associated to the spectrum ID
     */
    protected SpectrumData getOrCreateSpectrum(String spectrumID) {
        SpectrumData spectrum;
        if (spectra.containsKey(spectrumID)) {
            spectrum = spectra.get(spectrumID);
        } else {
            spectrum = new SpectrumData();
            spectra.put(spectrumID, spectrum);
        }
        return spectrum;
    }

    /**
     * Returns the spectrum data for the given spectrum ID
     *
     * @param spectrumID the spectrum ID
     * @return the spectrum data for the given spectrum ID
     */
    public SpectrumData getSpectrum(String spectrumID){
        return spectra.get(spectrumID);
    }

    /**
     * Returns the precomputed M/Z histogram
     *
     * @param isIdentified indicates the type of the mz data
     * @return the precomputed M/Z histogram
     */
    public Map<Integer, PrideHistogram> getMzHist(boolean isIdentified) {
        if(isIdentified)
            return mzIdentifiedHist;
        else
            return mzUnidentifiedHist;
    }

    /**
     * Returns the precomputed intensity histogram
     *
     * @param isIdentified indicates the type of the intensity data
     * @return the precomputed intensity histogram
     */
    public Map<Integer, Integer> getIntensityHist(boolean isIdentified) {
        if(isIdentified)
            return intensityIdentifiedHist;
        else
            return intensityUnidentifiedHist;
    }

    /**
     * Returns the precomputed intensity peaks histogram
     *
     * @return the precomputed intensity peaks histogram
     */
    public Map<Integer, Integer> getPeaksHist() {
        return peaksHist;
    }

    /**
     * Returns all the calculated masses of each spectrum in the spectral chartData of a PRIDE experiment
     *
     * @param isIdentified indicates the type of the precursor masses
     * @return all the calculated masses of each spectrum
     */
    public List<Double> getMassVecExp(boolean isIdentified){
        List<Double> massVecExp = new ArrayList<Double>();
        for (String s : spectra.keySet()) {
            SpectrumData spectrum = spectra.get(s);
            if(spectrum.isIdentified()==isIdentified){
                try {
                    massVecExp.add(spectrum.getMass());
                } catch (SpectrumDataException e) {/*Nothing here*/}
            }
        }
        return massVecExp;
    }

    /**
     * Returns all the precursor charges of each spectrum in the spectral chartData of a PRIDE experiment
     *
     * @param isIdentified indicates the type of the precursor charges
     * @return all the precursor charges of each spectrum
     */
    public List<Double> getPrecChargeVecExp(boolean isIdentified) {
        List<Double> precChargeVecExp = new ArrayList<Double>();
        for (String s : spectra.keySet()) {
            SpectrumData spectrum = spectra.get(s);
            if(spectrum.isIdentified()==isIdentified)
                try {
                    precChargeVecExp.add(spectrum.getPrecursorCharge());
                } catch (SpectrumDataException e) {
                    //Todo: what to do if there is not precursor charge here?
                }
        }
        return precChargeVecExp;
    }

    /**
     * Returns the list of proteins with the peptides used to identify each one
     *
     * @return list of proteins with the peptides used to identify each one
     */
    public List<ProteinPeptide> getProteinsPeptides() {
        return proteinsPeptides;
    }

    /**
     * Returns the number of spectrum contained in the spectral chartData of the PRIDE experiment
     *
     * @return the number of spectrum
     */
    public int size() {
        return spectra.size();
    }

    /**
     * Returns the state of the chartData loaded for the current experiment
     *
     * @return the state of the chartData loaded for the current experiment
     */
    public SpectralDataPerExperimentState getState() {
        return state;
    }

    /**
     * Print the message on the standard output
     *
     * @param msg the message to be printed on the standard output
     * @param newLine if true then println is used
     */
    protected void printMessage(String msg, boolean newLine){
        if(verbose)
            logger.info(msg);
//            if( newLine ) logger.info(msg);
//            else logger.infoSystem.out.print(msg);
    }

    /**
     * Print the message on the standard output
     *
     * @param msg the message to be printed on the standard output
     */
    protected void printMessage(String msg){
        printMessage(msg, true);
    }
}
