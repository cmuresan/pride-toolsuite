package uk.ebi.pride.jmzidentml.validator.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.jmzidml.model.mzidml.Cv;
import uk.ac.ebi.jmzidml.model.mzidml.SpectraData;
import uk.ebi.pride.jmzidentml.validator.controller.utils.*;

import javax.naming.ConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The MzIdentMLControllerImpl is the controller that retrieve the information from
 * the mzidentml files. It have support for Experiment Metadata (Global metadata),
 * also it have information about the IdentificationMetadata. The MzGraphMetaData is not
 * supported for this files because they not contains information about spectrums. The controller
 * support the mzidentml schema version 1.1.
 * <p/>
 * User: yperez
 * Date: 19/09/11
 * Time: 16:08
 */

public class MzIdentMLControllerImpl extends AbstractMzIdentMLDataAccessController {

    // Logger property to trace the Errors
    private static final Logger logger = LoggerFactory.getLogger(MzIdentMLControllerImpl.class);

    //The unmarshller class that retrieve the information from the mzidentml files
    private MzIdentMLUnmarshallerAdaptor unmarshaller;

    // The Match pattern for a valid mzidentml file, its support now the version 1.1.
    private static final Pattern mzIdentMLHeaderPattern = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*)?<(MzIdentML)|(indexedmzIdentML) xmlns=.*", Pattern.MULTILINE);



    /*
      * This is a set of controllers related with the MS information in the mzidentml file
      * one or more controllers can be related with the same file formats. The Comparable
      * name of the file is an id of the file and the controller is the DataAccessController
       * related with the file.
     */
    private Map<Comparable, File> msDataAccessControllers;

    /* MZidentML file format version*/
    private String MZIDENTML_ACCEPTED_VERSION = "1.1.0";


    public MzIdentMLControllerImpl(File file) {
        super(file);
        initialize();
    }

    /**
     * This function initialize all the Categories in which the Controller
     * used the Cache System. In this case it wil be use cache for PROTEIN,
     * PEPTIDE, SAMPLE and SOFTWARE.
     */
    protected void initialize() {
        // create pride access utils
        File file = (File) getSource();
        try {
            unmarshaller = new MzIdentMLUnmarshallerAdaptor(file);
        } catch (ConfigurationException e) {
            String msg = "Failed to create XML unmarshaller for mzIdentML file: " + file.getAbsolutePath();
            throw new DataAccessException(msg, e);
        }

        // init ms data accession controller map
        this.msDataAccessControllers = new HashMap<Comparable, File>();

        // set data source description
        this.setName(file.getName());

        populateCache();

    }

    /**
     * Return the mzidentml unmarshall adaptor to be used by the CacheBuilder
     * Implementation.
     *
     * @return MzIdentMLUnmarshallerAdaptor
     */
    public MzIdentMLUnmarshallerAdaptor getUnmarshaller() {
        return unmarshaller;
    }

    /**
     * Get the unique id of the data access controller
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
                String msg = "Failed to generate unique id for mzIdentML file";
                logger.error(msg, e);
            }
        }
        return uid;
    }

    /**
     * Get a list of cv lookup objects.
     *
     * @return List<CVLookup>   a list of cvlookup objects.
     */
    public List<Cv> getCvLookups() {
        return unmarshaller.getCvList();
    }

    /**
     * Get the List of File Spectras that the Mzidentml use to identified peptides
     *
     * @return
     */
    public List<SpectraData> getSpectraDataFiles() {
       return unmarshaller.getSpectraData();
    }

    /**
     * The MzGraphMetadata is not supported by mzidentml.
     *
     * @return
     */
    @Override
    public boolean hasSpectrum() {
        if (msDataAccessControllers != null) {
            for (Comparable id : msDataAccessControllers.keySet()) {
                if (msDataAccessControllers.get(id) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check a file is MZIdentML XML file
     *
     * @param file input file
     * @return boolean true means MZIdentML XML
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
            // check file type
            Matcher matcher = mzIdentMLHeaderPattern.matcher(content);
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


    private String getInvalidMzIdentMLVersionWarning(List<File> mzIdentMLFiles) {
        StringBuilder errMsg = new StringBuilder();
        errMsg.append("<html>");
        errMsg.append("<b>The following mzIdentML are not version " + MZIDENTML_ACCEPTED_VERSION + "</b><br/>");
        for (File mzIdentMLFile : mzIdentMLFiles) {
            errMsg.append("<li>");
            errMsg.append(mzIdentMLFile.getName());
            errMsg.append("</li>");
        }
        errMsg.append("</html>");

        return errMsg.toString();
    }


    public void addMSController(List<File> dataAccessControllerFiles) {
        Map<SpectraData, File> spectraDataFileMap = checkMScontrollers(dataAccessControllerFiles);

        for (SpectraData spectraData : spectraDataFileMap.keySet()) {
            File peakListController = spectraDataFileMap.get(spectraData);
            msDataAccessControllers.put(spectraData.getId(), peakListController);
        }
    }

    public Map<SpectraData, File> checkMScontrollers(List<File> mzIdentMLFiles) {

        Map<Comparable, SpectraData> spectraDataMap = getSpectraDataMap();

        Map<SpectraData, File> spectraFileMap = new HashMap<SpectraData, File>();

        for (File file : mzIdentMLFiles) {
            for (Comparable id : spectraDataMap.keySet()) {
                SpectraData spectraData = spectraDataMap.get(id);
                if (spectraData.getLocation().indexOf(file.getName()) > 0) {
                    spectraFileMap.put(spectraData, file);
                }
            }
        }
        return spectraFileMap;
    }

    public void addMSController(Map<SpectraData, File> spectraDataFileMap) {

        Map<SpectraData, File> spectraDataControllerMap = getSpectraDataMSFiles();

        for (SpectraData spectraData : spectraDataControllerMap.keySet()) {
            for (SpectraData spectraDataFile : spectraDataFileMap.keySet()) {
                if (spectraDataControllerMap.get(spectraData) == null && spectraData.getId().equals(spectraDataFile.getId())) {
                    if (MzIdentMLUtils.getSpectraDataFormat(spectraData) == Constants.SpecFileFormat.MZXML)
                        msDataAccessControllers.put(spectraData.getId(), spectraDataFileMap.get(spectraDataFile));
                    if (MzIdentMLUtils.getSpectraDataFormat(spectraData) == Constants.SpecFileFormat.MGF)
                        msDataAccessControllers.put(spectraData.getId(), spectraDataFileMap.get(spectraDataFile));
                    if (MzIdentMLUtils.getSpectraDataFormat(spectraData) == Constants.SpecFileFormat.MZML)
                        msDataAccessControllers.put(spectraData.getId(), spectraDataFileMap.get(spectraDataFile));
                    if (MzIdentMLUtils.getSpectraDataFormat(spectraData) == Constants.SpecFileFormat.DTA)
                        msDataAccessControllers.put(spectraData.getId(), spectraDataFileMap.get(spectraDataFile));
                }
            }

        }
    }

    private Map<Comparable, SpectraData> getSpectraDataMap() {
        Map<Comparable, SpectraData> spectraDataMapResult = (Map<Comparable, SpectraData>) getCache();
        if (spectraDataMapResult == null) {
            return Collections.emptyMap();
        } else {
            return spectraDataMapResult;
        }
    }

    public Map<SpectraData, File> getSpectraDataMSControllers() {
        Map<Comparable, SpectraData> spectraDataMap = getSpectraDataMap();
        Map<SpectraData, File> mapResult = new HashMap<SpectraData, File>();

        for (Comparable id : spectraDataMap.keySet()) {
            if (msDataAccessControllers.containsKey(id)) {
                mapResult.put(spectraDataMap.get(id), msDataAccessControllers.get(id));
            } else {
                mapResult.put(spectraDataMap.get(id), null);
            }
        }
        return mapResult;
    }

    public Map<SpectraData, File> getSpectraDataMSFiles() {
        Map<SpectraData, File> spectraDataControllerMAp = getSpectraDataMSControllers();
        Map<SpectraData, File> spectraDataFileMap = new HashMap<SpectraData, File>();
        for (SpectraData spectraData : spectraDataControllerMAp.keySet()) {
            File controller = spectraDataControllerMAp.get(spectraData);
            spectraDataFileMap.put(spectraData, (controller == null) ? null : (File) controller);
        }
        return spectraDataFileMap;
    }

    public List<File> getSpectrumDataAccessControllers() {
        return new ArrayList<File>(msDataAccessControllers.values());
    }

    @Override
    public void close() {
        unmarshaller = null;
        super.close();

    }

    @Override
    public void populateCache() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public Object getCache() {
        return null;
    }


}
