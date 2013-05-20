package uk.ac.ebi.pride.data.controller.cache.strategy;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.cache.Cache;
import uk.ac.ebi.pride.data.controller.cache.CachingStrategy;

/**
 * @author Rui Wang
 * @version $Id$
 */
public abstract class AbstractCachingStrategy implements CachingStrategy {

    protected Cache cache;

    protected DataAccessController controller;

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void setDataAccessController(DataAccessController controller) {
        this.controller = controller;
    }

}
