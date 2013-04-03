package uk.ac.ebi.pride.data.controller.cache.impl;

import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PeakControllerImpl;
import uk.ac.ebi.pride.data.io.file.PeakUnmarshallerAdaptor;

import java.util.ArrayList;

/**
 * PeakCacheBuilder provides the methods to initialize the Cache Categories
 * for pure peaks list file formats.
 *
 * User: yperez
 * Date: 3/15/12
 * Time: 10:45 PM
 */
public class PeakCacheBuilder extends AbstractAccessCacheBuilder {

    public PeakCacheBuilder(PeakControllerImpl c) {
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
        PeakUnmarshallerAdaptor unmarshaller = ((PeakControllerImpl) controller).getUnmarshaller();

        // clear and add spectrum ids
        cache.clear(CacheCategory.SPECTRUM_ID);
        cache.storeInBatch(CacheCategory.SPECTRUM_ID, new ArrayList<Comparable>(unmarshaller.getSpectrumIds()));
    }
}