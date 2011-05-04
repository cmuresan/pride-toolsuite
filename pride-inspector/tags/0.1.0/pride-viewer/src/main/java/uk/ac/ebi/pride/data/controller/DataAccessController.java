package uk.ac.ebi.pride.data.controller;

import uk.ac.ebi.pride.data.core.*;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 09-Apr-2010
 * Time: 14:25:49
 */
public interface DataAccessController {

    public static final String EXPERIMENT_TYPE = "Experiment";
    public static final String MZGRAPH_TYPE = "MzGraph";
    public static final String SPECTRUM_TYPE = "Spectrum";
    public static final String CHROMATOGRAM_TYPE = "Chromatogram";
    public static final String IDENTIFICATION_TYPE ="Identification";
    public static final String TWO_DIM_IDENTIFICATION_TYPE = "2D";
    public static final String GEL_FREE_IDENTIFICATION_TYPE = "Gel Free";

    public static final String FOREGROUND_EXPERIMENT_CHANGED = "foregroundExperiment";
    public static final String FOREGROUND_SPECTRUM_CHANGED = "foregroundSpectrum";
    public static final String FOREGROUND_CHROMATOGRAM_CHANGED = "foregroundChromatogram";
    public static final String FOREGROUND_TWODIM_IDENTIFICATION_CHANGED = "foregroundTwoDimIdent";
    public static final String FOREGROUND_GELFREE_IDENTIFICATION_CHANGED = "foregroundGelFreeIdent";

    /** ========================================= Data accession section =========================================*/
    /**
     * Get an collection of experiment ids from data source
     * @return Collection<String>   a string collection of experiment ids
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<String> getExperimentIds() throws DataAccessException;

    /**
     * Get an Experiment object via an experiment id
     * @param experimentId
     * @return Experiment   an Experiment object which consists only the meta data
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Experiment getExperimentById(String experimentId) throws DataAccessException;

    /**
     * Get an MzML object
     * @return MzML an MzML object which contains only meta data
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public MzML getMzMLMetaData() throws DataAccessException;

    /**
     * Get an collection of spectrum ids
     * @return Collection<String>   a string collection of spectrum ids
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<String> getSpectrumIds() throws DataAccessException;

    /**
     * Get an Spectrum object via an spectrum id
     * @param id    Spectrum id
     * @return Spectrum an Spectrum object
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Spectrum getSpectrumById(String id) throws DataAccessException;

    /**
     * Get an collection of chromatogram ids.
     * @return Collection<String>   an string collection of chromatogram ids
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<String> getChromatogramIds() throws DataAccessException;

    /**
     * Get an Chromatogram object
     * @param id    chromatogram string id
     * @return Chromatogram an chromatogram object
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Chromatogram getChromatogramById(String id) throws DataAccessException;

    /**
     * Get an collection of two dimensional identification ids
     * @return Collection<String>   an string collection of two dimentional identification ids
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<String> getTwoDimIdentIds() throws DataAccessException;

    /**
     * Get an TwoDimIdentification object
     * @param id    an string id of TwoDimIdentification
     * @return TwoDimIdentification an TwoDimIdentification object
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public TwoDimIdentification getTwoDimIdentById(String id) throws DataAccessException;

    /**
     * Get an collection of gel free identification ids.
     * @return Collection<String>   an string collection of gel free identification ids
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<String> getGelFreeIdentIds() throws DataAccessException;

    /**
     * Get an GelFreeIdentification object
     * @param id    an string id of GelFreeIdentification
     * @return GelFreeIdentification    an GelFreeIdentification object
     * @throws DataAccessException      throw a exception when there is an error accessing the data source
     */
    public GelFreeIdentification getGelFreeIdentById(String id) throws DataAccessException;

    /** ========================================= Description section =========================================*/

    /**
     * Get the display name for this controller, for GUI
     * @return String the name of this DataAccessController
     */
    public String getName();

    /**
     * Set the name of this DataAccessionController
     * @param name  the new name for this DataAccessController
     */
    public void setName(String name);

    /**
     * Get the description for this controller, for GUI
     * @return String   the description of this controller
     */
    public String getDescription();

    /**
     * Set the description for this controller, for GUI
     * @param desc  the new description for the controller
     */
    public void setDescription(String desc);

    /**
     * Get the original source of the data, for instance: an File Object for files or an Connection Object for databases
     * @return Object   source object
     */
    public Object getSource();

    /**
     * Set the original soruce of the data.
     * @param obj   new source object
     */
    public void setSource(Object obj);

    /**
     * Whether this controller contains experiment meta data
     * @return boolean  return true if experiment meta data exists
     */
    public boolean isExperimentFriendly();

    /**
     * Set this controller has experiment meta data.
     * @param exp   true if experiment meta data exists
     */
    public void setExperimentFriendly(boolean exp);

    /**
     * Whether this controller contains identifications
     * @return boolean  return true if identifications exist
     */
    public boolean isIdentificationFriendly();

    /**
     * Set this controller has identifications
     * @param ident true if identifications exist
     */
    public void setIdentificationFriendly(boolean ident);

    /**
     * Whether this controller contains spectra
     * @return boolean return true if spectra exist
     */
    public boolean isSpectrumFriendly();

    /**
     * Set this controller has spectra
     * @param spec  true if spectra exist
     */
    public void setSpectrumFriendly(boolean spec);

    /**
     * Get the current foreground experiment
     * @return Experiment foreground experiment object.
     */
    public Experiment getForegroundExperiment();

    /**
     * Set a new foreground experiment using experiment id
     * @param expId experiment id
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public void setForegroundExperimentById(String expId) throws DataAccessException;

    /**
     * Get the current foreground chromatogram
     * @return Chromatogram foreground chromatogram object
     */
    public Chromatogram getForegroundChromatogram();

    /**
     * Set a new foreground chromatogram using id.
     * @param chromaId  chromatogram id
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public void setForegroundChromatogramById(String chromaId) throws DataAccessException;

    /**
     * Get the foreground two dimensional identification
     * @return TwoDimIdentification two dimensional identification object
     */
    public TwoDimIdentification getForegroundTwoDimIdent();

    /**
     * Set a new foreground two dimensional identification using accession.
     * @param identId   identification id
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public void setForegroundTwoDimIdentById(String identId) throws DataAccessException;

    /**
     * Get a foreground gel free identification using accession.
     * @return GelFreeIdentificaiton    Gel free identification object
     */
    public GelFreeIdentification getForegroundGelFreeIdent();

    /**
     * Set a new foregound gel free identification using accessioin.
     * @param identId   gel free identification accession.
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public void setForegroundGelFreeIdentById(String identId) throws DataAccessException;

    /**
     * Get the current foreground spectrum.
     * @return Spectrum foreground spectrum
     */
    public Spectrum getForegroundSpectrum();

    /**
     * Set a foreground spectrum using id
     * @param specId    spectrum id
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public void setForegroundSpectrumById(String specId) throws DataAccessException;

    /**
     * shutdown this controller, release all the resources.
     */
    public void close();

}
