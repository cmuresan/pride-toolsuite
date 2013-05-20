package uk.ac.ebi.pride.data.controller.cache;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.controller.cache.decider.MzIdentMLCachingStrategyDecider;
import uk.ac.ebi.pride.data.controller.cache.strategy.MzIdentMLProteinGroupCachingStrategy;
import uk.ac.ebi.pride.data.controller.impl.MzIdentMlControllerImplTest;

import java.io.File;
import java.net.URL;

import static junit.framework.Assert.assertTrue;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class MzIdentMLCachingStrategyDeciderTests {
    private File mzIdentMLFile;


    @Before
    public void setUp() throws Exception {
        URL url = MzIdentMlControllerImplTest.class.getClassLoader().getResource("small.mzid");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        mzIdentMLFile = new File(url.toURI());
    }

    @Test
    public void testCachingStrategy() throws Exception {
        MzIdentMLCachingStrategyDecider decider = new MzIdentMLCachingStrategyDecider(80*1024*1024);

        CachingStrategy cachingStrategy = decider.decide(mzIdentMLFile);

        assertTrue(cachingStrategy instanceof MzIdentMLProteinGroupCachingStrategy);
    }
}
