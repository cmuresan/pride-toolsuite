package uk.ac.ebi.pride.data.controller.cache;

/**
 * Interface for building the cache.
 * <p/>
 * User: rwang
 * Date: 03-Sep-2010
 * Time: 15:09:02
 */
public interface CacheBuilder {

    /**
     * initialize the cache, it should clear the previous cache first,
     * then createAttributedSequence the new cache.
     *
     * @throws Exception exception while populating the cache
     */
    public void populate() throws Exception;
}



