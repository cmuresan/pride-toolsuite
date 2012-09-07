package uk.ac.ebi.pride.data.io.file;


import org.apache.batik.ext.awt.image.renderable.CompositeRable;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.model.mzidml.Organization;
import uk.ac.ebi.jmzidml.model.mzidml.Peptide;
import uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidence;
import uk.ac.ebi.jmzidml.model.mzidml.Person;
import uk.ac.ebi.jmzidml.model.mzidml.Provider;
import uk.ac.ebi.jmzidml.model.mzidml.Sample;
import uk.ac.ebi.jmzidml.model.mzidml.SourceFile;
import uk.ac.ebi.jmzidml.model.mzidml.SpectraData;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationProtocol;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.utils.MzIdentMLUtils;


import javax.crypto.Mac;
import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * MzMLIdentMLUnmarshallerAdaptor provides a list of convenient
 * methods to access mzidentML files.
 * <p/>
 * User: yperez, rwang
 * Date: 23/09/11
 * Time: 15:28
 */
public class MzIdentMLUnmarshallerAdaptor {

    private MzIdentMLUnmarshaller unmarshaller = null;

    public MzIdentMLUnmarshallerAdaptor(MzIdentMLUnmarshaller um) {
        this.unmarshaller = um;
    }

    public String getMzIdentMLId() {
        return unmarshaller.getMzIdentMLId();
    }

    public String getMzIdentMLVersion() {
        return unmarshaller.getMzIdentMLVersion();
    }

