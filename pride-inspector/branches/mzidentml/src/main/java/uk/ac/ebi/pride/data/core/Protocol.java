package uk.ac.ebi.pride.data.core;

/**
 * The collection of protocols which include the parameters and settings of the performed analyses.
 * <p>
 *     This class Represent the information for MzIdentMl Experiments at the Protein and Spectrum Level
 * </p>
 *
 * User: yperez
 * Date: 08/08/11
 * Time: 11:32
 */
public class Protocol extends IdentifiableParamGroup {
    /**
     * The protein or Spectrum detection software used,
     * given as a reference to the SoftwareCollection section.
     */
    private Software analysisSoftware = null;
    /**
     * The threshold(s) applied to determine that a result is significant.
     * If multiple terms are used it is assumed that all conditions are satisfied
     * by the passing results.
     */
    private ParamGroup threshold = null;

    /**
     * Detection Protocol for MzIdentMl Experiments at the Protein and Spectrum Level.
     * @param id
     * @param name
     * @param analysisSoftware
     * @param analysisParam
     * @param threshold
     */
    public Protocol(ParamGroup analysisParam, Comparable id,
                    String name,
                    Software analysisSoftware,
                    ParamGroup threshold) {
        super(analysisParam, id, name);
        this.analysisSoftware = analysisSoftware;
        this.threshold = threshold;
    }

    /**
     *
     * @param id
     * @param name
     * @param analysisParam
     */
    public Protocol(Comparable id, String name, ParamGroup analysisParam) {
        super(analysisParam, id, name);

    }

    public Software getAnalysisSoftware() {
        return analysisSoftware;
    }

    public void setAnalysisSoftware(Software analysisSoftware) {
        this.analysisSoftware = analysisSoftware;
    }

    public ParamGroup getAnalysisParam() {
        return new ParamGroup(this.getCvParams(),this.getUserParams());
    }

    public void setAnalysisParam(ParamGroup analysisParam) {
        this.setCvParams(analysisParam.getCvParams());
        this.setUserParams(analysisParam.getUserParams());
    }

    public ParamGroup getThreshold() {
        return threshold;
    }

    public void setThreshold(ParamGroup threshold) {
        this.threshold = threshold;
    }
}
