package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.QuantCvTermReference;

import java.util.*;

/**
 * Quantification object is a pseudo object which contains all the cv params related to quantification data
 * <p/>
 * It also provides a set of methods for accessing these information
 * <p/>
 * <p/>
 * User: rwang
 * Date: 28/07/2011
 * Time: 09:21
 */
public class Quantification {

    /**
     * The type of the identification;
     */
    public enum Type {
        PROTEIN, PEPTIDE
    }
    /**
     * single sample quantification method results
     * NOTE: single sample are often label free method, such as: emPAI, TIC
     */
    private Map<QuantCvTermReference, Double> singleSampleResults;

    /**
     * multi samples quantification method
     * NOTE: this is mainly labelled methods, but it can also be label free, such as: ion intensity
     */
    private QuantCvTermReference multiSampleMethod;

    /**
     * multiplex method intensity values
     */
    private Double[] multiSampleIntensities;

    /**
     * multiplex method standard deviations
     */
    private Double[] multiSampleDeviations;

    /**
     * multiplex method standard error
     */
    private Double[] multiSampleErrors;

    /**
     * quantification unit
     */
    private QuantCvTermReference unit;

    /**
     * The type of the identification
     */
    private Type type;

    public Quantification(Type type, List<CvParam> cvParamList) {
        this.type = type;
        this.multiSampleIntensities = new Double[QuantitativeSample.MAX_SUB_SAMPLE_SIZE];
        this.multiSampleDeviations = new Double[QuantitativeSample.MAX_SUB_SAMPLE_SIZE];
        this.multiSampleErrors = new Double[QuantitativeSample.MAX_SUB_SAMPLE_SIZE];
        this.singleSampleResults = new HashMap<QuantCvTermReference, Double>();

        if (cvParamList != null) {
            init(cvParamList);
        }
    }


    private void init(List<CvParam> cvParamList) {
        for (CvParam cvParam : cvParamList) {
            // check intensities
            if (QuantCvTermReference.isIntensity(cvParam)) {
                int index = QuantCvTermReference.getIntensityIndex(cvParam);
                multiSampleIntensities[index - 1] = Double.parseDouble(cvParam.getValue());
            }
            // check standard deviation
            else if (QuantCvTermReference.isStandardDeviation(cvParam)) {
                int index = QuantCvTermReference.getStandardDeviationIndex(cvParam);
                multiSampleDeviations[index - 1] = Double.parseDouble(cvParam.getValue());
            }
            // check standard error
            else if (QuantCvTermReference.isStandardError(cvParam)) {
                int index = QuantCvTermReference.getStandardErrorIndex(cvParam);
                multiSampleErrors[index - 1] = Double.parseDouble(cvParam.getValue());
            }
            // check unit
            else if (QuantCvTermReference.isUnit(cvParam)) {
                unit = QuantCvTermReference.getUnit(cvParam);
            }
            // check isotope labelling
            else if (QuantCvTermReference.isMultiSampleMethod(cvParam)) {
                multiSampleMethod = QuantCvTermReference.getMultiSampleMethod(cvParam);
            }
            // check label free
            else if (QuantCvTermReference.isSingleSampleMethod(cvParam)) {
                QuantCvTermReference method = QuantCvTermReference.getSingleSampleMethod(cvParam);
                Double value = Double.parseDouble(cvParam.getValue());
                singleSampleResults.put(method, value);
            }
        }
    }

    /**
     * Get type of the quantification, either at protein level or at peptide level
     *
     * @return Type    quantification type
     */
    public Type getType() {
        return type;
    }

    /**
     * Set quantification type
     *
     * @param type quantification type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Check whether the results of single sample quantification methods are present
     * NOTE: single sample methods are mainly label free methods, such as: emPAI, TIC
     *
     * @return boolean true means label free methods exist
     */
    public boolean hasSingleSampleMethod() {
        return (singleSampleResults.size() != 0);
    }

    /**
     * Get a list of label free methods which are present
     *
     * @return List<QuanCvTermReference>   a list of label free methods
     */
    public List<QuantCvTermReference> getSingleSampleMethods() {
        if (singleSampleResults != null) {
            return new ArrayList<QuantCvTermReference>(singleSampleResults.keySet());
        }
        return null;
    }

