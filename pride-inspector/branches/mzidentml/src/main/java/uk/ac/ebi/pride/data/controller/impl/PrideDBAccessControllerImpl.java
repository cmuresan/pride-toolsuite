package uk.ac.ebi.pride.data.controller.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.list.SetUniqueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.controller.PrideChartSummaryData;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.cache.impl.PrideDBCacheBuilder;
import uk.ac.ebi.pride.data.coreIdent.*;
import uk.ac.ebi.pride.data.io.db.DBUtilities;
import uk.ac.ebi.pride.data.io.db.PooledConnectionFactory;
import uk.ac.ebi.pride.data.utils.BinaryDataUtils;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.data.utils.MD5Utils;
import uk.ac.ebi.pride.gui.component.chart.PrideChartManager;
import uk.ac.ebi.pride.gui.utils.Constants;
import uk.ac.ebi.pride.model.interfaces.mzdata.Param;
import uk.ac.ebi.pride.term.CvTermReference;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private final static String CONTACT_INFO = "contact information";
    private final static String SAMPLE_ID = "sample1";
    private final static String COMMENTS = "comments";
    private final static String COMPLETION_TIME = "completion time";
    private final static int PROCESSING_METHOD_ORDER = 1;
    private final static String DATA_PROCESSING_ID = "dataprocessing1";
    private final static String PROTOCOL_ID = "protocol1";

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

    private List<CvParam> getCvParams(Connection connection, String table_name, int parent_element_id) throws DataAccessException {
        List<CvParam> cvParams = new ArrayList<CvParam>();
        boolean newConnection = false;

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.debug("Getting a connection for Cv Params");
                connection = PooledConnectionFactory.getConnection();
                newConnection = true;
            }
            st = connection.prepareStatement("SELECT accession, name, value, cv_label FROM " + table_name
                    + " WHERE parent_element_fk = ? AND cv_label is not null");
            st.setInt(1, parent_element_id);
            rs = st.executeQuery();
            while (rs.next()) {
                cvParams.add(new CvParam(rs.getString("accession"), rs.getString("name"),
                        rs.getString("cv_label"), rs.getString("value"), "", "", ""));
            }
        } catch (SQLException e) {
            String errMsg = "Failed to query cv params from " + table_name;
            logger.error(errMsg, e);
            throw new DataAccessException(errMsg, e);
        } finally {
            DBUtilities.releaseResources(newConnection ? connection : null, st, rs);
        }
        return cvParams;
    }

    //same as before, but returns a list of UserParam

    private List<UserParam> getUserParams(Connection connection, String table_name, int parent_element_id) {
        List<UserParam> userParams = new ArrayList<UserParam>();
        boolean newConnection = false;

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.debug("Getting a connection for User Params");
                connection = PooledConnectionFactory.getConnection();
                newConnection = true;
            }
            st = connection.prepareStatement("SELECT name, value FROM " + table_name +
                    " WHERE parent_element_fk = ? AND cv_label is null");
            st.setInt(1, parent_element_id);
            rs = st.executeQuery();
            while (rs.next()) {
                userParams.add(new UserParam(rs.getString("name"), "", rs.getString("value"), "", "", ""));
            }
        } catch (SQLException e) {
            logger.error("Failed to query user params from {}", table_name, e);
        } finally {
            DBUtilities.releaseResources(newConnection ? connection : null, st, rs);
        }

        return userParams;
    }

    private List<SourceFile> getSourceFiles(Connection connection) {
        List<SourceFile> sourceFiles = new ArrayList<SourceFile>();

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement("SELECT sf.name_of_file, sf.path_to_file FROM mzdata_source_file sf, mzdata_mz_data mz " +
                    "WHERE mz.accession_number= ? and mz.source_file_id=sf.source_file_id");
            st.setString(1, foregroundExperimentAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                //there should be a single source file per spectrum
                SourceFile sourceFile = new SourceFile(null, rs.getString("name_of_file"), rs.getString("path_to_file"), null);
                sourceFiles.add(sourceFile);
            }
        } catch (SQLException e) {
            logger.error("Failed to query source files", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return sourceFiles;
    }


    private List<ParamGroup> getContacts(Connection connection) {
        List<ParamGroup> contacts = new ArrayList<ParamGroup>();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT contact_name, institution, contact_info FROM mzdata_contact sf, mzdata_mz_data mz " +
                    "WHERE mz.accession_number= ? and mz.mz_data_id=sf.mz_data_id");
            st.setString(1, foregroundExperimentAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                //there should be a single source file per spectrum
                List<CvParam> cvParams = new ArrayList<CvParam>();
                CvTermReference contactName = CvTermReference.CONTACT_NAME;
                cvParams.add(new CvParam(contactName.getAccession(), contactName.getName(), contactName.getCvLabel(), rs.getString("contact_name"), null, null, null));
                CvTermReference contactOrg = CvTermReference.CONTACT_ORG;
                cvParams.add(new CvParam(contactOrg.getAccession(), contactOrg.getName(), contactOrg.getCvLabel(), rs.getString("institution"), null, null, null));
                //ToDo: extract email, address information into CvParams?
                List<UserParam> userParams = null;
                String contactInfo = rs.getString("contact_info");
                if (contactInfo != null) {
                    userParams = new ArrayList<UserParam>();
                    userParams.add(new UserParam(CONTACT_INFO, null, contactInfo, null, null, null));
                }
                contacts.add(new ParamGroup(cvParams, userParams));
            }
        } catch (SQLException e) {
            logger.error("Failed to query contacts", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }
        return contacts;
    }

    @Override
    public List<Person> getPersonContacts() throws DataAccessException {
        ExperimentMetaData metadata = super.getExperimentMetaData();
        if(metadata == null){
            List<Person> persons = new ArrayList<Person>();
            Connection connection = null;
            PreparedStatement st = null;
            ResultSet rs = null;
            try {
                logger.debug("Getting Person Contacts");
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT contact_name, institution, contact_info FROM mzdata_contact sf, mzdata_mz_data mz " + "WHERE mz.accession_number= ? and mz.mz_data_id=sf.mz_data_id");
                st.setString(1, foregroundExperimentAcc.toString());
                rs = st.executeQuery();
                while (rs.next()){
                    List<CvParam> cvParams = new ArrayList<CvParam>();
                    CvTermReference contactName = CvTermReference.CONTACT_NAME;
                    cvParams.add(new CvParam(contactName.getAccession(), contactName.getName(), contactName.getCvLabel(), rs.getString("contact_name"), null, null, null));
                    CvTermReference contactOrg = CvTermReference.CONTACT_ORG;
                    cvParams.add(new CvParam(contactOrg.getAccession(), contactOrg.getName(), contactOrg.getCvLabel(), rs.getString("institution"), null, null, null));
                    //ToDo: extract email, address information into CvParams?
                    List<UserParam> userParams = null;
                    String contactInfo = rs.getString("contact_info");
                    if (contactInfo != null) {
                        userParams = new ArrayList<UserParam>();
                        userParams.add(new UserParam(CONTACT_INFO, null, contactInfo, null, null, null));
                    }
                    List<Organization> affiliation = new ArrayList<Organization>();
                    affiliation.add(new Organization(null,rs.getString("institution"),null,null));
                    Person contactPerson = new Person(new ParamGroup(cvParams, userParams),null,rs.getString("contact_name"),null,null,null,affiliation,null);
                    persons.add(contactPerson);
                }
                metadata.setPersonList(persons);
            } catch (SQLException e) {
                logger.error("Failed to query contacts", e);
            } finally {
                DBUtilities.releaseResources(null, st, rs);
            }
        }
        return metadata.getPersonList();
    }

    @Override
    public List<Organization> getOrganizationContacts() throws DataAccessException {
        ExperimentMetaData metadata = super.getExperimentMetaData();
        if(metadata == null){
            Set<Organization> organizations = new HashSet<Organization>();
            Connection connection = null;
            PreparedStatement st = null;
            ResultSet rs = null;
            try {
                logger.debug("Getting Organization Contacts");
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT contact_name, institution, contact_info FROM mzdata_contact sf, mzdata_mz_data mz " + "WHERE mz.accession_number= ? and mz.mz_data_id=sf.mz_data_id");
                st.setString(1, foregroundExperimentAcc.toString());
                rs = st.executeQuery();
                while (rs.next()){
                    List<CvParam> cvParams = new ArrayList<CvParam>();
                    CvTermReference contactOrg = CvTermReference.CONTACT_ORG;
                    cvParams.add(new CvParam(contactOrg.getAccession(), contactOrg.getName(), contactOrg.getCvLabel(), rs.getString("institution"), null, null, null));
                    //ToDo: extract email, address information into CvParams?
                    List<UserParam> userParams = null;
                    Organization organization = new Organization(new ParamGroup(cvParams, userParams),rs.getString("institution"),null);
                    organizations.add(organization);
                }
                metadata.setOrganizationList(new ArrayList<Organization>(organizations));
            } catch (SQLException e) {
                logger.error("Failed to query contacts", e);
            } finally {
                DBUtilities.releaseResources(null, st, rs);
            }
        }
        return metadata.getOrganizationList();
    }

    @Override
    public List<Sample> getSamples() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            List<Sample> samples = new ArrayList<Sample>();

            List<CvParam> cvParam;
            List<UserParam> userParam;

            Connection connection = null;
            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                logger.debug("Getting samples");
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT mz_data_id, sample_name FROM mzdata_mz_data mz WHERE mz.accession_number= ?");
                st.setString(1, foregroundExperimentAcc.toString());
                rs = st.executeQuery();
                while (rs.next()) {
                    int mz_data_id = rs.getInt("mz_data_id");
                    String sample_name = rs.getString("sample_name");
                    userParam = getUserParams(connection, "mzdata_sample_param", mz_data_id);
                    cvParam = getCvParams(connection, "mzdata_sample_param", mz_data_id);
                    samples.add(new Sample(new ParamGroup(cvParam, userParam),SAMPLE_ID, sample_name));
                }
            } catch (SQLException e) {
                logger.error("Failed to query samples", e);
            } finally {
                DBUtilities.releaseResources(connection, st, rs);
            }
            return samples;
        } else {
            return metaData.getSampleList();
        }
    }

    @Override
    public List<Software> getSoftwareList() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            List<Software> softwares = new ArrayList<Software>();

            Connection connection = null;
            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                logger.debug("Getting software");
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT software_name, software_version, software_completion_time, software_comments FROM mzdata_mz_data mz WHERE mz.accession_number= ?");
                st.setString(1, foregroundExperimentAcc.toString());
                rs = st.executeQuery();
                while (rs.next()) {
                    List<CvParam> cvParams = new ArrayList<CvParam>();
                    //ToDo: semantic support, need to add a child term of MS:1000531 (software)
                    List<UserParam> userParams = new ArrayList<UserParam>();
                    userParams.add(new UserParam(COMMENTS, null, rs.getString("software_comments"), null, null, null));
                    String completionTime = rs.getString("software_completion_time");
                    if (completionTime != null) {
                        userParams.add(new UserParam(COMPLETION_TIME, null, completionTime, null, null, null));
                    }
                    softwares.add(new Software(new ParamGroup(cvParams, userParams),null,rs.getString("software_name"), rs.getString("software_version")));
                }

            } catch (SQLException e) {
                logger.error("Failed to query software", e);
            } finally {
                DBUtilities.releaseResources(connection, st, rs);
            }
            metaData.setSoftwareList(softwares);
        }

        return metaData.getSoftwareList();

    }

    private List<ParamGroup> getAnalyzerList(Connection connection, int mz_data_id) throws DataAccessException {

        List<ParamGroup> analyzerList = new ArrayList<ParamGroup>();
        List<UserParam> userParams;
        List<CvParam> cvParams;
        ParamGroup params;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT analyzer_id FROM mzdata_analyzer WHERE mz_data_id = ?");
            st.setInt(1, mz_data_id);
            rs = st.executeQuery();
            while (rs.next()) {
                userParams = getUserParams(connection, "mzdata_analyzer_param", rs.getInt("analyzer_id"));
                cvParams = getCvParams(connection, "mzdata_analyzer_param", rs.getInt("analyzer_id"));
                params = new ParamGroup(cvParams, userParams);
                analyzerList.add(params);

            }
        } catch (SQLException e) {
            logger.error("Failed to query param groups", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return analyzerList;
    }

    @Override
    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

        if (metaData == null) {
            List<InstrumentConfiguration> instrumentConfigurations = new ArrayList<InstrumentConfiguration>();
            //get software
            Software software = null;
            if (getSoftwareList().size() > 0) {
                software = getSoftwareList().get(0);
            }

            Connection connection = null;
            PreparedStatement st = null;
            ResultSet rs = null;
            try {
                logger.debug("Getting instrument");
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT instrument_name, mz_data_id FROM mzdata_mz_data mz WHERE mz.accession_number= ?");
                st.setString(1, foregroundExperimentAcc.toString());
                rs = st.executeQuery();
                while (rs.next()) {
                    int mz_data_id = rs.getInt("mz_data_id");
                    //instrument params
                    // ParamGroup params = new ParamGroup(getCvParams("mzdata_instrument_param", mz_data_id), getUserParams("mzdata_instrument_param", mz_data_id));
                    ParamGroup params = new ParamGroup();
                    CvTermReference cvTerm = CvTermReference.INSTRUMENT_MODEL;
                    params.addCvParam(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                            rs.getString("instrument_name"), null, null, null));
                    // create instrument components
                    //create source
                    //create source object
                    int sourceOrder = 1;
                    InstrumentComponent sourceInstrument = new InstrumentComponent(sourceOrder, new ParamGroup(getCvParams(connection, "mzdata_instrument_source_param", mz_data_id), getUserParams(connection, "mzdata_instrument_source_param", mz_data_id)));
                    List<InstrumentComponent> sources  = new ArrayList<InstrumentComponent>();
                    sources.add(sourceInstrument);
                    //create detector
                    int detectorOrder = getAnalyzerList(connection, mz_data_id).size() + 2;
                    InstrumentComponent detectorInstrument = new InstrumentComponent(detectorOrder, new ParamGroup(getCvParams(connection, "mzdata_instrument_detector_param", mz_data_id), getUserParams(connection, "mzdata_instrument_detector_param", mz_data_id)));
                    //create analyzer
                    List<InstrumentComponent> detectors = new ArrayList<InstrumentComponent>();
                    detectors.add(detectorInstrument);
                    List<ParamGroup> paramGroupAnalyzers = getAnalyzerList(connection, mz_data_id);
                    List<InstrumentComponent> analyzers = new ArrayList<InstrumentComponent>();
                    int orderCnt = 2;
                    for (ParamGroup analyzer : analyzers) {
                        InstrumentComponent analyzerInstrument = new InstrumentComponent(orderCnt, analyzer);
                         analyzers.add(analyzerInstrument);
                    }
                    instrumentConfigurations.add(new InstrumentConfiguration(rs.getString("instrument_name"), null, software, sources, analyzers, detectors,params));
                }
            } catch (SQLException e) {
                logger.error("Failed to query instrument configuration", e);
            } finally {
                DBUtilities.releaseResources(connection, st, rs);
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
            List<CvParam> cvParams;
            List<UserParam> userParams;
            List<ProcessingMethod> procMethods = new ArrayList<ProcessingMethod>();
            List<DataProcessing> dataProcessings = new ArrayList<DataProcessing>();
            Software software = null;
            if (getSoftwareList().size() > 0) {
                software = getSoftwareList().get(0);
            }

            Connection connection = null;
            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                logger.debug("Getting data processings");
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT mz_data_id FROM mzdata_mz_data mz WHERE mz.accession_number= ?");
                st.setString(1, foregroundExperimentAcc.toString());
                rs = st.executeQuery();
                while (rs.next()) {
                    int mz_data_id = rs.getInt("mz_data_id");
                    userParams = getUserParams(connection, "mzdata_processing_method_param", mz_data_id);
                    cvParams = getCvParams(connection, "mzdata_processing_method_param", mz_data_id);
                    ParamGroup params = new ParamGroup(cvParams, userParams);
//                CvTermReference cvTerm = CvTermReference.CONVERSION_TO_MZML;
//                params.addCvParam(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(), null, null, null, null));
                    procMethods.add(new ProcessingMethod(PROCESSING_METHOD_ORDER, software, params));
                    dataProcessings.add(new DataProcessing(DATA_PROCESSING_ID, procMethods));
                }
            } catch (SQLException e) {
                logger.error("Failed to query data processings", e);
            } finally {
                DBUtilities.releaseResources(connection, st, rs);
            }
            metaData.setDataProcessingList(dataProcessings);
        }
        return metaData.getDataProcessingList();

    }

    //for a given experimentId, will return the protocol_steps_id sorted by index

    private List<Integer> getProtocolStepsById(Connection connection, int experimentId) {
        List<Integer> protocol_steps = new ArrayList<Integer>();

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT protocol_step_id FROM pride_protocol_step pp, pride_experiment pe WHERE " +
                    "pe.experiment_id = pp.experiment_id AND pe.accession = ? ORDER BY protocol_step_index");
            st.setInt(1, experimentId);
            rs = st.executeQuery();
            while (rs.next()) {
                protocol_steps.add(rs.getInt("protocol_step_id"));
            }
        } catch (SQLException e) {
            logger.error("Failed to query protocol steps", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return protocol_steps;
    }

    private ExperimentProtocol getProtocol(Connection connection) throws DataAccessException {

        List<Integer> protocol_steps;
        List<UserParam> userParams;
        List<CvParam> cvParams;
        List<ParamGroup> paramGroup = new ArrayList<ParamGroup>();
        String protocol_name = null;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT protocol_name FROM pride_experiment WHERE accession= ?");
            st.setString(1, foregroundExperimentAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                protocol_name = rs.getString("protocol_name");
            }
        } catch (SQLException e) {
            logger.error("Failed to query protocol", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        protocol_steps = getProtocolStepsById(connection, Integer.parseInt(foregroundExperimentAcc.toString()));

        //for each protocol_step, get the paramGroup
        for (int protocol_step_id : protocol_steps) {
            cvParams = getCvParams(connection, "pride_protocol_param", protocol_step_id);
            userParams = getUserParams(connection, "pride_protocol_param", protocol_step_id);
            paramGroup.add(new ParamGroup(cvParams, userParams));
        }

        return new ExperimentProtocol(null,PROTOCOL_ID, protocol_name, paramGroup);
    }

    private List<Reference> getReferences(Connection connection) throws DataAccessException {

        List<Reference> references = new ArrayList<Reference>();
        List<UserParam> userParams;
        List<CvParam> cvParams;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT reference_line, pr.reference_id FROM pride_experiment pe, pride_reference pr, pride_reference_exp_link pl WHERE " +
                    "pe.accession = ? AND pl.reference_id = pr.reference_id AND pl.experiment_id = pe.experiment_id");
            st.setInt(1, Integer.parseInt(foregroundExperimentAcc.toString()));
            rs = st.executeQuery();
            while (rs.next()) {
                userParams = getUserParams(connection, "pride_reference_param", rs.getInt("pr.reference_id"));
                cvParams = getCvParams(connection, "pride_reference_param", rs.getInt("pr.reference_id"));
                references.add(new Reference( new ParamGroup(cvParams, userParams),rs.getString("reference_line")));
            }

        } catch (SQLException e) {
            logger.error("Failed to query references", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return references;
    }

    @Override
    public ParamGroup getAdditional() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            ParamGroup additional = null;
            List<CvParam> cvParam;
            List<UserParam> userParam;

            PreparedStatement st = null;
            ResultSet rs = null;
            Connection connection = null;

            try {
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT experiment_id FROM pride_experiment WHERE accession= ?");
                st.setString(1, foregroundExperimentAcc.toString());
                rs = st.executeQuery();
                while (rs.next()) {
                    int experiment_id = rs.getInt("experiment_id");
                    userParam = getUserParams(connection, "pride_experiment_param", experiment_id);
                    cvParam = getCvParams(connection, "pride_experiment_param", experiment_id);
                    additional = new ParamGroup(cvParam, userParam);
                }
            } catch (SQLException e) {
                logger.error("Failed to query additional params", e);
            } finally {
                DBUtilities.releaseResources(connection, st, rs);
            }
            return additional;
        } else {
            return metaData.getAdditional();
        }
    }

    @Override
    public ExperimentMetaData getExperimentMetaData() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();
        if (metaData == null) {
            String accession = "";
            String version = "2.1";
            String title = "";
            String shortLabel = "";

            Connection connection = null;
            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                logger.debug("Getting meta data");
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT pe.title, pe.accession, pe.short_label FROM pride_experiment pe WHERE pe.accession = ?");
                st.setString(1, foregroundExperimentAcc.toString());
                rs = st.executeQuery();
                while (rs.next()) {
                    accession = rs.getString("pe.accession");
                    title = rs.getString("pe.title");
                    shortLabel = rs.getString("pe.short_label");
                }

                List<Sample> samples = getSamples();
                List<Software> software = getSoftwareList();
                //List<InstrumentConfiguration> instrumentConfigurations = getInstrumentConfigurations();
                //List<DataProcessing> dataProcessings = getDataProcessings();
                ParamGroup additional = getAdditional();
                ExperimentProtocol protocol = getProtocol(connection);
                List<SourceFile> sourceFiles = getSourceFiles(connection);
                List<Person> persons = getPersonContacts();
                List<Organization> organizations = getOrganizationContacts();
                List<Reference> references = getReferences(connection);
                metaData = new ExperimentMetaData(additional,accession,null,version,shortLabel,samples,software,persons,sourceFiles,null,organizations,references,null,null,protocol);
                // store in cache
                cache.store(CacheCategory.EXPERIMENT_METADATA, metaData);
            } catch (SQLException e) {
                logger.error("Failed to query experiment metadata", this, e);
            } finally {
                DBUtilities.releaseResources(connection, st, rs);
            }
        }

        return metaData;
    }

    @Override
    public List<CVLookup> getCvLookups() throws DataAccessException {
        List<CVLookup> cvLookups = new ArrayList<CVLookup>();

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            logger.debug("Getting cv lookups");
            connection = PooledConnectionFactory.getConnection();
            st = connection.prepareStatement("SELECT cv_label, version, address, full_name FROM mzdata_cv_lookup sf, mzdata_mz_data mz WHERE mz.accession_number= ? and mz.mzdata_id=sf.mzdata_id");
            st.setString(1, foregroundExperimentAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                CVLookup cvLookup = new CVLookup(rs.getString("cv_label"), rs.getString("full_name"), rs.getString("version"), rs.getString("address"));
                cvLookups.add(cvLookup);
            }
        } catch (SQLException e) {
            logger.error("Failed to query cvlookups", e);
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }

        return cvLookups;
    }

    private BinaryDataArray getBinaryDataArray(Connection connection, int array_binary_id, CvTermReference binaryType) throws UnsupportedEncodingException {
        BinaryDataArray binaryDataArray = null;
        CvTermReference dataType = null;
        ByteOrder order = null;
        String total_array = null;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT data_precision, data_endian FROM mzdata_binary_array WHERE binary_array_id = ?");
            st.setInt(1, array_binary_id);
            rs = st.executeQuery();
            //first get precision and order of the binary data
            while (rs.next()) {
                dataType = "32".equals(rs.getString("data_precision")) ? CvTermReference.FLOAT_32_BIT : CvTermReference.FLOAT_64_BIT;
                order = "big".equals(rs.getString("data_endian")) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
            }
            st.close();
            rs.close();

            st = connection.prepareStatement("SELECT base_64_data FROM mzdata_base_64_data WHERE binary_array_id = ?");
            st.setInt(1, array_binary_id);
            rs = st.executeQuery();
            //then get the binary string
            while (rs.next()) {
                total_array = rs.getString("base_64_data");
            }

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


            binaryDataArray = new BinaryDataArray(null, binaryDoubleArr, params);
        } catch (SQLException e) {
            logger.error("Failed to query binary data array", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return binaryDataArray;
    }

    private List<ParamGroup> getScanWindows(BigDecimal mz_range_start, BigDecimal mz_range_stop) {
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

    private List<Scan> getScanList(Connection connection, int acq_specification_id, List<ParamGroup> scanWindows) throws DataAccessException {
        List<Scan> scanList = new ArrayList<Scan>();
        ParamGroup params = null;
        List<CvParam> cvParams;
        List<UserParam> userParams;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT acquisition_id FROM mzdata_acquisition WHERE acq_specification_id = ?");
            st.setInt(1, acq_specification_id);
            rs = st.executeQuery();
            while (rs.next()) {
                cvParams = getCvParams(connection, "mzdata_acquisition_param", rs.getInt("acquisition_id"));
                userParams = getUserParams(connection, "mzdata_acquisition_param", rs.getInt("acquisition_id"));
                params = new ParamGroup(cvParams, userParams);
            }

            scanList.add(new Scan(null, null, null, null, scanWindows, params));
        } catch (SQLException e) {
            logger.error("Failed to query scan list", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return scanList;
    }

    private List<Precursor> getPrecursorsBySpectrum_id(Connection connection, int spectrum_id) throws DataAccessException {
        List<Precursor> precursors = new ArrayList<Precursor>();
        List<ParamGroup> selectedIon = new ArrayList<ParamGroup>();
        Spectrum spectrum;
        List<CvParam> cvParams;
        List<UserParam> userParams;
        ParamGroup activation;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT precursor_id, precursor_spectrum_id FROM mzdata_precursor WHERE spectrum_id = ?");
            st.setInt(1, spectrum_id);
            rs = st.executeQuery();
            while (rs.next()) {
                cvParams = getCvParams(connection, "mzdata_activation_param", rs.getInt("precursor_id"));
                userParams = getUserParams(connection, "mzdata_activation_param", rs.getInt("precursor_id"));
                spectrum = getSpectrumById(Integer.toString(rs.getInt("precursor_spectrum_id")));
                activation = new ParamGroup(cvParams, userParams);
                cvParams = getCvParams(connection, "mzdata_ion_selection_param", rs.getInt("precursor_id"));
                userParams = getUserParams(connection, "mzdata_ion_selection_param", rs.getInt("precursor_id"));
                selectedIon.add(new ParamGroup(cvParams, userParams));
                precursors.add(new Precursor(spectrum, null, null, null, selectedIon, activation));
            }
        } catch (SQLException e) {
            logger.error("Failed to query precursors", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
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

    private List<UserParam> getSpectrumDesc(Connection connection, int spectrum_id) {
        List<UserParam> userParams = new ArrayList<UserParam>();

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT comment_text FROM mzdata_spectrum_desc_comment WHERE spectrum_id = ?");
            st.setInt(1, spectrum_id);
            rs = st.executeQuery();
            while (rs.next()) {
                userParams.add(new UserParam(COMMENTS, null, rs.getString("comment_text"), null, null, null));
            }

        } catch (SQLException e) {
            logger.error("Failed to query spectrum descritpion", this, e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
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
    public Spectrum getSpectrumById(Comparable id, boolean useCache) throws DataAccessException {
        logger.debug("Getting a spectrum: {}", id);
        Spectrum spectrum = super.getSpectrumById(id, useCache);

        if (spectrum == null) {
            ScanList scanList;
            List<Scan> scans = new ArrayList<Scan>();
            List<Precursor> precursors;
            ParamGroup spectrumParams = new ParamGroup();
            ParamGroup scanParams = new ParamGroup();
            List<BinaryDataArray> binaryArray = new ArrayList<BinaryDataArray>();
            int defaultArrLength;

            if (id == null) {
                return spectrum;
            }

            Connection connection = null;
            PreparedStatement st = null;
            ResultSet rs = null;
            //first get spectrum information from mzdata_spectrum
            try {
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT spectrum_id, mz_data_id, mz_range_start, " +
                        "mz_range_stop, ms_level, mz_array_binary_id, inten_array_binary_id, spectrum_type, method_of_combination, ms.acq_specification_id " +
                        "FROM mzdata_spectrum ms LEFT JOIN mzdata_acq_specification ma ON ms.acq_specification_id = ma.acq_specification_id WHERE spectrum_id = ?");
                int value = Integer.parseInt(id.toString());
                st.setInt(1, value);
                rs = st.executeQuery();
                while (rs.next()) {
                    int index = CollectionUtils.getIndex(getSpectrumIds(), id);

                    //do scanlist
                    List<ParamGroup> scanWindows = getScanWindows(rs.getBigDecimal("mz_range_start"), rs.getBigDecimal("mz_range_stop"));
                    if (rs.getInt("ms.acq_specification_id") != 0) {
                        // add method of combination
                        scanParams.addCvParam(getMethodOfCombination(rs.getString("method_of_combination")));
                        scans.addAll(getScanList(connection, rs.getInt("ms.acq_specification_id"), scanWindows));

                    } else {
                        // add method of combination cv param to param group
                        scanParams.addCvParam(getMethodOfCombination(null));
                        // create a Scan
                        Scan scan = new Scan(null, null, null, null, scanWindows, null);
                        scans.add(scan);
                    }
                    // assemble scan list object
                    scanList = new ScanList(scans, scanParams);

                    precursors = getPrecursorsBySpectrum_id(connection, value);

                    BinaryDataArray mz = getBinaryDataArray(connection, rs.getInt("mz_array_binary_id"), CvTermReference.MZ_ARRAY);
                    BinaryDataArray inten = getBinaryDataArray(connection, rs.getInt("inten_array_binary_id"), CvTermReference.INTENSITY_ARRAY);
                    binaryArray.add(mz);
                    binaryArray.add(inten);
                    defaultArrLength = mz.getDoubleArray().length;
                    //additional params
                    // add ms level
                    CvTermReference msLevelCv = CvTermReference.MS_LEVEL;
                    spectrumParams.addCvParam(new CvParam(msLevelCv.getAccession(), msLevelCv.getName(), msLevelCv.getCvLabel(),
                            rs.getInt("ms_level") + "", null, null, null));
                    // add spectrum type
                    CvTermReference massSpecCv = CvTermReference.MASS_SPECTRUM;
                    spectrumParams.addCvParam(new CvParam(massSpecCv.getAccession(), massSpecCv.getName(), massSpecCv.getCvLabel(),
                            null, null, null, null));
                    if (rs.getString("spectrum_type") != null) {
                        spectrumParams.addCvParam(getSpectrumType(rs.getString("spectrum_type")));
                    }
                    // add spectrum instrument
                    spectrumParams.addCvParams(getCvParams(connection, "mzdata_spectrum_instrument_param", value));
                    spectrumParams.addUserParams(getUserParams(connection, "mzdata_spectrum_instrument_param", value));
                    // add comments
                    spectrumParams.addUserParams(getSpectrumDesc(connection, value));
                    spectrum = new Spectrum(spectrumParams,rs.getInt("spectrum_id"),null,index,null,defaultArrLength,binaryArray,null,null,scanList,precursors,null);
                    //spectrum = new Spectrum(Integer.toString(rs.getInt("spectrum_id")), index, null, null, defaultArrLength, null, scanList, precursors, null, binaryArray, spectrumParams);

                    if (useCache) {
                        cache.store(CacheCategory.SPECTRUM, id, spectrum);
                    }
                }
            } catch (SQLException e) {
                logger.error("Failed to query spectrum", e);
            } catch (UnsupportedEncodingException e) {
                logger.error("Failed to decode binary data array", e);
            } finally {
                DBUtilities.releaseResources(connection, st, rs);
            }
        }
        return spectrum;
    }

    private List<Double> getDeltaValues(Connection connection, int modification_id, String deltaType) {
        List<Double> deltas = new ArrayList<Double>();

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT mass_delta_value FROM pride_mass_delta WHERE modification_id = ? AND classname = ?");
            st.setInt(1, modification_id);
            st.setString(2, deltaType);
            rs = st.executeQuery();
            while (rs.next()) {
                deltas.add(rs.getDouble("mass_delta_value"));
            }
        } catch (SQLException e) {
            logger.error("Failed to query delta values", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return deltas;
    }

    private List<Modification> getModificationsPeptide(Connection connection, int peptide_id) throws DataAccessException {
        List<Modification> modifications = new ArrayList<Modification>();
        List<Double> monoMassDeltas;
        List<Double> avgMassDeltas;
        ParamGroup params;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT modification_id, accession, mod_database, mod_database_version, location FROM pride_modification WHERE peptide_id = ?");
            st.setInt(1, peptide_id);
            rs = st.executeQuery();
            while (rs.next()) {
                params = new ParamGroup(getCvParams(connection, "pride_modification_param", rs.getInt("modification_id")), getUserParams(connection, "pride_modification_param",
                        rs.getInt("modification_id")));
                monoMassDeltas = getDeltaValues(connection, rs.getInt("modification_id"), "uk.ac.ebi.pride.rdbms.ojb.model.core.MonoMassDeltaBean");
                avgMassDeltas = getDeltaValues(connection, rs.getInt("modification_id"), "uk.ac.ebi.pride.rdbms.ojb.model.core.AverageMassDeltaBean");
                modifications.add(new Modification(params,rs.getString("accession"),null,rs.getInt("location"),null,avgMassDeltas,monoMassDeltas,rs.getString("mod_database"), rs.getString("mod_database_version")));
            }
        } catch (SQLException e) {
            logger.error("Failed to query modifications", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return modifications;
    }

    private List<FragmentIon> getFragmentIons(Connection connection, int peptide_id) throws DataAccessException {
        List<FragmentIon> fragmentIons = new ArrayList<FragmentIon>();


        boolean newConnection = false;

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.debug("Getting a connection for fragment ions");
                connection = PooledConnectionFactory.getConnection();
                newConnection = true;
            }
            st = connection.prepareStatement("SELECT fragment_ion_id, mz, intensity, mass_error, retention_time_error, accession_ion_type, ion_type_name, fragment_ion_number, ion_charge FROM pride_fragment_ion WHERE peptide_id = ?");
            st.setInt(1, peptide_id);
            rs = st.executeQuery();
            while (rs.next()) {
                List<CvParam> cvParams = new ArrayList<CvParam>();
                //add mz param
                CvTermReference cvTerm = CvTermReference.PRODUCT_ION_MZ;
                cvParams.add(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                        Double.toString(rs.getDouble("mz")), null, null, null));
                cvTerm = CvTermReference.PRODUCT_ION_INTENSITY;
                cvParams.add(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                        Double.toString(rs.getDouble("intensity")), null, null, null));
                cvTerm = CvTermReference.PRODUCT_ION_MASS_ERROR;
                cvParams.add(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                        Double.toString(rs.getDouble("mass_error")), null, null, null));
                cvTerm = CvTermReference.PRODUCT_ION_RETENTION_TIME_ERROR;
                cvParams.add(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                        Double.toString(rs.getDouble("retention_time_error")), null, null, null));
                cvTerm = CvTermReference.PRODUCT_ION_TYPE;
                cvParams.add(new CvParam(rs.getString("accession_ion_type"), rs.getString("ion_type_name"), cvTerm.getCvLabel(),
                        rs.getString("fragment_ion_number"), null, null, null));
                cvTerm = CvTermReference.PRODUCT_ION_CHARGE;
                cvParams.add(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(),
                        rs.getString("ion_charge"), null, null, null));
                //get the charge
                cvParams.addAll(getCvParams(connection, "pride_fragment_ion_param", rs.getInt("fragment_ion_id")));
                fragmentIons.add(new FragmentIon(new ParamGroup(cvParams, null)));
            }
        } catch (SQLException e) {
            logger.error("Failed to query fragment ions", e);
        } finally {
            DBUtilities.releaseResources(newConnection ? connection : null, st, rs);
        }

        return fragmentIons;
    }

    private Spectrum getSpectrumByPeptide(Connection connection, int experiment_id, int spectrum_ref) throws DataAccessException {
        Spectrum spectrum = null;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT spectrum_id FROM pride_experiment pe, mzdata_spectrum ms WHERE pe.experiment_id = ? AND " +
                    "pe.mz_data_id = ms.mz_data_id AND ms.spectrum_identifier = ?");
            st.setInt(1, experiment_id);
            st.setInt(2, spectrum_ref);
            rs = st.executeQuery();
            while (rs.next()) {
                spectrum = getSpectrumById(Integer.toString(rs.getInt("spectrum_id")));
            }
        } catch (SQLException e) {
            logger.error("Failed to query spectrum by peptide", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return spectrum;
    }

    private List<Peptide> getPeptideIdentification(Connection connection, int identification_id, int experiment_id) throws DataAccessException {
        List<Peptide> peptides = new ArrayList<Peptide>();
        List<Modification> modifications;
        List<FragmentIon> fragmentIons;
        Spectrum spectrum;
        ParamGroup params;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT peptide_id, sequence, pep_start, pep_end, spectrum_ref FROM pride_peptide WHERE identification_id = ?");
            st.setInt(1, identification_id);
            rs = st.executeQuery();
            while (rs.next()) {
                params = new ParamGroup(getCvParams(connection, "pride_peptide_param", rs.getInt("peptide_id")), getUserParams(connection, "pride_peptide_param", rs.getInt("peptide_id")));
                modifications = getModificationsPeptide(connection, rs.getInt("peptide_id"));
                fragmentIons = getFragmentIons(connection, rs.getInt("peptide_id"));
                spectrum = getSpectrumByPeptide(connection, experiment_id, rs.getInt("spectrum_ref"));
                PeptideSequence peptideSequence = new PeptideSequence(null,null,rs.getString("sequence"),modifications);
                List<PeptideEvidence> peptideEvidences = new ArrayList<PeptideEvidence>();
                peptideEvidences.add(new PeptideEvidence(null,null,rs.getInt("pep_start"),rs.getInt("pep_end"),false,peptideSequence,null));
                peptides.add(new Peptide(params,null,null,-1,0.0,0.0,0.0,peptideSequence,-1,false,null,null,peptideEvidences,fragmentIons,null,spectrum,null));
            }
        } catch (SQLException e) {
            logger.error("Failed to query peptides", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return peptides;
    }


    private Gel getPeptideGel(Connection connection, int gel_id, double x_coordinate, double y_coordinate, double molecular_weight, double pi) throws DataAccessException {
        Gel gel;
        ParamGroup params = null;
        String gelLink = null;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT gel_link FROM pride_gel WHERE gel_id = ?");
            st.setInt(1, gel_id);
            rs = st.executeQuery();
            while (rs.next()) {
                params = new ParamGroup(getCvParams(connection, "pride_gel_param", gel_id), getUserParams(connection, "pride_gel_param", gel_id));
                gelLink = rs.getString("gel_link");
            }
        } catch (SQLException e) {
            logger.error("Failed to query gel", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        gel = new Gel(params, gelLink, x_coordinate, y_coordinate, molecular_weight, pi);
        return gel;
    }

    @Override
    public Identification getIdentificationById(Comparable id, boolean useCache) throws DataAccessException {
        Identification identification = super.getIdentificationById(id, useCache);
        if (identification == null) {
            List<Peptide> peptides;
            Spectrum spectrum;
            ParamGroup params;
            Gel gel;

            Connection connection = null;
            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT identification_id, accession_number, accession_version, score, search_database, database_version, " +
                        "search_engine, sequence_coverage, splice_isoform, threshold, gel_id, x_coordinate, y_coordinate, " +
                        "molecular_weight, pi, identification_id, pi.experiment_id, pi.classname, pi.spectrum_ref FROM pride_identification pi, pride_experiment pe " +
                        "WHERE pe.accession = ? AND pi.experiment_id = pe.experiment_id AND pi.identification_id = ?");
                st.setString(2, id.toString());
                st.setString(1, foregroundExperimentAcc.toString());
                rs = st.executeQuery();
                while (rs.next()) {
                    String accession = rs.getString("accession_number");
                    logger.debug("Getting a identification from database: {}", accession);
                    Double seqConverage = rs.getDouble("sequence_coverage");
                    double seqConverageVal = seqConverage == 0 ? -1 : seqConverage;
                    params = new ParamGroup(getCvParams(connection, "pride_identification_param", rs.getInt("identification_id")), getUserParams(connection, "pride_identification_param", rs.getInt("identification_id")));
                    peptides = getPeptideIdentification(connection, rs.getInt("identification_id"), rs.getInt("pi.experiment_id"));
                    spectrum = getSpectrumByRef(connection, rs.getString("spectrum_ref"));
                    String className = rs.getString("classname");
                    if ("uk.ac.ebi.pride.rdbms.ojb.model.core.TwoDimensionalIdentificationBean".equals(className)) {
                        gel = getPeptideGel(connection, rs.getInt("gel_id"), rs.getDouble("x_coordinate"), rs.getDouble("y_coordinate"), rs.getDouble("molecular_weight"), rs.getDouble("pi"));
                        identification = new TwoDimIdentification(Integer.toString(rs.getInt("identification_id")), accession, rs.getString("accession_version"), peptides, rs.getDouble("score"),
                                rs.getString("search_database"), rs.getString("database_version"), rs.getString("search_engine"), seqConverageVal,
                                spectrum, rs.getString("splice_isoform"), rs.getDouble("threshold"), params, gel);
                    } else if ("uk.ac.ebi.pride.rdbms.ojb.model.core.GelFreeIdentificationBean".equals(className)) {
                        identification = new GelFreeIdentification(Integer.toString(rs.getInt("identification_id")), accession, rs.getString("accession_version"), peptides, rs.getDouble("score"),
                                rs.getString("search_database"), rs.getString("database_version"), rs.getString("search_engine"), seqConverageVal,
                                spectrum, rs.getString("splice_isoform"), rs.getDouble("threshold"), params);
                    }

                    if (useCache) {
                        cache.store(CacheCategory.IDENTIFICATION, id, identification);
                    }
                }
            } catch (SQLException e) {
                logger.error("Failed to query identifications", e);
            } finally {
                DBUtilities.releaseResources(connection, st, rs);
            }
        }
        return identification;
    }


    private Spectrum getSpectrumByRef(Connection connection, String spectrum_ref) throws DataAccessException {
        Spectrum spectrum = null;

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT ms.spectrum_id FROM mzdata_spectrum ms, mzdata_mz_data mz WHERE " +
                    "mz.accession_number = ? AND mz.mz_data_id = ms.mz_data_id AND ms.spectrum_identifier = ?");
            st.setString(1, foregroundExperimentAcc.toString());
            st.setString(2, spectrum_ref);
            rs = st.executeQuery();
            if (rs.next()) {
                spectrum = getSpectrumById(Integer.toString(rs.getInt("ms.spectrum_id")));
            }
        } catch (SQLException e) {
            logger.error("Failed to query spectrum by spectrum reference", e);
        } finally {
            DBUtilities.releaseResources(null, st, rs);
        }

        return spectrum;
    }

    /**
     * ===========================This section is added to overcome the database retrieve speed issue =====================
     */

    @Override
    public boolean isIdentifiedSpectrum(Comparable specId) throws DataAccessException {
        Map<Comparable, Comparable> peptideToSpectrum = (Map<Comparable, Comparable>) cache.get(CacheCategory.PEPTIDE_TO_SPECTRUM);
        return peptideToSpectrum != null && peptideToSpectrum.containsValue(specId);
    }

    /**
     * ===========================This section is added by rwang 31/08/2010 ===============================================
     */


    @Override
    public Peptide getPeptideById(Comparable identId, Comparable peptideId, boolean useCache) throws DataAccessException {

        Peptide peptide = super.getPeptideById(identId, peptideId, useCache);
        if (peptide == null) {
            //todo: check whether to use cache
            String sequence = (String) cache.get(CacheCategory.PEPTIDE_SEQUENCE, peptideId);
            logger.debug("getPeptideById(identId, peptideId): ID[{}] : Sequence[{}]", new Object[]{peptideId, sequence});
            Integer start = (Integer) cache.get(CacheCategory.PEPTIDE_START, peptideId);
            Integer end = (Integer) cache.get(CacheCategory.PEPTIDE_END, peptideId);
            int pid = Integer.parseInt(peptideId.toString());
            ParamGroup params = new ParamGroup(getCvParams(null, "pride_peptide_param", pid), getUserParams(null, "pride_peptide_param", pid));
            List<Modification> modifications = this.getPTMs(identId, peptideId);
            List<FragmentIon> fragmentIons = getFragmentIons(null, pid);
            Spectrum spectrum = null;
            Comparable specId = this.getPeptideSpectrumId(identId, peptideId);
            if (specId != null) {
                spectrum = getSpectrumById(specId);
            }
            PeptideSequence peptideSequence = new PeptideSequence(null,null,sequence,modifications);
            List<PeptideEvidence> peptideEvidences = new ArrayList<PeptideEvidence>();
            peptideEvidences.add(new PeptideEvidence(null,null,start,end,false,peptideSequence,null));
            peptide = new Peptide(params,null,null,-1,0.0,0.0,0.0,peptideSequence,-1,false,null,null,peptideEvidences,fragmentIons,null,spectrum,null);

            if (useCache) {
                cache.store(CacheCategory.PEPTIDE, new Tuple<Comparable, Comparable>(identId, peptideId), peptide);
            }
        }

        return peptide;
    }

    @Override
    public SearchEngine getSearchEngine() throws DataAccessException {
        // check with cache if exists then use the in-memory ident object
        super.getSearchEngine();
        if (searchEngine == null && hasIdentification()) {
            Connection connection = null;
            PreparedStatement st = null;
            ResultSet rs = null;
            // if not exists then use query the engine directly
            try {
                logger.debug("Getting search engine");
                Comparable identId = CollectionUtils.getElement(getIdentificationIds(), 0);
                connection = PooledConnectionFactory.getConnection();

                // get mz data array id
                st = connection.prepareStatement("select search_engine from pride_identification where identification_id=?");
                st.setString(1, identId.toString());
                rs = st.executeQuery();
                if (rs.next()) {
                    searchEngine = new SearchEngine(null,getString("search_engine"));
                }

                // get search engine types
                Map<Comparable, ParamGroup> params = (Map<Comparable, ParamGroup>) cache.get(CacheCategory.PEPTIDE_TO_PARAM);
                if (params != null && !params.isEmpty()) {
                    Collection<ParamGroup> paramGroups = params.values();
                    ParamGroup paramGroup = CollectionUtils.getElement(paramGroups, 0);
                    searchEngine.setSearchEngineTypes(DataAccessUtilities.getSearchEngineTypes(paramGroup));
                }
            } catch (SQLException e) {
                String errMsg = "Failed to query search engine for identification";
                logger.error(errMsg, e);
                throw new DataAccessException(errMsg, e);
            } finally {
                DBUtilities.releaseResources(connection, st, rs);
            }
        }

        return searchEngine;
    }

    @Override
    public String getIdentificationType(Comparable identId) throws DataAccessException {
        String type = null;

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            logger.debug("Getting identification type");
            connection = PooledConnectionFactory.getConnection();
            // get mz data array id
            st = connection.prepareStatement("select classname from pride_identification where identification_id=?");
            st.setString(1, identId.toString());
            rs = st.executeQuery();
            if (rs.next()) {
                String className = rs.getString("classname");
                type = className.contains("TwoDimensionalIdentificationBean") ? TWO_DIM_IDENTIFICATION_TYPE : GEL_FREE_IDENTIFICATION_TYPE;
            }
        } catch (SQLException e) {
            String errMsg = "Failed to query identification type";
            logger.error(errMsg, e);
            throw new DataAccessException(errMsg, e);
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }

        return type;
    }

    @Override
    public int getNumberOfPeaks(Comparable specId) throws DataAccessException {
        // check with cache if exists then use the in-memory spectrum object
        int cnt = 0;
        Integer num = (Integer) cache.get(CacheCategory.NUMBER_OF_PEAKS, specId);
        if (num == null) {
            Spectrum spectrum = (Spectrum) cache.get(CacheCategory.SPECTRUM, specId);
            if (spectrum != null) {
                cnt = DataAccessUtilities.getNumberOfPeaks(spectrum);
            } else {
                Connection connection = null;
                PreparedStatement st = null;
                ResultSet rs = null;

                // if not exists then use query the database directly
                try {
                    logger.debug("Getting number of peaks");
                    connection = PooledConnectionFactory.getConnection();
                    // get mz data array id
                    st = connection.prepareStatement("select mz_array_binary_id from mzdata_spectrum where spectrum_id=?");
                    st.setString(1, specId.toString());
                    rs = st.executeQuery();
                    int mzArrId = -1;
                    if (rs.next()) {
                        mzArrId = rs.getInt("mz_array_binary_id");
                    }
                    // get binary data array
                    if (mzArrId != -1) {
                        BinaryDataArray mzBinaryArray = getBinaryDataArray(connection, mzArrId, CvTermReference.MZ_ARRAY);
                        cnt = mzBinaryArray.getDoubleArray().length;
                        cache.store(CacheCategory.NUMBER_OF_PEAKS, specId, cnt);
                    }
                } catch (SQLException e) {
                    String errMsg = "Failed to query number of peaks for spectrum";
                    logger.error(errMsg, e);
                    throw new DataAccessException(errMsg, e);
                } catch (UnsupportedEncodingException e) {
                    String errMsg = "Failed to query number of peaks during decoding of the binary data array";
                    logger.error(errMsg, e);
                    throw new DataAccessException(errMsg, e);
                } finally {
                    DBUtilities.releaseResources(connection, st, rs);
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
        Double sumOfIntent = (Double) cache.get(CacheCategory.SUM_OF_INTENSITY, specId);
        if (sumOfIntent == null) {
            Spectrum spectrum = (Spectrum) cache.get(CacheCategory.SPECTRUM, specId);
            if (spectrum != null) {
                sum = DataAccessUtilities.getSumOfIntensity(spectrum);
            } else {
                Connection connection = null;
                PreparedStatement st = null;
                ResultSet rs = null;
                // if not exists then use query the database directly
                try {
                    logger.debug("Getting sum of intensity: spectrum id[{}]", specId);
                    connection = PooledConnectionFactory.getConnection();
                    // get mz data array id
                    st = connection.prepareStatement("select inten_array_binary_id from mzdata_spectrum where spectrum_id=?");
                    st.setString(1, specId.toString());
                    rs = st.executeQuery();
                    int mzArrId = -1;
                    if (rs.next()) {
                        mzArrId = rs.getInt("inten_array_binary_id");
                    }
                    // get binary data array
                    if (mzArrId != -1) {
                        BinaryDataArray intentBinaryArray = getBinaryDataArray(connection, mzArrId, CvTermReference.MZ_ARRAY);
                        if (intentBinaryArray != null) {
                            double[] originalIntentArr = intentBinaryArray.getDoubleArray();
                            for (double intent : originalIntentArr) {
                                sum += intent;
                            }
                            cache.store(CacheCategory.SUM_OF_INTENSITY, specId, sum);
                            cache.store(CacheCategory.NUMBER_OF_PEAKS, specId, intentBinaryArray.getDoubleArray().length);
                        }
                    }
                } catch (SQLException e) {
                    String errMsg = "Failed to query sum of intensity for spectrum";
                    logger.error(errMsg, e);
                    throw new DataAccessException(errMsg, e);
                } catch (UnsupportedEncodingException e) {
                    String errMsg = "Failed to query sum of intensity while decoding the binary data array";
                    logger.error(errMsg, e);
                    throw new DataAccessException(errMsg, e);
                } finally {
                    DBUtilities.releaseResources(connection, st, rs);
                }
            }
        } else {
            sum = sumOfIntent;
        }

        return sum;
    }

    @Override
    public Quantitation getProteinQuantData(Comparable identId) throws DataAccessException {
        ParamGroup paramGroup = (ParamGroup) cache.get(CacheCategory.IDENTIFICATION_TO_PARAM, identId);

        if (paramGroup != null) {
            return new Quantitation(Quantitation.Type.PROTEIN, paramGroup.getCvParams());
        }

        return null;
    }

    @Override
    public Quantitation getPeptideQuantData(Comparable identId, Comparable peptideId) throws DataAccessException {
        ParamGroup paramGroup = (ParamGroup) cache.get(CacheCategory.PEPTIDE_TO_PARAM, peptideId);

        if (paramGroup != null) {
            return new Quantitation(Quantitation.Type.PEPTIDE, paramGroup.getCvParams());
        }

        return null;
    }

    @Override
    public List<PrideChartManager> getChartData() throws DataAccessException {
        List<PrideChartManager> list = new ArrayList<PrideChartManager>();

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            connection = PooledConnectionFactory.getConnection();
            st = connection.prepareStatement(
                    "select c.chart_type, c.intermediate_data " +
                            "from pride_chart_data c, pride_experiment e " +
                            "where c.experiment_id = e.experiment_id and " +
                            "   e.accession = ? and " +
                            "   c.intermediate_data is not null " +
                            "order by c.chart_type");
            st.setString(1, foregroundExperimentAcc.toString());

            Map<Integer, PrideChartManager> map = new HashMap<Integer, PrideChartManager>();
            rs = st.executeQuery();
            while (rs.next()) {
                int type = rs.getInt("chart_type");
                String jsonData = rs.getString("intermediate_data");

                PrideChart prideChart = PrideChartFactory.getChart(type, jsonData);
                map.put(type, new PrideChartManager(prideChart));
            }
            rs.close();

            if (!map.isEmpty())
                for (Integer id : PrideChartFactory.getChartOrder())
                    if (map.containsKey(id)) list.add(map.get(id));

        } catch (Exception e) {
            String errMsg = "Charts intermediate data could not be retrieved from database";
            logger.error(errMsg, e);
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }

        //If the list is empty means that the intermediate data has not been retrieved for some reason
        if (list.isEmpty()) {
            try {
                /**
                 * The next PrideChartSummaryData object if from PRIDE-Chart package
                 * uk.ac.ebi.pride.chart.controller.PrideChartSummaryData
                 * and is used for retrieving the summary data from the experiment
                 * instead of using the intermediate data (because it could not be retrieved
                 * in the above step)
                 */
                String accession = foregroundExperimentAcc.toString();
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
                DBUtilities.releaseResources(connection, st, rs);
            }
        }

        return list;
    }
}




