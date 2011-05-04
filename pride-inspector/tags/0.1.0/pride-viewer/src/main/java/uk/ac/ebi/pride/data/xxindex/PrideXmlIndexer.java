package uk.ac.ebi.pride.data.xxindex;

import org.apache.log4j.Logger;
import psidev.psi.tools.xxindex.index.IndexElement;
import uk.ac.ebi.pride.data.utils.PatternUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Mar-2010
 * Time: 11:25:43
 */
public class PrideXmlIndexer extends AbstractXmlIndexer{
    private static final Logger logger = Logger.getLogger(PrideXmlIndexer.class.getName());
    /** pattern to match experiment accession */
    private static final Pattern ACCESSION_PATTERN = Pattern.compile("\\s*\\<(Experiment)*Accession\\>\\s*([^\\s]+)\\s*\\<\\/(Experiment)*Accession\\>\\s*", Pattern.CASE_INSENSITIVE);
    /** pattern to match id, cvLabel or Accessioin */
    private static final Pattern ID_PATTERN = Pattern.compile("\\s(id|cvLabel)\\s*=\\s*['\"]([^'\"]*)['\"]", Pattern.CASE_INSENSITIVE);

    /** these maps provide quick mapping from id/accession to their byte range in the input file */
    private Map<String, IndexElement> experimentAccMap = null;
    private Map<String, Map<String, IndexElement>> cvIdMap = null;
    private Map<String, Map<String, IndexElement>> spectrumIdMap = null;
    private Map<String, Map<String, IndexElement>> gelFreeAccMap = null;
    private Map<String, Map<String, IndexElement>> twoDimAccMap = null;

    /** create a list of xpaths, add them into a xpath list */
    public static final String EXPERIMENT = "/ExperimentCollection/Experiment";
    public static final String EXPERIMENT_ACCESSION = "/ExperimentCollection/Experiment/ExperimentAccession";
    public static final String EXPERIMENT_TITLE = "/ExperimentCollection/Experiment/Title";
    public static final String EXPERIMENT_REFERENCE = "/ExperimentCollection/Experiment/Reference";
    public static final String EXPERIMENT_SHORT_LABEL = "/ExperimentCollection/Experiment/ShortLabel";
    public static final String EXPERIMENT_PROTOCOL = "/ExperimentCollection/Experiment/Protocol";
    public static final String EXPERIMENT_ADDITIONAL_PARAM = "/ExperimentCollection/Experiment/additional";
    public static final String EXPERIMENT_MZDATA = "/ExperimentCollection/Experiment/mzData";
    public static final String EXPERIMENT_CV_LOOKUP = "/ExperimentCollection/Experiment/mzData/cvLookup";
    public static final String EXPERIMENT_ADMIN = "/ExperimentCollection/Experiment/mzData/description/admin";
    public static final String EXPERIMENT_INSTRUMENT = "/ExperimentCollection/Experiment/mzData/description/instrument";
    public static final String EXPERIMENT_DATA_PROCESSING = "/ExperimentCollection/Experiment/mzData/description/dataprocessing";
    public static final String EXPERIMENT_SPECTRUM = "/ExperimentCollection/Experiment/mzData/spectrumList/spectrum";
    public static final String EXPERIMENT_GEL_FREE_IDENTIFICATION = "/ExperimentCollection/Experiment/GelFreeIdentification";
    public static final String EXPERIMENT_TWO_DIMENSIONAL_IDENTIFICATION = "/ExperimentCollection/Experiment/TwoDimensionalIdentification";

    private static Collection<String> xpaths = new ArrayList<String>();
    
    static {
        xpaths.add(EXPERIMENT);
        xpaths.add(EXPERIMENT_ACCESSION);
        xpaths.add(EXPERIMENT_TITLE);
        xpaths.add(EXPERIMENT_REFERENCE);
        xpaths.add(EXPERIMENT_SHORT_LABEL);
        xpaths.add(EXPERIMENT_PROTOCOL);
        xpaths.add(EXPERIMENT_ADDITIONAL_PARAM);
        xpaths.add(EXPERIMENT_MZDATA);
        xpaths.add(EXPERIMENT_CV_LOOKUP);
        xpaths.add(EXPERIMENT_ADMIN);
        xpaths.add(EXPERIMENT_INSTRUMENT);
        xpaths.add(EXPERIMENT_DATA_PROCESSING);
        xpaths.add(EXPERIMENT_SPECTRUM);
        xpaths.add(EXPERIMENT_GEL_FREE_IDENTIFICATION);
        xpaths.add(EXPERIMENT_TWO_DIMENSIONAL_IDENTIFICATION);
    }

