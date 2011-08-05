package uk.ac.ebi.pride.data.controller.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Quantitation;
import uk.ac.ebi.pride.data.utils.QuantCvTermReference;

import java.util.Collection;
import java.util.Map;

/**
 * QuantDataAccess defines interface for accessing quantitative proteomics data
 * <p/>
 * User: rwang
 * Date: 06/07/2011
 * Time: 11:58
 */
public interface QuantDataAccess {

    /**
     * Check whether the experiment contains quantitative data
     *
     * @return boolean true means quantitative data exists
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException  error while getting the data from source
     */
    public boolean hasQuantData() throws DataAccessException;

    /**
     *
     * @return
     * @throws DataAccessException
     */
    public boolean hasIdentQuantData() throws DataAccessException;

    /**
     *
     * @return
     * @throws DataAccessException
     */
    public boolean hasPeptideQuantData() throws DataAccessException;

    /**
     * Get quantitative method type
     * @return Collection<QuantCvTermReference> a list of quantitative methods
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException  error while getting the data from source
     */
    public Collection<QuantCvTermReference> getQuantMethods() throws DataAccessException ;

    /**
     * Get the number of reagents
     * @return  int the number of reagents
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException  error while getting the data from source
     */
    public int getNumberOfReagents() throws DataAccessException ;

    /**
     * Get the mapping between sub samples and reagents
     * @return  Map<QuantCvTermReference, QuantCvTermReference> sample and reagent mappings
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException  error while getting the data from source
     */
    public Map<QuantCvTermReference, QuantCvTermReference> getSubSampleToReagentMapping() throws DataAccessException ;

    /**
     * Get the unit for protein identifications
     * @return  QuantCvTermReference    unit's cv term
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException  error while getting the data from source
     */
    public QuantCvTermReference getIdentQuantUnit() throws DataAccessException ;

    /**
     * Get the unit for peptide identification
     * @return  QuantCvTermReference    unit's cv term
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException  error while getting the data from source
     */
    public QuantCvTermReference getPeptideQuantUnit() throws DataAccessException ;

    /**
     * Get quantitative data related to a given protein
     * @param identId   protein identification id
     * @return  Quantification   quantitative data
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException  error while getting the data from source
     */
    public Quantitation getIdentQuantData(Comparable identId) throws DataAccessException ;

    /**
     * Get quantitative data related to a given peptide
     *
     * @param identId   protein identification id
     * @param peptideId peptide id
     * @return  Quantification   quantitative data
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException  error while getting the data from source
     */
    public Quantitation getPeptideQuantData(Comparable identId, Comparable peptideId) throws DataAccessException ;
}
