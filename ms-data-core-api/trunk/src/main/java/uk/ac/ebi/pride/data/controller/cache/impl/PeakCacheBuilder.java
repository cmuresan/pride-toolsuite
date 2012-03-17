package uk.ac.ebi.pride.data.controller.cache.impl;

import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzXmlControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PeakControllerImpl;
import uk.ac.ebi.pride.data.io.file.PeakUnmarshallerAdaptor;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 3/15/12
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
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