    public List<Sample> getSampleList() {
        uk.ac.ebi.jmzidml.model.mzidml.AnalysisSampleCollection asc =
            unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.AnalysisSampleCollection.class);
        return (asc!=null)?asc.getSample():null;
    }

    public List<SourceFile> getSourceFiles() {
        uk.ac.ebi.jmzidml.model.mzidml.Inputs dc = unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.Inputs.class);

        return dc.getSourceFile();
    }

    public List<AnalysisSoftware> getSoftwares() {
        uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftwareList asl =
            unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftwareList.class);

        return asl.getAnalysisSoftware();
    }

    public List<Person> getPersonContacts() {
        uk.ac.ebi.jmzidml.model.mzidml.AuditCollection ac =
            unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.AuditCollection.class);

        return (ac != null)?ac.getPerson():null;
    }

    public List<Organization> getOrganizationContacts() {
        uk.ac.ebi.jmzidml.model.mzidml.AuditCollection ac =
            unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.AuditCollection.class);

        return (ac != null)? ac.getOrganization():null;
    }

    public Iterator<BibliographicReference> getReferences() {
        return unmarshaller.unmarshalCollectionFromXpath(uk.ac.ebi.jmzidml.MzIdentMLElement.BibliographicReference);
    }

    public ProteinDetectionHypothesis getIdentificationById(Comparable IdentId) throws JAXBException {
        return unmarshaller.unmarshal(ProteinDetectionHypothesis.class,(String) IdentId);
    }

    public int getNumIdentifiedPeptides() throws ConfigurationException, JAXBException {
        int total = 0;
        Set<String> listIDs = unmarshaller.getIDsForElement(MzIdentMLElement.SpectrumIdentificationItem);
        return listIDs.size();
    }

    public FragmentationTable getFragmentationTable() {
        return unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable.class);
    }

    public Set<String> getIDsForElement(MzIdentMLElement mzIdentMLElement) throws ConfigurationException {
        return unmarshaller.getIDsForElement(mzIdentMLElement);
    }

    public List<Cv> getCvList() {
        return (unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.CvList.class)).getCv();
    }

    public String getMzIdentMLName() {
        Map<String, String> properties = unmarshaller.getElementAttributes(unmarshaller.getMzIdentMLId(),
                                             uk.ac.ebi.jmzidml.model.mzidml.MzIdentML.class);

        /*
         * This is the only way that we can use now to retrieve the name property
         * In the future we need to think in more elaborated way.
         */
        return (properties.containsKey("name"))  ? properties.get("name") : "Unknown experiment (mzIdentML)";
    }

    public Date getCreationDate(){
        Map<String, String> properties = unmarshaller.getElementAttributes(unmarshaller.getMzIdentMLId(),
                                             uk.ac.ebi.jmzidml.model.mzidml.MzIdentML.class);

        /*
         * This is the only way that we can use now to retrieve the name property
         * In the future we need to think in more elaborated way.
         */
        Date dateCreation = null;
        if(properties.containsKey("creationDate")){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd");
                Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(properties.get("creationDate"));
                dateCreation = calendar.getTime();
        }
        return dateCreation;

    }

    public Provider getProvider() {
        return unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.Provider.class);
    }

    public List<SpectrumIdentificationProtocol> getSpectrumIdentificationProtcol() {
        return (unmarshaller.unmarshal(AnalysisProtocolCollection.class)).getSpectrumIdentificationProtocol();
    }

    public ProteinDetectionProtocol getProteinDetectionProtocol() {
        return unmarshaller.unmarshal(ProteinDetectionProtocol.class);
    }

    public List<SearchDatabase> getSearchDatabases() {
        uk.ac.ebi.jmzidml.model.mzidml.Inputs dc = unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.Inputs.class);

        return dc.getSearchDatabase();
    }

    public List<SpectraData> getSpectraData() {
        Inputs dc = unmarshaller.unmarshal(Inputs.class);
        return dc.getSpectraData();
    }

    public Map<Comparable, SpectraData> getSpectraDataMap() {
        Inputs dc = unmarshaller.unmarshal(Inputs.class);
        List<SpectraData> spectraDataList =  dc.getSpectraData();
        Map<Comparable,SpectraData> spectraDataMap = null;
        if(spectraDataList != null && spectraDataList.size()>0) spectraDataMap = new HashMap<Comparable, SpectraData>();
        for(SpectraData spectraData: spectraDataList){
            spectraDataMap.put(spectraData.getId(),spectraData);
        }
        return spectraDataMap;
    }


    public Map<Comparable,SpectraData> getSpectraData(Set<Comparable> ids) throws JAXBException {
        Map<Comparable, SpectraData> spectraDataMap = new HashMap<Comparable, SpectraData>();
        for(Comparable id: ids){
            SpectraData spectraData = unmarshaller.unmarshal(SpectraData.class, (String) id);
            spectraDataMap.put(id,spectraData);
        }
        return spectraDataMap;
    }

    public List<PeptideHypothesis> getPeptideHypothesisbyID(Comparable id) throws JAXBException {
        ProteinDetectionHypothesis proteinDetectionHypothesis = getIdentificationById(id);
        List<PeptideHypothesis> peptideHypothesises = new ArrayList<PeptideHypothesis>();
        for(PeptideHypothesis peptideHypothesis : proteinDetectionHypothesis.getPeptideHypothesis()){
            PeptideEvidence peptideEvidence = unmarshaller.unmarshal(PeptideEvidence.class,peptideHypothesis.getPeptideEvidenceRef());
            Peptide peptide = unmarshaller.unmarshal(Peptide.class,peptideEvidence.getPeptideRef());
            peptideEvidence.setPeptide(peptide);
            peptideHypothesis.setPeptideEvidence(peptideEvidence);
            peptideHypothesises.add(peptideHypothesis);
        }
        return peptideHypothesises;
    }

    public PeptideEvidence getPeptideEvidence(Comparable id) throws JAXBException {
          return unmarshaller.unmarshal(PeptideEvidence.class,(String) id);
    }

    public Map<Comparable,Comparable> getPeptideEvidencesBySpectrum(Comparable id) throws JAXBException {
        List<PeptideEvidenceRef> peptideEvidenceRefs = unmarshaller.unmarshal(SpectrumIdentificationItem.class,(String) id).getPeptideEvidenceRef();
        Map<Comparable,Comparable> peptideRefs = new HashMap<Comparable, Comparable>(peptideEvidenceRefs.size());
        for(PeptideEvidenceRef peptideEvidenceRef: peptideEvidenceRefs){
            peptideRefs.put(peptideEvidenceRef.getPeptideEvidenceRef(), peptideEvidenceRef.getPeptideEvidence().getDBSequenceRef());
        }
        return peptideRefs;
    }

    public Comparable getDBSequencebyProteinHypothesis(Comparable id) throws JAXBException
    {
        return unmarshaller.unmarshal(ProteinDetectionHypothesis.class,(String) id).getDBSequenceRef();
    }

    public DBSequence getDBSequenceById(Comparable id) throws JAXBException {
        return unmarshaller.unmarshal(DBSequence.class,(String) id);
    }

    public List<SpectrumIdentificationItem> getSpectrumIdentificationsbyIds(List<Comparable> spectrumIdentIds) throws JAXBException {
        List<SpectrumIdentificationItem> spectrumIdentifications = null;
        if(spectrumIdentIds != null && spectrumIdentIds.size() > 0){
            spectrumIdentifications = new ArrayList<SpectrumIdentificationItem>();
            for(Comparable id: spectrumIdentIds){
                SpectrumIdentificationItem spectrumIdentification = unmarshaller.unmarshal(SpectrumIdentificationItem.class, (String) id);
                spectrumIdentifications.add(spectrumIdentification);
            }

        }
        return spectrumIdentifications;
    }

    public Map<Comparable,String[]> getSpectrumIdentificationsbySpectrumResult(Comparable id) throws JAXBException {
        ArrayList<SpectrumIdentificationItem> spectrumIdentificationItems = null;
        SpectrumIdentificationResult results  = unmarshaller.unmarshal(SpectrumIdentificationResult.class, (String) id);
        Map<Comparable, String[]> spectrumIds = new HashMap<Comparable, String[]>(results.getSpectrumIdentificationItem().size());
        for(SpectrumIdentificationItem idSpectrumItem: results.getSpectrumIdentificationItem()){
            String[] spectrumInfo = new String[2];
            spectrumInfo[0] = results.getSpectrumID();
            spectrumInfo[1] = results.getSpectraDataRef();
            spectrumIds.put(idSpectrumItem.getId(),spectrumInfo);
        }
        return spectrumIds;  //To change body of created methods use File | Settings | File Templates.
    }

    public Map<CacheCategory, Object> getPreScanIdMaps() throws ConfigurationException, JAXBException {

        Map<CacheCategory, Object> maps = new HashMap<CacheCategory, Object>();

        long time = System.currentTimeMillis();


        Map<Comparable, SpectraData> spectraDataIds = getSpectraDataMap();

        System.out.println("1:" + String.valueOf(System.currentTimeMillis() - time));

        time = System.currentTimeMillis();


        // First Map is the Relation bettwen an Spectrum file and all the Spectrums ids in the file
        Map<Comparable, List<Comparable>> spectraDataMap = new HashMap<Comparable, List<Comparable>>(spectraDataIds.size());


        Map<Comparable, String[]> identSpectrumMap = new HashMap<Comparable, String[]>();


        ArrayList<Comparable> spectrumMatchResultsIds  = new ArrayList<Comparable>(unmarshaller.getIDsForElement(MzIdentMLElement.SpectrumIdentificationResult));

        System.out.println("2:" + String.valueOf(System.currentTimeMillis() - time));

        time = System.currentTimeMillis();


        Map<Comparable,Map<Comparable,List<String[]>>> identProteinsMap = new HashMap<Comparable, Map<Comparable, List<String[]>>>();



        for(Comparable idSpectrumResult: spectrumMatchResultsIds){
            SpectrumIdentificationResult spectrumResult = unmarshaller.unmarshal(SpectrumIdentificationResult.class,(String) idSpectrumResult);

            // SpectraDataMap fill
            List<Comparable> spectrumIds = spectraDataMap.get(spectrumResult.getSpectraDataRef());
            if(spectrumIds == null) spectrumIds = new ArrayList<Comparable>(1);
            spectrumIds.add(spectrumResult.getSpectrumID());
            spectraDataMap.put(spectrumResult.getSpectraDataRef(),spectrumIds);

            for(SpectrumIdentificationItem spectrumIdentItem: spectrumResult.getSpectrumIdentificationItem()){

                // fill the SpectrumIdentification and the Spectrum information
                String[] spectrumFeatures = new String[2];
                SpectraData spectraData = spectraDataIds.get(spectrumResult.getSpectraDataRef());

                String spectrumId   = MzIdentMLUtils.getSpectrumId(spectraData,spectrumResult.getSpectrumID());
                spectrumFeatures[0] = spectrumId;
                spectrumFeatures[1] = spectrumResult.getSpectraDataRef();

                identSpectrumMap.put(spectrumIdentItem.getId(),spectrumFeatures);

                List<String[]> evidences = new ArrayList<String[]>();
                Set<Comparable> idProteins = new HashSet<Comparable>();
                for (PeptideEvidenceRef evidenceRef: spectrumIdentItem.getPeptideEvidenceRef()){
                    String[] evidence = new String[2];
                    evidence[0] = evidenceRef.getPeptideEvidenceRef();
                    evidence[1] = evidenceRef.getPeptideEvidence().getDBSequenceRef();
                    evidences.add(evidence);
                    idProteins.add(evidence[1]);
                }
                for(Comparable idProtein: idProteins){
                    Map<Comparable,List<String[]>> spectrumIdentifications = identProteinsMap.get(idProtein);
                    if(spectrumIdentifications == null) spectrumIdentifications = new HashMap<Comparable, List<String[]>>();
                    spectrumIdentifications.put(spectrumIdentItem.getId(),evidences);
                    identProteinsMap.put(idProtein,spectrumIdentifications);
                }
            }
        }

        System.out.println("3:" + String.valueOf(System.currentTimeMillis() - time));

        time = System.currentTimeMillis();


        maps.put(CacheCategory.SPECTRADATA_TO_SPECTRUMIDS,spectraDataMap);
        maps.put(CacheCategory.IDENTIFICATION_TO_PEPTIDE_EVIDENCES,identProteinsMap);
        maps.put(CacheCategory.PEPTIDE_TO_SPECTRUM,identSpectrumMap);

        return maps;
    }
}



