package uk.ac.ebi.pride.data.controller.access;

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
     */
    public boolean hasQuantitativeData();

    /**
     * Get quantitative method type
     */
    public String getQuantitativeMethod();

    /**
     * Get the number of sub samples
     * @return  int the number of sub samples
     */
    public int getNumberOfSubSamples();

    /**
     * Get the number of reagents
     * @return  int the number of reagents
     */
    public int getNumberOfReagents();



}
