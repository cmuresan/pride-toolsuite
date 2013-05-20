package uk.ac.ebi.pride.data.controller.cache;

import uk.ac.ebi.pride.data.controller.DataAccessController;

/**
 * Interface for building the cache.
 * <p/>
 * User: rwang
 * Date: 03-Sep-2010
 * Time: 15:09:02
 */
public interface CachingStrategy {

    /**
     * initialize the cache, it should clear the previous cache first,
     * then createAttributedSequence the new cache.
     */
    void cache();

    void setDataAccessController(DataAccessController dataAccessController);

    void setCache(Cache cache);
}



