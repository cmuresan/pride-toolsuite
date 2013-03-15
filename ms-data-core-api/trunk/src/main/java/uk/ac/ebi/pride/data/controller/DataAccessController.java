package uk.ac.ebi.pride.data.controller;


import uk.ac.ebi.pride.data.controller.access.*;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.prop.PropertyChangeHandler;

import java.beans.PropertyChangeListener;
import java.util.Collection;


/**
 * DataAccessController is an aggregate interface for data access.
 * It extends a list of interfaces, also added methods for accessing metadata.
 * Setting the state of the data access controller.
 * <p/>
 * User: yperez
 * Date: 09-Apr-2010
 * Time: 14:25:49
 */
public interface DataAccessController
        extends MzGraphDataAccess, ProteinDataAccess, PeptideDataAccess, QuantDataAccess, ChartAccess {

    /**
     * fired when data controller is closed
     */
    public static final String DATA_SOURCE_CLOSED = "dataSourceClosed";

    /**
     * ContentCategory defines the type of content a data access controller should have.
     * Note: this does not automatically mean they must have them.
     */
    public enum ContentCategory {
        SPECTRUM,
        CHROMATOGRAM,
        PROTEIN,
        PROTEIN_GROUPS,
        PEPTIDE,
        QUANTITATION,
        SAMPLE,
        PROTOCOL,
        SOFTWARE,
        INSTRUMENT,
        DATA_PROCESSING
    }

    /**
     * Type indicates the I/O of the data source.
     * There are two types of data access controller at the moment:
     * <p/>
     * DATABASE means connection to a database
     * XML_FILE means reading the data from a file.
     */
    public enum Type { DATABASE, XML_FILE, PEAK_FILE,MZIDENTML}

    /** ========================================= Description section ========================================= */

    /**
     * Get the unique id represent the uniqueness of the data source
     *
     * @return String    uid
     */
    public String getUid();

    /**
     * Set a unique id
     *
     * @param uid unique id
     */
    public void setUid(String uid);

    /**
     * Get the display name for this controller, for GUI
     *
     * @return String the name of this DataAccessController
     */
    public String getName();

    /**
     * Set the name of this DataAccessionController
     *
     * @param name the new name for this DataAccessController
     */
    public void setName(String name);

    /**
     * Get the type of database access controller.
     *
     * @return DataAccessController.Type controller type.
     */
    public Type getType();

    /**
     * Set the type of database access controller.
     *
     * @param type controller type.
     */
    public void setType(Type type);

    /**
     * Return a collection of content categories
     *
     * @return Collection<ContentCategory>  a collection of content categories.
     */
    public Collection<ContentCategory> getContentCategories();

    /**
     * Add a array of content categories.
     *
     * @param categories a array of categories.
     */
    public void setContentCategories(ContentCategory... categories);

    /**
     * Get the description for this controller, for GUI
     *
     * @return String   the description of this controller
     */
    public String getDescription();

    /**
     * Set the description for this controller, for GUI
     *
     * @param desc the new description for the controller
     */
    public void setDescription(String desc);

    /**
     * Get the original data source object
     *
     * @return Object   data source object
     */
    public Object getSource();

    /**
     * Set the orginal data source object
     *
     * @param src data source object
     */
    public void setSource(Object src);

    /**
     * Get the data access mode
     *
     * @return DataAccessMode   data access mode
     */
    public DataAccessMode getMode();

    /**
     * Set the data access mode
     *
     * @param mode data access mode
     */
    public void setMode(DataAccessMode mode);

    /**
     * shutdown this controller, release all the resources.
     */
    public void close();

    /** ========================================= Metadata accession section ========================================= */

    /**
     * Get an collection of experiment ids from data source
     *
     * @return Collection<Comparable>   a string collection of experiment ids
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<Comparable> getExperimentAccs() throws DataAccessException;

    /**
     * Get a meta object
     *
     * @return MetaData meta data object
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public ExperimentMetaData getExperimentMetaData() throws DataAccessException;

    /**
     * Get the MetaData Information for Identification Object.
     *
     * @return IdentificationMetaData
     * @throws DataAccessException DataAccessException
     */
    public IdentificationMetaData getIdentificationMetaData() throws DataAccessException;

    /**
     * Get MetaData Information for Spectrum Experiment Object
     *
     * @return MzGraphMetaData
     * @throws DataAccessException DataAccessException
     */
    public MzGraphMetaData getMzGraphMetaData() throws DataAccessException;

    /**
     * Get an collection of cv lookups
     *
     * @return Collection<CVLookup> a collection of cv lookups
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<CVLookup> getCvLookups() throws DataAccessException;

    /**
     * Get File Content Object
     *
     * @return ParamGroup
     * @throws DataAccessException DataAccessException
     */
    public ParamGroup getFileContent() throws DataAccessException;

    /**
     * Get the referenceable param group
     *
     * @return ReferenceableParamGroup  the referenceable param group
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public ReferenceableParamGroup getReferenceableParamGroup() throws DataAccessException;

    /**
     * Get an collection of samples.
     *
     * @return Collection<Sample>   an collection of samples.
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<Sample> getSamples() throws DataAccessException;

    /**
     * Get the Collection of the SpectrumIdentification Protocol
     *
     * @return Collection<SpectrumIdentificationProtocol>  a Collection of SpectrumIdentification Protocol
     * @throws DataAccessException DataAccessException
     */
    public Collection<SpectrumIdentificationProtocol> getSpectrumIdentificationProtocol() throws DataAccessException;

    /**
     * Get ProteinDetection Protocol
     *
     * @return Protocol is a Protein Detection Protocol.
     * @throws DataAccessException DataAccessException
     */
    public Protocol getProteinDetectionProtocol() throws DataAccessException;

    /**
     * Get a Provider for a File
     *
     * @return Provider The last software that provide the mzidentml file
     * @throws DataAccessException DataAccessException
     */
    public Provider getProvider() throws DataAccessException;

    /**
     * Get an collection of softwares.
     *
     * @return Collection<Software> an collection of softwares.
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<Software> getSoftwares() throws DataAccessException;

    /**
     * Get the List of Persons Related with the Experiment
     *
     * @return Collection<Person> Collection of person contacts
     * @throws DataAccessException DataAccessException
     */
    public Collection<Person> getPersonContacts() throws DataAccessException;

    /**
     * Get the Collection of Organizations
     *
     * @return Collection<Organization> Collection of Organization Contacts
     * @throws DataAccessException DataAccessException
     */
    public Collection<Organization> getOrganizationContacts() throws DataAccessException;

    /**
     * Get an collection of scan settings.
     *
     * @return Collection<ScanSetting>  an collection of scan settings.
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<ScanSetting> getScanSettings() throws DataAccessException;

    /**
     * Get an collection of instruments.
     *
     * @return Collection<Instrument>   an collection of instruments.
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException;

    /**
     * Get an collection of data processings.
     *
     * @return Collection<DataProcessing>   an collection of data processings.
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<DataProcessing> getDataProcessings() throws DataAccessException;

    /**
     * Get all the Search Databases used in the Experiment
     *
     * @return Collection<SearchDataBase> Collection of SearchDatabases uses in the Identification
     * @throws DataAccessException DataAccessException
     */
    public Collection<SearchDataBase> getSearchDataBases() throws DataAccessException;

    /**
     * Get all modification presented in the Experiment
     *
     * @return Collection<Modification> Collection of the Modification Present in the Experiment
     * @throws DataAccessException DataAccessException
     */
    public Collection<Modification> getModification() throws DataAccessException;

    /**
     * Get all Source Files
     *
     * @return Collection<SourceFile> Collection of all of the Source Files in the Experiment
     * @throws DataAccessException DataAccessException
     */
    public Collection<SourceFile> getSourceFiles() throws DataAccessException;

    /**
     * Get experimental additional params
     *
     * @return ParamGroup  experimental additional param
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public ParamGroup getAdditional() throws DataAccessException;

    /**
     * Get all SpectraDataFiles From mzIdentMl File
     *
     * @return Collection<SpectraData> Collection of all SpectraData (Reference to the source file where spectra data is store)
     * @throws DataAccessException DataAccessException
     */
    public Collection<SpectraData> getSpectraDataFiles() throws DataAccessException;

    /**
     * This function give the user the possibility to know if the controller contains
     * MetaData. The metaData could be ExperimentMetadata, MzGraphMetaData, IdentificationMetaData
     *
     * @return boolean
     */
    public boolean hasMetaDataInformation();

    // Todo: getDefaultInstrument() from mzML's run element
    // Todo: getDefaultSample() from mzML's run element
    // Todo: getDefaultSpectrumDataProcessing() from mzML's spectrumList element
    // Todo: getDefaultChromatogramDataProcessing() from mzML's chromatogramList element
}

