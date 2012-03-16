package uk.ac.ebi.pride.data.controller.cache.impl;

//~--- non-JDK imports --------------------------------------------------------

import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.io.file.MzIdentMLUnmarshallerAdaptor;

import java.util.ArrayList;

//~--- JDK imports ------------------------------------------------------------

/**
 * ToDo: document this class
 * <p/>
 * User: yperez
 * Date: 19/09/11
 * Time: 16:44
 */
public class MzIdentMLCacheBuilder extends AbstractAccessCacheBuilder {
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
        MzIdentMLUnmarshallerAdaptor unmarshaller = ((MzIdentMLControllerImpl) controller).getUnmarshaller();

        // clear and add metadata
        cache.clear(CacheCategory.EXPERIMENT_METADATA);
        controller.getExperimentMetaData();

        cache.clear(CacheCategory.PEPTIDE_SEQUENCE);


        // clear and add peptide ids
        cache.clear(CacheCategory.IDENTIFICATION_ID);
        cache.storeInBatch( CacheCategory.IDENTIFICATION_ID, new ArrayList<Comparable>(unmarshaller.getIDsForElement(MzIdentMLElement.ProteinDetectionHypothesis)));
    }
}



