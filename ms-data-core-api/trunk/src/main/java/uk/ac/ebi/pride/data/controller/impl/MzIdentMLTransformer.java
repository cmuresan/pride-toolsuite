package uk.ac.ebi.pride.data.controller.impl;


import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.core.AbstractContact;
import uk.ac.ebi.pride.data.core.CvParam;
import uk.ac.ebi.pride.data.core.DBSequence;
import uk.ac.ebi.pride.data.core.Enzyme;
import uk.ac.ebi.pride.data.core.Filter;
import uk.ac.ebi.pride.data.core.MassTable;
import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Organization;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.PeptideEvidence;
import uk.ac.ebi.pride.data.core.Person;
import uk.ac.ebi.pride.data.core.Provider;
import uk.ac.ebi.pride.data.core.Sample;
import uk.ac.ebi.pride.data.core.SearchModification;
import uk.ac.ebi.pride.data.core.SourceFile;
import uk.ac.ebi.pride.data.core.SpectraData;
import uk.ac.ebi.pride.data.core.SpectrumIdentificationProtocol;
import uk.ac.ebi.pride.data.core.SubstitutionModification;
import uk.ac.ebi.pride.data.core.UserParam;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.term.CvTermReference;

import java.util.*;

/**
 * ToDo: document this class
 * <p/>
 * User: yperez
 * Date: 19/09/11
 * Time: 16:08
 */
public class MzIdentMLTransformer {
    private static List<IdentifiableParamGroup> FragmentationTable = null;

