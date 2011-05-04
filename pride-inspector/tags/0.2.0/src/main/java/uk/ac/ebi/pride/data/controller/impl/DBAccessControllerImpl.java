package uk.ac.ebi.pride.data.controller.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessControllerType;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.utils.BinaryDataUtils;
import uk.ac.ebi.pride.data.utils.CvTermReference;
import uk.ac.ebi.pride.data.utils.LoggerUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.ByteOrder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: dani
 * Date: 13-Apr-2010
 * Time: 14:32:25
 * To change this template use File | Settings | File Templates.
 */
public class DBAccessControllerImpl extends AbstractDataAccessController {

    private Connection DBConnection = null;
    private final static String CONTACT_INFO = "contact information";
    private final static String SAMPLE_ID = "sample1";
    private final static String COMMENTS = "comments";
    private final static String COMPLETION_TIME = "completion time";
    private final static int PROCESSING_METHOD_ORDER = 1;
    private final static String DATA_PROCESSING_ID = "dataprocessing1";
    private final static String PROTOCOL_ID = "protocol1";

    //caching mechanisms for spectrumIds, TwoDimIds, GelFreeIds, and ChromatogramIds
    private List<Comparable> spectrumIds = new ArrayList<Comparable>();
    private List<Comparable> twoDimIds = new ArrayList<Comparable>();
    private List<Comparable> gelFreeIds = new ArrayList<Comparable>();

    private static final Logger logger = Logger.getLogger(DBAccessControllerImpl.class.getName());

    public DBAccessControllerImpl() throws DataAccessException {
        initialize();
    }

    private void initialize() throws DataAccessException {
        //get properties file
        Properties properties = new Properties();
        URL url = ClassLoader.getSystemResource("prop/database.prop");
        //    URL url = DBAccessControllerImpl.class.getClassLoader().getResource("database.prop");
        try {
            properties.load(new FileInputStream(url.getFile()));
        }
        catch (IOException e) {
            LoggerUtils.error(logger, this, e);
        }
        //create connection
        //load driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LoggerUtils.error(logger, this, e);
        }
        String url_connection = properties.getProperty("protocol") + ':' + properties.getProperty("subprotocol") +
                ':' + properties.getProperty("alias");
        try {
            DBConnection = DriverManager.getConnection(url_connection, properties.getProperty("user"), properties.getProperty("password"));
            this.setSource(DBConnection);
        }
        catch (SQLException err) {
            LoggerUtils.error(logger, this, err);
        }

