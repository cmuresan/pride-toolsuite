package uk.ac.ebi.pride.data.controller.impl.ControllerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.cache.impl.MzXmlCacheBuilder;
import uk.ac.ebi.pride.data.controller.impl.Transformer.MzXmlTransformer;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.DataProcessing;
import uk.ac.ebi.pride.data.core.InstrumentConfiguration;
import uk.ac.ebi.pride.data.core.ParamGroup;
import uk.ac.ebi.pride.data.core.Software;
import uk.ac.ebi.pride.data.core.SourceFile;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.data.io.file.MzXmlUnmarshallerAdaptor;
import uk.ac.ebi.pride.data.utils.MD5Utils;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLFile;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLParsingException;
import uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.MsInstrument;
import uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Operator;
import uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.ParentFile;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.Duration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the controller to parse an retrieve the information from mzXML files,
 * It retrieve the spectrum information, and also the most important metadata, the relevant and
 * portable to the ms-core-api information from the mzXML files. this controller use the MzXMLFile Api
 * to parse an retrieve the information from mzXML files.
 *
 * User: yperez, rwang
 * Date: 2/27/12
 * Time: 2:12 PM
 *
 */

public class MzXmlControllerImpl extends CachedDataAccessController {

    private static final Logger logger = LoggerFactory.getLogger(MzXmlControllerImpl.class);
    /**
     * Pattern for validating mzXML format
     */
    private static Pattern mzXmlHeaderPattern = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<(mzXML) xmlns=.*", Pattern.MULTILINE);

    /**
     * Reader for getting information from mzXML file
     */
    private MzXmlUnmarshallerAdaptor unmarshaller;

    /**
     * Construct a data access controller using a given mzXML file
     *
     * @param file jmzReader file
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception
     */
    public MzXmlControllerImpl(File file) throws DataAccessException {
        this(file, null);
    }

    /**
     * Construct a data access controller using a given mzML file and data access mode
     *
     * @param file mzXML file
     * @param mode data access mode
     * @throws DataAccessException data access exception
     */
    public MzXmlControllerImpl(File file, DataAccessMode mode) throws DataAccessException {
        super(file, mode);
        initialize();
    }

    /**
     * Initialize the data access controller
     *
     *
     * @throws DataAccessException data access exception
     */
    private void initialize() throws DataAccessException {

        File file = (File) this.getSource();
        // create unmarshaller
        MzXMLFile um = null;
        try {
            um = new MzXMLFile(file);
            unmarshaller = new MzXmlUnmarshallerAdaptor(um);
        } catch (MzXMLParsingException e) {
            String msg = "Error while trying to initialize the mzXML file";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }

        // set data source name
        this.setName(file.getName());
        // set the type
        this.setType(DataAccessController.Type.XML_FILE);
        // set the content categories
        this.setContentCategories(DataAccessController.ContentCategory.SPECTRUM,
                DataAccessController.ContentCategory.SAMPLE,
                DataAccessController.ContentCategory.INSTRUMENT,
                DataAccessController.ContentCategory.SOFTWARE,
                DataAccessController.ContentCategory.DATA_PROCESSING);
        // create cache builder
        setCacheBuilder(new MzXmlCacheBuilder(this));
        // populate cache
        populateCache();
    }

