package uk.ac.ebi.pride.data.controller.cache.impl;

//~--- non-JDK imports --------------------------------------------------------

import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.io.file.MzIdentMLUnmarshallerAdaptor;

import java.util.*;

//~--- JDK imports ------------------------------------------------------------

/**
 * ToDo: document this class
 * <p/>
 * User: yperez
 * Date: 19/09/11
 * Time: 16:44
 */
public class MzIdentMLCacheBuilder extends AbstractAccessCacheBuilder {
    public MzIdentMLCacheBuilder(MzIdentMLControllerImpl c) {
        super(c);
    }

    /**
     * Spectrum ids and identification ids are cached.
     *
     * @throws Exception error while caching the ids.
     */
    @Override
    public void populate() throws Exception {
        super.populate();

        // get a reference to xml reader
        MzIdentMLUnmarshallerAdaptor unmarshaller = ((MzIdentMLControllerImpl) controller).getUnmarshaller();


        /* Get a preScan of the File, the PreCan of the mzidentml File gets the information
         * about all the spectrums, protein identifications, and peptide-spectrum matchs with the
         * same structure that currently follow the mzidentml library.
         * */


        Map<CacheCategory, Object> mzIdentMLMaps = unmarshaller.getPreScanIdMaps();



        // Protein To to Peptides Evidences, It retrieve the peptides per Proteins
        Map<Comparable,Map<Comparable,List<String[]>>> identProteinsMap = (Map<Comparable, Map<Comparable, List<String[]>>>) mzIdentMLMaps.get(CacheCategory.PROTEIN_TO_PEPTIDE_EVIDENCES);
        cache.storeInBatch(CacheCategory.PROTEIN_TO_PEPTIDE_EVIDENCES, identProteinsMap);

        cache.clear(CacheCategory.PROTEIN_ID);
        cache.storeInBatch( CacheCategory.PROTEIN_ID, new ArrayList<Comparable>(identProteinsMap.keySet()));

        Map<Comparable, List<Comparable>> spectraDataMap = (Map<Comparable, List<Comparable>>) mzIdentMLMaps.get(CacheCategory.SPECTRADATA_TO_SPECTRUMIDS);
        cache.clear(CacheCategory.SPECTRADATA_TO_SPECTRUMIDS);
        cache.storeInBatch(CacheCategory.SPECTRADATA_TO_SPECTRUMIDS, spectraDataMap);

        Map<Comparable, String[]> identSpectrumMap = (Map<Comparable, String[]>) mzIdentMLMaps.get(CacheCategory.PEPTIDE_TO_SPECTRUM);
        cache.clear(CacheCategory.PEPTIDE_TO_SPECTRUM);
        cache.storeInBatch(CacheCategory.PEPTIDE_TO_SPECTRUM,identSpectrumMap);

        cache.clear(CacheCategory.PROTEIN_GROUP_ID);
        cache.storeInBatch(CacheCategory.PROTEIN_GROUP_ID, new ArrayList<Comparable>(unmarshaller.getIDsForElement(MzIdentMLElement.ProteinAmbiguityGroup)));
        if(cache.hasCacheCategory(CacheCategory.PROTEIN_GROUP_ID)){
           ArrayList<Comparable> proteinHIds = new ArrayList<Comparable>(unmarshaller.getIDsForElement(MzIdentMLElement.ProteinDetectionHypothesis));
           Map<Comparable, Comparable> proteinHypothesisMap = new HashMap<Comparable, Comparable>(proteinHIds.size());
            for(Comparable id: proteinHIds){
               proteinHypothesisMap.put(unmarshaller.getDBSequencebyProteinHypothesis(id),id);
            }
            cache.storeInBatch(CacheCategory.PROTEIN_TO_PROTEIN_GROUP_ID, proteinHypothesisMap);
        }

    }
}