    /**
     * Constructor 
     * @param prideXmlFile  the input pride xml file
     */
    public PrideXmlIndexer(File prideXmlFile) {
        super(prideXmlFile, xpaths);
        try {
            initMapCaches();
        }catch(IOException ioe) {
            logger.error("PrideXmlIndexer error while constructing id/accession caches", ioe);
            throw new IllegalStateException("PrideXmlIndexer error: " + ioe);
        }
    }

    /**
     * build id/accession to byte range mappings
     * @throws IOException
     */
    private void initMapCaches() throws IOException{

        logger.info("build experiment accession cache");
        experimentAccMap = initExperimentAccMappedCache();

        logger.info("build cv cache");
        cvIdMap = initExperimentMappedCache(EXPERIMENT_CV_LOOKUP, ID_PATTERN, 2);

        logger.info("build spectra cache");
        spectrumIdMap = initExperimentMappedCache(EXPERIMENT_SPECTRUM, ID_PATTERN, 2);

        logger.info("build gel free cache");
        gelFreeAccMap = initExperimentMappedCache(EXPERIMENT_GEL_FREE_IDENTIFICATION, ACCESSION_PATTERN, 2);

        logger.info("build two dimensional cache");
        twoDimAccMap = initExperimentMappedCache(EXPERIMENT_TWO_DIMENSIONAL_IDENTIFICATION, ACCESSION_PATTERN, 2);

    }

    private Map<String, IndexElement> initExperimentAccMappedCache() throws IOException {
        Map<String, IndexElement> accMap = new LinkedHashMap<String, IndexElement>();

        List<IndexElement> expList = this.getIndexElements(EXPERIMENT);
        List<IndexElement> expAccList = this.getIndexElements(EXPERIMENT_ACCESSION);
        if (expAccList.size() > 0) {
            for(IndexElement expAcc : expAccList) {
                long expAccStart = expAcc.getStart();
                long expAccEnd = expAcc.getStop();
                String xml = this.readXml(expAcc);
                String acc = PatternUtils.getMatchedString(ACCESSION_PATTERN, xml, 2);
                for(IndexElement exp : expList) {
                    if (expAccStart >= exp.getStart() && expAccEnd <= exp.getStop()) {
                        accMap.put(acc, exp);
                    }
                }
            }
        } else {
            int count = 1;
            for(IndexElement exp : expList) {
                accMap.put(count+"", exp);
                count++;
            }
        }

        return accMap;
    }

    /**
     * This is method is essential for creating correct mapping caches
     * Note: one pride xml file might have multiple experiments
     * @param xpath xpath to the IDs need to be cached
     * @return Map<String, Map<String, IndexElement>> Map<ExperimentAccession, Map<ID, ByteRange>>
     * @throws IOException
     */
    private Map<String, Map<String, IndexElement>> initExperimentMappedCache(String xpath, Pattern pattern, int offset) throws IOException{
        Map<String, Map<String, IndexElement>> expMap = new LinkedHashMap<String, Map<String, IndexElement>>();
        Map<IndexElement, String> idMap = initMapCache(xpath, pattern, offset);

        Set<Map.Entry<IndexElement, String>> ids = idMap.entrySet();
        for(Map.Entry<IndexElement, String> idEntry : ids) {
            String id = idEntry.getValue();
            IndexElement element = idEntry.getKey();
            long startRange = element.getStart();
            Set<Map.Entry<String, IndexElement>> expAccEntrys = experimentAccMap.entrySet();
            for(Map.Entry<String, IndexElement> expAccEntry : expAccEntrys) {
                String expAcc = expAccEntry.getKey();
                IndexElement expElement = expAccEntry.getValue();
                long sr = expElement.getStart();
                long er = expElement.getStop();
                if (startRange>sr && startRange<er) {
                    Map<String, IndexElement> innerMap = expMap.get(expAcc);
                    if (innerMap == null) {
                        innerMap = new LinkedHashMap<String, IndexElement>();
                        expMap.put(expAcc, innerMap);
                    }
                    innerMap.put(id, element);
                    break;
                }
            }
        }
        return expMap;
    }

    /**
     * Get a list of experiment accessions from the pride xml file
     * @return List<String> a list of experiment accessions
     */
    public List<String> getExperimentIds() {
        return new ArrayList<String>(experimentAccMap.keySet());
    }

