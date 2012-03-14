package uk.ac.ebi.pride.data.controller.cache.impl;

//~--- non-JDK imports --------------------------------------------------------

import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.MzXmlControllerImpl;
import uk.ac.ebi.pride.data.io.file.MzXmlUnmarshallerAdaptor;

import java.util.ArrayList;

//~--- JDK imports ------------------------------------------------------------

/**
 * MzMlAccessCacheBuilder initialize the cache for mzML reading.
 * <p/>
 * User: rwang
 * Date: 06-Sep-2010
 * Time: 17:07:55
 */
public class MzXmlCacheBuilder extends AbstractAccessCacheBuilder {
    public MzXmlCacheBuilder(MzXmlControllerImpl c) {
        super(c);
    }

    /**
     * For the moment, MzXmlCacheBuilder only caches spectrum ids and chromatogram ids.
     *
     * @throws Exception error while caching the ids.
     */
    @Override
    public void populate() throws Exception {
        super.populate();

        // get a direct reference to unmarshaller
        MzXmlUnmarshallerAdaptor unmarshaller = ((MzXmlControllerImpl) controller).getUnmarshaller();

        // clear and add spectrum ids
        cache.clear(CacheCategory.SPECTRUM_ID);
        cache.storeInBatch(CacheCategory.SPECTRUM_ID, new ArrayList<Comparable>(unmarshaller.getSpectrumIds()));
    }
}



