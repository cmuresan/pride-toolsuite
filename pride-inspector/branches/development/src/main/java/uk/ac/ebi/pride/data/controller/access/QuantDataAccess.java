package uk.ac.ebi.pride.data.controller.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Quantification;
import uk.ac.ebi.pride.data.core.QuantitativeSample;
import uk.ac.ebi.pride.data.utils.QuantCvTermReference;

import java.util.Collection;

/**
 * QuantDataAccess defines methods for accessing quantitative proteomics data
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
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          error while getting the data from source
     */
    public boolean hasQuantData() throws DataAccessException;

    /**
     * Check whether the experiment contains quantitative data at the protein level
     *
     * @return boolean true means quantitative data available
     * @throws DataAccessException data access exception
     */
    public boolean hasProteinLevelQuantData() throws DataAccessException;

    /**
     * Check whether the experiment contains total intensities at the protein level
     *
     * @return boolean  true means there are total intensities
     * @throws DataAccessException data access exception
     */
    public boolean hasProteinLevelTotalIntensities() throws DataAccessException;

    /**
     * Check whether the experiment contains quantitative data at the peptide level
     *
     * @return boolean true means quantitative data available
     * @throws DataAccessException data access exception
     */
    public boolean hasPeptideLevelQuantData() throws DataAccessException;

    /**
     * Check whether the experiment contains total intensities at the protein level
     *
     * @return boolean true means quantitative data available
     * @throws DataAccessException data access exception
     */
    public boolean hasPeptideLevelTotalIntensities() throws DataAccessException;

    /**
     * Check whether the experiment contains quantitative data from quantification methods using a single sample
     *
     * @return boolean true means single sample methods (e.g. TIC, emPAI) have been used
     * @throws DataAccessException data access exception
     */
    public boolean hasSingleSampleQuantMethods() throws DataAccessException;

    /**
     * Get all the single sample methods used
     *
     * @return Collection<QuantCvTermReference>    a collection of single sample methods
     * @throws DataAccessException data access exception
     */
    public Collection<QuantCvTermReference> getSingleSampleQuantMethods() throws DataAccessException;

    /**
     * Get the single sample methods at the protein identification level
     *
     * @return Collection<QuantCvTermReference>    a collection of single sample methods
     * @throws DataAccessException data access exception
     */
    public Collection<QuantCvTermReference> getProteinLevelSingleSampleQuantMethods() throws DataAccessException;

    /**
     * Get the single sample methods at the peptide identification level
     *
     * @return Collection<QuantCvTermReference>    a collection of single sample methods
     * @throws DataAccessException data access exception
     */
    public Collection<QuantCvTermReference> getPeptideLevelSingleSampleQuantMethods() throws DataAccessException;

    /**
     * Check whether the experiment contains quantitative data from quantification methods using multiple samples
     *
     * @return boolean true means multiple sample methods have been used
     * @throws DataAccessException data access exception
     */
    public boolean hasMultiSampleQuantMethods() throws DataAccessException;

    /**
     * Get the multiple sample methods at the protein level
     *
     * @return Collection<QuantCvTermReference>    a collection of multiple sample methods
     * @throws DataAccessException data access exception
     */
    public Collection<QuantCvTermReference> getProteinLevelMultiSampleQuantMethods() throws DataAccessException;

    /**
     * Get the multiple sample methods at the peptide level
     *
     * @return Collection<QuantCvTermReference>    a collection of multiple sample methods
     * @throws DataAccessException data access exception
     */
    public Collection<QuantCvTermReference> getPeptideLevelMultiSampleQuantMethods() throws DataAccessException;

    /**
     * Get all the multiple sample methods
     *
     * @return Collection<QuantCvTermReference>    a collection of multiple sample methods
     * @throws DataAccessException data access exception
     */
    public Collection<QuantCvTermReference> getMultiSampleQuantMethods() throws DataAccessException;

    /**
     * Get quantitative method type
     *
     * @return Collection<QuantCvTermReference> a list of quantitative methods
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          error while getting the data from source
     */
    public Collection<QuantCvTermReference> getQuantMethods() throws DataAccessException;

    /**
     * Get the number of reagents
     *
     * @return int the number of reagents
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          error while getting the data from source
     */
    public int getNumberOfReagents() throws DataAccessException;

    /**
     * Get reference reagent's sub sample index
     *
     * @return int the index of a reference sub sample reagent
     * @throws DataAccessException data access exception
     */
    public int getReferenceSubSampleIndex() throws DataAccessException;

    /**
     * Get the mapping between sub samples and reagents
     *
     * @return QuantitativeSample quantitative sample description
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          error while getting the data from source
     */
    public QuantitativeSample getQuantSample() throws DataAccessException;

    /**
     * Get the unit for protein identifications
     *
     * @return QuantCvTermReference    unit's cv term
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          error while getting the data from source
     */
    public QuantCvTermReference getProteinQuantUnit() throws DataAccessException;

    /**
     * Get the unit for peptide identification
     *
     * @return QuantCvTermReference    unit's cv term
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          error while getting the data from source
     */
    public QuantCvTermReference getPeptideQuantUnit() throws DataAccessException;

    /**
     * Get quantitative data related to a given protein
     *
     * @param identId protein identification id
     * @return Quantification   quantitative data
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          error while getting the data from source
     */
    public Quantification getProteinQuantData(Comparable identId) throws DataAccessException;

    /**
     * Get quantitative data related to a given peptide
     *
     * @param identId   protein identification id
     * @param peptideId peptide id
     * @return Quantification   quantitative data
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          error while getting the data from source
     */
    public Quantification getPeptideQuantData(Comparable identId, Comparable peptideId) throws DataAccessException;
}
