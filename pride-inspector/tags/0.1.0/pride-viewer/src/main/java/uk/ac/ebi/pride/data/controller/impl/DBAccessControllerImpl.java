package uk.ac.ebi.pride.data.controller.impl;

import org.apache.log4j.Logger;
import sun.security.util.BigInt;
import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.utils.BinaryDataUtils;
import uk.ac.ebi.pride.data.utils.LoggerUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteOrder;
import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: dani
 * Date: 13-Apr-2010
 * Time: 14:32:25
 * To change this template use File | Settings | File Templates.
 */
public class DBAccessControllerImpl extends AbstractDataAccessController {

    private Connection DBConnection = null;

    private static final Logger logger = Logger.getLogger(DBAccessControllerImpl.class.getName());

    public DBAccessControllerImpl() throws DataAccessException{
        //get properties file
        Properties properties = new Properties();
        URL url = ClassLoader.getSystemResource("database.properties");
        try{
            properties.load(new FileInputStream(url.getFile()));
        }
        catch (IOException e){
            LoggerUtils.error(logger, this, e);
        }
        //create connection
        //load driver
        try{
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LoggerUtils.error(logger, this, e);
        }
        String url_connection = properties.getProperty("protocol") + ':' + properties.getProperty("subprotocol") +
                ':' + properties.getProperty("alias");
        try{
            DBConnection = DriverManager.getConnection(url_connection,properties.getProperty("user"),properties.getProperty("password"));
            this.setExperimentFriendly(true);
            this.setIdentificationFriendly(true);
            this.setSpectrumFriendly(true);
        }
        catch(SQLException err){
            LoggerUtils.error(logger, this, err);
        }
    }
    
    public List<String> getExperimentIds() throws DataAccessException {
        List<String> array = new ArrayList<String>();
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT accession FROM pride_experiment LIMIT 4");
            ResultSet rs = st.executeQuery();
            while (rs.next()){
               // array.add(Integer.toString(rs.getInt("experiment_id")));
                array.add(rs.getString("accession"));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return array;
    }

    public void close(){
        try{
            DBConnection.close();
        }
        catch (SQLException e){
            LoggerUtils.error(logger, this, e);
            System.out.println("Could not close connection to database" + e.getMessage());    
        }
    }

   /* private String title = null;
    private String accession = null;
    private String shortLabel = null;
    private Protocol protocol =  null;
    private List<Reference> references = null;
    private java.util.Date publicDate = null;
    private java.util.Date creationDate = null;
    private boolean isPublic = false;
    private BigInt submitterID = null;
                                             */
     public Experiment getExperimentById(String experimentId) throws DataAccessException {
        Experiment exp = null;
        int hits = 0;
        String protocol_name;
        Protocol prot = null;
        List<Reference> references;
        try{
            PreparedStatement st = DBConnection.prepareStatement("SELECT pe.title, pe.accession, pe.short_label, pe.public_record, pe.creation_date, pe.public_flag, pe.submitter_id, pe.protocol_name, pe.experiment_id FROM pride_experiment pe WHERE pe.accession = ?");
           // int value = Integer.parseInt(experimentId);
            
            //st.setInt(1, value);
            st.setString(1,experimentId);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                int value = rs.getInt("pe.experiment_id");
                hits++;
                protocol_name = rs.getString("pe.protocol_name");
                if (protocol_name != null){
                    //the experiment has a protocol, get it
                    prot = getExperimentProtocol(value,protocol_name);
                }
                //get references for experiment_id
                references = getExperimentReferences(value);
                exp = new Experiment(rs.getString("pe.title"),rs.getString("pe.accession"), rs.getString("pe.short_label"), prot, references, rs.getDate("pe.public_record"),
                        rs.getDate("pe.creation_date"), rs.getBoolean("pe.public_flag"), new BigInt(rs.getInt("pe.submitter_id")),null);

            }
            rs.close();
        } catch(SQLException e){
            LoggerUtils.error(logger,this,e);
         }
         if (hits == 0){
             System.out.println("Experiment " + experimentId + "duplicated in database");   
         }
         return exp;      
    }

