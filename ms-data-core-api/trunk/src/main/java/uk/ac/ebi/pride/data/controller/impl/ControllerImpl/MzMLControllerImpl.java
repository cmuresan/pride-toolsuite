package uk.ac.ebi.pride.data.controller.impl.ControllerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.jmzml.model.mzml.*;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.cache.impl.MzMlCacheBuilder;
import uk.ac.ebi.pride.data.controller.impl.Transformer.MzMLTransformer;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.DataProcessing;
import uk.ac.ebi.pride.data.core.InstrumentConfiguration;
import uk.ac.ebi.pride.data.core.ParamGroup;
import uk.ac.ebi.pride.data.core.ReferenceableParamGroup;
import uk.ac.ebi.pride.data.core.Sample;
import uk.ac.ebi.pride.data.core.Software;
import uk.ac.ebi.pride.data.core.SourceFile;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.data.io.file.MzMLUnmarshallerAdaptor;
import uk.ac.ebi.pride.data.utils.MD5Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MzXmlControllerImpl provides methods to access mzML files.
 * <p/>
 * User: rwang
 * Date: 02-Feb-2010
 * Time: 12:31:43
 */
public class MzMLControllerImpl extends CachedDataAccessController {
    private static final Logger logger = LoggerFactory.getLogger(MzMLControllerImpl.class);
    /**
     * Pattern for validating mzML format
     */
    private static Pattern mzMLHeaderPattern = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<(mzML)|(indexedmzML) xmlns=.*", Pattern.MULTILINE);

    /**
     * Reader for getting information from mzML file
     */
    private MzMLUnmarshallerAdaptor unmarshaller = null;

    /**
     * Construct a data access controller using a given mzML file
     *
     * @param file mzML file
     * @throws DataAccessException data access exception
     */
    public MzMLControllerImpl(File file) throws DataAccessException {
        this(file, null);
    }

    /**
     * Construct a data access controller using a given mzML file and data access mode
     *
     * @param file mzML file
     * @param mode data access mode
     * @throws DataAccessException data access exception
     */
    public MzMLControllerImpl(File file, DataAccessMode mode) throws DataAccessException {
        super(file, mode);
        initialize();
    }

    /**
     * Initialize the data access controller
     *
     * @throws DataAccessException data access exception
     */
    private void initialize() throws DataAccessException {
        File file = (File) this.getSource();
        // create unmarshaller
        MzMLUnmarshaller um = new MzMLUnmarshaller(file);
        unmarshaller = new MzMLUnmarshallerAdaptor(um);

        // set data source name
        this.setName(file.getName());
        // set the type
        this.setType(DataAccessController.Type.XML_FILE);
        // set the content categories
        this.setContentCategories(DataAccessController.ContentCategory.SPECTRUM,
                DataAccessController.ContentCategory.CHROMATOGRAM,
                DataAccessController.ContentCategory.SAMPLE,
                DataAccessController.ContentCategory.INSTRUMENT,
                DataAccessController.ContentCategory.SOFTWARE,
                DataAccessController.ContentCategory.DATA_PROCESSING);
        // create cache builder
        setCacheBuilder(new MzMlCacheBuilder(this));
        // populate cache
        populateCache();
    }

    /**
     * Get the backend data reader
     *
     * @return MzMLUnmarshallerAdaptor mzML reader
     */
    public MzMLUnmarshallerAdaptor getUnmarshaller() {
        return unmarshaller;
    }

    /**
     * Get the unique id for this data access controller
     * It generates a MD5 hash using the absolute path of the file
     * This will guarantee the same id if the file path is the same
     *
     * @return String  unique id
     */
    @Override
    public String getUid() {
        String uid = super.getUid();
        if (uid == null) {
            // create a new UUID
            File file = (File) this.getSource();
            try {
                uid = MD5Utils.generateHash(file.getAbsolutePath());
            } catch (NoSuchAlgorithmException e) {
                String msg = "Failed to generate unique id for mzML file";
                logger.error(msg, e);
            }
        }
        return uid;
    }

    /**
     * Get a list of cvlookups, these are not cached
     *
     * @return List<CvLookup>  a list of cvlookups
     * @throws DataAccessException data access exception
     */
    public List<CVLookup> getCvLookups() throws DataAccessException {
        try {
            CVList rawCvList = unmarshaller.getCVList();
            return MzMLTransformer.transformCVList(rawCvList);
        } catch (MzMLUnmarshallerException e) {
            String msg = "Exception while trying to read a list of cv lookups";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }
    }

    /**
     * Get referenceable paramgroup, this concept is only available in mzML
     * It is a paramgroup with id
     * And this is not cached
     *
     * @return ReferenceableParamGroup param group
     * @throws DataAccessException data access exception
     */
    @Override
    public ReferenceableParamGroup getReferenceableParamGroup() throws DataAccessException {

        try {
            ReferenceableParamGroupList rawRefParamGroup = unmarshaller.getReferenceableParamGroupList();
            return MzMLTransformer.transformReferenceableParamGroupList(rawRefParamGroup);
        } catch (MzMLUnmarshallerException e) {
            String msg = "Exception while trying to read referenceable param group";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }
    }