    /**
     * Get the results of single sample quantification methods
     *
     * @param types the types of methods, the result will be ordered according the the input types
     * @return List<CvParam>   single sample method results
     */
    public List<Double> getSingleSampleResults(Collection<QuantCvTermReference> types) {
        List<Double> results = new ArrayList<Double>();

        if (singleSampleResults != null) {
            for (QuantCvTermReference quantCvTermReference : types) {
                Double val = singleSampleResults.get(quantCvTermReference);
                results.add(val);
            }
        }

        return results;
    }

    /**
     * Check whether the results of multiple sample method are present
     *
     * @return boolean true means isotope labelling methods exist
     */
    public boolean hasMultiSampleMethod() {
        return multiSampleMethod != null && multiSampleIntensities != null;
    }

    /**
     * Get the multiple sample method
     *
     * @return QuantCvTermReference    multiple sample method
     */
    public QuantCvTermReference getMultiSampleMethod() {
        return multiSampleMethod;
    }

    /**
     * Get a list of results of multiple sample
     *
     * @return List<CvParam>   a list of cv params
     */
    public List<Double> getMultiSampleIntensities() {
        if (multiSampleIntensities != null) {
            return Arrays.asList(multiSampleIntensities);
        }
        return null;
    }

    /**
     * Get the result of multiple sample method according to a given index
     *
     * @param index index of the sub sample
     * @return CvParam the result of a sub sample
     */
    public Double getMultiSampleIntensity(int index) {
        if (multiSampleIntensities != null && index > 0 && index <= QuantitativeSample.MAX_SUB_SAMPLE_SIZE) {
            return multiSampleIntensities[index - 1];
        }

        return null;
    }

    /**
     * Get a list of standard deviations
     * @return  List<Double>    a list of deviation
     */
    public List<Double> getMultiSampleDeviations() {
        if (multiSampleDeviations != null) {
            return Arrays.asList(multiSampleDeviations);
        }
        return null;
    }

    /**
     * Get the standard deviation of a given sub sample
     * @param index index of the sub sample
     * @return  Double  standard deviation
     */
    public Double getMultiSampleDeviation(int index) {
        if (multiSampleDeviations != null && index > 0 && index <= QuantitativeSample.MAX_SUB_SAMPLE_SIZE) {
            return multiSampleDeviations[index - 1];
        }
        return null;
    }

    /**
     * Get a list of standard errors
     * @return  List<Double>    a list of standard errors
     */
    public List<Double> getMultiSampleStandardErrors() {
        if (multiSampleErrors != null) {
            return Arrays.asList(multiSampleErrors);
        }
        return null;
    }

    /**
     * Get the standard error of a given sub sample
     * @param index index of the sub sample
     * @return  Double  standard error
     */
    public Double getMultiSampleStandardErrors(int index) {
        if (multiSampleErrors != null && index > 0 && index <= QuantitativeSample.MAX_SUB_SAMPLE_SIZE) {
            return multiSampleErrors[index - 1];
        }
        return null;
    }

    /**
     * Convenient method to check whether the reported value is total intensity
     * These values can be used for calculating ratios.
     *
     * @return boolean     true means total intensities are present
     */
    public boolean hasTotalIntensities() {
        return hasMultiSampleMethod() && (getUnit() == null);
    }

    /**
     * Get the unit of the quantification
     *
     * @return QuantCvTermReference    the term describes the unit
     */
    public QuantCvTermReference getUnit() {
        return unit;
    }

    /**
     * Get the index of reference sub sample
     * @return  int index of a reference sub sample, if two intensities are 1.0, then -1 index is returned, because
     *              it cannot be decided
     */
    public int getReferenceSubSampleIndex() {
        int index = -1;
        int cnt = 0;

        for (int i = 0; i < multiSampleIntensities.length; i++) {
            Double multiSampleIntensity = multiSampleIntensities[i];
            if (multiSampleIntensity != null && multiSampleIntensity == 1.0) {
                index = i + 1;
                cnt++;
            }
        }

        return cnt == 1 ? index: -1;
    }
}
