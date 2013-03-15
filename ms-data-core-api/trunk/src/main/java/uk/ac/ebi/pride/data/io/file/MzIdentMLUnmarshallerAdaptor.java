package uk.ac.ebi.pride.data.io.file;


import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.utils.MzIdentMLUtils;

import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;
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

        return (asl != null)?asl.getAnalysisSoftware():null;
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
            Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(properties.get("creationDate"));
                dateCreation = calendar.getTime();
        }
        return dateCreation;

    }

    public Provider getProvider() {
        return unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.Provider.class);
    }

    public List<SpectrumIdentificationProtocol> getSpectrumIdentificationProtcol() {
        AnalysisProtocolCollection apc = unmarshaller.unmarshal(AnalysisProtocolCollection.class);
        return (apc != null)?apc.getSpectrumIdentificationProtocol():null;
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
        if(spectraDataList != null && spectraDataList.size()>0){
            spectraDataMap = new HashMap<Comparable, SpectraData>();
            for(SpectraData spectraData: spectraDataList){
                spectraDataMap.put(spectraData.getId(),spectraData);
            }
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

    public Comparable getDBSequencebyProteinHypothesis(Comparable id) throws JAXBException {
        Map<String, String> attributes = unmarshaller.getElementAttributes((String) id, ProteinDetectionHypothesis.class);
        return attributes.get("dBSequence_ref");
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

    /*
     * This function try to Map in memory ids mapping and relation for an mzidentml file. The structure of the
     * mzidentml files is from spectrum->peptide->protein, but most for the end users is more interesting to
     * have an information structure from protein->peptide->spectrum. The function take the information from
     * spectrumItems and read the Peptide Evidences and the Proteins related with these peptideEvidence. Finally
     * the function construct a map in from proteins to spectrums named identProteinsMap.
     *
     * @return
     * @throws ConfigurationException
     * @throws JAXBException
     */
    public Map<CacheCategory, Object> getPreScanIdMaps() throws ConfigurationException, JAXBException {

        Map<CacheCategory, Object> maps = new HashMap<CacheCategory, Object>();


        Map<Comparable, SpectraData> spectraDataIds = getSpectraDataMap();



        /* First Map is the Relation between an Spectrum file and all the Spectrums ids in the file
         * This information is useful to retrieve the for each spectrum File with spectrums are really
         * SpectrumIdentificationItems. For PRIDE Inspector is important for one of the windows that
         * shows the number of missing spectrum for an mzidentml file.
         */
        Map<Comparable, List<Comparable>> spectraDataMap = new HashMap<Comparable, List<Comparable>>(spectraDataIds.size());

        /* The relation between the peptide evidence and the spectrumIdentificationItem. This map allow the access to the peptide evidence and
         * spectrum information without the Protein information.
        * */

        Map<Comparable, String[]> identSpectrumMap = new HashMap<Comparable, String[]>();


        ArrayList<Comparable> spectrumMatchResultsIds  = new ArrayList<Comparable>(unmarshaller.getIDsForElement(MzIdentMLElement.SpectrumIdentificationResult));

        /**
         * This Protein Map represents the Protein identification in the DBSequence Section that contains SpectrumIdentification Items
         * Each key is the Protein Id, the Map related with each key is a Peptide Evidence Map. Each Peptide Evidence Map contains a key
         * of the for a PeptideEvidence and a list of SpectrumIdentificationItems. Each Sepctrum Identification Item is that contains
         * the original id of the spectrum in the Spectrum file and the id of the spectrum file.
         *
         */
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

        maps.put(CacheCategory.SPECTRADATA_TO_SPECTRUMIDS,spectraDataMap);
        maps.put(CacheCategory.PROTEIN_TO_PEPTIDE_EVIDENCES,identProteinsMap);
        maps.put(CacheCategory.PEPTIDE_TO_SPECTRUM,identSpectrumMap);

        return maps;
    }


}



