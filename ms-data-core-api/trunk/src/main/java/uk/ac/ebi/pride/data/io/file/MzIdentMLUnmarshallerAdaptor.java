package uk.ac.ebi.pride.data.io.file;

//~--- non-JDK imports --------------------------------------------------------

import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;

import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//~--- JDK imports ------------------------------------------------------------

/**
 * MzMLIdentMLUnmarshallerAdaptor provides a list of convenient methods to access mzidentML files
 * <p/>
 * User: yperez
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

    /*
     * public CVList getCVList() throws MzMLUnmarshallerException {
     *   return unmarshaller.unmarshalFromXpath("/mzML/cvList", CVList.class);
     * }
     *
     * public FileDescription getFileDescription() throws MzMLUnmarshallerException {
     *   return unmarshaller.unmarshalFromXpath("/mzML/fileDescription", FileDescription.class);
     * }
     *
     * public ReferenceableParamGroupList getReferenceableParamGroupList() throws MzMLUnmarshallerException {
     *   return unmarshaller.unmarshalFromXpath("/mzML/referenceableParamGroupList", ReferenceableParamGroupList.class);
     * }
     */
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
        return unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionHypothesis.class,
                                      (String) IdentId);
    }

    public SpectrumIdentificationItem getPeptideIdentificationById(Comparable IdentId, Comparable index) {
        ProteinDetectionHypothesis protein;

        try {
            protein = unmarshaller.unmarshal(ProteinDetectionHypothesis.class, (String) IdentId);

            List<PeptideHypothesis> peptideHypothesises = protein.getPeptideHypothesis();

            for (PeptideHypothesis peptideHypothesis : peptideHypothesises) {
                for (SpectrumIdentificationItemRef spectrumIdentificationItemRef :
                        peptideHypothesis.getSpectrumIdentificationItemRef()) {
                    if (spectrumIdentificationItemRef.getSpectrumIdentificationItem().getId().equalsIgnoreCase(
                            (String) index)) {
                        return spectrumIdentificationItemRef.getSpectrumIdentificationItem();
                    }
                }
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getNumIdentifiedPeptides() {
        return (unmarshaller.unmarshal(
            uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationResult.class)).getSpectrumIdentificationItem().size();
    }

    public FragmentationTable getFragmentationTable() {
        return unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable.class);
    }

    public Set<String> getIDsForElement(MzIdentMLElement mzIdentMLElement) {
        try {
            return unmarshaller.getIDsForElement(mzIdentMLElement);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        return null;
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
        return (properties.containsKey("name"))
               ? properties.get("name")
               : "Unknown experiment (mzIdentML)";
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
}



