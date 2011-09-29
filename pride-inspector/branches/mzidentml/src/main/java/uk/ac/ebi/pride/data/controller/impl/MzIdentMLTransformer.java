package uk.ac.ebi.pride.data.controller.impl;

import uk.ac.ebi.pride.data.coreIdent.*;
import uk.ac.ebi.pride.data.coreIdent.AbstractContact;
import uk.ac.ebi.pride.data.coreIdent.CvParam;
import uk.ac.ebi.pride.data.coreIdent.DBSequence;
import uk.ac.ebi.pride.data.coreIdent.MassTable;
import uk.ac.ebi.pride.data.coreIdent.Modification;
import uk.ac.ebi.pride.data.coreIdent.Organization;
import uk.ac.ebi.pride.data.coreIdent.Peptide;
import uk.ac.ebi.pride.data.coreIdent.PeptideEvidence;
import uk.ac.ebi.pride.data.coreIdent.Person;
import uk.ac.ebi.pride.data.coreIdent.Provider;
import uk.ac.ebi.pride.data.coreIdent.Sample;
import uk.ac.ebi.pride.data.coreIdent.SourceFile;
import uk.ac.ebi.pride.data.coreIdent.SubstitutionModification;
import uk.ac.ebi.pride.data.coreIdent.UserParam;
import uk.ac.ebi.pride.term.CvTermReference;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 19/09/11
 * Time: 16:08
 */
public class MzIdentMLTransformer {
    private static List<IdentifiableParamGroup> FragmentationTable = null;

    public static List<SourceFile> transformSourceFile(List<uk.ac.ebi.jmzidml.model.mzidml.SourceFile> oldSourceFiles) {
        List<SourceFile> sourceFiles = null ;

        if (oldSourceFiles != null) {
            sourceFiles = new ArrayList<SourceFile>();
            for(uk.ac.ebi.jmzidml.model.mzidml.SourceFile oldsourcefile: oldSourceFiles){
                String id = oldsourcefile.getId();
                String name = oldsourcefile.getName();
                String location = oldsourcefile.getLocation();
                CvParam format = transformToCvParam(oldsourcefile.getFileFormat().getCvParam());
                String formatDocumentation = oldsourcefile.getExternalFormatDocumentation();
                List<CvParam> cvParams = transformCvParams(oldsourcefile.getCvParam());
                List<UserParam> userParams = transformUserParams(oldsourcefile.getUserParam());
                sourceFiles.add(new SourceFile(new ParamGroup(cvParams,userParams),id,name,location,format,formatDocumentation));
            }
        }
        return sourceFiles;
    }

    private static List<UserParam> transformUserParams(List<uk.ac.ebi.jmzidml.model.mzidml.UserParam> oldUserParams) {
        List<UserParam> userParams = null;
        if(oldUserParams != null){
            userParams = new ArrayList<UserParam>();
            for (uk.ac.ebi.jmzidml.model.mzidml.UserParam oldUserParam: oldUserParams){
                userParams.add(transformUserParam(oldUserParam));
            }
        }
        return userParams;
    }

    private static List<CvParam> transformCvParams(List<uk.ac.ebi.jmzidml.model.mzidml.CvParam> oldCvParams) {
        List<CvParam> cvParams = null;
        if(oldCvParams != null){
            cvParams = new ArrayList<CvParam>();
            for (uk.ac.ebi.jmzidml.model.mzidml.CvParam oldCvParam: oldCvParams){
                cvParams.add(transformToCvParam(oldCvParam));
            }
        }
        return cvParams;
    }

