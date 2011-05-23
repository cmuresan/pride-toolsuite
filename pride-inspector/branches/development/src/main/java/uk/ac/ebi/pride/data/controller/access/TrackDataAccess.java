package uk.ac.ebi.pride.data.controller.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.data.core.Spectrum;

/**
 * TrackDataAccess acts as a point for data access, tracking the foreground experiment,
 * spectrum, chromatogram and identification.
 * <p/>
 * User: rwang
 * Date: 29-Aug-2010
 * Time: 18:14:16
 */
public interface TrackDataAccess {
    /**
     * fired when foreground experiment has changed
     */
    public static final String FOREGROUND_EXPERIMENT_CHANGED = "foregroundExperiment";
    /**
     * fired when foreground spectrum has changed
     */
    public static final String FOREGROUND_SPECTRUM_CHANGED = "foregroundSpectrum";
    /**
     * fired when foreground chromatogram has changed
     */
    public static final String FOREGROUND_CHROMATOGRAM_CHANGED = "foregroundChromatogram";
    /**
     * fired when foreground identification has changed
     */
    public static final String FOREGROUND_IDENTIFICATION_CHANGED = "foregroundIdentification";

    /**
     * Get the current foreground experiment id
     *
     * @return Experiment foreground experiment id.
     */
    public Comparable getForegroundExperimentAcc();

    /**
     * Set a new foreground experiment id
     *
     * @param expId experiment id
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          throw a exception when there is an error accessing the data source
     */
    public void setForegroundExperimentAcc(Comparable expId) throws DataAccessException;

    /**
     * Get the current foreground chromatogram
     *
     * @return Chromatogram foreground chromatogram object
     */
    public Chromatogram getForegroundChromatogram();

    /**
     * Set a new foreground chromatogram using id.
     *
     * @param chromaId chromatogram id
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public void setForegroundChromatogramById(Comparable chromaId) throws DataAccessException;

    /**
     * Get the current foreground spectrum.
     *
     * @return Spectrum foreground spectrum
     */
    public Spectrum getForegroundSpectrum();

    /**
     * Set a foreground spectrum using id
     *
     * @param specId spectrum id
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public void setForegroundSpectrumById(Comparable specId) throws DataAccessException;

    /**
     * Get the foreground mz graph, can be either spectrum or chromatogram
     *
     * @param classType classes that extends MzGraph
     * @return MzGraph  foreground MzGraph object.
     */
    public MzGraph getForegroundMzGraph(Class<? extends MzGraph> classType);

    /**
     * Get the foreground identification
     *
     * @return Identification identification object
     */
    public Identification getForegroundIdentification();

    /**
     * Set a new foreground identification using accession.
     *
     * @param identId identification id
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public void setForegroundIdentificationById(Comparable identId) throws DataAccessException;
}
