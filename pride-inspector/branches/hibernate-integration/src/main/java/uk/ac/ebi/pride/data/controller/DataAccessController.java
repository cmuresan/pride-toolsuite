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
     * Get an collection of cv lookups
     * @return Collection<CVLookup> a collection of cv lookups
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<CVLookup> getCvLookups() throws DataAccessException;

    /**
     * Get file description object.
     * @return FileDescription  FileDescription object
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public FileDescription getFileDescription() throws DataAccessException;

    /**
     * Get the referenceable param group
     * @return ReferenceableParamGroup  the referenceable param group
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public ReferenceableParamGroup getReferenceableParamGroup() throws DataAccessException;

    /**
     * Get an collection of samples.
     * @return Collection<Sample>   an colleciton of samples.
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<Sample> getSamples() throws DataAccessException;

    /**
     * Get an collection of softwares.
     * @return Collection<Software> an collection of softwares.
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<Software> getSoftware() throws DataAccessException;

    /**
     * Get an collection of scan settings.
     * @return Collection<ScanSetting>  an collection of scan settings.
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<ScanSetting> getScanSettings() throws DataAccessException;

    /**
     * Get an collection of instruments.
     * @return Collection<Instrument>   an collection of instruments.
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException;

    /**
     * Get an collection of data processings.
     * @return Collection<DataProcessing>   an collection of data processings.
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<DataProcessing> getDataProcessings() throws DataAccessException;
    
    /**
     * Get an collection of experiment ids from data source
     * @return Collection<Comparable>   a string collection of experiment ids
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<Comparable> getExperimentIds() throws DataAccessException;

    /**
     * Get an Experiment object via an experiment id
     * @param expId
     * @return Experiment   an Experiment object which consists only the meta data
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Experiment getExperimentById(Comparable expId) throws DataAccessException;

    /**
     * Get a meta object
     * @return MetaData meta data object
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public MetaData getMetaData() throws DataAccessException;

    /**
     * Get a collection of spectrum ids
     * @return Collection   a string collection of spectrum ids
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<Comparable> getSpectrumIds() throws DataAccessException;

    /**
     * Get a Spectrum object via an spectrum id
     * @param id    Spectrum id
     * @return Spectrum an Spectrum object
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Spectrum getSpectrumById(Comparable id) throws DataAccessException;

    /**
     * Get a collection of chromatogram ids.
     * @return Collection<Comparable>   a string collection of chromatogram ids
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<Comparable> getChromatogramIds() throws DataAccessException;

    /**
     * Get a Chromatogram object
     * @param id    chromatogram string id
     * @return Chromatogram an chromatogram object
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Chromatogram getChromatogramById(Comparable id) throws DataAccessException;

    /**
     * Get a collection of two dimensional identification ids
     * @return Collection   a string collection of two dimentional identification ids
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<Comparable> getTwoDimIdentIds() throws DataAccessException;

    /**
     * Get a TwoDimIdentification object
     * @param id    a string id of TwoDimIdentification
     * @return TwoDimIdentification an TwoDimIdentification object
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public TwoDimIdentification getTwoDimIdentById(Comparable id) throws DataAccessException;

    /**
     * Get a collection of gel free identification ids.
     * @return Collection   a string collection of gel free identification ids
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public Collection<Comparable> getGelFreeIdentIds() throws DataAccessException;

    /**
     * Get an GelFreeIdentification object
     * @param id    an string id of GelFreeIdentification
     * @return GelFreeIdentification    an GelFreeIdentification object
     * @throws DataAccessException      throw a exception when there is an error accessing the data source
     */
    public GelFreeIdentification getGelFreeIdentById(Comparable id) throws DataAccessException;

    //Todo: getDefaultInstrument() from mzML's run element
    //Todo: getDefaultSample() from mzML's run element
    //Todo: getDefaultSpectrumDataProcessing() from mzML's spectrumList element
    //Todo: getDefaultChromatogramDataProcessing() from mzML's chromatogramList element

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
     * Get the original data source object
     * @return Object   data source object
     */
    public Object getSource();

    /**
     * Set the orginal data source object
     * @param src   data source object
     */
    public void setSource(Object src);

    /**
     * Whether this controller contains identifications
     * @return boolean  return true if identifications exist
     */
    public boolean hasIdentification();

    /**
     * Whether this controller contains spectra
     * @return boolean return true if spectra exist
     */
    public boolean hasSpectrum();

    /**
     * Whether this controller contains chromatogram.
     * @return  boolean return true if chromatogram exist.
     */
    public boolean hasChromatogram();

    /**
     * Get the current foreground experiment id
     * @return Experiment foreground experiment id.
     */
    public Comparable getForegroundExperimentId();

    /**
     * Set a new foreground experiment id
     * @param expId experiment id
     * @throws DataAccessException  throw a exception when there is an error accessing the data source
     */
    public void setForegroundExperimentId(Comparable expId) throws DataAccessException;

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
    public void setForegroundChromatogramById(Comparable chromaId) throws DataAccessException;

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
    public void setForegroundTwoDimIdentById(Comparable identId) throws DataAccessException;

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
    public void setForegroundGelFreeIdentById(Comparable identId) throws DataAccessException;

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
    public void setForegroundSpectrumById(Comparable specId) throws DataAccessException;

    /**
     * shutdown this controller, release all the resources.
     */
    public void close();

}
