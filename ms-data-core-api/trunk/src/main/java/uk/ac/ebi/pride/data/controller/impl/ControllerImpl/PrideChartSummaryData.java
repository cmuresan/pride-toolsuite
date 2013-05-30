package uk.ac.ebi.pride.data.controller.impl.ControllerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.graphics.implementation.charts.MZHistogramChartSpectra;
import uk.ac.ebi.pride.chart.model.implementation.*;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.term.CvTermReference;

import java.util.*;

/**
 * <p>Container for the Spectral chartData of a PRIDE experiment.</p>
 *
 * @author Antonio Fabregat
 *         Date: 09-ago-2010
 *         Time: 14:14:13
 */
public class PrideChartSummaryData extends ExperimentSummaryData {
    public static final Logger logger = LoggerFactory.getLogger(PrideChartSummaryData.class);

    /**
     * <p> Creates an instance of this PrideChartSummaryData object, setting all fields as per description below.</p>
     *
     * @param cdac An object that implements the CachedDataAccessController of Pride Viewer
     * @throws SpectralDataPerExperimentException
     *          throws a ExperimentSummaryData Exception
     */
    public PrideChartSummaryData(CachedDataAccessController cdac) throws SpectralDataPerExperimentException {

        spectra = new HashMap<String, SpectrumData>();

        Collection<Comparable> spectrumIDs = null;

        try {
            spectrumIDs = cdac.getSpectrumIds();
        } catch (DataAccessException e) {
            System.err.println(e);
        }

        if (spectrumIDs != null) {
            for (Comparable spectrumID : spectrumIDs) {
                Spectrum spectrum;

                try {
                    spectrum = cdac.getSpectrumById(spectrumID, false);

                    if (spectrum != null) {
                        List<Precursor> precursors = spectrum.getPrecursors();

                        if (!precursors.isEmpty()) {
                            for (Precursor precursor : precursors) {
                                String specID = spectrumID.toString();
                                SpectrumData spectrumData = getOrCreateSpectrum(specID);

                                spectrumData.setIdentified(cdac.isIdentifiedSpectrum(specID));
                                setSpectrumPrecursor(spectrumData, precursor);
                            }

                            processSpectrumMzData(spectrum);
                            processSpectrumIntensityData(spectrum);
                        }
                    }
                } catch (DataAccessException e) {
                    System.err.println(e);
                }
            }
        }

        List<ProteinPeptide> list = getProteinPeptidesList(cdac);

        setProteinsPeptides(list);
        dataControl();
    }

    /**
     * Check if the experiment chartData has consistency
     *
     * @throws SpectralDataPerExperimentException
     *          the experiment chartData is not consistent
     */
    private void dataControl() {
        boolean precursorChargeFound = false;
        boolean precursorMassFound = false;
        Iterator iter = spectra.values().iterator();

        while (iter.hasNext() && (!precursorChargeFound || !precursorMassFound)) {
            SpectrumData spectrumData = (SpectrumData) iter.next();

            if (spectrumData.isPrecursorChargeLoaded()) {
                precursorChargeFound = true;
            }

            if (spectrumData.isPrecursorMassLoaded()) {
                precursorMassFound = true;
            }
        }

        // The precursor mass has been loaded from the source
        state.setPrecursorMassesLoaded(precursorMassFound);

        // The precursor charge has been loaded from the source
        state.setPrecursorChargesLoaded(precursorChargeFound);
    }

    private void setSpectrumPrecursor(SpectrumData spectrum, Precursor precursor) {
        if (precursor != null) {
            try {

                // ToDo: Decide what to do if the ms2 experiment uses more than one peak of ms1?
                double charge = DataAccessUtilities.getSelectedIonCharge(precursor, 0);

                // if( charge > 0 ) spectrum.setPrecursorCharge(charge);
                spectrum.setPrecursorCharge(charge);
            } catch (NullPointerException e) { /* Nothing here */
            }

            try {

                // ToDo: Decide what to do if the ms2 experiment uses more than one peak of ms1?
                double mz = DataAccessUtilities.getSelectedIonMz(precursor, 0);

                spectrum.setPrecursorMass(mz);
            } catch (NullPointerException e) { /* Nothing here */
            }
        }
    }

