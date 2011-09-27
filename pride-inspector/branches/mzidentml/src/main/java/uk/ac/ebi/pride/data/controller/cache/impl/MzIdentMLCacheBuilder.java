package uk.ac.ebi.pride.data.controller.cache.impl;

import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.io.file.MzIdentMLUnmarshallerAdaptor;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 19/09/11
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public class MzIdentMLCacheBuilder extends AbstractAccessCacheBuilder{

    public MzIdentMLCacheBuilder(MzIdentMLControllerImpl c) {
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
        MzIdentMLUnmarshallerAdaptor unmarshaller = ((MzIdentMLControllerImpl)controller).getUnmarshaller();
        // clear and add metadata
        cache.clear(CacheCategory.EXPERIMENT_METADATA);
        controller.getExperimentMetaData();
        // clear and add spectrum ids
        //cache.clear(CacheCategory.SPECTRUM_ID);
        //cache.storeInBatch(CacheCategory.SPECTRUM_ID, new ArrayList<Comparable>(unmarshaller.getSpectrumIds()));
        // clear and add peptide ids
        cache.clear(CacheCategory.IDENTIFICATION_ID);
        cache.storeInBatch(CacheCategory.IDENTIFICATION_ID, new ArrayList<Comparable>(unmarshaller.getIDsForElement(MzIdentMLElement.ProteinDetectionHypothesis)));
    }

}
