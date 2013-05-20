package uk.ac.ebi.pride.data.controller.cache;

import java.io.File;

/**
 * CachingStrategyDecider provide an interface to decide CachingStrategy
 *
 * @author Rui Wang
 * @version $Id$
 */
public interface CachingStrategyDecider {

    CachingStrategy decide(File file);
}
