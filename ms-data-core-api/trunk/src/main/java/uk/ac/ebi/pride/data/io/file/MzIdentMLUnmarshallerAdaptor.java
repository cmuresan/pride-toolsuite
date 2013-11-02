package uk.ac.ebi.pride.data.io.file;


import psidev.psi.tools.xxindex.index.IndexElement;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;

import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.*;


/**
 * MzMLIdentMLUnmarshallerAdaptor provides a list of convenient
 * methods to access mzidentML files.
 * <p/>
 * User: yperez, rwang
 * Date: 23/09/11
 * Time: 15:28
 */
public class MzIdentMLUnmarshallerAdaptor extends MzIdentMLUnmarshaller {

    private Map<String, Map<String, List<IndexElement>>> scannedIdMappings;

    public MzIdentMLUnmarshallerAdaptor(File mzIdentMLFile) throws ConfigurationException {
        super(mzIdentMLFile);
        long currentT = System.currentTimeMillis();
        scanIdMappings();
        System.out.println("Initialize mzidentml Time: " + (System.currentTimeMillis() - currentT) + " millis");
    }

    private void scanIdMappings() throws ConfigurationException {

        scannedIdMappings = new HashMap<String, Map<String, List<IndexElement>>>();

        // get id to index element mappings of SpectrumIdentificationResult
        Map<String, IndexElement> spectrumIdentResultIdToIndexElements = this.index.getIndexElements(SpectrumIdentificationResult.class);

        // get id to index element mappings of SpectrumIdentificationItem
        Map<String, IndexElement> spectrumIdentItemIdToIndexElements = this.index.getIndexElements(SpectrumIdentificationItem.class);

        // get index elements of PeptideEvidenceRef
        List<IndexElement> peptideEvidenceRefIndexElements = this.index.getIndexElements(MzIdentMLElement.PeptideEvidenceRef.getXpath());

        boolean proteinGroupPresent = hasProteinGroup();

        scanForIdMappings(spectrumIdentResultIdToIndexElements, spectrumIdentItemIdToIndexElements, peptideEvidenceRefIndexElements, proteinGroupPresent);

    }

    private void scanForIdMappings(Map<String, IndexElement> spectrumIdentResultIdToIndexElements,
                                   Map<String, IndexElement> spectrumIdentItemIdToIndexElements,
                                   List<IndexElement> peptideEvidenceRefIndexElements,
                                   boolean proteinGroupPresent) {

        for (String spectrumIdentResultId : spectrumIdentResultIdToIndexElements.keySet()) {
            IndexElement spectrumIdentResultIndexElement = spectrumIdentResultIdToIndexElements.get(spectrumIdentResultId);

            Iterator<Map.Entry<String, IndexElement>> spectrumIdentItemElementEntryIterator = spectrumIdentItemIdToIndexElements.entrySet().iterator();
            while (spectrumIdentItemElementEntryIterator.hasNext()) {
                Map.Entry<String, IndexElement> spectrumIdentItemElementEntry = spectrumIdentItemElementEntryIterator.next();
                String spectrumIdentItemId = spectrumIdentItemElementEntry.getKey();
                IndexElement spectrumIdentItemIndexElement = spectrumIdentItemElementEntry.getValue();
                if (isParentIndexElement(spectrumIdentResultIndexElement, spectrumIdentItemIndexElement)) {
                    Map<String, List<IndexElement>> spectrumIdentItemWithin = scannedIdMappings.get(spectrumIdentResultId);
                    if (spectrumIdentItemWithin == null) {
                        spectrumIdentItemWithin = new HashMap<String, List<IndexElement>>();
                        scannedIdMappings.put(spectrumIdentResultId, spectrumIdentItemWithin);
                    }

                    if (proteinGroupPresent) {
                        spectrumIdentItemWithin.put(spectrumIdentItemId, null);
                    } else {
                        spectrumIdentItemWithin.put(spectrumIdentItemId, findPeptideEvidenceRefIndexElements(spectrumIdentItemIndexElement, peptideEvidenceRefIndexElements));
                    }

                    spectrumIdentItemElementEntryIterator.remove();
                }
            }
        }
    }

