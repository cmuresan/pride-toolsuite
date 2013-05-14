package uk.ac.ebi.pride.data.controller.cache.impl;

import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidence;
import uk.ac.ebi.jmzidml.model.mzidml.SpectraData;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationResult;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.io.file.MzIdentMLUnmarshallerAdaptor;
import uk.ac.ebi.pride.data.utils.MzIdentMLUtils;

import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;
import java.util.*;

/**
 * The MzIdentMLCacheBuilder initialize the cache for mzidentml file  reading.
 * <p/>
 * User: yperez
 * Date: 19/09/11
 * Time: 16:44
 */
public class MzIdentMLCacheBuilder extends AbstractAccessCacheBuilder {

    private final MzIdentMLUnmarshallerAdaptor unmarshaller;

    public MzIdentMLCacheBuilder(MzIdentMLControllerImpl c) {
        super(c);
        this.unmarshaller = ((MzIdentMLControllerImpl) controller).getUnmarshaller();
    }

    /**
     * Spectrum ids and identification ids are cached.
     *
     * @throws Exception error while caching the ids.
     */
    @Override
    public void populate() throws Exception {
        super.populate();

        /* Get a preScan of the File, the PreCan of the mzidentml File gets the information
         * about all the spectrums, protein identifications, and peptide-spectrum matchs with the
         * same structure that currently follow the mzidentml library.
         * */
        Set<String> proteinAmbiguityGroupIds = unmarshaller.getIDsForElement(MzIdentMLElement.ProteinAmbiguityGroup);
        if (proteinAmbiguityGroupIds != null && !proteinAmbiguityGroupIds.isEmpty()) {
            getProteinAmbiguityGroupMaps(proteinAmbiguityGroupIds);
        }
        getPreScanIdMaps();
    }

    private void getProteinAmbiguityGroupMaps(Set<String> proteinAmbiguityGroupIds) throws ConfigurationException, JAXBException {
        cache.clear(CacheCategory.PROTEIN_GROUP_ID);
        cache.storeInBatch(CacheCategory.PROTEIN_GROUP_ID, new ArrayList<Comparable>(proteinAmbiguityGroupIds));

        List<Comparable> proteinHIds = new ArrayList<Comparable>(unmarshaller.getIDsForElement(MzIdentMLElement.ProteinDetectionHypothesis));
        Map<Comparable, Comparable> proteinHypothesisMap = new HashMap<Comparable, Comparable>();
        for (Comparable id : proteinHIds) {
            proteinHypothesisMap.put(unmarshaller.getDBSequencebyProteinHypothesis(id), id);
        }

        cache.clear(CacheCategory.PROTEIN_TO_PROTEIN_GROUP_ID);
        cache.storeInBatch(CacheCategory.PROTEIN_TO_PROTEIN_GROUP_ID, proteinHypothesisMap);
    }

