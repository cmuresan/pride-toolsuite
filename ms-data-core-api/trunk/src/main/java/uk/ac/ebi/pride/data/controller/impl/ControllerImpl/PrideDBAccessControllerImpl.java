package uk.ac.ebi.pride.data.controller.impl.ControllerImpl;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.ebi.pride.chart.controller.PrideChartSummaryData;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.cache.impl.PrideDBCacheBuilder;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.io.db.*;
import uk.ac.ebi.pride.data.utils.BinaryDataUtils;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.data.utils.Constants;
import uk.ac.ebi.pride.data.utils.MD5Utils;
import uk.ac.ebi.pride.term.CvTermReference;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.*;

/**
 * DataAccessController to access pride public instance.
 * <p/>
 * User: dani, rwang
 * Date: 13-Apr-2010
 * Time: 14:32:25
 */
public class PrideDBAccessControllerImpl extends CachedDataAccessController {

    private static final Logger logger = LoggerFactory.getLogger(PrideDBAccessControllerImpl.class);

    /**
     * default data access mode
     */
    private static final DataAccessMode DEFAULT_ACCESS_MODE = DataAccessMode.CACHE_ONLY;

    private final static String SAMPLE_ID = "sample1";
    private final static String COMMENTS = "comments";
    private final static int PROCESSING_METHOD_ORDER = 1;
    private final static String DATA_PROCESSING_ID = "dataprocessing1";
    private final static String PROTOCOL_ID = "protocol1";

    private final JdbcTemplate jdbcTemplate;

    public PrideDBAccessControllerImpl() throws DataAccessException {
        this(DEFAULT_ACCESS_MODE, null);
    }

    public PrideDBAccessControllerImpl(DataAccessMode mode) throws DataAccessException {
        this(mode, null);
    }

    /**
     * Open a pride database connection with a specified experiment accession.
     *
     * @param experimentAcc experiment accession
     * @throws DataAccessException data access exception
     */
    public PrideDBAccessControllerImpl(Comparable experimentAcc) throws DataAccessException {
        this(DEFAULT_ACCESS_MODE, experimentAcc);
    }

    /**
     * Open a pride database connection with a specified access mode and experiment accession.
     *
     * @param mode          data access mode
     * @param experimentAcc experiment accession
     * @throws DataAccessException data access exception
     */
    public PrideDBAccessControllerImpl(DataAccessMode mode, Comparable experimentAcc) throws DataAccessException {
        super(mode);
        this.jdbcTemplate = new JdbcTemplate(PooledConnectionFactory.getInstance().getConnectionPool());
        initialize(experimentAcc);
    }

    private void initialize(Comparable experimentAcc) throws DataAccessException {
        // set type
        this.setType(Type.DATABASE);

        // set the content categories
        this.setContentCategories(ContentCategory.SPECTRUM,
                ContentCategory.PROTEIN,
                ContentCategory.PEPTIDE,
                ContentCategory.SAMPLE,
                ContentCategory.PROTOCOL,
                ContentCategory.INSTRUMENT,
                ContentCategory.SOFTWARE,
                ContentCategory.DATA_PROCESSING,
                ContentCategory.QUANTITATION);

        // set the foreground experiment acc
        if (experimentAcc != null) {
            setForegroundExperimentAcc(experimentAcc);
        }

        // set cache builder
        setCacheBuilder(new PrideDBCacheBuilder(this));

        // populate cache
        populateCache();
    }

    @Override
    public String getUid() {
        String uid = super.getUid();
        if (uid == null) {
            // create a new unique id
            Comparable acc = getForegroundExperimentAcc();
            String msg = "PRIDE public mysql instance accession: " + (acc == null ? Constants.NOT_AVAILABLE : acc);
            try {
                uid = MD5Utils.generateHash(msg);
            } catch (NoSuchAlgorithmException e) {
                String err = "Failed to generate unique id for mzML file";
                logger.error(err, e);
            }
        }
        return uid;
    }

    private List<CvParam> getCvParams(String table_name, int parent_element_id) throws DataAccessException {
        String query = "SELECT accession, name, value, cv_label FROM " + table_name
                + " WHERE parent_element_fk = ? AND cv_label is not null";

        return jdbcTemplate.query(query, new CvParamRowMapper(), parent_element_id);
    }

    //same as before, but returns a list of UserParam

    private List<UserParam> getUserParams(String table_name, int parent_element_id) {
        String query = "SELECT name, value FROM " + table_name +
                " WHERE parent_element_fk = ? AND cv_label is null";

        return jdbcTemplate.query(query, new UserParamRowMapper(), parent_element_id);
    }


    public List<SourceFile> getSourceFiles() {
        logger.debug("Get source files");

        String query = "SELECT sf.name_of_file, sf.path_to_file FROM mzdata_source_file sf, mzdata_mz_data mz " +
                "WHERE mz.accession_number= ? and mz.source_file_id=sf.source_file_id";

        return jdbcTemplate.query(query, new SourceFileRowMapper(), getForegroundExperimentAcc());
    }


    private List<ParamGroup> getContacts() {
        logger.debug("Get contacts");

        String query = "SELECT contact_name, institution, contact_info FROM mzdata_contact sf, mzdata_mz_data mz " +
                "WHERE mz.accession_number= ? and mz.mz_data_id=sf.mz_data_id";

        return jdbcTemplate.query(query, new ContactRowMapper(), getForegroundExperimentAcc());
    }

    @Override
    public List<Person> getPersonContacts() throws DataAccessException {
        ExperimentMetaData metadata = super.getExperimentMetaData();
        if (metadata == null) {
            String query = "SELECT contact_name, institution, contact_info FROM mzdata_contact sf, mzdata_mz_data mz " +
                    "WHERE mz.accession_number= ? and mz.mz_data_id=sf.mz_data_id";

            return jdbcTemplate.query(query, new PersonRowMapper(), getForegroundExperimentAcc());
        }
        return metadata.getPersonList();
    }