    public static List<SourceFile> transformToSourceFile(List<uk.ac.ebi.jmzidml.model.mzidml.SourceFile> oldSourceFiles) {
        List<SourceFile> sourceFiles = null;

        if (oldSourceFiles != null) {
            sourceFiles = new ArrayList<SourceFile>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SourceFile oldsourcefile : oldSourceFiles) {
                String id = oldsourcefile.getId();
                String name = oldsourcefile.getName();
                String location = oldsourcefile.getLocation();
                CvParam format = transformToCvParam(oldsourcefile.getFileFormat().getCvParam());
                String formatDocumentation = oldsourcefile.getExternalFormatDocumentation();
                List<CvParam> cvParams = transformToCvParam(oldsourcefile.getCvParam());
                List<UserParam> userParams = transformToUserParam(oldsourcefile.getUserParam());
                sourceFiles.add(new SourceFile(new ParamGroup(cvParams, userParams), id, name, location, format, formatDocumentation));
            }
        }
        return sourceFiles;
    }

    private static List<UserParam> transformToUserParam(List<uk.ac.ebi.jmzidml.model.mzidml.UserParam> oldUserParams) {
        List<UserParam> userParams = null;
        if (oldUserParams != null) {
            userParams = new ArrayList<UserParam>();
            for (uk.ac.ebi.jmzidml.model.mzidml.UserParam oldUserParam : oldUserParams) {
                userParams.add(transformToUserParam(oldUserParam));
            }
        }
        return userParams;
    }

    private static List<CvParam> transformToCvParam(List<uk.ac.ebi.jmzidml.model.mzidml.CvParam> oldCvParams) {
        List<CvParam> cvParams = null;
        if (oldCvParams != null) {
            cvParams = new ArrayList<CvParam>();
            for (uk.ac.ebi.jmzidml.model.mzidml.CvParam oldCvParam : oldCvParams) {
                cvParams.add(transformToCvParam(oldCvParam));
            }
        }
        return cvParams;
    }

    public static List<Organization> transformToOrganization(List<uk.ac.ebi.jmzidml.model.mzidml.Organization> oldOrganizations) {
        List<Organization> organizations = null;
        if (oldOrganizations != null) {
            organizations = new ArrayList<Organization>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Organization oldOrganization : oldOrganizations) {
                //Todo: I need to solve the problem with mail and the parent organization
                organizations.add(transformToOrganization(oldOrganization));
            }
        }
        return organizations;
    }

    public static Organization transformToOrganization(uk.ac.ebi.jmzidml.model.mzidml.Organization oldOrganization) {
        Organization organization = null;
        if (oldOrganization != null) {
            Organization parentOrganization = null;
            if (oldOrganization.getParent() != null) {
                parentOrganization = transformToOrganization(oldOrganization.getParent().getOrganization());
            }
            organization = new Organization(new ParamGroup(transformToCvParam(oldOrganization.getCvParam()), transformToUserParam(oldOrganization.getUserParam())), oldOrganization.getId(), oldOrganization.getName(), parentOrganization, null);
        }
        return organization;
    }

    public static List<Organization> transformAffiliationToOrganization(List<uk.ac.ebi.jmzidml.model.mzidml.Affiliation> oldAffiliations) {
        List<Organization> organizations = null;
        if (oldAffiliations != null) {
            organizations = new ArrayList<Organization>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Affiliation oldAffiliation : oldAffiliations) {
                uk.ac.ebi.jmzidml.model.mzidml.Organization oldOrganization = oldAffiliation.getOrganization();
                organizations.add(transformToOrganization(oldOrganization));
            }
        }
        return organizations;
    }


    public static List<Person> transformToPerson(List<uk.ac.ebi.jmzidml.model.mzidml.Person> oldPersons) {
        List<Person> persons = null;
        if (oldPersons != null) {
            persons = new ArrayList<Person>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Person oldPerson : oldPersons) {
                persons.add(transformToPerson(oldPerson));
            }
        }
        return persons;
    }

    public static Person transformToPerson(uk.ac.ebi.jmzidml.model.mzidml.Person oldPerson) {
        if (oldPerson != null) {
            List<Organization> affiliation = transformAffiliationToOrganization(oldPerson.getAffiliation());
            //Todo: Take from Cv Params the value of the mail.
            return new Person(new ParamGroup(transformToCvParam(oldPerson.getCvParam()), transformToUserParam(oldPerson.getUserParam())), oldPerson.getId(), oldPerson.getName(), oldPerson.getLastName(), oldPerson.getFirstName(), oldPerson.getMidInitials(), affiliation, null);
        }

        return null;
    }


    public static List<Sample> transformToSample(List<uk.ac.ebi.jmzidml.model.mzidml.Sample> oldSamples) {
        List<Sample> samples = null;
        if (oldSamples != null) {
            samples = new ArrayList<Sample>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Sample oldSample : oldSamples) {
                samples.add(transformToSample(oldSample));
            }
        }
        return samples;
    }

    public static List<Sample> transformSubSampleToSample(List<uk.ac.ebi.jmzidml.model.mzidml.SubSample> oldSamples) {
        List<Sample> samples = null;
        if (oldSamples != null) {
            samples = new ArrayList<Sample>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SubSample oldSubSample : oldSamples) {
                samples.add(transformToSample(oldSubSample.getSample()));
            }
        }
        return samples;
    }

    public static Sample transformToSample(uk.ac.ebi.jmzidml.model.mzidml.Sample oldSample) {
        Sample sample = null;
        if (oldSample != null) {
            Map<AbstractContact, CvParam> role = transformToRoleList(oldSample.getContactRole());
            List<Sample> subSamples = null;
            if ((oldSample.getSubSample() != null) && (!oldSample.getSubSample().isEmpty())) {
                subSamples = transformSubSampleToSample(oldSample.getSubSample());
            }
            sample = new Sample(new ParamGroup(transformToCvParam(oldSample.getCvParam()), transformToUserParam(oldSample.getUserParam())), oldSample.getId(), oldSample.getName(), subSamples, role);
        }
        return sample;
    }

    private static Map<AbstractContact, CvParam> transformToRoleList(List<uk.ac.ebi.jmzidml.model.mzidml.ContactRole> contactRoles) {
        Map<AbstractContact, CvParam> contacts = null;
        if (contactRoles != null) {
            contacts = new HashMap<AbstractContact, CvParam>();
            for (uk.ac.ebi.jmzidml.model.mzidml.ContactRole oldRole : contactRoles) {
                AbstractContact contact = null;
                if (oldRole.getOrganization() != null) {
                    contact = transformToOrganization(oldRole.getOrganization());
                } else if (oldRole.getPerson() != null) {
                    contact = transformToPerson(oldRole.getPerson());
                }
                CvParam role = transformToCvParam(oldRole.getRole().getCvParam());
                contacts.put(contact, role);
            }
        }
        return contacts;
    }

    private static CvParam transformToCvParam(uk.ac.ebi.jmzidml.model.mzidml.CvParam oldCvParam) {
        CvParam newParam = null;
        if (oldCvParam != null) {
            String cvLookupID = null;
            uk.ac.ebi.jmzidml.model.mzidml.Cv cv = oldCvParam.getCv();
            if (cv != null) cvLookupID = cv.getId();
            String unitCVLookupID = null;
            cv = oldCvParam.getUnitCv();
            if (cv != null) unitCVLookupID = cv.getId();
            newParam = new CvParam(oldCvParam.getAccession(),
                    oldCvParam.getName(),
                    cvLookupID,
                    oldCvParam.getValue(),
                    oldCvParam.getUnitAccession(),
                    oldCvParam.getUnitName(), unitCVLookupID);
        }
        return newParam;
    }

    private static UserParam transformToUserParam(uk.ac.ebi.jmzidml.model.mzidml.UserParam oldUserParam) {
        UserParam newParam = null;
        if (oldUserParam != null) {
            String unitCVLookupID = null;
            uk.ac.ebi.jmzidml.model.mzidml.Cv cv = oldUserParam.getUnitCv();
            if (cv != null) unitCVLookupID = cv.getId();
            newParam = new UserParam(oldUserParam.getName(),
                    oldUserParam.getType(),
                    oldUserParam.getValue(),
                    oldUserParam.getUnitAccession(),
                    oldUserParam.getUnitName(),
                    unitCVLookupID);
        }
        return newParam;
    }

    public static List<Software> transformToSoftware(List<uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftware> oldSoftwares) {
        List<Software> softwares = null;
        if (oldSoftwares != null) {
            softwares = new ArrayList<Software>();
            for (uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftware oldSoftware : oldSoftwares) {
                softwares.add(transformToSoftware(oldSoftware));
            }
        }
        return softwares;
    }

    public static Software transformToSoftware(uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftware oldSoftware) {
        if (oldSoftware != null) {
            AbstractContact contact = null;
            if (oldSoftware.getContactRole().getOrganization() != null) {
                contact = transformToOrganization(oldSoftware.getContactRole().getOrganization());
            } else if (oldSoftware.getContactRole().getPerson() != null) {
                contact = transformToPerson(oldSoftware.getContactRole().getPerson());
            }
            return new Software(oldSoftware.getId(), oldSoftware.getName(), contact,oldSoftware.getCustomizations(), oldSoftware.getUri(), oldSoftware.getVersion());
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
            //Todo: Set the References ParamGroup for references
            Reference reference = new Reference(ref.getId(), ref.getName(), ref.getDoi(), ref.getTitle(), ref.getPages(), ref.getIssue(), ref.getVolume(), ref.getYear().toString(), ref.getEditor(), ref.getPublisher(), ref.getPublication(), ref.getAuthors(), refLine);
            references.add(reference);
        }
        return references;
    }

    public static Identification transformToIdentification(uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionHypothesis oldIdent, uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable) {
        Identification ident = null;
        if (FragmentationTable == null) {
            FragmentationTable = transformToFragmentationTable(oldFragmentationTable);
        }
        if (oldIdent != null) {
            Map<PeptideEvidence, List<Peptide>> peptides = transformToPeptideIdentifications(oldIdent.getPeptideHypothesis(), oldFragmentationTable);
            ParamGroup paramGroup = new ParamGroup(transformToCvParam(oldIdent.getCvParam()),transformToUserParam(oldIdent.getUserParam()));
            Score score = transformScore(paramGroup);
            ident = new Identification(paramGroup, oldIdent.getId(), oldIdent.getName(), transformToDBSequence(oldIdent.getDBSequence()), oldIdent.isPassThreshold(), peptides, score, -1, -1,null);
            //Todo: threshold and sequence coverage
        }
        return ident;
    }

    public static List<IdentifiableParamGroup> transformToFragmentationTable(uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable) {
        List<IdentifiableParamGroup> fragmentationTable = null;
        if (oldFragmentationTable != null) {
            fragmentationTable = new ArrayList<IdentifiableParamGroup>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Measure oldMeasure : oldFragmentationTable.getMeasure()) {
                fragmentationTable.add(new IdentifiableParamGroup(new ParamGroup(transformToCvParam(oldMeasure.getCvParam()), null), oldMeasure.getId(), oldMeasure.getName()));
            }
        }
        return fragmentationTable;
    }

    private static Map<PeptideEvidence, List<Peptide>> transformToPeptideIdentifications(List<uk.ac.ebi.jmzidml.model.mzidml.PeptideHypothesis> peptideHypothesis, uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable) {
        Map<PeptideEvidence, List<Peptide>> peptides = null;
        if (peptideHypothesis != null) {
            peptides = new HashMap<PeptideEvidence, List<Peptide>>();
            for (uk.ac.ebi.jmzidml.model.mzidml.PeptideHypothesis oldPeptideHypothesis : peptideHypothesis) {
                PeptideEvidence peptideEvidence = transformToPeptideEvidence(oldPeptideHypothesis.getPeptideEvidence());
                //System.out.println("1"); aqui
                List<Peptide> peptideIdentified = transformToPeptideIdentification(oldPeptideHypothesis.getSpectrumIdentificationItemRef(), oldFragmentationTable);
                //System.out.println("2");
                peptides.put(peptideEvidence, peptideIdentified);
                //System.out.println("3");
            }
        }
        return peptides;
    }

    public static List<Peptide> transformToPeptideIdentification(List<uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItemRef> spectrumIdentificationItemRefs, uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable) {
        List<Peptide> peptides = null;
        if (spectrumIdentificationItemRefs != null) {
            peptides = new ArrayList<Peptide>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItemRef oldSpectrumIdentificationItemRef : spectrumIdentificationItemRefs) {
                uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem oldPeptideIdentification = oldSpectrumIdentificationItemRef.getSpectrumIdentificationItem();
                Peptide peptide = transformToPeptideIdentification(oldPeptideIdentification, oldFragmentationTable);
                peptides.add(peptide);
            }
        }
        return peptides;
    }

    public static Peptide transformToPeptideIdentification(uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem oldSpectrumIdentification, uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable) {
        Peptide peptide = null;
        if (oldSpectrumIdentification != null) {
            String id = oldSpectrumIdentification.getId();
            String name = oldSpectrumIdentification.getName();
            int chargeState = oldSpectrumIdentification.getChargeState();
            double massToCharge = oldSpectrumIdentification.getExperimentalMassToCharge();
            double calcMassToCharge = oldSpectrumIdentification.getCalculatedMassToCharge();
            float pI = (float) 0.0;
            if(oldSpectrumIdentification.getCalculatedPI() != null){
               pI = oldSpectrumIdentification.getCalculatedPI();
            }
            uk.ac.ebi.jmzidml.model.mzidml.Peptide peptideSeq = oldSpectrumIdentification.getPeptide();
            int rank = oldSpectrumIdentification.getRank();
            boolean passThrehold = oldSpectrumIdentification.isPassThreshold();
            uk.ac.ebi.jmzidml.model.mzidml.MassTable massTable  = oldSpectrumIdentification.getMassTable();
            uk.ac.ebi.jmzidml.model.mzidml.Sample sample = oldSpectrumIdentification.getSample();
            List<uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidenceRef> peptideEvidence = oldSpectrumIdentification.getPeptideEvidenceRef();
            uk.ac.ebi.jmzidml.model.mzidml.Fragmentation fragmentation = oldSpectrumIdentification.getFragmentation();
            ParamGroup scoreParamGroup = new ParamGroup(transformToCvParam(oldSpectrumIdentification.getCvParam()),transformToUserParam(oldSpectrumIdentification.getUserParam()));
            peptide = new Peptide(id,
                                  name,
                                  chargeState,
                                  massToCharge,
                                  calcMassToCharge,
                                  pI,
                                  transformToPeptide(peptideSeq),
                                  rank,
                                  passThrehold,
                                  transformToMassTable(massTable),
                                  transformToSample(sample),
                                  transformToPeptideEvidence(peptideEvidence),
                                  transformToFragmentationIon(fragmentation, oldFragmentationTable), transformScore(scoreParamGroup), null, null);
            //Todo: Peptide SpectraData
        }
        return peptide;
    }

    private static Score transformScore(ParamGroup paramGroup) {
        List<SearchEngineType> types = DataAccessUtilities.getSearchEngineTypes(paramGroup);
        Score score = DataAccessUtilities.getPeptideScore(paramGroup,types);
        return score;
    }

    private static List<FragmentIon> transformToFragmentationIon(uk.ac.ebi.jmzidml.model.mzidml.Fragmentation fragmentation, uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable) {
        List<FragmentIon> fragmentIons = null;
        if (FragmentationTable == null) {
            FragmentationTable = transformToFragmentationTable(oldFragmentationTable);
        }
        if (fragmentation != null) {
            fragmentIons = new ArrayList<FragmentIon>();
            for (uk.ac.ebi.jmzidml.model.mzidml.IonType ionType : fragmentation.getIonType()) {

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

                if (ionTypeChar == null) continue;
                // ignore IonTypes with no index set
                //if (ionType.getIndex() == null)  continue;

                // iterate over the ion type indexes
                for (Integer index = 0; index < ionType.getIndex().size(); index++) {
                    //FragmentIon fragmentIon = new FragmentIon();
                    List<CvParam> cvParams = new ArrayList<CvParam>();
                    // charge
                    CvTermReference cvCharge = CvTermReference.PRODUCT_ION_CHARGE;
                    cvParams.add(new CvParam(cvCharge.getAccession(), cvCharge.getName(), cvCharge.getCvLabel(), String.valueOf(ionType.getCharge()), null, null, null));
                    //ion type
                    cvParams.add(new CvParam(ionType.getCvParam().getAccession(), ionType.getCvParam().getName(), ionType.getCvParam().getCvRef(), ionType.getCvParam().getValue(), null, null, null));
                    //mz
                    for (uk.ac.ebi.jmzidml.model.mzidml.FragmentArray fragArr : ionType.getFragmentArray()) {
                        uk.ac.ebi.jmzidml.model.mzidml.Measure oldMeasure = fragArr.getMeasure();
                        CvParam cvParam;
                        CvTermReference cvMz;
                        cvMz = CvTermReference.PRODUCT_ION_MZ;
                        cvParam = getCvParamByID(oldMeasure.getCvParam(), cvMz.getAccession(), fragArr.getValues().get(index).toString());
                        if (cvParam == null) {
                            cvMz = CvTermReference.PRODUCT_ION_INTENSITY;
                            cvParam = getCvParamByID(oldMeasure.getCvParam(), cvMz.getAccession(), fragArr.getValues().get(index).toString());
                            if (cvParam == null) {
                                cvMz = CvTermReference.PRODUCT_ION_MASS_ERROR;
                                cvParam = getCvParamByID(oldMeasure.getCvParam(), cvMz.getAccession(), fragArr.getValues().get(index).toString());
                                if (cvParam == null) {
                                    cvMz = CvTermReference.PRODUCT_ION_RETENTION_TIME_ERROR;
                                    cvParam = getCvParamByID(oldMeasure.getCvParam(), cvMz.getAccession(), fragArr.getValues().get(index).toString());
                                }
                            }
                        }
                        if (cvParam != null) {
                            cvParams.add(cvParam);
                        }
                    }
                    fragmentIons.add(new FragmentIon(new ParamGroup(cvParams, null)));
                }
            }
        }
        return fragmentIons;
    }

    private static CvParam getCvParamByID(List<uk.ac.ebi.jmzidml.model.mzidml.CvParam> oldCvParams, String accession, String newValue) {
        for (uk.ac.ebi.jmzidml.model.mzidml.CvParam oldCvParam : oldCvParams) {
            if (oldCvParam.getAccession().equalsIgnoreCase(accession)) {
                CvParam cvParam = transformToCvParam(oldCvParam);
                cvParam.setValue(newValue);
                return cvParam;
            }
        }
        return null;
    }

    private static List<MassTable> transformToMassTable(List<uk.ac.ebi.jmzidml.model.mzidml.MassTable> oldMassTables) {
        List<MassTable> massTables = null;
        if (oldMassTables != null) {
            massTables = new ArrayList<MassTable>();
            for (uk.ac.ebi.jmzidml.model.mzidml.MassTable oldMassTable : oldMassTables) {
                massTables.add(transformToMassTable(oldMassTable));
            }
        }
        return massTables;
    }

    private static MassTable transformToMassTable(uk.ac.ebi.jmzidml.model.mzidml.MassTable oldMassTable) {
        MassTable massTable = null;
        if (oldMassTable != null) {
            Map<String, Float> residues = new HashMap<String, Float>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Residue residue : oldMassTable.getResidue()) {
                residues.put(residue.getCode(), residue.getMass());
            }
            Map<String, ParamGroup> ambiguousResidues = new HashMap<String, ParamGroup>();
            for (uk.ac.ebi.jmzidml.model.mzidml.AmbiguousResidue residue : oldMassTable.getAmbiguousResidue()) {
                ambiguousResidues.put(residue.getCode(), new ParamGroup(transformToCvParam(residue.getCvParam()), transformToUserParam(residue.getUserParam())));
            }
            massTable = new MassTable(oldMassTable.getMsLevel(), residues, ambiguousResidues);
        }
        return massTable;
    }

    private static PeptideEvidence transformToPeptideEvidence(uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidence oldPeptideEvidence) {
        PeptideEvidence evidence = null;
        if (oldPeptideEvidence != null) {
            evidence = new PeptideEvidence(oldPeptideEvidence.getId(), oldPeptideEvidence.getName(), oldPeptideEvidence.getStart(), oldPeptideEvidence.getEnd(), oldPeptideEvidence.isIsDecoy(), transformToPeptide(oldPeptideEvidence.getPeptide()), transformToDBSequence(oldPeptideEvidence.getDBSequence()));
        }
        return evidence;
    }

    private static List<PeptideEvidence> transformToPeptideEvidence(List<uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidenceRef> oldPeptideEvidenceRefs) {
        List<PeptideEvidence> peptideEvidences = null;
        if (oldPeptideEvidenceRefs != null) {
            peptideEvidences = new ArrayList<PeptideEvidence>();
            for (uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidenceRef oldPeptideEvidenceRef : oldPeptideEvidenceRefs) {
                peptideEvidences.add(transformToPeptideEvidence(oldPeptideEvidenceRef.getPeptideEvidence()));
            }
        }
        return peptideEvidences;
    }

    private static PeptideSequence transformToPeptide(uk.ac.ebi.jmzidml.model.mzidml.Peptide oldPeptide) {
        PeptideSequence peptideSequence = null;
        if (oldPeptide != null) {
            peptideSequence = new PeptideSequence(oldPeptide.getId(), oldPeptide.getName(), oldPeptide.getPeptideSequence(), transformToModification(oldPeptide.getModification()), transformToSubstitutionMod(oldPeptide.getSubstitutionModification()));
        }
        return peptideSequence;
    }

    private static List<Modification> transformToModification(List<uk.ac.ebi.jmzidml.model.mzidml.Modification> oldModifications) {
        List<Modification> modifications = null;
        if (oldModifications != null) {
            modifications = new ArrayList<Modification>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Modification oldModification : oldModifications) {
                modifications.add(transformToModification(oldModification));
            }
        }
        return modifications;
    }

    private static Modification transformToModification(uk.ac.ebi.jmzidml.model.mzidml.Modification oldModification) {
        Modification modification = null;
        if (oldModification != null) {
            List<Double> monoMasses = null;
            List<Double> avgMasses  = null;
            if(oldModification.getMonoisotopicMassDelta() != null){
                monoMasses = new ArrayList<Double>();
                monoMasses.add(oldModification.getMonoisotopicMassDelta());
            }
            if(oldModification.getAvgMassDelta() != null){
                avgMasses = new ArrayList<Double>();
                avgMasses.add(oldModification.getAvgMassDelta());
            }
            List<CvParam> cvParams = transformToCvParam(oldModification.getCvParam());
            String id = null;
            String name = null;
            String dataBaseName = null;
            //Todo: Try to make this function more flexible, we can define default Mod Databases
            for(int i = 0; i < cvParams.size();i++ ){
               if(cvParams.get(i).getCvLookupID().compareToIgnoreCase("MOD") ==0){
                   id = cvParams.get(i).getAccession();
                   name = cvParams.get(i).getName();
                   dataBaseName = (cvParams.get(i).getCvLookupID() == null)?"MOD":cvParams.get(i).getCvLookupID();
                   break;
               }else if(cvParams.get(i).getCvLookupID().compareToIgnoreCase("UNIMOD") ==0){
                   id = cvParams.get(i).getAccession();
                   name = cvParams.get(i).getName();
                   dataBaseName = (cvParams.get(i).getCvLookupID() == null)?"UNIMOD":cvParams.get(i).getCvLookupID();
               }
            }
            ParamGroup param = new ParamGroup(cvParams,null);
            modification = new Modification(param, id, name, oldModification.getLocation(), oldModification.getResidues(), avgMasses, monoMasses,dataBaseName,null);
        }
        return modification;
    }

    private static List<SubstitutionModification> transformToSubstitutionMod(List<uk.ac.ebi.jmzidml.model.mzidml.SubstitutionModification> oldSubstitutionModifications) {
        List<SubstitutionModification> modifications = null;
        if (oldSubstitutionModifications != null) {
            modifications = new ArrayList<SubstitutionModification>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SubstitutionModification oldModification : oldSubstitutionModifications) {
                modifications.add(transformToSubstitutionMod(oldModification));
            }
        }
        return modifications;
    }

    private static SubstitutionModification transformToSubstitutionMod(uk.ac.ebi.jmzidml.model.mzidml.SubstitutionModification oldModification) {
        SubstitutionModification modification = null;
        if (oldModification != null) {
            double avgMass  = (oldModification.getAvgMassDelta() != null)?oldModification.getAvgMassDelta().doubleValue():-1.0;
            double monoMass = (oldModification.getMonoisotopicMassDelta()!=null)?oldModification.getMonoisotopicMassDelta().doubleValue():-1.0;
            int location = (oldModification.getLocation()!=null)?oldModification.getLocation().intValue():-1;
            modification = new SubstitutionModification(oldModification.getOriginalResidue(), oldModification.getReplacementResidue(), location, avgMass, monoMass);
        }
        return modification;
    }

    private static DBSequence transformToDBSequence(uk.ac.ebi.jmzidml.model.mzidml.DBSequence oldDbSequence) {
        DBSequence dbSequence = null;
        if (oldDbSequence != null) {
            String id = oldDbSequence.getId();
            String name = oldDbSequence.getName();
            int length = (oldDbSequence.getLength() != null)?oldDbSequence.getLength().intValue():-1;
            String accession = oldDbSequence.getAccession();
            ParamGroup params = new ParamGroup(transformToCvParam(oldDbSequence.getCvParam()),transformToUserParam(oldDbSequence.getUserParam()));
            dbSequence = new DBSequence(params,id, name, length, accession, transformToSeachDatabase(oldDbSequence.getSearchDatabase()), oldDbSequence.getSeq(), null, null);
        }
        return dbSequence;
    }

    private static SearchDataBase transformToSeachDatabase(uk.ac.ebi.jmzidml.model.mzidml.SearchDatabase oldDatabase) {
        CvParam fileFormat = (oldDatabase.getFileFormat() == null) ? null : transformToCvParam(oldDatabase.getFileFormat().getCvParam());
        String releaseDate = (oldDatabase.getReleaseDate() == null) ? null : oldDatabase.getReleaseDate().toString();
        int dataBaseSeq = (oldDatabase.getNumDatabaseSequences() == null) ? -1 : oldDatabase.getNumDatabaseSequences().intValue();
        int dataBaseRes = (oldDatabase.getNumResidues() == null) ? -1 : oldDatabase.getNumResidues().intValue();
        ParamGroup nameOfDatabase = null;
        if (oldDatabase.getDatabaseName() != null) {
            nameOfDatabase = new ParamGroup(transformToCvParam(oldDatabase.getDatabaseName().getCvParam()), transformToUserParam(oldDatabase.getDatabaseName().getUserParam()));
        }
        return new SearchDataBase(oldDatabase.getId(),
                oldDatabase.getName(), oldDatabase.getLocation(), fileFormat, oldDatabase.getExternalFormatDocumentation(), oldDatabase.getVersion(), releaseDate, dataBaseSeq, dataBaseRes, nameOfDatabase, transformToCvParam(oldDatabase.getCvParam()));
    }

    public static List<CVLookup> transformCVList(List<uk.ac.ebi.jmzidml.model.mzidml.Cv> cvList) {
        List<CVLookup> cvLookups = null;
        if (cvList != null) {
            cvLookups = new ArrayList<CVLookup>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Cv cv : cvList) {
                cvLookups.add(transformToCVLookup(cv));
            }
        }
        return cvLookups;
    }

    public static CVLookup transformToCVLookup(uk.ac.ebi.jmzidml.model.mzidml.Cv oldCv) {
        CVLookup cvLookup = null;
        if (oldCv != null) {
            cvLookup = new CVLookup(oldCv.getId(), oldCv.getFullName(),
                    oldCv.getVersion(), oldCv.getUri());
        }
        return cvLookup;
    }

    public static Provider transformToProvider(uk.ac.ebi.jmzidml.model.mzidml.Provider oldProvider) {
        Provider provider = null;
        if (oldProvider != null) {
            AbstractContact abstractContact = null;
            if (oldProvider.getContactRole().getOrganization() != null) {
                abstractContact = transformToOrganization(oldProvider.getContactRole().getOrganization());
            } else if (oldProvider.getContactRole().getPerson() != null) {
                abstractContact = transformToPerson(oldProvider.getContactRole().getPerson());
            }
            CvParam role = transformToCvParam(oldProvider.getContactRole().getRole().getCvParam());
            Software software = transformToSoftware(oldProvider.getSoftware());
            provider = new Provider(oldProvider.getId(), oldProvider.getName(), software, abstractContact, role);
        }
        return provider;
    }

    public static List<SpectrumIdentificationProtocol> transformToSpectrumIdentificationProtocol(List<uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationProtocol> oldSpecProtocol) {
        List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList = null;
        if (oldSpecProtocol != null) {
            spectrumIdentificationProtocolList = new ArrayList<SpectrumIdentificationProtocol>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationProtocol oldProtocol : oldSpecProtocol) {
                spectrumIdentificationProtocolList.add(transformToSpectrumIdentificationProtocol(oldProtocol));
            }
        }
        return spectrumIdentificationProtocolList;

    }

    public static SpectrumIdentificationProtocol transformToSpectrumIdentificationProtocol(uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationProtocol oldProtocol) {
        SpectrumIdentificationProtocol spectrumIdentificationProtocol = null;
        if (oldProtocol != null) {
            Software analysisSoftware = transformToSoftware(oldProtocol.getAnalysisSoftware());
            ParamGroup threshold = new ParamGroup(transformToCvParam(oldProtocol.getThreshold().getCvParam()), transformToUserParam(oldProtocol.getThreshold().getUserParam()));
            ParamGroup searchType = new ParamGroup(transformToCvParam(oldProtocol.getSearchType().getCvParam()), transformToUserParam(oldProtocol.getSearchType().getUserParam()));
            boolean enzymeIndependent = (oldProtocol.getEnzymes().isIndependent() == null) ? false : oldProtocol.getEnzymes().isIndependent();
            List<Enzyme> enzymeList = transformToEnzyme(oldProtocol.getEnzymes().getEnzyme());
            List<CvParam> fragmentTolerance = transformToCvParam(oldProtocol.getFragmentTolerance().getCvParam());
            List<CvParam> parentTolerance = transformToCvParam(oldProtocol.getParentTolerance().getCvParam());
            List<Filter> filterList = transformToFilter(oldProtocol.getDatabaseFilters().getFilter());
            DataBaseTranslation dataBaseTranslation = transformToDataBaseTranslation(oldProtocol.getDatabaseTranslation());
            List<SearchModification> searchModificationList = transformToSearchModification(oldProtocol.getModificationParams().getSearchModification());
            List<MassTable> massTableList = transformToMassTable(oldProtocol.getMassTable());
            spectrumIdentificationProtocol = new SpectrumIdentificationProtocol(new ParamGroup(transformToCvParam(oldProtocol.getAdditionalSearchParams().getCvParam()), transformToUserParam(oldProtocol.getAdditionalSearchParams().getUserParam())), oldProtocol.getId(), oldProtocol.getName(), analysisSoftware, threshold, searchType, searchModificationList, enzymeIndependent, enzymeList, massTableList, fragmentTolerance, parentTolerance, filterList, dataBaseTranslation);
        }
        return spectrumIdentificationProtocol;
    }

    private static SearchModification transformToSearchModification(uk.ac.ebi.jmzidml.model.mzidml.SearchModification oldModification) {
        SearchModification searchModification = null;
        if (oldModification != null) {
            List<CvParam> rules = (oldModification.getSpecificityRules() == null) ? null : transformToCvParam(oldModification.getSpecificityRules().getCvParam());
            searchModification = new SearchModification(oldModification.isFixedMod(), oldModification.getMassDelta(), oldModification.getResidues(), rules, transformToCvParam(oldModification.getCvParam()));
        }
        return searchModification;
    }

    private static List<SearchModification> transformToSearchModification(List<uk.ac.ebi.jmzidml.model.mzidml.SearchModification> oldSearchModifications) {
        List<SearchModification> searchModifications = null;
        if (oldSearchModifications != null) {
            searchModifications = new ArrayList<SearchModification>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SearchModification oldSearchModification : oldSearchModifications) {
                searchModifications.add(transformToSearchModification(oldSearchModification));
            }
        }
        return searchModifications;
    }


    private static DataBaseTranslation transformToDataBaseTranslation(uk.ac.ebi.jmzidml.model.mzidml.DatabaseTranslation oldDatabaseTranslation) {
        DataBaseTranslation dataBaseTranslation = null;
        if (oldDatabaseTranslation != null) {
            List<IdentifiableParamGroup> translationTable = new ArrayList<IdentifiableParamGroup>();
            for (uk.ac.ebi.jmzidml.model.mzidml.TranslationTable oldTranslationTable : oldDatabaseTranslation.getTranslationTable()) {
                translationTable.add(new IdentifiableParamGroup(new ParamGroup(transformToCvParam(oldTranslationTable.getCvParam()), null), oldTranslationTable.getId(), oldTranslationTable.getName()));
            }
            dataBaseTranslation = new DataBaseTranslation(oldDatabaseTranslation.getFrames(), translationTable);
        }
        return dataBaseTranslation;
    }

    private static List<Filter> transformToFilter(List<uk.ac.ebi.jmzidml.model.mzidml.Filter> oldFilters) {
        List<Filter> filters = null;
        if (oldFilters != null) {
            filters = new ArrayList<Filter>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Filter oldFilter : oldFilters) {
                ParamGroup filterType = null;
                if (oldFilter.getFilterType() != null) {
                    filterType = new ParamGroup(transformToCvParam(oldFilter.getFilterType().getCvParam()),
                            transformToUserParam(oldFilter.getFilterType().getUserParam()));
                }
                ParamGroup include = null;
                if (oldFilter.getInclude() != null) {
                    include = new ParamGroup(transformToCvParam(oldFilter.getInclude().getCvParam()),
                            transformToUserParam(oldFilter.getInclude().getUserParam()));
                }
                ParamGroup exclude = null;
                if (oldFilter.getExclude() != null) {
                    exclude = new ParamGroup(transformToCvParam(oldFilter.getExclude().getCvParam()),
                            transformToUserParam(oldFilter.getExclude().getUserParam()));
                }
                filters.add(new Filter(filterType, include, exclude));
            }
        }
        return filters;
    }

    private static List<Enzyme> transformToEnzyme(List<uk.ac.ebi.jmzidml.model.mzidml.Enzyme> oldEnzymes) {
        List<Enzyme> enzymes = null;
        if (oldEnzymes != null) {
            enzymes = new ArrayList<Enzyme>();
            for (uk.ac.ebi.jmzidml.model.mzidml.Enzyme oldEnzyme : oldEnzymes) {
                enzymes.add(transformToEnzyme(oldEnzyme));
            }
        }
        return enzymes;
    }

    private static Enzyme transformToEnzyme(uk.ac.ebi.jmzidml.model.mzidml.Enzyme oldEnzyme) {
        Enzyme newEnzyme = null;
        if (oldEnzyme != null) {
            boolean specific = (oldEnzyme.isSemiSpecific() == null) ? false : oldEnzyme.isSemiSpecific();
            int misscleavage = (oldEnzyme.getMissedCleavages() == null) ? 0 : oldEnzyme.getMissedCleavages();
            int mindistance = (oldEnzyme.getMinDistance() == null) ? -1 : oldEnzyme.getMinDistance();

            newEnzyme = new Enzyme(oldEnzyme.getId(),
                    oldEnzyme.getName(),
                    specific,
                    misscleavage,
                    mindistance,
                    new ParamGroup(transformToCvParam(oldEnzyme.getEnzymeName().getCvParam()), transformToUserParam(oldEnzyme.getEnzymeName().getUserParam())),
                    oldEnzyme.getSiteRegexp());
        }
        return newEnzyme;
    }

    public static Protocol transformToProteinDetectionProtocol(uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionProtocol oldProteinDetectionProtocol) {
        Protocol proteinDetectionProtocol = null;
        if (oldProteinDetectionProtocol != null) {
            proteinDetectionProtocol = new Protocol(new ParamGroup(transformToCvParam(oldProteinDetectionProtocol.getAnalysisParams().getCvParam()), transformToUserParam(oldProteinDetectionProtocol.getAnalysisParams().getUserParam())),
                    oldProteinDetectionProtocol.getId(),
                    oldProteinDetectionProtocol.getName(),
                    transformToSoftware(oldProteinDetectionProtocol.getAnalysisSoftware()),
                    new ParamGroup(transformToCvParam(oldProteinDetectionProtocol.getThreshold().getCvParam()), transformToUserParam(oldProteinDetectionProtocol.getThreshold().getUserParam())));

        }
        return proteinDetectionProtocol;
    }

    public static List<SearchDataBase> transformToSearchDataBase(List<uk.ac.ebi.jmzidml.model.mzidml.SearchDatabase> oldSearchDatabases) {
        List<SearchDataBase> searchDataBases = null;
        if (oldSearchDatabases != null) {
            searchDataBases = new ArrayList<SearchDataBase>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SearchDatabase oldSearchDatabase : oldSearchDatabases) {
                searchDataBases.add(transformToSeachDatabase(oldSearchDatabase));
            }
        }
        return searchDataBases;
    }

    private static SpectraData transformToSpectraData(uk.ac.ebi.jmzidml.model.mzidml.SpectraData oldSpectraData) {
        SpectraData spectraData = null;
        if (oldSpectraData != null) {
            CvParam fileFormat = (oldSpectraData.getFileFormat() == null) ? null : transformToCvParam(oldSpectraData.getFileFormat().getCvParam());
            CvParam spectrumId = (oldSpectraData.getSpectrumIDFormat().getCvParam() == null) ? null : transformToCvParam(oldSpectraData.getSpectrumIDFormat().getCvParam());
            spectraData = new SpectraData(oldSpectraData.getId(), oldSpectraData.getName(), oldSpectraData.getLocation(), fileFormat, oldSpectraData.getExternalFormatDocumentation(), spectrumId);
        }
        return spectraData;
    }

    public static List<SpectraData> transformToSpectraData(List<uk.ac.ebi.jmzidml.model.mzidml.SpectraData> oldSpectraDatas) {
        List<SpectraData> spectraDatas = null;
        if (oldSpectraDatas != null) {
            spectraDatas = new ArrayList<SpectraData>();
            for (uk.ac.ebi.jmzidml.model.mzidml.SpectraData oldSpectraData : oldSpectraDatas) {
                spectraDatas.add(transformToSpectraData(oldSpectraData));
            }
        }
        return spectraDatas;
    }
}
