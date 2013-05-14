package uk.ac.ebi.pride.data.controller.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;

/**
 * Interface for accessing the cache.
 * <p/>
 * User: rwang
 * Date: 06-Sep-2010
 * Time: 09:46:55
 */
public interface CacheAccess {

    /**
     * Whether the cache has be populated
     *
     * @return  true means cached
     */
    public boolean isCached();

    /**
     * Clear all the content in existing cache.
     */
    public void clearCache();

    /**
     * Populate the cache with content.
     *
     * @throws DataAccessException data access exception
     */
    public void populateCache() throws DataAccessException;
}



