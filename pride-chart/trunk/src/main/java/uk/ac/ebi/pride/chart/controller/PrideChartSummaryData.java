package uk.ac.ebi.pride.chart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.graphics.implementation.charts.MZHistogramChartSpectra;
import uk.ac.ebi.pride.chart.model.implementation.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Container for the Spectral chartData of a PRIDE experiment.</p>
 *
 * @author Antonio Fabregat
 * Date: 13-jul-2010
 * Time: 12:17:36
 */
public class PrideChartSummaryData extends ExperimentSummaryData {
     private static final Logger logger = LoggerFactory.getLogger(PrideChartSummaryData.class);
    /**
     * Contains an instance of the chartData base access controller
     */
    private DBAccessController dbac;

    private String accessionNumber;

    private int experimentID = -1;

    /**
     * <p> Creates an instance of this PrideChartSummaryData object, setting all fields as per description below.</p>
     *
     * @param accessionNumber the experiment accession number
     * @throws uk.ac.ebi.pride.chart.model.implementation.SpectralDataPerExperimentException
     *          throws a SpectralDataPerExperimentException
     */
    public PrideChartSummaryData(String accessionNumber) throws SpectralDataPerExperimentException {
        this.accessionNumber = accessionNumber;
        dbac = new DBAccessController();
        initialize(accessionNumber);
    }

    /**
     * <p> Creates an instance of this PrideChartSummaryData object, setting all fields as per description below.</p>
     *
     * @param accessionNumber the experiment accession number
     * @param DBConnection    An existing database connection to be used instead of creating a new one (used for the integration with PRIDE inspector)
     * @throws SpectralDataPerExperimentException
     *          throws a SpectralDataPerExperimentException
     */
    public PrideChartSummaryData(String accessionNumber, Connection DBConnection) throws SpectralDataPerExperimentException {
        this.accessionNumber = accessionNumber;
        dbac = new DBAccessController(DBConnection);
        initialize(accessionNumber);
    }