    /**
     * Process the M/Z data and set the peaks histogram and the intensity histogram
     *
     * @param spectrum the spectrum data
     */
    private void processSpectrumMzData(Spectrum spectrum) {
        BinaryDataArray mzBA = spectrum.getMzBinaryDataArray();
        BinaryDataArray intensityBA = spectrum.getIntensityBinaryDataArray();
        SpectrumData spectrumData = getOrCreateSpectrum(spectrum.getId().toString());
        Map<Integer, PrideHistogram> mzHist;

        if (spectrumData.isIdentified()) {
            mzHist = mzIdentifiedHist;
        } else {
            mzHist = mzUnidentifiedHist;
        }

        int charge = DataAccessUtilities.getPrecursorChargeParamGroup(spectrum);
        PrideHistogram histogram;

        if (mzHist.containsKey(charge)) {
            histogram = mzHist.get(charge);
        } else {
            histogram = new PrideHistogram();
            mzHist.put(charge, histogram);
        }

        for (int pos = 0; pos < mzBA.getDoubleArray().length; pos++) {
            double value = mzBA.getDoubleArray()[pos];
            int bin = (int) Math.round(value / MZHistogramChartSpectra.BIN_SIZE);
            double intensityValue = intensityBA.getDoubleArray()[pos];

            if (histogram.containsKey(bin)) {
                histogram.put(bin, histogram.get(bin) + intensityValue);
            } else {
                histogram.put(bin, intensityValue);
            }
        }
    }

    /**
     * Process the intensity data and set the peaks histogram and the intensity histogram
     *
     * @param spectrum the spectrum data
     */
    private void processSpectrumIntensityData(Spectrum spectrum) {
        BinaryDataArray mzBA = spectrum.getIntensityBinaryDataArray();
        double[] intensityArray = mzBA.getDoubleArray();
        int bin = intensityArray.length;

        if (peaksHist.containsKey(bin)) {
            peaksHist.put(bin, peaksHist.get(bin) + 1);
        } else {
            peaksHist.put(bin, 1);
        }

        SpectrumData spectrumData = getOrCreateSpectrum(spectrum.getId().toString());
        Map<Integer, Integer> intensityHist;

        if (spectrumData.isIdentified()) {
            intensityHist = intensityIdentifiedHist;
        } else {
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

    private List<ProteinPeptide> getProteinPeptidesList(CachedDataAccessController cdac) {
        List<ProteinPeptide> list = new ArrayList<ProteinPeptide>();

        try {
            Collection<Comparable> idenIDList = cdac.getProteinIds();
            int idenIDInt = 0;
            for (Comparable idenID : idenIDList) {
                Protein id = cdac.getProteinById(idenID, true);
                //int identification_id = Integer.valueOf(id.getId().toString());
                int identification_id = idenIDInt;

                for (Peptide pep : id.getPeptides()) {
                    double ptmMass = 0;

                    for (Modification modification : pep.getPeptideSequence().getModifications()) {
                        try {
                            ptmMass += (modification.getMonoisotopicMassDelta() != null && modification.getMonoisotopicMassDelta().size() > 0)
                                    ? modification.getMonoisotopicMassDelta().get(0)
                                    : (modification.getAvgMassDelta() != null && modification.getAvgMassDelta().size() > 0) ? modification.getAvgMassDelta().get(0) : 0.0;
                        } catch (IndexOutOfBoundsException e) { /* Nothing here */
                        }
                    }

                    String seq = pep.getPeptideSequence().getSequence();
                    Spectrum spectrum = pep.getSpectrum();
                    ProteinPeptide pp;

                    if (spectrum != null) {

                        // ToDo: the spectrum is a Comparable not an Integer!!
                        // Comparable spectrumID = Integer.parseInt(spectrum.getId().toString());

                        pp = new ProteinPeptide(spectrum.getId(), identification_id, seq, ptmMass);
                    } else {
                        pp = new ProteinPeptide(identification_id, seq, ptmMass);
                    }

                    List<SearchEngineType> searchEngines = cdac.getSearchEngineTypes();

                    for (SearchEngineType searchEngineType : searchEngines) {
                        for (CvTermReference cvTermReference : searchEngineType.getSearchEngineScores()) {
                            Number pepScore = DataAccessUtilities.getPeptideScore(pep.getSpectrumIdentification(), searchEngines).getScore(searchEngineType, cvTermReference);

                            if (pepScore != null) {
                                pp.addPeptideScore(searchEngineType, cvTermReference, pepScore);
                            }
                        }
                    }

                    list.add(pp);
                }
                idenIDInt++;
            }
        } catch (DataAccessException ex) {
            logger.error("Error while reading protein identification", ex);
        }

        return list;
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