    private List<IndexElement> findPeptideEvidenceRefIndexElements(IndexElement spectrumIdentItemIndexElement, List<IndexElement> peptideEvidenceRefIndexElements) {
        List<IndexElement> peptideEvidenceRefIndexElementsFound = new ArrayList<IndexElement>();

        Iterator<IndexElement> peptideEvidenceRefIndexElementIterator = peptideEvidenceRefIndexElements.iterator();
        while (peptideEvidenceRefIndexElementIterator.hasNext()) {
            IndexElement peptideEvidenceRefIndexElement = peptideEvidenceRefIndexElementIterator.next();
            if (isParentIndexElement(spectrumIdentItemIndexElement, peptideEvidenceRefIndexElement)) {
                peptideEvidenceRefIndexElementsFound.add(peptideEvidenceRefIndexElement);
                peptideEvidenceRefIndexElementIterator.remove();
            }
        }

        return peptideEvidenceRefIndexElementsFound;
    }

    private boolean isParentIndexElement(IndexElement parent, IndexElement child) {
        return parent.getStart() <= child.getStart() && parent.getStop() >= child.getStop();
    }

    public List<Sample> getSampleList() {
        uk.ac.ebi.jmzidml.model.mzidml.AnalysisSampleCollection asc =
                this.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.AnalysisSampleCollection.class);
        return (asc != null) ? asc.getSample() : null;
    }

    public List<SourceFile> getSourceFiles() {
        uk.ac.ebi.jmzidml.model.mzidml.Inputs dc = this.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.Inputs.class);

        return dc.getSourceFile();
    }

    public List<AnalysisSoftware> getSoftwares() {
        uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftwareList asl =
                this.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftwareList.class);

        return (asl != null) ? asl.getAnalysisSoftware() : null;
    }

    public List<Person> getPersonContacts() {
        uk.ac.ebi.jmzidml.model.mzidml.AuditCollection ac =
                this.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.AuditCollection.class);

        return (ac != null) ? ac.getPerson() : null;
    }

    public List<Organization> getOrganizationContacts() {
        uk.ac.ebi.jmzidml.model.mzidml.AuditCollection ac =
                this.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.AuditCollection.class);

        return (ac != null) ? ac.getOrganization() : null;
    }

    public Iterator<BibliographicReference> getReferences() {
        return this.unmarshalCollectionFromXpath(uk.ac.ebi.jmzidml.MzIdentMLElement.BibliographicReference);
    }

    public ProteinDetectionHypothesis getIdentificationById(Comparable IdentId) throws JAXBException {
        return this.unmarshal(ProteinDetectionHypothesis.class, (String) IdentId);
    }

    public int getNumIdentifiedPeptides() throws ConfigurationException {
        List<IndexElement> spectrumIdentItemRefs = this.index.getIndexElements(MzIdentMLElement.SpectrumIdentificationItemRef.getXpath());

        if (spectrumIdentItemRefs == null || spectrumIdentItemRefs.isEmpty()) {
            return this.getIDsForElement(MzIdentMLElement.SpectrumIdentificationItem).size();
        } else {
            return spectrumIdentItemRefs.size();
        }
    }

    public FragmentationTable getFragmentationTable() {
        return this.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable.class);
    }

    public List<Cv> getCvList() {
        return (this.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.CvList.class)).getCv();
    }

    public String getMzIdentMLName() {
        Map<String, String> properties = this.getElementAttributes(this.getMzIdentMLId(),
                uk.ac.ebi.jmzidml.model.mzidml.MzIdentML.class);

        /*
         * This is the only way that we can use now to retrieve the name property
         * In the future we need to think in more elaborated way.
         */
        return (properties.containsKey("name")) ? properties.get("name") : "Unknown experiment (mzIdentML)";
    }

    public Date getCreationDate() {
        Map<String, String> properties = this.getElementAttributes(this.getMzIdentMLId(),
                uk.ac.ebi.jmzidml.model.mzidml.MzIdentML.class);

        /*
         * This is the only way that we can use now to retrieve the name property
         * In the future we need to think in more elaborated way.
         */
        Date dateCreation = null;
        if (properties.containsKey("creationDate")) {
            Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(properties.get("creationDate"));
            dateCreation = calendar.getTime();
        }
        return dateCreation;

    }

    public Provider getProvider() {
        return this.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.Provider.class);
    }

    public List<SpectrumIdentificationProtocol> getSpectrumIdentificationProtocol() {
        AnalysisProtocolCollection apc = this.unmarshal(AnalysisProtocolCollection.class);
        return (apc != null) ? apc.getSpectrumIdentificationProtocol() : null;
    }

    public ProteinDetectionProtocol getProteinDetectionProtocol() {
        return this.unmarshal(ProteinDetectionProtocol.class);
    }

    public List<SearchDatabase> getSearchDatabases() {
        uk.ac.ebi.jmzidml.model.mzidml.Inputs dc = this.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.Inputs.class);

        return dc.getSearchDatabase();
    }

    public List<SpectraData> getSpectraData() {
        Inputs dc = this.unmarshal(Inputs.class);
        return dc.getSpectraData();
    }

    public Map<Comparable, SpectraData> getSpectraDataMap() {
        Inputs dc = this.unmarshal(Inputs.class);
        List<SpectraData> spectraDataList = dc.getSpectraData();
        Map<Comparable, SpectraData> spectraDataMap = null;
        if (spectraDataList != null && spectraDataList.size() > 0) {
            spectraDataMap = new HashMap<Comparable, SpectraData>();
            for (SpectraData spectraData : spectraDataList) {
                spectraDataMap.put(spectraData.getId(), spectraData);
            }
        }
        return spectraDataMap;
    }

    public Map<Comparable, SpectraData> getSpectraData(Set<Comparable> ids) throws JAXBException {
        Map<Comparable, SpectraData> spectraDataMap = new HashMap<Comparable, SpectraData>();
        for (Comparable id : ids) {
            SpectraData spectraData = this.unmarshal(SpectraData.class, (String) id);
            spectraDataMap.put(id, spectraData);
        }
        return spectraDataMap;
    }

    public ProteinAmbiguityGroup getProteinAmbiguityGroup(Comparable id) throws JAXBException {
        return this.unmarshal(ProteinAmbiguityGroup.class, (String) id);
    }

    public List<PeptideHypothesis> getPeptideHypothesisbyID(Comparable id) throws JAXBException {
        ProteinDetectionHypothesis proteinDetectionHypothesis = getIdentificationById(id);
        List<PeptideHypothesis> peptideHypothesises = new ArrayList<PeptideHypothesis>();
        for (PeptideHypothesis peptideHypothesis : proteinDetectionHypothesis.getPeptideHypothesis()) {
            PeptideEvidence peptideEvidence = this.unmarshal(PeptideEvidence.class, peptideHypothesis.getPeptideEvidenceRef());
            Peptide peptide = this.unmarshal(Peptide.class, peptideEvidence.getPeptideRef());
            peptideEvidence.setPeptide(peptide);
            peptideHypothesis.setPeptideEvidence(peptideEvidence);
            peptideHypothesises.add(peptideHypothesis);
        }
        return peptideHypothesises;
    }

    public PeptideEvidence getPeptideEvidence(Comparable id) throws JAXBException {
        return this.unmarshal(PeptideEvidence.class, (String) id);
    }

    public Map<Comparable, Comparable> getPeptideEvidencesBySpectrum(Comparable id) throws JAXBException {
        List<PeptideEvidenceRef> peptideEvidenceRefs = this.unmarshal(SpectrumIdentificationItem.class, (String) id).getPeptideEvidenceRef();
        Map<Comparable, Comparable> peptideRefs = new HashMap<Comparable, Comparable>(peptideEvidenceRefs.size());
        for (PeptideEvidenceRef peptideEvidenceRef : peptideEvidenceRefs) {
            peptideRefs.put(peptideEvidenceRef.getPeptideEvidenceRef(), peptideEvidenceRef.getPeptideEvidence().getDBSequenceRef());
        }
        return peptideRefs;
    }

    public Comparable getDBSequencebyProteinHypothesis(Comparable id) {
        Map<String, String> attributes = this.getElementAttributes((String) id, ProteinDetectionHypothesis.class);
        return attributes.get("dBSequence_ref");
    }

    public DBSequence getDBSequenceById(Comparable id) throws JAXBException {
        return this.unmarshal(DBSequence.class, (String) id);
    }

    public boolean hasProteinGroup() throws ConfigurationException {
        Set<String> proteinAmbiguityGroupIds = this.getIDsForElement(MzIdentMLElement.ProteinAmbiguityGroup);

        return proteinAmbiguityGroupIds != null && !proteinAmbiguityGroupIds.isEmpty();
    }

    public List<SpectrumIdentificationItem> getSpectrumIdentificationsByIds(List<Comparable> spectrumIdentIds) throws JAXBException {
        List<SpectrumIdentificationItem> spectrumIdentifications = null;
        if (spectrumIdentIds != null && spectrumIdentIds.size() > 0) {
            spectrumIdentifications = new ArrayList<SpectrumIdentificationItem>();
            for (Comparable id : spectrumIdentIds) {
                SpectrumIdentificationItem spectrumIdentification = this.unmarshal(SpectrumIdentificationItem.class, (String) id);
                spectrumIdentifications.add(spectrumIdentification);
            }

        }
        return spectrumIdentifications;
    }

    public Map<Comparable, String[]> getSpectrumIdentificationsBySpectrumResult(Comparable id) throws JAXBException {
        SpectrumIdentificationResult results = this.unmarshal(SpectrumIdentificationResult.class, (String) id);
        Map<Comparable, String[]> spectrumIds = new HashMap<Comparable, String[]>(results.getSpectrumIdentificationItem().size());
        for (SpectrumIdentificationItem idSpectrumItem : results.getSpectrumIdentificationItem()) {
            String[] spectrumInfo = new String[2];
            spectrumInfo[0] = results.getSpectrumID();
            spectrumInfo[1] = results.getSpectraDataRef();
            spectrumIds.put(idSpectrumItem.getId(), spectrumInfo);
        }
        return spectrumIds;
    }

    public Set<String> getSpectrumIdentificationItemIds(String spectrumIdentResultId) {
        Map<String, List<IndexElement>> elementsWithSpectrumIdentResult = scannedIdMappings.get(spectrumIdentResultId);

        if (elementsWithSpectrumIdentResult != null) {
            return new LinkedHashSet<String>(elementsWithSpectrumIdentResult.keySet());
        } else {
            return Collections.emptySet();
        }
    }

    public Set<String> getPeptideEvidenceReferences(String spectrumIdentResultId, String spectrumIdentItemId) {
        Map<String, List<IndexElement>> elementsWithSpectrumIdentResult = scannedIdMappings.get(spectrumIdentResultId);

        if (elementsWithSpectrumIdentResult != null) {
            List<IndexElement> peptideEvidenceRefIndexElements = elementsWithSpectrumIdentResult.get(spectrumIdentItemId);
            if (peptideEvidenceRefIndexElements != null) {
                Set<String> peptideEvidenceRefs = new LinkedHashSet<String>();

                for (IndexElement peptideEvidenceRefIndexElement : peptideEvidenceRefIndexElements) {
                    Map<String, String> peptideEvidenceRefAttributes = this.getElementAttributes(this.index.getXmlString(peptideEvidenceRefIndexElement));
                    if (peptideEvidenceRefAttributes.containsKey("peptideEvidence_ref")) {
                        peptideEvidenceRefs.add(peptideEvidenceRefAttributes.get("peptideEvidence_ref"));
                    }
                }

                return peptideEvidenceRefs;
            } else {
                return Collections.emptySet();
            }
        } else {
            return Collections.emptySet();
        }
    }

    public boolean hasProteinSequence() throws ConfigurationException {
        boolean proteinSequencePresent = false;
        Set<String> proteinSequence = this.getIDsForElement(MzIdentMLElement.DBSequence);
        if (proteinSequence != null && !proteinSequence.isEmpty()) {
            Map<String, String> attributes = this.getElementAttributes((String) proteinSequence.toArray()[0], DBSequence.class);
            return attributes.containsKey("Seq");
        }
        return proteinSequencePresent;
    }
}



