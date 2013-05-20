package uk.ac.ebi.pride.data.controller.cache.strategy;

import uk.ac.ebi.pride.data.controller.cache.CacheEntry;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzXmlControllerImpl;
import uk.ac.ebi.pride.data.io.file.MzXmlUnmarshallerAdaptor;

import java.util.ArrayList;

/**
 * MzMlAccessCacheBuilder initialize the cache for mzML reading.
 * <p/>
 * User: rwang
 * Date: 06-Sep-2010
 * Time: 17:07:55
 */
public class MzXmlCachingStrategy extends AbstractCachingStrategy {

    /**
     * For the moment, MzXmlCacheBuilder only caches spectrum ids and chromatogram ids.
     *
     * @throws Exception error while caching the ids.
     */
    @Override
    public void cache(){
        // get a direct reference to unmarshaller
        MzXmlUnmarshallerAdaptor unmarshaller = ((MzXmlControllerImpl) controller).getUnmarshaller();

        // clear and add spectrum ids
        cache.clear(CacheEntry.SPECTRUM_ID);
        cache.storeInBatch(CacheEntry.SPECTRUM_ID, new ArrayList<Comparable>(unmarshaller.getSpectrumIds()));
    }
}



