package uk.ac.ebi.pride.data.controller.cache.impl;

//~--- non-JDK imports --------------------------------------------------------

import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;

import java.util.ArrayList;

//~--- JDK imports ------------------------------------------------------------

/**
 * PrideXmlAccessCacheBuilder initialize the cache for pride xml reading.
 * <p/>
 * User: rwang
 * Date: 06-Sep-2010
 * Time: 17:08:20
 */
public class PrideXmlCacheBuilder extends AbstractAccessCacheBuilder {
    public PrideXmlCacheBuilder(PrideXmlControllerImpl c) {
        super(c);
    }

    /**
     * Spectrum ids and identification ids are cached.
     *
     * @throws Exception error while caching the ids.
     */
    @Override
    public void populate() throws Exception {
        super.populate();

        // get a reference to xml reader
        PrideXmlReader reader = ((PrideXmlControllerImpl) controller).getReader();

        // clear and add spectrum ids
        cache.clear(CacheCategory.SPECTRUM_ID);
        cache.storeInBatch(CacheCategory.SPECTRUM_ID, new ArrayList<Comparable>(reader.getSpectrumIds()));

        // clear and add peptide ids
        cache.clear(CacheCategory.PROTEIN_ID);
        cache.storeInBatch(CacheCategory.PROTEIN_ID, new ArrayList<Comparable>(reader.getIdentIds()));
    }
}