    @Override
    public List<Organization> getOrganizationContacts() throws DataAccessException {
        ExperimentMetaData metadata = super.getExperimentMetaData();
        if (metadata == null) {
            String query = "SELECT contact_name, institution, contact_info FROM mzdata_contact sf, mzdata_mz_data mz " +
                    "WHERE mz.accession_number= ? and mz.mz_data_id=sf.mz_data_id";

            return jdbcTemplate.query(query, new OrganizationRowMapper(), getForegroundExperimentAcc());
        }
        return metadata.getOrganizationList();
    }

    @Override
    public List<Sample> getSamples() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            logger.debug("Get samples");

            String query = "SELECT mz_data_id, sample_name FROM mzdata_mz_data mz WHERE mz.accession_number= ?";

            List<Map<String, Object>> results = jdbcTemplate.queryForList(query, getForegroundExperimentAcc());

            List<Sample> samples = new ArrayList<Sample>();

            for (Map<String, Object> result : results) {
                int mz_data_id = ((Long) result.get("mz_data_id")).intValue();
                String sample_name = (String) result.get("sample_name");
                List<UserParam> userParam = getUserParams("mzdata_sample_param", mz_data_id);
                List<CvParam> cvParam = getCvParams("mzdata_sample_param", mz_data_id);
                samples.add(new Sample(new ParamGroup(cvParam, userParam), SAMPLE_ID, sample_name));
            }