    //get References for Experiment
    public List<Reference> getExperimentReferences(int experiment_id){
        List<Reference> references = new ArrayList<Reference>();
        List<UserParam> userParams;
        List<CvParam> cvParams;
        
        try{
            PreparedStatement st = DBConnection.prepareStatement("SELECT reference_line, pr.reference_id FROM pride_reference pr, pride_reference_exp_link pl WHERE pl.experiment_id = ? AND pl.reference_id = pr.reference_id");
            st.setInt(1,experiment_id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                userParams = getUserParams("pride_reference_param", rs.getInt("pr.reference_id"));
                cvParams = getCvParams("pride_reference_param", rs.getInt("pr.reference_id"));
                references.add(new Reference(rs.getString("reference_line"), new ParamGroup(cvParams, userParams)));
            }
            rs.close();
        }
        catch(SQLException e){
            LoggerUtils.error(logger,this,e);
         }

        return references;
    }

    //get ExperimentProtocol
    public Protocol getExperimentProtocol(int experiment_id, String protocol_name){
        List<Integer> protocol_steps;
        List<UserParam> userParams;
        List<CvParam> cvParams;
        List<ParamGroup> paramGroup = new ArrayList<ParamGroup>();
        
        protocol_steps = getProtocolStepsById(experiment_id);

        //for each protocol_step, get the paramGroup
        for(int protocol_step_id: protocol_steps){
            cvParams = getCvParams("pride_protocol_param",protocol_step_id);
            userParams = getUserParams("pride_protocol_param", protocol_step_id);
            paramGroup.add( new ParamGroup(cvParams, userParams) );
        }
        return new Protocol("",protocol_name,paramGroup,null);
    }


    //for a protocol_step_id, returns the list of CvParams
    public List<CvParam> getCvParams(String table_name, int parent_element_id){
        List<CvParam> cvParams = new ArrayList<CvParam>();
        try{
            PreparedStatement st = DBConnection.prepareStatement("SELECT accession, name, value, order_index, is_internal FROM " + table_name + " WHERE parent_element_fk = ? AND cv_label is not null");
            st.setInt(1,parent_element_id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                cvParams.add(new CvParam(rs.getString("accession"), rs.getString("name"), "", rs.getString("value"), "", "", "", rs.getInt("order_index"), rs.getBoolean("is_internal")));
            }
            rs.close();
        }
       catch(SQLException e){
            LoggerUtils.error(logger,this,e);
        }
        return cvParams;
    }

    //same as before, but returns a list of UserParam
    public List<UserParam> getUserParams(String table_name, int parent_element_id){
        List<UserParam> userParams = new ArrayList<UserParam>();
        try{
            PreparedStatement st = DBConnection.prepareStatement("SELECT name, value, order_index, is_internal FROM " + table_name + " WHERE parent_element_fk = ? AND cv_label is null");
            st.setInt(1,parent_element_id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                userParams.add(new UserParam(rs.getString("name"), rs.getString("value"), "", "", "", rs.getInt("order_index"), rs.getBoolean("is_internal")));
            }
            rs.close();
        }
       catch(SQLException e){
            LoggerUtils.error(logger,this,e);
        }
        return userParams;
    }

