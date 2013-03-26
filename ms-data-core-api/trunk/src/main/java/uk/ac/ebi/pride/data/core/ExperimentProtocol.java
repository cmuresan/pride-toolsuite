package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import uk.ac.ebi.pride.data.utils.CollectionUtils;

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
     * Pride Protocol Constructor
     *
     * @param id  ID Experiment Protocol
     * @param name Name of the Experiment Protocol
     */
    public ExperimentProtocol(Comparable id, String name) {
        super(id, name);
    }

    /**
     * Constructor of the Experiment Protocol
     *
     * @param params ParamGroup
     * @param id     ID
     * @param name   Name
     * @param protocolSteps A list of ParamGroup that define the current Experiment Protocol
     */
    public ExperimentProtocol(ParamGroup params, String id, String name, List<ParamGroup> protocolSteps) {
        super(params, id, name);
        this.protocolSteps = CollectionUtils.createListFromList(protocolSteps);
    }

    /**
     * Get Experiment Protocol Steps. A list of ParamGroup
     *
     * @return List of ParamGroup
     */
    public List<ParamGroup> getProtocolSteps() {
        return protocolSteps;
    }

    /**
     * Set the CvTerms for Each Step of the Pride Experiment
     *
     * @param protocolSteps A list of ParamGroup that define the different steps in the Protocol
     */
    public void setProtocolSteps(List<ParamGroup> protocolSteps) {
        CollectionUtils.replaceValuesInCollection(protocolSteps, this.protocolSteps);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExperimentProtocol)) return false;
        if (!super.equals(o)) return false;

        ExperimentProtocol that = (ExperimentProtocol) o;

        if (!protocolSteps.equals(that.protocolSteps)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + protocolSteps.hashCode();
        return result;
    }
}