    /**
     * Get the experiment title
     * @param expAcc experiment accession
     * @return String experiment title, only one is allowed
     * @throws IOException
     */
    public String getTitleXmlString(String expAcc) throws IOException {
        return getFirstXmlString(EXPERIMENT_TITLE, expAcc);
    }

    /**
     * Mulitple references can be available in one pride experiment.
     * @param expAcc experiment accession
     * @return
     * @throws IOException
     */
    public List<String> getReferenceXmlStrings(String expAcc) throws IOException {
        IndexElement range = experimentAccMap.get(expAcc);
        return this.getXmlStringWithinRange(EXPERIMENT_REFERENCE, range);
    }

    /**
     * Get the short label of the experiment
     * @param expAcc experiment accession
     * @return String short label string
     * @throws IOException
     */
    public String getShortLabelXmlString(String expAcc) throws IOException {
        return getFirstXmlString(EXPERIMENT_SHORT_LABEL, expAcc);
    }

    public String getProtocolXmlString(String expAcc) throws IOException {
        return getFirstXmlString(EXPERIMENT_PROTOCOL, expAcc);
    }

    public String getAdditionalParamXmlString(String expAcc) throws IOException {
        return getFirstXmlString(EXPERIMENT_ADDITIONAL_PARAM, expAcc);
    }

    public String getAdminXmlString(String expAcc) throws IOException {
        return getFirstXmlString(EXPERIMENT_ADMIN, expAcc);
    }

    public String getInstrumentXmlString(String expAcc) throws IOException {
        return getFirstXmlString(EXPERIMENT_INSTRUMENT, expAcc);
    }

    public String getDataProcessingXmlString(String expAcc) throws IOException {
        return getFirstXmlString(EXPERIMENT_DATA_PROCESSING, expAcc);
    }

    private String getFirstXmlString(String xpath, String expAcc) throws IOException {
        IndexElement range = experimentAccMap.get(expAcc);
        List<String> xmlStrs = this.getXmlStringWithinRange(xpath,range);
        String xml = null;
        if (xmlStrs != null && !xmlStrs.isEmpty()) {
            xml = xmlStrs.get(0);
        }
        return xml;
    }

    public List<String> getCvLookupIds(String expAcc) {
        List<String> result = null;

        Map<String, IndexElement> idMap = cvIdMap.get(expAcc);
        if (idMap != null) {
            result = new ArrayList<String>(cvIdMap.keySet());
        }
        
        return result;
    }

    public String getCvLookupXmlString(String expAcc, String id) throws IOException{
        String result = null;

        Map<String, IndexElement> idMap = cvIdMap.get(expAcc);

        if (idMap != null) {
            result = readXml(idMap.get(id));
        }
        
        return result;
    }

    public List<String> getSpectrumIds(String expAcc) {
        List<String> result = null;
        Map<String, IndexElement> idMap = spectrumIdMap.get(expAcc);

        if (idMap != null) {
            result = new ArrayList<String>(idMap.keySet());
        }
        
        return result;
    }

    public String getSpectrumXmlString(String expAcc, String id) throws IOException {
        String result = null;

        Map<String, IndexElement> idMap = spectrumIdMap.get(expAcc);

        if (idMap != null) {
            result = readXml(idMap.get(id));
        }

        return result;
    }

    public List<String> getGelFreeIdentAccs(String expAcc) {
        List<String> result = null;
        Map<String, IndexElement> idMap = gelFreeAccMap.get(expAcc);

        if (idMap != null) {
            result = new ArrayList<String>(idMap.keySet());
        }

        return result;
    }

    public String getGelFreeIdentXmlString(String expAcc, String acc) throws IOException {
        String result = null;

        Map<String, IndexElement> idMap = gelFreeAccMap.get(expAcc);

        if (idMap != null) {
            result = readXml(idMap.get(acc));
        }

        return result;
    }

    public List<String> getTwoDimIdentAccs(String expAcc) {
        List<String> result = null;
        Map<String, IndexElement> idMap = twoDimAccMap.get(expAcc);

        if (idMap != null) {
            result = new ArrayList<String>(idMap.keySet());
        }

        return result;
    }

    public String getTwoDimIdentXmlString(String expAcc, String acc) throws IOException {
        String result = null;

        Map<String, IndexElement> idMap = twoDimAccMap.get(expAcc);

        if (idMap != null) {
            result = readXml(idMap.get(acc));
        }

        return result;
    }
}