    public static List<Organization> transformToOrganization(List<uk.ac.ebi.jmzidml.model.mzidml.Organization> oldOrganizations) {
        List<Organization> organizations = null;
        if(oldOrganizations != null){
            organizations = new ArrayList<Organization>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Organization oldOrganization: oldOrganizations){
                //Todo: I need to solve the problem with mail and the parent organization
                organizations.add(transformToOrganization(oldOrganization));
            }
        }
        return organizations;
    }

    public static Organization transformToOrganization(uk.ac.ebi.jmzidml.model.mzidml.Organization oldOrganization) {
        Organization organization = null;
        if(oldOrganization != null){
              Organization parentOrganization = null;
              if(oldOrganization.getParent()!=null){
                  parentOrganization = transformToOrganization(oldOrganization.getParent().getOrganization());
              }
              organization = new Organization(new ParamGroup(transformCvParams(oldOrganization.getCvParam()),transformUserParams(oldOrganization.getUserParam())),oldOrganization.getId(),oldOrganization.getName(),parentOrganization,null);
        }
        return organization;
    }

    public static List<Organization> transformAffiliationToOrganization(List<uk.ac.ebi.jmzidml.model.mzidml.Affiliation> oldAffiliations) {
        List<Organization> organizations = null;
        if(oldAffiliations != null){
            organizations = new ArrayList<Organization>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Affiliation oldAffiliation: oldAffiliations){
                uk.ac.ebi.jmzidml.model.mzidml.Organization oldOrganization = oldAffiliation.getOrganization();
                organizations.add(transformToOrganization(oldOrganization));
            }
        }
        return organizations;
    }


    public static List<Person> transformToPerson(List<uk.ac.ebi.jmzidml.model.mzidml.Person> oldPersons) {
        List<Person> persons = null;
        if(oldPersons != null){
            persons = new ArrayList<Person>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Person oldPerson : oldPersons){
                persons.add(transformToPerson(oldPerson));
            }
        }
        return persons;
    }

    public static Person transformToPerson(uk.ac.ebi.jmzidml.model.mzidml.Person oldPerson) {
        if(oldPerson != null){
            List<uk.ac.ebi.jmzidml.model.mzidml.Affiliation> oldAffiliation = oldPerson.getAffiliation();
            List<Organization> affiliation = transformAffiliationToOrganization(oldPerson.getAffiliation());
                //Todo: Take from Cv Params the value of the mail.
            return new Person(new ParamGroup(transformCvParams(oldPerson.getCvParam()),transformUserParams(oldPerson.getUserParam())),oldPerson.getId(),oldPerson.getName(),oldPerson.getLastName(),oldPerson.getFirstName(),oldPerson.getMidInitials(),affiliation,null);
        }

        return null;
    }


    public static List<Sample> transformToSample(List<uk.ac.ebi.jmzidml.model.mzidml.Sample> oldSamples) {
        List<Sample> samples = null;
        if(oldSamples != null){
            samples = new ArrayList<Sample>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Sample oldSample: oldSamples){
                samples.add(transformToSample(oldSample));
            }
        }
        return samples;
    }

    public static List<Sample> transformSubSampleToSample(List<uk.ac.ebi.jmzidml.model.mzidml.SubSample> oldSamples) {
        List<Sample> samples = null;
        if(oldSamples != null){
            samples = new ArrayList<Sample>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SubSample oldSubSample: oldSamples){
                samples.add(transformToSample(oldSubSample.getSample()));
            }
        }
        return samples;
    }

    public static Sample transformToSample(uk.ac.ebi.jmzidml.model.mzidml.Sample oldSample){
        Sample sample = null;
        if(oldSample != null){
            Map<AbstractContact, CvParam> role = transformToRoleList(oldSample.getContactRole());
            List<Sample> subSamples = null;
            if((oldSample.getSubSample() != null) && (!oldSample.getSubSample().isEmpty())){
                subSamples = transformSubSampleToSample(oldSample.getSubSample());
            }
            sample = new Sample(new ParamGroup(transformCvParams(oldSample.getCvParam()),transformUserParams(oldSample.getUserParam())),oldSample.getId(),oldSample.getName(),subSamples,role);
        }
        return sample;
    }

    private static Map<AbstractContact,CvParam> transformToRoleList(List<uk.ac.ebi.jmzidml.model.mzidml.ContactRole> contactRoles) {
        Map<AbstractContact, CvParam> contacts = null;
        if(contactRoles != null){
            contacts = new HashMap<AbstractContact, CvParam>();
            for (uk.ac.ebi.jmzidml.model.mzidml.ContactRole oldRole : contactRoles){
               AbstractContact contact = null;
               if(oldRole.getOrganization()!= null){
                     contact = transformToOrganization(oldRole.getOrganization());
               }else if(oldRole.getPerson() != null){
                     contact = transformToPerson(oldRole.getPerson());
               }
               CvParam role = transformToCvParam(oldRole.getRole().getCvParam());
                contacts.put(contact,role);
            }
        }
        return contacts;
    }

    private static CvParam transformToCvParam(uk.ac.ebi.jmzidml.model.mzidml.CvParam oldCvParam) {
        String cvLookupID = null;
        uk.ac.ebi.jmzidml.model.mzidml.Cv cv = oldCvParam.getCv();
        if (cv != null) cvLookupID = cv.getId();
        String unitCVLookupID = null;
        cv = oldCvParam.getUnitCv();
        if (cv != null) unitCVLookupID = cv.getId();
        CvParam newParam = new CvParam(oldCvParam.getAccession(), oldCvParam.getName(), cvLookupID,oldCvParam.getValue(),
                oldCvParam.getUnitAccession(),
                oldCvParam.getUnitName(), unitCVLookupID);
        return newParam;
    }

    private static UserParam transformUserParam(uk.ac.ebi.jmzidml.model.mzidml.UserParam oldUserParam) {

        String unitCVLookupID = null;
        uk.ac.ebi.jmzidml.model.mzidml.Cv cv = oldUserParam.getUnitCv();
        if (cv != null) unitCVLookupID = cv.getId();
        UserParam newParam = new UserParam(oldUserParam.getName(), oldUserParam.getType(),
                        oldUserParam.getValue(), oldUserParam.getUnitAccession(),
                        oldUserParam.getUnitName(), unitCVLookupID);
        return newParam;
    }

    public static List<Software> transformToSoftware(List<uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftware> oldSoftwares) {
        List<Software> softwares = null;
        if(oldSoftwares != null){
            softwares = new ArrayList<Software>();
            for (uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftware oldSoftware : oldSoftwares){
                softwares.add(transformToSoftware(oldSoftware));
            }
        }
        return softwares;
    }

    public static Software transformToSoftware(uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftware oldSoftware) {
        if(oldSoftware != null){
            AbstractContact contact = null;
            if(oldSoftware.getContactRole().getOrganization()!=null){
               contact = transformToOrganization(oldSoftware.getContactRole().getOrganization());
            }else if(oldSoftware.getContactRole().getPerson() != null){
                contact = transformToPerson(oldSoftware.getContactRole().getPerson());
            }
            Software software = new Software(oldSoftware.getId(),oldSoftware.getName(),oldSoftware.getVersion(),oldSoftware.getUri(),contact,oldSoftware.getCustomizations());
            return software;
        }
        return null;
    }

    public static List<Reference> transformToReference(Iterator<uk.ac.ebi.jmzidml.model.mzidml.BibliographicReference> it) {
        List<Reference> references = new ArrayList<Reference>();
        while (it.hasNext()) {
            uk.ac.ebi.jmzidml.model.mzidml.BibliographicReference ref = it.next();
            // RefLine Trying to use the same approach of pride converter
            String refLine = ((ref.getAuthors() != null) ? ref.getAuthors() + ". " : "") +
                            ((ref.getYear() != null) ? "(" + ref.getYear().toString() + "). " : "") +
                            ((ref.getTitle() != null) ? ref.getTitle() + " " : "") +
                            ((ref.getPublication() != null) ? ref.getPublication() + " " : "") +
                            ((ref.getVolume() != null) ? ref.getVolume() + "" : "") +
                            ((ref.getIssue() != null) ? "(" + ref.getIssue() + ")" : "") +
                            ((ref.getPages() != null) ? ":" + ref.getPages() + "." : "");
             // create the ref
            ParamGroup additional = new ParamGroup();
            //Todo: Set the References ParamGroup for references
            Reference reference = new Reference(ref.getId(),ref.getName(),ref.getDoi(),ref.getTitle(),ref.getPages(),ref.getIssue(),ref.getVolume(),ref.getYear().toString(),ref.getEditor(),ref.getPublisher(),ref.getPublication(),ref.getAuthors(),refLine);
            references.add(reference);
        }
        return references;
    }

    public static Identification transformToIdentification(uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionHypothesis oldIdent, uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable) {
        Identification ident = null;
        if(FragmentationTable == null){
            FragmentationTable = transformFragmentationTable(oldFragmentationTable);
        }
        if(oldIdent != null){
            SearchDataBase database = transformToSeachDatabase(oldIdent.getDBSequence().getSearchDatabase());
            Map<PeptideEvidence, List<Peptide>> peptides = transformToPeptideIdentifications(oldIdent.getPeptideHypothesis(),oldFragmentationTable);
            ident = new GelFreeIdentification(oldIdent.getId(),oldIdent.getName(),transformToDBSequence(oldIdent.getDBSequence()),oldIdent.isPassThreshold(),peptides,-1,-1,null,-1);
            //Todo: SearchEngine information, score, threshold and sequence coverage
        }
        return ident;
    }

    public static List<IdentifiableParamGroup> transformFragmentationTable(uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable){
        List<IdentifiableParamGroup> fragmentationTable = null;
        if(oldFragmentationTable != null){
            fragmentationTable = new ArrayList<IdentifiableParamGroup>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Measure oldMeasure : oldFragmentationTable.getMeasure()){
                fragmentationTable.add(new IdentifiableParamGroup(new ParamGroup(transformCvParams(oldMeasure.getCvParam()),null),oldMeasure.getId(),oldMeasure.getName()));
            }
        }
        return fragmentationTable;
    }

    private static Map<PeptideEvidence,List<Peptide>> transformToPeptideIdentifications(List<uk.ac.ebi.jmzidml.model.mzidml.PeptideHypothesis> peptideHypothesis,uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable) {
        Map<PeptideEvidence,List<Peptide>> peptides = null;
        if(peptideHypothesis != null){
            peptides = new HashMap<PeptideEvidence, List<Peptide>>();
            for(uk.ac.ebi.jmzidml.model.mzidml.PeptideHypothesis oldPeptideHypothesis: peptideHypothesis){
                PeptideEvidence peptideEvidence = transformToPeptideEvidence(oldPeptideHypothesis.getPeptideEvidence());
                List<Peptide> peptideIdentified = transformToPeptideIdentification(oldPeptideHypothesis.getSpectrumIdentificationItemRef(), oldFragmentationTable);
            }
       }
       return peptides;
    }

    public static List<Peptide> transformToPeptideIdentification(List<uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItemRef> spectrumIdentificationItemRefs, uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable) {
        List<Peptide> peptides = null;
        if(spectrumIdentificationItemRefs != null){
            peptides = new ArrayList<Peptide>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItemRef oldSpectrumIdentificationItemRef: spectrumIdentificationItemRefs){
                uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem oldPeptideIdentification = oldSpectrumIdentificationItemRef.getSpectrumIdentificationItem();
                Peptide peptide = transformToPeptideIdentification(oldPeptideIdentification, oldFragmentationTable);
                peptides.add(peptide);
            }
        }
        return peptides;
    }

    public static Peptide transformToPeptideIdentification(uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem oldSpectrumIdentification, uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable){
        Peptide peptide = null;
        if(oldSpectrumIdentification != null){
            peptide = new Peptide(oldSpectrumIdentification.getId(),oldSpectrumIdentification.getName(),oldSpectrumIdentification.getChargeState(),oldSpectrumIdentification.getExperimentalMassToCharge(),oldSpectrumIdentification.getCalculatedMassToCharge(),oldSpectrumIdentification.getCalculatedPI(),transformToPeptide(oldSpectrumIdentification.getPeptide()),oldSpectrumIdentification.getRank(),false,transformToMassTable(oldSpectrumIdentification.getMassTable()),transformToSample(oldSpectrumIdentification.getSample()),transformToPeptideEvidence(oldSpectrumIdentification.getPeptideEvidenceRef()),transformToFragmentationIon(oldSpectrumIdentification.getFragmentation(),oldFragmentationTable),null,null,null);
            //Todo: Peptide Score
            //Todo: Peptide SpectraData
        }
        return peptide;
    }

    private static List<FragmentIon> transformToFragmentationIon(uk.ac.ebi.jmzidml.model.mzidml.Fragmentation fragmentation, uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable) {
        List<FragmentIon> fragmentIons = null;
        if(FragmentationTable == null){
            FragmentationTable = transformFragmentationTable(oldFragmentationTable);
        }
        if(fragmentation != null){
            fragmentIons = new ArrayList<FragmentIon>();
            for(uk.ac.ebi.jmzidml.model.mzidml.IonType ionType : fragmentation.getIonType()){

               // ignore not supported iontypes
               // TODO: Once the MS ontology is adapted for the new fragment ion params, adapt this code
               // TODO DOC Converter only consider - only a-c, x-z ions are reported
               String ionTypeChar = null;
               if (ionType.getCvParam().getName().contains("a ion")) ionTypeChar = "a";
               if (ionType.getCvParam().getName().contains("b ion")) ionTypeChar = "b";
               if (ionType.getCvParam().getName().contains("c ion")) ionTypeChar = "c";
               if (ionType.getCvParam().getName().contains("x ion")) ionTypeChar = "x";
               if (ionType.getCvParam().getName().contains("y ion")) ionTypeChar = "y";
               if (ionType.getCvParam().getName().contains("z ion")) ionTypeChar = "z";

               if (ionTypeChar == null)     continue;
               // ignore IonTypes with no index set
               //if (ionType.getIndex() == null)  continue;

               // iterate over the ion type indexes
               for (Integer index = 0; index < ionType.getIndex().size(); index++) {
                   Integer ionIndex = ionType.getIndex().get(index);
                   //FragmentIon fragmentIon = new FragmentIon();
                   List<CvParam> cvParams = new ArrayList<CvParam>();
                   // charge
                   CvTermReference cvCharge = CvTermReference.PRODUCT_ION_CHARGE;
                   cvParams.add(new CvParam(cvCharge.getAccession(),cvCharge.getName(),cvCharge.getCvLabel(),String.valueOf(ionType.getCharge()),null,null,null));
                   //ion type
                   cvParams.add(new CvParam(ionType.getCvParam().getAccession(),ionType.getCvParam().getName(),ionType.getCvParam().getCvRef(),ionType.getCvParam().getValue(),null,null,null));
                   //mz
                   for (uk.ac.ebi.jmzidml.model.mzidml.FragmentArray fragArr : ionType.getFragmentArray()){
                       uk.ac.ebi.jmzidml.model.mzidml.Measure oldMeasure = fragArr.getMeasure();
                       CvParam cvParam = null;
                       CvTermReference cvMz = null;
                       cvMz = CvTermReference.PRODUCT_ION_MZ;
                       cvParam = getCvParamByID(oldMeasure.getCvParam(),cvMz.getAccession(),fragArr.getValues().get(index).toString());
                       if(cvParam == null){
                           cvMz = CvTermReference.PRODUCT_ION_INTENSITY;
                           cvParam = getCvParamByID(oldMeasure.getCvParam(),cvMz.getAccession(),fragArr.getValues().get(index).toString());
                           if(cvParam == null){
                               cvMz = CvTermReference.PRODUCT_ION_MASS_ERROR;
                               cvParam = getCvParamByID(oldMeasure.getCvParam(),cvMz.getAccession(),fragArr.getValues().get(index).toString());
                               if(cvParam == null){
                                   cvMz = CvTermReference.PRODUCT_ION_RETENTION_TIME_ERROR;
                                   cvParam = getCvParamByID(oldMeasure.getCvParam(),cvMz.getAccession(),fragArr.getValues().get(index).toString());
                               }
                           }
                       }
                       if(cvParam != null){
                           cvParams.add(cvParam);
                       }
                   }
                   fragmentIons.add(new FragmentIon(new ParamGroup(cvParams,null)));
               }
           }
        }
        return fragmentIons;
    }

    private static CvParam getCvParamByID(List<uk.ac.ebi.jmzidml.model.mzidml.CvParam> oldCvParams, String accession, String newValue){
        for (uk.ac.ebi.jmzidml.model.mzidml.CvParam oldCvParam: oldCvParams){
            if(oldCvParam.getAccession().equalsIgnoreCase(accession)){
                CvParam cvParam=  transformToCvParam(oldCvParam);
                cvParam.setValue(newValue);
                return cvParam;
            }
        }
        return null;
    }


    private static MassTable transformToMassTable(uk.ac.ebi.jmzidml.model.mzidml.MassTable oldMassTable) {
        MassTable massTable = null;
        if(oldMassTable != null){
            Map<String,Double> residues = new HashMap<String, Double>();
            for(uk.ac.ebi.jmzidml.model.mzidml.Residue residue : oldMassTable.getResidue()){
               residues.put(residue.getCode(),new Double(residue.getMass()));
            }
            Map<String, ParamGroup> ambiguousResidues = new HashMap<String, ParamGroup>();
            for(uk.ac.ebi.jmzidml.model.mzidml.AmbiguousResidue residue : oldMassTable.getAmbiguousResidue()){
               ambiguousResidues.put(residue.getCode(),new ParamGroup(transformCvParams(residue.getCvParam()),transformUserParams(residue.getUserParam())));
            }
            massTable = new MassTable(oldMassTable.getMsLevel(),residues,ambiguousResidues);
        }
        return massTable;
    }

    private static PeptideEvidence transformToPeptideEvidence(uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidence oldPeptideEvidence) {
        PeptideEvidence evidence = null;
        if(evidence != null){
            evidence = new PeptideEvidence(oldPeptideEvidence.getId(),oldPeptideEvidence.getName(),oldPeptideEvidence.getStart(),oldPeptideEvidence.getEnd(),oldPeptideEvidence.isIsDecoy(),transformToPeptide(oldPeptideEvidence.getPeptide()), transformToDBSequence(oldPeptideEvidence.getDBSequence()));
        }
        return evidence;
    }

    private static List<PeptideEvidence> transformToPeptideEvidence(List<uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidenceRef> oldPeptideEvidenceRefs){
        List<PeptideEvidence> peptideEvidences = null;
        if(oldPeptideEvidenceRefs != null){
            peptideEvidences = new ArrayList<PeptideEvidence>();
            for (uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidenceRef oldPeptideEvidenceRef : oldPeptideEvidenceRefs){
                peptideEvidences.add(transformToPeptideEvidence(oldPeptideEvidenceRef.getPeptideEvidence()));
            }
        }
        return peptideEvidences;
    }

    private static PeptideSequence transformToPeptide(uk.ac.ebi.jmzidml.model.mzidml.Peptide oldPeptide) {
        PeptideSequence peptideSequence = null;
        if(oldPeptide!= null){
          peptideSequence = new PeptideSequence(oldPeptide.getId(),oldPeptide.getName(),oldPeptide.getPeptideSequence(),transformToModification(oldPeptide.getModification()),transformToSubstitutionMod(oldPeptide.getSubstitutionModification()));
        }
        return peptideSequence;
    }

    private static List<Modification> transformToModification(List<uk.ac.ebi.jmzidml.model.mzidml.Modification> oldModifications) {
        List<Modification> modifications = null;
        if(oldModifications != null){
            modifications = new ArrayList<Modification>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Modification oldModification: oldModifications){
                modifications.add(transformToModification(oldModification));
            }
        }
        return modifications;
    }

    private static Modification transformToModification(uk.ac.ebi.jmzidml.model.mzidml.Modification oldModification) {
        Modification modification = null;
        if(oldModification != null){
            List<Double> monoMasses = new ArrayList<Double>();
            monoMasses.add(new Double(oldModification.getMonoisotopicMassDelta()));
            List<Double> avgMasses = new ArrayList<Double>();
            avgMasses.add(new Double(oldModification.getAvgMassDelta()));
            modification = new Modification(null,null,oldModification.getLocation(),oldModification.getResidues(),avgMasses,monoMasses);
        }
        return modification;
    }

    private static List<SubstitutionModification> transformToSubstitutionMod(List<uk.ac.ebi.jmzidml.model.mzidml.SubstitutionModification> oldSubstitutionModifications) {
        List<SubstitutionModification> modifications = null;
        if(oldSubstitutionModifications != null){
            modifications = new ArrayList<SubstitutionModification>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SubstitutionModification oldModification: oldSubstitutionModifications){
                modifications.add(transformToSubstitutionMod(oldModification));
            }
        }
        return modifications;
    }

    private static SubstitutionModification transformToSubstitutionMod(uk.ac.ebi.jmzidml.model.mzidml.SubstitutionModification oldModification) {
        SubstitutionModification modification = null;
        if(oldModification != null){
            List<Double> monoMasses = new ArrayList<Double>();
            monoMasses.add(new Double(oldModification.getMonoisotopicMassDelta()));
            List<Double> avgMasses = new ArrayList<Double>();
            avgMasses.add(new Double(oldModification.getAvgMassDelta()));
            modification = new SubstitutionModification(oldModification.getOriginalResidue(),oldModification.getReplacementResidue(),oldModification.getLocation(),oldModification.getAvgMassDelta(),oldModification.getMonoisotopicMassDelta());
        }
        return modification;
    }

    private static DBSequence transformToDBSequence(uk.ac.ebi.jmzidml.model.mzidml.DBSequence oldDbSequence) {
        DBSequence dbSequence = null;
        if(oldDbSequence != null){
            dbSequence = new DBSequence(oldDbSequence.getId(),oldDbSequence.getName(),oldDbSequence.getLength(),oldDbSequence.getAccession(),transformToSeachDatabase(oldDbSequence.getSearchDatabase()),oldDbSequence.getSeq(),null,null);
        }
        return dbSequence;
    }

    private static SearchDataBase transformToSeachDatabase(uk.ac.ebi.jmzidml.model.mzidml.SearchDatabase oldDatabase) {
        return new SearchDataBase(oldDatabase.getId(),oldDatabase.getName(),oldDatabase.getLocation(),transformToCvParam(oldDatabase.getFileFormat().getCvParam()),oldDatabase.getExternalFormatDocumentation(),oldDatabase.getVersion(),oldDatabase.getReleaseDate().toString(),oldDatabase.getNumDatabaseSequences().intValue(),oldDatabase.getNumResidues(),null,transformCvParams(oldDatabase.getCvParam()));
    }

    public static List<CVLookup> transformCVList(List<uk.ac.ebi.jmzidml.model.mzidml.Cv> cvList) {
        List<CVLookup> cvLookups = null;
        if(cvLookups != null){
            cvLookups = new ArrayList<CVLookup>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Cv cv : cvList){
               cvLookups.add(transformCVLookup(cv));
            }
        }
        return cvLookups;
    }

    public static CVLookup transformCVLookup(uk.ac.ebi.jmzidml.model.mzidml.Cv oldCv) {
        CVLookup cvLookup = null;
        if (oldCv != null) {
            cvLookup = new CVLookup(oldCv.getId(), oldCv.getFullName(),
                    oldCv.getVersion(), oldCv.getUri());
        }
        return cvLookup;
    }

    public static Provider transformProvider(uk.ac.ebi.jmzidml.model.mzidml.Provider oldProvider) {
       Provider provider = null;
       if(oldProvider !=null){
           AbstractContact abstractContact = null;
           if(oldProvider.getContactRole().getOrganization() != null){
               abstractContact = transformToOrganization(oldProvider.getContactRole().getOrganization());
           }else if(oldProvider.getContactRole().getPerson() != null){
               abstractContact = transformToPerson(oldProvider.getContactRole().getPerson());
           }
           CvParam role = transformToCvParam(oldProvider.getContactRole().getRole().getCvParam());
           Software software = transformToSoftware(oldProvider.getSoftware());
           provider = new Provider(oldProvider.getId(),oldProvider.getName(),software,abstractContact,role);
       }
        return provider;
    }
}
