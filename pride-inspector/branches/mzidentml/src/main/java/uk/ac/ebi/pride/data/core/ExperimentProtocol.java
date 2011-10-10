package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Protocol used to generate the dataset, added by PRIDE XML 2.0.
 * <p/>
 * mzIdentML protocols are arranged in a slightly different way
 * In the case of MzIdentMl the Protocol is specified at the level of ProteinDetection and SpectrumDetection.
 * but we can create an objetc of this type integrating the information from SpectrumidentificationObject and ProteinDetection Object
 * <p/>
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:43:20
 */
public class ExperimentProtocol extends IdentifiableParamGroup {
   /**
     * Global Protocol steps for a PRIDE Experiment
     */
    private List<ParamGroup> protocolSteps;

    /**
     *
     * @param params
     * @param id
     * @param name
     * @param protocolSteps
     */
    public ExperimentProtocol(ParamGroup params,
                              String id,
                              String name,
                              List<ParamGroup> protocolSteps) {
        super(params, id, name);
        this.protocolSteps = protocolSteps;
    }

    /**
     *
      * @param id
     * @param name
     * @param protocolSteps
     */
    public ExperimentProtocol(Comparable id,
                              String name,
                              List<ParamGroup> protocolSteps) {
        super(id, name);
        this.protocolSteps = protocolSteps;
    }

    /**
     * Pride Protocol Constructor
     * @param id
     * @param name
     */
    public ExperimentProtocol(Comparable id, String name) {
        super(id, name);
    }

    /**
     *
     * @return
     */
    public List<ParamGroup> getProtocolSteps() {
        return protocolSteps;
    }

    /**
     * Set the CvTerms for Each Step of the Pride Experiment
     * @param protocolSteps
     */
    public void setProtocolSteps(List<ParamGroup> protocolSteps) {
        this.protocolSteps = protocolSteps;
    }
}