package uk.ac.ebi.pride.data.controller.cache.impl;

import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzDataControllerImpl;
import uk.ac.ebi.pride.data.io.file.MzDataUnmarshallerAdaptor;

import java.util.ArrayList;

/**
 * Class to Create the Cache for MzData Files.
 * User: yperez
 * Date: 3/15/12
 * Time: 8:00 AM
 */
public class MzDataCacheBuilder extends AbstractAccessCacheBuilder {
    public MzDataCacheBuilder(MzDataControllerImpl c) {
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
        MzDataUnmarshallerAdaptor unmarshaller = ((MzDataControllerImpl) controller).getUnmarshaller();

        // clear and add spectrum ids
        cache.clear(CacheCategory.SPECTRUM_ID);
        cache.storeInBatch(CacheCategory.SPECTRUM_ID, new ArrayList<Comparable>(unmarshaller.getSpectrumIds()));
    }
}