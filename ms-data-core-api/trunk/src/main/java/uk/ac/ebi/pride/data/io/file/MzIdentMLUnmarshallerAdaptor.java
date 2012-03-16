package uk.ac.ebi.pride.data.io.file;


import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;

import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;
import java.text.ParseException;
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

        return ac.getPerson();
    }

    public List<Organization> getOrganizationContacts() {
        uk.ac.ebi.jmzidml.model.mzidml.AuditCollection ac =
            unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.AuditCollection.class);

        return ac.getOrganization();
    }

    public Iterator<BibliographicReference> getReferences() {
        return unmarshaller.unmarshalCollectionFromXpath(uk.ac.ebi.jmzidml.MzIdentMLElement.BibliographicReference);
    }

    public ProteinDetectionHypothesis getIdentificationById(Comparable IdentId) throws JAXBException {
        return unmarshaller.unmarshal(ProteinDetectionHypothesis.class,(String) IdentId);
    }

    public int getNumIdentifiedPeptides() throws ConfigurationException, JAXBException {
        int total = 0;
        Set<String> listIDs = unmarshaller.getIDsForElement(MzIdentMLElement.ProteinDetectionHypothesis);
        Object[] ArrayIDs = listIDs.toArray();
        for(int i = 0; i < ArrayIDs.length;i++){
            ProteinDetectionHypothesis protein = null;
            protein = unmarshaller.unmarshal(ProteinDetectionHypothesis.class, (String) ArrayIDs[i]);
            int count = 0;
            for(PeptideHypothesis peptideHypothesis: protein.getPeptideHypothesis()){
                count += peptideHypothesis.getSpectrumIdentificationItemRef().size();
            }
            total += count;
        }
        return total;
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
}



