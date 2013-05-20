package uk.ac.ebi.pride.data.controller.cache.strategy;

import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.cache.CacheEntry;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.io.file.MzIdentMLUnmarshallerAdaptor;

import javax.naming.ConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class MzIdentMLProteinGroupCachingStrategy extends MzIdentMLQuickCachingStrategy {

    @Override
    public void cache(){

        MzIdentMLUnmarshallerAdaptor unmarshaller = ((MzIdentMLControllerImpl) controller).getUnmarshaller();

        // cache spectra data
        cacheSpectraData(unmarshaller);

        // fragmentation table
        cacheFragmentationTable(unmarshaller);

        // cvlookup map
        cacheCvlookupMap(unmarshaller);

        // protein groups
        try {
            cacheProteinGroups(unmarshaller);
        } catch (ConfigurationException e) {
            throw new DataAccessException("Failed to cache protein groups fro mzIdentML", e);
        }
    }

    private void cacheProteinGroups(MzIdentMLUnmarshallerAdaptor unmarshaller) throws ConfigurationException {
        Set<String> proteinAmbiguityGroupIds = unmarshaller.getIDsForElement(MzIdentMLElement.ProteinAmbiguityGroup);

        if (proteinAmbiguityGroupIds != null && !proteinAmbiguityGroupIds.isEmpty()) {

            cache.clear(CacheEntry.PROTEIN_GROUP_ID);
            cache.storeInBatch(CacheEntry.PROTEIN_GROUP_ID, new ArrayList<Comparable>(proteinAmbiguityGroupIds));

            List<Comparable> proteinHIds = new ArrayList<Comparable>(unmarshaller.getIDsForElement(MzIdentMLElement.ProteinDetectionHypothesis));

            if (!proteinHIds.isEmpty()) {
                cache.clear(CacheEntry.PROTEIN_ID);
                cache.storeInBatch(CacheEntry.PROTEIN_ID, proteinHIds);
            }
        }
    }
}
