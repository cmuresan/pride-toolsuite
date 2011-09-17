package uk.ac.ebi.pride.data.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.jmzml.model.mzml.*;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.cache.impl.MzMLCacheBuilder;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.DataProcessing;
import uk.ac.ebi.pride.data.core.FileDescription;
import uk.ac.ebi.pride.data.core.InstrumentConfiguration;
import uk.ac.ebi.pride.data.core.ParamGroup;
import uk.ac.ebi.pride.data.core.ReferenceableParamGroup;
import uk.ac.ebi.pride.data.core.Sample;
import uk.ac.ebi.pride.data.core.Software;
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
 * MzMLControllerImpl provides methods to access mzML files.
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
        this.setType(Type.XML_FILE);
        // set the content categories
        this.setContentCategories(ContentCategory.SPECTRUM,
                ContentCategory.CHROMATOGRAM,
                ContentCategory.SAMPLE,
                ContentCategory.INSTRUMENT,
                ContentCategory.SOFTWARE,
                ContentCategory.DATA_PROCESSING);
        // create cache builder
        setCacheBuilder(new MzMLCacheBuilder(this));
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
     * Get experimental metadata by checking the cache first
     *
     * @return MetaData    meta data
     * @throws DataAccessException data access exception
     */
    public MetaData getMetaData() throws DataAccessException {
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            // id , accession and version
            String id = unmarshaller.getMzMLId();
            String accession = unmarshaller.getMzMLAccession();
            String version = unmarshaller.getMzMLVersion();
            // FileDescription
            FileDescription fileDesc = getFileDescription();
            // Sample list
            List<Sample> samples = getSamples();
            // Software list
            List<Software> softwares = getSoftware();
            // ScanSettings list
            List<ScanSetting> scanSettings = getScanSettings();
            // Instrument configuration
            List<InstrumentConfiguration> instrumentConfigurations = getInstrumentConfigurations();
            // Data processing list
            List<DataProcessing> dataProcessings = getDataProcessings();
            // Param group
            ParamGroup params = null;

            metaData = new MetaData(id, accession, version, fileDesc,
                    samples, softwares, scanSettings, instrumentConfigurations,
                    dataProcessings, params);

            getCache().store(CacheCategory.EXPERIMENT_METADATA, metaData);
        }

        return metaData;
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
            logger.error("Creating CvLookups", e);
            throw new DataAccessException("Exception while trying to read a list of cv lookups", e);
        }
    }

    /**
     * Get file description
     *
     * @return FileDescription file description
     * @throws DataAccessException data access exception
     */
    @Override
    public FileDescription getFileDescription() throws DataAccessException {
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            try {
                uk.ac.ebi.jmzml.model.mzml.FileDescription
                        rawFileDesc = unmarshaller.getFileDescription();
                return MzMLTransformer.transformFileDescription(rawFileDesc);
            } catch (MzMLUnmarshallerException e) {
                logger.error("Creating File Description", e);
                throw new DataAccessException("Exception while trying to read file description", e);
            }
        } else {
            return metaData.getFileDescription();
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
            logger.error("Creating ReferenceableParamGroup", e);
            throw new DataAccessException("Exception while trying to read referenceable param group", e);
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
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            try {
                SampleList rawSample = unmarshaller.getSampleList();
                return MzMLTransformer.transformSampleList(rawSample);
            } catch (MzMLUnmarshallerException e) {
                logger.error("Creating Samples", e);
                throw new DataAccessException("Exception while trying to read smaples", e);
            }
        } else {
            return metaData.getSamples();
        }
    }

    /**
     * Get a list of software by checking the cache first
     *
     * @return List<Software>  a list of software
     * @throws DataAccessException data access exception
     */
    @Override
    public List<Software> getSoftware() throws DataAccessException {
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            try {
                SoftwareList rawSoftware = unmarshaller.getSoftwareList();
                return MzMLTransformer.transformSoftwareList(rawSoftware);
            } catch (MzMLUnmarshallerException e) {
                logger.error("Creating Software", e);
                throw new DataAccessException("Exception while trying to read softwares", e);
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
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            try {
                ScanSettingsList rawScanSettingList = unmarshaller.getScanSettingsList();
                return MzMLTransformer.transformScanSettingList(rawScanSettingList);
            } catch (MzMLUnmarshallerException e) {
                logger.error("Creating ScanSettings", e);
                throw new DataAccessException("Exception while trying to read scan settings list", e);
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
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            try {
                InstrumentConfigurationList rawInstrumentList = unmarshaller.getInstrumentConfigurationList();
                return MzMLTransformer.transformInstrumentConfigurationList(rawInstrumentList);
            } catch (MzMLUnmarshallerException e) {
                logger.error("Creating Instrument Configurations", e);
                throw new DataAccessException("Exception while trying to read instrument configuration list", e);
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
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            try {
                uk.ac.ebi.jmzml.model.mzml.DataProcessingList
                        rawDataProcList = unmarshaller.getDataProcessingList();
                return MzMLTransformer.transformDataProcessingList(rawDataProcList);
            } catch (MzMLUnmarshallerException e) {
                logger.error("Creating DataProcessings", e);
                throw new DataAccessException("Exception while trying to read data processing list", e);
            }
        } else {
            return metaData.getDataProcessings();
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
    protected Spectrum getSpectrumById(Comparable id, boolean useCache) throws DataAccessException {
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
