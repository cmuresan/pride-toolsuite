package uk.ac.ebi.pride.data.controller.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.data.core.IdentificationMetaData;
import uk.ac.ebi.pride.data.core.MzGraphMetaData;

/**
 * @author Rui Wang
 * @version $Id$
 */
public interface MetaDataAccess {

    /**
     * Get a meta object
     *
     * @return MetaData meta data object
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException throw a exception when there is an error accessing the data source
     */
    public ExperimentMetaData getExperimentMetaData() throws DataAccessException;

    /**
     * Get the MetaData Information for Identification Object.
     *
     * @return IdentificationMetaData
     * @throws DataAccessException DataAccessException
     */
    public IdentificationMetaData getIdentificationMetaData() throws DataAccessException;

    /**
     * Get MetaData Information for Spectrum Experiment Object
     *
     * @return MzGraphMetaData
     * @throws DataAccessException DataAccessException
     */
    public MzGraphMetaData getMzGraphMetaData() throws DataAccessException;

    /**
     * This function give the user the possibility to know if the controller contains
     * MetaData. The metaData could be ExperimentMetadata, MzGraphMetaData, IdentificationMetaData
     *
     * @return boolean
     */
    public boolean hasMetaDataInformation();
}
