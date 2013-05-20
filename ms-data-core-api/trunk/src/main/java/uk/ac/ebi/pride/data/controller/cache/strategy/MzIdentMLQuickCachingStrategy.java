package uk.ac.ebi.pride.data.controller.cache.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.cache.CacheEntry;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.Transformer.MzIdentMLTransformer;
import uk.ac.ebi.pride.data.core.CVLookup;
import uk.ac.ebi.pride.data.core.IdentifiableParamGroup;
import uk.ac.ebi.pride.data.core.SpectraData;
import uk.ac.ebi.pride.data.io.file.MzIdentMLUnmarshallerAdaptor;
import uk.ac.ebi.pride.data.utils.Constants;
import uk.ac.ebi.pride.data.utils.MzIdentMLUtils;

import javax.naming.ConfigurationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class MzIdentMLQuickCachingStrategy extends AbstractCachingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(MzIdentMLQuickCachingStrategy.class);

    @Override
    public void cache(){

        MzIdentMLUnmarshallerAdaptor unmarshaller = ((MzIdentMLControllerImpl) controller).getUnmarshaller();

        // cache spectra data
        cacheSpectraData(unmarshaller);

        // fragmentation table
        cacheFragmentationTable(unmarshaller);

        // cvlookup map
        cacheCvlookupMap(unmarshaller);

        // protein ids from db sequence
        cacheProteinIds(unmarshaller);
    }

    protected void cacheCvlookupMap(MzIdentMLUnmarshallerAdaptor unmarshaller) {
        List<CVLookup> cvLookupList = MzIdentMLTransformer.transformCVList(unmarshaller.getCvList());
        if (cvLookupList != null && !cvLookupList.isEmpty()) {
            Map<String, CVLookup> cvLookupMap = new HashMap<String, CVLookup>();
            for (CVLookup cvLookup : cvLookupList) {
                cvLookupMap.put(cvLookup.getCvLabel(), cvLookup);
            }
            cache.clear(CacheEntry.CV_LOOKUP);
            cache.storeInBatch(CacheEntry.CV_LOOKUP, cvLookupMap);
        }
    }

    protected void cacheFragmentationTable(MzIdentMLUnmarshallerAdaptor unmarshaller) {
        uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable oldFragmentationTable = unmarshaller.getFragmentationTable();
        if (oldFragmentationTable != null) {
            Map<String, IdentifiableParamGroup> fragmentationTable = MzIdentMLTransformer.transformToFragmentationTable(oldFragmentationTable);
            cache.clear(CacheEntry.FRAGMENTATION_TABLE);
            cache.storeInBatch(CacheEntry.FRAGMENTATION_TABLE, fragmentationTable);
        }
    }

    protected void cacheSpectraData(MzIdentMLUnmarshallerAdaptor unmarshaller) {
        Map<Comparable, uk.ac.ebi.jmzidml.model.mzidml.SpectraData> oldSpectraDataMap = unmarshaller.getSpectraDataMap();
        if (oldSpectraDataMap != null && !oldSpectraDataMap.isEmpty()) {
            Map<Comparable, SpectraData> spectraDataMapResult = new HashMap<Comparable, SpectraData>();

            for (Comparable id : oldSpectraDataMap.keySet()) {
                uk.ac.ebi.pride.data.core.SpectraData spectraData = MzIdentMLTransformer.transformToSpectraData(oldSpectraDataMap.get(id));
                if (isSpectraDataSupported(spectraData)) {
                    spectraDataMapResult.put(id, spectraData);
                }
            }

            cache.clear(CacheEntry.SPECTRA_DATA);
            cache.storeInBatch(CacheEntry.SPECTRA_DATA, spectraDataMapResult);
        }
    }

    private void cacheProteinIds(MzIdentMLUnmarshallerAdaptor unmarshaller) {
        try {
            Set<String> proteinIds = unmarshaller.getIDsForElement(MzIdentMLElement.DBSequence);
            cache.clear(CacheEntry.PROTEIN_ID);
            cache.storeInBatch(CacheEntry.PROTEIN_ID, proteinIds);
        } catch (ConfigurationException e) {
            String msg = "Failed to get protein ids";
            logger.error(msg, e);
            throw new DataAccessException(msg, e);
        }
    }

    private boolean isSpectraDataSupported(SpectraData spectraData) {
        return (!(MzIdentMLUtils.getSpectraDataIdFormat(spectraData) == Constants.SpecIdFormat.NONE ||
                MzIdentMLUtils.getSpectraDataFormat(spectraData) == Constants.SpecFileFormat.NONE));
    }
}
