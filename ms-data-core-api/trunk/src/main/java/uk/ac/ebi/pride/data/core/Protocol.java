package uk.ac.ebi.pride.data.core;

/**
 * The collection of protocols which include the parameters and settings of the performed analyses.
 * <p>
 * This class Represent the information for MzIdentMl Experiments at the Protein and Spectrum Level
 * </p>
 * <p/>
 * User: yperez
 * Date: 08/08/11
 * Time: 11:32
 */
public class Protocol extends IdentifiableParamGroup {

    /**
     * The protein or Spectrum detection software used,
     * given as a reference to the SoftwareCollection section.
     */
    private Software analysisSoftware;

    /**
     * The threshold(s) applied to determine that a result is significant.
     * If multiple terms are used it is assumed that all conditions are satisfied
     * by the passing results.
     */
    private ParamGroup threshold;

    /**
     * @param id      Generic Id
     * @param name    Generic Name
     * @param analysisParam Analysis ParamGroup
     */
    public Protocol(Comparable id, String name, ParamGroup analysisParam) {
        super(analysisParam, id, name);
    }

    /**
     * Detection Protocol for MzIdentMl Experiments at the Protein and Spectrum Level.
     *
     * @param id      Generic Id
     * @param name    Generic Name
     * @param analysisSoftware Analysis Software
     * @param analysisParam  Analysis ParamGroup
     * @param threshold      Threshold
     */
    public Protocol(ParamGroup analysisParam, Comparable id, String name, Software analysisSoftware,
                    ParamGroup threshold) {
        super(analysisParam, id, name);
        this.analysisSoftware = analysisSoftware;
        this.threshold        = threshold;
    }

    public Software getAnalysisSoftware() {
        return analysisSoftware;
    }

    public void setAnalysisSoftware(Software analysisSoftware) {
        this.analysisSoftware = analysisSoftware;
    }

    public ParamGroup getAnalysisParam() {
        if(this.getCvParams()!=null || this.getUserParams() != null){
            return new ParamGroup(this.getCvParams(), this.getUserParams());
        }else{
            return null;
        }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Protocol protocol = (Protocol) o;

        return !(analysisSoftware != null ? !analysisSoftware.equals(protocol.analysisSoftware) : protocol.analysisSoftware != null) && !(threshold != null ? !threshold.equals(protocol.threshold) : protocol.threshold != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (analysisSoftware != null ? analysisSoftware.hashCode() : 0);
        result = 31 * result + (threshold != null ? threshold.hashCode() : 0);
        return result;
    }
}