    //for a given experimentId, will return the protocol_steps_id sorted by index
    public List<Integer> getProtocolStepsById(int experimentId){
        List<Integer> protocol_steps = new ArrayList<Integer>();
        try{
            PreparedStatement st = DBConnection.prepareStatement("SELECT protocol_step_id FROM pride_protocol_step WHERE experiment_id = ? ORDER BY protocol_step_index");
            st.setInt(1,experimentId);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                protocol_steps.add(rs.getInt("protocol_step_id"));
            }
        }
        catch(SQLException e){
            LoggerUtils.error(logger,this,e);    
        }
        return protocol_steps;
    }

    //get all spectrumIds in the database
    public List<String> getSpectrumIds() throws DataAccessException {
        
        List<String> array = new ArrayList<String>();
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT spectrum_id FROM mzdata_spectrum LIMIT 4");
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                array.add(Integer.toString(rs.getInt("spectrum_id")));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return array;
    }
    /*String id,
                    BigInteger index,     not necessary
                    String spotID,        not necessary
                    Instrument instrument,    mzda_mzdata
                    DataProcessing dataProcessing,     mzdata_mzdata
                    int arrLength,  not necessary
                    SourceFile sourceFile,       mzdata_source_file
                    Sample sample,     mzdata_mzdata
                    Date timeStamp,    not necessary
                    ScanList scanList,  mzdata_acquisition  ask Rui again
                    List<Precursor> precursors,   mzdata_precursor
                    List<Product> products,    not necessary
                    List<BinaryDataArray> binaryArray,  mzdata_base_64_chunk  mzdata_binary_array
                    ParamGroup params)  not necessary   
                    */
    
    //get Spectrum for a spectrum_id
    public Spectrum getSpectrumById(String id) throws DataAccessException {
        Spectrum spectrum = null;
        Instrument instrument;
        SourceFile sourceFile;
        DataProcessing data_processing;
        Sample sample;
        ScanList scanList;
        List<Scan> scans;
        List<UserParam> userParams;
        List<CvParam> cvParams;
        List<Precursor> precursors;
        List<BinaryDataArray> binaryArray = new ArrayList<BinaryDataArray>();

        //first get spectrum information from mzdata_spectrum
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT mz_array_binary_id, mz_data_id, acq_specification_id, mz_range_start, " +
                    "mz_range_stop, inten_array_binary_id, mz_array_binary_id FROM mzdata_spectrum WHERE spectrum_id = ?");
            int value = Integer.parseInt(id);
            st.setInt(1, value);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                PreparedStatement st2 = DBConnection.prepareStatement("SELECT sample_name, source_file_id FROM mzdata_mz_data WHERE mz_data_id = ?");
                st2.setInt(1,rs.getInt("mz_data_id"));
                ResultSet rs2 = st2.executeQuery();
                while(rs2.next()){
                    instrument = getInstrumentById(rs.getInt("mz_data_id"));
                    sourceFile = getSourceFileById(rs2.getInt("source_file_id"));
                    data_processing = getDataProcessing(rs.getInt("mz_data_id"));
                    sample = getSample(rs2.getString("sample_name"),rs.getInt("mz_data_id"));
                    cvParams = getCvParams("mzdata_spectrum_instrument_param", value );
                    userParams = getUserParams("mzdata_spectrum_instrument_param", value);
                    userParams.addAll(getUserParams("mzdata_spectrum_instrument_param", value)); //add spectrum instrument params
                    userParams.addAll(getAdSpecification(rs.getInt("acq_specification_id")));    //add acq_specification spectrum_type and method_combination
                    userParams.add(getSpectrumDesc(value));
                    scans = getScanList(rs.getInt("acq_specification_id"), rs.getBigDecimal("mz_range_start"), rs.getBigDecimal("mz_range_stop"));
                    scanList = new ScanList(scans,new ParamGroup(cvParams, userParams));
                    precursors = getPrecursorsBySpectrum_id(value);
                    binaryArray.add(getBinaryDataArray(rs.getInt("mz_array_binary_id")));
                    binaryArray.add(getBinaryDataArray(rs.getInt("inten_array_binary_id")));
                    spectrum = new Spectrum(id,null, "", instrument, data_processing, 0, sourceFile, sample, null, scanList, precursors, null, binaryArray, null);
                }
                rs2.close();
            }
        }catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        return spectrum;
    }

    public BinaryDataArray getBinaryDataArray(int array_binary_id){
        BinaryDataArray binaryDataArray = null;
        BinaryDataUtils.BinaryDataType dataType = null;
        ByteOrder order = null;
        String chunk;
        String total_array = null;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT data_precision, data_endian FROM mzdata_binary_array WHERE binary_array_id = ?");
            st.setInt(1, array_binary_id);
            ResultSet rs = st.executeQuery();
            //first get precision and order of the binary data
            while(rs.next()){
                dataType = "32".equals(rs.getString("data_precision")) ? BinaryDataUtils.BinaryDataType.FLOAT32BIT : BinaryDataUtils.BinaryDataType.FLOAT64BIT;
                order = "big".equals(rs.getString("data_endian")) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
            }
            rs.close();
            st = DBConnection.prepareStatement("SELECT chunk FROM mzdata_base_64_chunk WHERE binary_array_id = ? ORDER BY order_index");
            st.setInt(1,array_binary_id);
            rs = st.executeQuery();
            //then get the binary string
            while(rs.next()){
                chunk = rs.getString("chunk");
                chunk = chunk.replaceAll("\n","");
                if (total_array == null){
                    total_array = chunk;    
                }
                else{
                    total_array = total_array.concat(chunk);
                }
            }
            rs.close();
            double[] binaryDoubleArr;
            if (total_array != null){
                int modulus = total_array.length() % 8;
                //System.out.println("BEFORE binary array " + array_binary_id + " length "+ total_array.length());
                if (modulus != 0 ){
                    total_array = appendEquals(total_array, modulus);
                }
                //System.out.println("binary array " + array_binary_id + " length "+ total_array.length());
                binaryDoubleArr = BinaryDataUtils.toDoubleArray(total_array.getBytes(), dataType, order);
            }
            else{
                binaryDoubleArr = BinaryDataUtils.toDoubleArray(null, dataType, order);
            }
            //System.out.println("Array id " + array_binary_id);
         /*   for(int i=0; i< binaryDoubleArr.length;i++){
                System.out.print(binaryDoubleArr[i] + " ");
            } */
            binaryDataArray = new BinaryDataArray(null, binaryDoubleArr, null);
        }catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        
        return binaryDataArray;
    }

    public String appendEquals(String array, int num_equals){
        String new_array = "";
        String test = "QFE564UeuFJAVYZ9Vmz0H0BjpLaufVZtQGWjr7fpD/lAZeRSvTw2EUBm5MSbpeNUQGcDmMfigkFAZySlenhsIkBopOl41P30QGkja7" +
                "mMfihAauUmF8G9pUBsRZPdl/YrQHMDEgW8AaNAc6MzMzMzM0B30e+dsi0OQHhTMSbpeNVAeuO9pRGc4EB69EbcXWOIQHsEx+KCQLhAe/NNATq" +
                "So0B9VA3tKIznQH1YhR64UexAfYVZs9B8hUB+ZBqfvnbJQH6EQfIS13NAgAKBOpKjBUCAiVcKPXCkQICK4raufVZAgIxq59Vmz0CAkuoWHk92Q" +
                "ICa4oJAt4BAgP5TjvNNakCBRmK2rn1WQIFKVJUYKplAgdrqs2eg+UCCAel41P30QIJifIS13MZAgmUFHrhR7ECCZpLXcxj8QIK6ufVZs9BAgsLa" +
                "7mMfikCDk1If8uSPQIQjHeaa1CxAhCscDr7fpECELSa1Cw8oQITzTWoWHk9AhPsGjbi6x0CFA2O801qGQIU7ItDlYEJAhZNN0vGpECHMtGCqZMMQ" +
                "Ic7qV6eGwlAh7t++dsi0UCHwz0HyEteQIfLPg3tKI1AimbsImgJ1UCK85TjvNNbQIsDcnuy/sVAiwXMzMzMzUCOfG/SH/LlQI6EMY/FBIE======";
        for(int i=0; i<num_equals;i++){
            new_array = new_array.concat("=");
        }

       return array.concat(new_array);
    }
    /*Precursor(ParamGroup activation,
                     String externalSpectrumID,
                     ParamGroup isolationWindow,
                     int msLevel,
                     List<ParamGroup> selectedIon,
                     SourceFile sourceFile,
                     Spectrum spectrum)
    */
    
    public List<Precursor> getPrecursorsBySpectrum_id(int spectrum_id) throws DataAccessException {
        List<Precursor> precursors = new ArrayList<Precursor>();
        List<ParamGroup> selectedIon = new ArrayList<ParamGroup>();
        Spectrum spectrum;
        List<CvParam> cvParams;
        List<UserParam> userParams;
        ParamGroup activation;

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT precursor_id, ms_level, precursor_spectrum_id FROM mzdata_precursor WHERE spectrum_id = ?");
            st.setInt(1, spectrum_id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                cvParams = getCvParams("mzdata_activation_param", rs.getInt("precursor_id"));
                userParams = getUserParams("mzdata_activation_param", rs.getInt("precursor_id"));
                spectrum = getSpectrumById(Integer.toString(rs.getInt("precursor_spectrum_id")));
                activation = new ParamGroup(cvParams, userParams);
                cvParams = getCvParams("mzdata_ion_selection_param", rs.getInt("precursor_id"));
                userParams = getUserParams("mzdata_ion_selection_param", rs.getInt("precursor_id"));
                selectedIon.add(new ParamGroup(cvParams, userParams));
                precursors.add(new Precursor(activation, "", null, rs.getInt("ms_level"), selectedIon, null, spectrum));
            }
            rs.close();

        }catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        return precursors;
    }

    //returns method_combination and spectrum_type as userParams
    public List<UserParam> getAdSpecification(int acq_specification_id){
        List<UserParam> userParams = new ArrayList<UserParam>();

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT spectrum_type, method_of_combination FROM mzdata_acq_specification WHERE acq_specification_id = ?");
            st.setInt(1, acq_specification_id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                userParams.add(new UserParam("SpectrumType",rs.getString("spectrum_type"),"", "", "", 0, false));
                userParams.add(new UserParam("MethodOfCombination",rs.getString("method_of_combination"),"", "", "", 0, false));
            }
            rs.close();

        }catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return userParams;
    }

    //for a given spectrum_id, returns the comment information as userParam
    public UserParam getSpectrumDesc(int spectrum_id){
        UserParam userParam = null;

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT comment_text FROM mzdata_spectrum_desc_comment WHERE spectrum_id = ?");
            st.setInt(1, spectrum_id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                userParam = new UserParam("SpectrumDescription",rs.getString("comment_text"),"", "", "", 0, false);
            }
            rs.close();

        }catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        return userParam;
    }


    public List<Scan> getScanList(int acq_specification_id, BigDecimal mz_range_start, BigDecimal mz_range_stop){
        List<Scan> scanList = new ArrayList<Scan>();
        ParamGroup params = null;
        List<ParamGroup> scanWindow = new ArrayList<ParamGroup>();
        List<CvParam> cvParams;
        List<UserParam> userParams;
        List<CvParam> mzRange = new ArrayList<CvParam>();

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT acquisition_id FROM mzdata_acquisition WHERE acq_specification_id = ?");
            st.setInt(1, acq_specification_id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                cvParams = getCvParams("mzdata_acquisition_param", rs.getInt("acquisition_id"));
                userParams = getUserParams("mzdata_acquisition_param", rs.getInt("acquisition_id"));
                params = new ParamGroup(cvParams, userParams);                
            }
            rs.close();
            if (mz_range_start != null){
                mzRange.add(new CvParam("", "MzRangeStart", "", mz_range_start.toString(), "", "", "", 0, false));
            }
            if (mz_range_stop != null){
                mzRange.add(new CvParam("", "MzRangeEnd", "", mz_range_stop.toString(), "", "", "", 0, false));
            }
            scanWindow.add(new ParamGroup(mzRange, null));
            scanList.add(new Scan("", null, null, scanWindow, params));
        }catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        
        return scanList;    
    }

    public Sample getSample(String sample_name, int mz_data_id){
        Sample sample;
        
        List<CvParam> cvParam;
        List<UserParam> userParam;

        userParam = getUserParams("mzdata_sample_param", mz_data_id);
        cvParam = getCvParams("mzdata_sample_param", mz_data_id);
        sample = new Sample("", sample_name, new ParamGroup(cvParam, userParam));
        
        return sample;

    }
    public DataProcessing getDataProcessing(int mz_data_id){

        DataProcessing data_processing;
        List<ProcessingMethod> methods = new ArrayList<ProcessingMethod>();
        ParamGroup params;
        List<UserParam> userParams;
        List<CvParam> cvParams;

        userParams = getUserParams("mzdata_processing_method_param", mz_data_id);
        cvParams = getCvParams("mzdata_processing_method_param", mz_data_id);
        params = new ParamGroup(cvParams, userParams);
        methods.add(new ProcessingMethod(null, params));
        data_processing = new DataProcessing("",methods);
        
        return data_processing;
    }

   /*                 String id,   id not necessary ??
                      String name,
                      String path,
                      ParamGroup params*/

    public SourceFile getSourceFileById(int source_file_id){
        SourceFile sourceFile = null;

        try{
            PreparedStatement st = DBConnection.prepareStatement("SELECT name_of_file, path_to_file FROM mzdata_source_file WHERE source_file_id = ?");
            st.setInt(1, source_file_id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                //there should be a single source file per spectrum
                sourceFile = new SourceFile("", rs.getString("name_of_file"), rs.getString("path_to_file"),null);
            }
            rs.close();
        }catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }

        return sourceFile;
    }
    //get Instrument object
    /*      no mapping for ScanSetting and Software so far
    * String id, ScanSetting scanSetting, Software software,
                      List<ParamGroup> source, List<ParamGroup> analyzerList,
                      List<ParamGroup> detector, ParamGroup params)

                      canSetting(String id, List<SourceFile> sourceFile,
                       List<ParamGroup> targets,
                       ParamGroup params

             
    * */
    public Instrument getInstrumentById(int mz_data_id){

        Instrument instrument;
        List<ParamGroup> source = new ArrayList<ParamGroup>();
        List<UserParam> userParams;
        List<CvParam> cvParams;
        ParamGroup paramGroup;
        List<ParamGroup> analyzerList;
        List<ParamGroup> detector = new ArrayList<ParamGroup>();

        //create source object
        userParams = getUserParams("mzdata_instrument_source_param", mz_data_id);
        cvParams = getCvParams("mzdata_instrument_source_param", mz_data_id);
        paramGroup = new ParamGroup(cvParams, userParams);
        source.add(paramGroup);
        //create analyzer list
        analyzerList = getAnalyzerList(mz_data_id);
        //create detector
        userParams = getUserParams("mzdata_instrument_detector_param", mz_data_id);
        cvParams = getCvParams("mzdata_instrument_detector_param", mz_data_id);
        paramGroup = new ParamGroup(cvParams, userParams);
        detector.add(paramGroup);
        //create instrument params
        userParams = getUserParams("mzdata_instrument_param", mz_data_id);
        cvParams = getCvParams("mzdata_instrument_param", mz_data_id);
        instrument = new Instrument("1", null, null, source, analyzerList, detector, new ParamGroup(cvParams, userParams));

        return instrument;
    }

    public List<ParamGroup> getAnalyzerList(int mz_data_id){

    List<ParamGroup> analyzerList = new ArrayList<ParamGroup>();
    List<UserParam> userParams;
    List<CvParam> cvParams;
    ParamGroup params;
    try {
        PreparedStatement st = DBConnection.prepareStatement("SELECT analyzer_id FROM mzdata_analyzer WHERE mz_data_id = ?");
        st.setInt(1, mz_data_id);
        ResultSet rs = st.executeQuery();
        while(rs.next()){
            userParams = getUserParams("mzdata_analyzer_param", rs.getInt("analyzer_id"));
            cvParams = getCvParams("mzdata_analyzer_param", rs.getInt("analyzer_id"));
            params = new ParamGroup(cvParams, userParams);
            analyzerList.add(params);

        }
        rs.close();

    }catch (SQLException e) {
        LoggerUtils.error(logger, this, e);
    }
        
    return analyzerList;
    }

    
    public List<String> getTwoDimIdentIds() throws DataAccessException {

        List<String> array = new ArrayList<String>();
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT accession_number FROM pride_identification WHERE classname = 'uk.ac.ebi.pride.rdbms.ojb.model.core.TwoDimensionalIdentificationBean' LIMIT 4");
            ResultSet rs = st.executeQuery();
            while (rs.next()){
               // array.add(Integer.toString(rs.getInt("identification_id")));
                array.add(rs.getString("accession_number"));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return array;

    }

    /*
        String accession, String accessionVersion, List<Peptide> peptides,
        Double score, String searchDatabase, String searchDatabaseVerison,
        String searchEngine, double sequenceConverage, Spectrum spectrum,
        String spliceIsoform, double threshold, ParamGroup params
     */

    public TwoDimIdentification getTwoDimIdentById(String id) throws DataAccessException {

        List<Peptide> peptides;
        Spectrum spectrum = null;
        ParamGroup params;
        Gel gel;
        TwoDimIdentification twoDimIdentification = null;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT accession_number, accession_version, score, search_database, database_version, " +
                    "search_engine, sequence_coverage, splice_isoform, threshold, gel_id, x_coordinate, y_coordinate, " +
                    "molecular_weight, pi, identification_id, experiment_id FROM pride_identification WHERE accession_number = ?");
            st.setString(1, id);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                params = new ParamGroup(getCvParams("pride_identification_param", rs.getInt("identification_id")), getUserParams("pride_identification_param",rs.getInt("identification_id")));
                peptides = getPeptideIdentification(rs.getInt("identification_id"), rs.getInt("experiment_id"));
                gel = getPeptideGel(rs.getInt("gel_id"), rs.getDouble("x_coordinate"), rs.getDouble("y_coordinate"), rs.getDouble("molecular_weight"), rs.getDouble("pi"));
                twoDimIdentification = new TwoDimIdentification(rs.getString("accession_number"), rs.getString("accession_version"), peptides, rs.getDouble("score"),
                        rs.getString("search_database"), rs.getString("database_version"), rs.getString("search_engine"), rs.getDouble("sequence_coverage"),
                        spectrum, rs.getString("splice_isoform"), rs.getDouble("threshold"), params, gel);
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return twoDimIdentification;
    }

    /*
            ParamGroup params, String gelLink,
            double xCoordinate, double yCoordinate,
            double molecularWeight, double pI
     */
    Gel getPeptideGel(int gel_id, double x_coordinate, double y_coordinate, double molecular_weight, double pi){
        Gel gel = null;
        ParamGroup params;
        
        try{
            PreparedStatement st = DBConnection.prepareStatement("SELECT gel_link FROM pride_gel WHERE gel_id = ?");
            st.setInt(1, gel_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                params = new ParamGroup(getCvParams("pride_gel_param", gel_id), getUserParams("pride_gel_param",gel_id));
                gel = new Gel(params, rs.getString("gel_link"), x_coordinate, y_coordinate, molecular_weight, pi);
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return gel;
    }
    /*
    ParamGroup params, String id, String name, String sequence,
                   BigInteger start, BigInteger end, List<Modification> modifications,
                   List<ParamGroup> fragmentIons, Spectrum spectrum
     */
    List<Peptide> getPeptideIdentification(int identification_id, int experiment_id) throws DataAccessException {
        List<Peptide> peptides = new ArrayList<Peptide>();
        List<Modification> modifications;
        List<ParamGroup> fragmentIons;
        Spectrum spectrum;
        ParamGroup params;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT peptide_id, sequence, pep_start, pep_end, spectrum_ref FROM pride_peptide WHERE identification_id = ?");
            st.setInt(1, identification_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                params = new ParamGroup(getCvParams("pride_peptide_param", rs.getInt("peptide_id")), getUserParams("pride_peptide_param", rs.getInt("peptide_id")));
                modifications = getModificationsPeptide(rs.getInt("peptide_id"));
                fragmentIons = getFragmentIons(rs.getInt("peptide_id"));
                spectrum = getSpectrumByPeptide(experiment_id, rs.getInt("spectrum_ref"));
                peptides.add(new Peptide(params, "", "", rs.getString("sequence"), new BigInteger(Integer.toString(rs.getInt("pep_start"))),
                        new BigInteger(Integer.toString(rs.getInt("pep_end"))), modifications, fragmentIons, spectrum));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return peptides;
    }

      Spectrum getSpectrumByPeptide(int experiment_id, int spectrum_ref) throws DataAccessException {
        Spectrum spectrum = null;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT spectrum_id FROM pride_experiment pe, mzdata_spectrum ms WHERE pe.experiment_id = ? AND " +
                    "pe.mz_data_id = ms.mz_data_id AND ms.spectrum_identifier = ?");
            st.setInt(1, experiment_id);
            st.setInt(2, spectrum_ref);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                spectrum = getSpectrumById(Integer.toString(rs.getInt("spectrum_id")));    
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return spectrum;
    }
    List<ParamGroup> getFragmentIons(int peptide_id){
        List<ParamGroup> fragmentIons = new ArrayList<ParamGroup>();

        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT fragment_ion_id FROM pride_fragment_ion WHERE peptide_id = ?");
            st.setInt(1, peptide_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                fragmentIons.add(new ParamGroup(getCvParams("pride_fragment_ion_param", rs.getInt("fragment_ion_id")), getUserParams("pride_fragment_ion_param", rs.getInt("fragment_ion_id"))));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return fragmentIons;
    }

    /*
        ParamGroup params, String accession,
        String modDatabase, String modDatabaseVersion,
        List<Double> monoMassDeltas, List<Double> avgMassDeltas,
        BigInteger location
     */
    List<Modification> getModificationsPeptide(int peptide_id){
        List<Modification> modifications = new ArrayList<Modification>();
        List<Double> monoMassDeltas;
        List<Double> avgMassDeltas;
        ParamGroup params;

        try{
            PreparedStatement st = DBConnection.prepareStatement("SELECT modification_id, accession, mod_database, mod_database_version, location FROM pride_modification WHERE peptide_id = ?");
            st.setInt(1, peptide_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                params = new ParamGroup(getCvParams("pride_modification_param", rs.getInt("modification_id")), getUserParams("pride_modification_param",
                            rs.getInt("modification_id")));
                monoMassDeltas =getDeltaValues(rs.getInt("modification_id"),"uk.ac.ebi.pride.rdbms.ojb.model.core.MonoMassDeltaBean");
                avgMassDeltas = getDeltaValues(rs.getInt("modification_id"),"uk.ac.ebi.pride.rdbms.ojb.model.core.AverageMassDeltaBean");
                modifications.add(new Modification(params, rs.getString("accession"), rs.getString("mod_database"), rs.getString("mod_database_version"), monoMassDeltas, avgMassDeltas,
                        new BigInteger(Integer.toString(rs.getInt("location")))));
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return modifications;
    }

    List<Double> getDeltaValues(int modification_id, String deltaType){
        List<Double> deltas = new ArrayList<Double>();

        try{
            PreparedStatement st = DBConnection.prepareStatement("SELECT mass_delta_value FROM pride_mass_delta WHERE modification_id = ? AND classname = ?");
            st.setInt(1, modification_id);
            st.setString(2, deltaType);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                deltas.add(rs.getDouble("mass_delta_value"));   
            }
            rs.close();
        }
        catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        
        return deltas;
    }

    public List<String> getGelFreeIdentIds() throws DataAccessException {

        List<String> array = new ArrayList<String>();
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT accession_number FROM pride_identification WHERE classname = 'uk.ac.ebi.pride.rdbms.ojb.model.core.GelFreeIdentificationBean' LIMIT 4");
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                //array.add(Integer.toString(rs.getInt("identification_id")));
                array.add(rs.getString("accession_number"));
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return array;
    }

    /*
            (String accession, String accessionVersion,
             List<Peptide> peptides, double score,
             String searchDatabase, String searchDatabaseVerison,
             String searchEngine, double sequenceConverage,
             Spectrum spectrum, String spliceIsoform,
             double threshold, ParamGroup params)
     */
    public GelFreeIdentification getGelFreeIdentById(String id) throws DataAccessException {

        List<Peptide> peptides;
        Spectrum spectrum = null;
        ParamGroup params;
        GelFreeIdentification gelFreeIdentification = null;
        try {
            PreparedStatement st = DBConnection.prepareStatement("SELECT accession_number, accession_version, score, search_database, database_version, " +
                    "search_engine, sequence_coverage, splice_isoform, threshold, experiment_id, identification_id FROM pride_identification WHERE accession_number = ?");
            st.setString(1, id);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                params = new ParamGroup(getCvParams("pride_identification_param", rs.getInt("identification_id")), getUserParams("pride_identification_param",rs.getInt("identification_id")));
                peptides = getPeptideIdentification(rs.getInt("identification_id"), rs.getInt("experiment_id"));

                gelFreeIdentification = new GelFreeIdentification(rs.getString("accession_number"), rs.getString("accession_version"), peptides, rs.getDouble("score"),
                        rs.getString("search_database"), rs.getString("database_version"), rs.getString("search_engine"), rs.getDouble("sequence_coverage"),
                        spectrum, rs.getString("splice_isoform"), rs.getDouble("threshold"), params);
            }
            rs.close();
        } catch (SQLException e) {
            LoggerUtils.error(logger, this, e);
        }
        return gelFreeIdentification;
    }

}