    /**
     * This function try to Map in memory ids mapping and relation for an mzidentml file. The structure of the
     * mzidentml files is from spectrum->peptide->protein, but most for the end users is more interesting to
     * have an information structure from protein->peptide->spectrum. The function take the information from
     * spectrumItems and read the Peptide Evidences and the Proteins related with these peptideEvidence. Finally
     * the function construct a map in from proteins to spectrums named identProteinsMap.
     *
     * @return
     * @throws javax.naming.ConfigurationException
     *
     */
    public void getPreScanIdMaps() throws ConfigurationException {

        /**
         * Map of IDs to SpectraData, e.g. IDs to spectra files
         */
        Map<Comparable, SpectraData> spectraDataIds = unmarshaller.getSpectraDataMap();


        /**
         * First Map is the Relation between an Spectrum file and all the Spectrums ids in the file
         * This information is useful to retrieve the for each spectrum File with spectrums are really
         * SpectrumIdentificationItems. For PRIDE Inspector is important for one of the windows that
         * shows the number of missing spectrum for an mzidentml file.
         * Map of SpectraData IDs to List of spectrum IDs, e.g. which spectra come from which file
         */
        Map<Comparable, List<Comparable>> spectraDataMap = new HashMap<Comparable, List<Comparable>>(spectraDataIds.size());

        /**
         * The relation between the peptide evidence and the spectrumIdentificationItem.
         * This map allow the access to the peptide evidence and spectrum information
         * without the Protein information.
         * ???? PeptideEvidence ????
         *
         * Map of SII IDs to a String[2] of spectrum ID and spectrum file ID
         */
        Map<Comparable, String[]> identSpectrumMap = new HashMap<Comparable, String[]>();


        /**
         * List of PSMs, e.g. SpectrumIdentificationResult IDs
         */
        Set<String> spectrumIdentResultIds = unmarshaller.getIDsForElement(MzIdentMLElement.SpectrumIdentificationResult);

        /**
         * This Protein Map represents the Protein identification in the DBSequence Section that contains SpectrumIdentification Items
         * Each key is the Protein Id, the Map related with each key is a Peptide Evidence Map. Each Peptide Evidence Map contains a key
         * of the for a PeptideEvidence and a list of SpectrumIdentificationItems. Each Sepctrum Identification Item is that contains
         * the original id of the spectrum in the Spectrum file and the id of the spectrum file.
         *
         */
        Map<Comparable, Map<Comparable, List<String[]>>> identProteinsMap = new HashMap<Comparable, Map<Comparable, List<String[]>>>();

        for (String spectrumIdentResultId : spectrumIdentResultIds) {

            Map<String, String> spectrumIdentificationResultAttributes = unmarshaller.getElementAttributes(spectrumIdentResultId, SpectrumIdentificationResult.class);
            String spectrumDataReference = spectrumIdentificationResultAttributes.get("spectraData_ref");
            String spectrumID = spectrumIdentificationResultAttributes.get("spectrumID");

            // fill the SpectraDataMap
            // for the currently referenced spectra file, retrieve the List (if it exists already) that is to store all the spectra IDs
            List<Comparable> spectrumIds = spectraDataMap.get(spectrumDataReference);
            // if there is no spectra ID list for the spectrum file yet, then create one and add it to the map
            if (spectrumIds == null) {
                spectrumIds = new ArrayList<Comparable>();
                spectraDataMap.put(spectrumDataReference, spectrumIds);
            }
            // add the spectrum ID to the list of spectrum IDs for the current spectrum file
            spectrumIds.add(spectrumID);

            // proceed to populate the identSpectrumMap
            Set<String> spectrumIdentItemIds = unmarshaller.getSpectrumIdentificationItemIds(spectrumIdentResultId);
            for (String spectrumIdentItemId : spectrumIdentItemIds) {

                // fill the SpectrumIdentification and the Spectrum information
                SpectraData spectraData = spectraDataIds.get(spectrumDataReference);

                // extract the spectrum ID from the provided identifier
                String formattedSpectrumID = MzIdentMLUtils.getSpectrumId(spectraData, spectrumID);
                String[] spectrumFeatures = {formattedSpectrumID, spectrumDataReference};

                identSpectrumMap.put(spectrumIdentItemId, spectrumFeatures);

                List<String[]> evidences = new ArrayList<String[]>();
                Set<Comparable> idProteins = new HashSet<Comparable>();
                Set<String> peptideEvidenceReferences = unmarshaller.getPeptideEvidenceReferences(spectrumIdentResultId, spectrumIdentItemId);
                for (String peptideEvidenceReference : peptideEvidenceReferences) {
                    String[] evidence = new String[2];
                    evidence[0] = peptideEvidenceReference;
                    Map<String, String> attributes = unmarshaller.getElementAttributes(peptideEvidenceReference, PeptideEvidence.class);
                    evidence[1] = attributes.get("dBSequence_ref");
                    evidences.add(evidence);
                    idProteins.add(evidence[1]);
                }

                for (Comparable idProtein : idProteins) {
                    Map<Comparable, List<String[]>> spectrumIdentifications = identProteinsMap.get(idProtein);
                    if (spectrumIdentifications == null) {
                        spectrumIdentifications = new HashMap<Comparable, List<String[]>>();
                        identProteinsMap.put(idProtein, spectrumIdentifications);
                    }
                    spectrumIdentifications.put(spectrumIdentItemId, evidences);
                }
            }
        }

        // Protein To to Peptides Evidences, It retrieve the peptides per Proteins
        cache.clear(CacheCategory.PROTEIN_TO_PEPTIDE_EVIDENCES);
        cache.storeInBatch(CacheCategory.PROTEIN_TO_PEPTIDE_EVIDENCES, identProteinsMap);

        cache.clear(CacheCategory.PROTEIN_ID);
        cache.storeInBatch(CacheCategory.PROTEIN_ID, new ArrayList<Comparable>(identProteinsMap.keySet()));

        cache.clear(CacheCategory.SPECTRADATA_TO_SPECTRUMIDS);
        cache.storeInBatch(CacheCategory.SPECTRADATA_TO_SPECTRUMIDS, spectraDataMap);

        cache.clear(CacheCategory.PEPTIDE_TO_SPECTRUM);
        cache.storeInBatch(CacheCategory.PEPTIDE_TO_SPECTRUM, identSpectrumMap);
    }

}