        // set type
        this.setType(DataAccessControllerType.DATABASE);
    }

    @Override
    public void setForegroundExperimentAcc(Comparable expId) throws DataAccessException {
        //change in experiment, clear the spectrumId, gelFreeIds and twoDimIds cache
        clearCache();
        super.setForegroundExperimentAcc(expId);
    }


    private void clearCache() {
        spectrumIds.clear();
        twoDimIds.clear();
        gelFreeIds.clear();
    }

    public List<CvParam> getCvParams(String table_name, int parent_element_id) {
        List<CvParam> cvParams = new ArrayList<CvParam>();
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT accession, name, value, cv_label FROM " + table_name + " WHERE parent_element_fk = ? AND cv_label is not null");
            st.setInt(1, parent_element_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                cvParams.add(new CvParam(rs.getString("accession"), rs.getString("name"), rs.getString("cv_label"), rs.getString("value"), "", "", ""));
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return cvParams;
    }

    //same as before, but returns a list of UserParam

    public List<UserParam> getUserParams(String table_name, int parent_element_id) {
        List<UserParam> userParams = new ArrayList<UserParam>();
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT name, value FROM " + table_name + " WHERE parent_element_fk = ? AND cv_label is null");
            st.setInt(1, parent_element_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                userParams.add(new UserParam(rs.getString("name"), "", rs.getString("value"), "", "", ""));
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return userParams;
    }

    public List<Comparable> getExperimentAccs() throws DataAccessException {
        List<Comparable> array = new ArrayList<Comparable>();
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT accession FROM pride_experiment ORDER BY ABS(accession)");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                array.add(rs.getString("accession"));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return array;
    }

    public List<SourceFile> getSourceFiles() {
        List<SourceFile> sourceFiles = new ArrayList<SourceFile>();

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT sf.name_of_file, sf.path_to_file FROM mzdata_source_file sf, mzdata_mz_data mz WHERE mz.accession_number= ? and mz.source_file_id=sf.source_file_id");
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                //there should be a single source file per spectrum
                SourceFile sourceFile = new SourceFile("", rs.getString("name_of_file"), rs.getString("path_to_file"), null);
                sourceFiles.add(sourceFile);
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        return sourceFiles;
    }

    public List<ParamGroup> getContacts() {
        List<ParamGroup> contacts = new ArrayList<ParamGroup>();
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT contact_name, institution, contact_info FROM mzdata_contact sf, mzdata_mz_data mz WHERE mz.accession_number= ? and mz.mz_data_id=sf.mz_data_id");
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
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
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return contacts;
    }

    public FileDescription getFileDescription() throws DataAccessException {

        List<CvParam> cvParams = new ArrayList<CvParam>();
        CvTermReference cvTerm = CvTermReference.MASS_SPECTRUM;
        CvParam cvParam = new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(), null, null, null, null);
        cvParams.add(cvParam);
        ParamGroup fileContent = new ParamGroup(cvParams, null);
        List<SourceFile> sourceFiles = getSourceFiles();
        List<ParamGroup> contacts = getContacts();
        return new FileDescription(fileContent, sourceFiles, contacts);
    }

    public List<Sample> getSamples() {
        List<Sample> samples = new ArrayList<Sample>();

        List<CvParam> cvParam;
        List<UserParam> userParam;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT mz_data_id, sample_name FROM mzdata_mz_data mz WHERE mz.accession_number= ?");
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int mz_data_id = rs.getInt("mz_data_id");
                String sample_name = rs.getString("sample_name");
                userParam = getUserParams("mzdata_sample_param", mz_data_id);
                cvParam = getCvParams("mzdata_sample_param", mz_data_id);
                samples.add(new Sample(SAMPLE_ID, sample_name, new ParamGroup(cvParam, userParam)));
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return samples;

    }

    public List<Software> getSoftware() throws DataAccessException {
        List<Software> softwares = new ArrayList<Software>();

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT software_name, software_version, software_completion_time, software_comments FROM mzdata_mz_data mz WHERE mz.accession_number= ?");
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                List<CvParam> cvParams = new ArrayList<CvParam>();
                //ToDo: semantic support, need to add a child term of MS:1000531 (software)
                List<UserParam> userParams = new ArrayList<UserParam>();
                userParams.add(new UserParam(COMMENTS, null, rs.getString("software_comments"), null, null, null));
                String completionTime = rs.getString("software_completion_time");
                if (completionTime != null) {
                    userParams.add(new UserParam(COMPLETION_TIME, null, completionTime, null, null, null));
                }
                softwares.add(new Software(rs.getString("software_name"), rs.getString("software_version"), new ParamGroup(cvParams, userParams)));
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return softwares;
    }

    public List<ParamGroup> getAnalyzerList(int mz_data_id) {

        List<ParamGroup> analyzerList = new ArrayList<ParamGroup>();
        List<UserParam> userParams;
        List<CvParam> cvParams;
        ParamGroup params;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT analyzer_id FROM mzdata_analyzer WHERE mz_data_id = ?");
            st.setInt(1, mz_data_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                userParams = getUserParams("mzdata_analyzer_param", rs.getInt("analyzer_id"));
                cvParams = getCvParams("mzdata_analyzer_param", rs.getInt("analyzer_id"));
                params = new ParamGroup(cvParams, userParams);
                analyzerList.add(params);

            }
            rs.close();

        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        return analyzerList;
    }

    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        List<InstrumentConfiguration> instrumentConfigurations = new ArrayList<InstrumentConfiguration>();
        //get software
        Software software = null;
        if (getSoftware().size() > 0) {
            software = getSoftware().get(0);
        }
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT instrument_name, mz_data_id FROM mzdata_mz_data mz WHERE mz.accession_number= ?");
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
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
                InstrumentComponent source = new InstrumentComponent(sourceOrder, new ParamGroup(getCvParams("mzdata_instrument_source_param", mz_data_id), getUserParams("mzdata_instrument_source_param", mz_data_id)));
                //create detector
                int detectorOrder = getAnalyzerList(mz_data_id).size() + 2;
                InstrumentComponent detector = new InstrumentComponent(detectorOrder, new ParamGroup(getCvParams("mzdata_instrument_detector_param", mz_data_id), getUserParams("mzdata_instrument_detector_param", mz_data_id)));
                //create analyzer
                List<ParamGroup> analyzers = getAnalyzerList(mz_data_id);
                int orderCnt = 2;
                for (ParamGroup analyzer : analyzers) {
                    InstrumentComponent analyzerInstrument = new InstrumentComponent(orderCnt, analyzer);
                    instrumentConfigurations.add(new InstrumentConfiguration(rs.getString("instrument_name"), null, software, source, analyzerInstrument, detector, params));
                    orderCnt++;
                }
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return instrumentConfigurations;
    }

    public List<DataProcessing> getDataProcessings() throws DataAccessException {
        List<CvParam> cvParams;
        List<UserParam> userParams;
        List<ProcessingMethod> procMethods = new ArrayList<ProcessingMethod>();
        List<DataProcessing> dataProcessings = new ArrayList<DataProcessing>();
        Software software = null;
        if (getSoftware().size() > 0) {
            software = getSoftware().get(0);
        }
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT mz_data_id FROM mzdata_mz_data mz WHERE mz.accession_number= ?");
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int mz_data_id = rs.getInt("mz_data_id");
                userParams = getUserParams("mzdata_processing_method_param", mz_data_id);
                cvParams = getCvParams("mzdata_processing_method_param", mz_data_id);
                ParamGroup params = new ParamGroup(cvParams, userParams);
                CvTermReference cvTerm = CvTermReference.CONVERSION_TO_MZML;
                params.addCvParam(new CvParam(cvTerm.getAccession(), cvTerm.getName(), cvTerm.getCvLabel(), null, null, null, null));
                procMethods.add(new ProcessingMethod(PROCESSING_METHOD_ORDER, software, params));
                dataProcessings.add(new DataProcessing(DATA_PROCESSING_ID, procMethods));
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return dataProcessings;
    }

    //for a given experimentId, will return the protocol_steps_id sorted by index

    public List<Integer> getProtocolStepsById(int experimentId) {
        List<Integer> protocol_steps = new ArrayList<Integer>();
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT protocol_step_id FROM pride_protocol_step pp, pride_experiment pe WHERE " +
                    "pe.experiment_id = pp.experiment_id AND pe.accession = ? ORDER BY protocol_step_index");
            st.setInt(1, experimentId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                protocol_steps.add(rs.getInt("protocol_step_id"));
            }
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return protocol_steps;
    }

    public Protocol getProtocol() {
        List<Integer> protocol_steps;
        List<UserParam> userParams;
        List<CvParam> cvParams;
        List<ParamGroup> paramGroup = new ArrayList<ParamGroup>();
        String protocol_name = null;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT protocol_name FROM pride_experiment WHERE accession= ?");
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                protocol_name = rs.getString("protocol_name");
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        protocol_steps = getProtocolStepsById(Integer.parseInt(foregroundExperimentAcc.toString()));

        //for each protocol_step, get the paramGroup
        for (int protocol_step_id : protocol_steps) {
            cvParams = getCvParams("pride_protocol_param", protocol_step_id);
            userParams = getUserParams("pride_protocol_param", protocol_step_id);
            paramGroup.add(new ParamGroup(cvParams, userParams));
        }

        return new Protocol(PROTOCOL_ID, protocol_name, paramGroup, null);
    }

    public List<Reference> getReferences() {
        List<Reference> references = new ArrayList<Reference>();
        List<UserParam> userParams;
        List<CvParam> cvParams;

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT reference_line, pr.reference_id FROM pride_experiment pe, pride_reference pr, pride_reference_exp_link pl WHERE " +
                    "pe.accession = ? AND pl.reference_id = pr.reference_id AND pl.experiment_id = pe.experiment_id");
            st.setInt(1, Integer.parseInt(foregroundExperimentAcc.toString()));
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                userParams = getUserParams("pride_reference_param", rs.getInt("pr.reference_id"));
                cvParams = getCvParams("pride_reference_param", rs.getInt("pr.reference_id"));
                references.add(new Reference(rs.getString("reference_line"), new ParamGroup(cvParams, userParams)));
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        return references;
    }

    public ParamGroup getAdditional() {
        ParamGroup additional = null;
        List<CvParam> cvParam;
        List<UserParam> userParam;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT experiment_id FROM pride_experiment WHERE accession= ?");
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int experiment_id = rs.getInt("experiment_id");
                userParam = getUserParams("pride_experiment_param", experiment_id);
                cvParam = getCvParams("pride_experiment_param", experiment_id);
                additional = new ParamGroup(cvParam, userParam);
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return additional;
    }

    public MetaData getMetaData() throws DataAccessException {
        String accession = "";
        String version = "2.1";
        String title = "";
        String shortLabel = "";
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT pe.title, pe.accession, pe.short_label FROM pride_experiment pe WHERE pe.accession = ?");
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                accession = rs.getString("pe.accession");
                title = rs.getString("pe.title");
                shortLabel = rs.getString("pe.short_label");
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        FileDescription fileDesc = getFileDescription();
        List<Sample> samples = getSamples();
        List<Software> software = getSoftware();
        List<InstrumentConfiguration> instrumentConfigurations = getInstrumentConfigurations();
        List<DataProcessing> dataProcessings = getDataProcessings();
        ParamGroup additional = getAdditional();
        Protocol protocol = getProtocol();
        List<Reference> references = getReferences();
        return new Experiment(null, accession, version, fileDesc,
                samples, software, null, instrumentConfigurations,
                dataProcessings, additional, title, shortLabel,
                protocol, references, null, null);
    }


    public Experiment getExperimentByAcc(Comparable experimentId) throws DataAccessException {
        return null;
    }

    public List<CVLookup> getCvLookups() throws DataAccessException {
        List<CVLookup> cvLookups = new ArrayList<CVLookup>();

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT cv_label, version, address, full_name FROM mzdata_cv_lookup sf, mzdata_mz_data mz WHERE mz.accession_number= ? and mz.mzdata_id=sf.mzdata_id");
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                CVLookup cvLookup = new CVLookup(rs.getString("cv_label"), rs.getString("full_name"), rs.getString("version"), rs.getString("address"));
                cvLookups.add(cvLookup);
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        return cvLookups;
    }

    public List<Comparable> getSpectrumIds() throws DataAccessException {

        List<Comparable> array = new ArrayList<Comparable>();
        if (spectrumIds.isEmpty()) {
            try {
                PreparedStatement st = DBConnection.prepareStatement("SELECT spectrum_id FROM mzdata_spectrum s, mzdata_mz_data mz WHERE mz.accession_number = ? AND mz.mz_data_id = s.mz_data_id");
                st.setString(1, foregroundExperimentAcc.toString());
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    array.add(Integer.toString(rs.getInt("spectrum_id")));
                }
                rs.close();

            } catch (SQLException e) {
                LoggerUtils.error(logger, this, e);
            }
        } else {
            array.addAll(spectrumIds);
        }
        return array;
    }

    private BinaryDataArray getBinaryDataArray(int array_binary_id, CvTermReference binaryType) throws UnsupportedEncodingException {
        BinaryDataArray binaryDataArray = null;
        CvTermReference dataType = null;
        ByteOrder order = null;
        String chunk;
        String total_array = null;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT data_precision, data_endian FROM mzdata_binary_array WHERE binary_array_id = ?");
            st.setInt(1, array_binary_id);
            ResultSet rs = st.executeQuery();
            //first get precision and order of the binary data
            while (rs.next()) {
                dataType = "32".equals(rs.getString("data_precision")) ? CvTermReference.FLOAT_32_BIT : CvTermReference.FLOAT_64_BIT;
                order = "big".equals(rs.getString("data_endian")) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
            }
            rs.close();
            st = DBConnection.prepareStatement("SELECT chunk FROM mzdata_base_64_chunk WHERE binary_array_id = ? ORDER BY order_index");
            st.setInt(1, array_binary_id);
            rs = st.executeQuery();
            //then get the binary string
            while (rs.next()) {
                chunk = rs.getString("chunk");
                chunk = chunk.replaceAll("\n", "");
                if (total_array == null) {
                    total_array = chunk;
                } else {
                    total_array = total_array.concat(chunk);
                }
            }
            rs.close();
            double[] binaryDoubleArr;
            if (total_array != null) {
                /* Previous object model, was necessary to add at the end of the string some '=' char, seems not necessary any longer
                int modulus = total_array.length() % 8;
                if (modulus != 0) {
             //      total_array = appendEquals(decoded_array, modulus);
                }  */
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
            LoggerUtils.error(logger, this, e);
        }

        return binaryDataArray;
    }

    private String appendEquals(String array, int num_equals) {
        String new_array = "";
        String test = "QFE564UeuFJAVYZ9Vmz0H0BjpLaufVZtQGWjr7fpD/lAZeRSvTw2EUBm5MSbpeNUQGcDmMfigkFAZySlenhsIkBopOl41P30QGkja7" +
                "mMfihAauUmF8G9pUBsRZPdl/YrQHMDEgW8AaNAc6MzMzMzM0B30e+dsi0OQHhTMSbpeNVAeuO9pRGc4EB69EbcXWOIQHsEx+KCQLhAe/NNATq" +
                "So0B9VA3tKIznQH1YhR64UexAfYVZs9B8hUB+ZBqfvnbJQH6EQfIS13NAgAKBOpKjBUCAiVcKPXCkQICK4raufVZAgIxq59Vmz0CAkuoWHk92Q" +
                "ICa4oJAt4BAgP5TjvNNakCBRmK2rn1WQIFKVJUYKplAgdrqs2eg+UCCAel41P30QIJifIS13MZAgmUFHrhR7ECCZpLXcxj8QIK6ufVZs9BAgsLa" +
                "7mMfikCDk1If8uSPQIQjHeaa1CxAhCscDr7fpECELSa1Cw8oQITzTWoWHk9AhPsGjbi6x0CFA2O801qGQIU7ItDlYEJAhZNN0vGpECHMtGCqZMMQ" +
                "Ic7qV6eGwlAh7t++dsi0UCHwz0HyEteQIfLPg3tKI1AimbsImgJ1UCK85TjvNNbQIsDcnuy/sVAiwXMzMzMzUCOfG/SH/LlQI6EMY/FBIE======";
        for (int i = 0; i < num_equals; i++) {
            new_array = new_array.concat("=");
        }

        return array.concat(new_array);
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

    public List<Scan> getScanList(int acq_specification_id, List<ParamGroup> scanWindows) {
        List<Scan> scanList = new ArrayList<Scan>();
        ParamGroup params = null;
        List<CvParam> cvParams;
        List<UserParam> userParams;

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT acquisition_id FROM mzdata_acquisition WHERE acq_specification_id = ?");
            st.setInt(1, acq_specification_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                cvParams = getCvParams("mzdata_acquisition_param", rs.getInt("acquisition_id"));
                userParams = getUserParams("mzdata_acquisition_param", rs.getInt("acquisition_id"));
                params = new ParamGroup(cvParams, userParams);
            }
            rs.close();

            scanList.add(new Scan(null, null, null, null, scanWindows, params));
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        return scanList;
    }

    public List<Precursor> getPrecursorsBySpectrum_id(int spectrum_id) throws DataAccessException {
        List<Precursor> precursors = new ArrayList<Precursor>();
        List<ParamGroup> selectedIon = new ArrayList<ParamGroup>();
        Spectrum spectrum;
        List<CvParam> cvParams;
        List<UserParam> userParams;
        ParamGroup activation;

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT precursor_id, precursor_spectrum_id FROM mzdata_precursor WHERE spectrum_id = ?");
            st.setInt(1, spectrum_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                cvParams = getCvParams("mzdata_activation_param", rs.getInt("precursor_id"));
                userParams = getUserParams("mzdata_activation_param", rs.getInt("precursor_id"));
                spectrum = getSpectrumById(Integer.toString(rs.getInt("precursor_spectrum_id")));
                activation = new ParamGroup(cvParams, userParams);
                cvParams = getCvParams("mzdata_ion_selection_param", rs.getInt("precursor_id"));
                userParams = getUserParams("mzdata_ion_selection_param", rs.getInt("precursor_id"));
                selectedIon.add(new ParamGroup(cvParams, userParams));
                precursors.add(new Precursor(spectrum, null, null, null, selectedIon, activation));
            }
            rs.close();

        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
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

    public List<UserParam> getSpectrumDesc(int spectrum_id) {
        List<UserParam> userParams = new ArrayList<UserParam>();

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT comment_text FROM mzdata_spectrum_desc_comment WHERE spectrum_id = ?");
            st.setInt(1, spectrum_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                userParams.add(new UserParam(COMMENTS, null, rs.getString("comment_text"), null, null, null));
            }
            rs.close();

        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
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

    public Spectrum getSpectrumById(Comparable id) throws DataAccessException {
        Spectrum spectrum = null;
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
        //first get spectrum information from mzdata_spectrum
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT spectrum_id, mz_data_id, mz_range_start, " +
                    "mz_range_stop, ms_level, mz_array_binary_id, inten_array_binary_id, spectrum_type, method_of_combination, ms.acq_specification_id " +
                    "FROM mzdata_spectrum ms LEFT JOIN mzdata_acq_specification ma ON ms.acq_specification_id = ma.acq_specification_id WHERE spectrum_id = ?");
            int value = Integer.parseInt(id.toString());
            st.setInt(1, value);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int index = getSpectrumIds().indexOf(id);

                //do scanlist
                List<ParamGroup> scanWindows = getScanWindows(rs.getBigDecimal("mz_range_start"), rs.getBigDecimal("mz_range_stop"));
                if (rs.getInt("ms.acq_specification_id") != 0) {
                    // add method of combination
                    scanParams.addCvParam(getMethodOfCombination(rs.getString("method_of_combination")));
                    scans.addAll(getScanList(rs.getInt("ms.acq_specification_id"), scanWindows));

                } else {
                    // add method of combination cv param to param group
                    scanParams.addCvParam(getMethodOfCombination(null));
                    // create a Scan
                    Scan scan = new Scan(null, null, null, null, scanWindows, null);
                    scans.add(scan);
                }
                // assemble scan list object
                scanList = new ScanList(scans, scanParams);

                precursors = getPrecursorsBySpectrum_id(value);

                BinaryDataArray mz = getBinaryDataArray(rs.getInt("mz_array_binary_id"), CvTermReference.MZ_ARRAY);
                BinaryDataArray inten = getBinaryDataArray(rs.getInt("inten_array_binary_id"), CvTermReference.INTENSITY_ARRAY);
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
                spectrumParams.addCvParams(getCvParams("mzdata_spectrum_instrument_param", value));
                spectrumParams.addUserParams(getUserParams("mzdata_spectrum_instrument_param", value));
                // add comments
                spectrumParams.addUserParams(getSpectrumDesc(value));
                spectrum = new Spectrum(Integer.toString(rs.getInt("spectrum_id")), index, null, null, defaultArrLength, null,
                        scanList, precursors, null, binaryArray, spectrumParams);
            }
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        } catch (UnsupportedEncodingException e) {
            LoggerUtils.error(logger, this, e); //To change body of catch statement use File | Settings | File Templates.

        }
        return spectrum;
    }


    public List<Comparable> getTwoDimIdentIds() throws DataAccessException {

        List<Comparable> array = new ArrayList<Comparable>();
        if (twoDimIds.isEmpty()) {
            try {
                PreparedStatement st = DBConnection.prepareStatement("SELECT pi.identification_id FROM pride_identification pi, pride_experiment pe " +
                        "WHERE classname = 'uk.ac.ebi.pride.rdbms.ojb.model.core.TwoDimensionalIdentificationBean' AND pi.experiment_id = pe.experiment_id " +
                        "AND pe.accession = ?");
                st.setString(1, foregroundExperimentAcc.toString());
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    array.add(rs.getString("pi.identification_id"));
                }
                rs.close();
            } catch (SQLException e) {
                LoggerUtils.error(logger, this, e);
            }
        } else {
            array.addAll(twoDimIds);
        }
        return array;
    }

    private List<Double> getDeltaValues(int modification_id, String deltaType) {
        List<Double> deltas = new ArrayList<Double>();

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT mass_delta_value FROM pride_mass_delta WHERE modification_id = ? AND classname = ?");
            st.setInt(1, modification_id);
            st.setString(2, deltaType);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                deltas.add(rs.getDouble("mass_delta_value"));
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        return deltas;
    }

    private List<Modification> getModificationsPeptide(int peptide_id) {
        List<Modification> modifications = new ArrayList<Modification>();
        List<Double> monoMassDeltas;
        List<Double> avgMassDeltas;
        ParamGroup params;

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT modification_id, accession, mod_database, mod_database_version, location FROM pride_modification WHERE peptide_id = ?");
            st.setInt(1, peptide_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                params = new ParamGroup(getCvParams("pride_modification_param", rs.getInt("modification_id")), getUserParams("pride_modification_param",
                        rs.getInt("modification_id")));
                monoMassDeltas = getDeltaValues(rs.getInt("modification_id"), "uk.ac.ebi.pride.rdbms.ojb.model.core.MonoMassDeltaBean");
                avgMassDeltas = getDeltaValues(rs.getInt("modification_id"), "uk.ac.ebi.pride.rdbms.ojb.model.core.AverageMassDeltaBean");
                modifications.add(new Modification(params, rs.getString("accession"), rs.getString("mod_database"), rs.getString("mod_database_version"), monoMassDeltas, avgMassDeltas,
                        rs.getInt("location")));
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return modifications;
    }

    private List<FragmentIon> getFragmentIons(int peptide_id) {
        List<FragmentIon> fragmentIons = new ArrayList<FragmentIon>();
        List<CvParam> cvParams = new ArrayList<CvParam>();
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT fragment_ion_id, mz, intensity, mass_error, retention_time_error, ion_type_name FROM pride_fragment_ion WHERE peptide_id = ?");
            st.setInt(1, peptide_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
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
                cvParams.add(new CvParam(cvTerm.getAccession(), rs.getString("ion_type_name"), cvTerm.getCvLabel(),
                        "0", null, null, null));
                //get the charge
                cvParams.addAll(getCvParams("pride_fragment_ion_param", rs.getInt("fragment_ion_id")));
                fragmentIons.add(new FragmentIon(new ParamGroup(cvParams, null)));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return fragmentIons;
    }

    private Spectrum getSpectrumByPeptide(int experiment_id, int spectrum_ref) throws DataAccessException {
        Spectrum spectrum = null;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT spectrum_id FROM pride_experiment pe, mzdata_spectrum ms WHERE pe.experiment_id = ? AND " +
                    "pe.mz_data_id = ms.mz_data_id AND ms.spectrum_identifier = ?");
            st.setInt(1, experiment_id);
            st.setInt(2, spectrum_ref);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                spectrum = getSpectrumById(Integer.toString(rs.getInt("spectrum_id")));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return spectrum;
    }

    public List<Peptide> getPeptideIdentification(int identification_id, int experiment_id) throws DataAccessException {
        List<Peptide> peptides = new ArrayList<Peptide>();
        List<Modification> modifications;
        List<FragmentIon> fragmentIons;
        Spectrum spectrum;
        ParamGroup params;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT peptide_id, sequence, pep_start, pep_end, spectrum_ref FROM pride_peptide WHERE identification_id = ?");
            st.setInt(1, identification_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                params = new ParamGroup(getCvParams("pride_peptide_param", rs.getInt("peptide_id")), getUserParams("pride_peptide_param", rs.getInt("peptide_id")));
                modifications = getModificationsPeptide(rs.getInt("peptide_id"));
                fragmentIons = getFragmentIons(rs.getInt("peptide_id"));
                spectrum = getSpectrumByPeptide(experiment_id, rs.getInt("spectrum_ref"));
                peptides.add(new Peptide(params, rs.getString("sequence"), rs.getInt("pep_start"),
                        rs.getInt("pep_end"), modifications, fragmentIons, spectrum));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return peptides;
    }

    private Gel getPeptideGel(int gel_id, double x_coordinate, double y_coordinate, double molecular_weight, double pi) {
        Gel gel;
        ParamGroup params = null;
        String gelLink = null;

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT gel_link FROM pride_gel WHERE gel_id = ?");
            st.setInt(1, gel_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                params = new ParamGroup(getCvParams("pride_gel_param", gel_id), getUserParams("pride_gel_param", gel_id));
                gelLink = rs.getString("gel_link");
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        gel = new Gel(params, gelLink, x_coordinate, y_coordinate, molecular_weight, pi);
        return gel;
    }

    public TwoDimIdentification getTwoDimIdentById(Comparable id) throws DataAccessException {

        List<Peptide> peptides;
        Spectrum spectrum = null;
        ParamGroup params;
        Gel gel;
        TwoDimIdentification twoDimIdentification = null;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT identification_id, accession_number, accession_version, score, search_database, database_version, " +
                    "search_engine, sequence_coverage, splice_isoform, threshold, gel_id, x_coordinate, y_coordinate, " +
                    "molecular_weight, pi, identification_id, pi.experiment_id FROM pride_identification pi, pride_experiment pe " +
                    "WHERE pe.accession = ? AND pi.experiment_id = pe.experiment_id AND pi.identification_id = ?");
            st.setString(2, id.toString());
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Double seqConverage = rs.getDouble("sequence_coverage");
                double seqConverageVal = seqConverage == 0 ? -1 : seqConverage;
                params = new ParamGroup(getCvParams("pride_identification_param", rs.getInt("identification_id")), getUserParams("pride_identification_param", rs.getInt("identification_id")));
                peptides = getPeptideIdentification(rs.getInt("identification_id"), rs.getInt("pi.experiment_id"));
                gel = getPeptideGel(rs.getInt("gel_id"), rs.getDouble("x_coordinate"), rs.getDouble("y_coordinate"), rs.getDouble("molecular_weight"), rs.getDouble("pi"));
                twoDimIdentification = new TwoDimIdentification(Integer.toString(rs.getInt("identification_id")), rs.getString("accession_number"), rs.getString("accession_version"), peptides, rs.getDouble("score"),
                        rs.getString("search_database"), rs.getString("database_version"), rs.getString("search_engine"), seqConverageVal,
                        spectrum, rs.getString("splice_isoform"), rs.getDouble("threshold"), params, gel);
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return twoDimIdentification;
    }


    public List<Comparable> getGelFreeIdentIds() throws DataAccessException {

        List<Comparable> array = new ArrayList<Comparable>();
        if (gelFreeIds.isEmpty()) {
            try {
                PreparedStatement st = DBConnection.prepareStatement("SELECT pi.identification_id FROM pride_identification pi, pride_experiment pe WHERE classname = 'uk.ac.ebi.pride.rdbms.ojb.model.core.GelFreeIdentificationBean' " +
                        "AND pi.experiment_id = pe.experiment_id AND pe.accession = ?");
                st.setString(1, foregroundExperimentAcc.toString());
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    array.add(rs.getString("pi.identification_id"));
                }
                rs.close();
            } catch (SQLException e) {
                LoggerUtils.error(logger, this, e);
            }
        } else {
            array.addAll(gelFreeIds);
        }
        return array;
    }

    private Spectrum getSpectrumByRef(int spectrum_ref) throws DataAccessException {
        Spectrum spectrum = null;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT ms.spectrum_id FROM mzdata_spectrum ms, mzdata_mz_data mz WHERE " +
                    "mz.accession_number = ? AND mz.mz_data_id = ms.mz_data_id AND ms.spectrum_identifier = ?");
            st.setString(1, foregroundExperimentAcc.toString());
            st.setInt(2, spectrum_ref);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                spectrum = getSpectrumById(Integer.toString(rs.getInt("ms.spectrum_id")));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return spectrum;
    }

    public GelFreeIdentification getGelFreeIdentById(Comparable id) throws DataAccessException {

        List<Peptide> peptides;
        Spectrum spectrum;
        ParamGroup params;
        GelFreeIdentification gelFreeIdentification = null;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT identification_id, accession_number, accession_version, score, search_database, database_version, " +
                    "search_engine, sequence_coverage, splice_isoform, threshold, pi.experiment_id, identification_id, spectrum_ref FROM pride_identification pi, pride_experiment pe " +
                    "WHERE pe.accession = ? AND pi.experiment_id = pe.experiment_id AND pi.identification_id = ?");
            st.setString(2, id.toString());
            st.setString(1, foregroundExperimentAcc.toString());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Double seqConverage = rs.getDouble("sequence_coverage");
                double seqConverageVal = seqConverage == 0 ? -1 : seqConverage;
                params = new ParamGroup(getCvParams("pride_identification_param", rs.getInt("identification_id")), getUserParams("pride_identification_param", rs.getInt("identification_id")));
                peptides = getPeptideIdentification(rs.getInt("identification_id"), rs.getInt("pi.experiment_id"));
                spectrum = getSpectrumByRef(rs.getInt("spectrum_ref"));
                gelFreeIdentification = new GelFreeIdentification(Integer.toString(rs.getInt("identification_id")), rs.getString("accession_number"), rs.getString("accession_version"), peptides, rs.getDouble("score"),
                        rs.getString("search_database"), rs.getString("database_version"), rs.getString("search_engine"), seqConverageVal,
                        spectrum, rs.getString("splice_isoform"), rs.getDouble("threshold"), params);
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return gelFreeIdentification;
    }

    @Override
    public void reload() throws DataAccessException {
        initialize();
        clearCache();
        firePropertyChange(DATA_SOURCE_RELOADED, false, true);
    }

    @Override
    public void close() {
        try {
            DBConnection.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
            System.out.println("Could not close connection to database" + e.getMessage());
        }
    }
}