    /**
     * Get a list of samples by checking the cache first
     *
     * @return List<Sample>    a list of samples
     * @throws DataAccessException data access exception
     */
    @Override
    public List<Sample> getSamples() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            try {
                SampleList rawSample = unmarshaller.getSampleList();
                return MzMLTransformer.transformSampleList(rawSample);
            } catch (MzMLUnmarshallerException e) {
                String msg = "Exception while trying to read samples";
                logger.error(msg, e);
                throw new DataAccessException(msg, e);
            }
        } else {
            return metaData.getSampleList();
        }
    }

    /**
     * Get a list of person contacts
     *
     * @return List<Person>    list of persons
     * @throws DataAccessException data access exception
     */
    @Override
    public List<Person> getPersonContacts() throws DataAccessException {
        try {
            FileDescription rawFileDesc = unmarshaller.getFileDescription();
            // List of Persons
            return MzMLTransformer.transformFileDescriptionToPerson(rawFileDesc);
        } catch (MzMLUnmarshallerException e) {
            String msg = "Error while getting a list of person contacts";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }
    }

    @Override
    public List<Organization> getOrganizationContacts() throws DataAccessException {
        try {
            FileDescription rawFileDesc = unmarshaller.getFileDescription();
            // List of Persons
            return MzMLTransformer.transformFileDescriptionOrganization(rawFileDesc);
        } catch (MzMLUnmarshallerException e) {
            String msg = "Error while getting a list of organizational contacts";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }
    }

    @Override
    public List<SourceFile> getSourceFiles() throws DataAccessException {
        try {
            FileDescription rawFileDesc = unmarshaller.getFileDescription();
            // List of Persons
            return MzMLTransformer.transformFileDescriptionToFileSource(rawFileDesc);
        } catch (MzMLUnmarshallerException e) {
            String msg = "Error while getting a list of source files";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }
    }


    @Override
    public ParamGroup getFileContent() throws DataAccessException {
        try {
            FileDescription rawFileDesc = unmarshaller.getFileDescription();
            // List of Persons
            return MzMLTransformer.transformFileDescriptionToFileContent(rawFileDesc);

        } catch (MzMLUnmarshallerException e) {
            String msg = "Error while getting a list of file content";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }
    }

    @Override
    public List<Software> getSoftwares() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            try {
                SoftwareList rawSoftware = unmarshaller.getSoftwares();
                return MzMLTransformer.transformSoftwareList(rawSoftware);
            } catch (MzMLUnmarshallerException e) {
                String msg = "Error while getting a list of software";
                logger.error(msg, e);
                throw new DataAccessException(msg, e);
            }
        } else {
            return metaData.getSoftwares();
        }
    }

    /**
     * Get a list of scan settings by checking the cache first
     *
     * @return List<ScanSetting>   a list of scan settings
     * @throws DataAccessException data access exception
     */
    @Override
    public List<ScanSetting> getScanSettings() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

        if (metaData == null) {
            try {
                ScanSettingsList rawScanSettingList = unmarshaller.getScanSettingsList();
                return MzMLTransformer.transformScanSettingList(rawScanSettingList);
            } catch (MzMLUnmarshallerException e) {
                String msg = "Error while getting a list of scan settings";
                logger.error(msg, e);
                throw new DataAccessException(msg, e);
            }
        } else {
            return metaData.getScanSettings();
        }
    }

    /**
     * Get a list of instrument configurations by checking the cache first
     *
     * @return List<Instrumentconfiguration>   a list of instrument configurations
     * @throws DataAccessException data access exception
     */
    @Override
    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

        if (metaData == null) {
            try {
                InstrumentConfigurationList rawInstrumentList = unmarshaller.getInstrumentConfigurationList();
                return MzMLTransformer.transformInstrumentConfigurationList(rawInstrumentList);
            } catch (MzMLUnmarshallerException e) {
                String msg = "Error while getting a list of instrument configurations";
                logger.error(msg, e);
                throw new DataAccessException(msg, e);
            }
        } else {
            return metaData.getInstrumentConfigurations();
        }
    }

    /**
     * Get a list of data processings by checking the cache first
     *
     * @return List<DataProcessing>    a list of data processings
     * @throws DataAccessException data access exception
     */
    @Override
    public List<DataProcessing> getDataProcessings() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

        if (metaData == null) {
            try {
                uk.ac.ebi.jmzml.model.mzml.DataProcessingList rawDataProcList = unmarshaller.getDataProcessingList();
                return MzMLTransformer.transformDataProcessingList(rawDataProcList);
            } catch (MzMLUnmarshallerException e) {
                String msg = "Error while getting a list of data processings";
                logger.error(msg, e);
                throw new DataAccessException(msg, e);
            }
        } else {
            return metaData.getDataProcessingList();
        }

    }

    /**
     * Get additional details, mzML don't have this kind of information
     *
     * @return ParamGroup  param group
     * @throws DataAccessException data access exception
     */
    @Override
    public ParamGroup getAdditional() throws DataAccessException {
        throw new UnsupportedOperationException("This method is not supported");
    }

    /**
     * Get spectrum using a spectrum id, gives the option to choose whether to use cache.
     * This implementation provides a way of by passing the cache.
     *
     * @param id       spectrum id
     * @param useCache true means to use cache
     * @return Spectrum spectrum object
     * @throws DataAccessException data access exception
     */
    @Override
    Spectrum getSpectrumById(Comparable id, boolean useCache) throws DataAccessException {
        Spectrum spectrum = super.getSpectrumById(id, useCache);
        if (spectrum == null) {
            try {
                uk.ac.ebi.jmzml.model.mzml.Spectrum
                        rawSpec = unmarshaller.getSpectrumById(id.toString());
                spectrum = MzMLTransformer.transformSpectrum(rawSpec);
                if (useCache) {
                    getCache().store(CacheCategory.SPECTRUM, id, spectrum);
                }
            } catch (MzMLUnmarshallerException ex) {
                logger.error("Get spectrum by id", ex);
                throw new DataAccessException("Exception while trying to read Spectrum using Spectrum ID", ex);
            }
        }
        return spectrum;
    }

    /**
     * Get chromatogram using a chromatogram id, gives the option to choose whether to use cache.
     * This implementation provides a way of by passing the cache.
     *
     * @param id       chromatogram id
     * @param useCache true means to use cache
     * @return Chromatogram chromatogram object
     * @throws DataAccessException data access exception
     */
    @Override
    public Chromatogram getChromatogramById(Comparable id, boolean useCache) throws DataAccessException {
        Chromatogram chroma = super.getChromatogramById(id, useCache);
        if (chroma == null) {
            try {
                uk.ac.ebi.jmzml.model.mzml.Chromatogram
                        rawChroma = unmarshaller.getChromatogramById(id.toString());
                chroma = MzMLTransformer.transformChromatogram(rawChroma);
                if (useCache) {
                    getCache().store(CacheCategory.CHROMATOGRAM, id, chroma);
                }
            } catch (MzMLUnmarshallerException ex) {
                logger.error("Get chromatogram by id", ex);
                throw new DataAccessException("Exception while trying to read Chromatogram using chromatogram ID", ex);
            }
        }
        return chroma;
    }

    /**
     * Close data access controller by resetting the data reader first
     */
    @Override
    public void close() {
        unmarshaller = null;
        super.close();
    }

    @Override
    public ExperimentMetaData getExperimentMetaData() throws DataAccessException {

        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            // id , accession and version
            String id = unmarshaller.getMzMLId();
            String accession = unmarshaller.getMzMLAccession();
            String version = unmarshaller.getMzMLVersion();
            // SourceFile List
            List<SourceFile> sourceFileList = getSourceFiles();
            // List of Persons
            List<Person> personList = getPersonContacts();
            // List of Organizations
            List<Organization> organizationList = getOrganizationContacts();
            // Sample list
            List<Sample> samples = getSamples();
            // Software list
            List<Software> softwares = getSoftwares();
            // ScanSettings list
            ParamGroup fileContent = getFileContent();
            metaData = new ExperimentMetaData(fileContent, id, accession, version, null, samples, softwares, personList, sourceFileList, null, organizationList, null, null, null, null);
        }
        return metaData;
    }

    @Override
    public MzGraphMetaData getMzGraphMetaData() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();
        if (metaData == null) {
            List<ScanSetting> scanSettings = getScanSettings();
            List<DataProcessing> dataProcessings = getDataProcessings();
            List<InstrumentConfiguration> instrumentConfigurations = getInstrumentConfigurations();
            metaData = new MzGraphMetaData(null, null, scanSettings, instrumentConfigurations, dataProcessings);
        }
        return metaData;
    }

    @Override
    public IdentificationMetaData getIdentificationMetaData() throws DataAccessException {
        return null;
    }

    /**
     * Check a file is mzML file
     *
     * @param file input file
     * @return boolean true means mzML
     */
    public static boolean isValidFormat(File file) {
        boolean valid = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            // read the first ten lines
            StringBuilder content = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                content.append(reader.readLine());
            }

            Matcher matcher = mzMLHeaderPattern.matcher(content);

            valid = matcher.find();
        } catch (Exception e) {
            logger.error("Failed to read file", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing here
                }
            }
        }

        return valid;
    }
}
