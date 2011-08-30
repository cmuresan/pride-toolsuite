package uk.ac.ebi.pride.data.coreIdent;

/**
 * The collection of protocols which include the parameters and settings of the performed analyses.
 * <p>
 *     This class Represent the information for MzIdentMl Experiments at the Protein and Spectrum Level
 * </p>
 *
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 08/08/11
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */
public class Protocol extends Identifiable {
    /**
     * The protein or Spectrum detection software used,
     * given as a reference to the SoftwareCollection section.
     */
    private Software analysisSoftware = null;
    /**
     * The parameters and settings for the protein detection given as CV terms.
     */
    private ParamGroup analysisParam = null;
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
    public Protocol(Comparable id,
                    String name,
                    Software analysisSoftware,
                    ParamGroup analysisParam,
                    ParamGroup threshold) {
        super(id, name);
        this.analysisSoftware = analysisSoftware;
        this.analysisParam = analysisParam;
        this.threshold = threshold;
    }

    /**
     *
     * @param id
     * @param name
     * @param analysisParam
     */
    public Protocol(Comparable id, String name, ParamGroup analysisParam) {
        super(id, name);
        analysisParam = analysisParam;
    }

    public Software getAnalysisSoftware() {
        return analysisSoftware;
    }

    public void setAnalysisSoftware(Software analysisSoftware) {
        this.analysisSoftware = analysisSoftware;
    }

    public ParamGroup getAnalysisParam() {
        return analysisParam;
    }

    public void setAnalysisParam(ParamGroup analysisParam) {
        this.analysisParam = analysisParam;
    }

    public ParamGroup getThreshold() {
        return threshold;
    }

    public void setThreshold(ParamGroup threshold) {
        this.threshold = threshold;
    }
}