            return samples;
        }

        return metaData.getSampleList();
    }

    @Override
    public List<Software> getSoftwares() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            logger.debug("Getting software");

            String query = "SELECT software_name, software_version, software_completion_time, software_comments FROM mzdata_mz_data mz WHERE mz.accession_number= ?";

            return jdbcTemplate.query(query, new SoftwareRowMapper(), getForegroundExperimentAcc());
        }
        return metaData.getSoftwares();
    }

    @Override
    public List<ScanSetting> getScanSettings() throws DataAccessException {
        throw new UnsupportedOperationException("This method is unsupported");
    }

    private List<ParamGroup> getAnalyzerList(int mz_data_id) throws DataAccessException {
        logger.debug("Get analyzer list");

        List<ParamGroup> analyzerList = new ArrayList<ParamGroup>();

        String query = "SELECT analyzer_id FROM mzdata_analyzer WHERE mz_data_id = ?";
        List<Integer> analyzerIds = jdbcTemplate.queryForList(query, Integer.class, mz_data_id);

        for (Integer analyzerId : analyzerIds) {
            List<UserParam> userParams = getUserParams("mzdata_analyzer_param", analyzerId);
            List<CvParam> cvParams = getCvParams("mzdata_analyzer_param", analyzerId);
            ParamGroup params = new ParamGroup(cvParams, userParams);
            analyzerList.add(params);
        }

        return analyzerList;
    }

    @Override
    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

        if (metaData == null) {
            logger.debug("Get instrument configurations");

            List<InstrumentConfiguration> instrumentConfigurations = new ArrayList<InstrumentConfiguration>();
            //get software
            Software software = null;
            if (getSoftwares().size() > 0) {
                software = getSoftwares().get(0);
            }

            // get instrument
            String query = "SELECT instrument_name, mz_data_id FROM mzdata_mz_data mz WHERE mz.accession_number= ?";

            List<Map<String, Object>> results = jdbcTemplate.queryForList(query, getForegroundExperimentAcc());
            for (Map<String, Object> result : results) {
                int mz_data_id = ((Long) result.get("mz_data_id")).intValue();
                //instrument params
                // ParamGroup params = new ParamGroup(getCvParams("mzdata_instrument_param", mz_data_id), getUserParams("mzdata_instrument_param", mz_data_id));
                ParamGroup params = new ParamGroup();
                CvTermReference cvTerm = CvTermReference.INSTRUMENT_MODEL;
                params.addCvParam(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                        (String) result.get("instrument_name"), null, null, null));
                // create instrument components
                //create source
                //create source object
                int sourceOrder = 1;
                InstrumentComponent sourceInstrument = new InstrumentComponent(sourceOrder, new ParamGroup(getCvParams("mzdata_instrument_source_param", mz_data_id), getUserParams("mzdata_instrument_source_param", mz_data_id)));
                List<InstrumentComponent> sources = new ArrayList<InstrumentComponent>();
                sources.add(sourceInstrument);
                //create detector
                int detectorOrder = getAnalyzerList(mz_data_id).size() + 2;
                InstrumentComponent detectorInstrument = new InstrumentComponent(detectorOrder, new ParamGroup(getCvParams("mzdata_instrument_detector_param", mz_data_id), getUserParams("mzdata_instrument_detector_param", mz_data_id)));
                //create analyzer
                List<InstrumentComponent> detectors = new ArrayList<InstrumentComponent>();
                detectors.add(detectorInstrument);
                List<ParamGroup> paramGroupAnalyzers = getAnalyzerList(mz_data_id);
                List<InstrumentComponent> analyzers = new ArrayList<InstrumentComponent>();
                int orderCnt = 2;
                for (ParamGroup analyzer : paramGroupAnalyzers) {
                    InstrumentComponent analyzerInstrument = new InstrumentComponent(orderCnt, analyzer);
                    analyzers.add(analyzerInstrument);
                }
                instrumentConfigurations.add(new InstrumentConfiguration((String) result.get("instrument_name"), null, software, sources, analyzers, detectors, params));
            }

            return instrumentConfigurations;
        } else {
            return metaData.getInstrumentConfigurations();
        }
    }

    @Override
    public List<DataProcessing> getDataProcessings() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

        if (metaData == null) {
            logger.debug("Getting data processings");

            Software software = null;
            if (getSoftwares().size() > 0) {
                software = getSoftwares().get(0);
            }

            List<ProcessingMethod> procMethods = new ArrayList<ProcessingMethod>();
            List<DataProcessing> dataProcessings = new ArrayList<DataProcessing>();
            String query = "SELECT mz_data_id FROM mzdata_mz_data mz WHERE mz.accession_number= ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query, getForegroundExperimentAcc());

            for (Map<String, Object> result : results) {
                int mz_data_id = ((Long) result.get("mz_data_id")).intValue();
                List<UserParam> userParams = getUserParams("mzdata_processing_method_param", mz_data_id);
                List<CvParam> cvParams = getCvParams("mzdata_processing_method_param", mz_data_id);
                ParamGroup params = new ParamGroup(cvParams, userParams);
//                CvTermReference cvTerm = CvTermReference.CONVERSION_TO_MZML;
//                params.addCvParam(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(), null, null, null, null));
                procMethods.add(new ProcessingMethod(PROCESSING_METHOD_ORDER, software, params));
                dataProcessings.add(new DataProcessing(DATA_PROCESSING_ID, procMethods));
            }

            return dataProcessings;
        }
        return metaData.getDataProcessingList();
    }

    /**
     * for a given experimentId, will return the protocol_steps_id sorted by index
     */
    private List<Integer> getProtocolStepsById(int experimentId) {
        logger.debug("Get protocol steps");

        String query = "SELECT protocol_step_id FROM pride_protocol_step pp, pride_experiment pe WHERE " +
                "pe.experiment_id = pp.experiment_id AND pe.accession = ? ORDER BY protocol_step_index";

        return jdbcTemplate.queryForList(query, Integer.class, experimentId);
    }

    private ExperimentProtocol getProtocol() throws DataAccessException {
        logger.debug("Get experiment protocol");

        // protocol name
        String query = "SELECT protocol_name FROM pride_experiment WHERE accession= ?";
        String protocol_name = jdbcTemplate.queryForObject(query, String.class, getForegroundExperimentAcc());

        List<Integer> protocol_steps = getProtocolStepsById(Integer.parseInt(getForegroundExperimentAcc().toString()));

        //for each protocol_step, get the paramGroup
        List<ParamGroup> paramGroup = new ArrayList<ParamGroup>();
        for (int protocol_step_id : protocol_steps) {
            List<CvParam> cvParams = getCvParams("pride_protocol_param", protocol_step_id);
            List<UserParam> userParams = getUserParams("pride_protocol_param", protocol_step_id);
            paramGroup.add(new ParamGroup(cvParams, userParams));
        }

        return new ExperimentProtocol(null, PROTOCOL_ID, protocol_name, paramGroup);
    }

    /**
     * Get a list of references
     *
     * @return List<Reference> a list of references
     */
    public List<Reference> getReferences() throws DataAccessException {
        logger.debug("Get references");

        List<Reference> references = new ArrayList<Reference>();

        String query = "SELECT reference_line, pr.reference_id FROM pride_experiment pe, pride_reference pr, pride_reference_exp_link pl WHERE " +
                "pe.accession = ? AND pl.reference_id = pr.reference_id AND pl.experiment_id = pe.experiment_id";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, getForegroundExperimentAcc());
        for (Map<String, Object> result : results) {
            List<UserParam> userParams = getUserParams("pride_reference_param", ((Long) result.get("reference_id")).intValue());
            List<CvParam> cvParams = getCvParams("pride_reference_param", ((Long) result.get("reference_id")).intValue());
            references.add(new Reference(new ParamGroup(cvParams, userParams), (String) result.get("reference_line")));
        }

        return references;
    }


    /**
     * Get experiment additional params
     *
     * @return ParamGroup  experiment additional params
     * @throws DataAccessException data access exception
     */
    @Override
    public ParamGroup getAdditional() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            logger.debug("Get additional params");

            int experiment_id = getExperimentId(getForegroundExperimentAcc());
            List<UserParam> userParam = getUserParams("pride_experiment_param", experiment_id);
            List<CvParam> cvParam = getCvParams("pride_experiment_param", experiment_id);
            return new ParamGroup(cvParam, userParam);

        } else {
            return metaData.getAdditional();
        }
    }


    /**
     * Get PRIDE internal experiment id
     *
     * @param accession PRIDE experiment accession
     * @return int PRIDE internal experiment id
     */
    private int getExperimentId(Comparable accession) {
        String query = "SELECT experiment_id FROM pride_experiment WHERE accession= ?";
        return jdbcTemplate.queryForInt(query, accession);
    }

    @Override
    public ExperimentMetaData getExperimentMetaData() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();
        if (metaData == null) {

            String query = "SELECT pe.title, pe.accession, pe.short_label FROM pride_experiment pe WHERE pe.accession = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(query, getForegroundExperimentAcc());

            String accession = (String) result.get("accession");
            String version = "2.1";
            String title = (String) result.get("title");
            String shortLabel = (String) result.get("short_label");

            List<Sample> samples = getSamples();
            List<Software> software = getSoftwares();
            ExperimentProtocol protocol = getProtocol();
            ParamGroup additional = getAdditional();
            List<SourceFile> sourceFiles = getSourceFiles();
            List<Person> persons = getPersonContacts();
            List<Organization> organizations = getOrganizationContacts();
            List<Reference> references = getReferences();
            metaData = new ExperimentMetaData(additional, accession, title, version, shortLabel, samples, software, persons, sourceFiles, null, organizations, references, null, null, protocol);
            // store in cache
            getCache().store(CacheCategory.EXPERIMENT_METADATA, metaData);
        }

        return metaData;
    }

    @Override
    public List<CVLookup> getCvLookups() throws DataAccessException {
        logger.debug("Getting cv lookups");

        String query = "SELECT sf.cv_label, sf.version, sf.address, sf.full_name FROM mzdata_cv_lookup sf, mzdata_mz_data mz, mzdata_cv_lookup_mzdata_lnk ln WHERE mz.accession_number= ? and mz.mz_data_id=ln.mz_data_id and sf.cv_lookup_id=ln.cv_lookup_id";

        return jdbcTemplate.query(query, new CVLookupRowMapper(), getForegroundExperimentAcc());
    }

    private BinaryDataArray getBinaryDataArray(int array_binary_id, CvTermReference binaryType) throws UnsupportedEncodingException {
        String precisionQuery = "SELECT data_precision, data_endian FROM mzdata_binary_array WHERE binary_array_id = ?";
        Map<String, Object> precisionResult = jdbcTemplate.queryForMap(precisionQuery, array_binary_id);

        CvTermReference dataType = "32".equals(precisionResult.get("data_precision")) ? CvTermReference.FLOAT_32_BIT : CvTermReference.FLOAT_64_BIT;
        ByteOrder order = "big".equals(precisionResult.get("data_endian")) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;

        String baseQuery = "SELECT base_64_data FROM mzdata_base_64_data WHERE binary_array_id = ?";
        String total_array = jdbcTemplate.queryForObject(baseQuery, String.class, array_binary_id);

        double[] binaryDoubleArr;
        if (total_array != null) {
            binaryDoubleArr = BinaryDataUtils.toDoubleArray(Base64.decodeBase64(total_array.getBytes("ASCII")), dataType, order);
        } else {
            binaryDoubleArr = BinaryDataUtils.toDoubleArray(null, dataType, order);
        }

        // create param group
        ParamGroup params = new ParamGroup();
        // add precision
        if (dataType != null) {
            params.addCvParam(new CvParam(dataType.getAccession(), dataType.getName(), dataType.getCvLabel(), null, null, null, null));
        }
        // add compression type
        CvTermReference compressionTerm = CvTermReference.NO_COMPRESSION;
        params.addCvParam(new CvParam(compressionTerm.getAccession(), compressionTerm.getName(), compressionTerm.getCvLabel(), null, null, null, null));
        params.addCvParam(new CvParam(binaryType.getAccession(), binaryType.getName(), binaryType.getCvLabel(), null, null, null, null));


        return new BinaryDataArray(null, binaryDoubleArr, params);
    }

    public List<ParamGroup> getScanWindows(BigDecimal mz_range_start, BigDecimal mz_range_stop) {
        List<ParamGroup> scanWindows = null;

        // mz range start/stop are optional in pride xml
        if (mz_range_start != null && mz_range_stop != null) {
            CvTermReference mzStartTerm = CvTermReference.SCAN_WINDOW_LOWER_LIMIT;
            CvParam mzRangeStart = new CvParam(mzStartTerm.getAccession(), mzStartTerm.getName(), mzStartTerm.getCvLabel(),
                    mz_range_start.toString(), null, null, null);
            CvTermReference mzStopTerm = CvTermReference.SCAN_WINDOW_UPPER_LIMIT;
            CvParam mzRangeStop = new CvParam(mzStopTerm.getAccession(), mzStopTerm.getName(), mzStopTerm.getCvLabel(),
                    mz_range_stop.toString(), null, null, null);
            scanWindows = new ArrayList<ParamGroup>();
            ParamGroup scanWindow = new ParamGroup();
            scanWindow.addCvParam(mzRangeStart);
            scanWindow.addCvParam(mzRangeStop);
            scanWindows.add(scanWindow);
        }

        return scanWindows;
    }

    public List<Scan> getScanList(int acq_specification_id, List<ParamGroup> scanWindows) throws DataAccessException {
        List<Scan> scanList = new ArrayList<Scan>();

        String query = "SELECT acquisition_id FROM mzdata_acquisition WHERE acq_specification_id = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(query, acq_specification_id);

        List<CvParam> cvParams = getCvParams("mzdata_acquisition_param", (Integer) result.get("acquisition_id"));

        List<UserParam> userParams = getUserParams("mzdata_acquisition_param", (Integer) result.get("acquisition_id"));
        ParamGroup params = new ParamGroup(cvParams, userParams);

        scanList.add(new Scan(null, null, null, null, scanWindows, params));

        return scanList;
    }

    private List<Precursor> getPrecursorsBySpectrum_id(int spectrum_id) throws DataAccessException {
        logger.debug("Get precursors");

        List<Precursor> precursors = new ArrayList<Precursor>();
        List<ParamGroup> selectedIon = new ArrayList<ParamGroup>();

        String query = "SELECT precursor_id, precursor_spectrum_id FROM mzdata_precursor WHERE spectrum_id = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, spectrum_id);

        for (Map<String, Object> result : results) {
            int precursorId = ((Long) result.get("precursor_id")).intValue();
            String spectrumId = result.get("precursor_spectrum_id").toString();
            List<CvParam> actCvParams = getCvParams("mzdata_activation_param", precursorId);
            List<UserParam> actUserParams = getUserParams("mzdata_activation_param", precursorId);
            Spectrum spectrum = getSpectrumById(spectrumId);
            ParamGroup activation = new ParamGroup(actCvParams, actUserParams);

            List<CvParam> selCvParams = getCvParams("mzdata_ion_selection_param", precursorId);
            List<UserParam> selUserParams = getUserParams("mzdata_ion_selection_param", precursorId);
            selectedIon.add(new ParamGroup(selCvParams, selUserParams));
            precursors.add(new Precursor(spectrum, null, null, null, selectedIon, activation));
        }

        return precursors;
    }

    /**
     * create a cv param for spectrum type
     *
     * @param value original value of the spectrum type, map discrete to centroid spectrum, continuous to profile spectrum.
     * @return CvParam cv param in core data model format.
     */
    private CvParam getSpectrumType(String value) {
        CvParam cvParam = null;
        CvTermReference cvTerm = null;
        if ("discrete".equals(value)) {
            cvTerm = CvTermReference.CENTROID_SPECTRUM;
        } else if ("continuous".equals(value)) {
            cvTerm = CvTermReference.PROFILE_SPECTRUM;
        }

        if (cvTerm != null) {
            cvParam = new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                    value, null, null, null);
        }
        return cvParam;
    }

    private List<UserParam> getSpectrumDesc(int spectrumId) {
        logger.debug("Get spectrum description");

        List<UserParam> userParams = new ArrayList<UserParam>();

        String query = "SELECT comment_text FROM mzdata_spectrum_desc_comment WHERE spectrum_id = ?";
        List<String> results = jdbcTemplate.queryForList(query, String.class, spectrumId);

        for (String result : results) {
            userParams.add(new UserParam(COMMENTS, null, result, null, null, null));
        }

        return userParams;
    }

    /**
     * create a cv param for method of combination
     *
     * @param value original value of the method of combination.
     * @return CvParam  cv param in core data model format.
     */
    private CvParam getMethodOfCombination(String value) {
        CvTermReference cvTerm = CvTermReference.NO_COMBINATION;

        if (value != null && value.toLowerCase().contains("sum")) {
            cvTerm = CvTermReference.SUM_OF_SPECTRA;
        } else {
            value = null;
        }

        return new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                value, null, null, null);
    }

    @Override
    public Spectrum getSpectrumById(Comparable spectrumId, boolean useCache) throws DataAccessException {
        logger.debug("Getting a spectrum: {}", spectrumId);
        Spectrum spectrum = super.getSpectrumById(spectrumId, useCache);

        if (spectrum == null) {
            ScanList scanList;
            List<Scan> scans = new ArrayList<Scan>();
            List<Precursor> precursors;
            ParamGroup spectrumParams = new ParamGroup();
            ParamGroup scanParams = new ParamGroup();
            List<BinaryDataArray> binaryArray = new ArrayList<BinaryDataArray>();
            int defaultArrLength;

            if (spectrumId == null) {
                return spectrum;
            }

            String query = "SELECT spectrum_id, mz_data_id, mz_range_start, " +
                    "mz_range_stop, ms_level, mz_array_binary_id, inten_array_binary_id, spectrum_type, method_of_combination, ms.acq_specification_id " +
                    "FROM mzdata_spectrum ms LEFT JOIN mzdata_acq_specification ma ON ms.acq_specification_id = ma.acq_specification_id WHERE spectrum_id = ?";

            int idInt = Integer.parseInt(spectrumId.toString());
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query, idInt);
            for (Map<String, Object> result : results) {
                try {
                    int index = CollectionUtils.getIndex(getSpectrumIds(), idInt);

                    //do scanlist
                    List<ParamGroup> scanWindows = getScanWindows((BigDecimal) result.get("mz_range_start"), (BigDecimal) result.get("mz_range_stop"));
                    int msAcqSpecId = ((Long) result.get("acq_specification_id")).intValue();
                    if (msAcqSpecId != 0) {
                        // add method of combination
                        scanParams.addCvParam(getMethodOfCombination((String) result.get("method_of_combination")));
                        scans.addAll(getScanList(msAcqSpecId, scanWindows));

                    } else {
                        // add method of combination cv param to param group
                        scanParams.addCvParam(getMethodOfCombination(null));
                        // create a Scan
                        Scan scan = new Scan(null, null, null, null, scanWindows, null);
                        scans.add(scan);
                    }
                    // assemble scan list object
                    scanList = new ScanList(scans, scanParams);

                    precursors = getPrecursorsBySpectrum_id(idInt);

                    int mzArrBinId = ((Long) result.get("mz_array_binary_id")).intValue();
                    int intenArrBinId = ((Long) result.get("inten_array_binary_id")).intValue();
                    BinaryDataArray mz = getBinaryDataArray(mzArrBinId, CvTermReference.MZ_ARRAY);
                    BinaryDataArray inten = getBinaryDataArray(intenArrBinId, CvTermReference.INTENSITY_ARRAY);
                    binaryArray.add(mz);
                    binaryArray.add(inten);
                    defaultArrLength = mz.getDoubleArray().length;
                    //additional params
                    // add ms level
                    CvTermReference msLevelCv = CvTermReference.MS_LEVEL;
                    spectrumParams.addCvParam(new CvParam(msLevelCv.getAccession(), msLevelCv.getName(), msLevelCv.getCvLabel(),
                            ((Long) result.get("ms_level")).intValue() + "", null, null, null));
                    // add spectrum type
                    CvTermReference massSpecCv = CvTermReference.MASS_SPECTRUM;
                    spectrumParams.addCvParam(new CvParam(massSpecCv.getAccession(), massSpecCv.getName(), massSpecCv.getCvLabel(),
                            null, null, null, null));
                    if ((String) result.get("spectrum_type") != null) {
                        spectrumParams.addCvParam(getSpectrumType((String) result.get("spectrum_type")));
                    }
                    // add spectrum instrument
                    spectrumParams.addCvParams(getCvParams("mzdata_spectrum_instrument_param", idInt));
                    spectrumParams.addUserParams(getUserParams("mzdata_spectrum_instrument_param", idInt));
                    // add comments
                    spectrumParams.addUserParams(getSpectrumDesc(idInt));
                    spectrum = new Spectrum(spectrumParams, (Long)result.get("spectrum_id"), null, index, null, defaultArrLength, binaryArray, null, null, scanList, precursors, null);


                    if (useCache) {
                        getCache().store(CacheCategory.SPECTRUM, spectrumId, spectrum);
                    }
                } catch (UnsupportedEncodingException e) {
                    logger.error("Failed to decode binary data array", e);
                }
            }
        }
        return spectrum;
    }

    private List<Double> getDeltaValues(int modification_id, String deltaType) {
        logger.debug("Get delta values");

        String query = "SELECT mass_delta_value FROM pride_mass_delta WHERE modification_id = ? AND classname = ?";
        return jdbcTemplate.queryForList(query, Double.class, modification_id, deltaType);
    }

    private List<Modification> getModificationsPeptide(int peptide_id) throws DataAccessException {
        logger.debug("Get a list of modifications, peptide {}", peptide_id);

        List<Modification> modifications = new ArrayList<Modification>();

        String query = "SELECT modification_id, accession, mod_database, mod_database_version, location FROM pride_modification WHERE peptide_id = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, peptide_id);

        for (Map<String, Object> result : results) {
            ParamGroup params = new ParamGroup(getCvParams("pride_modification_param", (Integer) result.get("modification_id")), getUserParams("pride_modification_param",
                    (Integer) result.get("modification_id")));
            List<Double> monoMassDeltas = getDeltaValues((Integer) result.get("modification_id"), "uk.ac.ebi.pride.rdbms.ojb.model.core.MonoMassDeltaBean");
            List<Double> avgMassDeltas = getDeltaValues((Integer) result.get("modification_id"), "uk.ac.ebi.pride.rdbms.ojb.model.core.AverageMassDeltaBean");
            modifications.add(new Modification(params, (String) result.get("accession"), null, (Integer) result.get("location"), null, monoMassDeltas, avgMassDeltas, (String) result.get("mod_database"), (String) result.get("mod_database_version")));
        }

        return modifications;
    }

    private List<FragmentIon> getFragmentIons(int peptide_id) throws DataAccessException {
        logger.debug("Get list of fragment ions for peptide {}", peptide_id);

        List<FragmentIon> fragmentIons = new ArrayList<FragmentIon>();

        String query = "SELECT fragment_ion_id, mz, intensity, mass_error, retention_time_error, accession_ion_type, ion_type_name, fragment_ion_number, ion_charge FROM pride_fragment_ion WHERE peptide_id = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, peptide_id);

        for (Map<String, Object> result : results) {
            List<CvParam> cvParams = new ArrayList<CvParam>();
            //add mz param
            CvTermReference cvTerm = CvTermReference.PRODUCT_ION_MZ;
            cvParams.add(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                    result.get("mz") == null ? null : result.get("mz").toString(), null, null, null));
            cvTerm = CvTermReference.PRODUCT_ION_INTENSITY;
            cvParams.add(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                    result.get("intensity") == null ? null : result.get("intensity").toString(), null, null, null));
            cvTerm = CvTermReference.PRODUCT_ION_MASS_ERROR;
            cvParams.add(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                    result.get("mass_error") == null ? null : result.get("mass_error").toString(), null, null, null));
            cvTerm = CvTermReference.PRODUCT_ION_RETENTION_TIME_ERROR;
            cvParams.add(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                    result.get("retention_time_error") == null ? null : result.get("retention_time_error").toString(), null, null, null));
            cvTerm = CvTermReference.PRODUCT_ION_TYPE;
            cvParams.add(new CvParam((String) result.get("accession_ion_type"), (String) result.get("ion_type_name"), cvTerm.getCvLabel(),
                    result.get("fragment_ion_number") == null ? null : result.get("fragment_ion_number").toString(), null, null, null));
            cvTerm = CvTermReference.PRODUCT_ION_CHARGE;
            cvParams.add(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                    result.get("ion_charge") == null ? null : result.get("ion_charge").toString(), null, null, null));
            //get the charge
            cvParams.addAll(getCvParams("pride_fragment_ion_param", ((Long) result.get("fragment_ion_id")).intValue()));
            fragmentIons.add(new FragmentIon(new ParamGroup(cvParams, null)));
        }

        return fragmentIons;
    }

    private Spectrum getSpectrumByPeptide(int experiment_id, int spectrum_ref) throws DataAccessException {
        logger.debug("Get spectrum by spectrum reference {}", spectrum_ref);

        String query = "SELECT spectrum_id FROM pride_experiment pe, mzdata_spectrum ms WHERE pe.experiment_id = ? AND " +
                "pe.mz_data_id = ms.mz_data_id AND ms.spectrum_identifier = ?";

        String spectrumId = jdbcTemplate.queryForObject(query, String.class, experiment_id, spectrum_ref);

        return getSpectrumById(spectrumId);
    }

    private List<Peptide> getPeptideIdentification(int identification_id, int experiment_id) throws DataAccessException {
        logger.debug("Get a list of peptides for identification {}", identification_id);
        List<Peptide> peptides = new ArrayList<Peptide>();

        String query = "SELECT peptide_id, sequence, pep_start, pep_end, spectrum_ref FROM pride_peptide WHERE identification_id = ?";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, identification_id);

        for (Map<String, Object> result : results) {
            ParamGroup params = new ParamGroup(getCvParams("pride_peptide_param", (Integer) result.get("peptide_id")),
                    getUserParams("pride_peptide_param", (Integer) result.get("peptide_id")));
            List<Modification> modifications = getModificationsPeptide((Integer) result.get("peptide_id"));
            List<FragmentIon> fragmentIons = getFragmentIons((Integer) result.get("peptide_id"));
            Spectrum spectrum = getSpectrumByPeptide(experiment_id, (Integer) result.get("spectrum_ref"));
            PeptideSequence peptideSequence = new PeptideSequence(null, null, (String)result.get("sequence"), modifications);
            List<PeptideEvidence> peptideEvidences = new ArrayList<PeptideEvidence>();
            PeptideEvidence peptideEvidence = new PeptideEvidence(null, null, (Integer)result.get("pep_start"), (Integer)result.get("pep_end"), false, peptideSequence, null);
            peptideEvidences.add(peptideEvidence);

            int charge = DataAccessUtilities.getPrecursorCharge(spectrum);
            double mz = DataAccessUtilities.getPrecursorMz(spectrum);

            //get Scores
            Score score = DataAccessUtilities.getScore(params);

            SpectrumIdentification spectrumIdentification = new SpectrumIdentification(params, null, null, charge, mz, 0.0, 0.0, peptideSequence, -1, false, null, null, peptideEvidences, fragmentIons, score, spectrum, null);
            peptides.add(new Peptide(peptideEvidence, spectrumIdentification));
        }

        return peptides;
    }


    private Gel getPeptideGel(int gel_id, double x_coordinate, double y_coordinate, double molecular_weight, double pi) throws DataAccessException {
        logger.debug("Get peptide gel {}", gel_id);


        String query = "SELECT gel_link FROM pride_gel WHERE gel_id = ?";

        String gelLink = jdbcTemplate.queryForObject(query, String.class, gel_id);

        ParamGroup params = new ParamGroup(getCvParams("pride_gel_param", gel_id), getUserParams("pride_gel_param", gel_id));

        return new Gel(params, gelLink, x_coordinate, y_coordinate, molecular_weight, pi);
    }

    @Override
    public Protein getProteinById(Comparable proteinId, boolean useCache) throws DataAccessException {
        Protein protein = super.getProteinById(proteinId, useCache);
        if (protein == null) {
            logger.debug("Get protein identification {}", proteinId);

            String query = "SELECT identification_id, accession_number, accession_version, score, search_database, database_version, " +
                    "search_engine, sequence_coverage, splice_isoform, threshold, gel_id, x_coordinate, y_coordinate, " +
                    "molecular_weight, pi, identification_id, pi.experiment_id, pi.classname, pi.spectrum_ref FROM pride_identification pi, pride_experiment pe " +
                    "WHERE pe.accession = ? AND pi.experiment_id = pe.experiment_id AND pi.identification_id = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query, getForegroundExperimentAcc(), proteinId);

            for (Map<String, Object> result : results) {
                String accession = (String) result.get("accession_number");
                logger.debug("Getting a identification from database: {}", accession);
                Double seqConverage = (Double) result.get("sequence_coverage");
                double seqConverageVal = seqConverage == 0 ? -1 : seqConverage;
                ParamGroup params = new ParamGroup(getCvParams("pride_identification_param", (Integer) result.get("identification_id")),
                        getUserParams("pride_identification_param", (Integer) result.get("identification_id")));

                List<Peptide> peptides = getPeptideIdentification((Integer) result.get("identification_id"), (Integer) result.get("pi.experiment_id"));
                String className = (String) result.get("classname");
                DBSequence dbSequence = new DBSequence(null, null, null, -1, accession, new SearchDataBase((String)result.get("search_database"), (String)result.get("database_version")), null, (String)result.get("accession_version"), (String)result.get("splice_isoform"));


                if ("uk.ac.ebi.pride.rdbms.ojb.model.core.TwoDimensionalIdentificationBean".equals(className)) {
                    Gel gel = getPeptideGel((Integer)result.get("gel_id"), (Double)result.get("x_coordinate"), (Double)result.get("y_coordinate"), (Double)result.get("molecular_weight"), (Double)result.get("pi"));
                    protein = new Protein(params, (Integer)result.get("identification_id"), null, dbSequence, false, peptides, null, (Double)result.get("threshold"), seqConverageVal, gel);
                } else if ("uk.ac.ebi.pride.rdbms.ojb.model.core.GelFreeIdentificationBean".equals(className)) {
                    protein = new Protein(params, (Integer)result.get("identification_id"), null, dbSequence, false, peptides, null, (Double)result.get("threshold"), seqConverageVal,null);
                }

                if (useCache) {
                    getCache().store(CacheCategory.PROTEIN, proteinId, protein);
                }
            }
        }
        return protein;
    }

    /**
     * ===========================This section is added to overcome the database retrieve speed issue =====================
     */

    @Override
    public boolean isIdentifiedSpectrum(Comparable specId) throws DataAccessException {
        Map<Comparable, Comparable> peptideToSpectrum = (Map<Comparable, Comparable>) getCache().get(CacheCategory.PEPTIDE_TO_SPECTRUM);
        return peptideToSpectrum != null && peptideToSpectrum.containsValue(specId);
    }

    /**
     * ===========================This section is added by rwang 31/08/2010 ===============================================
     */


    @Override
    public Peptide getPeptideByIndex(Comparable proteinId, Comparable peptideId, boolean useCache) throws DataAccessException {
        Peptide peptide = super.getPeptideByIndex(proteinId, peptideId, useCache);
        if (peptide == null) {
            //todo: check whether to use cache
            String sequence = (String) getCache().get(CacheCategory.PEPTIDE_SEQUENCE, peptideId);
            logger.debug("getPeptideById(identId, peptideId): ID[{}] : Sequence[{}]", new Object[]{peptideId, sequence});
            Integer start = (Integer) getCache().get(CacheCategory.PEPTIDE_START, peptideId);
            Integer end = (Integer) getCache().get(CacheCategory.PEPTIDE_END, peptideId);
            int pid = Integer.parseInt(peptideId.toString());
            ParamGroup params = new ParamGroup(getCvParams("pride_peptide_param", pid), getUserParams("pride_peptide_param", pid));
            List<Modification> modifications = this.getPTMs(proteinId, peptideId);
            List<FragmentIon> fragmentIons = getFragmentIons(pid);
            Spectrum spectrum = null;
            Comparable specId = this.getPeptideSpectrumId(proteinId, peptideId);
            if (specId != null) {
                spectrum = getSpectrumById(specId);
            }
            int charge = this.getPrecursorCharge(specId);
            double mz = this.getPrecursorMz(specId);
            PeptideSequence peptideSequence = new PeptideSequence(null, null, sequence, modifications);
            List<PeptideEvidence> peptideEvidences = new ArrayList<PeptideEvidence>();
            PeptideEvidence peptideEvidence = new PeptideEvidence(null, null, start, end, false, peptideSequence, null);
            peptideEvidences.add(peptideEvidence);
            SpectrumIdentification spectrumIdentification = new SpectrumIdentification(params, null, null, charge, mz, 0.0, 0.0, peptideSequence, -1, false, null, null, peptideEvidences, fragmentIons, null, spectrum, null);
            peptide = new Peptide(peptideEvidence,spectrumIdentification);

            if (useCache) {
                getCache().store(CacheCategory.PEPTIDE, new Tuple<Comparable, Comparable>(proteinId, peptideId), peptide);
            }
        }

        return peptide;
    }

    @Override
    public SearchEngine getSearchEngine() throws DataAccessException {
        // check with cache if exists then use the in-memory ident object
        SearchEngine searchEngine = super.getSearchEngine();
        if (searchEngine == null && hasProtein()) {
            String query = "select search_engine from pride_identification where identification_id=?";
            Comparable identId = CollectionUtils.getElement(getProteinIds(), 0);
            String seStr = jdbcTemplate.queryForObject(query, String.class, identId);
            searchEngine = new SearchEngine(null, seStr);

            // get search engine types
            Map<Comparable, ParamGroup> params = (Map<Comparable, ParamGroup>) getCache().get(CacheCategory.PEPTIDE_TO_PARAM);
            if (params != null && !params.isEmpty()) {
                Collection<ParamGroup> paramGroups = params.values();
                ParamGroup paramGroup = CollectionUtils.getElement(paramGroups, 0);
                searchEngine.setSearchEngineTypes(DataAccessUtilities.getSearchEngineTypes(paramGroup));
            }
            getCache().store(CacheCategory.SEARCH_ENGINE_TYPE, searchEngine);
        }

        return searchEngine;
    }

    @Override
    public String getProteinType(Comparable proteinId) throws DataAccessException {
        String query = "select classname from pride_identification where identification_id=?";
        String className = jdbcTemplate.queryForObject(query, String.class, proteinId);

        return className.contains("TwoDimensionalIdentificationBean") ? TWO_DIM_PROTEIN_IDENTIFICATION_TYPE : GEL_FREE_PROTEIN_IDENTIFICATION_TYPE;
    }

    @Override
    public int getNumberOfPeaks(Comparable specId) throws DataAccessException {
        // check with cache if exists then use the in-memory spectrum object
        int cnt = 0;
        Integer num = (Integer) getCache().get(CacheCategory.NUMBER_OF_PEAKS, specId);
        if (num == null) {
            Spectrum spectrum = (Spectrum) getCache().get(CacheCategory.SPECTRUM, specId);
            if (spectrum != null) {
                cnt = DataAccessUtilities.getNumberOfPeaks(spectrum);
            } else {
                logger.debug("Getting number of peaks");
                String query = "select mz_array_binary_id from mzdata_spectrum where spectrum_id=?";
                Integer mzArrIdObj = jdbcTemplate.queryForObject(query, Integer.class, specId);
                int mzArrId = mzArrIdObj == null ? -1 : mzArrIdObj;

                // get binary data array
                if (mzArrId != -1) {
                    BinaryDataArray mzBinaryArray = null;
                    try {
                        mzBinaryArray = getBinaryDataArray(mzArrId, CvTermReference.MZ_ARRAY);
                        cnt = mzBinaryArray.getDoubleArray().length;
                        getCache().store(CacheCategory.NUMBER_OF_PEAKS, specId, cnt);
                    } catch (UnsupportedEncodingException e) {
                        String errMsg = "Failed to query number of peaks during decoding of the binary data array";
                        logger.error(errMsg, e);
                        throw new DataAccessException(errMsg, e);
                    }

                }
            }
        } else {
            cnt = num;
        }

        return cnt;
    }

    @Override
    public double getSumOfIntensity(Comparable specId) throws DataAccessException {
        double sum = 0;
        Double sumOfIntent = (Double) getCache().get(CacheCategory.SUM_OF_INTENSITY, specId);
        if (sumOfIntent == null) {
            Spectrum spectrum = (Spectrum) getCache().get(CacheCategory.SPECTRUM, specId);
            if (spectrum != null) {
                sum = DataAccessUtilities.getSumOfIntensity(spectrum);
            } else {
                logger.debug("Getting sum of intensity: spectrum id[{}]", specId);
                String query = "select inten_array_binary_id from mzdata_spectrum where spectrum_id=?";
                Integer mzArrIdObj = jdbcTemplate.queryForObject(query, Integer.class, specId);
                int mzArrId = mzArrIdObj == null ? -1 : mzArrIdObj;

                // get binary data array
                if (mzArrId != -1) {
                    try {
                        BinaryDataArray intentBinaryArray = getBinaryDataArray(mzArrId, CvTermReference.MZ_ARRAY);
                        if (intentBinaryArray != null) {
                            double[] originalIntentArr = intentBinaryArray.getDoubleArray();
                            for (double intent : originalIntentArr) {
                                sum += intent;
                            }
                            getCache().store(CacheCategory.SUM_OF_INTENSITY, specId, sum);
                            int numOfPeaks = intentBinaryArray.getDoubleArray().length;
                            if (numOfPeaks == 1 && intentBinaryArray.getDoubleArray()[0] == 0) {
                                numOfPeaks = 0;
                            }

                            getCache().store(CacheCategory.NUMBER_OF_PEAKS, specId, numOfPeaks);
                        }
                    } catch (UnsupportedEncodingException e) {
                        String errMsg = "Failed to query sum of intensity while decoding the binary data array";
                        logger.error(errMsg, e);
                        throw new DataAccessException(errMsg, e);
                    }
                }
            }
        } else {
            sum = sumOfIntent;
        }

        return sum;
    }

    @Override
    public Quantitation getProteinQuantData(Comparable proteinId) throws DataAccessException {
        ParamGroup paramGroup = (ParamGroup) getCache().get(CacheCategory.PROTEIN_TO_PARAM, proteinId);

        if (paramGroup != null) {
            return new Quantitation(Quantitation.Type.PROTEIN, paramGroup.getCvParams());
        }

        return null;
    }

    @Override
    public Quantitation getPeptideQuantData(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        ParamGroup paramGroup = (ParamGroup) getCache().get(CacheCategory.PEPTIDE_TO_PARAM, peptideId);

        if (paramGroup != null) {
            return new Quantitation(Quantitation.Type.PEPTIDE, paramGroup.getCvParams());
        }

        return null;
    }

    @Override
    public List<PrideChartManager> getChartData() throws DataAccessException {
        List<PrideChartManager> list = new ArrayList<PrideChartManager>();

        String query = "select c.chart_type, c.intermediate_data " +
                "from pride_chart_data c, pride_experiment e " +
                "where c.experiment_id = e.experiment_id and " +
                "   e.accession = ? and " +
                "   c.intermediate_data is not null " +
                "order by c.chart_type";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, getForegroundExperimentAcc());
        Map<Integer, PrideChartManager> map = new HashMap<Integer, PrideChartManager>();
        for (Map<String, Object> result : results) {
            int type = ((Long) result.get("chart_type")).intValue();
            String jsonData = (String) result.get("intermediate_data");

            PrideChart prideChart = PrideChartFactory.getChart(type, jsonData);
            map.put(type, new PrideChartManager(prideChart));
        }

        if (!map.isEmpty())
            for (Integer id : PrideChartFactory.getChartOrder())
                if (map.containsKey(id)) list.add(map.get(id));


        //If the list is empty means that the intermediate data has not been retrieved for some reason
        Connection connection = null;
        if (list.isEmpty()) {
            try {
                /**
                 * The next PrideChartSummaryData object if from PRIDE-Chart package
                 * uk.ac.ebi.pride.chart.controller.PrideChartSummaryData
                 * and is used for retrieving the summary data from the experiment
                 * instead of using the intermediate data (because it could not be retrieved
                 * in the above step)
                 */
                String accession = getForegroundExperimentAcc().toString();
                connection = PooledConnectionFactory.getConnection();
                PrideChartSummaryData summaryData = new PrideChartSummaryData(accession, connection);
                for (PrideChart prideChart : PrideChartFactory.getAllCharts(summaryData)) {
                    list.add(new PrideChartManager(prideChart));
                }
            } catch (Exception e) {
                String errMsg = "Charts summary data could not be retrieved from database";
                logger.error(errMsg, e);
                throw new DataAccessException(errMsg, e);
            } finally {
                //ToDo: what to use instead of st and rs (because they are from previous try)
                DBUtilities.releaseResources(connection, null, null);
            }
        }

        return list;
    }


}