    /**
     * Get the backend data reader
     *
     * @return MzMLUnmarshallerAdaptor mzML reader
     */
    public MzXmlUnmarshallerAdaptor getUnmarshaller() {
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
     * Get a list of person contacts
     *
     * @return List<Person>    list of persons
     * @throws DataAccessException data access exception
     */
    @Override
    public List<Person> getPersonContacts() throws DataAccessException {
        try {
            List<Operator> operators = unmarshaller.getPersonContacts();
            // List of Persons
            return MzXmlTransformer.transformPersonContacts(operators);
        } catch (MzXMLParsingException e) {
            String msg = "Error while getting a list of person contacts";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }
    }

    /**
     * The mzXML files do not contain information about Organizations
     * @return
     * @throws DataAccessException
     */
    @Override
    public List<Organization> getOrganizationContacts() throws DataAccessException {
        throw new UnsupportedOperationException("This method is not supported");
    }

    /**
     * Get a list of Source Files. The information of filetype will be converted to CvTerm
     * using the MS ontology.
     *
     * @return List<SourceFile> list of SourceFile
     * @throws DataAccessException
     */

    @Override
    public List<SourceFile> getSourceFiles() throws DataAccessException {
        try {
            List<ParentFile> rawParentFiles = unmarshaller.getParentFiles();
            // List of SourceFiles
            return MzXmlTransformer.transformFileSources(rawParentFiles);
        } catch (MzXMLParsingException e) {
            String msg = "Error while getting a list of source files";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }
    }

    /**
     * This will be summarize the different parent file formats. Looking inside the Cvterms
     * of the mzXML parent file.
     *
     * @return
     * @throws DataAccessException
     */
    @Override
    public ParamGroup getFileContent() throws DataAccessException {
        ParamGroup paramGroup = null;
        Set<CvParam> cvParamSet = null;
        List<SourceFile> sourceFiles = getSourceFiles();
        if(sourceFiles != null){
            cvParamSet = new HashSet<CvParam>();
            for(SourceFile sourceFile: sourceFiles){
                if(sourceFile.getFileFormat() != null){
                    cvParamSet.add(sourceFile.getFileFormat());
                }
            }
        }
        if (cvParamSet != null){
            List<CvParam> cvParams = new ArrayList<CvParam>();
            cvParams.addAll(cvParamSet);
            paramGroup = new ParamGroup(cvParams,null);
        }
        return paramGroup;
    }

    /**
     * The softwares on mzXml are annotated with their names, the type, and the execution
     * time processing the data. The function annotated also a CvParam related with the Type
     * of the Software (Data Processing or Data Acquisition).
     *
     * @return
     * @throws DataAccessException
     */
    @Override
    public List<Software> getSoftwares() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            try {
                List<uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Software> rawSoftware = unmarshaller.getSoftwares();
                return MzXmlTransformer.transformSoftwares(rawSoftware);
            } catch (MzXMLParsingException e) {
                String msg = "Error while getting a list of software";
                logger.error(msg, e);
                throw new DataAccessException(msg, e);
            }
        } else {
            return metaData.getSoftwares();
        }
    }

    /**
     * Is not possible to convert any information from mzXML to the ScanSetting concept in
     * terms of CvTerms.
     *
     * @return List<ScanSetting>   a list of scan settings
     * @throws DataAccessException data access exception
     */
    @Override
    public List<ScanSetting> getScanSettings() throws DataAccessException {
        throw new UnsupportedOperationException("This method is not supported");
    }

    /**
     * Get a list of instrument configurations. Here the function capture the information related with
     * souce, detector and analyzer for each instrument. The name of the instrument is related with the model
     * of the Instrument.
     *
     * @return List<Instrumentconfiguration>   a list of instrument configurations
     * @throws DataAccessException data access exception
     */
    @Override
    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

        if (metaData == null) {
            try {
                List<MsInstrument> rawInstrumentList = unmarshaller.getMsInstruments();
                return MzXmlTransformer.transformMsInstrument(rawInstrumentList);
            } catch (MzXMLParsingException e) {
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
                List<uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.DataProcessing> rawDataProcList = unmarshaller.getDataProcessing();
                return MzXmlTransformer.transformDataProcessingList(rawDataProcList);
            } catch (MzXMLParsingException e) {
                String msg = "Error while getting a list of data processings";
                logger.error(msg, e);
                throw new DataAccessException(msg, e);
            }
        } else {
            return metaData.getDataProcessingList();
        }
    }

    /**
     * Get additional details, the mzXML is like mzML the Additional is the combination of FileContent
     * and StartTime.
     *
     * @return ParamGroup  param group
     * @throws DataAccessException data access exception
     */
    @Override
    public ParamGroup getAdditional() throws DataAccessException {
        ParamGroup fileContent = getFileContent();
        Duration startDate = null;

        try {
            startDate = unmarshaller.getStartDate();
            CvParam cvParamStartDate = MzXmlTransformer.transformDurationToCvParam(startDate, CvTermReference.MS_SCAN_DATE);
            if(cvParamStartDate !=null && fileContent != null) fileContent.addCvParam(cvParamStartDate);
        } catch (DatatypeConfigurationException e) {
            String msg = "Error while getting the started Time";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }
        return fileContent;
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
            uk.ac.ebi.pride.tools.jmzreader.model.Spectrum rawSpec = null;
            try {
                rawSpec = unmarshaller.getSpectrumById(id.toString());
                spectrum = MzXmlTransformer.transformSpectrum(rawSpec);
                if (useCache) {
                    getCache().store(CacheCategory.SPECTRUM, id, spectrum);
                }
            } catch (JMzReaderException ex) {
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
        throw new UnsupportedOperationException("This method is not supported");
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
            String id = ((File) getSource()).getName();
            String accession = null;
            String version = null;
            // SourceFile List
            List<SourceFile> sourceFileList = getSourceFiles();
            // List of Persons
            List<Person> personList = getPersonContacts();
            // List of Organizations
            List<Organization> organizationList = null;
            // Sample list
            List<Sample> samples = null;
            // Software list
            List<Software> softwares = getSoftwares();
            // ScanSettings list
            ParamGroup additional = getAdditional();
            metaData = new ExperimentMetaData(additional, id, accession, version, null, samples, softwares, personList, sourceFileList, null, organizationList, null, null, null, null);
        }


        return metaData;
    }

    @Override
    public MzGraphMetaData getMzGraphMetaData() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();
        if (metaData == null) {
            //Scan settings is not support for mzXml files
            List<ScanSetting> scanSettings = null;
            // Get Data Processing
            List<DataProcessing> dataProcessings = getDataProcessings();
            // Get Instrument Configurations
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

            Matcher matcher = mzXmlHeaderPattern.matcher(content);

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