    /**
     * <p> Creates an instance of this PrideChartSummaryData object, setting all fields as per description below.</p>
     *
     * @param accessionNumber the experiment accession number
     * @param dbac a database access controller
     * @throws SpectralDataPerExperimentException throws a SpectralDataPerExperimentException
     */
    public PrideChartSummaryData(String accessionNumber, DBAccessController dbac) throws SpectralDataPerExperimentException {
        this.accessionNumber = accessionNumber;
        this.dbac = dbac;
        initialize(accessionNumber);
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public int getExperimentID(){
        if(experimentID==-1)
            experimentID = dbac.getExperimentID(accessionNumber);
        return experimentID;
    }

    /**
     * Set the verbose option to the class
     *
     * @param verbose true to show state messages on standard output
     */
    public static void setVerbose(boolean verbose) {
        PrideChartSummaryData.verbose = verbose;
    }

    /**
     * Method used to load the spectra chartData structure with the required chartData
     *
     * @param accessionNumber the experiment accession number
     * @throws SpectralDataPerExperimentException
     *          throws a SpectralDataPerExperimentException
     */
    private void initialize(String accessionNumber) throws SpectralDataPerExperimentException {
        spectra = new HashMap<String, SpectrumData>();
        //this.accessionNumber = accessionNumber;

        printMessage("Start");
        long start = System.currentTimeMillis();

        printMessage("Loading identified spectrum ", false);
        Map<String,Boolean> spectrumReferences = dbac.precursorSpectrumIdentified(accessionNumber);
        int cont = 0; int total = 0;
        for (String key : spectrumReferences.keySet()) {
            setSpectrumReferences(key, spectrumReferences.get(key));
            if (spectrumReferences.get(key)) cont++;
            total++;
        }
        printMessage("(found: " + cont + " identified of " + total + ")", false);
        long step1 = System.currentTimeMillis();
        printMessage("............ OK [" + (step1 - start) + " ms]");

        printMessage("Loading precursor mass chartData ", false);
        List<PrecursorData> precursorMass = dbac.precursorMassData(accessionNumber);
        for (PrecursorData pm : precursorMass) {
            setSpectrumPrecursorMass(pm);
        }
        //The precursor charge has been loaded from the database
        if (precursorMass.size() > 0) state.setPrecursorMassesLoaded(true);
        long step2 = System.currentTimeMillis();
        printMessage("............ OK [" + (step2 - step1) + " ms]");

        printMessage("Loading precursor charge chartData ", false);
        List<PrecursorData> precursorCharge = dbac.precursorChargeData(accessionNumber);
        for (PrecursorData pc : precursorCharge) {
            setSpectrumPrecursorCharge(pc);
        }
        //The precursor charge has been loaded from the database
        if (precursorCharge.size() > 0) state.setPrecursorChargesLoaded(true);
        long step3 = System.currentTimeMillis();
        printMessage("............ OK [" + (step3 - step2) + " ms]");

        //ToDo: What to do when precursorMass size is not the same than precursorCharge size

        printMessage("Loading MZ and Intensity values ", false);
        PeakListBasicInfo mzListBasicInfo = dbac.peakListMzInfo(accessionNumber);
        PeakListBasicInfo intensityListBasicInfo = dbac.peakListIntensityInfo(accessionNumber);
        if(mzListBasicInfo!=null && intensityListBasicInfo!=null){
            PeakListManager mzData = new PeakListManager(dbac, mzListBasicInfo);
            PeakListManager intensityData = new PeakListManager(dbac, intensityListBasicInfo);
            Iterator<PeakList> mzIterator = mzData.iterator();
            Iterator<PeakList> intensityIterator = intensityData.iterator();
            while(mzIterator.hasNext() && intensityIterator.hasNext()){
                PeakList mz = mzIterator.next();
                PeakList intensity = intensityIterator.next();
                processSpectrumMzData(mz,intensity);
                processSpectrumIntensityData(intensity);
            }
        }
        long step4 = System.currentTimeMillis();
        printMessage("............ OK [" + (step4 - step4) + " ms]");

        printMessage("Loading proteins identifications by peptides  ", false);
        setProteinsPeptides(dbac.getProteinsIdentification(accessionNumber));

        long step5 = System.currentTimeMillis();
        printMessage("............ OK [" + (step5 - step4) + " ms]");

        printMessage("Spectra Loaded [" + (step5 - start) / 1000 + " seconds]");

        dataControl();
    }

    /**
     * Check if the experiment chartData has consistency
     *
     * @throws SpectralDataPerExperimentException the experiment chartData is not consistent
     */
    private void dataControl() throws SpectralDataPerExperimentException {
        //ToDo: to check if the experiment is public?

        if (spectra.size() == 0 && proteinsPeptides.size()==0)
            throw new SpectralDataPerExperimentException("There is not data in the specified experiment");
    }

    /**
     * Set the spectrum references
     *
     * @param spectrumID the spectrum identifier
     * @param identified True if the spectrum is identified
     */
    private void setSpectrumReferences(String spectrumID, boolean identified){
        SpectrumData spectrum = getOrCreateSpectrum(spectrumID);
        spectrum.setIdentified(identified);
    }

    /**
     * Set the precursor mass of the spectrum ID associated to the given precursor mass chartData
     *
     * @param precursorMass the precursor mass chartData
     */
    private void setSpectrumPrecursorMass(PrecursorData precursorMass) {
        String spectrumID = precursorMass.getSpectrumID();
        SpectrumData spectrum = getOrCreateSpectrum(spectrumID);
        spectrum.setPrecursorMass(precursorMass.getValue());
    }

    /**
     * Set the precursor mass of the spectrum ID associated to the given precursor charge chartData
     *
     * @param precursorCharge the precursor charge chartData
     */
    private void setSpectrumPrecursorCharge(PrecursorData precursorCharge) {
        String spectrumID = precursorCharge.getSpectrumID();
        SpectrumData spectrum = getOrCreateSpectrum(spectrumID);
        spectrum.setPrecursorCharge(precursorCharge.getValue());
    }

    /**
     * Process the M/Z data and set the peaks histogram and the intensity histogram
     *
     * @param mzData the m/z chartData
     */
    private void processSpectrumMzData(PeakList mzData, PeakList intensity) {
        String spectrumID = mzData.getSpectrumID().toString();
        SpectrumData spectrum = getOrCreateSpectrum(spectrumID);
        Map<Integer, PrideHistogram> mzHist;
        if(spectrum.isIdentified()){
            mzHist = mzIdentifiedHist;
        }else{
            mzHist = mzUnidentifiedHist;
        }
        try {
            int charge = (int) Math.ceil(spectrum.getPrecursorCharge());
            PrideHistogram histogram;
            if (mzHist.containsKey(charge)) {
                histogram = mzHist.get(charge);
            } else {
                histogram = new PrideHistogram();
                mzHist.put(charge, histogram);
            }
            for (int pos = 0; pos<mzData.getDoubleArray().length; pos++) {
            //for (double value : mzData.getDoubleArray()) {
                double value = mzData.getDoubleArray()[pos];
                int bin = (int) Math.round(value / MZHistogramChartSpectra.BIN_SIZE);
                double intensityValue = intensity.getDoubleArray()[pos];
                if (histogram.containsKey(bin)) {
                    histogram.put(bin, histogram.get(bin) + intensityValue);
                } else {
                    histogram.put(bin, intensityValue);
                }
            }
        } catch (SpectrumDataException e) {
            //Do NOT take into account this spectrum because it has not precursor charge 
        }
    }

    /**
     * Process the intensity data and set the peaks histogram and the intensity histogram
     *
     * @param intensity the intensity peaks data
     */
    private void processSpectrumIntensityData(PeakList intensity) {
        double[] intensityArray = intensity.getDoubleArray();

        int bin = intensityArray.length;
        if(peaksHist.containsKey(bin))
            peaksHist.put(bin, peaksHist.get(bin)+1);
        else
            peaksHist.put(bin, 1);

        SpectrumData spectrum = getOrCreateSpectrum(intensity.getSpectrumID().toString());
        Map<Integer, Integer> intensityHist;
        if(spectrum.isIdentified()){
            intensityHist = intensityIdentifiedHist;
        }else{
            intensityHist = intensityUnidentifiedHist;
        }

        for (double value : intensityArray) {
            bin = (int) Math.round(value / 5.0);
            if (intensityHist.containsKey(bin)) {
                intensityHist.put(bin, intensityHist.get(bin) + 1);
            } else {
                intensityHist.put(bin, 1);
            }
        }
    }

    /**
     * Set the proteins list based in the peptides found in the experiment
     *
     * @param proteinsPeptides the list of proteins-peptides relation
     */
    private void setProteinsPeptides(List<ProteinPeptide> proteinsPeptides) {
        this.proteinsPeptides = proteinsPeptides;
    }
}
