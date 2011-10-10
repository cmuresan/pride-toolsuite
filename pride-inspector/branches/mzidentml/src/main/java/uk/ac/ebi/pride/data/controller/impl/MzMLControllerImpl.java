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
 * MzMLControllerImpl provides methods to access mzML files.
 * <p/>
 * User: rwang
 * Date: 02-Feb-2010
 * Time: 12:31:43
 */
public class MzMLControllerImpl extends CachedDataAccessController {

    private static final Logger logger = LoggerFactory.getLogger(MzMLControllerImpl.class);

    private static Pattern mzMLHeaderPattern = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<(mzML)|(indexedmzML) xmlns=.*", Pattern.MULTILINE);

    private MzMLUnmarshallerAdaptor unmarshaller = null;

    public MzMLControllerImpl(File file) throws DataAccessException {
        this(file, null);
    }

    public MzMLControllerImpl(File file, DataAccessMode mode) throws DataAccessException {
        super(file, mode);
        initialize();
    }

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

    public MzMLUnmarshallerAdaptor getUnmarshaller() {
        return unmarshaller;
    }

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

    public List<CVLookup> getCvLookups() throws DataAccessException {
        try {
            CVList rawCvList = unmarshaller.getCVList();
            return MzMLTransformer.transformCVList(rawCvList);
        } catch (MzMLUnmarshallerException e) {
            logger.error("Creating CvLookups", e);
            throw new DataAccessException("Exception while trying to read a list of cv lookups", e);
        }
    }

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

    @Override
    public List<Sample> getSamples() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            try {
                SampleList rawSample = unmarshaller.getSampleList();
                return MzMLTransformer.transformSampleList(rawSample);
            } catch (MzMLUnmarshallerException e) {
                logger.error("Creating Samples", e);
                throw new DataAccessException("Exception while trying to read smaples", e);
            }
        } else {
            return metaData.getSampleList();
        }
    }

    @Override
    public List<Person> getPersonContacts() throws DataAccessException {
        try {
            FileDescription rawFileDesc = unmarshaller.getFileDescription();
            // List of Persons
            List<Person> personList = MzMLTransformer.transformFileDescriptionToPerson(rawFileDesc);
            return personList;
        } catch (MzMLUnmarshallerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    @Override
    public List<Organization> getOrganizationContacts() throws DataAccessException {
         try {
            FileDescription rawFileDesc = unmarshaller.getFileDescription();
            // List of Persons
            List<Organization> organizationList = MzMLTransformer.transformFileDescriptionOrganization(rawFileDesc);
            return organizationList;
        } catch (MzMLUnmarshallerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    @Override
    public List<SourceFile> getSourceFiles() throws DataAccessException {
        try {
            FileDescription rawFileDesc = unmarshaller.getFileDescription();
            // List of Persons
            List<SourceFile> sourceFileList = MzMLTransformer.transformFileDescriptionToFileSource(rawFileDesc);
            return sourceFileList;
        } catch (MzMLUnmarshallerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;

    }


    @Override
    public ParamGroup getFileContent() throws DataAccessException{
        ParamGroup params = null;
        try {
            FileDescription rawFileDesc = unmarshaller.getFileDescription();
            // List of Persons
            params = MzMLTransformer.transformFileDescriptionToFileContent(rawFileDesc);

        } catch (MzMLUnmarshallerException e) {
            e.printStackTrace();
        }
        return params;
    }

    @Override
    public List<Software> getSoftwareList() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            try {
                SoftwareList rawSoftware = unmarshaller.getSoftwareList();
                return MzMLTransformer.transformSoftwareList(rawSoftware);
            } catch (MzMLUnmarshallerException e) {
                logger.error("Creating Software", e);
                throw new DataAccessException("Exception while trying to read softwares", e);
            }
        } else {
            return metaData.getSoftwareList();
        }
    }

    @Override
    public List<ScanSetting> getScanSettings() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

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

    @Override
    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

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

    @Override
    public List<DataProcessing> getDataProcessings() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

        if (metaData == null) {
            try {
                uk.ac.ebi.jmzml.model.mzml.DataProcessingList rawDataProcList = unmarshaller.getDataProcessingList();
                return MzMLTransformer.transformDataProcessingList(rawDataProcList);
            } catch (MzMLUnmarshallerException e) {
                logger.error("Creating DataProcessings", e);
                throw new DataAccessException("Exception while trying to read data processing list", e);
            }
        } else {
            return metaData.getDataProcessingList();
        }

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
                    cache.store(CacheCategory.SPECTRUM, id, spectrum);
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
                    cache.store(CacheCategory.CHROMATOGRAM, id, chroma);
                }
            } catch (MzMLUnmarshallerException ex) {
                logger.error("Get chromatogram by id", ex);
                throw new DataAccessException("Exception while trying to read Chromatogram using chromatogram ID", ex);
            }
        }
        return chroma;
    }

    @Override
    public void close() {
        unmarshaller = null;
        super.close();
    }

    @Override
    public ExperimentMetaData getExperimentMetaData() throws DataAccessException {

        ExperimentMetaData metaData = super.getExperimentMetaData();

        if(metaData == null){
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
            List<Software> softwares = getSoftwareList();
            // ScanSettings list
            ParamGroup fileContent = getFileContent();
            metaData = new ExperimentMetaData(fileContent,id,accession,version,null,samples,softwares,personList,sourceFileList,null,organizationList,null,null,null,null);
        }
        return metaData;
    }

    @Override
    public MzGraphMetaData getMzGraphMetaData() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();
        if(metaData == null){
            List<ScanSetting> scanSettings = getScanSettings();
            List<DataProcessing> dataProcessings = getDataProcessings();
            List<InstrumentConfiguration> instrumentConfigurations = getInstrumentConfigurations();
            metaData = new MzGraphMetaData(null,null,scanSettings,instrumentConfigurations,dataProcessings);
        }
        return metaData;
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
