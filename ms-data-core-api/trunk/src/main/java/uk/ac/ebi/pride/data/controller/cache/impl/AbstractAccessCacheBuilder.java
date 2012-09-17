package uk.ac.ebi.pride.data.controller.cache.impl;

//~--- non-JDK imports --------------------------------------------------------

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.cache.Cache;
import uk.ac.ebi.pride.data.controller.cache.CacheBuilder;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.CachedDataAccessController;

/**
 * Abstract implementation of CacheBuilder
 * By default, it will clear the spectrum, chromatogram and identification cache.
 * <p/>
 * User: rwang
 * Date: 06-Sep-2010
 * Time: 16:41:14
 */
public abstract class AbstractAccessCacheBuilder implements CacheBuilder {

    /**
     * Cache
     */
    Cache cache;

    /**
     * DataAccessController
     */
    DataAccessController controller;

    /**
     * Constructor
     * Stores a internal reference to DataAccessController and its Cache
     *
     * @param controller CachedDataAccessController
     */
    protected AbstractAccessCacheBuilder(CachedDataAccessController controller) {
        this.controller = controller;
        this.cache      = controller.getCache();
    }

    @Override
    public void populate() throws Exception {

        // always clear existing spectrum, chromatogram and identification caches
        cache.clear(CacheCategory.SPECTRUM);
        cache.clear(CacheCategory.CHROMATOGRAM);
        cache.clear(CacheCategory.PROTEIN);
        cache.clear(CacheCategory.PEPTIDE);
    }
}



