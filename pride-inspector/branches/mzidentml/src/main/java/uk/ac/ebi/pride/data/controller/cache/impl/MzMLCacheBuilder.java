package uk.ac.ebi.pride.data.controller.cache.impl;

import uk.ac.ebi.pride.data.controller.cache.impl.AbstractAccessCacheBuilder;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.MzMLControllerImpl;
import uk.ac.ebi.pride.data.io.file.MzMLUnmarshallerAdaptor;

import java.util.ArrayList;

/**
 * MzMlAccessCacheBuilder initialize the cache for mzML reading.
 * <p/>
 * User: rwang
 * Date: 06-Sep-2010
 * Time: 17:07:55
 */
public class MzMLCacheBuilder extends AbstractAccessCacheBuilder {

    public MzMLCacheBuilder(MzMLControllerImpl c) {
        super(c);
    }

    /**
     * For the moment, MzMLCacheBuilder only caches spectrum ids and chromatogram ids.
     *
     * @throws Exception    error while caching the ids.
     */
    @Override
    public void populate() throws Exception {
        super.populate();
        // get a direct reference to unmarshaller
        MzMLUnmarshallerAdaptor unmarshaller = ((MzMLControllerImpl)controller).getUnmarshaller();
        // clear and add metadata
        cache.clear(CacheCategory.EXPERIMENT_METADATA);
        controller.getMetaData();
        // clear and add spectrum ids
        cache.clear(CacheCategory.SPECTRUM_ID);
        cache.storeInBatch(CacheCategory.SPECTRUM_ID, new ArrayList<Comparable>(unmarshaller.getSpectrumIds()));
        // clear and add chromatogram ids
        cache.clear(CacheCategory.CHROMATOGRAM_ID);
        cache.storeInBatch(CacheCategory.CHROMATOGRAM_ID, new ArrayList<Comparable>(unmarshaller.getChromatogramIds()));
    }
}
